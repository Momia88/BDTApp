package com.coretronic.bdt.HealthQA.module;

import android.util.Log;

import java.util.List;

/**
 * Created by james on 14/12/5.
 */
public class QuestionInfo {

    private String msgCode;
    private String status;
    private Result result;

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

    public void setResult(Result result) {
        this.result = result;
    }
    public Result getResult() {

        return result;
    }

    public class Result {
        private String title;
        private String content;
        private String heart;
        private String today;
        private String all;

        public void setTitle(String title)
        {
            this.title = title;
        }
        public String getTitle()
        {
            return title;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public void setToday(String today) {
            this.today = today;
        }
        public String getToday() {
            return today;
        }

        public void setHeart(String heart) {
            this.heart = heart;
        }

        public String getHeart() {
            return heart;
        }

        public void setAll(String all)
        {
            this.all = all;
        }
        public String getAll()
        {
            return all;
        }
    }
}
