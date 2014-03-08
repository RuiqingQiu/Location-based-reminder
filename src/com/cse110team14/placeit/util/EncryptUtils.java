package com.cse110team14.placeit.util;

import java.io.IOException;
import java.security.MessageDigest;

public class EncryptUtils {
	//SHA256 encode
	public static String encode(String toEncode) {
		String encoded = "";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(toEncode.getBytes("UTF-8"));
			for (byte b : md.digest()) {
				// System.out.format("%02X", toEncode);
				encoded = encoded + String.format("%02X", b);
			}
			// System.out.print(encoded);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encoded;
	}
}
