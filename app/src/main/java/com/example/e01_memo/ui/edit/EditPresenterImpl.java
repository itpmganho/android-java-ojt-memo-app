package com.example.e01_memo.ui.edit;

import android.util.Log;

import com.example.e01_memo.data.pojo.MemoItem;
import com.example.e01_memo.usecase.MemoDataInteractor;

import java.util.Date;

public class EditPresenterImpl implements EditContract.EditPresenter {

    private EditContract.EditView editView;
    private MemoDataInteractor interactor;
    private String memoStr = "";
    private boolean favoriteFlag;

    public EditPresenterImpl(EditContract.EditView view, String memoStr, boolean favorite, MemoDataInteractor interactor) {
        this.editView = view;
        this.interactor = interactor;
        this.memoStr = memoStr;
        this.favoriteFlag = favorite;

        editView.setPresenter(this);
    }

    @Override
    public void onDestroy() {
        if (editView != null) editView = null;
    }

    @Override
    public void onUpdateMemo() {
        if (editView != null && interactor != null) {
            int id = editView.getMemoId();
            String memoText = editView.readMemoText();
            boolean favorite = editView.getFavorite();

            MemoItem item = new MemoItem();
            item.setId(id);
            item.setMemo(memoText);
            item.setFavorite(favorite);
            if (id == -1) {
                // 新規作成
                // メモが変更されていない場合は保存せずに終了
                if (this.memoStr.equals(memoText)) {
                    editView.finishPage(EditContract.FinishStatus.CANCEL);
                    return;
                }
                interactor.insertMemoData(item, new MemoDataInteractor.OnInsertMemoDataListener() {
                    @Override
                    public void onInsertMemoDataFinished() {
                        if (editView != null) editView.finishPage(EditContract.FinishStatus.OK);
                    }
                });
            } else {
                // 編集
                // メモが変更されていない場合は保存せずに終了
                int favoriteNum1 = favoriteFlag ? 1 : 0;
                int favoriteNum2 = favorite ? 1 : 0;
                if (this.memoStr.equals(memoText)) {
                    if (favoriteNum1 == favoriteNum2) {
                        editView.finishPage(EditContract.FinishStatus.CANCEL);
                    } else {
                        interactor.updateMemoData(item, new MemoDataInteractor.OnUpdateMemoDataListener() {
                            @Override
                            public void onUpdateMemoDataFinished() {
                                if (editView != null) editView.finishPage(EditContract.FinishStatus.CANCEL);
                            }
                        });
                    }
                    return;
                }
                item.setCreateDate(new Date(System.currentTimeMillis()));
                interactor.updateMemoData(item, new MemoDataInteractor.OnUpdateMemoDataListener() {
                    @Override
                    public void onUpdateMemoDataFinished() {
                        if (editView != null) editView.finishPage(EditContract.FinishStatus.OK);
                    }
                });
            }
        }
    }

    @Override
    public void onTapShare() {
        if (editView != null) editView.shareText();
    }

    @Override
    public void onTapMenu() {
        if (editView != null) editView.showMenu();
    }

    @Override
    public void onTapMenuItem(EditContract.PopupButton popupButton) {
        if (editView == null) return;
        switch (popupButton) {
            case CopyButton:
                editView.copyToClipboard();
                break;
            case RestoreButton:
                editView.restoreMemo(memoStr);
                break;
            case SaveAsImgButton:
                editView.saveAsImage();
                break;
            default:
        }
    }

    @Override
    public String getMemoStr() {
        return this.memoStr;
    }
}
