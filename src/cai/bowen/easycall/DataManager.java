package cai.bowen.easycall;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;


public class DataManager {

	static final String PREFER_NAME;
	static final String FL_DATA_NAME;
//	static final String FL_TEMP_NAME;
	static final String STR_DELI;
	
	static final String ATT_THIS_PHONE_NUM;
	static final String ATT_TGT_PHONE_NUM;
	static final String ATT_COUNT;
	static final String ATT_THIS_PHONE_IMEI;
	static final int WALLPARER_NUM;
	static {
		PREFER_NAME = "CallMySon_Pref";
		FL_DATA_NAME = "CallMySon_Data";
//		FL_TEMP_NAME = "CallMySon_Temp";
		STR_DELI = "###";
		
		ATT_THIS_PHONE_NUM = "this_phone_number";
		ATT_TGT_PHONE_NUM = "target_phone_to_call";
		ATT_COUNT = "app_start_count";
		ATT_THIS_PHONE_IMEI = "this_phone_imei_hash";
		 // 78 pictures, from "wallpaper_0.jpg to "wallpaper_77.jpg"
		WALLPARER_NUM = 78;// int "new Random(78).nextInt()", 78 is exclusive.
	}
	
	private final Class<R.drawable> res_;
	private final Context context;
	
	private SharedPreferences shPref_;
	private SharedPreferences.Editor editor_;
	
	
	private static DataManager class_handler = null;
	public static void init(final Context ct) {
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
	private DataManager(final Context act) {
		this.context = act;
		this.res_ = R.drawable.class;
		shPref_ = context.getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);  
		editor_ = shPref_.edit();
		long count = shPref_.getLong(ATT_COUNT, 0L);
		if (0L == count) {

			this.setThisPhoneNum(context.getString(R.string.phone_num_mo));
			editor_.putString(ATT_THIS_PHONE_IMEI, context.getString(R.string.phone_imei1));
			
			editor_.putString(ATT_TGT_PHONE_NUM, context.getString(R.string.phone_num_son));

			final String[] smTemps = context.getResources().getStringArray(
										R.array.txt_sm_templates);
			for (final String str : smTemps) {
				addTemplate(str);
			}

			editor_.putString("Author", context.getString(R.string.app_author));
			editor_.putString("First Start Time", new Date().toString());
			
			editor_.commit();
		}
	}

	
	void count(long i) {
		long count = shPref_.getLong(ATT_COUNT, 0L);
		count += i;
		editor_.putLong(ATT_COUNT, count);
		editor_.commit();
	}
	
	long getCount() {
		return shPref_.getLong(ATT_COUNT, 0);
	}
	
	int getThisIMEIHash() {
		return shPref_.getString(ATT_THIS_PHONE_IMEI, "").hashCode();
	}
	
	void setThisPhoneNum(final String this_phone_num) {
		editor_.putString(ATT_THIS_PHONE_NUM, this_phone_num);
		editor_.commit();
	}
	final String getThisPhoneNum() {
		return shPref_.getString(ATT_THIS_PHONE_NUM, "");
	}
	
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
	
	void setTgtPhoneNumber(final String num) {
		editor_.putString(ATT_TGT_PHONE_NUM, num);
		editor_.commit();
	}
	
	final String getTgtPhoneNumber() {
		return shPref_.getString(ATT_TGT_PHONE_NUM,
				context.getString(R.string.phone_num_son));
	}

	final String[] getTemplates() {
        String buf = new String();
        StringBuilder strBuilder = new StringBuilder();
		    try {
		        InputStream inStrm = context.openFileInput(FL_DATA_NAME);
		        if ( inStrm != null ) {
		            InputStreamReader inStrmReader = new InputStreamReader(inStrm);
		            BufferedReader bufReader = new BufferedReader(inStrmReader);

		            while ( (buf = bufReader.readLine()) != null ) {
//System.out.println(">> readLine: " + inStr);
		                strBuilder.append(buf);
		            }
		            inStrm.close();
		        }
		    }
		    catch (FileNotFoundException e) {
		        System.out.println("File not found: " + e.toString());
		    } catch (IOException e) {
		        System.out.println("Can not read file: " + e.toString());
		    }
		    return strBuilder.toString().split(STR_DELI);
	}
	
	void addTemplate(final String str) {
	    try {
	        OutputStreamWriter outStrmWriter = 
	        		new OutputStreamWriter(
	        				context.openFileOutput(FL_DATA_NAME, Context.MODE_APPEND)
	        				);
	        outStrmWriter.append(str);
//System.out.println(">>> adding " + str);
	        outStrmWriter.append(STR_DELI);
	        outStrmWriter.close();
	    }
	    catch (IOException e) {
	        System.out.println("Exception"+ "File write failed: " + e.toString());
	    } 
	}

	void deleteTemplate(final String oldStr) {
//System.out.println(">>> deleting " + oldStr);
//        String buf = new String();
//		    try {
//		        InputStream inStrm = parent_.openFileInput(FL_DATA_NAME);
//		        if ( inStrm != null ) {
//		            InputStreamReader inStrmReader = new InputStreamReader(inStrm);
//		            BufferedReader bufReader = new BufferedReader(inStrmReader);
//		            
//			        OutputStreamWriter outStrmWriter = 
//			        		new OutputStreamWriter(
//			        				parent_.openFileOutput(FL_TEMP_NAME, Context.MODE_PRIVATE)
//			        				);
//			        
//		            while ( (buf = bufReader.readLine()) != null ) {
//		            	if (oldStr.equals(buf)) {
//		            		continue;
//		            	} else {
//							outStrmWriter.append(buf);
//						}
//		            }
//		            inStrm.close();
//			        outStrmWriter.close();
////			        File oldFile = parent_.getDir(FL_DATA_NAME, Context.MODE_PRIVATE);
////			        oldFile.delete();
////			        File tempFile = parent_.(FL_TEMP_NAME, Context.MODE_PRIVATE);
////			        tempFile.renameTo(oldFile);
//		        }
//		    }
//		    catch (FileNotFoundException e) {
//		        System.out.println("File not found: " + e.toString());
//		    } catch (IOException e) {
//		        System.out.println("Can not read file: " + e.toString());
//		    }
	}
	
}
