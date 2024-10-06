package org.example.for_doc1.config;

import java.util.Base64;

public class EncodingUtil {

    // Method to encode a string
    public static String encode(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }


//    Base64.getEncoder().encodeToString(to.getBytes());

    // Method to decode a string
    public static String decode(String encoded) {
        return new String(Base64.getDecoder().decode(encoded));
    }
}

