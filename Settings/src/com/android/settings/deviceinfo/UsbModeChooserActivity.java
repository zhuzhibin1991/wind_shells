/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.deviceinfo;

import android.annotation.Nullable;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
/*added by liuxiaoshuan@wind-mobi.com 2016.3.22 for bug 102178 begin*/
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
/*added by liuxiaoshuan@wind-mobi.com 2016.3.22 for bug 102178 end*/
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.settings.R;
/* zhangheting@wind-mobi.com remove MIDI 20160506 start */
import com.mediatek.settings.FeatureOption;
/* zhangheting@wind-mobi.com remove MIDI 20160506 end */
/**
 * UI for the USB chooser dialog.
 *
 */
public class UsbModeChooserActivity extends Activity {

    public static final int[] DEFAULT_MODES = {
        UsbBackend.MODE_POWER_SINK | UsbBackend.MODE_DATA_NONE,
        UsbBackend.MODE_POWER_SOURCE | UsbBackend.MODE_DATA_NONE,
        UsbBackend.MODE_POWER_SINK | UsbBackend.MODE_DATA_MTP,
        UsbBackend.MODE_POWER_SINK | UsbBackend.MODE_DATA_PTP,
        UsbBackend.MODE_POWER_SINK | UsbBackend.MODE_DATA_MIDI,
        /// M: Add for Built-in CD-ROM and USB Mass Storage @{
        UsbBackend.MODE_POWER_SINK | UsbBackend.MODE_DATA_MASS_STORAGE,
        UsbBackend.MODE_POWER_SINK | UsbBackend.MODE_DATA_BICR
        /// M: @}
    };
    //hanweiwei@wind-mobi.com add 2016.08.10 start
    public static final int[] DEFAULT_MODES_NO_MASS_STORAGE = {
        UsbBackend.MODE_POWER_SINK | UsbBackend.MODE_DATA_NONE,
        UsbBackend.MODE_POWER_SOURCE | UsbBackend.MODE_DATA_NONE,
        UsbBackend.MODE_POWER_SINK | UsbBackend.MODE_DATA_MTP,
        UsbBackend.MODE_POWER_SINK | UsbBackend.MODE_DATA_PTP,
        UsbBackend.MODE_POWER_SINK | UsbBackend.MODE_DATA_MIDI,
        UsbBackend.MODE_POWER_SINK | UsbBackend.MODE_DATA_BICR
        /// M: @}
    };
    //hanweiwei@wind-mobi.com add 2016.08.10 end
    /* zhangheting@wind-mobi.com remove MIDI 20160506 start */
    //hanweiwei@wind-mobi.com mod rename DEFAULT_MODES_NO_MASS_STORAGE remove MASS_STORAGE
    public static final int[] DEFAULT_MODES_NO_MASS_STORAGE_NO_MIDI = {
        UsbBackend.MODE_POWER_SINK | UsbBackend.MODE_DATA_NONE,
        UsbBackend.MODE_POWER_SOURCE | UsbBackend.MODE_DATA_NONE,
        UsbBackend.MODE_POWER_SINK | UsbBackend.MODE_DATA_MTP,
        UsbBackend.MODE_POWER_SINK | UsbBackend.MODE_DATA_PTP,
        UsbBackend.MODE_POWER_SINK | UsbBackend.MODE_DATA_BICR
        /// M: @}
    };
    /* zhangheting@wind-mobi.com remove MIDI 20160506 end */
    private UsbBackend mBackend;
    private AlertDialog mDialog;
    private LayoutInflater mLayoutInflater;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mLayoutInflater = LayoutInflater.from(this);

        mDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.usb_use)
                .setView(R.layout.usb_dialog_container)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create();
        mDialog.show();

        LinearLayout container = (LinearLayout) mDialog.findViewById(R.id.container);

        mBackend = new UsbBackend(this);
        int current = mBackend.getCurrentMode();
        /* zhangheting@wind-mobi.com remove MIDI 20160506 start */
        //hanweiwei@wind-mobi.com add WIND_DEF_PRO_E188F
        //fix Feature #129242 for delete MIDI
        if(FeatureOption.WIND_DEF_OPTR_E183L_TEL || FeatureOption.WIND_DEF_PRO_E188F
                || FeatureOption.WIND_DEF_OPTR_E169F_RUS || FeatureOption.WIND_DEF_PRO_E181L){
          for (int i = 0; i < DEFAULT_MODES_NO_MASS_STORAGE_NO_MIDI.length; i++) {
              if (mBackend.isModeSupported(DEFAULT_MODES_NO_MASS_STORAGE_NO_MIDI[i])) {
                  inflateOption(DEFAULT_MODES_NO_MASS_STORAGE_NO_MIDI[i], current == DEFAULT_MODES_NO_MASS_STORAGE_NO_MIDI[i], container);
              }
          }
        //hanweiwei@wind-mobi.com add 2016.08.10 start
        }else if (FeatureOption.WIND_DEF_PRO_E183L || FeatureOption.WIND_DEF_PRO_E169F){
          for (int i = 0; i < DEFAULT_MODES_NO_MASS_STORAGE.length; i++) {
              if (mBackend.isModeSupported(DEFAULT_MODES_NO_MASS_STORAGE[i])) {
                  inflateOption(DEFAULT_MODES_NO_MASS_STORAGE[i], current == DEFAULT_MODES_NO_MASS_STORAGE[i], container);
              }
          }
        //hanweiwei@wind-mobi.com add 2016.08.10 end
        }else{
          for (int i = 0; i < DEFAULT_MODES.length; i++) {
              if (mBackend.isModeSupported(DEFAULT_MODES[i])) {
                  inflateOption(DEFAULT_MODES[i], current == DEFAULT_MODES[i], container);
              }
          }
        }
        /* zhangheting@wind-mobi.com remove MIDI 20160506 end */
