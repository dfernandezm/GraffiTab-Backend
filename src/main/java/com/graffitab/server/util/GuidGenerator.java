package com.graffitab.server.util;

import java.util.Random;

public class GuidGenerator {

	private static String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	
	public static String generate() {
		char[] chars = alphabet.toCharArray();
		StringBuffer guid = new StringBuffer();
		Random r = new Random();
		for (int i = 0; i<13; i++) {
			int pos = r.nextInt() % chars.length;
			guid.append(chars[pos]);
		}
		return guid.toString();
	}
}
