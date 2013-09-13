package cai.bowen.callmyson;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataManager extends SQLiteOpenHelper {
	
	private final int WALLPARER_NUM;
	private final String IMEI;

	private Context context = null;
	private final Class<R.drawable> res_;
	
	private DataManager(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
		res_ =  R.drawable.class;
		WALLPARER_NUM = context.getResources().getInteger(R.integer.wallpaper_num);
		IMEI = context.getString(R.string.phone_imei1);
	}
	
	private static DataManager class_handler = null;
	public static DataManager getInstance(final Context ct) {
		if (null == class_handler) {
			class_handler = new DataManager(ct);
		}
		return class_handler;
	}
	
///////////////////////////////////////////////////////////
	static final String DB_NAME;
	static final int DB_VERSION;
				// commen attribute of two tables
    static final String FLD_ID;
    //		// table to store unique data
	static final String TABLE_UNIQUE_VAL;
	
    static final String FLD_COUNT;
    static final String FLD_THIS_PHONE_NUM;
    static final String FLD_TGT_PHONE_NUM;
    			//Variable
	static final String TABLE_VARIABLE;
    static final String FLD_SM_TEMPLATES;
    static final String FLD_MODIFIED_DATE;
	

    static final String FLD_THIS_PHONE_IMEI;
    
	static {
		DB_NAME = "CallMySon_database";
		DB_VERSION = 20130913;
	    
		TABLE_UNIQUE_VAL = "Table_CallMySonConstant";
		FLD_ID = "_id";
	    FLD_COUNT = "COL_appStartCount";
	    FLD_THIS_PHONE_NUM = "COL_thisPhoneNumber";
	    FLD_THIS_PHONE_IMEI = "COL_thisPhoneIMEI";
	    FLD_TGT_PHONE_NUM = "COL_targetPhoneNumber";
	    
		TABLE_VARIABLE = "Table_CallMySonConstant";
	    FLD_SM_TEMPLATES = "COL_smTemplates";
	    FLD_MODIFIED_DATE = "COL_modifiedDate";
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
	        // TODO Auto-generated method stub
	        String statmentCreate = 
	        		"CREATE TABLE " + TABLE_UNIQUE_VAL 
	        		+ " ( "
	        			+ FLD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
	        			+ FLD_COUNT 			+ " INTEGER, "
	        			+ FLD_THIS_PHONE_NUM 	+ " TEXT, "
	        			+ FLD_THIS_PHONE_IMEI + " TEXT, "
	        			+ FLD_TGT_PHONE_NUM 	+ " TEXT "
	        		+ " ) ";
	        database.execSQL(statmentCreate);
	        statmentCreate = 
	        		"CREATE TABLE " + TABLE_VARIABLE 
	        		+ "(" 
	        			+ FLD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
	        			+ FLD_SM_TEMPLATES 	+ " TEXT, "
	        			+ FLD_MODIFIED_DATE 	+ " TEXT, "
	        		+ " )";
	        database.execSQL(statmentCreate);
	        
	        SQLiteDatabase myDatabase = this.getWritableDatabase();
	        ContentValues cv = new ContentValues(); 
	        cv.put(FLD_COUNT, 0);
	        cv.put(FLD_THIS_PHONE_NUM, this.context.getString(R.string.phone_num_mo));
	        cv.put(FLD_THIS_PHONE_IMEI, this.context.getString(R.string.phone_imei1));
	        cv.put(FLD_TGT_PHONE_NUM, this.context.getString(R.string.phone_num_son));
	        myDatabase.insert(DB_NAME, null, cv);
	        

	        final String initDate = new Date().toString();
	        final String[] smTemps = context.getResources().getStringArray(R.array.txt_sm_templates);
	        for(final String str : smTemps) {
		        cv = new ContentValues(); 
	        	cv.put(FLD_SM_TEMPLATES, str);
		        cv.put(FLD_MODIFIED_DATE, initDate);
		        myDatabase.insert(TABLE_VARIABLE, null, cv);
	        }
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = " DROP TABLE IF EXISTS " + TABLE_UNIQUE_VAL;
		db.execSQL(sql);
		sql = " DROP TABLE IF EXISTS " + TABLE_VARIABLE;
		db.execSQL(sql);
		onCreate(db);
	}
///////////////////////////////////////////////////////////
	int getRandomBackgroundID() {
		String wallpCode = new String(context.getString(R.string.name_wallpaper_perfix));
		wallpCode += String.valueOf(new Random().nextInt(WALLPARER_NUM));
		try {
			Field wallpField = res_.getField(wallpCode);
			return wallpField.getInt(wallpField);
		} catch (Exception e) {
//			e.printStackTrace();
//System.out.println(e.toString());
			return R.drawable.wallpaper_0;
		}
	}
	
	int getCount() {
		Cursor cursor = this.getReadableDatabase().query(
								TABLE_UNIQUE_VAL,
								new String[]{FLD_COUNT}, null, null, null, null, null);
System.out.println(">>>> " + cursor.getInt(0) + "  ===  " + cursor.getInt(1));
		return cursor.getInt(0);
	}
	
	void count(int i) {
		int current = getCount();
		current += i;
		this.getWritableDatabase().execSQL(
				"UPDATE " + TABLE_UNIQUE_VAL 
					+ " SET " + FLD_COUNT + " = " + current 
						+ " where " +  FLD_THIS_PHONE_IMEI + " = '" + IMEI + "'");
//		execSQL("UPDATE emptable set age="+ emp_age+ " where name='"+ emp_name +"'");
	}
	
	int getThisIMEIHash() {
		Cursor cursor = this.getReadableDatabase().query(
				TABLE_UNIQUE_VAL,
				new String[]{FLD_THIS_PHONE_IMEI}, null, null, null, null, null);
System.out.println(">>>> " + cursor.getString(0) + "  ===  " + cursor.getString(1) + " ==  " + cursor.getString(2));
		return cursor.getString(0).hashCode();
	}
	
	void setThisPhoneNum(final String newPhoneNum) {
		this.getWritableDatabase().execSQL(
				"UPDATE " + TABLE_UNIQUE_VAL 
					+ " SET " + FLD_THIS_PHONE_NUM + " = " + newPhoneNum 
						+ " where " +  FLD_THIS_PHONE_IMEI + " = '" + IMEI + "'");
	}
	
	final String getThisPhoneNum() {
		Cursor cursor = this.getReadableDatabase().query(
			TABLE_UNIQUE_VAL,
			new String[]{FLD_THIS_PHONE_NUM}, null, null, null, null, null);
System.out.println(">>>> " + cursor.getString(0) + "  ===  " + cursor.getString(1) + " ===  " + cursor.getString(2));
	return cursor.getString(0);
	}

	
	void setTgtPhoneNumber(final String num) {
		this.getWritableDatabase().execSQL(
				"UPDATE " + TABLE_UNIQUE_VAL
					+ " SET " + FLD_TGT_PHONE_NUM + " = " + num 
						+ " where " +  FLD_THIS_PHONE_IMEI + " = '" + IMEI + "'");
	}
	
	final String getTgtPhoneNumber() {		
		Cursor cursor = this.getReadableDatabase().query(
			TABLE_UNIQUE_VAL,
			new String[]{FLD_TGT_PHONE_NUM}, null, null, null, null, null);
System.out.println(">>>> " + cursor.getString(0) + "  ===  " + cursor.getString(1) + " ==  " + cursor.getString(2));
	return cursor.getString(0);
	}

	final String[] getTemplates() {
		Cursor cursor = this.getReadableDatabase().query(
				TABLE_VARIABLE,
				new String[]{FLD_SM_TEMPLATES}, null, null, null, null, null);
	System.out.println(">>>> " + cursor.getString(0) + "  ===  " + cursor.getString(1) + " ==  " + cursor.getString(2));
//		return cursor.getString(0);
//	String []str = new String[cursor.getCount()];
//	return str;
	ArrayList<String> strs = new ArrayList<String>();
	int i = 0;
	   while (!cursor.isAfterLast()) {
		   strs.add(cursor.getString(i));
		   i++;
		   cursor.moveToNext();
	   }
	   return (String[]) strs.toArray();
	}
	
	void addTemplate(final String str) {
		        ContentValues cv=new ContentValues(); 
		        cv.put(FLD_SM_TEMPLATES, str);
		        this.getWritableDatabase().insert(TABLE_UNIQUE_VAL,null, cv);
	}

	void deleteTemplate(final String oldStr) {
	}
	
}