/*added by liuxiaoshuan@wind-mobi.com 2016.3.22 for bug 102178 begin*/
        IntentFilter filter=new IntentFilter();
        filter.addAction("android.hardware.usb.action.USB_STATE");
        registerReceiver(mBroadcastReceiver, filter);
/*added by liuxiaoshuan@wind-mobi.com 2016.3.22 for bug 102178 end*/
    }
/*added by liuxiaoshuan@wind-mobi.com 2016.3.22 for bug 102178 begin*/
    BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("android.hardware.usb.action.USB_STATE")){
                              if (intent.getExtras().getBoolean("connected")){

                            }else{
                             UsbModeChooserActivity.this.finish();
                               }
                        }

         }

    };
/*added by liuxiaoshuan@wind-mobi.com 2016.3.22 for bug 102178 end*/
    private void inflateOption(final int mode, boolean selected, LinearLayout container) {
        View v = mLayoutInflater.inflate(R.layout.radio_with_summary, container, false);

        ((TextView) v.findViewById(android.R.id.title)).setText(getTitle(mode));
        ((TextView) v.findViewById(android.R.id.summary)).setText(getSummary(mode));

        v.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ActivityManager.isUserAMonkey()) {
                    mBackend.setMode(mode);
                }
                mDialog.dismiss();
                finish();
            }
        });
        ((Checkable) v).setChecked(selected);
        container.addView(v);
    }

    private static int getSummary(int mode) {
        switch (mode) {
            case UsbBackend.MODE_POWER_SINK | UsbBackend.MODE_DATA_NONE:
                return R.string.usb_use_charging_only_desc;
            case UsbBackend.MODE_POWER_SOURCE | UsbBackend.MODE_DATA_NONE:
                return R.string.usb_use_power_only_desc;
            case UsbBackend.MODE_POWER_SINK | UsbBackend.MODE_DATA_MTP:
                return R.string.usb_use_file_transfers_desc;
            case UsbBackend.MODE_POWER_SINK | UsbBackend.MODE_DATA_PTP:
                return R.string.usb_use_photo_transfers_desc;
            case UsbBackend.MODE_POWER_SINK | UsbBackend.MODE_DATA_MIDI:
                return R.string.usb_use_MIDI_desc;
            /// M: Add for Built-in CD-ROM and USB Mass Storage @{
            case UsbBackend.MODE_POWER_SINK | UsbBackend.MODE_DATA_MASS_STORAGE:
                return R.string.usb_ums_summary;
            case UsbBackend.MODE_POWER_SINK | UsbBackend.MODE_DATA_BICR:
                return R.string.usb_bicr_summary;
            /// M: @}
        }
        return 0;
    }

    private static int getTitle(int mode) {
        switch (mode) {
            case UsbBackend.MODE_POWER_SINK | UsbBackend.MODE_DATA_NONE:
                return R.string.usb_use_charging_only;
            case UsbBackend.MODE_POWER_SOURCE | UsbBackend.MODE_DATA_NONE:
                return R.string.usb_use_power_only;
            case UsbBackend.MODE_POWER_SINK | UsbBackend.MODE_DATA_MTP:
                return R.string.usb_use_file_transfers;
            case UsbBackend.MODE_POWER_SINK | UsbBackend.MODE_DATA_PTP:
                return R.string.usb_use_photo_transfers;
            case UsbBackend.MODE_POWER_SINK | UsbBackend.MODE_DATA_MIDI:
                return R.string.usb_use_MIDI;
            /// M: Add for Built-in CD-ROM and USB Mass Storage @{
            case UsbBackend.MODE_POWER_SINK | UsbBackend.MODE_DATA_MASS_STORAGE:
                return R.string.usb_use_mass_storage;
            case UsbBackend.MODE_POWER_SINK | UsbBackend.MODE_DATA_BICR:
                return R.string.usb_use_built_in_cd_rom;
            /// M: @}
        }
        return 0;
    }
/*added by liuxiaoshuan@wind-mobi.com 2016.3.22 for bug 102178 begin*/
    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
/*added by liuxiaoshuan@wind-mobi.com 2016.3.22 for bug 102178 end*/
}
