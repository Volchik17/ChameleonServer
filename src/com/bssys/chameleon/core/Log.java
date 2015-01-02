package com.bssys.chameleon.core;

/**
 * Created by volchik on 26.12.14.
 */
public interface Log {

    void Error(String module, String info);

    void Error(String module, String format, Object ... args);

    void Error(String module, Throwable e, String info);

    void Error(String module, Throwable e, String format, Object ... args);

    void Warning(String module, String info);

    void Warning(String module, String format, Object... args);

    void Info(String module, String info);

    void Info(String module, String format, Object... args);

    void Debug(String module, String info);

    void Debug(String module, String format, Object... args);

}
