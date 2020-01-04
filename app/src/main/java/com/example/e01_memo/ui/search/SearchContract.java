package com.example.e01_memo.ui.search;

import com.example.e01_memo.data.pojo.MemoItem;

import java.util.List;

public interface SearchContract {

    enum SearchButton {
        ListItem, DeleteModeClose, DeleteButton
    }

    interface SearchView {
        void setPresenter(SearchPresenter presenter);
        void showEmpty();
        void clearDeleteBar();
        void moveToEdit(MemoItem item);
        void displayMemoData(List<MemoItem> itemList);
        void showDeletedToast();
        List<MemoItem> getSelectMemoData();
        boolean isDeleteMode();
    }

    interface SearchPresenter {
        void onButton(SearchContract.SearchButton searchButton, MemoItem item);
        void onChangeFavorite(MemoItem item);
        void onSearchItem(String searchStr);
        void onSwipeDeleteMemo(MemoItem item);
        void onTextChanged();
        void onTapDeleteBar(SearchButton searchButton);
        void onDestroy();
    }
}
