package com.coretronic.bdt.DataModule;

import java.util.List;
/**
 * Created by darren on 2014/12/1.
 */
public class DailyStepRecord {
    private String msgCode;
    private String status;
    private List<Result> result;


    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    public String getMsgCode() {
        return msgCode;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public List<Result> getResult() {
        return result;
    }

    /*
    *step record
    */
    public class Result {
        private String count;
        private String start;
        private String walkway_name;

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getWalkwayName() {
            return walkway_name;
        }

        public void setWalkwayName(String walkway_name) {
            this.walkway_name = walkway_name;
        }
    }

}