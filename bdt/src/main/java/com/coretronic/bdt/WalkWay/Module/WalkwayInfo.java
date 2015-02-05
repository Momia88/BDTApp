package com.coretronic.bdt.WalkWay.Module;

import java.util.List;

/**
 * Created by Morris on 14/10/29.
 */
public class WalkwayInfo {

    private String walkwayId;
    private String imagePath;
    private String walkwayName;
    private String walkwayTitle;
    private String walkwayFeature;
    private String parking;
    private String walkwayAddress;
    private String kilometers;
    private String description;
    private GLocation location;
    private List<Friends> friends;
    private String visited;
    private List<Weather> weather;

    public class GLocation{
        private String lat;
        private String lng;

        public String getLat() {
            return lat;
        }

        public String getLng() {
            return lng;
        }
    }

    public class Weather{
        String status;
        String pop;
        String date;

        public String getPop() {
            return pop;
        }

        public String getStatus() {
            return status;
        }

        public String getDate() {
            return date;
        }
    }

    public class Friends{
        String id;
        String name;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getDescription() {
        return description;
    }

    public GLocation getLocation() {
        return location;
    }

    public String getWalkwayId() {
        return walkwayId;
    }

    public String getWalkwayName() {
        return walkwayName;
    }

    public String getWalkwayTitle() {
        return walkwayTitle;
    }

    public String getWalkwayFeature() {
        return walkwayFeature;
    }

    public String getWalkwayAddress() {
        return walkwayAddress;
    }

    public String getParking() {
        return parking;
    }

    public String getKilometers() {
        return kilometers;
    }

    public String getVisited() {
        return visited;
    }

    public List<Friends> getFriends() {
        return friends;
    }

    public List<Weather> getWeather() {
        return weather;
    }
}
