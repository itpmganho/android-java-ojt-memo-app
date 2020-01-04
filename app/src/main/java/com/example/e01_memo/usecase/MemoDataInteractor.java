package com.example.e01_memo.usecase;

import android.database.sqlite.SQLiteDatabase;

import com.example.e01_memo.data.MemoSQLiteOpenHelper;
import com.example.e01_memo.data.pojo.MemoItem;
import com.example.e01_memo.util.SharedPreferencesUtil;

import java.util.List;

public interface MemoDataInteractor {

    void getAllMemoData(SharedPreferencesUtil.SortSetting sort, OnAllMemoDataLoadListener listener);
    interface OnAllMemoDataLoadListener {
        void onAllMemoDataLoaded(List<MemoItem> itemList);
    }

    void insertMemoData(MemoItem item, OnInsertMemoDataListener listener);
    interface OnInsertMemoDataListener {
        void onInsertMemoDataFinished();
    }

    void updateMemoData(MemoItem item, OnUpdateMemoDataListener listener);
    interface OnUpdateMemoDataListener {
        void onUpdateMemoDataFinished();
    }

    void deleteMemoData(List<MemoItem> items, OnDeleteMemoDataListener listener);
    interface OnDeleteMemoDataListener {
        void onDeleteMemoDataFinished();
    }

    void removeMemoData(MemoItem item, OnRemoveMemoDataListener listener);
    interface OnRemoveMemoDataListener {
        void onRemoveMemoDataFinished();
    }

    void removeAllMemoData(OnRemoveAllMemoDataListener listener);
    interface OnRemoveAllMemoDataListener {
        void onRemoveAllMemoDataFinished();
    }

    void restoreMemoData(List<MemoItem> items, OnRestoreMemoDataListener listener);
    interface OnRestoreMemoDataListener {
        void onRestoreMemoDataFinished();
    }

    void getMemoData(SharedPreferencesUtil.SortSetting sort, String keyWord, OnMemoDataLoadListener listener);
    interface OnMemoDataLoadListener {
        void onMemoDataLoaded(List<MemoItem> itemList);
    }
}
