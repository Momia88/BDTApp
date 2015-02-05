package com.coretronic.bdt.DataModule;

/**
 * Created by poter.hsu on 2014/12/14.
 */
public class FriendThumbInfo {
    private String msgCode;
    private String status;
    private Result result;

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    public String getStatus() {
        return status;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public class Result {
        private String uid;
        private String thumb;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }
    }
}
