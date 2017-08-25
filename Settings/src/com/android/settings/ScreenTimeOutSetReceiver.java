package com.android.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
//liuxiaoshuan@wind-mobi.com 2016.08.05 start
import com.mediatek.settings.FeatureOption;
//liuxiaoshuan@wind-mobi.com 2016.08.05 end
/**
 * Created by zhangtianwen on 2016/4/26.
 * Bug #108863
 */
public class ScreenTimeOutSetReceiver extends BroadcastReceiver {
    private static String FIRST_BOOT_TIMEOUT_CHECK = "first_boot_time_check";
    private static final String SCREEN_OFF_TIMEOUT = "screen_off_timeout";
    private boolean firstBoot;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            firstBoot = android.provider.Settings.System.getInt(context.getContentResolver(), FIRST_BOOT_TIMEOUT_CHECK, 0) == 0;
            int timeoutValue = android.provider.Settings.System.getInt(context.getContentResolver(), SCREEN_OFF_TIMEOUT, 60000);
            if (firstBoot) {
                android.provider.Settings.System.putInt(context.getContentResolver(), FIRST_BOOT_TIMEOUT_CHECK, 1);
                if ((timeoutValue != 15000) && (timeoutValue != 30000) && (timeoutValue != 60000) && (timeoutValue != 120000) && (timeoutValue != 300000) && (timeoutValue != 600000) && (timeoutValue != 1800000)) {
                    //liuxiaoshuan@wind-mobi.com 2016.08.05 start
                    //wangshaohua@wind-mobi.com 2016.08.29 start
                    //fix Bug #127339 set sleep interval 30 seconds
                    if(FeatureOption.WIND_DEF_OPTR_E188F_IN || FeatureOption.WIND_DEF_OPTR_E183L_IN){
                        android.provider.Settings.System.putInt(context.getContentResolver(), SCREEN_OFF_TIMEOUT, 30000);
                    }else if(FeatureOption.WIND_DEF_OPTR_E183L_EUR){
                        android.provider.Settings.System.putInt(context.getContentResolver(), SCREEN_OFF_TIMEOUT, 15000);
                    }else{
                        android.provider.Settings.System.putInt(context.getContentResolver(), SCREEN_OFF_TIMEOUT, 60000);
                    }
                    //liuxiaoshuan@wind-mobi.com 2016.08.05 end
                    //wangshaohua@wind-mobi.com 2016.08.29 end
                }
            }
        }
    }
}
