package com.mycompany.chatappbackend.config;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class OffsetDateTimeConverter implements Converter<String, OffsetDateTime> {

	@Override
	public OffsetDateTime convert(final String source) {
		if (source == null || source.isEmpty()) {
			return null;
		}
		return OffsetDateTime.parse(source, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	}

}
