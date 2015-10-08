package com.graffitab.server.config.spring;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by david
 */
public class CustomMappingJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {

    @Autowired
    public MappingJackson2HttpMessageConverter delegate;

    private List<JavaType> knownJavaTypes;

    private SimpleModule customDeserializersJacksonModule;

    private ObjectMapper currentObjectMapper;

    public CustomMappingJackson2HttpMessageConverter() {
        super(new ObjectMapper(), new MediaType("application", "json", DEFAULT_CHARSET),
                new MediaType("application", "*+json", DEFAULT_CHARSET));
        this.knownJavaTypes = new ArrayList<>();
    }

    private String propertyToExtract;

    public void setPropertyToExtract(String propertyToExtract) {
        this.propertyToExtract = propertyToExtract;
    }

    private void addCustomDeserializerToObjectMapper(JavaType javaType) {

        if (customDeserializersJacksonModule == null) {
            customDeserializersJacksonModule = new SimpleModule();
        }

        GenericDeserializer deserializer = new GenericDeserializer(javaType.getRawClass(), propertyToExtract, delegate.getObjectMapper());
        customDeserializersJacksonModule.addDeserializer(javaType.getRawClass(), deserializer);
        currentObjectMapper = new ObjectMapper();
        currentObjectMapper.registerModule(customDeserializersJacksonModule);
        super.objectMapper = currentObjectMapper;

        knownJavaTypes.add(javaType);
    }

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {

        if (propertyToExtract != null) {
            String messageString = IOUtils.toString(inputMessage.getBody());
            JavaType javaType = getJavaType(type, contextClass);

            if (!knownJavaTypes.contains(javaType)) {
                addCustomDeserializerToObjectMapper(javaType);
            }

            return readJavaTypeFromStringInputMessage(javaType, messageString);
        } else {

            return delegate.read(type,contextClass,inputMessage);
        }
    }

    private Object readJavaTypeFromStringInputMessage(JavaType javaType, String inputMessageAsString) {
        try {
            Object o = objectMapper.readValue(inputMessageAsString, javaType);
            return o;
        } catch (IOException ex) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex);
        }
    }
}
