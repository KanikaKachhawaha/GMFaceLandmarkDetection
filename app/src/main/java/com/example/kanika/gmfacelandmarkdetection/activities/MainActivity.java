package com.example.kanika.gmfacelandmarkdetection.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kanika.gmfacelandmarkdetection.R;
import com.example.kanika.gmfacelandmarkdetection.api.ApiClient;
import com.example.kanika.gmfacelandmarkdetection.api.ApiService;
import com.example.kanika.gmfacelandmarkdetection.constants.Constants;
import com.example.kanika.gmfacelandmarkdetection.modelFacePlusPlus.Face;
import com.example.kanika.gmfacelandmarkdetection.modelFacePlusPlus.FacePlusPlus;
import com.example.kanika.gmfacelandmarkdetection.modelFacePlusPlus.FaceRectangle;
import com.example.kanika.gmfacelandmarkdetection.modelFacePlusPlus.Landmark;
import com.example.kanika.gmfacelandmarkdetection.utility.AlertPopup;
import com.example.kanika.gmfacelandmarkdetection.utility.ConvertImagePointToViewPoint;
import com.example.kanika.gmfacelandmarkdetection.utility.FileUtil;
import com.example.kanika.gmfacelandmarkdetection.utility.Show_Log;
import com.example.kanika.gmfacelandmarkdetection.utility.Utility;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.xiaopo.flying.sticker.BitmapStickerIcon;
import com.xiaopo.flying.sticker.DrawableSticker;
import com.xiaopo.flying.sticker.Sticker;
import com.xiaopo.flying.sticker.StickerView;
import com.xiaopo.flying.sticker.ZoomIconEvent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    Switch switch_api;
    TextView text_switch;
    Button button_browse_image;
    Button button_call_api;
    PhotoView image_target;
    PhotoView image_canvas;
    ByteArrayOutputStream outputStream;
    ByteArrayInputStream inputStream;
    private File targetFile;
    private String strImgPath = "";
    String imagePathString;
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    Bitmap bitmap = null;

    RelativeLayout relative_image;
    RelativeLayout relative_parent;
    StickerView stickerView;
    ImageView iv_eyebrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setViewsById();
        setOnClickListeners();

        switchApi();

