package cai.bowen.easycall;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataManager extends SQLiteOpenHelper {
	
	private final int WALLPARER_NUM;
	
	private Context context = null;
	
	private DataManager(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
		WALLPARER_NUM = context.getResources().getInteger(R.integer.wallpaper_num);
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
	
	int getRandomBackgroundID() {
		String wallpCode = new String(context.getString(R.string.name_wallpaper_perfix));
		wallpCode += String.valueOf(new Random().nextInt(WALLPARER_NUM));
		try {
			Field wallpField = R.drawable.class.getField(wallpCode);
			return wallpField.getInt(wallpField);
		} catch (Exception e) {
			e.printStackTrace();
			return R.drawable.wallpaper_0;
		}
	}
/////////////////////DATA BASE///////////////////////////
	static final String DB_NAME;
	static final int DB_VERSION;
				// Common attributes of two tables
    static final String FLD_ID;
    //		// first table: store unique data
	static final String TABLE_UNIQUE;
	
    static final String FLD_COUNT;
    static final String FLD_THIS_PHONE_NUM;
    static final String FLD_TGT_PHONE_NUM;
    static final String FLD_THIS_PHONE_IMEI;
    			//second table, store sm templates and data modified date
	static final String TABLE_VARIABLE;
    static final String FLD_SM_TEMPLATES;
    static final String FLD_MODIFIED_DATE;
    
    static final String TAG_CURSOR_ERROR;
    static final String TAG_DB_ERROR;
	static {
		DB_NAME = "EasyCall_database";
		DB_VERSION = 20130920;
		TABLE_UNIQUE = "Table_Unique";
		FLD_ID = "_id";
	    FLD_COUNT = "AppStartCount";
	    FLD_THIS_PHONE_NUM = "ThisPhoneNumber";
	    FLD_THIS_PHONE_IMEI = "ThisPhoneIMEI";
	    FLD_TGT_PHONE_NUM = "TargetPhoneNumber";
	    
		TABLE_VARIABLE = "Table_UsageData";
	    FLD_SM_TEMPLATES = "SmTemplates";
	    FLD_MODIFIED_DATE = "ModifiedDate";
	    TAG_CURSOR_ERROR = "Cursor error";
	    TAG_DB_ERROR = "Database error";
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		
	        database.execSQL(
	        		"CREATE TABLE IF NOT EXISTS " + TABLE_UNIQUE 
	        		+ " ( "
        				+ FLD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        				+ FLD_COUNT 			+ " INTEGER, "
        				+ FLD_THIS_PHONE_NUM 	+ " TEXT, "
        				+ FLD_THIS_PHONE_IMEI 	+ " TEXT, "
        				+ FLD_TGT_PHONE_NUM 	+ " TEXT "
        			+ " ) ");

	        database.execSQL(
	        		"CREATE TABLE IF NOT EXISTS " + TABLE_VARIABLE 
	        		+ "(" 
        				+ FLD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        				+ FLD_SM_TEMPLATES 	+ " TEXT, "
        				+ FLD_MODIFIED_DATE 	+ " TEXT "
        			+ " )");

	        ContentValues cv = new ContentValues(); 
	        cv.put(FLD_COUNT, 0);
	        cv.put(FLD_THIS_PHONE_NUM, this.context.getString(R.string.phone_num_mo));
	        cv.put(FLD_THIS_PHONE_IMEI, this.context.getString(R.string.phone_imei));
	        cv.put(FLD_TGT_PHONE_NUM, this.context.getString(R.string.phone_num_son));
	        database.insert(TABLE_UNIQUE, null, cv);
	        
	        final String initDate = new Date().toString();
	        final String[] smTemps = context.getResources().getStringArray(
	        									R.array.txt_sm_templates);
	        for(final String str : smTemps) {
		        cv = new ContentValues(); 
	        	cv.put(FLD_SM_TEMPLATES, str);
		        cv.put(FLD_MODIFIED_DATE, initDate);
		        database.insert(TABLE_VARIABLE, null, cv);
	        }
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(" DROP TABLE IF EXISTS " + TABLE_UNIQUE);
		db.execSQL(" DROP TABLE IF EXISTS " + TABLE_VARIABLE);
		onCreate(db);
	}
	int getCount() {
		Cursor cursor = this.getReadableDatabase().rawQuery(
				"SELECT " + FLD_COUNT + " FROM " + TABLE_UNIQUE + " WHERE "
						+ FLD_ID + " = 1", null);
		if (!cursor.moveToFirst()) {
			Log.e(TAG_CURSOR_ERROR, "getCount moveToFirst");
			return 0;
		}
		return cursor.getInt(0);// 0
	}
	
	void count(int i) {
		int current = getCount();
		current += i;
		this.getWritableDatabase().execSQL(
				"UPDATE " + TABLE_UNIQUE 
					+ " SET " + FLD_COUNT + " = " + current 
						+ " where " +  FLD_ID + " = 1");
	}
	
	int getThisIMEIHash() {
		
		Cursor cursor = this.getReadableDatabase().rawQuery(
				"SELECT " + FLD_THIS_PHONE_IMEI
						+ " FROM " + TABLE_UNIQUE 
							+ " WHERE " + FLD_ID + " = 1", null);
		if (!cursor.moveToFirst()) {
			Log.e(TAG_CURSOR_ERROR, "getThisIMEIHash moveToFirst");
		}
		return cursor.getString(0).hashCode();
	}
	
	void setThisPhoneNum(final String newPhoneNum) {
		this.getWritableDatabase().execSQL(
				"UPDATE " + TABLE_UNIQUE 
					+ " SET " + FLD_THIS_PHONE_NUM + " = '" + newPhoneNum + "' "
					+ " where " +  FLD_ID + " = 1");
	}
	
	final String getThisPhoneNum() {

		Cursor cursor = this.getReadableDatabase().rawQuery(
				"SELECT " + FLD_THIS_PHONE_NUM 
						+ " FROM " + TABLE_UNIQUE 
							+ " WHERE " + FLD_ID + " =  1", null);

		if (!cursor.moveToFirst()) {
			Log.e(TAG_CURSOR_ERROR, "getThisPhoneNum moveToFirst");
			return "13657192845";
		}
		return cursor.getString(0);
	}

	void setTgtPhoneNumber(final String num) {
		this.getWritableDatabase().execSQL(
				"UPDATE " + TABLE_UNIQUE
					+ " SET " + FLD_TGT_PHONE_NUM + " = '" + num + "' "
					+ " where " +  FLD_ID + " = 1");
	}
	
	final String getTgtPhoneNumber() {
		Cursor cursor = this.getReadableDatabase().rawQuery(
				"SELECT " + FLD_TGT_PHONE_NUM 
						+ " FROM " + TABLE_UNIQUE 
							+ " WHERE " + FLD_ID + " = 1", null);

		if (!cursor.moveToFirst()) {
			Log.e(TAG_CURSOR_ERROR, "getTgtPhoneNumber moveToFirst");
			return "18392387786";
		}
		return cursor.getString(0);
	}

	final String[] getTemplates() {
		Cursor cursor = this.getReadableDatabase().rawQuery(
				"SELECT " + FLD_SM_TEMPLATES + " FROM " + TABLE_VARIABLE, null);
		
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
				"INSERT INTO " + TABLE_VARIABLE
				+ " ( " + FLD_SM_TEMPLATES + " , " + FLD_MODIFIED_DATE + " ) "
				+ " VALUES ( '" + str + "', '" + new Date().toString() + "' )" 
				);
	}

	void deleteTemplate(final String oldStr) {
		this.getWritableDatabase().execSQL(
				"DELETE FROM " + TABLE_VARIABLE
				+ " WHERE " + FLD_SM_TEMPLATES + " = '" + oldStr + "' ");
	}
	
	void restore() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(" DROP TABLE IF EXISTS " + TABLE_UNIQUE);
		db.execSQL(" DROP TABLE IF EXISTS " + TABLE_VARIABLE);
		this.onCreate(db);
	}
}










