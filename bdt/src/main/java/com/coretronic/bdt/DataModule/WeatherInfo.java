package com.coretronic.bdt.DataModule;

import com.google.gson.annotations.SerializedName;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by Morris on 2014/2/27.
 */
public class WeatherInfo {

    private Coord coord;
    private Sys sys;
    private List<Weather> weather;
    private String base;
    private MainValue main;
    private Wind wind;
    private Rain rain;
    private Snow snow;
    private Clouds clouds;
    private BigInteger id;
    private BigInteger dt;
    private String name;
    private int cod;

    public Coord getCoord() {
        return coord;
    }

    public Sys getSys() {
        return sys;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public MainValue getMain() {
        return main;
    }

    public Wind getWind() {
        return wind;
    }

    public Rain getRain() {
        return rain;
    }

    public Snow getSnow() {
        return snow;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public int getCod() {
        return cod;
    }

    public String getName() {
        return name;
    }

    public BigInteger getDt() {
        return dt;
    }

    public BigInteger getId() {
        return id;
    }

    public String getBase() {
        return base;
    }

    public class Clouds{
        int all;
    }
    public class Snow{
        @SerializedName("3h")
        private float h3;
    }
    public class Rain {
        @SerializedName("3h")
        private float h3;
    }
    public class Coord{
        private float lat;
        private float lon;

        public float getLat() {
            return lat;
        }

        public float getLon() {
            return lon;
        }
    }

    public class Sys {
        private float message;
        private String country;
        private int sunrise;
        private int sunset;

        public float getMessage() {
            return message;
        }

        public String getCountry() {
            return country;
        }

        public int getSunrise() {
            return sunrise;
        }

        public int getSunset() {
            return sunset;
        }
    }

    public class Weather{
        private int id;
        private String main;
        private String description;
        private String icon;

        public int getId() {
            return id;
        }

        public String getMain() {
            return main;
        }

        public String getDescription() {
            return description;
        }

        public String getIcon() {
            return icon;
        }
    }

    public class Wind {
        private float speed;
        private float deg;

        public float getSpeed() {
            return speed;
        }

        public float getDeg() {
            return deg;
        }
    }

    public class MainValue{
        private float temp;
        private float temp_min;
        private float temp_max;
        private float pressure;
        private float sea_level;
        private float grnd_level;
        private int humidity;

        public float getSea_level() {
            return sea_level;
        }

        public float getGrnd_level() {
            return grnd_level;
        }

        public float getTemp() {
            return temp;
        }

        public float getPressure() {
            return pressure;
        }

        public int getHumidity() {
            return humidity;
        }

        public float getTemp_min() {
            return temp_min;
        }

        public float getTemp_max() {
            return temp_max;
        }
    }

}
