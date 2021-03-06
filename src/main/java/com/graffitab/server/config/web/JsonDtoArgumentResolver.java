package com.graffitab.server.config.web;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.graffitab.server.api.errors.RestApiException;
import com.graffitab.server.api.errors.ResultCode;

/**
 * Created by david on 16/05/15.
 */
public class JsonDtoArgumentResolver implements HandlerMethodArgumentResolver {

	private static Logger log = LogManager.getLogger();

	@Autowired
	private List<HttpMessageConverter<?>> converters;

	@Autowired
	private SingleJsonPropertyMappingJackson2HttpMessageConverter jacksonConverter;

    private RequestResponseBodyMethodProcessor delegateRequestResponseBodyMethodProcessor = null;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(JsonProperty.class);
    }

    private RequestResponseBodyMethodProcessor getDelegateRequestResponseBodyMethodProcessor() {

        if (delegateRequestResponseBodyMethodProcessor == null) {
            List<HttpMessageConverter<?>> messageConverters = converters;

            for (HttpMessageConverter<?> messageConverter : messageConverters) {
                if (messageConverter instanceof RequestResponseBodyMethodProcessor) {
                    delegateRequestResponseBodyMethodProcessor = (RequestResponseBodyMethodProcessor) messageConverter;
                }
            }

            if (delegateRequestResponseBodyMethodProcessor == null) {
            	messageConverters.clear();
            	messageConverters.add(jacksonConverter);
                delegateRequestResponseBodyMethodProcessor = new RequestResponseBodyMethodProcessor(messageConverters);
            }
        }

        return delegateRequestResponseBodyMethodProcessor;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        JsonProperty annotation = parameter.getParameterAnnotation(JsonProperty.class);
        String propertyToExtract = annotation.value();

        RunWithJsonProperty.set(propertyToExtract);

        Object value;

        try {
        	 value = getDelegateRequestResponseBodyMethodProcessor().resolveArgument(parameter,mavContainer,webRequest,binderFactory);
        } catch (MissingJsonPropertyException mjpe) {
        	throw mjpe;
        } catch(Throwable t) {
        	String msg = "Cannot process JSON payload";
        	log.error(msg, t);
        	throw new RestApiException(ResultCode.INVALID_JSON, msg);
        } finally {
        	RunWithJsonProperty.reset();
        }

        return value;
    }
}

