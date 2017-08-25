/*
 * Bug #99570 lijingwei@wind-mobi.com 2016/3/9 start
 */
package com.android.settings;

import com.mediatek.settings.FeatureOption;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.mediatek.settings.FeatureOption;
import android.os.SystemProperties;
import android.util.Log;

/**
 * This receiver catches when quick settings turns off the hotspot, so we can
 * cancel the alarm in that case.  All other cancels are handled in tethersettings.
 */
public class SetSilentReceiver extends BroadcastReceiver {
	private static final String TAG = "SetSilentReceiver";
	private static final String SILENT_ACTION="android.silent.CHANGED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (FeatureOption.WIND_DEF_PRO_E183L){
            if(SILENT_ACTION.equals(intent.getAction())){
            	String value = intent.getStringExtra("android.silent.VALUE");
                SystemProperties.set("persist.sys.silent", value);
                Log.d(TAG,"setSilentValue="+value);
            }
        }
    }
}
/*
 * Bug #99570 lijingwei@wind-mobi.com 2016/3/9 end
 */