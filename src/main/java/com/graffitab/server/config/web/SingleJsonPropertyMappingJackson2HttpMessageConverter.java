package com.graffitab.server.config.web;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Created by david
 */
public class SingleJsonPropertyMappingJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {

    @Autowired
    public MappingJackson2HttpMessageConverter delegate;

    @Autowired
    public SinglePropertyJsonDeserializer singlePropertyJsonDeserializer;

    private Map<String, SinglePropertyJsonDeserializer> jsonPropertiesToDeserializer = new ConcurrentHashMap<>();


    public SingleJsonPropertyMappingJackson2HttpMessageConverter() {
        super(new ObjectMapper(), new MediaType("application", "json", DEFAULT_CHARSET),
                new MediaType("application", "*+json", DEFAULT_CHARSET));
    }

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {

    	String propertyToExtract = RunWithJsonProperty.get();

        if (propertyToExtract != null) {

            JavaType javaType = getJavaType(type, contextClass);
            configureObjectMapperForSpecificPropertyExtraction(javaType, propertyToExtract);

            return super.read(type, contextClass, inputMessage);

        } else {
            return delegate.read(type,contextClass,inputMessage);
        }
    }

    @SuppressWarnings("unchecked")
	private void configureObjectMapperForSpecificPropertyExtraction(JavaType javaType, String propertyToExtract) {

    	SinglePropertyJsonDeserializer deserializer = jsonPropertiesToDeserializer.get(propertyToExtract);

    	if (deserializer == null || !deserializer.getRawJavaClass().equals(javaType.getRawClass())) {

    		singlePropertyJsonDeserializer.setRawJavaClass(javaType.getRawClass());

       	 	ObjectMapper newObjectMapper = new ObjectMapper();
       	 	SimpleModule simpleModule = new SimpleModule();

       	 	simpleModule.addDeserializer(javaType.getRawClass(), singlePropertyJsonDeserializer);
       	 	newObjectMapper.registerModule(simpleModule);

            super.setObjectMapper(newObjectMapper);

            jsonPropertiesToDeserializer.put(propertyToExtract, singlePropertyJsonDeserializer);
    	}
    }
}


