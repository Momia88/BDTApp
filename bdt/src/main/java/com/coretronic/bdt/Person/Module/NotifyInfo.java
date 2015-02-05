package com.coretronic.bdt.Person.Module;

import java.util.List;

/**
 * Created by changyuanyu on 14/9/13.
 */
public class NotifyInfo {
    private String msgCode;
    private String status;
    private List<Result> result;

    public String getMsgCode() {
        return msgCode;
    }

    public String getStatus() {
        return status;
    }

    public List<Result> getResult() {
        return result;
    }

    public class Result {
        private String notificationId;
        private String systemTime;
        private String messageType;
        private String referenceId;
        private String refArticleType;
        private String state;
        private String title;
        private String userRealName;

        public String getTitle() {
            return title;
        }

        public String getNotificationId() {
            return notificationId;
        }

        public String getSystemTime() {
            return systemTime;
        }

        public String getMessageType() {
            return messageType;
        }

        public String getReferenceId() {
            return referenceId;
        }

        public String getRefArticleType() {
            return refArticleType;
        }

        public String getState() {
            return state;
        }

        public String getUserRealName() {
            return userRealName;
        }
    }
}
