package com.coretronic.bdt.DataModule;

import java.util.List;

/**
 * Created by changyuanyu on 14/9/13.
 */
public class WallDairyInfo {
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
        private String photoDate;
        private String weather;
        private String steps;
        private String isGood;
        private String totalGoodNum;
        private String walkwayName;
        private String mission1Photo;
        private String mission2Photo;
        private String mission3Photo;
        private String mission4Photo;
        private String mission5Photo;

        private String mission1Comment;
        private String mission2Comment;
        private String mission3Comment;
        private String mission4Comment;
        private String mission5Comment;
        private List<Good> good;

        public String getPhotoDate() {
            return photoDate;
        }

        public void setPhotoDate(String photoDate) {
            this.photoDate = photoDate;
        }


        public void setWeather(String weather) {
            this.weather = weather;
        }

        public String getWeather() {
            return weather;
        }

        public void setSteps(String steps) {
            this.steps = steps;
        }

        public String getSteps() {
            return steps;
        }

        public void setIsGood(String isGood) {
            this.isGood = isGood;
        }

        public String getIsGood() {
            return isGood;
        }

        public void setWalkwayName(String walkwayName) {
            this.walkwayName = walkwayName;
        }

        public String getWalkwayName() {
            return walkwayName;
        }

        public void setMission1Photo(String mission1Photo) {
            this.mission1Photo = mission1Photo;
        }
        public String getMission1Photo() {
            return mission1Photo;
        }


        public String getMission2Photo() {
            return mission2Photo;
        }
        public void setMission2Photo(String mission2Photo) {
            this.mission2Photo = mission2Photo;
        }

        public String getMission3Photo() {
            return mission3Photo;
        }
        public void setMission3Photo(String mission3Photo) {
            this.mission3Photo = mission3Photo;
        }

        public String getMission4Photo() {
            return mission4Photo;
        }
        public void setMission4Photo(String mission4Photo) {
            this.mission4Photo = mission4Photo;
        }

        public String getMission5Photo() {
            return mission5Photo;
        }
        public void setMission5Photo(String mission5Photo) {
            this.mission5Photo = mission5Photo;
        }

        public String getMission1Comment() {
            return mission1Comment;
        }
        public void setMission1Comment(String mission1Comment) {
            this.mission1Comment = mission1Comment;
        }

        public String getMission2Comment() {
            return mission2Comment;
        }
        public void setMission2Comment(String mission2Comment) {
            this.mission2Comment = mission2Comment;
        }

        public String getMission3Comment() {
            return mission3Comment;
        }
        public void setMission3Comment(String mission3Comment) {
            this.mission3Comment = mission3Comment;
        }

        public String getMission4Comment() {
            return mission4Comment;
        }
        public void setMission4Comment(String mission4Comment) {
            this.mission4Comment = mission4Comment;
        }

        public String getMission5Comment() {
            return mission5Comment;
        }
        public void setMission5Comment(String mission5Comment) {
            this.mission5Comment = mission5Comment;
        }

        public String getTotalGoodNum() {
            return totalGoodNum;
        }
        public void setTotalGoodNum(String totalGoodNum) {
            this.totalGoodNum = totalGoodNum;
        }

        public void setGood(List<Good> good) {
            this.good = good;
        }

        public List<Good> getGood() {
            return good;
        }


    }

    public static class Good
    {
        private String uid;
        private String name;

        public void setUid(String uid)
        {
            this.uid = uid;
        }
        public String getUid()
        {
            return uid;
        }

        public void setName(String name)
        {
            this.name = name;
        }
        public String getName()
        {
            return name;
        }

    }

}
