package com.example.e01_memo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class MemoSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "memoDB.db";
    private static final int DATABASE_VERSION = 1;

    public static final String ORDER_ASC = "ASC";
    public static final String ORDER_DESC = "DESC";

    public static final String TABLE_NAME = "memo_table";
    public static final String _ID = "_id";
    public static final String COL_MEMO = "memo";
    public static final String COL_CREATE_DATE = "create_date";
    public static final String COL_FAVORITE = "favorite";
    public static final String COL_DELETE_FLAG = "delete_flag";

    private static final String CREATE_MEMO_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY, " +
            COL_MEMO + " TEXT, " +
            COL_CREATE_DATE + " TEXT, " +
            COL_FAVORITE + " INTEGER DEFAULT 0, " +
            COL_DELETE_FLAG + " INTEGER DEFAULT 0)";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public MemoSQLiteOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MEMO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }
}
