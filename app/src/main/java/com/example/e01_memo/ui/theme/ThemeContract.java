package com.example.e01_memo.ui.theme;

public interface ThemeContract {

    interface ThemeView {
        void setPresenter(ThemePresenter presenter);
    }

    interface ThemePresenter {
        void onDestroy();
    }
}
