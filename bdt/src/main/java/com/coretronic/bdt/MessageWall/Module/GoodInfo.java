package com.coretronic.bdt.MessageWall.Module;

import java.util.List;

/**
 * Created by Morris on 14/12/17.
 */
public class GoodInfo {
    private String msgCode;
    private String status;
    private ResultObj result;

    public String getMsgCode() {
        return msgCode;
    }

    public String getStatus() {
        return status;
    }

    public ResultObj getResult() {
        return result;
    }

    public class ResultObj {
        int totalGoodNum;
        private List<Goods> good;

        public int getTotalGoodNum() {
            return totalGoodNum;
        }

        public List<Goods> getGood() {
            return good;
        }
    }



}
