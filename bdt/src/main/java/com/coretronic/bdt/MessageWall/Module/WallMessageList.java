package com.coretronic.bdt.MessageWall.Module;

import java.util.List;

/**
 * Created by Morris on 2014/10/17.
 */
public class WallMessageList {
    private String msgCode;
    private String status;
    private List<WallMessageInfo> result;

    public String getMsgCode() {
        return msgCode;
    }

    public String getStatus() {
        return status;
    }

    public List<WallMessageInfo> getResult() {
        return result;
    }

}
