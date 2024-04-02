package com.nas.server.util.process;

import java.util.HashMap;
import java.util.Map;

public class ProcessResult {

    private int exitValue;
    private StringBuilder outPut;
    private StringBuilder errOutPut;

    public int getExitValue() {
        return exitValue;
    }

    public void setExitValue(int exitValue) {
        this.exitValue = exitValue;
    }

    public StringBuilder getOutPut() {
        return outPut;
    }

    public void setOutPut(StringBuilder outPut) {
        this.outPut = outPut;
    }

    public StringBuilder getErrOutPut() {
        return errOutPut;
    }

    public void setErrOutPut(StringBuilder errOutPut) {
        this.errOutPut = errOutPut;
    }

    public Map<?, ?> toMap() {
        return new HashMap<String, Object>(){{
            put("exitValue", exitValue);
            put("outPut", outPut.toString());
            put("errOutPut", errOutPut.toString());
        }};
    }

}
