package com.example.kanika.gmfacelandmarkdetection.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.kanika.gmfacelandmarkdetection.constants.FontsConstant;

public class GothamLightTextView extends TextView {
    public GothamLightTextView(Context context) {
        super(context);
        setFont();
    }

    public GothamLightTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }

    public GothamLightTextView(Context context, AttributeSet attrs,
                               int defStyle) {
        super(context, attrs, defStyle);
        setFont();
    }

    private void setFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(),
                FontsConstant.GOTHAM_LIGHT);
        setTypeface(font, Typeface.NORMAL);

    }
}
