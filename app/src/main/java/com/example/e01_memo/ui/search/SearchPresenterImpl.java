package com.example.e01_memo.ui.search;

import com.example.e01_memo.data.pojo.MemoItem;
import com.example.e01_memo.usecase.MemoDataInteractor;
import com.example.e01_memo.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenterImpl implements SearchContract.SearchPresenter {

    private SearchContract.SearchView searchView;
    private MemoDataInteractor memoDataInteractor;
    private String keyWord = "";

    public SearchPresenterImpl(SearchContract.SearchView view, MemoDataInteractor interactor) {
        this.searchView = view;
        this.memoDataInteractor = interactor;

        searchView.setPresenter(this);
    }

    @Override
    public void onButton(SearchContract.SearchButton searchButton, MemoItem item) {
        if (searchView == null) return;
        switch (searchButton) {
            case ListItem:
                searchView.moveToEdit(item);
                break;
            default:
        }
    }

    @Override
    public void onChangeFavorite(MemoItem item) {
        if (memoDataInteractor == null) return;
        memoDataInteractor.updateMemoData(item, new MemoDataInteractor.OnUpdateMemoDataListener() {
            @Override
            public void onUpdateMemoDataFinished() {
                SharedPreferencesUtil.SortSetting sortType = SharedPreferencesUtil.SortSetting.SortNew;
                if (memoDataInteractor != null) memoDataInteractor.getMemoData(sortType, keyWord, new MemoDataInteractor.OnMemoDataLoadListener() {
                    @Override
                    public void onMemoDataLoaded(List<MemoItem> itemList) {
                        if (searchView != null) searchView.displayMemoData(itemList);
                    }
                });
            }
        });
    }

    @Override
    public void onSearchItem(String searchStr) {
        SharedPreferencesUtil.SortSetting sortType = SharedPreferencesUtil.SortSetting.SortNew;
        if (memoDataInteractor != null) {
            keyWord = searchStr;
            memoDataInteractor.getMemoData(sortType, searchStr, new MemoDataInteractor.OnMemoDataLoadListener() {

                @Override
                public void onMemoDataLoaded(List<MemoItem> itemList) {
                    if (searchView != null) {
                        if (itemList.size() != 0) {
                            searchView.displayMemoData(itemList);
                        } else {
                            searchView.showEmpty();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onSwipeDeleteMemo(MemoItem item) {
        List<MemoItem> memoItems = new ArrayList<>();
        item.setDeleteFlag(true);
        memoItems.add(item);
        deleteMemoData(memoItems, new MemoDataInteractor.OnDeleteMemoDataListener() {
            @Override
            public void onDeleteMemoDataFinished() {
                if (searchView != null) {
                    searchView.clearDeleteBar();
                    onSearchItem(keyWord);
                }
            }
        });
    }

    @Override
    public void onTextChanged() {
        if (searchView != null) searchView.showEmpty();
    }

    @Override
    public void onTapDeleteBar(SearchContract.SearchButton searchButton) {
        if (searchView == null) return;
        switch (searchButton) {
            case DeleteModeClose:
                searchView.clearDeleteBar();
                break;
            case DeleteButton:
                deleteMemoData(searchView.getSelectMemoData(), new MemoDataInteractor.OnDeleteMemoDataListener() {
                    @Override
                    public void onDeleteMemoDataFinished() {
                        if (searchView != null) {
                            searchView.clearDeleteBar();
                            searchView.showDeletedToast();
                            onSearchItem(keyWord);
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        if (searchView != null) searchView = null;
    }

    private void deleteMemoData(List<MemoItem> memoItems, MemoDataInteractor.OnDeleteMemoDataListener listener) {
        if (memoDataInteractor != null) memoDataInteractor.deleteMemoData(memoItems, listener);
    }
}
