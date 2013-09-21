package cai.bowen.easycall;

import java.util.Random;

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


	private int WALLPARER_NUM;
	private EditText smTempEditor;
	private Button addSmTempBtn;

	private String strToDel;
	private Spinner delSmTempSpn;
	private Button delSmTempBtn;
	private Button restoreBtn;
	
	private Button setPhoneBtn;
	private EditText phoneNumEditor;
	
	private int currentBgIndex;
	String[] smTemplates;
	
	private MyGestureListener gestureListener;
	private DataManager dataManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ly_config);
		

		WALLPARER_NUM = getResources().getInteger(R.integer.wallpaper_num);
		dataManager = DataManager.getInstance();
		
	   	currentBgIndex = new Random().nextInt(WALLPARER_NUM);
	   	
    	this.findViewById(android.R.id.content).setBackgroundResource(
    			dataManager.getPictureId(currentBgIndex));

		gestureListener = new MyGestureListener(this);
		findViewById(id.content).setOnTouchListener(gestureListener);
		
		smTempEditor = (EditText)findViewById(R.id.txt_new_sm_temp);
		addSmTempBtn = (Button)findViewById(R.id.btn_new_sm_temp);
		delSmTempSpn = (Spinner)findViewById(R.id.spn_select_del_sms);
		delSmTempBtn = (Button)findViewById(R.id.btn_del_sm_temp);

		phoneNumEditor = (EditText)findViewById(R.id.txt_new_phone_num);
		phoneNumEditor.setHint(dataManager.getTgtPhoneNumber());
		setPhoneBtn = (Button)findViewById(R.id.btn_set_phone);
		setPhoneBtn.setHint(dataManager.getTgtPhoneNumber());
		restoreBtn = (Button)findViewById(R.id.btn_restore);
		
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
		
		restoreBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(Act_Config.this)
			    .setMessage(getString(R.string.txt_restore_ask))
			    .setPositiveButton(getString(R.string.txt_is_ok), 
			    		new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Act_Config.this.dataManager.restore();
						Act_Config.this.updateSpinner();
						Toast.makeText(Act_Config.this, 
								Act_Config.this.getString(R.string.txt_finished),
								Toast.LENGTH_LONG).show();
					}
				})
			    .setNegativeButton(getString(R.string.txt_cancel), 
			    						new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			        }
			     })
			     .show();
			}
		});
	} // onCreat
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
		return gestureListener.getDetector().onTouchEvent(event); 
    }

    @Override
    public int getCurrentBgIndex() {
		return currentBgIndex;
	}

	@Override
	public void switchToNextBg() {
		currentBgIndex++;
		currentBgIndex %= (WALLPARER_NUM);
		this.findViewById(android.R.id.content).setBackgroundResource(
				dataManager.getPictureId(currentBgIndex));
	}

	@Override
	public void switchToPrevBg() {
		currentBgIndex--;
		if (0 > currentBgIndex) {
			currentBgIndex = WALLPARER_NUM;
		}
		this.findViewById(android.R.id.content).setBackgroundResource(
				dataManager.getPictureId(currentBgIndex));
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






