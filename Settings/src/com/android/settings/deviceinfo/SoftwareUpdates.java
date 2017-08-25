/*
********************************************************************
****ochusuxia@wind-mobi.com add for SDCARD update 20160122 start****
********************************************************************
*/
package com.android.settings.deviceinfo;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.android.settings.R;
import com.android.settings.Utils;

import android.content.Context;
import android.os.PowerManager;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import android.content.res.Resources;
import com.mediatek.settings.FeatureOption;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.app.Activity;
import android.content.pm.PackageManager;

public class SoftwareUpdates extends PreferenceActivity {
    private static final String TAG = "SoftwareUpdates";
	
	private static final String KEY_SDCARD_UPDATE = "sdcard_updates";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.software_updates);
        /*chusuxia@wind-mobi.com add 20160215 start*/
        if(FeatureOption.WIND_DEF_SUPPORT_ZTE_FOTA){
            Preference preference = findPreference("system_update_settings");
            if (preference != null) {
            getPreferenceScreen().removePreference(preference);
            }
        }else{
            Preference preference = findPreference("ztefota_software_update");
            if (preference != null) {
            getPreferenceScreen().removePreference(preference);
            }
        }
        /*chusuxia@wind-mobi.com add 20160215 end*/
    }
}
/*
********************************************************************
*****chusuxia@wind-mobi.com add for SDCARD update 20160122 end*****
********************************************************************
*/
