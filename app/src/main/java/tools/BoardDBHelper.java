package tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BoardDBHelper extends SQLiteOpenHelper {

    private final static String dbname = "board.db";

    private final static String CREATE_TABLE_MSG = "create table msg(_id INTEGER PRIMARY KEY AUTOINCREMENT,id INTEGER not null unique,userid text not null,time text not null,content text not null,haspic INTEGER not null,picture BLOB,comment BLOB)";
    private final static String CREATE_TABLE_USER_INFO = "create table userinfo(id INTEGER primary key autoincrement,userid text not null,nickname text not null,portrait BLOB not null,email text not null,checktime text not null,priority integer not null,token text not null,data BLOB)";
    private final static String CREATE_TABLE_PUBLIC_INFO = "create table publicinfo(id INTEGER primary key autoincrement,userid text not null unique,nickname text not null,portrait BLOB not null)";

    private final static int DB_VERSION = 1;

    private static BoardDBHelper msgDBHelper;

    private BoardDBHelper(Context context) {
        super(context, dbname, null, DB_VERSION);
    }

    public static BoardDBHelper getMsgDBHelper(Context context) {

        if (msgDBHelper == null) {
            return new BoardDBHelper(context);
        }

        return msgDBHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_MSG);
        db.execSQL(CREATE_TABLE_USER_INFO);
        db.execSQL(CREATE_TABLE_PUBLIC_INFO);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        for (int i = oldVersion; i < newVersion; i++) {
            switch (i) {
                case 2:
                    upgradeToVersion2(db);
                    break;
                case 3:
                    upgradeToVersion3(db);
                    break;

                default:
                    break;
            }
        }
    }

    private void upgradeToVersion2(SQLiteDatabase db) {
//        String sql1 = "ALTER TABLE msg ADD COLUMN comment BLOB";
//        db.execSQL(sql1);
    }

    private void upgradeToVersion3(SQLiteDatabase db) {

    }

}
