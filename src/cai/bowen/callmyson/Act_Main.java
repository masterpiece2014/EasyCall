package cai.bowen.callmyson;

import java.io.IOException;

import android.R.id;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class Act_Main extends Activity {

	public static final int ACT_CODE_CONFIG = 0;
	public static final int ACT_CODE_CHECK = 1;
	private SMSender smSender;
///////////////////////////////////////
	private Spinner selectSpn;
	private Button sendBtn;
	private Button callBtn;
	private Button cfgBtn;
	private EditText contentEditor;
	private TextView startCount;
	private String[] smTemps;
	private View currentView;
	private int currentWallpaper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_main);
		
		smSender = new SMSender(this);
		ContentManager.init(this);
		ContentManager.instance().count(1);
		
		setupUi();
		checkThisPhone();
		((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(1000);
//////////////////////////////////////////////////////////////////////////////
		currentView.setLongClickable(true);
		currentView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				new AlertDialog.Builder(Act_Main.this)
						.setMessage(getString(R.string.txt_set_wallpaper))
						.setNegativeButton(getString(R.string.txt_cancel),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								})
						.setPositiveButton(getString(R.string.txt_ok),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										try {
											WallpaperManager
											.getInstance(Act_Main.this)
												.setResource(
													Act_Main.this.currentWallpaper);
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										Toast.makeText(Act_Main.this, 
												Act_Main.this.getString(R.string.txt_finished),
												Toast.LENGTH_LONG).show();
									}
								}).show();
				return false;
			}
		});
		
		
		startCount.setText(String.valueOf(
				ContentManager.instance().getCount()
			));
		
		selectSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Act_Main.this.contentEditor.setText(Act_Main.this.smTemps[position]);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		sendBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String address = ContentManager.instance().getTgtPhoneNumber();
				final String content = Act_Main.this.contentEditor.getText().toString();
				if (content.length() > 0) {
					Act_Main.this.contentEditor.setText(getResources().getString(R.string.txt_sms_hint));
					Act_Main.this.smSender.sendSMessage(address, content);
				}
			}
		});
		callBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_CALL);
				final String address = ContentManager.instance().getTgtPhoneNumber();
				intent.setData(Uri.parse("tel:" + address));
				Act_Main.this.startActivity(intent);
			}
		});
		cfgBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Act_Main.this, Act_Config.class);
				Act_Main.this.startActivityForResult(intent, ACT_CODE_CONFIG);
			}
		});
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
    	switch (requestCode) {
		case ACT_CODE_CHECK:
//			int retCode = data.getIntExtra(Act_Check.RET_CODE, Act_Check.RET_REJ);
//			if (Act_Check.RET_REJ == retCode) {
//				this.finish();
//			}
			if (Activity.RESULT_CANCELED == resultCode) {
				this.finish();
			}
			break;
		case ACT_CODE_CONFIG:
				this.updateSipnner();
				break;
		default:
			break;
		}
    }
    
    void setupUi() {
    	currentView = findViewById(id.content);
    	currentWallpaper = ContentManager.instance().getRandomBackgroundID();
//		callBtn.setAlpha(0.75F);
		this.currentView.setBackgroundResource(currentWallpaper);
		
		this.selectSpn = (Spinner)findViewById(R.id.spn_select_sms);
		this.sendBtn  =(Button)findViewById(R.id.btn_send_sms);		
		this.callBtn  =(Button)findViewById(R.id.btn_call);
		this.cfgBtn = (Button)findViewById(R.id.btn_config);
		this.startCount = (TextView)findViewById(R.id.txt_start_count);
		this.contentEditor  =(EditText)findViewById(R.id.txt_sms);
		
		this.updateSipnner();
    }
    
    void updateSipnner() {
    	
		contentEditor.setText(getString(R.string.txt_sms_hint));

		this.smTemps = ContentManager.instance().getSmTemplates();
//System.out.println(">>> " + smTemps.length);
//for(String string : smTemps) {
//	System.out.println("=====>>> " + string);
//}
		if (null == smTemps && 0 == smTemps.length) {
			contentEditor.setText(getString(R.string.txt_sm_temp_null));
			smTemps = new String[]{getString(R.string.txt_sm_template1)};
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
								android.R.layout.simple_spinner_item,
								smTemps);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		selectSpn.setAdapter(adapter);
    }
    
	void checkThisPhone() {
		
		TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();
		String phoneNum = tm.getLine1Number();

		int problem = 0;
		if (imei.hashCode() != ContentManager.instance().getThisIMEIHash()) {
			problem = -1;
		} else if ( !phoneNum.equals(ContentManager.instance().getThisPhoneNum())) {
			problem = 1;
		}
		
		if (0 != problem) {
			Intent intent = new Intent(Act_Main.this, Act_Check.class);

			intent.putExtra(Act_Check.ATT_PROBLEM, String.valueOf(problem));
			intent.putExtra(Act_Check.ATT_THIS_PHONE_NUM, phoneNum);

			this.startActivityForResult(intent, ACT_CODE_CHECK);
		}
	}
	
}




