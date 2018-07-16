package com.example.kanika.gmfacelandmarkdetection.api;

import com.google.gson.JsonElement;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;

/**
 * Created by kanika on 11/6/18.
 */
public interface ApiService {

    @Multipart
    @POST("detect")
    Call<JsonElement> detectFace(@PartMap Map<String, RequestBody> params);


    @Multipart
    @POST("detect?returnFaceLandmarks=true")
    @Headers({
            "Content-Type: application/octet-stream",
//            "Content-Type: application/json; charset=utf-8",
            "Ocp-Apim-Subscription-Key: cafa95be33944158867687de25430c4b",
    })
    Call<JsonElement> detectFaceMS(@PartMap Map<String, RequestBody> params);
}
