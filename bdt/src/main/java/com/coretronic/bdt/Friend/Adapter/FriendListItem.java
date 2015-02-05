package com.coretronic.bdt.Friend.Adapter;

/**
 * Created by poter.hsu on 2014/12/12.
 */
public class FriendListItem {

    private String friendId;
    private String friendThumb;
    private String friendName;
    private String type;

    public FriendListItem() {
        friendId = null;
        friendThumb = null;
        friendName = null;
        type = null;
    }

    public FriendListItem(String friendId, String friendThumb, String friendName, String type) {
        this.friendId = friendId;
        this.friendThumb = friendThumb;
        this.friendName = friendName;
        this.type = type;
    }

    public String getFriendId() {
        return friendId;
    }

    public String getFriendThumb() {
        return friendThumb;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public void setFriendThumb(String friendThumb) {
        this.friendThumb = friendThumb;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
