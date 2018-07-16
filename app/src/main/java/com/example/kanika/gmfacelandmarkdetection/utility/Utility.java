package com.example.kanika.gmfacelandmarkdetection.utility;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kanika.gmfacelandmarkdetection.R;
import com.example.kanika.gmfacelandmarkdetection.activities.MainActivity;
import com.example.kanika.gmfacelandmarkdetection.constants.Constants;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by kanika on 8/6/18.
 */
public class Utility {
    private static String TAG = Utility.class.getSimpleName();
    private Context mContext;
    public static File filePath;
    private static CustomProgressDialog mProgressDialog;
    /**
     * function to  showCaptureDialog
     *
     * @param mContext
     */
    public static Dialog mDialog;
    public static Uri mImageURI;

    public static void startProgressBar(Activity ctx, String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new CustomProgressDialog(ctx, message);
        }

        mProgressDialog.show();
    }

    public static void startProgressBar(Activity ctx, String message, boolean isCancel) {
        if (mProgressDialog == null) {
            mProgressDialog = new CustomProgressDialog(ctx, message, isCancel);
        }

        mProgressDialog.show();
    }

    public static void dismissProgressBar() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public static void showCaptureDialog(final Activity mContext) {
        if (mDialog != null) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            mDialog = null;
        }

        mDialog = new Dialog(mContext, R.style.Dialog_No_Border);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.capture_dialog);
        mDialog.setCancelable(false);

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        Window window = mDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        Show_Log.Logcat(TAG, "width is: " + width, 1);


        wlp.flags &= ~WindowManager.LayoutParams.DIM_AMOUNT_CHANGED;
        window.setAttributes(wlp);
        mDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.argb(0, 0, 0, 0)));


        final TextView txt_FromCamera = (TextView) mDialog.findViewById(R.id.txt_FromCamera);


        final TextView txt_FromGallery = (TextView) mDialog.findViewById(R.id.txt_FromGallery);

        final TextView txt_cancel = (TextView) mDialog.findViewById(R.id.txt_cancel);


        txt_FromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        CaptureImageViaCamera(mContext);
                    } else {
                        ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.CAMERA,
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                Constants.CAMERA_REQUEST_CODE);
                    }
                }

                mDialog.dismiss();
            }
        });

        txt_FromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        CaptureImageViaGallery(mContext);
                    } else {
                        ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.GALLERY_REQUEST_CODE);
                    }
                }

                mDialog.dismiss();
            }
        });

        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });


        try {
            mDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //    This method using for capturing image from gallery
    public static void CaptureImageViaGallery(Activity context) {
        Show_Log.Logcat(TAG, "in CaptureImageViaGallery", 1);
            Intent mIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                context.startActivityForResult(mIntent, Constants.GALLERY_REQUEST_CODE);
    }

    //    This method using for capturing image from camera
    public static void CaptureImageViaCamera(Activity mActivity) {
        try {
            Show_Log.Logcat(TAG, "in CaptureImageViaCamera", 1);
//            PackageManager mPkgMgr = mActivity.getPackageManager();
//            if (mPkgMgr.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
//
//               String filepaths = Environment
//                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//                                + "/" + mActivity.getResources().getString(R.string.app_name) + "/";
//
//                        File myDir = new File(filepaths);
//                        if (myDir.exists() == false)
//                            myDir.mkdirs();
//
//                        Calendar c = Calendar.getInstance();
//                        SimpleDateFormat sdf = new SimpleDateFormat(
//                                "dd:MM:yyyy_hh:mm:ss a", Locale.ENGLISH);
//                        String strDate = sdf.format(c.getTime());
//
//                        DateFormat inputFormatter = new SimpleDateFormat(
//                                "dd:MM:yyyy_hh:mm:ss a", Locale.ENGLISH);
//                        Date date;
//                        try {
//                            date = inputFormatter.parse(strDate);
//                            DateFormat outputFormatter = new SimpleDateFormat(
//                                    "dd_MM_yyyy_hh_mm_ss_a", Locale.ENGLISH);
//                            String output = outputFormatter.format(date);
//
//                            String filename = mActivity.getResources().getString(R.string.app_name) + " " + output + ".jpg";
//                            File file = new File(myDir, filename);
//                            mImageURI = Uri.fromFile(file);
////                            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
////                            StrictMode.setVmPolicy(builder.build());
//                            Intent mCamIntent = new Intent(
//                                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                            mCamIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, 90);
//                            mCamIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageURI);
//                            mActivity.startActivityForResult(mCamIntent,
//                                    Constants.CAMERA_REQUEST_CODE);
//
//                        } catch (Exception e) {
//                            Show_Log.Logcat(TAG, "execption==" + e.getMessage(), 1);
//                        }
//
//            }
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            filePath = getOutputFilePath(mActivity, Constants.DIR_IMAGE, Constants.PREFIX_IMG +
                    System.currentTimeMillis() + Constants.EXT_JPG);
            if (filePath != null) {
                Uri photoURI = FileProvider.getUriForFile(mActivity, mActivity.getPackageName() + ".provider", filePath);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                mActivity.startActivityForResult(cameraIntent, Constants.CAMERA_REQUEST_CODE);
            }
            else {
                AlertPopup.showAlert_with_OK_cacel(mActivity
                        , mActivity.getString(R.string.camera_error), true,
                        mActivity.getString(R.string.ok), "", null, null, false);
            }
        } catch (Exception e) {
            Show_Log.Logcat(TAG, "execption==" + e.getMessage(), 1);
        }
    }


    //    This method using for finding real path from image content uri
    public static String getRealPathFromURI(Uri contentURI, Context mContext) {
        String result;
        String[] proj = {MediaStore.Video.Media.DATA};

        Cursor cursor = mContext.getContentResolver().query(contentURI, proj, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static Bitmap decodeSampledBitmapFromFile(String filename,
                                                     int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        return BitmapFactory.decodeFile(filename, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            //long totalPixels = width * height / inSampleSize;

            // Anything more than 2x the requested pixels we'll sample down further
            //final long totalReqPixelsCap = reqWidth * reqHeight * 2;

            /*while (totalPixels > totalReqPixelsCap) {
                inSampleSize *= 2;
                totalPixels /= 2;
            }*/
        }
        return inSampleSize;
    }


    public static int getExifOrientation(String filepath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (exif != null) {
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                // We only recognize a subset of orientation tag values.
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            }
        }

        return degree;
    }

    public static File saveBitmap(String filePath, Bitmap bitmap) {
        OutputStream outStream = null;

        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
            file = new File(filePath);
        }

        try {
            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Log.e("file", "" + file);
        return file;
    }

    public static File setImage(Context context, String imagePath, ImageView imageView) {
        File targetFile = new File(imagePath);
        Uri photoURI = FileProvider.getUriForFile(context,
                context.getApplicationContext().getPackageName() + ".provider", targetFile);
        Picasso.with(context).load(photoURI).into(imageView);

        return targetFile;
    }

    public static Bitmap rotate(Bitmap b, int degrees) {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();
            if (degrees != 0) {
                m.postRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
            }

            try {
                Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
                // We have no memory to rotate. Return the original bitmap.
            }
        }
        return b;
    }


    public static int getDisplayRotation(Context context) {
        int rotation = ((Activity) context).getWindowManager().getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
        }
        return 0;
    }

    public static String getGalleryImagePath(Context context, Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
        if (cursor != null) {
            String picturePath = null;
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
            }
            cursor.close();
            return picturePath;
        }
        return null;
    }


    public static File getOutputFilePath(Context context, String dirName, String fileName) {
        File dir = new File(
                context.getExternalFilesDir(null), dirName);

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.d(TAG, "failed to create " + dirName + " directory");
                return null;
            }
        }

        File mediaFile = new File(dir, fileName);
        return mediaFile;
    }

    public static RequestBody createPartFromString(String value) {
        if (TextUtils.isEmpty(value))
            value = "";
        RequestBody requestBody = RequestBody.create(MultipartBody.FORM, value);
        return requestBody;
    }

    /**
     * log for debugging
     */
    public static void debug(int type, String TAG, String message) {
        switch (type) {
            case 0:
                Log.i(TAG, message);
                break;
            case 1:
                Log.d(TAG, message);
                break;
            case 2:
                Log.e(TAG, message);
                break;
            case 3:
                Log.v(TAG, message);
                break;
            case 4:
                Log.w(TAG, message);
                break;
            case 5:
                int maxLogSize = 1000;
                for(int i = 0; i <= message.length() / maxLogSize; i++) {
                    int start = i * maxLogSize;
                    int end = (i+1) * maxLogSize;
                    end = end > message.length() ? message.length() : end;
                    Log.d(TAG, message);
                }
                break;
            default:
                Log.d(TAG, message);
                break;
        }
    }
}
