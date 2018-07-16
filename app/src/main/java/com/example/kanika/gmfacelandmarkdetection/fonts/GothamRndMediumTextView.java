package com.example.kanika.gmfacelandmarkdetection.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.kanika.gmfacelandmarkdetection.constants.FontsConstant;

public class GothamRndMediumTextView extends TextView {
    public GothamRndMediumTextView(Context context) {
        super(context);
        setFont();
    }

    public GothamRndMediumTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }

    public GothamRndMediumTextView(Context context, AttributeSet attrs,
                                   int defStyle) {
        super(context, attrs, defStyle);
        setFont();
    }

    private void setFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(),
                FontsConstant.GOTHAM_RND_MEDIUM);
        setTypeface(font, Typeface.NORMAL);

    }
}
