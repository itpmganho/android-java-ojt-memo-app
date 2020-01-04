package com.example.e01_memo.ui.main;

import android.view.View;

import com.example.e01_memo.data.pojo.MemoItem;
import com.example.e01_memo.util.SharedPreferencesUtil;

import java.util.List;

public interface MainContract {

    enum MainButton {
        NavAllMemo, NavFavorite, NavGarbageBox,
        EditButton, ListItem, DeleteItem,
        DeleteModeClose, DeleteButton, RestoreButton
    }

    interface MainView {
        void setPresenter(MainPresenter presenter);
        void openDrawer(boolean open);
        void selectNavigationItem(int navigationItemNum);
        void displayMemoData(List<MemoItem> itemList);
        void moveToEdit(MemoItem item);
        void clearDeleteBar();
        void showEmpty();
        boolean isDeleteMode();
        void showDeletedToast();
        void showRestoreToast();
        void showDeleteMemoDetail(MemoItem item);
        SharedPreferencesUtil.SortSetting getSelectSort();
        List<MemoItem> getSelectMemoData();
    }

    interface MainPresenter {
        void onDestroy();
        void onButton(MainButton mainButton, MemoItem item);
        void onTapNavItem(MainButton mainButton);
        void onTapDeleteBar(MainButton mainButton);
        void onChangeFavorite(MemoItem item);
        void onOpenDrawer();
        boolean isDeleteMode();
        void onBackDeleteMode();
        void onDeleteMemo(MemoItem item);
        void onSwipeDeleteMemo(MemoItem item);
        void onSwipeRestoreMemo(MemoItem item);
        void garbageRemoveAll();
        void start();
    }
}
