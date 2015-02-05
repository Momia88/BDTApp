package com.coretronic.bdt.HealthQA.module;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by james on 14/12/4.
 */
public class MemberRecordInfo {

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
        private String rank;
        private String userId;
        private String count;
        private String userName;
        private String thumb;
        private String challengeId;
        private String totalUser;
        private String heart;
        private List<Users> users;

        public void setRank(String rank) {
            this.rank = rank;
        }

        public String getRank() {
            return rank;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public String getCount() {
            return count;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserName() {
            return userName;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }

        public String getThumb() {
            return thumb;
        }

        public void setChallengeId(String challengeId) {
            this.challengeId = challengeId;
        }

        public String getChallengeId() {
            return challengeId;
        }

        public void setTotalUser(String totalUser) {
            this.totalUser = totalUser;
        }

        public String getTotalUser() {
            return totalUser;
        }

        public void setHeart(String heart) {
            this.heart = heart;
        }

        public String getHeart() {
            return heart;
        }

        public void setUsersList(List<Users> users) {
            Log.i("info", "usersList:"+users);
            this.users = users;
        }

        public List<Users> getUsersList() {
            return users;
        }


    }

    public class Users {
        private String rank;
        private String userId;
        private String count;
        private String userName;
        private String thumb;

        public void setRank(String rank) {
            this.rank = rank;
        }

        public String getRank() {
            return rank;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserId() {
            return userId;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public String getCount() {
            return count;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserName() {
            return userName;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }

        public String getThumb() {
            return thumb;
        }
    }
}