//        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener(mScaleFactor, image_target));
    }

    private void setViewsById() {
        switch_api = (Switch) findViewById(R.id.switch_api);
        text_switch = (TextView) findViewById(R.id.text_switch);
        button_browse_image = (Button) findViewById(R.id.button_browse_image);
        button_call_api = (Button) findViewById(R.id.button_call_api);
        image_target = (PhotoView) findViewById(R.id.image_target);
        stickerView = (StickerView) findViewById(R.id.sticker_view);
        relative_image = (RelativeLayout) findViewById(R.id.relative_image);
        relative_parent = (RelativeLayout) findViewById(R.id.relative_parent);
        iv_eyebrow = (ImageView) findViewById(R.id.iv_eyebrow);
//        image_canvas = (PhotoView) findViewById(R.id.image_canvas);
    }

    private void setOnClickListeners() {
        button_browse_image.setOnClickListener(this);
        button_call_api.setOnClickListener(this);
        text_switch.setOnClickListener(this);
    }

    private void switchApi() {
        switch_api.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "switch checked = " + switch_api.isChecked());
                if (switch_api.isChecked() == true) {
                    text_switch.setText("Face++ Api");
                    //call Face++ api to get the landmark points on the selected image with a human face
                } else {
                    text_switch.setText("Microsoft Api");
                    //call Microsoft azure api to get the landmark points on the selected image with a human face
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_browse_image:
                Utility.showCaptureDialog(MainActivity.this);
                break;
            case R.id.button_call_api:
//                callFacePPApi();
//                callMicrosoftApi();

                File file = FileUtil.getNewFile(MainActivity.this, "Sticker");
                if (file != null) {
                    stickerView.save(file);
                    Toast.makeText(MainActivity.this, "saved in " + file.getAbsolutePath(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "the file is null", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.text_switch:
                startActivity(new Intent(MainActivity.this, MicrosoftApiCalling.class));
                break;
        }
    }

    private void callFacePPApi(final Bitmap bitmap) {
        Utility.startProgressBar(MainActivity.this, "calling retrofit api");
        Map<String, RequestBody> params = new HashMap();
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        try {

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), targetFile);
            MultipartBody.Part profileImg = MultipartBody.Part.createFormData("image_file", targetFile.getName(), requestFile);
            String fileName = "image_file" + "\"; filename=\"" + targetFile.getName() + "\" ";
            params.put(fileName, profileImg.body());
            params.put("api_key", Utility.createPartFromString(Constants.FacePlusPlus_ApiKey));
            params.put("api_secret", Utility.createPartFromString(Constants.FaceplusPlus_ApiSecret));
            params.put("return_landmark", Utility.createPartFromString("2"));
            params.put("return_attributes", Utility.createPartFromString("gender,age,smiling,facequality,eyestatus,mouthstatus,skinstatus"));
            Call<JsonElement> response = apiService.detectFace(params);
            response.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    if (response.body() != null) {
                        Utility.dismissProgressBar();
                        Utility.debug(1, TAG, "Calling Body 1..." + response.body());
                        Log.w(TAG, "print = " + new Gson().toJson(response));
//                        drawFaceRectanglesOnBitmap(bitmap, response.body(), true);
                        Intent send = new Intent(Intent.ACTION_SENDTO);
                        String uriText = "mailto:" + Uri.encode("kanika.kachhawaha@cgt.co.in") +
                                "?subject=" + Uri.encode("the subject") +
                                "&body=" + Uri.encode(response.body().toString());
                        Uri uri = Uri.parse(uriText);

                        send.setData(uri);
                        startActivity(Intent.createChooser(send, "Send mail..."));

                        parseFacePlusPlusResponse(response, bitmap);


                    } else {
                        try {
                            Utility.debug(1, TAG, "Calling Body 2..." + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    Utility.dismissProgressBar();
                    Show_Log.Logcat(TAG, "response failure message = " + t.getLocalizedMessage(), 1);
                    AlertPopup.showAlert_with_OK_cancel(MainActivity.this,
                            "",
                            t.getLocalizedMessage(),
                            true,
                            "OK",
                            "Cancel",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }, false
                    );
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseFacePlusPlusResponse(Response<JsonElement> response, Bitmap imageBitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream imageInputStream =
                new ByteArrayInputStream(outputStream.toByteArray());
        Gson gson = new Gson();
        FacePlusPlus facePlusPlusModel = gson.fromJson(response.body(), FacePlusPlus.class);

        if (facePlusPlusModel != null) {
            if (facePlusPlusModel.getImageId() != null) {
//                image_target.setImageBitmap(bitmap);
                image_target.setImageBitmap(drawFaceRectanglesOnBitmap(bitmap, facePlusPlusModel, true));
//                Bitmap bitmapBrow = BitmapFactory.decodeResource(getResources(), R.drawable.brow3);
//                image_canvas.setImageBitmap(drawFaceRectanglesOnBitmapBrow(bitmap, facePlusPlusModel, true));
                loadSticker(facePlusPlusModel);

//                RelativeLayout.LayoutParams params;
//                params = new RelativeLayout.LayoutParams(50,
//                        50);
////                params.addRule(RelativeLayout.ABOVE, R.id.relative_image);
//                params.leftMargin = facePlusPlusModel.getFaces().get(0).getLandmark().getLeftEyebrowLeftCorner().getX();
//                params.topMargin = facePlusPlusModel.getFaces().get(0).getLandmark().getLeftEyebrowLeftCorner().getY();
//                Drawable drawable = null;
//                drawable = ContextCompat.getDrawable(this, R.drawable.brow1);
//                drawable.setColorFilter(getResources().getColor(R.color.blueProgress), PorterDuff.Mode.SRC_IN);
//                iv_eyebrow.setLayoutParams(params);

//                iv_eyebrow.setX(facePlusPlusModel.getFaces().get(0).getLandmark().getLeftEyebrowLeftCorner().getX());
//                iv_eyebrow.setY(facePlusPlusModel.getFaces().get(0).getLandmark().getLeftEyebrowLeftCorner().getY());

//                relative_parent.addView(stickerView.addSticker(new DrawableSticker(drawable),
//                        facePlusPlusModel.getFaces().get(0).getLandmark().getLeftEyebrowLeftCorner().getX()), params);

//                relative_parent.addView(iv_eyebrow, params);

                pointCalculation(bitmap, facePlusPlusModel);

            }
        }
    }

    private void pointCalculation(Bitmap bitmap, FacePlusPlus facePlusPlusModel) {
        float heightB = (float) bitmap.getHeight();
        float heightI = (float) image_target.getHeight();
        float scaleFactor, widthScaleFactor, heightScaleFactor, viewWidth, viewHeight, bitmapWidth, bitmapHeight;
        viewWidth = image_target.getWidth();
        viewHeight = image_target.getHeight();
        bitmapWidth = bitmap.getWidth();
        bitmapHeight = bitmap.getHeight();
        widthScaleFactor = (float) viewWidth / bitmapWidth;
        heightScaleFactor = (float) viewHeight / bitmapHeight;
        scaleFactor = widthScaleFactor > heightScaleFactor ? heightScaleFactor : widthScaleFactor;


        float viewX = (viewWidth - (bitmapWidth * scaleFactor)) / 2;


        Show_Log.Logcat(TAG,
                "imageView height = " + image_target.getHeight() +
                        "\n imageView width = " + image_target.getWidth() +
                        "\n bitmap height = " + bitmap.getHeight() +
                        "\n bitmap width = " + bitmap.getWidth() +
                        "\n scaleFactor = " + scaleFactor, 1);
    }

    private void loadSticker(FacePlusPlus facePlusPlusModel) {

        BitmapStickerIcon deleteIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
                com.xiaopo.flying.sticker.R.drawable.sticker_ic_close_white_18dp),
                BitmapStickerIcon.LEFT_TOP);
        deleteIcon.setIconRadius(0);
        deleteIcon.setAlpha(0);

        BitmapStickerIcon zoomIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
                com.xiaopo.flying.sticker.R.drawable.sticker_ic_scale_white_18dp),
                BitmapStickerIcon.RIGHT_BOTOM);
        zoomIcon.setIconEvent(new ZoomIconEvent());

        BitmapStickerIcon flipIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
                com.xiaopo.flying.sticker.R.drawable.sticker_ic_flip_white_18dp),
                BitmapStickerIcon.RIGHT_TOP);
        flipIcon.setIconRadius(0);
        flipIcon.setAlpha(0);

        BitmapStickerIcon heartIcon =
                new BitmapStickerIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_white_24dp),
                        BitmapStickerIcon.LEFT_BOTTOM);
        heartIcon.setIconRadius(0);
        heartIcon.setAlpha(0);

        stickerView.setIcons(Arrays.asList(deleteIcon, zoomIcon, flipIcon, heartIcon));
        stickerView.setBackgroundColor(Color.WHITE);
        stickerView.setLocked(false);
        stickerView.setConstrained(false);
        stickerView.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
            @Override
            public void onStickerAdded(@NonNull Sticker sticker) {

            }

            @Override
            public void onStickerClicked(@NonNull Sticker sticker) {

            }

            @Override
            public void onStickerDeleted(@NonNull Sticker sticker) {

            }

            @Override
            public void onStickerDragFinished(@NonNull Sticker sticker) {

            }

            @Override
            public void onStickerTouchedDown(@NonNull Sticker sticker) {

            }

            @Override
            public void onStickerZoomFinished(@NonNull Sticker sticker) {

            }

            @Override
            public void onStickerFlipped(@NonNull Sticker sticker) {

            }

            @Override
            public void onStickerDoubleTapped(@NonNull Sticker sticker) {

            }
        });

    }

    private Bitmap drawFaceRectanglesOnBitmapBrow(Bitmap originalBitmap, FacePlusPlus facePlusPlus, boolean drawLandmarks) {
        Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        final Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.YELLOW);
        int stokeWidth = 1;
        paint.setStrokeWidth(stokeWidth);

        if (facePlusPlus != null && facePlusPlus.getFaces().size() > 0) {
            for (Face face : facePlusPlus.getFaces()) {
                Landmark faceLandmark = face.getLandmark();
                FaceRectangle faceRectangle = face.getFaceRectangle();
//                canvas.drawRect(
//                        faceRectangle.getLeft(),
//                        faceRectangle.getTop(),
//                        faceRectangle.getLeft() + faceRectangle.getWidth(),
//                        faceRectangle.getTop() + faceRectangle.getHeight(),
//                        paint);
            }
        }

        //get width, height, x, y of drawn canvas with respect to the screen


        return bitmap;
    }

    private Bitmap drawFaceRectanglesOnBitmap(Bitmap originalBitmap, FacePlusPlus facePlusPlus, boolean drawLandmarks) {
        Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        final Canvas canvas = new Canvas(bitmap);
        final int[] location = new int[2];
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        int stokeWidth = 1;
        paint.setStrokeWidth(stokeWidth);
        final float heightBitmap = bitmap.getHeight();
        final float widthBitmap = bitmap.getWidth();
        float heightCanvas = canvas.getHeight();
        float widthCanvas = canvas.getWidth();
        float scaleFactor = heightBitmap / heightCanvas;
        //get width, height, x, y of drawn canvas with respect to the screen

        final ViewTreeObserver imageTartgetVTO = image_target.getViewTreeObserver();
        imageTartgetVTO.addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {
                image_target.getViewTreeObserver().removeOnDrawListener(this);
                int imageViewHeight = image_target.getMeasuredHeight();
                int imageViewWidth = image_target.getMeasuredWidth();


                image_target.getLocationOnScreen(location);
                Show_Log.Logcat(TAG,
                        "imageViewHeight = " + imageViewHeight +
                                "\n imageViewWidth = " + imageViewWidth +
                                "\n imageViewX = " + location[0] +
                                "\n imageViewY = " + location[1] +
                                "\n heightBitmap = " + heightBitmap +
                                "\n widthBitmap = " + widthBitmap
//                                + "\n canvas height = " + canvas.getHeight() +
//                                "\n canvas width = " + canvas.getWidth() +
//                                "\n image_target.getLeft() = " + image_target.getLeft() +
//                                "\n image_target.getTop() = " + image_target.getTop() +
//                                "\n canvas right = " + canvas.getClipBounds().right +
//                                "\n canvas bottom = " + canvas.getClipBounds().bottom
                        , 1);
            }
        });


        if (facePlusPlus != null && facePlusPlus.getFaces().size() > 0) {
            for (Face face : facePlusPlus.getFaces()) {
                Landmark faceLandmark = face.getLandmark();
                FaceRectangle faceRectangle = face.getFaceRectangle();

                canvas.drawRect(
                        faceRectangle.getLeft(),
                        faceRectangle.getTop(),
                        faceRectangle.getLeft() + faceRectangle.getWidth(),
                        faceRectangle.getTop() + faceRectangle.getHeight(),
                        paint);

                canvas.drawRect(canvas.getClipBounds(), paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.BLUE);
                int stroke = 10;
                paint.setStrokeWidth(stroke);
                canvas.drawRect(image_target.getLeft(), image_target.getTop(), image_target.getRight(), image_target.getBottom(), paint);

//                canvas.drawLine(faceLandmark.getLeftEyebrowLeftCorner().getX(),
//                        faceLandmark.getLeftEyebrowLeftCorner().getY(),
//                        faceLandmark.getLeftEyebrowLowerRightCorner().getX(),
//                        faceLandmark.getLeftEyebrowLowerRightCorner().getY(),
//                        paint);

//                RelativeLayout.LayoutParams params;
//                params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
//                        RelativeLayout.LayoutParams.WRAP_CONTENT);
//                params.leftMargin = faceLandmark.getLeftEyebrowLeftCorner().getX();
//                params.topMargin = faceLandmark.getLeftEyebrowLeftCorner().getY();
                Drawable drawable = null;
                drawable = ContextCompat.getDrawable(this, R.drawable.brow1);
                drawable.setColorFilter(getResources().getColor(R.color.blueProgress), PorterDuff.Mode.SRC_IN);
//                relative_image.addView(stickerView.addSticker(new DrawableSticker(drawable),
//                        faceLandmark.getLeftEyebrowLeftCorner().getX()), params);

                Show_Log.Logcat(TAG,
                        "left X = " + faceLandmark.getLeftEyebrowLeftCorner().getX() +
                                "\n left Y = " + faceLandmark.getLeftEyebrowLeftCorner().getY() +
                                "\n right X = " + faceLandmark.getRightEyebrowRightCorner().getX() +
                                "\n right Y = " + faceLandmark.getRightEyebrowRightCorner().getY(), 1);
//                stickerView.addSticker(new DrawableSticker(drawable));

                int eyebrowViewPoint[] = new int[2];
                ConvertImagePointToViewPoint convertImagePointToViewPoint = new ConvertImagePointToViewPoint(MainActivity.this,
                        faceLandmark,
                        image_target,
                        bitmap);
                eyebrowViewPoint = convertImagePointToViewPoint.conertImagetoViewPoint(faceLandmark, image_target, bitmap);
                Show_Log.Logcat(TAG,
                        "x point = " + eyebrowViewPoint[0] +
                                ", y point = " + eyebrowViewPoint[1], 1);
                stickerView.addStickerAt(bitmap, new DrawableSticker(drawable),
                        eyebrowViewPoint[0],
                        eyebrowViewPoint[1]
                );

//                stickerView.addStickerAt(bitmap, new DrawableSticker(drawable),
//                        (int) 987.6165822197,
//                        (int) 375.0479525596
//
//                );

//                float[] points = new float[8];
//                points[0] = faceLandmark.getLeftEyebrowLeftCorner().getX();
//                points[1] = faceLandmark.getLeftEyebrowLeftCorner().getY();
//                points[2] = faceLandmark.getLeftEyebrowUpperRightCorner().getX();
//                points[3] = faceLandmark.getLeftEyebrowUpperRightCorner().getY();
//                points[4] = faceLandmark.getLeftEyebrowLeftCorner().getX();
//                points[5] = faceLandmark.getLeftEyebrowLeftCorner().getY();
//                points[6] = faceLandmark.getLeftEyebrowLowerRightCorner().getX();
//                points[7] = faceLandmark.getLeftEyebrowLowerRightCorner().getY();
//
//                stickerView.getCurrentSticker().setBoundPoints(stickerView.getCurrentSticker(), points);
                Drawable drawable1 =
                        ContextCompat.getDrawable(this, R.drawable.brow2);
//                drawable1.setBounds(faceLandmark.getRightEyebrowLowerLeftCorner().getX(),
//                        faceLandmark.getRightEyebrowLowerLeftCorner().getY(),
//                        faceLandmark.getRightEyebrowRightCorner().getX(),
//                        faceLandmark.getRightEyebrowRightCorner().getY());
                drawable1.setColorFilter(getResources().getColor(R.color.blueProgress), PorterDuff.Mode.SRC_IN);

                int eyebrowViewPoint1[] = new int[2];
                ConvertImagePointToViewPoint convertImagePointToViewPoint1 = new ConvertImagePointToViewPoint(MainActivity.this,
                        faceLandmark,
                        image_target,
                        bitmap);
                eyebrowViewPoint = convertImagePointToViewPoint.conertImagetoViewPoint(faceLandmark, image_target, bitmap);
                Show_Log.Logcat(TAG,
                        "x point = " + eyebrowViewPoint[0] +
                                ", y point = " + eyebrowViewPoint[1], 1);
                stickerView.addStickerAt(bitmap, new DrawableSticker(drawable1),
                        faceLandmark.getRightEyebrowRightCorner().getX(),
                        faceLandmark.getRightEyebrowRightCorner().getY() + location[1]
                );

//                stickerView.addStickerAt(new DrawableSticker(drawable1),
//                        (int) 559.7544,
//                        (int) 458.97717
//                );

                canvas.drawLine(faceLandmark.getRightEyebrowRightCorner().getX(),
                        faceLandmark.getRightEyebrowRightCorner().getY(),
                        faceLandmark.getRightEyebrowLowerLeftCorner().getX(),
                        faceLandmark.getRightEyebrowLowerLeftCorner().getY(),
                        paint);

                float[] stickerPoints = stickerView.getStickerPoints(stickerView.getCurrentSticker());


                for (float stickerPoint : stickerPoints) {
                    Show_Log.Logcat(TAG, "sticker points = " + stickerPoint, 1);
                }


//                stickerView.layout(faceLandmark.getLeftEyebrowLeftCorner().getX(),
//                        faceLandmark.getLeftEyebrowLeftCorner().getY(),
//                        (faceLandmark.getLeftEyebrowLowerRightCorner().getX()-faceLandmark.getLeftEyebrowLeftCorner().getX()),
//                        (faceLandmark.getLeftEyebrowLowerRightCorner().getY()-faceLandmark.getLeftEyebrowLeftCorner().getY()));

                if (drawLandmarks) {
                    int radius = faceRectangle.getWidth() / 30;
                    if (radius == 0) {
                        radius = 1;
                    }
                    paint.setStyle(Paint.Style.FILL);
                    paint.setStrokeWidth(radius);

                    canvas.drawCircle(
                            (float) faceLandmark.getLeftEyebrowLeftCorner().getX(),
                            (float) faceLandmark.getLeftEyebrowLeftCorner().getY(),
                            radius,
                            paint);


                    canvas.drawCircle(
                            (float) faceLandmark.getLeftEyebrowUpperRightCorner().getX(),
                            (float) faceLandmark.getLeftEyebrowUpperRightCorner().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getLeftEyebrowLowerRightCorner().getX(),
                            (float) faceLandmark.getLeftEyebrowLowerRightCorner().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getLeftEyePupil().getX(),
                            (float) faceLandmark.getLeftEyePupil().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getRightEyePupil().getX(),
                            (float) faceLandmark.getRightEyePupil().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getNoseTip().getX(),
                            (float) faceLandmark.getNoseTip().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getMouthLeftCorner().getX(),
                            (float) faceLandmark.getMouthLeftCorner().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getMouthRightCorner().getX(),
                            (float) faceLandmark.getMouthRightCorner().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getLeftEyeLeftCorner().getX(),
                            (float) faceLandmark.getLeftEyeLeftCorner().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getLeftEyeTop().getX(),
                            (float) faceLandmark.getLeftEyeTop().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getLeftEyeBottom().getX(),
                            (float) faceLandmark.getLeftEyeBottom().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getLeftEyeRightCorner().getX(),
                            (float) faceLandmark.getLeftEyeRightCorner().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getRightEyebrowRightCorner().getX(),
                            (float) faceLandmark.getRightEyebrowRightCorner().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getRightEyebrowLowerLeftCorner().getX(),
                            (float) faceLandmark.getRightEyebrowLowerLeftCorner().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getRightEyebrowUpperLeftCorner().getX(),
                            (float) faceLandmark.getRightEyebrowUpperLeftCorner().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getRightEyeLeftCorner().getX(),
                            (float) faceLandmark.getRightEyeLeftCorner().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getRightEyeTop().getX(),
                            (float) faceLandmark.getRightEyeTop().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getRightEyeBottom().getX(),
                            (float) faceLandmark.getRightEyeBottom().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getRightEyeRightCorner().getX(),
                            (float) faceLandmark.getRightEyeRightCorner().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getNoseLeftContour1().getX(),
                            (float) faceLandmark.getNoseLeftContour1().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getNoseLeftContour2().getX(),
                            (float) faceLandmark.getNoseLeftContour2().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getNoseLeftContour3().getX(),
                            (float) faceLandmark.getNoseLeftContour3().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getNoseLeftContour4().getX(),
                            (float) faceLandmark.getNoseLeftContour4().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getNoseLeftContour5().getX(),
                            (float) faceLandmark.getNoseLeftContour5().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getNoseRightContour1().getX(),
                            (float) faceLandmark.getNoseRightContour1().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getNoseRightContour2().getX(),
                            (float) faceLandmark.getNoseRightContour2().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getNoseRightContour3().getX(),
                            (float) faceLandmark.getNoseRightContour3().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getNoseRightContour4().getX(),
                            (float) faceLandmark.getNoseRightContour4().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getNoseRightContour5().getX(),
                            (float) faceLandmark.getNoseRightContour5().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getNoseTip().getX(),
                            (float) faceLandmark.getNoseTip().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getNoseBridge1().getX(),
                            (float) faceLandmark.getNoseBridge1().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getNoseBridge2().getX(),
                            (float) faceLandmark.getNoseBridge2().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getNoseBridge3().getX(),
                            (float) faceLandmark.getNoseBridge3().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getMouthLowerLipBottom().getX(),
                            (float) faceLandmark.getMouthLowerLipBottom().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getMouthUpperLipTop().getX(),
                            (float) faceLandmark.getMouthUpperLipTop().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getMouthLowerLipTop().getX(),
                            (float) faceLandmark.getMouthLowerLipTop().getY(),
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) faceLandmark.getMouthUpperLipBottom().getX(),
                            (float) faceLandmark.getMouthUpperLipBottom().getY(),
                            radius,
                            paint);

                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(stokeWidth);
                }
            }
        }
        return bitmap;
    }

    public static byte[] getBytesFromFile(File f) {
        if (f == null) {
            return null;
        }
        try {
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1)
                out.write(b, 0, n);
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == Constants.CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Utility.CaptureImageViaCamera(MainActivity.this);
            }
        }
        if (requestCode == Constants.GALLERY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Utility.CaptureImageViaGallery(MainActivity.this);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Show_Log.Logcat(TAG, " res code" + resultCode +
                " request code " + requestCode +
                " data" + data, 1);
        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {
                case Constants.CAMERA_REQUEST_CODE:
                    strImgPath = Utility.filePath.getAbsolutePath();
                    bitmap = Utility.decodeSampledBitmapFromFile(strImgPath, 320, 480);
                    int degree = Utility.getExifOrientation(strImgPath);
                    bitmap = Utility.rotate(bitmap, degree + Utility.getDisplayRotation(MainActivity.this));
                    targetFile = Utility.saveBitmap(strImgPath, bitmap);
                    Utility.setImage(MainActivity.this, strImgPath, image_target);
                    callFacePPApi(bitmap);

                    break;
                case Constants.GALLERY_REQUEST_CODE:

                    Uri photoUri = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
                        Show_Log.Logcat(TAG, "Gallery Activity Result bitmap = " + bitmap, 1);
                        imagePathString = Utility.getRealPathFromURI(photoUri, MainActivity.this);
                        targetFile = new File(imagePathString);

////                    String strImagePath1 = Utility.getGalleryImagePath(MainActivity.this, data.getData());
//                    bitmap = Utility.decodeSampledBitmapFromFile(imagePathString, 320, 480);
////                    File filePath = Utility.getOutputFilePath(MainActivity.this, Constants.DIR_IMAGE,
////                            Constants.PREFIX_IMG + System.currentTimeMillis() + Constants.EXT_JPG);
////                    strImgPath = filePath.getAbsolutePath();
//                    Show_Log.Logcat(TAG, "image path = " + strImgPath.toString(), 1);
//                    int degree1 = Utility.getExifOrientation(imagePathString);
//                    bitmap = Utility.rotate(bitmap, degree1 + Utility.getDisplayRotation(MainActivity.this));
//                    if (bitmap != null) {
//                        Show_Log.Logcat(TAG, "image bitmap = " + bitmap.toString(), 1);
//                    }
//                    targetFile = Utility.saveBitmap(imagePathString, bitmap);
//                    Utility.setImage(MainActivity.this, imagePathString, image_target);
                        callFacePPApi(bitmap);
//                    outputStream = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//                    inputStream =
//                            new ByteArrayInputStream(outputStream.toByteArray());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }


}
