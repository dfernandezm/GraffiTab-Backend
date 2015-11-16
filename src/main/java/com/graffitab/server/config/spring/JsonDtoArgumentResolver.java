package com.graffitab.server.config.spring;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by david on 16/05/15.
 */
public class JsonDtoArgumentResolver implements HandlerMethodArgumentResolver {

//   @Autowired
//    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

	@Autowired
	private List<HttpMessageConverter<?>> converters;
	
	@Autowired
	private CustomMappingJackson2HttpMessageConverter jacksonConverter;
	
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

    private void provideCustomPropertyToExtractToJacksonCustomHttpMessageConverter(String propertyToExtract) {

        List<HttpMessageConverter<?>> messageConverters =  converters;

        for (HttpMessageConverter<?> messageConverter : messageConverters) {

            if (messageConverter instanceof CustomMappingJackson2HttpMessageConverter) {
                ((CustomMappingJackson2HttpMessageConverter) messageConverter).setPropertyToExtract(propertyToExtract);
                return;
            }
        }
        throw new IllegalStateException("Cannot find Jackson HttpMessageConverter, it is required for custom field extraction");
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        JsonProperty annotation = parameter.getParameterAnnotation(JsonProperty.class);
        String propertyToExtract = annotation.value();

        provideCustomPropertyToExtractToJacksonCustomHttpMessageConverter(propertyToExtract);
        Object value = getDelegateRequestResponseBodyMethodProcessor().resolveArgument(parameter,mavContainer,webRequest,binderFactory);

        return value;
    }
}
