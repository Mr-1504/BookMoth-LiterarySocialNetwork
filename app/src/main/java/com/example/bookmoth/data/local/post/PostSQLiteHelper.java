package com.example.bookmoth.data.local.post;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.bookmoth.domain.model.post.Post;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PostSQLiteHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "post.db";
    public static final String TABLE_NAME = "post_table";
    public static final String COL_1 = "ID";              // Post ID từ server
    public static final String COL_2 = "TITLE";
    public static final String COL_3 = "CONTENT";
    public static final String COL_4 = "AUTHOR";          // Author ID
    public static final String COL_5 = "DATE";
    public static final String COL_6 = "MEDIA_URL";
    public static final String COL_7 = "AUTHOR_NAME";     // Tên đại diện
    public static final String COL_8 = "AUTHOR_AVATAR_URL"; // URL ảnh đại diện
    private static final int MAX_POSTS = 10;
    private final ImageDownloader imageDownloader;

    public PostSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, 3);
        this.imageDownloader = new ImageDownloader(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_1 + " INTEGER PRIMARY KEY, " +
                COL_2 + " TEXT, " +
                COL_3 + " TEXT, " +
                COL_4 + " TEXT, " +
                COL_5 + " TEXT, " +
                COL_6 + " TEXT, " +
                COL_7 + " TEXT, " +
                COL_8 + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_6 + " TEXT");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_7 + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_8 + " TEXT");
        }
    }
    public void savePosts(List<Post> posts) {
        new SavePostsTask().execute(posts);
    }

    private class SavePostsTask extends AsyncTask<List<Post>, Void, Void> {
        @Override
        protected Void doInBackground(List<Post>... params) {
            List<Post> posts = params[0];
            SQLiteDatabase db = getWritableDatabase();
            db.beginTransaction();
            try {
                db.execSQL("DELETE FROM " + TABLE_NAME);

                for (int i = 0; i < Math.min(posts.size(), MAX_POSTS); i++) {
                    Post post = posts.get(i);
                    String mediaPath = imageDownloader.downloadAndSaveImage(post.getMediaUrl(), post.getPostId());
                    String avatarPath = imageDownloader.downloadAndSaveImage(post.getAuthor_avatar_url(), post.getAuthorId());

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(COL_1, post.getPostId());
                    contentValues.put(COL_2, post.getTitle());
                    contentValues.put(COL_3, post.getContent());
                    contentValues.put(COL_4, String.valueOf(post.getAuthorId()));
                    contentValues.put(COL_5, post.getTimestamp());
                    contentValues.put(COL_6, mediaPath);
                    contentValues.put(COL_7, post.getAuthor_name());
                    contentValues.put(COL_8, avatarPath);
                    db.insert(TABLE_NAME, null, contentValues);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
                db.close();
            }
            return null;
        }
    }

    public List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_5 + " DESC LIMIT " + MAX_POSTS, null);

        if (cursor.moveToFirst()) {
            do {
                Post post = new Post();
                post.setPostId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_1)));
                post.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_2)));
                post.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COL_3)));
                post.setAuthorId(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(COL_4))));
                post.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(COL_5)));
                post.setMediaUrl(cursor.getString(cursor.getColumnIndexOrThrow(COL_6)));
                post.setAuthor_name(cursor.getString(cursor.getColumnIndexOrThrow(COL_7)));
                post.setAuthor_avatar_url(cursor.getString(cursor.getColumnIndexOrThrow(COL_8)));
                posts.add(post);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return posts;
    }

    public String getAuthorNameById(int authorId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String authorName = null;
        Cursor cursor = db.rawQuery("SELECT " + COL_7 + " FROM " + TABLE_NAME + " WHERE " + COL_4 + " = ? LIMIT 1",
                new String[]{String.valueOf(authorId)});

        if (cursor.moveToFirst()) {
            authorName = cursor.getString(cursor.getColumnIndexOrThrow(COL_7));
        }
        cursor.close();
        return authorName;
    }

    // Xóa tất cả dữ liệu
    public void clearAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }

    // Kiểm tra số lượng bài đăng hiện tại
    public int getPostCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
}