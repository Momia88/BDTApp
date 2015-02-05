package com.coretronic.bdt.DataModule;

/**
 * Created by james on 2014/9/15.
 */
public class ArticleFavorInfo {

    private String msgCode;
    private String status;
    private String favor_article;
    private Integer count;

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

    public void setTotalCount(Integer count)
    {
        this.count = count;
    }
    public Integer getTotalCount()
    {
        return count;
    }

    public void setFavorArticle(String favor_article)
    {
        this.favor_article = favor_article;
    }
    public String getFavorArticle()
    {
        return favor_article;
    }

}
