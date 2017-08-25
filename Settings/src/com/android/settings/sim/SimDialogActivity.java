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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.settings.R;
import com.mediatek.internal.telephony.DefaultSmsSimSettings;
/// M: Add for CT 6M.
import com.mediatek.settings.FeatureOption;
import com.mediatek.settings.UtilsExt;
import com.mediatek.settings.cdma.CdmaUtils;
import com.mediatek.settings.ext.IRCSSettings;
import com.mediatek.settings.ext.ISettingsMiscExt;
import com.mediatek.settings.ext.ISimManagementExt;
import com.mediatek.settings.sim.SimHotSwapHandler;
import com.mediatek.settings.sim.SimHotSwapHandler.OnSimHotSwapListener;
import com.mediatek.settings.sim.TelephonyUtils;
//add by liuqiong@wind-mobi.com 2016-03-01 begin
import android.os.Handler;
//add by liuqiong@wind-mobi.com 2016-03-01 end

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SimDialogActivity extends Activity {
    private static String TAG = "SimDialogActivity";

    public static String PREFERRED_SIM = "preferred_sim";
    public static String DIALOG_TYPE_KEY = "dialog_type";
    public static final int INVALID_PICK = -1;
    public static final int DATA_PICK = 0;
    public static final int CALLS_PICK = 1;
    public static final int SMS_PICK = 2;
    public static final int PREFERRED_PICK = 3;
    //lijingwei@wind-mobi.com Bug #105601 2016/4/7 start
    private static boolean isSetDataOk = false;
    //lijingwei@wind-mobi.com Bug #105601 2016/4/7 end
    //liuxiaoshuan@wind-mobi.com 2016/8/9 start
    //fix sim displayName don't update
    private boolean needUpdateDisplayName;
    private boolean isDataPick;
    private List<SubscriptionInfo> mSubscriptionInfos ;
    //liuxiaoshuan@wind-mobi.com 2016/8/9 end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle extras = getIntent().getExtras();

        /// M: [SIM Hot Swap]
        setSimStateCheck();

        /// M: for plug-in @{
        mSimManagementExt = UtilsExt.getSimManagmentExtPlugin(getApplicationContext());
        mMiscExt = UtilsExt.getMiscPlugin(getApplicationContext());
        mRCSExt = UtilsExt.getRcsSettingsPlugin(getApplicationContext());
        /// @}


        final int dialogType = extras.getInt(DIALOG_TYPE_KEY, INVALID_PICK);

        switch (dialogType) {
            case DATA_PICK:
            case CALLS_PICK:
            case SMS_PICK:
                /// M: for AlPS02113443, avoid window leak @{
                // createDialog(this, dialogType).show();
                mDialog = createDialog(this, dialogType);
                mDialog.show();
                break;
            case PREFERRED_PICK:
                /// M: for ALPS02423087, hot plug timing issue, the sub list may already changed @{
                List<SubscriptionInfo> subs = SubscriptionManager.from(this)
                        .getActiveSubscriptionInfoList();
                //wangshaohua@wind-mobil.com modified start 20160824
                if (subs == null || subs.size() != 1 ) {
                    Log.w(TAG, "Subscription count is not 1, skip preferred SIM dialog");
                    finish();
                    return;
                }
                if(FeatureOption.WIND_DEF_OPTR_E183L_EUR){
                    final SubscriptionInfo sirInfor = SubscriptionManager.from(this)
                             .getActiveSubscriptionInfoForSimSlotIndex(extras.getInt(PREFERRED_SIM));
                    final int subIdInfor = sirInfor.getSubscriptionId();
                    PhoneAccountHandle phoneAccountHandle =
                            subscriptionIdToPhoneAccountHandle(subIdInfor);
                    setDefaultDataSubId(this, subIdInfor);
                    setDefaultSmsSubId(this, subIdInfor);
                    setUserSelectedOutgoingPhoneAccount(phoneAccountHandle);
                    finish();
                    return;
                }
                //wangshaohua@wind-mobil.com modified end 20160824
                /// @}
                displayPreferredDialog(extras.getInt(PREFERRED_SIM));
                break;
            default:
                throw new IllegalArgumentException("Invalid dialog type " + dialogType + " sent.");
        }
    }

    private void displayPreferredDialog(final int slotId) {
        final Resources res = getResources();
        final Context context = getApplicationContext();
        final SubscriptionInfo sir = SubscriptionManager.from(context)
                .getActiveSubscriptionInfoForSimSlotIndex(slotId);

        if (sir != null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            /// M: for Plug-in @{
            /*
            alertDialogBuilder.setTitle(R.string.sim_preferred_title);
            alertDialogBuilder.setMessage(res.getString(
                        R.string.sim_preferred_message, sir.getDisplayName()));
            */
            int subId = SubscriptionManager.getSubIdUsingPhoneId(slotId);
            String title = mMiscExt.customizeSimDisplayString(res
                    .getString(R.string.sim_preferred_title), subId);
            //chengyonghui@wind-mobi.com modify 20160418 start
	    //hanweiwei@wind-mobi.com add FeatureOption.WIND_DEF_OPTR_E188F_RUS
            String message = null;
            if(FeatureOption.WIND_DEF_OPTR_E181L_RUS || FeatureOption.WIND_DEF_OPTR_E183L_RUS || FeatureOption.WIND_DEF_OPTR_E188F_RUS || FeatureOption.WIND_DEF_OPTR_E169F_RUS){
                String tmp = res.getString(R.string.sim_preferred_message, sir.getDisplayName());
                String simMessage = tmp.replaceFirst("CARD", "SIM");
                message = mMiscExt.customizeSimDisplayString(simMessage, subId);
            }else{
                message = mMiscExt.customizeSimDisplayString(res.getString(
                        R.string.sim_preferred_message, sir.getDisplayName()), subId);
            }
            //wangshaohua@wind-mobil.com 2016-08-10 modified start
            if(FeatureOption.WIND_DEF_OPTR_E183L_EUR && "es".equals(res.getConfiguration().locale.getLanguage())){
                String tmp = res.getString(R.string.sim_preferred_message, "La SIM"+" "+(slotId+1));
                message = mMiscExt.customizeSimDisplayString(tmp, subId);
            }
            //wangshaohua@wind-mobil.com 2016-08-10 modified end
            //chengyonghui@wind-mobi.com modify 20160418 end
            alertDialogBuilder.setTitle(title);
            alertDialogBuilder.setMessage(message);
            /// @}

            alertDialogBuilder.setPositiveButton(R.string.yes, new
                    DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    final int subId = sir.getSubscriptionId();
                    PhoneAccountHandle phoneAccountHandle =
                            subscriptionIdToPhoneAccountHandle(subId);
                    setDefaultDataSubId(context, subId);
                    setDefaultSmsSubId(context, subId);
                    setUserSelectedOutgoingPhoneAccount(phoneAccountHandle);

                    finish();
                }
            });
            alertDialogBuilder.setNegativeButton(R.string.no, new
                    DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog,int id) {
                    finish();
                }
            });

            /// M: for ALPS02345087, finish the activity if dialog dismissed @{
            alertDialogBuilder.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                }
            });
            /// @}

            /// M: for ALPS02422990 avoid window leak @{
            mDialog = alertDialogBuilder.create();
            mDialog.show();
            /// @}
        } else {
            finish();
        }
    }

    private void setDefaultDataSubId(final Context context, final int subId) {
        final SubscriptionManager subscriptionManager = SubscriptionManager.from(context);
        /// M: for plug-in, need to call before setDefaultDataSubId
        mSimManagementExt.setDataState(subId);
        /// M: for CT 6M project, set data service to default sub  @ {
        if (FeatureOption.MTK_CT6M_SUPPORT) {
            TelephonyManager telephonyManager = TelephonyManager.from(context);
            boolean enableBefore = telephonyManager.getDataEnabled();
            int preSubId = subscriptionManager.getDefaultDataSubId();
            Log.d(TAG, "data flag: " + enableBefore + ", subId: "
                    + subId + ", preSubId: " + preSubId);
            if (subscriptionManager.isValidSubscriptionId(subId) &&
                    subId != preSubId) {
                subscriptionManager.setDefaultDataSubId(subId);
                if (enableBefore) {
                    telephonyManager.setDataEnabled(subId, true);
                    telephonyManager.setDataEnabled(preSubId, false);
                }
            }
        } else {
            subscriptionManager.setDefaultDataSubId(subId);
        }
        /// @ }
        /// M: for plug-in, need to call after setDefaultDataSubId
        mSimManagementExt.setDataStateEnable(subId);
        /// M: for plug-in @{
        // Toast.makeText(context, R.string.data_switch_started, Toast.LENGTH_LONG).show();
        Toast.makeText(
                context,
                mMiscExt.customizeSimDisplayString(getResources().getString(
                        R.string.data_switch_started), subId), Toast.LENGTH_LONG).show();
        /// @}

        //add by liuqiong@wind-mobi.com 2016-03-01 begin
        if (FeatureOption.WIND_DEF_PRO_E183L||FeatureOption.WIND_DEF_PRO_E188F) {
            //Bug #100775 lijingwei@wind-mobi.com 2016/3/15 start
            int[] subs = SubscriptionManager.from(context).getActiveSubscriptionIdList();
            Log.d(TAG,"subs.length="+subs.length);
            if(subs.length==2){
                new Handler(){}.postDelayed(new Runnable() {
                    @Override
                     public void run() {
                        Toast.makeText(
                        context,
                        mMiscExt.customizeSimDisplayString(getResources().getString(
                                R.string.sim_switch_2g), subId), Toast.LENGTH_LONG).show();
                     }
                }, 2000);
            }
            //Bug #100775 lijingwei@wind-mobi.com 2016/3/15 end
        }
        //add by liuqiong@wind-mobi.com 2016-03-01 end

        //lijingwei@wind-mobi.com Bug #105601 2016/4/7 start
        isSetDataOk = true;
        //lijingwei@wind-mobi.com Bug #105601 2016/4/7 end
    }

    private static void setDefaultSmsSubId(final Context context, final int subId) {
        final SubscriptionManager subscriptionManager = SubscriptionManager.from(context);
        /// M: [C2K Solution 1.5] @{
        if (CdmaUtils.shouldSwitchCapability(context, subId)) {
            if (TelecomManager.from(context).isInCall()) {
                Toast.makeText(context, R.string.default_sms_switch_err_msg1,
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (TelephonyUtils.setRadioCapability(context, subId)) {
                subscriptionManager.setDefaultSmsSubId(subId);
            }
        } else {
            subscriptionManager.setDefaultSmsSubId(subId);
        }
        /// @}
    }

    private void setUserSelectedOutgoingPhoneAccount(PhoneAccountHandle phoneAccount) {
        final TelecomManager telecomManager = TelecomManager.from(this);
        /// M: [C2K Solution 1.5] @{
        if (CdmaUtils.shouldSwichCapabilityForCalls(this, phoneAccount)) {
            if (telecomManager.isInCall()) { ;
                Toast.makeText(this, R.string.default_calls_switch_err_msg1,
                    Toast.LENGTH_SHORT).show();
                return;
            }
            int subId =
                    TelephonyUtils.phoneAccountHandleTosubscriptionId(this, phoneAccount);
            if (SubscriptionManager.isValidSubscriptionId(subId)
                    && TelephonyUtils.setRadioCapability(this, subId)) {
                telecomManager.setUserSelectedOutgoingPhoneAccount(phoneAccount);
            }
        } else {
            telecomManager.setUserSelectedOutgoingPhoneAccount(phoneAccount);
        }
        /// @}
    }

    private PhoneAccountHandle subscriptionIdToPhoneAccountHandle(final int subId) {
        final TelecomManager telecomManager = TelecomManager.from(this);
        final TelephonyManager telephonyManager = TelephonyManager.from(this);
        final Iterator<PhoneAccountHandle> phoneAccounts =
                telecomManager.getCallCapablePhoneAccounts().listIterator();

        while (phoneAccounts.hasNext()) {
            final PhoneAccountHandle phoneAccountHandle = phoneAccounts.next();
            final PhoneAccount phoneAccount = telecomManager.getPhoneAccount(phoneAccountHandle);
            if (subId == telephonyManager.getSubIdForPhoneAccount(phoneAccount)) {
                return phoneAccountHandle;
            }
        }

        return null;
    }

    public Dialog createDialog(final Context context, final int id) {
        final ArrayList<String> list = new ArrayList<String>();
        final SubscriptionManager subscriptionManager = SubscriptionManager.from(context);
        final List<SubscriptionInfo> subInfoList =
            subscriptionManager.getActiveSubscriptionInfoList();
        final int selectableSubInfoLength = subInfoList == null ? 0 : subInfoList.size();

        final DialogInterface.OnClickListener selectionListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int value) {

                        final SubscriptionInfo sir;

                        switch (id) {
                            case DATA_PICK:
                                sir = subInfoList.get(value);
                                /// M: for plug-in
                                int targetSub = (sir == null ? null : sir.getSubscriptionId());
                                if (!mSimManagementExt.switchDefaultDataSub(context, targetSub)) {
                                    /// M: [C2K solution 2 enhancement] @{
                                    if (CdmaUtils.isCdmaCardCompetionForData(context)) {
                                        if (!isEqualDefaultValue(context, id, targetSub)) {
                                            if (TelecomManager.from(context).isInCall()) {
                                                Toast.makeText(context,
                                                        R.string.default_data_switch_err_msg1,
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                CdmaUtils.startAlertCdmaDialog(context, targetSub,
                                                        id);
                                            }
                                        }
                                        /// @}
                                        /// M: [C2K solution 1.5] @{
                                    } else if (CdmaUtils.isSwitchCdmaCardToGsmCard(context
                                            , targetSub)) {
                                        CdmaUtils.startAlertCdmaDialog(context, targetSub, id);
                                        /// @}
                                    } else {
                                        setDefaultDataSubId(context, targetSub);
                                    }
                                    //lijingwei@wind-mobi.com Bug #105601 2016/6/15 start
                                    if (isSetDataOk) {
                                        setDataSubId(context, subInfoList, value);
                                    }
                                    //lijingwei@wind-mobi.com Bug #105601 2016/6/15 end
                                }
                                break;
                            case CALLS_PICK:
                                final TelecomManager telecomManager =
                                        TelecomManager.from(context);
                                final List<PhoneAccountHandle> phoneAccountsList =
                                        telecomManager.getCallCapablePhoneAccounts();

                                //lijingwei@wind-mobi.com Bug #116207 2016/6/15 start
                                Log.d(TAG,"CALLS_PICK old value="+value);
                                //lijingwei@wind-mobi.com Bug #116207 2016/6/15 end
                                /// M: for plug-in
                                value = mSimManagementExt.customizeValue(value);

                                //lijingwei@wind-mobi.com Bug #116207 2016/6/15 start
                                Log.d(TAG,"CALLS_PICK new value="+value);
                                //lijingwei@wind-mobi.com Bug #116207 2016/6/15 end
                                /// M: for ALPS02320816 @{
                                // phone account may changed in background
                                if (value > phoneAccountsList.size()) {
                                    Log.w(TAG, "phone account changed, do noting! value = " +
                                            value + ", phone account size = " +
                                            phoneAccountsList.size());
                                    break;
                                }
                                /// @}

                                /// M: [C2K solution 2 enhancement] [C2K solution 1.5] @{
                                if (CdmaUtils.isCdmaCardCompetionForCalls(context, value)
                                        && (!isEqualDefaultValue(context, id, value))) {
                                    if (TelecomManager.from(context).isInCall()) {
                                        Toast.makeText(context,
                                                R.string.default_calls_switch_err_msg1,
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        CdmaUtils.startAlertCdmaDialog(context, value, id);
                                    }
                                } else {
                                    //lijingwei@wind-mobi.com Bug #116207 2016/6/15 start
                                    if (phoneAccountsList.size() == 1) {
                                        setUserSelectedOutgoingPhoneAccount(
                                                phoneAccountsList.get(value));
                                    } else{
                                    setUserSelectedOutgoingPhoneAccount(
                                            value < 1 ?
                                                    null : phoneAccountsList.get(value - 1));
                                    }
                                    //lijingwei@wind-mobi.com Bug #116207 2016/6/15 end
                                }
                                /// @}
                                break;
                            case SMS_PICK:
                                /// M: for [SMS Always Ask]
                                // sir = subInfoList.get(value);
                                int subId = getPickSmsDefaultSub(subInfoList, value);
                                /// M: [C2K solution 2 enhancement] [C2K solution 1.5] @{
                                if (CdmaUtils.isCdmaCardCompetionForSms(context, subId)) {
                                    if (!isEqualDefaultValue(context, id, subId)) {
                                        if (TelecomManager.from(context).isInCall()) {
                                            Toast.makeText(context,
                                                    R.string.default_sms_switch_err_msg1,
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            CdmaUtils.startAlertCdmaDialog(context, subId, id);
                                        }
                                    }
                                } else {
                                    setDefaultSmsSubId(context, subId);
                                }
                                /// @}
                                break;
                            default:
                                throw new IllegalArgumentException("Invalid dialog type "
                                        + id + " in SIM dialog.");
                        }

                        finish();
                    }
                };

        Dialog.OnKeyListener keyListener = new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                    KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        finish();
                    }
                    return true;
                }
            };

        ArrayList<SubscriptionInfo> callsSubInfoList = new ArrayList<SubscriptionInfo>();
        /// M: for [SMS Always Ask] @{
        ArrayList<SubscriptionInfo> smsSubInfoList = new ArrayList<SubscriptionInfo>();
        /// @}
        if (id == CALLS_PICK) {
            //liuxiaoshuan@wind-mobi.com 2016/8/9 start
            isDataPick = false;
            //liuxiaoshuan@wind-mobi.com 2016/8/9 end
            final TelecomManager telecomManager = TelecomManager.from(context);
            final TelephonyManager telephonyManager = TelephonyManager.from(context);
            final Iterator<PhoneAccountHandle> phoneAccounts =
                    telecomManager.getCallCapablePhoneAccounts().listIterator();

            /// M: only for multiple accounts
            if (telecomManager.getCallCapablePhoneAccounts().size() > 1) {
                list.add(getResources().getString(R.string.sim_calls_ask_first_prefs_title));
                callsSubInfoList.add(null);
            }

            while (phoneAccounts.hasNext()) {
                final PhoneAccount phoneAccount =
                        telecomManager.getPhoneAccount(phoneAccounts.next());
                /// M: for ALPS02362894, seldom happened that phone account
                // unregistered in the background @{
                if (phoneAccount == null) {
                    continue;
                }
                /// @}
                list.add((String)phoneAccount.getLabel());
                int subId = telephonyManager.getSubIdForPhoneAccount(phoneAccount);
                if (subId != SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
                    final SubscriptionInfo sir = SubscriptionManager.from(context)
                            .getActiveSubscriptionInfo(subId);
                    callsSubInfoList.add(sir);
                } else {
                    callsSubInfoList.add(null);
                }
            }

            /// M:for plug-in @{
            mSimManagementExt.customizeListArray(list);
            mSimManagementExt.customizeSubscriptionInfoArray(callsSubInfoList);
            /// @}
        /// M: for [SMS Always Ask] @{
        } else if (id == SMS_PICK) {
            //liuxiaoshuan@wind-mobi.com 2016/8/9 start
            isDataPick = false;
            //liuxiaoshuan@wind-mobi.com 2016/8/9 end
            setupSmsSubInfoList(list, subInfoList, selectableSubInfoLength, smsSubInfoList);
        /// @}
        } else {
            //liuxiaoshuan@wind-mobi.com 2016/8/9 start
            isDataPick = true;
            needUpdateDisplayName = false;
            //liuxiaoshuan@wind-mobi.com 2016/8/9 end
            for (int i = 0; i < selectableSubInfoLength; ++i) {
                final SubscriptionInfo sir = subInfoList.get(i);
                CharSequence displayName = sir.getDisplayName();
                //liuxiaoshuan@wind-mobi.com 2016/8/9 start
                if(displayName != null && selectableSubInfoLength >= 2)
                {
                    if(displayName.toString().contains("CARD")){
                        needUpdateDisplayName = true;
                   }/*else{
                        needUpdateDisplayName = false;
                    }*/
                }
                //liuxiaoshuan@wind-mobi.com 2016/8/9 end
                if (displayName == null) {
                    displayName = "";
                }
                list.add(displayName.toString());
            }
        }

        String[] arr = list.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        ListAdapter adapter = new SelectAccountListAdapter(
                /// M: for [SMS Always Ask] @{
                // id == CALLS_PICK ? callsSubInfoList : subInfoList,
                getAdapterData(id, subInfoList, callsSubInfoList, smsSubInfoList),
                /// @}
                builder.getContext(),
                R.layout.select_account_list_item,
                arr, id);

        switch (id) {
            case DATA_PICK:
                builder.setTitle(R.string.select_sim_for_data);
                break;
            case CALLS_PICK:
                builder.setTitle(R.string.select_sim_for_calls);
                break;
            case SMS_PICK:
                builder.setTitle(R.string.sim_card_select_title);
                break;
            default:
                throw new IllegalArgumentException("Invalid dialog type "
                        + id + " in SIM dialog.");
        }

        /// M: for plug-in
        changeDialogTitle(builder, id);

        Dialog dialog = builder.setAdapter(adapter, selectionListener).create();
        dialog.setOnKeyListener(keyListener);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                finish();
            }
        });

        return dialog;

    }

    private class SelectAccountListAdapter extends ArrayAdapter<String> {
        private Context mContext;
        private int mResId;
        private int mDialogId;
        private final float OPACITY = 0.54f;
        private List<SubscriptionInfo> mSubInfoList;

        public SelectAccountListAdapter(List<SubscriptionInfo> subInfoList,
                Context context, int resource, String[] arr, int dialogId) {
            super(context, resource, arr);
            mContext = context;
            mResId = resource;
            mDialogId = dialogId;
            mSubInfoList = subInfoList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)
                    mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView;
            final ViewHolder holder;

            if (convertView == null) {
                // Cache views for faster scrolling
                rowView = inflater.inflate(mResId, null);
                holder = new ViewHolder();
                holder.title = (TextView) rowView.findViewById(R.id.title);
                holder.summary = (TextView) rowView.findViewById(R.id.summary);
                holder.icon = (ImageView) rowView.findViewById(R.id.icon);
                rowView.setTag(holder);
            } else {
                rowView = convertView;
                holder = (ViewHolder) rowView.getTag();
            }

            final SubscriptionInfo sir = mSubInfoList.get(position);
            if (sir == null) {
                holder.title.setText(getItem(position));
                holder.summary.setText("");
                /// M: display icon for non-sub accounts @{
                if (mDialogId == CALLS_PICK) {
                    setPhoneAccountIcon(holder, position);
                } else {
                /// @}
                    holder.icon.setImageDrawable(getResources()
                            .getDrawable(R.drawable.ic_live_help));
                }
                /// @}
                /// M: for plug-in
                mSimManagementExt.setSmsAutoItemIcon(holder.icon, mDialogId, position);
                holder.icon.setAlpha(OPACITY);
            } else {
            //fengsong@wind-mobi.com 20160725 add start.
            //modify sim name to life:)
            if(FeatureOption.WIND_DEF_OPTR_E183L_LIFE
			    && (String.valueOf(sir.getDisplayName()).contains("BelarusianTelecommunicationsNet"))){
                sir.setDisplayName("life:)");
            }
            //fengsong@wind-mobi.com 20160725 add end
                holder.title.setText(sir.getDisplayName());
                holder.summary.setText(sir.getNumber());
                holder.icon.setImageBitmap(sir.createIconBitmap(mContext));
                /// M: when item numbers is over the screen, should set alpha 1.0f.
                holder.icon.setAlpha(1.0f);
            }
            return rowView;
        }

        private class ViewHolder {
            TextView title;
            TextView summary;
            ImageView icon;
        }

        private void setPhoneAccountIcon(ViewHolder holder, int location) {
            Log.d(TAG, "setSipAccountBitmap()... location: " + location);
            String askFirst = getResources().getString(R.string.sim_calls_ask_first_prefs_title);
            String lableString = getItem(location);
            final TelecomManager telecomManager = TelecomManager.from(mContext);
            List<PhoneAccountHandle> phoneAccountHandles=
                    telecomManager.getCallCapablePhoneAccounts();
            if (!askFirst.equals(lableString)) {
                if (phoneAccountHandles.size() > 1) {
                    location = location - 1;
                }
                PhoneAccount phoneAccount = null;
                if (location >= 0 && location < phoneAccountHandles.size()) {
                    phoneAccount =
                            telecomManager.getPhoneAccount(phoneAccountHandles.get(location));
                }
                Log.d(TAG, "setSipAccountBitmap()... position: " + location
                        + " account: "  + phoneAccount);
                if (phoneAccount != null) {
                    holder.icon.setImageDrawable(phoneAccount.getIcon().loadDrawable(mContext));
                }
            } else {
                holder.icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_live_help));
            }
        }
    }

    ///-----------------------------------------MTK-----------------------------------------------

    /// M: for [SIM Hot Swap]
    private SimHotSwapHandler mSimHotSwapHandler;
    private ISimManagementExt mSimManagementExt;
    private ISettingsMiscExt mMiscExt;
    private Dialog mDialog;
    private IRCSSettings mRCSExt;

    // Receiver to handle different actions
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive, action = " + action);
            finish();
        }
    };

    private void setSimStateCheck() {
        /// M: for [SIM Hot Swap] @{
        mSimHotSwapHandler = new SimHotSwapHandler(getApplicationContext());
        mSimHotSwapHandler.registerOnSimHotSwap(new OnSimHotSwapListener() {
            @Override
            public void onSimHotSwap() {
                Log.d(TAG, "onSimHotSwap, finish Activity");
                finish();
            }
        });
        /// @}
        IntentFilter itentFilter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        registerReceiver(mReceiver, itentFilter);
        //liuxiaoshuan@wind-mobi.com 2016/8/9 start
        IntentFilter itentFilter2 = new IntentFilter("android.intent.action.ACTION_SUBINFO_RECORD_UPDATED");
        registerReceiver(mSIMStateUpdateReceiver, itentFilter2);
        //liuxiaoshuan@wind-mobi.com 2016/8/9 end
    }

    private void unsetSimStateCheck() {
        /// M: for [SIM Hot Swap]
        mSimHotSwapHandler.unregisterOnSimHotSwap();
        unregisterReceiver(mReceiver);
        //liuxiaoshuan@wind-mobi.com 2016/8/9 start
        unregisterReceiver(mSIMStateUpdateReceiver);
        //liuxiaoshuan@wind-mobi.com 2016/8/9 end
    }

    @Override
    protected void onDestroy() {
        unsetSimStateCheck();

        // M: for AlPS02113443, avoid window leak.
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
        super.onDestroy();
    }

    private int getPickSmsDefaultSub(final List<SubscriptionInfo> subInfoList,
            int value) {
        int subId = SubscriptionManager.INVALID_SUBSCRIPTION_ID;
        /// M: for plug-in
        value = mSimManagementExt.customizeValue(value);

        if (value < 1) {
            int length = subInfoList == null ? 0 : subInfoList.size();
            if (length == 1) {
                subId = subInfoList.get(value).getSubscriptionId();
            } else {
                subId = DefaultSmsSimSettings.ASK_USER_SUB_ID;
            }
        } else if (value >= 1 && value < subInfoList.size() + 1) {
            subId = subInfoList.get(value - 1).getSubscriptionId();
        } else {
            /// M: for plug-in
            subId = mSimManagementExt.getDefaultSmsSubIdForAuto();
        }
        subId = mRCSExt.getDefaultSmsClickContentExt(subInfoList, value, subId);
        Log.d(TAG, "getPickSmsDefaultSub, value: " + value + ", subId: " + subId);
        return subId;
    }

    private void setupSmsSubInfoList(final ArrayList<String> list,
            final List<SubscriptionInfo> subInfoList, final int selectableSubInfoLength,
            ArrayList<SubscriptionInfo> smsSubInfoList) {
        if ((selectableSubInfoLength > 1) && (mRCSExt.isNeedAskFirstItemForSms())) {
            list.add(getResources().getString(R.string.sim_calls_ask_first_prefs_title));
            smsSubInfoList.add(null);
        }
        for (int i = 0; i < selectableSubInfoLength; ++i) {
            final SubscriptionInfo sir = subInfoList.get(i);
            smsSubInfoList.add(sir);
            CharSequence displayName = sir.getDisplayName();
            if (displayName == null) {
                displayName = "";
            }
            list.add(displayName.toString());
        }
        /// M: for plug-in @{
        mSimManagementExt.customizeListArray(list);
        mSimManagementExt.customizeSubscriptionInfoArray(smsSubInfoList);
        mSimManagementExt.initAutoItemForSms(list, smsSubInfoList);
        /// @}
    }

    private List<SubscriptionInfo> getAdapterData(final int id,
            final List<SubscriptionInfo> subInfoList, ArrayList<SubscriptionInfo> callsSubInfoList,
            ArrayList<SubscriptionInfo> smsSubInfoList) {
        List<SubscriptionInfo> listForAdpter = null;
        switch (id) {
            case DATA_PICK:
                listForAdpter = subInfoList;
                break;
            case CALLS_PICK:
                listForAdpter = callsSubInfoList;
                break;
            case SMS_PICK:
                listForAdpter = smsSubInfoList;
                break;
            default:
                listForAdpter = null;
                throw new IllegalArgumentException("Invalid dialog type "
                        + id + " in SIM dialog.");
        }
        return listForAdpter;
    }

    /**
     * only for plug-in, change "SIM" to "SIM/UIM".
     *
     * @param builder the dialog builder need to modify.
     * @param id the dialog id.
     */
    private void changeDialogTitle(AlertDialog.Builder builder, int id) {
        switch (id) {
            case DATA_PICK:
                builder.setTitle(mMiscExt.customizeSimDisplayString(
                                    getResources().getString(R.string.select_sim_for_data),
                                    SubscriptionManager.INVALID_SUBSCRIPTION_ID));
                break;
            case CALLS_PICK:
                builder.setTitle(mMiscExt.customizeSimDisplayString(
                                    getResources().getString(R.string.select_sim_for_calls),
                                    SubscriptionManager.INVALID_SUBSCRIPTION_ID));
                break;
            case SMS_PICK:
                builder.setTitle(mMiscExt.customizeSimDisplayString(
                                    getResources().getString(R.string.sim_card_select_title),
                                    SubscriptionManager.INVALID_SUBSCRIPTION_ID));
                break;
            default:
                throw new IllegalArgumentException("Invalid dialog type "
                        + id + " in SIM dialog.");
        }
    }

    /**
     * when click the same Item, do not handle it.
     * @param context context
     * @param dialogType dialogType
     * @param targetValue dialogType
     * @return true
     */
    public static boolean isEqualDefaultValue(Context context, int dialogType, int targetValue) {
        boolean isEqual = false;
        switch (dialogType) {
            case SimDialogActivity.DATA_PICK:
                isEqual = (SubscriptionManager.getDefaultDataSubId() == targetValue);
                break;
            case SimDialogActivity.CALLS_PICK:
                final TelecomManager telecomManager = TelecomManager.from(context);
                final List<PhoneAccountHandle> phoneAccountsList =
                        telecomManager.getCallCapablePhoneAccounts();
                PhoneAccountHandle targetHandle = targetValue < 1 ?
                        null : phoneAccountsList.get(targetValue - 1);
                PhoneAccountHandle defaultHandle =
                        telecomManager.getUserSelectedOutgoingPhoneAccount();
                if (targetHandle == null) {
                    isEqual = defaultHandle == null;
                } else {
                    isEqual = targetHandle.equals(defaultHandle);
                }
                break;
            case SimDialogActivity.SMS_PICK:
                isEqual = (SubscriptionManager.getDefaultSmsSubId() == targetValue);
                break;
            default:
                break;
        }
        Log.d(TAG, "isEqualDefaultValue()... isEqual: " + isEqual + ", dialogType: " + dialogType
                + " targetValue: " + targetValue);
        return isEqual;
    }

    //lijingwei@wind-mobi.com Bug #105601 2016/4/7 start
    private void setDataSubId(Context context, List<SubscriptionInfo> subInfoList, int value) {
        final TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (subInfoList != null && subInfoList.size() > 1) {
            for (int i = 0; i < subInfoList.size(); i++) {
                mTelephonyManager.setDataEnabled(subInfoList.get(i).getSubscriptionId(), (i == value));
            }
        }
    }
    //lijingwei@wind-mobi.com Bug #105601 2016/4/7 end
    //liuxiaoshuan@wind-mobi.com 2016/8/9 start
    //fix sim displayName don't update
    private BroadcastReceiver mSIMStateUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(mDialog == null || !isDataPick || !FeatureOption.WIND_DEF_OPTR_E188F_IN)
                return;
            String action = intent.getAction();
            final SubscriptionManager subscriptionManager = SubscriptionManager.from(SimDialogActivity.this);
            final List<SubscriptionInfo> subInfoList =
                subscriptionManager.getActiveSubscriptionInfoList();
            mSubscriptionInfos = getAdapterData(DATA_PICK, subInfoList, null, null);
            if("android.intent.action.ACTION_SUBINFO_RECORD_UPDATED".equals(action) && mSubscriptionInfos != null && mSubscriptionInfos.size() >= 2)
            {
                for(int i = 0;i <mSubscriptionInfos.size();i++){
                    final SubscriptionInfo sir = mSubscriptionInfos.get(i);
                    if( sir != null && String.valueOf(sir.getDisplayName()).contains("CARD")){
                        needUpdateDisplayName = true;
                        return;
                    }
                }
                if (needUpdateDisplayName && mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                    mDialog = null;
                    mDialog = createDialog(SimDialogActivity.this, DATA_PICK);
                    Log.d(TAG, "onReceive, lxs0809>>>>action+++update");
                    mDialog.show();
                }
            }
        }
    };
    //liuxiaoshuan@wind-mobi.com 2016/8/9 end
}
