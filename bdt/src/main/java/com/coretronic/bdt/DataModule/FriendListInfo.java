package com.coretronic.bdt.DataModule;

import java.util.List;

/**
 * Created by poter.hsu on 2014/12/14.
 */
public class FriendListInfo {

    private String msgCode;
    private String status;
    private Result result;

    public String getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public class Result {
        private List<Item> fd_list;
        private List<Item> invite;
        private List<Item> invite_me;

        public List<Item> getFd_list() {
            return fd_list;
        }

        public void setFd_list(List<Item> fd_list) {
            this.fd_list = fd_list;
        }

        public List<Item> getInvite() {
            return invite;
        }

        public void setInvite(List<Item> invite) {
            this.invite = invite;
        }

        public List<Item> getInvite_me() {
            return invite_me;
        }

        public void setInvite_me(List<Item> invite_me) {
            this.invite_me = invite_me;
        }

        public class Item {
            private String fid;
            private String name;

            public String getFid() {
                return fid;
            }

            public void setFid(String fid) {
                this.fid = fid;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

    }

}
