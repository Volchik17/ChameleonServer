package com.bssys.chameleon.core;

/**
 * Created by volchik on 26.12.14.
 */

public class ConsoleLog implements Log {

    @Override
    public void Error(String module, String info) {
        System.out.println(info);
    }

    @Override
    public void Error(String module, String format, Object... args) {
        System.out.println(String.format(format,args));
    }

    @Override
    public void Error(String module, Throwable e, String info) {
        System.out.println(info);
        System.out.println(e);
        e.printStackTrace();
    }

    @Override
    public void Error(String module, Throwable e, String format, Object... args) {
        System.out.println(String.format(format,e));
        System.out.println(e);
        e.printStackTrace();
    }

    @Override
    public void Warning(String module, String info) {
        System.out.println(info);
    }

    @Override
    public void Warning(String module, String format, Object... args) {
        System.out.println(String.format(format,args));
    }

    @Override
    public void Info(String module, String info) {
        System.out.println(info);
    }

    @Override
    public void Info(String module, String format, Object... args) {
        System.out.println(String.format(format,args));
    }

    @Override
    public void Debug(String module, String info) {
        System.out.println(info);
    }

    @Override
    public void Debug(String module, String format, Object... args) {
        System.out.println(String.format(format,args));
    }

}
