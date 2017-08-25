package com.android.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.preference.Preference;
/*lifeifei@wind-mobi.com 20150813 for #81009 end*/
import android.content.Intent;
/*lifeifei@wind-mobi.com 20150813 for #81009 end*/

import com.mediatek.wifi.Utf8ByteLengthFilter;

/*added by huyapeng@wind-mobi.com for #85976 begin*/
import android.text.TextUtils;
/*added by huyapeng@wind-mobi.com for #85976 end*/
/**
 * Created by shengbotao@wind-mobi.com 20150120 for #96089.
 */
public class PhoneNameDialogFragment extends DialogFragment implements TextWatcher{
    private static final String TAG = "PhoneNameDialogFragment";
    private static final int MAX_LENGTH = 32;
    private AlertDialog mAlertDialog;
    private EditText mDeviceNameView;
    private Preference prf;
    private Button mOkButton;
    PhoneNameConfig pc = new PhoneNameConfig();
    private String deviceName = pc.getPhoneName();

    /* lifeifei@wind-mobi.com 20150803 for #80349 start */
    public PhoneNameDialogFragment() {
    }
    /* lifeifei@wind-mobi.com 20150803 for #80349 end */

    public PhoneNameDialogFragment(Preference prf) {
        this.prf = prf;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mAlertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.rename_phone_name)
                .setView(createDialogView(deviceName))
                .setPositiveButton(R.string.phone_rename_button,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deviceName = mDeviceNameView.getText().toString();
                                setDeviceName(deviceName);
                            }
                        })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        return mAlertDialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        mOkButton = mAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
		/*added by huyapeng@wind-mobi.com for #85976 begin*/
		if(!TextUtils.isEmpty(mDeviceNameView.getText().toString()))
        { mOkButton.setEnabled(true);}
        else{ mOkButton.setEnabled(false);}
		/*added by huyapeng@wind-mobi.com for #85976 end*/
	
    }

    private View createDialogView(String deviceName) {
        final LayoutInflater layoutInflater = (LayoutInflater)getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.dialog_edittext, null);
        mDeviceNameView = (EditText) view.findViewById(R.id.edittext);
        mDeviceNameView.setFilters(new InputFilter[] {
                new Utf8ByteLengthFilter(MAX_LENGTH){
                }
        });
        mDeviceNameView.setText(deviceName);    // set initial value before adding listener
        mDeviceNameView.addTextChangedListener(this);
        mDeviceNameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
				/* modified by huyapeng@wind-mobi.com for #85976 begin */
                    //setDeviceName(v.getText().toString());
                    //mAlertDialog.dismiss();
                    //return true;    // action handled
				   if(TextUtils.isEmpty(v.getText().toString())){
				   return false;}
				   else{
				    setDeviceName(v.getText().toString());
                    mAlertDialog.dismiss();
                    return true;    // action handled
				   }
				  
			    /* modified by huyapeng@wind-mobi.com for #85976  end */
                } else {
                    return false;   // not handled
                 }				
            }
        });
        return view;
    }

    private void setDeviceName(String deviceName) {
        Log.d(TAG, "Setting device name to " + deviceName);
        pc.setPhoneName(deviceName);
        /*lifeifei@wind-mobi.com 20150813 for #81009 start*/
        sendBroadcast();
        //prf.setSummary(deviceName);
        /*lifeifei@wind-mobi.com 20150813 for #81009 end*/
        if(!pc.getChanged(PhoneNameConfig.PHONE_NAME)){
            Log.d(TAG,"set phoneName changed");
            pc.setChanged(PhoneNameConfig.PHONE_NAME,true);
        }
        setHotspotName(deviceName);
    }

    /*lifeifei@wind-mobi.com 20150813 for #81009 start*/
    private void sendBroadcast() {
        Intent intent = new Intent();
        intent.setAction(PhoneNameConfig.PNAME_CHANGED_ACTION);
        getActivity().sendBroadcast(intent);
    }
    /*lifeifei@wind-mobi.com 20150813 for #81009 end*/

    private void setHotspotName(String name) {
        if(pc.getChanged(PhoneNameConfig.HOT_NAME)){
            Log.d(TAG,"hotspot name already changed,return");
            return;
        }
        WifiManager wifiManager =
                (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int state = wifiManager.getWifiApState();
        if(state == WifiManager.WIFI_AP_STATE_ENABLED){
            Log.d(TAG,"hotspot on,return");
            return;
        }
        WifiManager mWifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration mWifiConfig = mWifiManager.getWifiApConfiguration();
        mWifiConfig.SSID = name;
        mWifiManager.setWifiApConfiguration(mWifiConfig);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
         
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mOkButton != null) {
            // huangyaling@wind-mobi.com modify for bug#99618 begin
            // mOkButton.setEnabled(s.length() != 0);
            String str = mDeviceNameView.getText().toString().replace(" ", "");
            mOkButton.setEnabled(str.length() != 0);
           // huangyaling@wind-mobi.com modify for bug#99618 end
        }
    }
}
