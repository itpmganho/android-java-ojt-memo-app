package com.example.e01_memo.ui.aboutapp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.e01_memo.BuildConfig;
import com.example.e01_memo.R;
import com.example.e01_memo.ui.BaseFragment;
import com.example.e01_memo.util.SharedPreferencesUtil;

public class AboutAppFragment extends BaseFragment implements AboutAppContract.AboutAppView, View.OnClickListener {

    private AboutAppContract.AboutAppPresenter presenter;
    private LinearLayout version;
    private TextView versionTitleText;
    private TextView versionNameText;
    private TextView license;
    private TextView privacyPolicy;

    public AboutAppFragment() {}

    public static AboutAppFragment newInstance() {
        return new AboutAppFragment();
     }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about_app, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        versionTitleText = view.findViewById(R.id.versionTitleText);
        version = view.findViewById(R.id.version);
        versionNameText = view.findViewById(R.id.versionNameText);
        license = view.findViewById(R.id.license);
        privacyPolicy = view.findViewById(R.id.privacyPolicy);
        versionNameText.setText(BuildConfig.VERSION_NAME);

        initTextFont(SharedPreferencesUtil.getSelectFont(getActivity()),
                versionTitleText,
                versionNameText,
                license,
                privacyPolicy
        );

        version.setOnClickListener(this);
        license.setOnClickListener(this);
        privacyPolicy.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        if (presenter != null) presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void setPresenter(AboutAppContract.AboutAppPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onClick(View view) {
        if (presenter != null) return;
        if (view.getId() == R.id.version) {
            presenter.onButton(AboutAppContract.AboutAppButton.Version);
        }
        else if (view.getId() == R.id.license) {
            presenter.onButton(AboutAppContract.AboutAppButton.License);
        }
        else if (view.getId() == R.id.privacyPolicy) {
            presenter.onButton(AboutAppContract.AboutAppButton.PrivacyPolicy);
        }
    }
}
