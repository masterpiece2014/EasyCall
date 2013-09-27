package cai.bowen.easycall;

import java.util.Random;

import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.telephony.TelephonyManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.Context;
import android.content.Intent;

public class Act_Main extends Activity implements IConfigurable {

	public static final int ACT_CODE_CONFIG = 0;
	public static final int ACT_CODE_CHECK = 1;

	private boolean appEnabled;
	private int WALLPARER_NUM;
	private DataManager dataManager;
	String[] smTemplates;
	private SMSender smSender;
	private MyGestureListener gestureListener;
	private int currentBgIndex;
///////////////////////////////////////
	private Spinner selectSpn;
	private Button sendBtn;
	private Button callBtn;
	private Button cfgBtn;
	private EditText smEditor; // edit sm
	private TextView startCount;	// show start count

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_main);
		
        appEnabled = true;
		WALLPARER_NUM = getResources().getInteger(R.integer.wallpaper_num);
		DataManager.init(this);		// get context
		dataManager = DataManager.getInstance();
		dataManager.count(1);
		smTemplates = null;
    	smSender = null;
    	gestureListener = null;
    	currentBgIndex = R.drawable.wallpaper_0;
		
		checkThisPhone();// check imei and this phone number
		
		setUpCpmponents();
		
		((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(300); // OK, start!
    }// OnCreat

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
    	switch (requestCode) {
		case ACT_CODE_CHECK: // return from phone check
			if (Activity.RESULT_CANCELED == resultCode) {
				this.finish();
			} else if (Activity.RESULT_OK == resultCode){
				enableThisPhone();
			}
			break;
		case ACT_CODE_CONFIG: // from configuration activity
				this.updateSpinner();
				break;
		default:
			break;
		}
    }

	@Override
    public boolean onTouchEvent(MotionEvent event) {
		return gestureListener.getDetector().onTouchEvent(event); 
    }
	
    void setUpCpmponents() {
    	
    	currentBgIndex = new Random().nextInt(WALLPARER_NUM);
    	this.findViewById(android.R.id.content).setBackgroundResource(
    			dataManager.getPictureId(currentBgIndex));

		gestureListener = new MyGestureListener(this);
    	findViewById(android.R.id.content).setOnTouchListener(gestureListener);
    	
//		callBtn.setAlpha(0.75F);
		this.selectSpn = (Spinner)findViewById(R.id.spn_select_sms);
		this.sendBtn  =(Button)findViewById(R.id.btn_send_sms);		
		this.callBtn  =(Button)findViewById(R.id.btn_call);
		this.startCount = (TextView)findViewById(R.id.txt_start_count);
			startCount.setText(String.valueOf(
					dataManager.getCount()
						));
		this.smEditor  =(EditText)findViewById(R.id.txt_sms);

		cfgBtn = (Button)findViewById(R.id.btn_config);
		cfgBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Act_Main.this, Act_Config.class);
				Act_Main.this.startActivityForResult(intent, ACT_CODE_CONFIG);
			}
		});
		
    	if (appEnabled) {
    		enableThisPhone();
    	} else {
			this.selectSpn.setEnabled(false);
			this.callBtn.setEnabled(false);
			this.sendBtn.setEnabled(false);
			this.smEditor.setText(getString(R.string.txt_no_service));
			this.smEditor.setEnabled(false);
		}
    }
    private void enableThisPhone() {
    	
		this.selectSpn.setEnabled(true);
		this.callBtn.setEnabled(true);
		this.sendBtn.setEnabled(true);
		this.smEditor.setEnabled(true);
		smEditor.setHint(getString(R.string.txt_sms_hint));
    	
		smSender = new SMSender(this);
		this.updateSpinner();
		selectSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				final String text = Act_Main.this.smTemplates[position];
				Act_Main.this.smEditor.setText(text);
				Act_Main.this.smEditor.setSelection(text.length());
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		sendBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String address = dataManager.getTgtPhoneNumber();
				final String content = Act_Main.this.smEditor.getText().toString();
				if (content.length() > 0) {
					Act_Main.this.smEditor.setText(getResources().getString(R.string.txt_sms_hint));
					Act_Main.this.smSender.sendSMessage(address, content);
				}
			}
		});
		callBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_CALL);
				final String address = dataManager.getTgtPhoneNumber();
				intent.setData(Uri.parse("tel:" + address));
				Act_Main.this.startActivity(intent);
			}
		});
    }
    
	@Override
	public void updateSpinner() {
		this.smTemplates = dataManager.getTemplates();
		if (null == smTemplates && 0 == smTemplates.length) {
			final String msgNullStr = getString(R.string.txt_sm_temp_null);
			smEditor.setText(msgNullStr);
			smEditor.setSelection(msgNullStr.length());
			smTemplates = new String[]{getString(R.string.txt_sm_temp_null)};
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
								android.R.layout.simple_spinner_item,
								smTemplates);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		selectSpn.setAdapter(adapter);
	}
	
    @Override
    public int getCurrentBgIndex() {
		return currentBgIndex;
	}
	@Override
	public void switchToNext(int n) {
		if (0 < n && n < WALLPARER_NUM) {
			currentBgIndex += n;
		}
		currentBgIndex %= WALLPARER_NUM;
		this.findViewById(android.R.id.content).setBackgroundResource(
				dataManager.getPictureId(currentBgIndex));
	}

	@Override
	public void switchToPrev(int n) {
		if (0 < n && n < WALLPARER_NUM) {
			currentBgIndex -= n;
		}
		if (0 > currentBgIndex) {
			currentBgIndex = WALLPARER_NUM - 1;
		}
		this.findViewById(android.R.id.content).setBackgroundResource(
				dataManager.getPictureId(currentBgIndex));
	}

	public static final int PHONE_OK = 0;
	public static final int WRONG_IMEI = 1;
	public static final int WRONG_NUMBER = 2;
	public static final int NO_SIM_CARD = 3;
	
	void checkThisPhone() {
		TelephonyManager tm = 
				(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		
		String imei = tm.getDeviceId();
		String simPhoneNum = tm.getLine1Number();
		int problem = PHONE_OK;
		this.appEnabled = true;
		
		if (imei.hashCode() != dataManager.getThisIMEIHash()) {
			problem = WRONG_IMEI;// wrong imei , exit
			this.appEnabled = false;
		} else if (null == simPhoneNum ) {
			this.appEnabled = false;
			problem = NO_SIM_CARD;
		} else if ( !simPhoneNum.equals(dataManager.getThisPhoneNum())) {
			this.appEnabled = false;// this phone number changed, require reactivate
			problem = WRONG_NUMBER;
		} 
		
		if (0 != problem) {
			Intent intent = new Intent(Act_Main.this, Act_Check.class);

			intent.putExtra(Act_Check.ATT_PROBLEM, String.valueOf(problem));
			intent.putExtra(Act_Check.ATT_THIS_PHONE_NUM, simPhoneNum);

			this.startActivityForResult(intent, ACT_CODE_CHECK);
		}
	}
	
	@Override
	protected void onStop() {
		if (appEnabled) {
			smSender.stop();
		}
		super.onStop();
	}
}




