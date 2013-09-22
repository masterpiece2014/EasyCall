package cai.bowen.easycall;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class MyGestureListener extends SimpleOnGestureListener implements OnTouchListener {

	private Activity activity;
	private GestureDetector gestureDetector;
	
	MyGestureListener(final Activity ct) {
		gestureDetector = new GestureDetector(ct, this);
		this.activity = ct;
	}
	@Override
	public void onLongPress(MotionEvent arg0) {
		new AlertDialog.Builder(activity)
		.setMessage(activity.getString(R.string.txt_set_wallpaper))
		.setNegativeButton(activity.getString(R.string.txt_cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
					}
				})
		.setPositiveButton(activity.getString(R.string.txt_ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						try {
							WallpaperManager
							.getInstance(activity)
								.setResource(
										((IConfigurable)activity).getCurrentBgIndex());
						} catch (IOException e) {
							Log.e("onLongPress WallpaperManager", e.toString());
						}
						Toast.makeText(activity, 
								activity.getString(R.string.txt_finished),
								Toast.LENGTH_LONG).show();
					}
				}).show();
	}// long press

	@Override
	public boolean onFling(MotionEvent me1, MotionEvent me2, float vX, float vY) {
		
		float dist_X = me2.getX() - me1.getX();
		float dist_Y = me2.getY() - me1.getY();
		if (dist_Y > 120F) {
			((IConfigurable)activity).switchToNext(10);
		} else if (dist_Y < -120F) {
			((IConfigurable)activity).switchToPrev(10);
		}
		else if (dist_X > 100F) {
			((IConfigurable)activity).switchToNext(1);
		} else if (dist_X < -100F) {
			((IConfigurable)activity).switchToPrev(1);
		}
//			int currentBackground = mainAct.getCurrentBackgroundID();
//			int nextBackground = DataManager.getInstance().getRandomBackgroundID();
//			android.animation.ValueAnimator backgroundanim = 
//					android.animation.ValueAnimator
//						.ofObject(new ArgbEvaluator(), currentBackground, nextBackground);
//			
//			backgroundanim.addUpdateListener(new AnimatorUpdateListener() {
//				
//				@Override
//				public void onAnimationUpdate(ValueAnimator animation) {
//					// TODO Auto-generated method stub
//			    	mainAct.findViewById(android.R.id.content)
//			    	.setBackgroundResource(
//			    			DataManager.getInstance().getRandomBackgroundID());
//				}
//			});
//			Integer colorFrom = getResources().getColor(R.color.red);
//			Integer colorTo = getResources().getColor(R.color.blue);
//			ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
//			colorAnimation.addUpdateListener(new AnimatorUpdateListener() {
//
//			    @Override
//			    public void onAnimationUpdate(ValueAnimator animator) {
//			        textView.setBackgroundColor((Integer)animator.getAnimatedValue());
//			    }
//
//			});
//			colorAnimation.start();
		return false;
	}
    
	@Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }


	public GestureDetector getDetector() {
		return this.gestureDetector;
	}
}