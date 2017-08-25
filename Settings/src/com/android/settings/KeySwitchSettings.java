package com.android.settings;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.preference.Preference;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceGroup;
import java.util.ArrayList;
import android.view.View.OnClickListener;
import android.content.pm.ActivityInfo;
import android.os.SystemProperties;
import android.widget.TextView;
import com.android.internal.logging.MetricsLogger;
//add by xushuhao@wind-mobi.com 20160508 start
import com.mediatek.settings.FeatureOption;
//add by xushuhao@wind-mobi.com 20160508 end
/* add by lijingwei@wind-mobi.com 20160119 for Feature#96017 */
public class KeySwitchSettings extends SettingsPreferenceFragment{
    private static final String TAG = "KeySwitchSettings";
    ImageView back_home_menu,menu_home_back;	
    TextView layout_one_textView, layout_two_textView;    
    private PreferenceGroup mPrefGroup;
    private ArrayList<KeySwitchPreference> mPrefs;
    private boolean WIND_CONFIG = SystemProperties.get("persist.sys.menu.config").equals("1");
	
    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.APPLICATION;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.keyswitch_selection);
        mPrefGroup = (PreferenceGroup) findPreference("keyswitch");
        showImageList();
        Context context = getActivity();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //setLayoutResource(R.layout.key_switch);
    }
    
    @Override
    public void onResume() {
        super.onResume();
    }

    private void showImageList(){
        mPrefGroup.removeAll();
        mPrefs = new ArrayList<KeySwitchPreference>();
        KeySwitchPreference pref;
        Context context = getActivity();
        pref = new KeySwitchPreference(context);
        mPrefs.add(pref);
        mPrefGroup.addPreference(pref);
    }
    
    private class KeySwitchPreference extends Preference {
        public KeySwitchPreference(Context context) {
                super(context);
//add by xushuhao@wind-mobi.com 20160508 start				
                //liuxiaoshuan@wind-mobi.com 20160708 start
                //add HARDKEY KEYSWITCH_NOTITLE
                if(FeatureOption.WIND_DEF_OPTR_E183L_TEL || FeatureOption.WIND_DEF_OPTR_E183L_THA || FeatureOption.WIND_KEYSWITCH_NOTITLE )
                //liuxiaoshuan@wind-mobi.com 20160708 end
                {
					setLayoutResource(R.layout.key_switch_no_tittle);
                }
                else
                {
					setLayoutResource(R.layout.key_switch);
                }
//add by xushuhao@wind-mobi.com 20160508 start
        }
        @Override
        protected void onBindView(View view) {
            super.onBindView(view);
            back_home_menu = (ImageView) view.findViewById(R.id.back_home_menu);
            menu_home_back = (ImageView) view.findViewById(R.id.menu_home_back);
            back_home_menu.setOnClickListener(mHomeClickListener);
            menu_home_back.setOnClickListener(mHomeClickListener);
            if(WIND_CONFIG){
                back_home_menu.setImageResource(R.drawable.back_menu_g);
                menu_home_back.setImageResource(R.drawable.menu_back_h);
            }else{
                back_home_menu.setImageResource(R.drawable.back_menu_h);
                menu_home_back.setImageResource(R.drawable.menu_back_g);
            }
        }
    }
    OnClickListener mHomeClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.menu_home_back) {
                back_home_menu.setImageResource(R.drawable.back_menu_g);
                menu_home_back.setImageResource(R.drawable.menu_back_h);
                SystemProperties.set("persist.sys.menu.config","1");
            }else if (v.getId() == R.id.back_home_menu) {
                back_home_menu.setImageResource(R.drawable.back_menu_h);
                menu_home_back.setImageResource(R.drawable.menu_back_g);
                SystemProperties.set("persist.sys.menu.config","0");
            }
        }
    };
}
/* add by lijingwei@wind-mobi.com 20160119 for Feature#96017 */