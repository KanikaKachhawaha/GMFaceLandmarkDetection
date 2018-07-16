package com.example.kanika.gmfacelandmarkdetection.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.example.kanika.gmfacelandmarkdetection.constants.FontsConstant;


public class GothamBookEditText extends EditText {
    public GothamBookEditText(Context context) {
        super(context);
        setFont();
    }

    public GothamBookEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }

    public GothamBookEditText(Context context, AttributeSet attrs,
                              int defStyle) {
        super(context, attrs, defStyle);
        setFont();
    }

    private void setFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(),
                FontsConstant.GOTHAM_BOOK);
        setTypeface(font, Typeface.NORMAL);

    }
}
