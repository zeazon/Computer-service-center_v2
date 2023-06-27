package com.twobytes.util;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;

@Component("StringUtilily")
public class StringUtility {

	public String convertUTF8ToISO_8859_1(String input) {
		if(input != null) {
			return new String(input.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
		}else {
			return null;
		}
			
	}
}
