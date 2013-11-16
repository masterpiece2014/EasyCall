package cai.bowen.easycall;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataManager extends SQLiteOpenHelper {
	
	private final String WALLPAPER_PERFIX;
	private Context context = null;
	
	private DataManager(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
		WALLPAPER_PERFIX = context.getResources().getString(R.string.wallpaper_perfix);
	}
	
	private static DataManager class_handler = null;
	
	synchronized public static void  init(final Context ct) {
		if (null == class_handler) {
			class_handler = new DataManager(ct);
		}
	}
	public static DataManager getInstance() {
		if (null == class_handler) {
			throw new NullPointerException("DataManager uninitialized");
		}
		return class_handler;
	}
	
	synchronized int getPictureId(int index) {
		try {
			final String resourceName = WALLPAPER_PERFIX + String.valueOf(index);
			Field wallpaperId = R.drawable.class.getField(resourceName);
			return wallpaperId.getInt(wallpaperId);
		} catch (Exception e) {
			Log.e(TAG_OTHER,e.toString());
			return R.drawable.wallpaper_0;
		}
	}

/////////////////////DATA BASE///////////////////////////
	static final String DB_NAME = "db_easycall";
	static final int DB_VERSION = 20130920;
	// Common attributes of two tables
	static final String FLD_ID = "id";
	// // first table: store unique data
	static final String TABLE_APP_INFO = "ec_app_info";

	static final String FLD_COUNT = "count_start";
	static final String FLD_THIS_PHONE_NUM = "phone_number_this";
	static final String FLD_TGT_PHONE_NUM = "phone_number_target";
	static final String FLD_THIS_PHONE_IMEI = "phone_imei_this";
	
	
	// second table, store sm templates and data modified date
	static final String TABLE_SM_INFO = "ec_sm_info";
	static final String FLD_SM_TEMPLATES = "sm_template";
	static final String FLD_MODIFIED_DATE = "date_modified";

	static final String TAG_CURSOR_ERROR = "Cursor_Error";
	static final String TAG_DB_ERROR = "Database_Error";
	static final String TAG_OTHER = "Other_Error";
	


	@Override
	public void onCreate(SQLiteDatabase database) {
		
	        database.execSQL(
	        		"CREATE TABLE IF NOT EXISTS " + TABLE_APP_INFO 
	        		+ " ( "
        				+ FLD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        				+ FLD_COUNT 			+ " INTEGER NOT NULL DEFAULT 0, "
        				+ FLD_THIS_PHONE_NUM 	+ " TEXT NOT NULL, "
        				+ FLD_THIS_PHONE_IMEI 	+ " TEXT NOT NULL, "
        				+ FLD_TGT_PHONE_NUM 	+ " TEXT NOT NULL"
        			+ " ) ");

	        database.execSQL(
	        		"CREATE TABLE IF NOT EXISTS " + TABLE_SM_INFO 
	        		+ "(" 
        				+ FLD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        				+ FLD_SM_TEMPLATES 	+ " TEXT NOT NULL, "
        				+ FLD_MODIFIED_DATE + " TEXT NOT NULL,"
        				+ FLD_MODIFIED_DATE + " TEXT NOT NULL"
        			+ " )");

	        ContentValues cv = new ContentValues();
	        cv.put(FLD_THIS_PHONE_NUM, this.context.getString(R.string.phone_num_mo));
	        cv.put(FLD_THIS_PHONE_IMEI, this.context.getString(R.string.phone_imei));
	        cv.put(FLD_TGT_PHONE_NUM, this.context.getString(R.string.phone_num_son));
	        database.insert(TABLE_APP_INFO, null, cv);
	        
	        final String initDate = new Date().toString();
	        final String[] smTemps = context.getResources().getStringArray(
	        									R.array.txt_sm_templates);
	        for(final String str : smTemps) {
		        cv = new ContentValues(); 
	        	cv.put(FLD_SM_TEMPLATES, str);
		        cv.put(FLD_MODIFIED_DATE, initDate);
		        
		        database.insert(TABLE_SM_INFO, null, cv);
	        }
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(" DROP TABLE IF EXISTS " + TABLE_APP_INFO);
		db.execSQL(" DROP TABLE IF EXISTS " + TABLE_SM_INFO);
		onCreate(db);
	}
	
	synchronized int getCount() {
		Cursor cursor = this.getReadableDatabase().rawQuery(
				"SELECT " + FLD_COUNT + " FROM " + TABLE_APP_INFO + " WHERE "
						+ FLD_ID + " = 1", null);
		if (!cursor.moveToFirst()) {
			Log.e(TAG_CURSOR_ERROR, "getCount moveToFirst");
			return 0;
		}
		return cursor.getInt(0);// 0
	}
	
	synchronized void count(int i) {
//		int current = getCount();
//		current += i;
		this.getWritableDatabase().execSQL(
				"UPDATE " + TABLE_APP_INFO 
					+ " SET " + FLD_COUNT + " = FLD_COUNT + " + i
						+ " where " +  FLD_ID + " = 1");
	}
	
	int getThisIMEIHash() {
		
		Cursor cursor = this.getReadableDatabase().rawQuery(
				"SELECT " + FLD_THIS_PHONE_IMEI
						+ " FROM " + TABLE_APP_INFO 
							+ " WHERE " + FLD_ID + " = 1", null);
		if (!cursor.moveToFirst()) {
			Log.e(TAG_CURSOR_ERROR, "getThisIMEIHash moveToFirst");
		}
		return cursor.getString(0).hashCode();
	}
	
	void setThisPhoneNum(final String newPhoneNum) {
		this.getWritableDatabase().execSQL(
				"UPDATE " + TABLE_APP_INFO 
					+ " SET " + FLD_THIS_PHONE_NUM + " = '" + newPhoneNum + "' "
					+ " where " +  FLD_ID + " = 1");
	}
	
	final String getThisPhoneNum() {

		Cursor cursor = this.getReadableDatabase().rawQuery(
				"SELECT " + FLD_THIS_PHONE_NUM 
						+ " FROM " + TABLE_APP_INFO 
							+ " WHERE " + FLD_ID + " =  1", null);

		if (!cursor.moveToFirst()) {
			Log.e(TAG_CURSOR_ERROR, "getThisPhoneNum moveToFirst");
			return "13657192845";
		}
		return cursor.getString(0);
	}

	void setTgtPhoneNumber(final String num) {
		this.getWritableDatabase().execSQL(
				"UPDATE " + TABLE_APP_INFO
					+ " SET " + FLD_TGT_PHONE_NUM + " = '" + num + "' "
					+ " where " +  FLD_ID + " = 1");
	}
	
	final String getTgtPhoneNumber() {
		Cursor cursor = this.getReadableDatabase().rawQuery(
				"SELECT " + FLD_TGT_PHONE_NUM 
						+ " FROM " + TABLE_APP_INFO 
							+ " WHERE " + FLD_ID + " = 1", null);

		if (!cursor.moveToFirst()) {
			Log.e(TAG_CURSOR_ERROR, "getTgtPhoneNumber moveToFirst");
			return "18392387786";
		}
		return cursor.getString(0);
	}

	final String[] getTemplates() {
		Cursor cursor = this.getReadableDatabase().rawQuery(
				"SELECT " + FLD_SM_TEMPLATES + " FROM " + TABLE_SM_INFO, null);
		
		ArrayList<String> strs = new ArrayList<String>();
		
		if (null != cursor) {
			if (cursor.moveToFirst()) {
				do {
					strs.add(cursor.getString(0));
				} while (cursor.moveToNext());
			}
		} else {
			Log.e(TAG_CURSOR_ERROR, "getTemplates null cursor");
		}
		return strs.toArray(new String[strs.size()]);
	}
	
	void addTemplate(final String str) {
		this.getWritableDatabase().execSQL(
				"INSERT INTO " + TABLE_SM_INFO
				+ " ( " + FLD_SM_TEMPLATES + " , " + FLD_MODIFIED_DATE + " ) "
				+ " VALUES ( '" + str + "', '" + new Date().toString() + "' )" 
				);
	}

	void deleteTemplate(final String oldStr) {
		this.getWritableDatabase().execSQL(
				"DELETE FROM " + TABLE_SM_INFO
				+ " WHERE " + FLD_SM_TEMPLATES + " = '" + oldStr + "' ");
	}
	
	void restore() {
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.execSQL(" DROP TABLE IF EXISTS " + TABLE_SM_INFO);
		db.execSQL(
		"CREATE TABLE IF NOT EXISTS " + TABLE_SM_INFO 
		+ "(" 
			+ FLD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ FLD_SM_TEMPLATES 	+ " TEXT, "
			+ FLD_MODIFIED_DATE 	+ " TEXT "
		+ " )");
		
		final String initDate = new Date().toString();
		final String[] smTemps = context.getResources().getStringArray(
											R.array.txt_sm_templates);
		ContentValues cv = new ContentValues(); 
		for(final String str : smTemps) {
		    cv = new ContentValues(); 
			cv.put(FLD_SM_TEMPLATES, str);
		    cv.put(FLD_MODIFIED_DATE, initDate);
		    
		    db.insert(TABLE_SM_INFO, null, cv);
		}
	}
}


