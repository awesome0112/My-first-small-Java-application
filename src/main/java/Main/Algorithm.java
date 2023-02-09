package Main;

import java.io.File;
import java.io.FileWriter;
import java.util.Random;

public final class Algorithm {

    public static String randomString() {
        int leftLimit = 97;
        int rightLimit = 122;
        int targetStringLength = 5;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedString = buffer.toString();
        return generatedString;
    }

    public static String getHashedString(String s) {
        Integer tmp = s.hashCode();
        return tmp.toString();
    }

    public static String normalize(String s) {
        while(!s.isEmpty() && s.charAt(0) == ' ') s = s.substring(1);
        while(!s.isEmpty() && s.charAt(s.length() - 1) == ' ') s = s.substring(0, s.length() - 1);
        return s;
    }
}