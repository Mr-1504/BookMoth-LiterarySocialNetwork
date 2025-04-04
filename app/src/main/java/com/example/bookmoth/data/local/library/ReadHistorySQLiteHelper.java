package com.example.bookmoth.data.local.library;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.bookmoth.core.libraryutils.LibraryConst;
import com.example.bookmoth.core.libraryutils.DateTimeFormat;
import com.example.bookmoth.domain.model.library.ReadHistory;

public class ReadHistorySQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLENAME = "ReadHistories";
    public static final String ID = "chapter_id";
    public static final String WORK = "work_id";
    public static final String POSTDATE = "post_date";

    private final SQLiteDatabase wr = getWritableDatabase();
    private final SQLiteDatabase rd = getReadableDatabase();

    public ReadHistorySQLiteHelper(@Nullable Context context) {
        super(context, LibraryConst.SQLITEDB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "create table " + TABLENAME + " ( " +
                ID + " integer primary key, " +
                WORK + " integer, " +
                POSTDATE + " datetime )";
        sqLiteDatabase.execSQL(query);
    }

    public void record(ReadHistory rec) {
        ContentValues cv = new ContentValues();
        cv.put(ID, rec.getChapter_id());
        cv.put(WORK, rec.getWork_id());
        cv.put(POSTDATE, DateTimeFormat.format(rec.getPost_date(), DateTimeFormat.SQLITE));
        if (!isRead(rec.getChapter_id())) insert(cv);
    }

    public boolean isRead(int chapter_id) {
        Cursor cr = rd.rawQuery("select " + ID + " from " + TABLENAME + " where " + ID + " = " + chapter_id, null);
        cr.moveToFirst();
        boolean res = !cr.isAfterLast();
        cr.close();
        return res;
    }

    public boolean isOutdated(int chapter_id, String post_date) {
        Cursor cr = rd.rawQuery("select " + POSTDATE + " from " + TABLENAME + " where " + ID + " = " + chapter_id + " limit 1", null);
        cr.moveToFirst();
        boolean res;
        if (cr.isAfterLast())
            res = true;
        else {
            String recorded_date = cr.getString(cr.getColumnIndexOrThrow(POSTDATE));
            res = !recorded_date.equalsIgnoreCase(DateTimeFormat.format(post_date, DateTimeFormat.SQLITE));
//            Log.d("SQLITEREAD", String.format("%s %s %s", recorded_date, DateTimeFormat.format(post_date, DateTimeFormat.SQLITE), res ? "true" : "false"));
            if (res) {
                delete(chapter_id);
            }
        }
        cr.close();
        return res;
    }

    private void insert(ContentValues cv) {
        wr.insert(TABLENAME, null, cv);
    }

    private void update(int chapter_id, ContentValues cv) {
        if (cv.containsKey(ID)) cv.remove(ID);
        wr.update(TABLENAME, cv, ID + " = " + chapter_id, null);
    }

    private void delete(int chapter_id) {
        wr.delete(TABLENAME, ID + " = " + chapter_id, null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}
}
