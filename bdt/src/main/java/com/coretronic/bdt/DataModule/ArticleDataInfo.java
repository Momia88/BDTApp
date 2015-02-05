package com.coretronic.bdt.DataModule;

/**
 * Created by changyuanyu on 14/9/13.
 */
public class ArticleDataInfo {
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



    public void setResult(Result result)
    {
        this.result= result;
    }
    public Result getResult()
    {
        return result;
    }

    public class Result{
        private String news_id;
        private String time;
        private String author;
        private String news_photo;
        private String news_title;
        private String news_type;
        private String news_content;
        private String favor_article;
        private String rtspurl;

        public String getShortUrl() {
            return shortUrl;
        }

        public void setShortUrl(String shortUrl) {
            this.shortUrl = shortUrl;
        }

        private String shortUrl;

        public void setNewsId(String newsId)
        {
            news_id = newsId;
        }
        public String getNewsId()
        {
            return news_id;
        }

        public void setTime(String time)
        {
            this.time = time;
        }
        public String getTime()
        {
            return time;
        }

        public void setAuthor(String news_author)
        {
            this.author = news_author;
        }
        public String getAuthor()
        {
            return author;
        }

        public void setNewsPhoto(String news_photo)
        {
            this.news_photo = news_photo;
        }
        public String getNewsPhoto()
        {
            return news_photo;
        }

        public void setNewsTitle(String news_title)
        {
            this.news_title = news_title;
        }
        public String getNewsTitle()
        {
            return news_title;
        }

        public void setNewsType(String news_type)
        {
            this.news_type = news_type;
        }
        public String getNewsType()
        {
            return news_type;
        }

        public void setNewsContent(String news_content)
        {
            this.news_content= news_content;
        }
        public String getNewsContent()
        {
            return news_content;
        }

        public void setFavorArticle(String favor_article)
        {
            this.favor_article = favor_article;
        }
        public String getFavorArticle()
        {
            return favor_article;
        }

        public void setRtspurl(String rtspurl)
        {
            this.rtspurl = rtspurl;
        }
        public String getRtspurl()
        {
            return rtspurl;
        }
    }
}
