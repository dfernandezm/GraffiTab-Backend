package com.graffitab.server.config.spring;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by david
 */
@SuppressWarnings("rawtypes")
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
        String extractedJson = node.get(propertyName).toString();
        Object o = mapper.readValue(extractedJson, clazz);
        return o;
    }
}