package cai.bowen.easycall;

import java.util.List;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.widget.Toast;

public class SMSender {
	private static SmsManager smsManager_;
	static {
		smsManager_ = SmsManager.getDefault();	
	}
	private final String SM_SENT;
	private final String SM_DELIVERED;

	private final Context context;
	private final PendingIntent sendPIntent_;
	private final PendingIntent deliveredPIntent_;
	
	public SMSender(final Context obj) {
		
		this.context = obj;	

		SM_DELIVERED = context.getResources().getString(R.string.txt_sms_delivered);
		SM_SENT = context.getResources().getString(R.string.txt_sms_sent);
		
		sendPIntent_ = PendingIntent.getBroadcast(context, 0, new Intent(
				SM_SENT), 0);

		deliveredPIntent_ = PendingIntent.getBroadcast(context, 0, new Intent(
			SM_DELIVERED), 0);
	}

	public void sendSMessage(final String phoneNumber, final String message) {
		context.registerReceiver(new BroadcastReceiver() {
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(context, SM_SENT,
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(context, 
							context.getString(R.string.txt_general_failure),
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(context,
							context.getString(R.string.txt_no_service),
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Toast.makeText(context, 
							context.getString(R.string.txt_null_pdu),
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Toast.makeText(context, 
							context.getString(R.string.txt_radio_off),
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(SM_SENT));

		context.registerReceiver(new BroadcastReceiver() {
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(context, SM_DELIVERED,
							Toast.LENGTH_SHORT).show();
					break;
				case Activity.RESULT_CANCELED:
					Toast.makeText(context,
							context.getResources().getString(R.string.txt_general_failure),
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(SM_DELIVERED));
		
		List<String>   contentList = smsManager_.divideMessage(message);
		for (String str : contentList) {
			SMSender.smsManager_.sendTextMessage(phoneNumber, null, str,
				sendPIntent_, deliveredPIntent_);
		}
		Toast.makeText(context, context.getString(R.string.txt_sms_sent), Toast.LENGTH_LONG).show();
	}
}





