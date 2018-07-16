package com.example.kanika.gmfacelandmarkdetection.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.example.kanika.gmfacelandmarkdetection.modelFacePlusPlus.Landmark;
import com.microsoft.projectoxford.face.contract.FaceLandmarks;

/**
 * Created by kanika on 26/6/18.
 */
public class ConvertImagePointToViewPoint {
    private static final String TAG = ConvertImagePointToViewPoint.class.getSimpleName();
    Context context;
    Landmark faceLandmarks;
    ImageView imageView;
    Bitmap bitmap;
    float scaleFactor;
    float heightScaleFactor;
    float widthScaleFactor;

    public ConvertImagePointToViewPoint(Context context, Landmark faceLandmarks, ImageView imageView, Bitmap bitmap) {
        this.context = context;
        this.faceLandmarks = faceLandmarks;
        this.imageView = imageView;
        this.bitmap = bitmap;
    }

    public int[] conertImagetoViewPoint(Landmark faceLandmarks, ImageView imageView, Bitmap bitmap) {
        int viewPoint[] = new int[2];

        viewPoint[0] = faceLandmarks.getLeftEyebrowLeftCorner().getX();
        viewPoint[1] = faceLandmarks.getLeftEyebrowLeftCorner().getY();

        widthScaleFactor = imageView.getWidth() / bitmap.getWidth();
        heightScaleFactor = imageView.getHeight() / bitmap.getHeight();
        Log.d(TAG, "image scaled to = "+imageView.getScaleType());
        switch (imageView.getScaleType()){
            case CENTER:
                Log.d(TAG, "image scaled to CENTER = "+imageView.getScaleType());
                break;
            case FIT_XY:
                scaleFactor = widthScaleFactor > heightScaleFactor ? heightScaleFactor : widthScaleFactor;
                viewPoint[0] *= scaleFactor;
                viewPoint[1] *= scaleFactor;

                viewPoint[0] += ((imageView.getWidth()  - (bitmap.getWidth()  * scaleFactor))) / 2.0f;
                viewPoint[1] += ((imageView.getHeight() - (bitmap.getHeight() * scaleFactor))) / 2.0f;
                break;
            case MATRIX:
                Log.d(TAG, "image scaled to MATRIX = "+imageView.getScaleType());
                break;
            case FIT_END:
                viewPoint[0] += imageView.getWidth() - bitmap.getWidth();
                viewPoint[1] += (imageView.getHeight() / 2.0) - (bitmap.getHeight() / 2.0f);
                break;
            case FIT_START:
                viewPoint[1] += (imageView.getHeight() / 2.0) - (bitmap.getHeight() / 2.0f);
                break;
            case FIT_CENTER:
                viewPoint[0] += (imageView.getWidth() / 2.0)  - (bitmap.getWidth() / 2.0f);
                viewPoint[1] += (imageView.getHeight() / 2.0) - (bitmap.getHeight() / 2.0f);
                break;
            case CENTER_CROP:
                scaleFactor = widthScaleFactor > heightScaleFactor ? heightScaleFactor : widthScaleFactor;
                viewPoint[0] *= scaleFactor;
                viewPoint[1] *= scaleFactor;

                viewPoint[0] += ((imageView.getWidth()  - (bitmap.getWidth()  * scaleFactor))) / 2.0f;
                viewPoint[1] += ((imageView.getHeight() - (bitmap.getHeight() * scaleFactor))) / 2.0f;
                break;
            case CENTER_INSIDE:
                viewPoint[0] += (imageView.getWidth() / 2.0)  - (bitmap.getWidth() / 2.0f);
                viewPoint[1] += (imageView.getHeight() / 2.0) - (bitmap.getHeight() / 2.0f);
                break;
        }

        return viewPoint;
    }
}
