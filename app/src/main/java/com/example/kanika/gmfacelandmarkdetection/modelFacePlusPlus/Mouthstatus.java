
package com.example.kanika.gmfacelandmarkdetection.modelFacePlusPlus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Mouthstatus {

    @SerializedName("close")
    @Expose
    private Double close;
    @SerializedName("surgical_mask_or_respirator")
    @Expose
    private Double surgicalMaskOrRespirator;
    @SerializedName("open")
    @Expose
    private Double open;
    @SerializedName("other_occlusion")
    @Expose
    private Double otherOcclusion;

    public Double getClose() {
        return close;
    }

    public void setClose(Double close) {
        this.close = close;
    }

    public Double getSurgicalMaskOrRespirator() {
        return surgicalMaskOrRespirator;
    }

    public void setSurgicalMaskOrRespirator(Double surgicalMaskOrRespirator) {
        this.surgicalMaskOrRespirator = surgicalMaskOrRespirator;
    }

    public Double getOpen() {
        return open;
    }

    public void setOpen(Double open) {
        this.open = open;
    }

    public Double getOtherOcclusion() {
        return otherOcclusion;
    }

    public void setOtherOcclusion(Double otherOcclusion) {
        this.otherOcclusion = otherOcclusion;
    }

}
