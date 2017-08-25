package com.android.settings;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.UserHandle;
import android.util.Log;

import com.mediatek.settings.FeatureOption;


/**
 * Created by shengbotao@wind-mobi.com 20150120 for #96089.
 */
public class PhoneNameReceiver extends BroadcastReceiver {
    private static final String TAG = "PhoneNameReceiver";
    private String myPhoneName;
    private Context mContext;
    private PhoneNameConfig pc;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(!FeatureOption.WIND_PHONE_NAME_SUPPORT){
            return;
        }
        mContext = context;
        pc = new PhoneNameConfig();
        String action = intent.getAction();
        myPhoneName = pc.getPhoneName();
        Log.v(TAG, "myPhoneName = " + myPhoneName + ", action = " + action );
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)){
            if(!pc.getChanged(PhoneNameConfig.PHONE_NAME)){
                if(myPhoneName.equals("")){
                    myPhoneName = Build.MODEL;
                }
                /* xushuhao@wind-mobi.com 20160525 start */
                if(FeatureOption.WIND_DEF_PRO_E169F_RUS)
                {
                /* lifeifei@wind-mobi.com 20150815 for #78541 start */
                int rad = (int)(Math.random()*9000 + 1000);
                myPhoneName = myPhoneName + "_" + rad;
                /* lifeifei@wind-mobi.com 20150815 for #78541 end */
                }
                /* xushuhao@wind-mobi.com 20160525 end */
                pc.setPhoneName(myPhoneName);
            }
            setWifiP2pName(myPhoneName);
            setHotspotName(myPhoneName);
            setBluetoothName(myPhoneName);
        }else if(action.equals("android.net.wifi.WIFI_STATE_CHANGED")){
            setWifiP2pName(myPhoneName);
        }else if(action.equals("android.bluetooth.adapter.action.STATE_CHANGED")){
            setBluetoothName(myPhoneName);
        }else if(action.equals("android.net.wifi.WIFI_AP_STATE_CHANGED")){
            /*WifiManager mWifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if(mWifiManager.getWifiApState() == WifiManager.WIFI_AP_STATE_DISABLED) {
                setHotspotName(myPhoneName);
            }*/
        }
    }

    private void setBluetoothName(String name) {
        if(pc.getChanged(PhoneNameConfig.BT_NAME)){
            Log.d(TAG,"bt name already changed,return");
            return;
        }
        BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
        mAdapter.setName(name);
    }

    private void setHotspotName(String name) {
        if(pc.getChanged(PhoneNameConfig.HOT_NAME)){
            Log.d(TAG,"hotspot name already changed,return");
            return;
        }
        WifiManager mWifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration mWifiConfig = mWifiManager.getWifiApConfiguration();
        mWifiConfig.SSID = name;
        mWifiManager.setWifiApConfiguration(mWifiConfig);
    }

    private void setWifiP2pName(final String name) {
        if(pc.getChanged(PhoneNameConfig.WIFI_NAME)){
            Log.d(TAG,"wifi name already changed,return");
            return;
        }
        WifiP2pManager mWifiP2pManager = (WifiP2pManager) mContext.getSystemService(Context.WIFI_P2P_SERVICE);
        WifiP2pManager.Channel mChannel = mWifiP2pManager.initialize(mContext, mContext.getMainLooper(), null);
        mWifiP2pManager.setDeviceName(mChannel, name, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.v(TAG, "set wifiP2p success");
                //sendThisDeviceChangedBroadcast(name);
            }

            @Override
            public void onFailure(int reason) {
                Log.e(TAG, "set wifiP2p failure");
            }
        });
    }

    /*private void sendThisDeviceChangedBroadcast(String deviceName) {
        final Intent intent = new Intent(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY_BEFORE_BOOT);
        intent.putExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE, new WifiP2pDevice(deviceName));
        mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
    }*/
}
