package com.rspn.cryptotool.db;

import java.util.ArrayList;
import java.util.List;

import com.rspn.cryptotool.model.Text;
import com.rspn.cryptotool.utils.CTUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TextSamplesDataSource {

	SQLiteOpenHelper dbhelper;
	SQLiteDatabase database;
	private static final String[] allColumns ={
		TextSamplesDBOpenHelper.COLUMN_ID, TextSamplesDBOpenHelper.COLUMN_TITLE,
		TextSamplesDBOpenHelper.COLUMN_TYPE,TextSamplesDBOpenHelper.COLUMN_CONTENT,
		TextSamplesDBOpenHelper.COLUMN_DELETABLE};
	
	public TextSamplesDataSource(Context context){
		dbhelper= new TextSamplesDBOpenHelper(context);
			
	}
	
	public void open(){
		Log.i(CTUtils.TAG, "Database Opened");
		database= dbhelper.getWritableDatabase();
	}
	
	public void close(){
		Log.i(CTUtils.TAG, "Database Closed");
		dbhelper.close();		
	}
	
	//used to insert in db
	public Text create(Text textSample){
		ContentValues values = new ContentValues();//has all SQL commands
		values.put(TextSamplesDBOpenHelper.COLUMN_TITLE, textSample.getTitle());
		values.put(TextSamplesDBOpenHelper.COLUMN_TYPE, textSample.getType());
		values.put(TextSamplesDBOpenHelper.COLUMN_CONTENT, textSample.getContent());
		values.put(TextSamplesDBOpenHelper.COLUMN_DELETABLE, textSample.getDeletable());
		long insertid= database.insert(TextSamplesDBOpenHelper.TABLE_TEXTSAMPLES, null, values);
		textSample.setId(insertid);
		return textSample;	
	}
	public List<Text> findAll(){
		Cursor cursor = database.query( TextSamplesDBOpenHelper.TABLE_TEXTSAMPLES,allColumns , null,null, null,null,null);
		Log.i(CTUtils.TAG, "Returned " + cursor.getCount()+ "rows");
		List<Text> textSamples = cursorToList(cursor);
		return textSamples;
	}
	
	public List<Text> findFiltered(String selection, String orderby){
		Cursor cursor = database.query( TextSamplesDBOpenHelper.TABLE_TEXTSAMPLES,allColumns , selection,null, null,null,orderby);
		Log.i(CTUtils.TAG, "Returned " + cursor.getCount()+ "rows");
		List<Text> textSamples = cursorToList(cursor);
		return textSamples;
	}
	
	public boolean delete(long ID){
		return database.delete(TextSamplesDBOpenHelper.TABLE_TEXTSAMPLES, TextSamplesDBOpenHelper.COLUMN_ID+ "="+ID, null)>0;
	}
	
	
	
	public List<Text> cursorToList(Cursor cursor){
		//loop through all columns
				List<Text> textSamples = new ArrayList<Text>();
				if(cursor.getCount()>0){
					while(cursor.moveToNext()){
						Text text = new Text();
						text.setId(cursor.getLong(cursor.getColumnIndex(TextSamplesDBOpenHelper.COLUMN_ID)));
						text.setTitle(cursor.getString(cursor.getColumnIndex(TextSamplesDBOpenHelper.COLUMN_TITLE)));
						text.setType(cursor.getString(cursor.getColumnIndex(TextSamplesDBOpenHelper.COLUMN_TYPE)));
						text.setContent(cursor.getString(cursor.getColumnIndex(TextSamplesDBOpenHelper.COLUMN_CONTENT)));
						text.setDeletable(cursor.getInt(cursor.getColumnIndex(TextSamplesDBOpenHelper.COLUMN_DELETABLE)));
						
						textSamples.add(text);	
					}
				}
		
		return textSamples;	
	}

	public List<Text> findSamples() {
		String filterSamples = "deletable =0";
		Cursor cursor = database.query(TextSamplesDBOpenHelper.TABLE_TEXTSAMPLES,allColumns,filterSamples,null,null,null,null);
		Log.i(CTUtils.TAG,"Returned " + cursor.getCount()+ "rows");
		List<Text> textSamples = cursorToList(cursor);
		return textSamples;
	}
	
	
}
