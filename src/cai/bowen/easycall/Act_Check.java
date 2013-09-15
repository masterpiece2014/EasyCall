package cai.bowen.easycall;

import java.math.BigInteger;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class Act_Check extends Activity {
	
//	static final int RET_AC;
//	static final int RET_REJ;
//	static final String RET_CODE;
	static final String ATT_PROBLEM;
	static final String ATT_THIS_PHONE_NUM;
	static {
//		RET_AC = 1;
//		RET_REJ = 0;
//		RET_CODE = "returnFromAct_Check";
		ATT_PROBLEM = "Problem";
		ATT_THIS_PHONE_NUM = "Phone Number Hash";
	}
	private Button enterBtn;
	private Button exitBtn;
	private TextView warntxt;
	private EditText editText;
	
	private int problem;
	private String newPhoneNum;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ly_check);
		
		enterBtn = (Button)findViewById(R.id.btn_check_reactivate);
		exitBtn = (Button)findViewById(R.id.btn_check_exit);
		editText = (EditText)findViewById(R.id.txt_check_reactivate);
		warntxt = (TextView)findViewById(R.id.txt_check_show);
		
		warntxt.setText(getString(R.string.txt_phone_num_changed));
		
		exitBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				Intent intent = new Intent();
//				intent.putExtra(RET_CODE, RET_REJ);
//				Act_Check.this.setResult(RESULT_CANCELED, intent);
				Act_Check.this.setResult(RESULT_CANCELED);
				Act_Check.this.finish();
			}
		});
		
		problem = Integer.valueOf(getIntent().getStringExtra(ATT_PROBLEM));
		newPhoneNum = getIntent().getStringExtra(ATT_THIS_PHONE_NUM);

		// wrong IMEI, just quit
		if (-1 == problem) {
			new AlertDialog.Builder(this)
		    .setMessage(getString(R.string.txt_wrong_phone))
		    .setNegativeButton(getString(R.string.txt_exit), 
		    						new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
//					Act_Check.this.setResult(Act_Check.RET_REJ);
		        	Act_Check.this.setResult(RESULT_CANCELED);
					Act_Check.this.finish();
		        }
		     })
		     .show();
		} else { // incorrect phone number, reactivate required
			enterBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (isValidKey(editText.getText().toString())) {

						DataManager.getInstance().setThisPhoneNum(Act_Check.this.newPhoneNum);
						
						Toast.makeText(Act_Check.this, 
								Act_Check.this.getString(R.string.txt_activated),
								Toast.LENGTH_LONG).show();

//						Act_Check.this.setResult(Act_Check.RET_AC);
						Act_Check.this.setResult(RESULT_OK);
						Act_Check.this.finish();
					} else {
						new AlertDialog.Builder(Act_Check.this)
					    .setMessage(getString(R.string.txt_activate_fail))
					    .setNegativeButton(getString(R.string.txt_exit), 
					    						new DialogInterface.OnClickListener() {
					        public void onClick(DialogInterface dialog, int which) {
//								Act_Check.this.setResult(Act_Check.RET_REJ);
					        	Act_Check.this.setResult(RESULT_CANCELED);
								Act_Check.this.finish();
					        }
					     })
					     .show();
					}
				}
			});
		}
	}
	@Override
	public void onBackPressed() {
	}

	public static boolean isValidKey(final String key) {
		BigInteger bigInt = new BigInteger(key);
		long upper = 931000L;
		long lower = 131000L;
		return (1 == bigInt.compareTo(BigInteger.valueOf(lower))
				&& -1 == bigInt.compareTo(BigInteger.valueOf(upper)))
				&& (key.startsWith("0337") || key.endsWith("0337"))
				&& bigInt.isProbablePrime(1000);
	}
}









