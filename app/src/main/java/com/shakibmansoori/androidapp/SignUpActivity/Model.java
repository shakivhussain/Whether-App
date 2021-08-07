package com.shakibmansoori.androidapp.SignUpActivity;

public class Model {


    public final String name;

    /**
     * Number of people who felt the earthquake and reported how strong it was
     */
    public final String district;

    /**
     * Perceived strength of the earthquake from the people's responses
     */
    public final String state;


    public Model(String name, String district, String state) {
        this.name = name;
        this.district = district;
        this.state = state;
    }

}
