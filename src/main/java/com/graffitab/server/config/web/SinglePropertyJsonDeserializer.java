package com.graffitab.server.config.web;

import java.io.IOException;

import javax.annotation.PostConstruct;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by david
 */
@SuppressWarnings("rawtypes")
@Log4j2
@Component
public class SinglePropertyJsonDeserializer extends JsonDeserializer {

    private Class<?> clazz;
    private ObjectMapper mapper;

    @Autowired
    public MappingJackson2HttpMessageConverter delegate;

    @PostConstruct
    public void setup() {

    	if (delegate != null) {
    		this.mapper = delegate.getObjectMapper();
    	} else {
    		throw new IllegalStateException("Jackson delegate mapper is null");
    	}
 	}

    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

    	String propertyName = RunWithJsonProperty.get();

    	JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        JsonNode extractedJsonNode = node.get(propertyName);

        if (extractedJsonNode == null) {

        	if (log.isDebugEnabled()) {
        		log.error("Required JSON property " + propertyName + " is not present in the request payload: " + node);
        	}

        	String msg = "The required property [" + propertyName + "] is not present in the request";
        	throw new MissingJsonPropertyException(msg, propertyName);
        }

        String extractedJson = extractedJsonNode.toString();
        Object value = mapper.readValue(extractedJson, clazz);
        return value;
    }

	public Class<?> getRawJavaClass() {
		return clazz;
	}

	public void setRawJavaClass(Class<?> clazz) {
		this.clazz = clazz;
	}
}
