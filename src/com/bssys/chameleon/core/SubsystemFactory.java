package com.bssys.chameleon.core;

/**
 * Created by volchik on 28.12.14.
 */

public interface SubsystemFactory {

    public Subsystem getSubsystem(String moduleType, String moduleName);

}
