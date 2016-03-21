package com.rspn.cryptotool.uihelper;

import android.content.Context;
import android.graphics.Paint;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
/*http://learnandroid.blogspot.ca/2012/11/number-picker-or-text-picker.html*/

public class HorizontalNumberPicker extends LinearLayout {
    private static final int NUMBER = 0;
    private static final int TEXT = 1;
    private int style = 0;
    private TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private int min = 1;
    private int max = 100;
    private ArrayList<String> valueString = new ArrayList<>();
    private int value = 1;
    private Button incButton;
    private Button decButton;
    private TextView valueView;

    public HorizontalNumberPicker(Context context) {
        super(context);
        init();
    }

    public HorizontalNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HorizontalNumberPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {

        decButton = new Button(getContext());
        valueView = new TextView(getContext());
        incButton = new Button(getContext());
        this.addView(decButton, new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        this.addView(valueView, new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        this.addView(incButton, new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        valueView.setText(getValueString());
        valueView.setTextSize(16);
        valueView.setGravity(Gravity.CENTER);
        paint.setTextSize(valueView.getTextSize());
        reCalculateWidthAndCaption();

        decButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    touchSet(0);
                }
                return false;
            }
        });
        incButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    touchSet(1);
                }
                return false;
            }
        });
    }

    protected void touchSet(int i) {
        int p = i ^ getOrientation();
        switch (p) {
            case 0:
                value--;
                break;
            case 1:
                value++;
                break;
        }
        value = value < min ? min : value > max ? max : value;
        valueView.setText(getValueString());
        invalidate();
    }

    @Override
    public void setOrientation(int orientation) {
        super.setOrientation(orientation);
        reCalculateWidthAndCaption();
    }

    private String getValueString() {
        String ret = "";
        switch (style) {
            case NUMBER:
                ret = String.valueOf(value);
                break;
            case TEXT:
                try {
                    ret = valueString.get(value) == null ? "" : valueString.get(value);
                } catch (Exception e) {
                    ret = "";
                }
                break;
        }
        return ret;
    }

    public int getStyle() {
        return style;
    }

    private void reCalculateWidthAndCaption() {
        MarginLayoutParams lp = (MarginLayoutParams) valueView.getLayoutParams();
        lp.width = (int) StaticLayout.getDesiredWidth("IIII", paint);
        if (valueString.size() > 0) {
            String s = "";
            for (int i = 0; i < valueString.size(); i++) {
                s = s.length() > valueString.get(i).length() ? s : valueString.get(i);
            }
            int w = (int) StaticLayout.getDesiredWidth(s, paint);
            lp.width = w > lp.width ? w : lp.width;
        }
        switch (getOrientation()) {
            case LinearLayout.HORIZONTAL:
                decButton.setText("-");
                decButton.setLayoutParams(new LinearLayout.LayoutParams(180, 150));
                incButton.setText("+");
                incButton.setLayoutParams(new LinearLayout.LayoutParams(180, 150));
                lp.width += 5;
                lp.leftMargin = 5;
                lp.rightMargin = 5;
                break;
            case LinearLayout.VERTICAL:
                decButton.setText("+");
                incButton.setText("-");
                lp.height = 50;
                lp.topMargin = 5;
                lp.bottomMargin = 5;
                break;
        }
        valueView.setText(getValueString());
    }

    public void setStyle(int style) {
        this.style = style;
        reCalculateWidthAndCaption();
    }

    public void setMax(int max) {
        this.max = max;
        value = value > max ? max : value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        valueView.setText(getValueString());
    }
}