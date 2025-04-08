package com.example.deepakmediaplayer.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "favouriteSong.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE favouriteSongs(id Integer primary Key, name Text,artist Text,album Text,url Text,duration Integer,isLiked Integer )");

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS favouriteSongs");
    }
    public boolean insertFavouriteSongs(int id,String name,String artist,String album,String url,long duration,boolean isLiked){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("id",id);
        contentValues.put("name",name);
        contentValues.put("artist",artist);
        contentValues.put("album",album);
        contentValues.put("url",url);
        contentValues.put("duration",duration);
        contentValues.put("isLiked",isLiked);
        long result=db.insert("favouriteSongs",null,contentValues);
        if(result==-1){
            return false;
        }
        else {
            return true;
        }
    }


    public boolean deleteFavouriteSongs(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM favouriteSongs WHERE id=?", new String[]{String.valueOf(id)});
        if (cursor.getCount() > 0) {
            long result = db.delete("favouriteSongs", "id=?", new String[]{String.valueOf(id)});
            cursor.close();
            if (result != -1) return true;
            else return false;
        } else {
            cursor.close();
            return false;
        }
    }


}
