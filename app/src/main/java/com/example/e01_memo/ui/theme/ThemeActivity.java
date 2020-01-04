package com.example.e01_memo.ui.theme;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.e01_memo.R;
import com.example.e01_memo.ui.setting.SettingActivity;
import com.example.e01_memo.util.SharedPreferencesUtil;

public class ThemeActivity extends AppCompatActivity implements ThemeFragment.ThemeActionListener {

    private ThemePresenterImpl presenter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferencesUtil.ThemeSetting themeSetting = SharedPreferencesUtil.getSelectTheme(this);
        setTheme(SharedPreferencesUtil.getSelectStyleId(this));
        setContentView(R.layout.activity_theme);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        ThemeFragment fragment = ThemeFragment.newInstance(themeSetting.ordinal());

        presenter = new ThemePresenterImpl(fragment);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainContainer, fragment)
                .commit();
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
                startActivity(SettingActivity.createIntent(this));
                finish();
                break;
            default:
                result = super.onOptionsItemSelected(item);
                break;
        }
        return result;
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, ThemeActivity.class);
        return intent;
    }

    @Override
    public void actionChangeTheme(View rootView, SharedPreferencesUtil.ThemeSetting themeSetting) {
        getWindow().setStatusBarColor(ContextCompat.getColor(this, themeSetting.getStatusColorResId()));
        toolbar.setBackgroundColor(ContextCompat.getColor(this, themeSetting.getHeaderColorResId()));
        rootView.setBackgroundColor(ContextCompat.getColor(this, themeSetting.getBackgroundColorResId()));
    }
}

