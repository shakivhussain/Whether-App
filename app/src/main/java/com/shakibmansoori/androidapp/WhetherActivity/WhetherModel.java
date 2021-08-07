package com.shakibmansoori.androidapp.WhetherActivity;

public class WhetherModel {

    public final Double temp_c;
    public final Double temp_f;
    public final Double latitude;
    public final Double longitude;
    public final String imgUrl;
    public final String currentWhe;

    public WhetherModel(Double temp_c, Double temp_f, Double latitude, Double longitude, String imgUrl, String currentWhe) {
        this.temp_c = temp_c;
        this.temp_f = temp_f;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imgUrl = imgUrl;
        this.currentWhe = currentWhe;
    }

}
