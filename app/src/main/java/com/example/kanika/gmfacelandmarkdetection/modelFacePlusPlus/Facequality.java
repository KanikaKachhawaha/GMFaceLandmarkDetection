
package com.example.kanika.gmfacelandmarkdetection.modelFacePlusPlus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Facequality {

    @SerializedName("threshold")
    @Expose
    private Double threshold;
    @SerializedName("value")
    @Expose
    private Double value;

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

}
