package com.example.e01_memo.ui.edit;

public interface EditContract {

    enum PopupButton {
        CopyButton, RestoreButton, SaveAsImgButton
    }

    enum FinishStatus {
        OK, CANCEL
    }

    interface EditView {
        void setPresenter(EditPresenter presenter);
        void hideSoftKeyboard();
        int getMemoId();
        String readMemoText();
        void finishPage(FinishStatus status);
        boolean getFavorite();
        void shareText();
        void showMenu();
        void restoreMemo(String memoStr);
        void copyToClipboard();
        void saveAsImage();
    }

    interface EditPresenter {
        void onDestroy();
        void onUpdateMemo();
        void onTapShare();
        void onTapMenu();
        void onTapMenuItem(PopupButton popupButton);
        String getMemoStr();
    }
}
