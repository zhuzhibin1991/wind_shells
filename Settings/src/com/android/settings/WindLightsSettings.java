//hanweiwei@wind-mobi.com add for feature#102488 2016-04-13 begin
package com.android.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.provider.*;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.logging.MetricsLogger;

/**
 * Created by hanweiwei on 2016/4/13.
 */
public class WindLightsSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener,Preference.OnPreferenceClickListener{

    private SwitchPreference chargingPreference;
    private SwitchPreference lowBatteryPreference;
    private SwitchPreference misssedMessagePreference;

    private static final String KEY_WIND_CHARGING = "wind_charging";
    private static final String KEY_WIND_LOW_BATTERY = "wind_low_battery";
    private static final String KEY_WIND_MISSED_MESSAGE = "wind_missed_message";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addPreferencesFromResource(R.xml.wind_light_settings);

        chargingPreference = (SwitchPreference)findPreference(KEY_WIND_CHARGING);
        lowBatteryPreference = (SwitchPreference)findPreference(KEY_WIND_LOW_BATTERY);
        misssedMessagePreference = (SwitchPreference)findPreference(KEY_WIND_MISSED_MESSAGE);

        chargingPreference.setOnPreferenceChangeListener(this);
        lowBatteryPreference.setOnPreferenceChangeListener(this);
        misssedMessagePreference.setOnPreferenceChangeListener(this);

        chargingPreference.setOnPreferenceClickListener(this);
        lowBatteryPreference.setOnPreferenceClickListener(this);
        misssedMessagePreference.setOnPreferenceClickListener(this);

        chargingPreference.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.WIND_CHARGING, 1) == 1);
        lowBatteryPreference.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.WIND_LOW_BATTERY, 1) == 1);
        misssedMessagePreference.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.WIND_MISSED_MESSAGE, 1) == 1);

    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.ACCESSIBILITY;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if(preference == chargingPreference){
            chargingPreference.setChecked((Boolean) newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.WIND_CHARGING, (Boolean) newValue ? 1 : 0);
        }else if(preference == lowBatteryPreference){
            lowBatteryPreference.setChecked((Boolean)newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.WIND_LOW_BATTERY, (Boolean) newValue ? 1 : 0);
        }else if (preference == misssedMessagePreference){
            misssedMessagePreference.setChecked((Boolean)newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.WIND_MISSED_MESSAGE, (Boolean) newValue ? 1 : 0);
        }
        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }
}
//hanweiwei@wind-mobi.com add for feature#102488 2016-04-13 end
