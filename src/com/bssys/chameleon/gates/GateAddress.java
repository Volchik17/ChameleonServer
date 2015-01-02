package com.bssys.chameleon.gates;

import com.bssys.chameleon.xml.IStructureParser;

/**
 * Created by volchik on 14.12.14.
 */

public abstract class GateAddress {

    public abstract IStructureParser getParser();

    public abstract String getGateType();
}
