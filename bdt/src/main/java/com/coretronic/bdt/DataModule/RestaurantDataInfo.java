package com.coretronic.bdt.DataModule;

import java.util.List;
/**
 * Created by darren on 2014/12/9.
 */
public class RestaurantDataInfo {
    private String msgCode;
    private String status;
    private List<Result> result;

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

    public void setResult(List<Result> result)
    {
        this.result= result;
    }

    public List<Result> getResult() {
        return result;
    }


    public class Result{
        private String ID;
        private String Walkway;
        private String Name;
        private String Address;
        private String Phone;
        private String Lat;
        private String Lng;
        private String MeanRate;
        private String Dist;

        public void setID(String ID)
        {
            this.ID = ID;
        }
        public String getID()
        {
            return ID;
        }

        public void setWalkway(String Walkway)
        {
            this.Walkway = Walkway;
        }
        public String getWalkway()
        {
            return Walkway;
        }

        public void setName(String Name)
        {
            this.Name = Name;
        }
        public String getName()
        {
            return Name;
        }

        public void setAddress(String Address)
        {
            this.Address = Address;
        }
        public String getAddress()
        {
            return Address;
        }

        public void setPhone(String Phone)
        {
            this.Phone = Phone;
        }
        public String getPhone()
        {
            return Phone;
        }

        public void setLat(String Lat)
        {
            this.Lat = Lat;
        }
        public String getLat()
        {
            return Lat;
        }

        public void setLng(String Lng)
        {
            this.Lng= Lng;
        }
        public String getLng()
        {
            return Lng;
        }

        public void setMeanRate(String MeanRate)
        {
            this.MeanRate = MeanRate;
        }
        public String getMeanRate()
        {
            return MeanRate;
        }

        public void setDist(String Dist)
        {
            this.Dist = Dist;
        }
        public String getDist()
        {
            return Dist;
        }

    }
}
