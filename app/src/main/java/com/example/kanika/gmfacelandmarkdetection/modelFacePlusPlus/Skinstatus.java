
package com.example.kanika.gmfacelandmarkdetection.modelFacePlusPlus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Skinstatus {

    @SerializedName("dark_circle")
    @Expose
    private Double darkCircle;
    @SerializedName("stain")
    @Expose
    private Double stain;
    @SerializedName("acne")
    @Expose
    private Double acne;
    @SerializedName("health")
    @Expose
    private Double health;

    public Double getDarkCircle() {
        return darkCircle;
    }

    public void setDarkCircle(Double darkCircle) {
        this.darkCircle = darkCircle;
    }

    public Double getStain() {
        return stain;
    }

    public void setStain(Double stain) {
        this.stain = stain;
    }

    public Double getAcne() {
        return acne;
    }

    public void setAcne(Double acne) {
        this.acne = acne;
    }

    public Double getHealth() {
        return health;
    }

    public void setHealth(Double health) {
        this.health = health;
    }

}
