package com.helloingob.gifter.utilities;

public class StringConverter {
    
    /** Convert an UTF8mb4 String to to UTF8 */
    public static String removeBadChars(String s) {
        if (s == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (Character.isHighSurrogate(s.charAt(i)))
                continue;
            stringBuilder.append(s.charAt(i));
        }
        return stringBuilder.toString();
    }

}
