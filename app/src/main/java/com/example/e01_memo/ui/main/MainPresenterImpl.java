package com.example.e01_memo.ui.main;

import android.app.Application;
import android.util.Log;
import android.view.View;

import com.example.e01_memo.MyApplication;
import com.example.e01_memo.R;
import com.example.e01_memo.data.MemoSQLiteOpenHelper;
import com.example.e01_memo.data.pojo.MemoItem;
import com.example.e01_memo.usecase.MemoDataInteractor;
import com.example.e01_memo.util.Constant;
import com.example.e01_memo.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

public class MainPresenterImpl implements MainContract.MainPresenter {

    private MainContract.MainView mainView;
    private MemoDataInteractor memoDataInteractor;

    public MainPresenterImpl(MainContract.MainView view, MemoDataInteractor interactor) {
        this.mainView = view;
        this.memoDataInteractor = interactor;

        mainView.setPresenter(this);
    }


    @Override
    public void onDestroy() {
        if (mainView != null) mainView = null;
    }

    @Override
    public void onButton(MainContract.MainButton mainButton, MemoItem item) {
        if (mainView == null) return;
        switch (mainButton) {
            case EditButton:
            case ListItem:
                mainView.moveToEdit(item);
                break;
            case DeleteItem:
                mainView.showDeleteMemoDetail(item);
                break;
            default:
        }
    }

    @Override
    public void onTapNavItem(MainContract.MainButton mainButton) {
        if (mainView == null) return;
        switch (mainButton) {
            case NavAllMemo:
                // すべてのメモ
                if (MyApplication.selectNavigationItem == Constant.NavigationItem.ALL_MEMO) return;
                MyApplication.selectNavigationItem = Constant.NavigationItem.ALL_MEMO;
                mainView.selectNavigationItem(Constant.NavigationItem.ALL_MEMO);
                break;
            case NavFavorite:
                // お気に入り
                if (MyApplication.selectNavigationItem == Constant.NavigationItem.FAVORITE) return;
                MyApplication.selectNavigationItem = Constant.NavigationItem.FAVORITE;
                mainView.selectNavigationItem(Constant.NavigationItem.FAVORITE);
                break;
            case NavGarbageBox:
                // ゴミ箱
                if (MyApplication.selectNavigationItem == Constant.NavigationItem.DELETE) return;
                MyApplication.selectNavigationItem = Constant.NavigationItem.DELETE;
                mainView.selectNavigationItem(Constant.NavigationItem.DELETE);
                break;
            default:
                return;
        }
        mainView.openDrawer(false);
    }

    @Override
    public void onTapDeleteBar(final MainContract.MainButton mainButton) {
        if (mainView == null) return;
        switch (mainButton) {
            case DeleteModeClose:
                mainView.clearDeleteBar();
                break;
            case DeleteButton:
                deleteMemoData(mainView.getSelectMemoData(), new MemoDataInteractor.OnDeleteMemoDataListener() {
                    @Override
                    public void onDeleteMemoDataFinished() {
                        if (mainView != null) {
                            mainView.clearDeleteBar();
                            mainView.showDeletedToast();
                            start();
                        }
                    }
                });
                break;
            case RestoreButton:
                restoreMemoData(mainView.getSelectMemoData(), new MemoDataInteractor.OnRestoreMemoDataListener() {
                    @Override
                    public void onRestoreMemoDataFinished() {
                        if (mainView != null) {
                            mainView.clearDeleteBar();
                            mainView.showRestoreToast();
                            start();
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public void onChangeFavorite(MemoItem item) {
        if (memoDataInteractor == null) return;
        memoDataInteractor.updateMemoData(item, new MemoDataInteractor.OnUpdateMemoDataListener() {
            @Override
            public void onUpdateMemoDataFinished() {
                SharedPreferencesUtil.SortSetting sortType = SharedPreferencesUtil.SortSetting.SortOld;
                if (mainView != null) sortType = mainView.getSelectSort();
                if (memoDataInteractor != null) memoDataInteractor.getAllMemoData(sortType, new MemoDataInteractor.OnAllMemoDataLoadListener() {
                    @Override
                    public void onAllMemoDataLoaded(List<MemoItem> itemList) {
                        if (mainView != null) mainView.displayMemoData(itemList);
                    }
                });
            }
        });
    }

    @Override
    public void onOpenDrawer() {
        if (mainView != null) mainView.clearDeleteBar();
    }

    @Override
    public boolean isDeleteMode() {
        if (mainView != null) {
            return mainView.isDeleteMode();
        }
        return false;
    }

    @Override
    public void onBackDeleteMode() {
        if (mainView != null) mainView.clearDeleteBar();
    }

    @Override
    public void onDeleteMemo(MemoItem item) {
        if (memoDataInteractor != null) memoDataInteractor.removeMemoData(item, new MemoDataInteractor.OnRemoveMemoDataListener() {
            @Override
            public void onRemoveMemoDataFinished() {
                start();
            }
        });
    }

    @Override
    public void onSwipeDeleteMemo(MemoItem item) {
        List<MemoItem> memoItems = new ArrayList<>();
        item.setDeleteFlag(true);
        memoItems.add(item);
        deleteMemoData(memoItems, new MemoDataInteractor.OnDeleteMemoDataListener() {
            @Override
            public void onDeleteMemoDataFinished() {
                if (mainView != null) {
                    mainView.clearDeleteBar();
                    start();
                }
            }
        });
    }

    @Override
    public void onSwipeRestoreMemo(MemoItem item) {
        List<MemoItem> memoItems = new ArrayList<>();
        item.setDeleteFlag(false);
        memoItems.add(item);
        restoreMemoData(memoItems, new MemoDataInteractor.OnRestoreMemoDataListener() {
            @Override
            public void onRestoreMemoDataFinished() {
                if (mainView != null) {
                    mainView.clearDeleteBar();
                    start();
                }
            }
        });
    }

    @Override
    public void garbageRemoveAll() {
        if (memoDataInteractor != null) memoDataInteractor.removeAllMemoData(new MemoDataInteractor.OnRemoveAllMemoDataListener() {
            @Override
            public void onRemoveAllMemoDataFinished() {
                start();
            }
        });
    }

    @Override
    public void start() {
        SharedPreferencesUtil.SortSetting sortType = SharedPreferencesUtil.SortSetting.SortOld;
        if (mainView != null) sortType = mainView.getSelectSort();
        if (memoDataInteractor != null) memoDataInteractor.getAllMemoData(sortType, new MemoDataInteractor.OnAllMemoDataLoadListener() {
            @Override
            public void onAllMemoDataLoaded(List<MemoItem> itemList) {
                if (mainView != null) {
                    if (itemList.size() != 0) {
                        mainView.displayMemoData(itemList);
                    } else {
                        mainView.showEmpty();
                    }
                }
            }
        });
    }

    private void deleteMemoData(List<MemoItem> memoItem, MemoDataInteractor.OnDeleteMemoDataListener listener) {
        if (memoDataInteractor != null) memoDataInteractor.deleteMemoData(memoItem, listener);
    }

    private void restoreMemoData(List<MemoItem> memoItems, MemoDataInteractor.OnRestoreMemoDataListener listener) {
        if (memoDataInteractor != null) memoDataInteractor.restoreMemoData(memoItems, listener);
    }
}
