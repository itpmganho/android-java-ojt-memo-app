package com.example.e01_memo.ui.aboutapp;

public interface AboutAppContract {

    enum AboutAppButton {
        Version, License, PrivacyPolicy
    }

    interface AboutAppView {
        void setPresenter(AboutAppPresenter presenter);
    }

    interface AboutAppPresenter {
        void onDestroy();
        void onButton(AboutAppButton aboutAppButton);
    }
}
