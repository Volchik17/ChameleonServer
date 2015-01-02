package com.bssys.chameleon.core;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

/**
 * Created by volchik on 30.12.14.
 */

public interface HashAlgorithm {

    String calculate(byte[] data) throws NoSuchAlgorithmException;
    String calculate(InputStream stream) throws NoSuchAlgorithmException,IOException;

}
