package com.example.e01_memo.ui.aboutapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.e01_memo.R;
import com.example.e01_memo.util.SharedPreferencesUtil;

public class AboutAppActivity extends AppCompatActivity {

    private AboutAppPresenterImpl presenter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(SharedPreferencesUtil.getSelectStyleId(this));
        setContentView(R.layout.activity_about_app);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        changeTextFont(SharedPreferencesUtil.getSelectFont(this));

        getSupportActionBar().setTitle(getString(R.string.aboutApp));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        AboutAppFragment fragment = AboutAppFragment.newInstance();

        presenter = new AboutAppPresenterImpl(fragment);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainContainer, fragment)
                .commit();
    }

    private void changeTextFont(SharedPreferencesUtil.FontSetting fontSetting) {
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                if (tv.getText().equals(toolbar.getTitle())) {
                    Typeface typeface = ResourcesCompat.getFont(this, fontSetting.getFontTypeResId());
                    tv.setTypeface(typeface);
                    break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (presenter != null) presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        boolean result = true;

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                result = super.onOptionsItemSelected(item);
                break;
        }
        return result;
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, AboutAppActivity.class);
        return intent;
    }
}
