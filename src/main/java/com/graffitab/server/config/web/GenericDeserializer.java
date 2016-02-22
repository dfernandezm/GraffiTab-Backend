package com.graffitab.server.config.web;

import java.io.IOException;

import lombok.extern.log4j.Log4j2;

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
public class GenericDeserializer extends JsonDeserializer {

    private String propertyName;
    private Class<?> clazz;
    private ObjectMapper mapper;

    public GenericDeserializer() {
        super();
    }

    public GenericDeserializer(Class<?> clazz, String propertyName, ObjectMapper mapper) {
        super();
        this.propertyName = propertyName;
        this.clazz = clazz;
        this.mapper = mapper;
    }

    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

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
        Object o = mapper.readValue(extractedJson, clazz);
        return o;
    }
}