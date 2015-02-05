package com.coretronic.bdt.WalkWay.Module;

import java.util.List;

/**
 * Created by Morris on 14/12/17.
 */
public class WalkWayVisitedInfo {
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
        int totalFriendNum;
        private List<FriendsVisited> friends;

        public List<FriendsVisited> getFriends() {
            return friends;
        }
    }



}
