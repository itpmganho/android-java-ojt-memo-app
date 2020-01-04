package com.example.e01_memo.ui.setting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.e01_memo.R;
import com.example.e01_memo.ui.aboutapp.AboutAppActivity;
import com.example.e01_memo.ui.main.MainActivity;
import com.example.e01_memo.ui.theme.ThemeActivity;
import com.example.e01_memo.util.SharedPreferencesUtil;

public class SettingActivity extends AppCompatActivity implements SettingFragment.SettingActionListener {

    private SettingPresenterImpl presenter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(SharedPreferencesUtil.getSelectStyleId(this));
        setContentView(R.layout.activity_setting);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferencesUtil.FontSetting fontSetting = SharedPreferencesUtil.getSelectFont(this);

        changeTextFont(fontSetting);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        SettingFragment fragment = SettingFragment.newInstance();

        presenter = new SettingPresenterImpl(fragment);

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

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        boolean result = true;

        switch (id) {
            case android.R.id.home:
                startActivity(MainActivity.createIntent(this));
                finish();
                break;
            default:
                result = super.onOptionsItemSelected(item);
                break;
        }
        return result;
    }

    @Override
    public void actionMoveToAboutApp() {
        startActivity(AboutAppActivity.createIntent(this));
    }

    @Override
    public void actionMoveToTheme() {
        startActivity(ThemeActivity.createIntent(this));
    }

    @Override
    public void actionChangeTextFont() {
        SharedPreferencesUtil.FontSetting fontSetting = SharedPreferencesUtil.getSelectFont(this);
        changeTextFont(fontSetting);
    }
}
