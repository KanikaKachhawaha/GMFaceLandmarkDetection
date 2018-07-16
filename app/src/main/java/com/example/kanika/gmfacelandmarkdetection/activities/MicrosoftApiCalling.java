package com.example.kanika.gmfacelandmarkdetection.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.kanika.gmfacelandmarkdetection.R;
import com.example.kanika.gmfacelandmarkdetection.api.ApiClient;
import com.example.kanika.gmfacelandmarkdetection.api.ApiService;
import com.example.kanika.gmfacelandmarkdetection.constants.Constants;
import com.example.kanika.gmfacelandmarkdetection.utility.Show_Log;
import com.example.kanika.gmfacelandmarkdetection.utility.Utility;
import com.google.gson.JsonElement;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceLandmarks;
import com.microsoft.projectoxford.face.contract.FaceRectangle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MicrosoftApiCalling extends AppCompatActivity {

    private final int PICK_IMAGE = 1;
    private ProgressDialog detectionProgressDialog;
    FaceServiceClient faceServiceClient;
    static String TAG = MicrosoftApiCalling.class.getSimpleName();
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_microsoft_api_calling);

        Button button1 = (Button) findViewById(R.id.button);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallIntent = new Intent(Intent.ACTION_GET_CONTENT);
                gallIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(gallIntent, "Select Picture"), PICK_IMAGE);
            }
        });

        detectionProgressDialog = new ProgressDialog(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
                File filePath = Utility.getOutputFilePath(MicrosoftApiCalling.this, Constants.DIR_IMAGE,
                        Constants.PREFIX_IMG + System.currentTimeMillis() + Constants.EXT_JPG);
                String strImgPath = filePath.getAbsolutePath();

                File targetFile = Utility.saveBitmap(strImgPath, bitmap);
                Show_Log.Logcat(TAG, "image string = " + targetFile, 1);


//                callApi(targetFile);
//                callMicrosoftApi(targetFile, bitmap);
                detectAndFrame(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void callApi(File file) {
        faceServiceClient = new FaceServiceRestClient(
                "https://westcentralus.api.cognitive.microsoft.com/face/v1.0",
                "cafa95be33944158867687de25430c4b");
    }

    private void callMicrosoftApi(final File targetFile, Bitmap bitmap) {
        Utility.startProgressBar(MicrosoftApiCalling.this, "calling retrofit api");



        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream imageInputStream =
                new ByteArrayInputStream(outputStream.toByteArray());
        byte[] profilePictureByte = outputStream.toByteArray();



        Map<String, RequestBody> params = new HashMap();
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        try {

            RequestBody requestFile = RequestBody.create(MediaType.parse("application/octet-stream"),profilePictureByte);
//            RequestBody requestFile = RequestBody.create(MediaType.parse("application/octet-stream"), targetFile);
            MultipartBody.Part profileImg = MultipartBody.Part.createFormData("image_file", targetFile.getName(), requestFile);
            String fileName = "image_file" + "\"; filename=\"" + targetFile.getName() + "\"";

            params.put("returnFaceAttributes", Utility.createPartFromString("glasses"));
            params.put(fileName, profileImg.body());
            Call<JsonElement> response = apiService.detectFaceMS(params);
            response.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    if (response.body() != null) {
                        Utility.dismissProgressBar();
                        Utility.debug(1, TAG, "Microsoft Body 1..." + response.body());

//                        drawFaceRectanglesOnBitmap(bitmap, response.body(), true);

                    } else {
                        try {
                            Utility.debug(1, TAG, "Microsoft Body 2..." + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    Utility.dismissProgressBar();
                    Show_Log.Logcat(TAG, "register response failure" + t.getLocalizedMessage(), 1);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Detect faces by uploading face images
    // Frame faces after detection

    private void detectAndFrame(final Bitmap imageBitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream imageInputStream =
                new ByteArrayInputStream(outputStream.toByteArray());
        AsyncTask<InputStream, String, Face[]> detectTask =
                new AsyncTask<InputStream, String, Face[]>() {
                    @Override
                    protected Face[] doInBackground(InputStream... params) {
                        try {
                            publishProgress("Detecting...");
                            Face[] result = faceServiceClient.detect(
                                    params[0],
                                    true,         // returnFaceId
                                    true,        // returnFaceLandmarks
                                    new FaceServiceClient.FaceAttributeType[]{
                                            FaceServiceClient.FaceAttributeType.Age,
                                            FaceServiceClient.FaceAttributeType.Gender,
                                            FaceServiceClient.FaceAttributeType.Emotion,
                                            FaceServiceClient.FaceAttributeType.Smile}           // returnFaceAttributes: a string like "age, gender"
                /* If you want value of FaceAttributes, try adding 4th argument like below.
                            new FaceServiceClient.FaceAttributeType[] {
                    FaceServiceClient.FaceAttributeType.Age,
                    FaceServiceClient.FaceAttributeType.Gender }
                */
                            );
                            if (result == null) {
                                publishProgress("Detection Finished. Nothing detected");
                                return null;
                            }
                            publishProgress(
                                    String.format("Detection Finished. %d face(s) detected",
                                            result.length));
                            return result;
                        } catch (Exception e) {
                            publishProgress("Detection failed");
                            return null;
                        }
                    }

                    @Override
                    protected void onPreExecute() {
                        //TODO: show progress dialog
                        detectionProgressDialog.show();
                    }

                    @Override
                    protected void onProgressUpdate(String... progress) {
                        //TODO: update progress
                        detectionProgressDialog.setMessage(progress[0]);
                    }

                    @Override
                    protected void onPostExecute(Face[] result) {
                        //TODO: update face frames
                        detectionProgressDialog.dismiss();
                        Show_Log.Logcat(TAG, "detected result = " + result, 1);
                        if (result == null) return;
                        ImageView imageView = (ImageView) findViewById(R.id.imageView);
                        imageView.setImageBitmap(drawFaceRectanglesOnBitmap(imageBitmap, result, true));
                        imageBitmap.recycle();
                    }
                };
        detectTask.execute(imageInputStream);
    }

    private static Bitmap drawFaceRectanglesOnBitmap(Bitmap originalBitmap, Face[] faces, boolean drawLandmarks) {
        Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        int stokeWidth = 2;
        paint.setStrokeWidth(stokeWidth);
        if (faces != null) {
            for (Face face : faces) {
                Show_Log.Logcat(TAG, "json = " + face.toString(), 1);
                FaceRectangle faceRectangle = face.faceRectangle;
                FaceLandmarks faceLandmarks = face.faceLandmarks;
                canvas.drawRect(
                        faceRectangle.left,
                        faceRectangle.top,
                        faceRectangle.left + faceRectangle.width,
                        faceRectangle.top + faceRectangle.height,
                        paint);

                if (drawLandmarks) {
                    int radius = face.faceRectangle.width / 30;
                    if (radius == 0) {
                        radius = 1;
                    }
                    paint.setStyle(Paint.Style.FILL);
                    paint.setStrokeWidth(radius);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.pupilLeft.x,
                            (float) face.faceLandmarks.pupilLeft.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.pupilRight.x,
                            (float) face.faceLandmarks.pupilRight.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.noseTip.x,
                            (float) face.faceLandmarks.noseTip.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.mouthLeft.x,
                            (float) face.faceLandmarks.mouthLeft.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.mouthRight.x,
                            (float) face.faceLandmarks.mouthRight.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.eyebrowLeftOuter.x,
                            (float) face.faceLandmarks.eyebrowLeftOuter.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.eyebrowLeftInner.x,
                            (float) face.faceLandmarks.eyebrowLeftInner.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.eyeLeftOuter.x,
                            (float) face.faceLandmarks.eyeLeftOuter.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.eyeLeftTop.x,
                            (float) face.faceLandmarks.eyeLeftTop.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.eyeLeftBottom.x,
                            (float) face.faceLandmarks.eyeLeftBottom.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.eyeLeftInner.x,
                            (float) face.faceLandmarks.eyeLeftInner.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.eyebrowRightInner.x,
                            (float) face.faceLandmarks.eyebrowRightInner.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.eyebrowRightOuter.x,
                            (float) face.faceLandmarks.eyebrowRightOuter.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.eyeRightInner.x,
                            (float) face.faceLandmarks.eyeRightInner.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.eyeRightTop.x,
                            (float) face.faceLandmarks.eyeRightTop.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.eyeRightBottom.x,
                            (float) face.faceLandmarks.eyeRightBottom.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.eyeRightOuter.x,
                            (float) face.faceLandmarks.eyeRightOuter.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.noseRootLeft.x,
                            (float) face.faceLandmarks.noseRootLeft.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.noseRootRight.x,
                            (float) face.faceLandmarks.noseRootRight.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.noseLeftAlarTop.x,
                            (float) face.faceLandmarks.noseLeftAlarTop.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.noseRightAlarTop.x,
                            (float) face.faceLandmarks.noseRightAlarTop.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.noseLeftAlarOutTip.x,
                            (float) face.faceLandmarks.noseLeftAlarOutTip.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.noseRightAlarOutTip.x,
                            (float) face.faceLandmarks.noseRightAlarOutTip.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.upperLipTop.x,
                            (float) face.faceLandmarks.upperLipTop.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.upperLipBottom.x,
                            (float) face.faceLandmarks.upperLipBottom.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.underLipTop.x,
                            (float) face.faceLandmarks.underLipTop.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.underLipBottom.x,
                            (float) face.faceLandmarks.underLipBottom.y,
                            radius,
                            paint);

                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(stokeWidth);
                }
            }
        }
        return bitmap;
    }

}
