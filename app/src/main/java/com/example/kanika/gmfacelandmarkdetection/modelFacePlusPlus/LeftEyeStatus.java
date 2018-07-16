
package com.example.kanika.gmfacelandmarkdetection.modelFacePlusPlus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LeftEyeStatus {

    @SerializedName("normal_glass_eye_open")
    @Expose
    private Double normalGlassEyeOpen;
    @SerializedName("no_glass_eye_close")
    @Expose
    private Double noGlassEyeClose;
    @SerializedName("occlusion")
    @Expose
    private Double occlusion;
    @SerializedName("no_glass_eye_open")
    @Expose
    private Double noGlassEyeOpen;
    @SerializedName("normal_glass_eye_close")
    @Expose
    private Double normalGlassEyeClose;
    @SerializedName("dark_glasses")
    @Expose
    private Double darkGlasses;

    public Double getNormalGlassEyeOpen() {
        return normalGlassEyeOpen;
    }

    public void setNormalGlassEyeOpen(Double normalGlassEyeOpen) {
        this.normalGlassEyeOpen = normalGlassEyeOpen;
    }

    public Double getNoGlassEyeClose() {
        return noGlassEyeClose;
    }

    public void setNoGlassEyeClose(Double noGlassEyeClose) {
        this.noGlassEyeClose = noGlassEyeClose;
    }

    public Double getOcclusion() {
        return occlusion;
    }

    public void setOcclusion(Double occlusion) {
        this.occlusion = occlusion;
    }

    public Double getNoGlassEyeOpen() {
        return noGlassEyeOpen;
    }

    public void setNoGlassEyeOpen(Double noGlassEyeOpen) {
        this.noGlassEyeOpen = noGlassEyeOpen;
    }

    public Double getNormalGlassEyeClose() {
        return normalGlassEyeClose;
    }

    public void setNormalGlassEyeClose(Double normalGlassEyeClose) {
        this.normalGlassEyeClose = normalGlassEyeClose;
    }

    public Double getDarkGlasses() {
        return darkGlasses;
    }

    public void setDarkGlasses(Double darkGlasses) {
        this.darkGlasses = darkGlasses;
    }

}
