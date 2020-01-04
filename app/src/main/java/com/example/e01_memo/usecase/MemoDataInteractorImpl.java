package com.example.e01_memo.usecase;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.example.e01_memo.MyApplication;
import com.example.e01_memo.data.MemoSQLiteOpenHelper;
import com.example.e01_memo.data.pojo.MemoItem;
import com.example.e01_memo.util.Constant;
import com.example.e01_memo.util.SharedPreferencesUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MemoDataInteractorImpl implements MemoDataInteractor {

    public static final String DATE_FORMAT_STR = "yyyy/MM/dd kk:mm";

    private MemoSQLiteOpenHelper openHelper;
    private OnAllMemoDataLoadListener allMemoDataLoadListener;
    private OnMemoDataLoadListener memoDataLoadListener;
    private OnInsertMemoDataListener insertMemoDataListener;
    private OnUpdateMemoDataListener updateMemoDataListener;
    private OnDeleteMemoDataListener deleteMemoDataListener;
    private OnRemoveMemoDataListener removeMemoDataListener;
    private OnRemoveAllMemoDataListener removeAllMemoDataListener;
    private OnRestoreMemoDataListener restoreMemoDataListener;
    private SharedPreferencesUtil.SortSetting sortType;

    public MemoDataInteractorImpl(MemoSQLiteOpenHelper helper) {
        this.openHelper = helper;
    }


    @Override
    public void getAllMemoData(SharedPreferencesUtil.SortSetting sort, OnAllMemoDataLoadListener listener) {
        this.allMemoDataLoadListener = listener;
        this.sortType = sort;
        new GetAllMemoDataTask().execute();
    }

    @Override
    public void insertMemoData(MemoItem item, OnInsertMemoDataListener listener) {
        this.insertMemoDataListener = listener;
        new InsertMemoDataTask().execute(item);
    }

    @Override
    public void updateMemoData(MemoItem item, OnUpdateMemoDataListener listener) {
        this.updateMemoDataListener = listener;
        new UpdateMemoDataTask().execute(item);
    }

    @Override
    public void deleteMemoData(List<MemoItem> items, OnDeleteMemoDataListener listener) {
        this.deleteMemoDataListener = listener;
        new DeleteMemoDataTask().execute(items);
    }

    @Override
    public void removeMemoData(MemoItem item, OnRemoveMemoDataListener listener) {
        this.removeMemoDataListener = listener;
        new RemoveMemoDataTask().execute(item);
    }

    @Override
    public void removeAllMemoData(OnRemoveAllMemoDataListener listener) {
        this.removeAllMemoDataListener = listener;
        new RemoveAllMemoDataTask().execute();
    }

    @Override
    public void restoreMemoData(List<MemoItem> items, OnRestoreMemoDataListener listener) {
        this.restoreMemoDataListener = listener;
        new RestoreMemoDataTask().execute(items);
    }

    @Override
    public void getMemoData(SharedPreferencesUtil.SortSetting sort, String keyWord, OnMemoDataLoadListener listener) {
        this.memoDataLoadListener = listener;
        this.sortType = sort;
        new GetMemoDataTask().execute(keyWord);
    }

    private class GetAllMemoDataTask extends AsyncTask<Void, Void, List<MemoItem>> {

        @Override
        protected List<MemoItem> doInBackground(Void... voids) {
            List<MemoItem> itemList = new ArrayList<>();
            String order = sortType == SharedPreferencesUtil.SortSetting.SortOld ? MemoSQLiteOpenHelper.ORDER_ASC : MemoSQLiteOpenHelper.ORDER_DESC;
            SQLiteDatabase db = openHelper.getWritableDatabase();
            db.beginTransaction();
            Cursor cursor = null;
            if (MyApplication.selectNavigationItem == Constant.NavigationItem.ALL_MEMO) {
                cursor = db.query(MemoSQLiteOpenHelper.TABLE_NAME, null, MemoSQLiteOpenHelper.COL_DELETE_FLAG + " = ?", new String[] {"0"}, null, null, MemoSQLiteOpenHelper.COL_CREATE_DATE + " " + order);
            }
            else if (MyApplication.selectNavigationItem == Constant.NavigationItem.FAVORITE) {
                cursor = db.query(MemoSQLiteOpenHelper.TABLE_NAME, null, MemoSQLiteOpenHelper.COL_FAVORITE + " = ? AND " + MemoSQLiteOpenHelper.COL_DELETE_FLAG + " = ?", new String[] {"1", "0"}, null, null, MemoSQLiteOpenHelper.COL_CREATE_DATE + " " + order);
            }
            else if (MyApplication.selectNavigationItem == Constant.NavigationItem.DELETE) {
                cursor = db.query(MemoSQLiteOpenHelper.TABLE_NAME, null, MemoSQLiteOpenHelper.COL_DELETE_FLAG + " = ?", new String[] {"1"}, null, null, MemoSQLiteOpenHelper.COL_CREATE_DATE + " " + order);
            }
            try {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex(MemoSQLiteOpenHelper._ID));
                    String memo = cursor.getString(cursor.getColumnIndex(MemoSQLiteOpenHelper.COL_MEMO));
                    String createDateStr = cursor.getString(cursor.getColumnIndex(MemoSQLiteOpenHelper.COL_CREATE_DATE));
                    Date createDate = stringToDate(createDateStr, DATE_FORMAT_STR);
                    boolean favorite = cursor.getInt(cursor.getColumnIndex(MemoSQLiteOpenHelper.COL_FAVORITE)) != 0;
                    boolean delete = cursor.getInt(cursor.getColumnIndex(MemoSQLiteOpenHelper.COL_DELETE_FLAG)) != 0;
                    itemList.add(new MemoItem(id, memo, createDate, favorite, delete));
                }
            } finally {
                db.setTransactionSuccessful();
                db.endTransaction();
                cursor.close();
                db.close();
            }
            return itemList;
        }

        @Override
        protected void onPostExecute(List<MemoItem> memoItems) {
            if (allMemoDataLoadListener != null) allMemoDataLoadListener.onAllMemoDataLoaded(memoItems);
        }
    }

    private class GetMemoDataTask extends AsyncTask<String, Void, List<MemoItem>> {

        @Override
        protected List<MemoItem> doInBackground(String... keyWords) {
            List<MemoItem> itemList = new ArrayList<>();
            String order = MemoSQLiteOpenHelper.ORDER_DESC;
            String keyWord = keyWords[0];
            SQLiteDatabase db = openHelper.getWritableDatabase();
            db.beginTransaction();
            Cursor cursor = db.query(MemoSQLiteOpenHelper.TABLE_NAME, null, MemoSQLiteOpenHelper.COL_DELETE_FLAG + " = ? AND " + MemoSQLiteOpenHelper.COL_MEMO + " LIKE ?", new String[] {"0", "%" + keyWord + "%"}, null, null, MemoSQLiteOpenHelper.COL_CREATE_DATE + " " + order);
            try {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex(MemoSQLiteOpenHelper._ID));
                    String memo = cursor.getString(cursor.getColumnIndex(MemoSQLiteOpenHelper.COL_MEMO));
                    String createDateStr = cursor.getString(cursor.getColumnIndex(MemoSQLiteOpenHelper.COL_CREATE_DATE));
                    Date createDate = stringToDate(createDateStr, DATE_FORMAT_STR);
                    boolean favorite = cursor.getInt(cursor.getColumnIndex(MemoSQLiteOpenHelper.COL_FAVORITE)) != 0;
                    boolean delete = cursor.getInt(cursor.getColumnIndex(MemoSQLiteOpenHelper.COL_DELETE_FLAG)) != 0;
                    itemList.add(new MemoItem(id, memo, createDate, favorite, delete));
                }
            } finally {
                db.setTransactionSuccessful();
                db.endTransaction();
                cursor.close();
                db.close();
            }
            return itemList;
        }

        @Override
        protected void onPostExecute(List<MemoItem> memoItems) {
            if (memoDataLoadListener != null) memoDataLoadListener.onMemoDataLoaded(memoItems);
        }
    }

    private class InsertMemoDataTask extends AsyncTask<MemoItem, Void, Void> {

        @Override
        protected Void doInBackground(MemoItem... memoItems) {
            MemoItem item = memoItems[0];
            if (item == null) return null;
            SQLiteDatabase db = openHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MemoSQLiteOpenHelper.COL_MEMO, item.getMemo());
            String dateStr = dateToString(new Date(System.currentTimeMillis()), DATE_FORMAT_STR);
            contentValues.put(MemoSQLiteOpenHelper.COL_CREATE_DATE, dateStr);
            contentValues.put(MemoSQLiteOpenHelper.COL_FAVORITE, item.isFavorite());
            try {
                db.insert(MemoSQLiteOpenHelper.TABLE_NAME, null, contentValues);
            } finally {
                db.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (insertMemoDataListener != null) insertMemoDataListener.onInsertMemoDataFinished();
        }
    }

    private class UpdateMemoDataTask extends AsyncTask<MemoItem, Void, Void> {

        @Override
        protected Void doInBackground(MemoItem... memoItems) {
            MemoItem item = memoItems[0];
            if (item == null) return null;
            SQLiteDatabase db = openHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MemoSQLiteOpenHelper.COL_MEMO, item.getMemo());
            if (item.getCreateDate() != null) {
                String dateStr = dateToString(item.getCreateDate(), DATE_FORMAT_STR);
                contentValues.put(MemoSQLiteOpenHelper.COL_CREATE_DATE, dateStr);
            }
            contentValues.put(MemoSQLiteOpenHelper.COL_FAVORITE, item.isFavorite());
            try {
                db.update(MemoSQLiteOpenHelper.TABLE_NAME, contentValues, MemoSQLiteOpenHelper._ID + "=" + item.getId(), null);
            } finally {
                db.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (updateMemoDataListener != null) updateMemoDataListener.onUpdateMemoDataFinished();
        }
    }

    private class DeleteMemoDataTask extends AsyncTask<List<MemoItem>, Void, Void> {

        @Override
        protected Void doInBackground(List<MemoItem>... lists) {
            List<MemoItem> items = lists[0];
            if (items == null) return null;
            SQLiteDatabase db = openHelper.getWritableDatabase();
            db.beginTransaction();
            for (MemoItem item : items) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MemoSQLiteOpenHelper.COL_DELETE_FLAG, item.isDeleteFlag());
                db.update(MemoSQLiteOpenHelper.TABLE_NAME, contentValues, MemoSQLiteOpenHelper._ID + "=" + item.getId(), null);
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (deleteMemoDataListener != null) deleteMemoDataListener.onDeleteMemoDataFinished();
        }
    }

    private class RemoveMemoDataTask extends AsyncTask<MemoItem, Void, Void> {

        @Override
        protected Void doInBackground(MemoItem... memoItems) {
            MemoItem item = memoItems[0];
            if (item == null) return null;
            SQLiteDatabase db = openHelper.getWritableDatabase();
            try {
                db.delete(MemoSQLiteOpenHelper.TABLE_NAME, MemoSQLiteOpenHelper._ID + "=" + item.getId(), null);
            } finally {
                db.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (removeMemoDataListener != null) removeMemoDataListener.onRemoveMemoDataFinished();
        }
    }

    private class RemoveAllMemoDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            SQLiteDatabase db = openHelper.getWritableDatabase();
            try {
                db.delete(MemoSQLiteOpenHelper.TABLE_NAME, MemoSQLiteOpenHelper.COL_DELETE_FLAG + "=?", new String[]{"1"});
            } finally {
                db.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (removeAllMemoDataListener != null) removeAllMemoDataListener.onRemoveAllMemoDataFinished();
        }
    }

    private class RestoreMemoDataTask extends AsyncTask<List<MemoItem>, Void, Void> {

        @Override
        protected Void doInBackground(List<MemoItem>... lists) {
            List<MemoItem> items = lists[0];
            if (items == null) return null;
            SQLiteDatabase db = openHelper.getWritableDatabase();
            db.beginTransaction();
            for (MemoItem item : items) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MemoSQLiteOpenHelper.COL_DELETE_FLAG, item.isDeleteFlag());
                db.update(MemoSQLiteOpenHelper.TABLE_NAME, contentValues, MemoSQLiteOpenHelper._ID + "=" + item.getId(), null);
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (restoreMemoDataListener != null) restoreMemoDataListener.onRestoreMemoDataFinished();
        }
    }

    private static Date stringToDate(String dateStr, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.JAPAN);
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String dateToString(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.JAPAN);
        return sdf.format(date);
    }
}
