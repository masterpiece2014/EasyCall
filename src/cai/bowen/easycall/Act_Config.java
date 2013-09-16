package cai.bowen.easycall;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.R.id;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class Act_Config extends Activity implements IConfigurable {

	private EditText smTempEditor;
	private Button addSmTempBtn;

	private String strToDel;
	private Spinner delSmTempSpn;
	private Button delSmTempBtn;
	
	private Button setPhoneBtn;
	private EditText phoneNumEditor;
	
	int currentBackgroundID;
	String[] smTemplates;
	
	private MyGestureListener gestureListener;
	private DataManager dataManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ly_config);
		
		dataManager = DataManager.getInstance();
		int currentBackgroundID = dataManager.getRandomBackgroundID();
		gestureListener = new MyGestureListener(this);
		
		smTempEditor = (EditText)findViewById(R.id.txt_new_sm_temp);
		addSmTempBtn = (Button)findViewById(R.id.btn_new_sm_temp);
		delSmTempSpn = (Spinner)findViewById(R.id.spn_select_del_sms);
		delSmTempBtn = (Button)findViewById(R.id.btn_del_sm_temp);

		phoneNumEditor = (EditText)findViewById(R.id.txt_new_phone_num);
		phoneNumEditor.setHint(dataManager.getTgtPhoneNumber());
		setPhoneBtn = (Button)findViewById(R.id.btn_set_phone);
		

		findViewById(id.content).setOnTouchListener(gestureListener);
		findViewById(id.content).setBackgroundResource(currentBackgroundID);
		
		updateSpinner();
///////////////////////////////////////////////////////////
		addSmTempBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				dataManager.addTemplate(smTempEditor.getText().toString());
				smTempEditor.setText("");
				Act_Config.this.updateSpinner();
				Toast.makeText(Act_Config.this, 
						Act_Config.this.getString(R.string.txt_finished),
						Toast.LENGTH_LONG).show();
			}
		});
		
		delSmTempSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {				
				Act_Config.this.strToDel = Act_Config.this.smTemplates[position];
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});

		delSmTempBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				dataManager.deleteTemplate(strToDel);				
				Act_Config.this.updateSpinner();				
				Toast.makeText(Act_Config.this, 
						Act_Config.this.getString(R.string.txt_finished),
						Toast.LENGTH_LONG).show();
			}
		});
		
		setPhoneBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String phone = phoneNumEditor.getText().toString();
				if (phone.matches("(\\d{11})|(\\+\\d{13})")) {
					dataManager.setTgtPhoneNumber(phone);
					Toast.makeText(Act_Config.this, 
							Act_Config.this.getString(R.string.txt_finished),
							Toast.LENGTH_LONG).show();
				} else {
					new AlertDialog.Builder(Act_Config.this)
				    .setMessage(getString(R.string.txt_illegal_phone))
				    .setNegativeButton(getString(R.string.txt_confirm), 
				    						new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) { 
				        }
				     })
				     .show();
				}
				smTempEditor.setText("");
			}
		});
	} // onCreat
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
		return gestureListener.getDetector().onTouchEvent(event); 
    }

    @Override
    public int getCurrentBackgroundID() {
		return currentBackgroundID;
	}
    @Override
    public void switchBackground() {
    	this.currentBackgroundID = dataManager.getRandomBackgroundID();
    	this.findViewById(android.R.id.content)
    			.setBackgroundResource(currentBackgroundID);
	}
	@Override
	public void updateSpinner() {
		this.smTemplates = dataManager.getTemplates();
		if (null == smTemplates && 0 == smTemplates.length) {
			smTemplates = new String[]{getString(R.string.txt_sm_temp_null)};
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
								android.R.layout.simple_spinner_item,
								smTemplates);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		delSmTempSpn.setAdapter(adapter);
	}
}






