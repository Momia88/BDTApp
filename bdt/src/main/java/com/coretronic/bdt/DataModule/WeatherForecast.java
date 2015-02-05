package com.coretronic.bdt.DataModule;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by Morris on 2014/9/10.
 */
public class WeatherForecast {
    private String cod;
    private float message;
    private City city;
    private int cnt;
    private List<Forecast> list;

    public String getCod() {
        return cod;
    }

    public float getMessage() {
        return message;
    }

    public City getCity() {
        return city;
    }

    public int getCnt() {
        return cnt;
    }

    public List<Forecast> getList() {
        return list;
    }

    public class City {
        private BigInteger id;
        private String name;
        private Coord coord;
        private String country;
        private int population;

        public BigInteger getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Coord getCoord() {
            return coord;
        }

        public String getCountry() {
            return country;
        }

        public int getPopulation() {
            return population;
        }
    }

    public class Coord {
        private float lat;
        private float lon;

        public float getLat() {
            return lat;
        }

        public float getLon() {
            return lon;
        }
    }

    public class Forecast {
        private BigInteger dt;
        private Temperature temp;
        private float pressure;
        private int humidity;
        private List<Weather> weather;
        private float speed;
        private int deg;
        private int clouds;

        public BigInteger getDt() {
            return dt;
        }

        public Temperature getTemp() {
            return temp;
        }

        public float getPressure() {
            return pressure;
        }

        public int getHumidity() {
            return humidity;
        }

        public List<Weather> getWeather() {
            return weather;
        }

        public float getSpeed() {
            return speed;
        }

        public int getDeg() {
            return deg;
        }

        public int getClouds() {
            return clouds;
        }
    }

    public class Temperature {
        private float day;
        private float min;
        private float max;
        private float night;
        private float eve;
        private float morn;

        public float getDay() {
            return day;
        }

        public float getMin() {
            return min;
        }

        public float getMax() {
            return max;
        }

        public float getNight() {
            return night;
        }

        public float getEve() {
            return eve;
        }

        public float getMorn() {
            return morn;
        }
    }

    public class Weather {
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
}
