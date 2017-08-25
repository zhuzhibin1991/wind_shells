package com.android.settings;

import android.os.SystemProperties;
import android.util.Log;

/**
 * Created by shengbotao@wind-mobi.com 20150120 for #96089.
 */
public class PhoneNameConfig {
    private static final String CHANGE_CONFIG = "persist.sys.phonename.change";
    private static final String NAME_CONFIG = "persist.sys.settings.phonename";
    /*lifeifei@wind-mobi.com 20150813 for #81009 start*/
    public static final String PNAME_CHANGED_ACTION = "phonename.change.action";
    /*lifeifei@wind-mobi.com 20150813 for #81009 end*/
    private static final String TAG = "PhoneNameConfig";
    private int mC;
    public static final int PHONE_NAME = 1;
    public static final int WIFI_NAME = 2;
    public static final int BT_NAME = 3;
    public static final int HOT_NAME = 4;

    public PhoneNameConfig() {
        mC = SystemProperties.getInt(CHANGE_CONFIG,0);
    }

    public void setPhoneName(String name){
        SystemProperties.set(NAME_CONFIG,name);
        setChanged(PHONE_NAME,true);
    }

    public String getPhoneName(){
        return SystemProperties.get(NAME_CONFIG);
    }

    public void setChanged(int what,boolean changed){
        if(changed){
            Log.d(TAG,"set changed what = " + what);
            switch(what){
                case PHONE_NAME:
                    mC |= 1;
                    break;
                case WIFI_NAME:
                    mC |= (1<<1);
                    break;
                case BT_NAME:
                    mC |= (1<<2);
                    break;
                case HOT_NAME:
                    mC |= (1<<3);
                    break;
            }
            SystemProperties.set(CHANGE_CONFIG,mC+"");
        }
    }

    public boolean getChanged(int what){
        int i = 0;
        switch (what){
            case PHONE_NAME:
                i = mC & 1;
                break;
            case WIFI_NAME:
                i = mC & (1 << 1);
                break;
            case BT_NAME:
                i = mC & (1 << 2);
                break;
            case HOT_NAME:
                i = mC & (1 << 3);
                break;
        }
        if(i != 0){
            Log.d(TAG,"getChanged what = " + what + ",return true");
            return true;
        }
        Log.d(TAG,"getChanged what = " + what + ",return false");
        return false;
    }


}
