package com.example.kanika.gmfacelandmarkdetection.utility;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.ProgressBar;

/**
 * Created by kanika on 12/6/18.
 */
public class CustomProgressDialog extends Dialog {

    public CustomProgressDialog(Context context, String text) {
        super(context, android.R.style.Theme_Holo_Dialog_NoActionBar);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ProgressBar v = new ProgressBar(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(v);
        setCancelable(false);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }


    public CustomProgressDialog(Context context, String text,boolean isCancel) {
        super(context, android.R.style.Theme_Holo_Dialog_NoActionBar);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ProgressBar v = new ProgressBar(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(v);
        setCancelable(isCancel);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }
}
