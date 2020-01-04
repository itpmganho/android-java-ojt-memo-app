package com.example.e01_memo.ui.setting;

public class SettingPresenterImpl implements SettingContract.SettingPresenter {

    private SettingContract.SettingView settingView;

    public SettingPresenterImpl(SettingContract.SettingView view) {
        this.settingView = view;

        settingView.setPresenter(this);
    }

    @Override
    public void onDestroy() {
        if (settingView != null) settingView = null;
    }

    @Override
    public void onButton(SettingContract.SettingButton settingButton) {
        switch (settingButton) {
            case Font:
                // フォント設定ダイアログ表示
                if (settingView != null) settingView.showFontDialog();
                break;
            case MojiSize:
                // 文字サイズ設定ダイアログ表示
                if (settingView != null) settingView.showMojiSizeDialog();
                break;
            case Theme:
                // テーマ変更画面へ遷移
                if (settingView != null) settingView.moveToTheme();
                break;
            case AboutApp:
                // アプリについて画面へ遷移
                if (settingView != null) settingView.moveToAboutApp();
                break;
        }
    }

    @Override
    public void start() {
        // nothing to do
    }
}
