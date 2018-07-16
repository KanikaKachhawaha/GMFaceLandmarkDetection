
package com.example.kanika.gmfacelandmarkdetection.modelFacePlusPlus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Eyestatus {

    @SerializedName("left_eye_status")
    @Expose
    private LeftEyeStatus leftEyeStatus;
    @SerializedName("right_eye_status")
    @Expose
    private RightEyeStatus rightEyeStatus;

    public LeftEyeStatus getLeftEyeStatus() {
        return leftEyeStatus;
    }

    public void setLeftEyeStatus(LeftEyeStatus leftEyeStatus) {
        this.leftEyeStatus = leftEyeStatus;
    }

    public RightEyeStatus getRightEyeStatus() {
        return rightEyeStatus;
    }

    public void setRightEyeStatus(RightEyeStatus rightEyeStatus) {
        this.rightEyeStatus = rightEyeStatus;
    }

}
