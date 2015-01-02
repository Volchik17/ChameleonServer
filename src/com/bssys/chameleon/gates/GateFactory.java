package com.bssys.chameleon.gates;

/**
 * Created by volchik on 14.12.14.
 */
public class GateFactory {

    public static Gate createGate(String gateTypeName) {
        return null;
    }

    public static GateAddress createGateAddress(String gateTypeName) {
        if (gateTypeName.equalsIgnoreCase(DBO3GateAddress.DBO3_GATE_TYPE))
            return new DBO3GateAddress();
        else
            throw new RuntimeException("Unknown gate type: "+gateTypeName);
    }
}
