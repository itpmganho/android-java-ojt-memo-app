package com.example.e01_memo.ui.setting;

public interface SettingContract {

    enum SettingButton {
        Font, MojiSize, Theme, AboutApp
    }

    interface SettingView {
        void setPresenter(SettingPresenter presenter);
        void showFontDialog();
        void showMojiSizeDialog();
        void moveToAboutApp();
        void moveToTheme();
    }

    interface SettingPresenter {
        void onDestroy();
        void onButton(SettingButton settingButton);
        void start();
    }
}
