
package com.example.kanika.gmfacelandmarkdetection.modelFacePlusPlus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attributes {

    @SerializedName("mouthstatus")
    @Expose
    private Mouthstatus mouthstatus;
    @SerializedName("gender")
    @Expose
    private Gender gender;
    @SerializedName("age")
    @Expose
    private Age age;
    @SerializedName("eyestatus")
    @Expose
    private Eyestatus eyestatus;
    @SerializedName("glass")
    @Expose
    private Glass glass;
    @SerializedName("skinstatus")
    @Expose
    private Skinstatus skinstatus;
    @SerializedName("smile")
    @Expose
    private Smile smile;
    @SerializedName("facequality")
    @Expose
    private Facequality facequality;

    public Mouthstatus getMouthstatus() {
        return mouthstatus;
    }

    public void setMouthstatus(Mouthstatus mouthstatus) {
        this.mouthstatus = mouthstatus;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Age getAge() {
        return age;
    }

    public void setAge(Age age) {
        this.age = age;
    }

    public Eyestatus getEyestatus() {
        return eyestatus;
    }

    public void setEyestatus(Eyestatus eyestatus) {
        this.eyestatus = eyestatus;
    }

    public Glass getGlass() {
        return glass;
    }

    public void setGlass(Glass glass) {
        this.glass = glass;
    }

    public Skinstatus getSkinstatus() {
        return skinstatus;
    }

    public void setSkinstatus(Skinstatus skinstatus) {
        this.skinstatus = skinstatus;
    }

    public Smile getSmile() {
        return smile;
    }

    public void setSmile(Smile smile) {
        this.smile = smile;
    }

    public Facequality getFacequality() {
        return facequality;
    }

    public void setFacequality(Facequality facequality) {
        this.facequality = facequality;
    }

}
