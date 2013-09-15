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

public class DataManager_test extends SQLiteOpenHelper {
	
	private final int WALLPARER_NUM;
	
	private Context context = null;
	private final Class<R.drawable> res_; // using reflex to get resource id
	
	private DataManager_test(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
		res_ =  R.drawable.class;
		WALLPARER_NUM = context.getResources().getInteger(R.integer.wallpaper_num);
	}
	
	private static DataManager_test class_handler = null;
	public static void init(final Context ct) {
		if (null == class_handler) {
			class_handler = new DataManager_test(ct);
		}
	}
	public static DataManager_test getInstance() {
		if (null == class_handler) {
			throw new NullPointerException("DataManager uninitialized");
		}
		return class_handler;
	}
	
	int getRandomBackgroundID() {
		String wallpCode = new String(context.getString(R.string.name_wallpaper_perfix));
		wallpCode += String.valueOf(new Random().nextInt(WALLPARER_NUM));
		try {
			Field wallpField = res_.getField(wallpCode);
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
	static {
		DB_NAME = "CallMySon_database";
		DB_VERSION = 20130913;
		TABLE_UNIQUE = "Table_CallMySonConstant";
		FLD_ID = "_id";
	    FLD_COUNT = "COL_appStartCount";
	    FLD_THIS_PHONE_NUM = "ThisPhoneNumber";
	    FLD_THIS_PHONE_IMEI = "ThisPhoneIMEI";
	    FLD_TGT_PHONE_NUM = "TargetPhoneNumber";
	    
		TABLE_VARIABLE = "Table_CallMySonUsageData";
	    FLD_SM_TEMPLATES = "SmTemplates";
	    FLD_MODIFIED_DATE = "ModifiedDate";
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
	        cv.put(FLD_THIS_PHONE_IMEI, this.context.getString(R.string.phone_imei1));
	        cv.put(FLD_TGT_PHONE_NUM, this.context.getString(R.string.phone_num_son));
	        database.insert(TABLE_UNIQUE, null, cv);
	        

	        final String initDate = new Date().toString();
	        final String[] smTemps = context.getResources().getStringArray(R.array.txt_sm_templates);
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
///////////////////////////////////////////////////////////
	
	int getCount() {
		Cursor cursor = this.getReadableDatabase().query(
								TABLE_UNIQUE,
								new String[]{FLD_COUNT}, null, null, null, null, null);
//		return cursor.getInt(1);
//System.out.println(cursor.getCount() );
//		return cursor.getInt(cursor.getColumnIndex(FLD_COUNT));
//return cursor.getInt(1);
		
try {
	if (cursor.moveToFirst()) {
		do {
	System.out.println("===>>> " + cursor.getString(1));
		} while (cursor.moveToNext());
	}
	return cursor.getInt(1);
} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}

return cursor.getInt(0);

	}
	
	void count(int i) {
		int current = getCount();
		current += i;
		this.getWritableDatabase().execSQL(
				"UPDATE " + TABLE_UNIQUE 
					+ " SET " + FLD_COUNT + " = " + current 
						+ " where " +  FLD_ID + " = 1");
//		execSQL("UPDATE emptable set age="+ emp_age+ " where name='"+ emp_name +"'");
	}
	
	int getThisIMEIHash() {
//		Cursor cursor = this.getReadableDatabase().query(
//				TABLE_UNIQUE,
//				new String[]{FLD_THIS_PHONE_IMEI}, null, null, null, null, null);
//		
////System.out.println(FLD_THIS_PHONE_IMEI + " getColumnIndex " + cursor.getColumnIndex(FLD_THIS_PHONE_IMEI));
////		return cursor.getString(1).hashCode();
////		return cursor.
		
		Cursor cursor = this.getReadableDatabase().rawQuery(
				"SELECT " + FLD_THIS_PHONE_IMEI
						+ " FROM " + TABLE_UNIQUE 
							+ " WHERE " + FLD_ID + " = 1", null);
		return cursor.getString(0).hashCode();
		
	}
	
	void setThisPhoneNum(final String newPhoneNum) {
		this.getWritableDatabase().execSQL(
				"UPDATE " + TABLE_UNIQUE 
					+ " SET " + FLD_THIS_PHONE_NUM + " = " + newPhoneNum 
					+ " where " +  FLD_ID + " = 1");
	}
	
	final String getThisPhoneNum() {
//		Cursor cursor = this.getReadableDatabase().query(
//			TABLE_UNIQUE,
//			new String[]{FLD_THIS_PHONE_NUM},
//			FLD_ID + " =? ", new String[] {"1"},
//			null, null, null);
		
		
//System.out.println(cursor.getColumnIndex(FLD_THIS_PHONE_NUM));
////		return cursor.getString(cursor.getColumnIndex(FLD_THIS_PHONE_NUM));
//
//try {
//	System.out.println( ">>>" + 
//			cursor.getString(0));
//	
//	System.out.println(">>>" + 
//			cursor.getString(1));
//	System.out.println(">>>" + 
//			cursor.getString(3));
//} catch (Exception e) {
//	// TODO Auto-generated catch block
//	e.printStackTrace();
//}
//		 Cursor c=db.rawQuery( 
//			     "SELECT name FROM sqlite_master WHERE type='table' AND name='mytable'", null); 
		Cursor cursor = this.getReadableDatabase().rawQuery(
				"SELECT " + FLD_THIS_PHONE_NUM 
						+ " FROM " + TABLE_UNIQUE 
							+ " WHERE " + FLD_ID + " =  1", null);
		return cursor.getString(1);
//		return "13657192845";
	}

	
	void setTgtPhoneNumber(final String num) {
		this.getWritableDatabase().execSQL(
				"UPDATE " + TABLE_UNIQUE
					+ " SET " + FLD_TGT_PHONE_NUM + " = " + num 
					+ " where " +  FLD_ID + " = 1");
	}
	
	final String getTgtPhoneNumber() {		
		Cursor cursor = this.getReadableDatabase().query(
			TABLE_UNIQUE,
			new String[]{FLD_TGT_PHONE_NUM}, null, null, null, null, null);
		return cursor.getString(0);
//		System.out.println(">>>> " + FLD_TGT_PHONE_NUM + "  " + cursor.getCount());
//		//return cursor.getInt(0);
//		if (cursor.moveToNext()) {
//			return cursor.getString(cursor.getColumnIndex(FLD_TGT_PHONE_NUM));
//		}
//		return "";
	}

	final String[] getTemplates() {
		Cursor cursor = this.getReadableDatabase().query(
				TABLE_VARIABLE,
				new String[]{FLD_SM_TEMPLATES}, null, null, null, null, null);
		
//	System.out.println(">>>> Row(should be 4)" + cursor.getCount());
//		return cursor.getString(0);
//	String []str = new String[cursor.getCount()];
//	return str;
	ArrayList<String> strs = new ArrayList<String>();
	if (null != cursor) {
		if (cursor.moveToFirst()) {
			do {
				strs.add(cursor.getString(cursor.getColumnIndex(FLD_SM_TEMPLATES)));
//System.out.println(">>> " + cursor.getString(cursor.getColumnIndex(FLD_SM_TEMPLATES)));
			} while (cursor.moveToNext());
		}
	}
//	   return (String[]) strs.toArray();
	return strs.toArray(strs.toArray(new String[strs.size()]));
	}
	
	void addTemplate(final String str) {
		
		this.getWritableDatabase().execSQL(
				"INSERT INTO " + TABLE_VARIABLE
				+ " ( " + FLD_SM_TEMPLATES + " , " + FLD_MODIFIED_DATE + " ) "
				+ " VALUES ( '" + str + "', '" + new Date().toString() + "' )" 
				);
//			ContentValues cv=new ContentValues(); 
//			cv.put(FLD_SM_TEMPLATES, str);
//			cv.put(FLD_MODIFIED_DATE, new Date().toString());
//		    this.getWritableDatabase().insert(TABLE_UNIQUE,null, cv);
	}

	void deleteTemplate(final String oldStr) {
		this.getWritableDatabase()
			.delete(TABLE_VARIABLE, FLD_SM_TEMPLATES + " = '" + oldStr + "'", null);
	}
	
}










