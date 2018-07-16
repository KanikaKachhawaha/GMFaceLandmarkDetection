package com.example.kanika.gmfacelandmarkdetection.utility;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.kanika.gmfacelandmarkdetection.R;

/**
 * Created by kanika on 8/6/18.
 */
public class AlertPopup {
    /*Context ---as a context of activity or fragment
     *message-----display message you want to show
     * is_cancelable----if you want to hide the aleart outside click pass true else pass false
     *OkTag------Ok Button Title
     * CancelTag---Cancel Button Title
     * Cancel_onclick --Custom Click list if you want to perform somthing on button press else pass null
     * OK_onclick --Custom Click list if you want to perform somthing on ok button press else pass null
     *ShowCancel---boolean for showing ok cancel or only ok button pass true for both else pass false to show only ok button
     */


    public static void showAlert_with_OK_cacel(final Context mcontext,
                                               String message,
                                               boolean is_Cancelable,
                                               String OKTag,
                                               String CancelTag,
                                               DialogInterface.OnClickListener OK_onclick,
                                               DialogInterface.OnClickListener Cancel_onclick,
                                               boolean ShowCancel) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(mcontext);
        Show_Log.Logcat("alert", "msg " + message, 1);
        builder1.setMessage(message);
        builder1.setCancelable(is_Cancelable);


        if (OK_onclick == null) {
            builder1.setPositiveButton(
                    OKTag,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

        } else {
            builder1.setPositiveButton(
                    OKTag, OK_onclick);
        }


        if (ShowCancel == true) {

            if (Cancel_onclick == null) {
                builder1.setNegativeButton(
                        CancelTag,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

            } else {
                builder1.setNegativeButton(
                        CancelTag, Cancel_onclick);
            }

        }

        final AlertDialog alert11 = builder1.create();
//        alert11.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface dialogInterface) {
//                alert11.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(mcontext.getResources().getColor(R.color.alertButtonTextColor));
//            }
//        });
//        alert11.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface dialogInterface) {
//                alert11.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(mcontext.getResources().getColor(R.color.alertButtonTextColor));
//            }
//        });
        alert11.show();

    }

    public static void showAlert_with_OK_cancel(final Context mcontext,
                                               String title,
                                               String message,
                                               boolean is_Cancelable,
                                               String OKTag,
                                               String CancelTag,
                                               DialogInterface.OnClickListener OK_onclick,
                                               DialogInterface.OnClickListener Cancel_onclick,
                                               boolean ShowCancel) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(mcontext);
        if (title != null && title.toString().trim().length() > 0)
            builder1.setTitle(title);
        if (message != null && message.toString().trim().length() > 0)
            builder1.setMessage(message);
        builder1.setCancelable(is_Cancelable);

        if (ShowCancel == true) {

            if (Cancel_onclick == null) {
                builder1.setNegativeButton(
                        CancelTag,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

            } else {
                builder1.setNegativeButton(
                        CancelTag, Cancel_onclick);
            }

        }
        if (OK_onclick == null) {

            builder1.setPositiveButton(
                    OKTag,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

        } else {
            builder1.setPositiveButton(
                    OKTag, OK_onclick);
        }


        final AlertDialog alert11 = builder1.create();

        alert11.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alert11.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(mcontext.getResources().getColor(R.color.alertButtonTextColor));
            }
        });
        alert11.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alert11.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(mcontext.getResources().getColor(R.color.alertButtonTextColor));
            }
        });
        alert11.show();

    }

    public static void showAlert_with_Three_Options(final Context mcontext,
                                                    String title, String message,
                                                    boolean is_Cancelable, String firstText, String cancelText,
                                                    String okText, DialogInterface.OnClickListener fisrtOnclick,
                                                    DialogInterface.OnClickListener cancelOnclick, DialogInterface.OnClickListener okonclick, boolean ShowCancel) {

        final AlertDialog builder1 = new AlertDialog.Builder(mcontext).create();
        if (title != null && title.toString().trim().length() > 0)
            builder1.setTitle(title);
        if (message != null && message.toString().trim().length() > 0)
            builder1.setMessage(message);
        builder1.setCancelable(is_Cancelable);

        if (okonclick == null) {


            builder1.setButton(AlertDialog.BUTTON_NEGATIVE, okText, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();


                }
            });

        } else {
            builder1.setButton(AlertDialog.BUTTON_NEGATIVE, okText, okonclick);

        }


        if (ShowCancel == true) {

            if (cancelOnclick == null) {
                builder1.setButton(AlertDialog.BUTTON_POSITIVE,
                        cancelText,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

            } else {
                builder1.setButton(AlertDialog.BUTTON_POSITIVE,
                        cancelText, cancelOnclick);
            }

        }
        if (fisrtOnclick == null) {


            builder1.setButton(AlertDialog.BUTTON_NEUTRAL, firstText, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();


                }
            });

        } else {
            builder1.setButton(AlertDialog.BUTTON_NEUTRAL, firstText, fisrtOnclick);
//            builder1.getButton(AlertDialog.BUTTON_NEUTRAL).setGravity(Gravity.CENTER_HORIZONTAL);
        }
//        final Button negButton = builder1.getButton(AlertDialog.BUTTON_NEGATIVE);
//        LinearLayout.LayoutParams negButtonLL = (LinearLayout.LayoutParams) negButton.getLayoutParams();
//        negButtonLL.gravity = Gravity.CENTER;
//        negButton.setLayoutParams(negButtonLL);

        builder1.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                builder1.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(mcontext.getResources().getColor(R.color.alertButtonTextColor));
                builder1.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(mcontext.getResources().getColor(R.color.alertButtonTextColor));
                builder1.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(mcontext.getResources().getColor(R.color.alertButtonTextColor));
                final Button positiveButton = builder1.getButton(AlertDialog.BUTTON_POSITIVE);
                if (positiveButton!=null){
                    LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
                    positiveButtonLL.gravity = Gravity.CENTER_HORIZONTAL;
                    positiveButton.setLayoutParams(positiveButtonLL);}
            }
        });

        builder1.show();
//        final Button positiveButton = builder1.getButton(AlertDialog.BUTTON_POSITIVE);
//        if (positiveButton!=null){
//            LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
//            positiveButtonLL.gravity = Gravity.CENTER_HORIZONTAL;
//            positiveButton.setLayoutParams(positiveButtonLL);}

    }
}
