package com.example.weatherapp;

public class WeatherItem
{
    String time;
    String location;
    String temperature;
    String imgCode;
    String country;
    String description;

    public WeatherItem(String time, String city, String temperature, String imgCode, String country, String description) {
        this.time = time;
        this.location = city;
        this.temperature = temperature;
        this.imgCode = imgCode;
        this.country = country;
        this.description = description;
        //this.dt = dt;
    }
    //get rid of setters later if not being used

    public String getTime() {
        return time;
    }
    /*public String getDt() {
        return dt;
    }
     */

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getImgCode() {
        return imgCode;
    }

    public void setImgCode(String img) {
        this.imgCode = img;
    }

}
