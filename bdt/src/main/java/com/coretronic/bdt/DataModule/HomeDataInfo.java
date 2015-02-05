package com.coretronic.bdt.DataModule;

/**
 * Created by changyuanyu on 14/9/13.
 */
public class HomeDataInfo {
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
        private String ArticleCounts;
        private String FriendCounts;
        private String MessageCounts;
        private String Name;
        private String Temp;
        private String Pop;

        public String getArticleCounts() {
            return ArticleCounts;
        }

        public void setArticleCounts(String NewsCounts) {
            this.ArticleCounts = NewsCounts;
        }


        public void setFriendCounts(String FriendCounts) {
            this.FriendCounts = FriendCounts;
        }

        public String getFriendCounts() {
            return FriendCounts;
        }

        public void setMessageCounts(String MessageCounts) {
            this.MessageCounts = MessageCounts;
        }

        public String getMessageCounts() {
            return MessageCounts;
        }

        public void setName(String Name) {
            this.Name = Name;
        }

        public String getName() {
            return Name;
        }

        public void setTemp(String Temp) {
            this.Temp = Temp;
        }

        public String getTemp() {
            return Temp;
        }

        public void setPop(String pop) {
            this.Pop = pop;
        }

        public String getPop() {
            return Pop;
        }


    }
}
