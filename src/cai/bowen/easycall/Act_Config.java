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

public class Act_Config extends Activity implements IBackground {

	private EditText editor;
	private Button setPhoneBtn;
	private Button addSmTempBtn;

	private String[] smTemps;
	private String strToDel;
	private Spinner delSmTempSpn;
	private Button delSmTempBtn;
	private int currentBgId;	// flip to change wallpaper
	private MyGestureListener gestureListener;
	private DataManager dataManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ly_config);
		
		editor = (EditText)findViewById(R.id.txt_new);
		setPhoneBtn = (Button)findViewById(R.id.btn_set_phone);
		addSmTempBtn = (Button)findViewById(R.id.btn_new_sm_temp);
		delSmTempSpn = (Spinner)findViewById(R.id.spn_select_del_sms);
		delSmTempBtn = (Button)findViewById(R.id.btn_del_sm_temp);
		
		dataManager = DataManager.getInstance();

		gestureListener = new MyGestureListener(this);
		currentBgId = dataManager.getRandomBackgroundID();
		

		findViewById(id.content).setOnTouchListener(gestureListener);
		findViewById(id.content).setBackgroundResource(currentBgId);
		
		setPhoneBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String phone = editor.getText().toString();
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
				editor.setText("");
			}
		});
		
		addSmTempBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
//				DataManager_old.instance().addTemplate(
//						editor.getText().toString());
				dataManager.addTemplate(editor.getText().toString());
				
				editor.setText("");
				
				Toast.makeText(Act_Config.this, 
						Act_Config.this.getString(R.string.txt_finished),
						Toast.LENGTH_LONG).show();
			}
		});

		this.smTemps = dataManager.getTemplates();
		if (null == smTemps && 0 == smTemps.length) {
			smTemps = new String[]{getString(R.string.txt_sm_temp_null)};
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
								android.R.layout.simple_spinner_item,
								smTemps);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		delSmTempSpn.setAdapter(adapter);
		
		delSmTempSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Act_Config.this.strToDel = Act_Config.this.smTemps[position];
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});

		delSmTempBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				DataManager_old.instance().deleteTemplate(strToDel);
				dataManager.deleteTemplate(strToDel);
			}
		});
		
	}
	@Override
    public boolean onTouchEvent(MotionEvent event) {
        // or implement in activity or component. When your not assigning to a child component.
        return gestureListener.getDetector().onTouchEvent(event); 
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
}






