package com.example.e01_memo.ui.aboutapp;

public class AboutAppPresenterImpl implements AboutAppContract.AboutAppPresenter {

    private AboutAppContract.AboutAppView aboutAppView;

    public AboutAppPresenterImpl(AboutAppContract.AboutAppView view) {
        this.aboutAppView = view;

        aboutAppView.setPresenter(this);
    }

    @Override
    public void onDestroy() {
        if (aboutAppView != null) aboutAppView = null;
    }

    @Override
    public void onButton(AboutAppContract.AboutAppButton aboutAppButton) {
        // TODO:
        switch (aboutAppButton) {
            case Version:
                // TODO:
                break;
            case License:
                // TODO:
                break;
            case PrivacyPolicy:
                // TODO:
                break;
        }
    }
}
