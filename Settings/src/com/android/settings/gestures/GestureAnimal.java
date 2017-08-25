//add by liuqiong@wind-mobi.com 2016-02-18 begin

package com.android.settings.gestures;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import com.android.settings.R;
import android.widget.ImageView;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Vibrator;
import android.provider.Settings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.PowerManager;
import android.app.Service;
import android.util.Log;

public class GestureAnimal extends Activity {
    private ImageView mImageView;
    private AnimationDrawable mAnimationDrawable;
    private Vibrator mVibrator;
    private PowerManager pm;
    private GesturesReceiver mGestureRec;

    private static GestureAnimal mGestureAnimal = null;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        setContentView(R.layout.gesture_animal);
        mVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        mImageView = (ImageView)findViewById(R.id.gesture_animal_bg);
        pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        mGestureRec = (GesturesReceiver)getIntent().getSerializableExtra("gesture_obj");
        mGestureAnimal = this;
    }

    private int getAnimDuration() {
        int duration = 0;
        String mode = getIntent().getStringExtra("gesture_mode");
        Log.d("JerryLiu", "getAnimDuration mode = " + mode);
        switch(mode) {
            case GesturesSettings.KEY_GESTURE_C:
                mImageView.setBackgroundResource(R.anim.gesture_c);
                break;
            case GesturesSettings.KEY_GESTURE_M:
                mImageView.setBackgroundResource(R.anim.gesture_m);
                break;
            case GesturesSettings.KEY_GESTURE_O:
                mImageView.setBackgroundResource(R.anim.gesture_o);
                break;
            case GesturesSettings.KEY_GESTURE_W:
                mImageView.setBackgroundResource(R.anim.gesture_w);
                break;
            default:
                mImageView.setBackgroundResource(R.anim.gesture_c);
                break;
        }
        
        mAnimationDrawable = (AnimationDrawable)mImageView.getBackground();  

        for (int i = 0; i < mAnimationDrawable.getNumberOfFrames(); i++) {
            duration += mAnimationDrawable.getDuration(i);
        }

        return duration;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGestureAnimal = null;
    }

    public static class GestureAnimalReceiver extends BroadcastReceiver {
        @Override 
        public void onReceive(Context context, Intent intent) { 
            String action = intent.getAction();
            Log.d("JerryLiu", "GestureAnimalReceiver action = " + action);
            if (action.equals("com.android.intent.gestures_animal_start")) {
                Log.d("JerryLiu", "GestureAnimalReceiver action");
                if (mGestureAnimal != null) {
                    new Handler(){}.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mGestureAnimal != null) {
                                mGestureAnimal.mAnimationDrawable.stop();
                                mGestureAnimal.mAnimationDrawable = null;
                                mGestureAnimal.mVibrator.cancel();
                                mGestureAnimal.sendBroadcast(new Intent("com.android.intent.gestures_animal"));
                                mGestureAnimal.finish();
                            }
                        }
                    }, mGestureAnimal.getAnimDuration());
                    mGestureAnimal.mAnimationDrawable.start();
                    if (Settings.System.getInt(mGestureAnimal.getContentResolver(), Settings.System.HAPTIC_FEEDBACK_ENABLED, 0) == 1) {
                        mGestureAnimal.mVibrator.vibrate(3000);
                    }  
                }
            }
        }
    }
}
//add by liuqiong@wind-mobi.com 2016-02-18 end

