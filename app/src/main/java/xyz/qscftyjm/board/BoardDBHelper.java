package xyz.qscftyjm.board;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BoardDBHelper extends SQLiteOpenHelper {

    final static String dbname = "board";
    final static String CREATE_TABLE_MSG = "create table msg(_id INTEGER PRIMARY KEY AUTOINCREMENT,userid text,time text,content text,picnum integer,picture BLOB)";
    public static BoardDBHelper msgDBHelper;

    public BoardDBHelper(Context context, int version) {
        super(context, dbname, null, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
