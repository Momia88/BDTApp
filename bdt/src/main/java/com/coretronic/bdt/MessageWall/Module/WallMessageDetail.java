package com.coretronic.bdt.MessageWall.Module;

import java.util.List;

/**
 * Created by Morris on 2014/10/17.
 */
public class WallMessageDetail {
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
        private String date;
        private String comment;
        private String isGood;
        private List<String> picUrl;
        private List<Goods> good;
        private List<FriendMessage> messages;
        private String totalMessageNum;
        private int totalGoodNum;
        private String thumb;
        private String userName;

        public String getThumb() {
            return thumb;
        }

        public String getUserName() {
            return userName;
        }

        public int getTotalGoodNum() {
            return totalGoodNum;
        }

        public String getDate() {
            return date;
        }

        public String getComment() {
            return comment;
        }

        public String getIsGood() {
            return isGood;
        }

        public List<String> getPicUrl() {
            return picUrl;
        }

        public List<Goods> getGood() {
            return good;
        }

        public List<FriendMessage> getMessages() {
            return messages;
        }

        public String getTotalMessageNum() {
            return totalMessageNum;
        }

    }
}
