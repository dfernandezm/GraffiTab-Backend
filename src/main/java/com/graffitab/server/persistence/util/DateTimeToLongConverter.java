package com.graffitab.server.persistence.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.joda.time.DateTime;

@Converter
public class DateTimeToLongConverter implements AttributeConverter<DateTime, Long> {

	@Override
	public Long convertToDatabaseColumn(DateTime attribute) {
		return attribute.getMillis();
	}

	@Override
	public DateTime convertToEntityAttribute(Long dbData) {
		return new DateTime(dbData);
	}
}