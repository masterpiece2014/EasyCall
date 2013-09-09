package cai.bowen.callmyson;

import java.io.IOException;

import android.os.Bundle;
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
import android.app.WallpaperManager;
import android.content.DialogInterface;

public class Act_Config extends Activity {

	private EditText editor;
	private Button setPhoneBtn;
	private Button addSmTempBtn;

	private String[] smTemps;
	private String strToDel;
	private Spinner delSmTempSpn;
	private Button delSmTempBtn;
	private int currentWallpaper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ly_confg);
		
		editor = (EditText)findViewById(R.id.txt_new);
		setPhoneBtn = (Button)findViewById(R.id.btn_set_phone);
		addSmTempBtn = (Button)findViewById(R.id.btn_new_sm_temp);
		delSmTempSpn = (Spinner)findViewById(R.id.spn_select_del_sms);
		delSmTempBtn = (Button)findViewById(R.id.btn_del_sm_temp);
		
		currentWallpaper = ContentManager.instance().getRandomBackgroundID();
		View currentView = this.findViewById(id.content);
		currentView.setBackgroundResource(currentWallpaper);
		currentView.setLongClickable(true);
		currentView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				new AlertDialog.Builder(Act_Config.this)
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
											.getInstance(Act_Config.this)
												.setResource(
														Act_Config.this.currentWallpaper);
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										Toast.makeText(Act_Config.this, 
												Act_Config.this.getString(R.string.txt_finished),
												Toast.LENGTH_LONG).show();
									}
								}).show();
				return false;
			}
		});
		
		setPhoneBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String phone = editor.getText().toString();
				if (phone.matches("(\\d{11})|(\\+\\d{13})")) {
					
					ContentManager.instance().setTgtPhoneNumber(phone);
					
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
				
				ContentManager.instance().addSmTemplate(
						editor.getText().toString());
				
				editor.setText("");
				
				Toast.makeText(Act_Config.this, 
						Act_Config.this.getString(R.string.txt_finished),
						Toast.LENGTH_LONG).show();
			}
		});

		this.smTemps = ContentManager.instance().getSmTemplates();

		if (null == smTemps && 0 == smTemps.length) {
			smTemps = new String[]{getString(R.string.txt_sm_template1)};
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
				ContentManager.instance().deleteSmTemplate(strToDel);
			}
		});
		
	}

//	delSmTempBtn
}






