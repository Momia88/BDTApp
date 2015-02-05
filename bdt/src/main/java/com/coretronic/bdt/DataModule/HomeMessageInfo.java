package com.coretronic.bdt.DataModule;

import java.util.List;

/**
 * Created by changyuanyu on 14/9/13.
 */
public class HomeMessageInfo {
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


    public void setResult(List<Result> result)
    {
        this.result= result;
    }
    public List<Result> getResult()
    {
        return result;
    }

    public class Result {
        private String id;
        private String title;
        private String date;
        private String articletype;
        private String read;
        private String messageId;

        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }


        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDate() {
            return date;
        }

        public void setArticletype(String articletype) {
            this.articletype = articletype;
        }

        public String getArticletype() {
            return articletype;
        }

        public void setRead(String read) {
            this.read = read;
        }

        public String getRead() {
            return read;
        }


        public void setMessageId(String messageId) {
            this.messageId = messageId;
        }

        public String getMessageId() {
            return messageId;
        }


    }
}
