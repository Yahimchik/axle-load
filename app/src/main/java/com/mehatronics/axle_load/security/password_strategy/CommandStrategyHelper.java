package com.mehatronics.axle_load.security.password_strategy;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CommandStrategyHelper {
    public static String hashMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(input.getBytes(StandardCharsets.UTF_8));
            final byte[] digest = md.digest();
            return String.format("%032x", new BigInteger(1, digest));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    public static void fillBufferWithHexString(byte[] buffer, String hexString) {
        for (int i = 0; i < 32; i += 2) {
            buffer[(4 + (i / 2))] = (byte) Integer.parseInt(hexString.substring(i, i + 2), 16);
        }
    }
}
