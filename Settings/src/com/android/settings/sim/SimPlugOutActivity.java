/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.android.settings.sim;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.android.settings.R;
import android.util.Log;
/*xushuhao@wind-mobi.com 20160506  start*/
import com.mediatek.settings.FeatureOption;
/*xushuhao@wind-mobi.com 20160506  end*/

public class SimPlugOutActivity extends Activity {
    private static String TAG = "SimPlugOutActivity";
    private Dialog mDialog;
    /* add by zhangwenjuan01@wind-mobi.com 2016.3.17 for bug#101014 begin */
    public static SimPlugOutActivity instance = null;
    /* add by zhangwenjuan01@wind-mobi.com 2016.3.17 for bug#101014 end*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* add by zhangwenjuan01@wind-mobi.com 2016.3.17 for bug#101014 begin */
        instance=this;
        /* add by zhangwenjuan01@wind-mobi.com 2016.3.17 for bug#101014 end */
        mDialog = createDialog(this);
        //xushuhao@wind-mobi.com 20160506 modify start
        if(!FeatureOption.WIND_DEF_OPTR_E183L_TEL)
        {
       		mDialog.show();
        }
        //xushuhao@wind-mobi.com 20160506 modify start
        Log.d(TAG, "onReceive()... onCreate ");
    }
    
    private Dialog createDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.wind_sim_missing_card);
        builder.setMessage(R.string.wind_sim_missing_card_detail);
        /*modify by zhangwenjuan01@wind-mobi.com 20160315 for bug#1010101 begin*/
        builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialog, int which){
                SimPlugOutActivity.this.finish();
            }
        });
        /*modify by zhangwenjuan01@wind-mobi.com 20160315 for bug#1010101 end*/
        Dialog dialog = builder.create();
        return dialog;
    }

    @Override
    protected void onDestroy() {
        // M: for AlPS02113443,when activity finish early and dialog dismiss later, view not to
        // attach to window manager. so, when activity finish, dismiss dialog.
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
        super.onDestroy();
    }
}
