package com.cn.websocket.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class StringToLongConverter extends JsonDeserializer<Long> {

	@Override
	public Long deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
			throws IOException, JsonProcessingException {
		String value = jsonParser.getText();
		try {
			return value == null ? null : Long.parseLong(value);
		} catch (NumberFormatException e) {
			log.error("", e);
			return null;
		}
	}
	
}