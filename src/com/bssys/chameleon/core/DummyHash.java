package com.bssys.chameleon.core;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

/**
 * Created by volchik on 31.12.14.
 */
public class DummyHash implements HashAlgorithm {

    @Override
    public String calculate(byte[] data) throws NoSuchAlgorithmException {
        return data.toString();
    }

    @Override
    public String calculate(InputStream stream) throws NoSuchAlgorithmException, IOException {
        return stream.toString();
    }
}
