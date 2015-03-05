package com.bssys.chameleon.core;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by volchik on 30.12.14.
 */

@Component
public class SHA256Hash implements HashAlgorithm {

    private static final int BUFFER_SIZE = 2048;
    private static final int EOF_MARK = -1;

    @Override
    public String calculate(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data, 0, data.length);
        byte[] mdbytes = md.digest();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mdbytes.length; i++) {
            sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    @Override
    public String calculate(InputStream stream) throws NoSuchAlgorithmException,IOException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = EOF_MARK;
        while ((bytesRead = stream.read(buffer)) != EOF_MARK) {
            md.update(buffer, 0, bytesRead);
        }
        byte[] mdbytes = md.digest();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mdbytes.length; i++) {
            sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

}
