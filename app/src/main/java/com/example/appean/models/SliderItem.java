package com.example.appean.models;

public class SliderItem {
    private String imageUrl;
    private long timeStamp;

    //Constructor vacio
    public SliderItem(){}

    //Constructor
    public SliderItem(String imageUrl, long timeStamp) {
        this.imageUrl = imageUrl;
        this.timeStamp = timeStamp;
    }

    //Getters y setters
    public String getImageUrl() {return imageUrl;}
    public void setImageUrl(String imageUrl) {this.imageUrl = imageUrl;}

    public long getTimeStamp() {return timeStamp;}
    public void setTimeStamp(long timeStamp) {this.timeStamp = timeStamp;}
}
