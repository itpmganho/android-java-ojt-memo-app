package com.example.e01_memo.ui;

import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.widget.TextView;

import com.example.e01_memo.util.SharedPreferencesUtil;

public abstract class BaseFragment extends Fragment {

    protected void initTextFont(SharedPreferencesUtil.FontSetting fontSetting, TextView... textViews) {
        Typeface typeface = ResourcesCompat.getFont(getActivity(), fontSetting.getFontTypeResId());
        for (TextView textView : textViews) {
            textView.setTypeface(typeface);
        }
    }

}
