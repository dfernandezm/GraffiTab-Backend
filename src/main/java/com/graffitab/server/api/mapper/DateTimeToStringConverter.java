package com.graffitab.server.api.mapper;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class DateTimeToStringConverter extends BidirectionalConverter<DateTime, String> {


	private static final String RFC_822_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z";

	private static final DateTimeFormatter[] RFC822_PARSE_SPEC = new DateTimeFormatter[] {
		DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss Z"),
		DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm Z"),
		DateTimeFormat.forPattern("dd MMM yyyy HH:mm Z"),
		DateTimeFormat.forPattern("dd MMM yyyy HH:mm:ss Z"),
		DateTimeFormat.forPattern("EEE, dd MMM yy HH:mm:ss Z"),
		DateTimeFormat.forPattern("EEE, dd MMM yy HH:mm Z"),
		DateTimeFormat.forPattern("dd MMM yy HH:mm Z"),
		DateTimeFormat.forPattern("dd MMM yy HH:mm:ss Z")};

	private static String format(DateTime dateTime, String pattern) {
		if (dateTime == null) {
			return null;
		}
		DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);
		return formatter.withZone(DateTimeZone.UTC).print(dateTime);
	}

    private static DateTime parseRfc822Date(String datetimeString) {
		DateTime dateTime = null;
		for (int i = 0; i < RFC822_PARSE_SPEC.length && dateTime == null; i++) {
			try {
				dateTime = RFC822_PARSE_SPEC[i].parseDateTime(datetimeString);
			} catch (IllegalArgumentException iae) {
				// Doesn't match this spec -- try next.
			}
		}

		if (dateTime == null) {
			// Failed to parse.
			throw new IllegalArgumentException(datetimeString + " is not an RFC822 date.");
		}

		return dateTime;
	}

	@Override
	public String convertTo(DateTime source, Type<String> destinationType) {
		return format(source, RFC_822_DATE_FORMAT);
	}

	@Override
	public DateTime convertFrom(String source, Type<DateTime> destinationType) {
		return parseRfc822Date(source);
	}

}
