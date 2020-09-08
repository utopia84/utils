package com.zhy.autolayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class AutoLayoutActivity extends AppCompatActivity {

    @Override
    public View onCreateView(String name, @NonNull Context context,@NonNull AttributeSet attrs)
    {
        View view = null;
        switch (name) {
            case "FrameLayout":
                view = new AutoFrameLayout(context, attrs);
                break;
            case "LinearLayout":
                view = new AutoLinearLayout(context, attrs);
                break;
            case "RelativeLayout":
                view = new AutoRelativeLayout(context, attrs);
                break;
        }

        if (view != null) return view;

        return super.onCreateView(name, context, attrs);
    }




}
