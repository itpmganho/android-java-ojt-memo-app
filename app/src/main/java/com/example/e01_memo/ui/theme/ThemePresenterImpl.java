package com.example.e01_memo.ui.theme;

public class ThemePresenterImpl implements ThemeContract.ThemePresenter {

    private ThemeContract.ThemeView themeView;

    public ThemePresenterImpl(ThemeContract.ThemeView view) {
        this.themeView = view;

        themeView.setPresenter(this);
    }

    @Override
    public void onDestroy() {
        if (themeView != null) themeView = null;
    }
}
