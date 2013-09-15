package cai.bowen.easycall;

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

//public class Act_Main extends Activity implements OnTouchListener, OnGestureListener{

public class Act_Main extends Activity implements IBackground {

	public static final int ACT_CODE_CONFIG = 0;
	public static final int ACT_CODE_CHECK = 1;
	private SMSender smSender;
///////////////////////////////////////
	private Spinner selectSpn;
	private Button sendBtn;
	private Button callBtn;
	private Button cfgBtn;
	private EditText contentEditor; // edit sm
	private TextView startCount;	// show start count
	private String[] smTemps;		// sm templates

	int currentBgId;	// flip to change wallpaper
	private MyGestureListener gestureListener;
	
	private DataManager dataManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_main);
		
		smSender = new SMSender(this);
//		detector.setIsLongpressEnabled(true);
		gestureListener = new MyGestureListener(this);
		
		DataManager.init(this);		// get context
		DataManager.getInstance().count(1);
		dataManager = DataManager.getInstance();
		
		setupUi();
		checkThisPhone();// check imei and this phone number
		
		((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(1000); // OK, start!
//////////////////////////////////////////////////////////////////////////////				
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
				final String address = dataManager.getTgtPhoneNumber();
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
				final String address = dataManager.getTgtPhoneNumber();
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
    }// OnCreat

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
    	switch (requestCode) {
		case ACT_CODE_CHECK: // return from phone check
			if (Activity.RESULT_CANCELED == resultCode) {
				this.finish();
			}
			break;
		case ACT_CODE_CONFIG: // from configuration activity
				this.updateSipnner();
				break;
		default:
			break;
		}
    }

	@Override
    public boolean onTouchEvent(MotionEvent event) {
        // or implement in activity or component. When your not assigning to a child component.
        return gestureListener.getDetector().onTouchEvent(event); 
    }
	
    void setupUi() {
    	updateBackground();
    	findViewById(android.R.id.content).setOnTouchListener(gestureListener);
//		callBtn.setAlpha(0.75F);
		this.selectSpn = (Spinner)findViewById(R.id.spn_select_sms);
		this.sendBtn  =(Button)findViewById(R.id.btn_send_sms);		
		this.callBtn  =(Button)findViewById(R.id.btn_call);
		this.cfgBtn = (Button)findViewById(R.id.btn_config);
		this.startCount = (TextView)findViewById(R.id.txt_start_count);
			startCount.setText(String.valueOf(
					dataManager.getCount()
						));
		this.contentEditor  =(EditText)findViewById(R.id.txt_sms);
		
		this.updateSipnner();
    }
    @Override
    public int getCurrentBackgroundID() {
		return currentBgId;
	}
    @Override
    public void updateBackground() {
    	this.currentBgId = dataManager.getRandomBackgroundID();
    	this.findViewById(android.R.id.content)
    			.setBackgroundResource(currentBgId);
	}
    
	// repaint spinner
    void updateSipnner() {
		contentEditor.setText(getString(R.string.txt_sms_hint));

		this.smTemps = dataManager.getTemplates();
		if (null == smTemps && 0 == smTemps.length) {
			contentEditor.setText(getString(R.string.txt_sm_temp_null));
			smTemps = new String[]{getString(R.string.txt_sm_temp_null)};
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

		if (imei.hashCode() != dataManager.getThisIMEIHash()) {
			// imei error, will exit
			problem = -1;
		} else if ( !phoneNum.equals(dataManager.getThisPhoneNum())) {
			// this phone number changed, require reactivate
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




