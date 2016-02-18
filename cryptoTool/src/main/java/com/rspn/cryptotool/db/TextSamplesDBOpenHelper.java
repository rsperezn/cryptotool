package com.rspn.cryptotool.db;

import com.rspn.cryptotool.utils.CTUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TextSamplesDBOpenHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "textsamples.db";
	private static final int DATABASE_VERSION = 4;

	public static final String TABLE_TEXTSAMPLES = "textsamples";
	public static final String COLUMN_ID = "ID";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_CONTENT = "content";
	public static final String COLUMN_DELETABLE = "deletable";

	private static final String TABLE_CREATE = 
			"CREATE TABLE " + TABLE_TEXTSAMPLES + " (" +
					COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					COLUMN_TITLE + " TEXT, " +
					COLUMN_TYPE + " NUMERIC, " +
					COLUMN_CONTENT + " TEXT, " +
					COLUMN_DELETABLE +" NUMERIC "+
					")";

//	public static final String TABLE_MYTEXTSAMPLES = "mytextsamples";
//	private static final String TABLE_MYTEXTSAMPLES_CREATE = 
//			"CREATE TABLE " + TABLE_MYTEXTSAMPLES + " (" +
//			COLUMN_ID + " INTEGER PRIMARY KEY)";
	
	public TextSamplesDBOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	//Automatically called by SDK
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE);
		//db.execSQL(TABLE_MYTEXTSAMPLES_CREATE);

		Log.i(CTUtils.TAG, "Table has been created");
	}

	@Override
	//Automatically called by SDK
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEXTSAMPLES);
		//db.execSQL("DROP TABLE IF EXISTS " + TABLE_MYTEXTSAMPLES);
		onCreate(db);

		Log.i(CTUtils.TAG, "Database has been upgraded from " + 
				oldVersion + " to " + newVersion);
	}
}
