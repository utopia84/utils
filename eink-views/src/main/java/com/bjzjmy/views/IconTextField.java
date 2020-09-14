package com.bjzjmy.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

public class IconTextField extends AppCompatEditText {
    private static final int EXTRA_AREA = 30;//额外点击范围

    private Drawable delDrawalbe;//清空图标
    private Drawable rightDrawalbe;//右侧图标
    private boolean clearIconEnable = false;
    private OnDrawableRightListener mRightListener;

    public interface OnDrawableRightListener {
        void onClick(View view);
    }

    public void setDrawableRightListener(OnDrawableRightListener listener) {
        this.mRightListener = listener;
    }

    public IconTextField(@NonNull Context context) {
        this(context, null);
    }

    public IconTextField(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconTextField(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        int rightDrawableResourceId = -1;
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.IconTextField);
            rightDrawableResourceId = array.getResourceId(R.styleable.IconTextField_rightIcon, rightDrawableResourceId);
            clearIconEnable = array.getBoolean(R.styleable.IconTextField_clearIconEnable, clearIconEnable);
            array.recycle();
        }

        if (clearIconEnable) {//需要显示清空按钮
            delDrawalbe = ContextCompat.getDrawable(context, R.mipmap.edittext_del_icon);
            delDrawalbe.setBounds(0, 0, 60, 60);
        }

        if (rightDrawableResourceId != -1) {//需要显示右侧图标
            rightDrawalbe = ContextCompat.getDrawable(context, rightDrawableResourceId);
            rightDrawalbe.setBounds(0, 0, 60, 60);
        }
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private void init() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    setCompoundDrawables(null, null, delDrawalbe, null);
                } else {
                    setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
            }
        });

        setOnFocusChangeListener((View view, boolean b) -> {
            if (b && getText().toString().length() > 0) {
                setCompoundDrawables(null, null, delDrawalbe, null);
            } else {
                setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            }
        });

        setLongClickable(false);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (clearIconEnable) {
            Rect bounds = getCompoundDrawables()[2].getBounds();
            int x = (int) event.getX();
            int rectX = getWidth() - bounds.width() - EXTRA_AREA - getPaddingRight();
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (x > rectX) {
                    clearText();
                    return true;
                }
            }

        }
        return super.onTouchEvent(event);
    }

    private void clearText() {
        setText("");
        //获取输入焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        findFocus();
    }
}
