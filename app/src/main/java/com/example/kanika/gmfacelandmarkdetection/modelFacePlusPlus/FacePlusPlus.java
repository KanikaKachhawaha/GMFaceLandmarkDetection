
package com.example.kanika.gmfacelandmarkdetection.modelFacePlusPlus;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FacePlusPlus {

    @SerializedName("image_id")
    @Expose
    private String imageId;
    @SerializedName("request_id")
    @Expose
    private String requestId;
    @SerializedName("time_used")
    @Expose
    private Integer timeUsed;
    @SerializedName("faces")
    @Expose
    private List<Face> faces = null;

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Integer getTimeUsed() {
        return timeUsed;
    }

    public void setTimeUsed(Integer timeUsed) {
        this.timeUsed = timeUsed;
    }

    public List<Face> getFaces() {
        return faces;
    }

    public void setFaces(List<Face> faces) {
        this.faces = faces;
    }

}
