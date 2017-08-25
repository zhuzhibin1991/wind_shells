//liangfeng@wind-mobi.com 2015.12.04 start
package com.android.settings.sim;
import com.android.internal.telephony.IccCardConstants;
import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.SystemProperties;
import android.provider.Settings;
public class SimPlugOutReceiver extends BroadcastReceiver {
    private static final String TAG = "SimPlugOutReceiver";
    private String stateExtra;
    private Context mContext;
    private SharedPreferences mSharedPreferences = null;
    private static final String SHARED_PREFERENCES_NAME = "plugout_state";
    private static final String HAS_PLUGIN_SIM = "has_plugin_sim";
    private static final String OLD_INSERT_SIMCOUNT = "old_insert_simcount";    
    
	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME,
                Context.MODE_PRIVATE);   
        stateExtra = intent.getStringExtra(IccCardConstants.INTENT_KEY_ICC_STATE);

        /* add by zhangwenjuan01@wind-mobi.com 2016.4.11 for bug#101014 begin */
        if("android.intent.action.SIM_STATE_CHANGED".equals(intent.getAction()) && getInsertSimCount()>0){
            Intent plugOutActivity = new Intent(context, SimPlugOutActivity.class);
            if(context.getPackageManager().resolveActivity(plugOutActivity,0) != null && SimPlugOutActivity.instance!=null){
                SimPlugOutActivity.instance.finish();
            }
        }
        /* add by zhangwenjuan01@wind-mobi.com 2016.4.11 for bug#101014 end */

        if((intent.getAction() == Intent.ACTION_BOOT_COMPLETED)
        		||(intent.getAction() == Intent.ACTION_SHUTDOWN)
        		||(intent.getAction() == Intent.ACTION_AIRPLANE_MODE_CHANGED))
        {
        	isBootStatus(false);
        }		
		if (IccCardConstants.INTENT_VALUE_ICC_LOADED.equals(stateExtra)) 
		{
			isBootStatus(true);
		}		
		if(mSharedPreferences.getInt(OLD_INSERT_SIMCOUNT, 0) == getInsertSimCount())
		{
			return;
		}else{
			Editor editor = mSharedPreferences.edit();
			editor.putInt(OLD_INSERT_SIMCOUNT,getInsertSimCount());
			editor.commit();
		}				
		Log.d(TAG, "onReceive()... action: " + intent.getAction()
			    + "... stateExtra :" + stateExtra
			    + "... HAS_PLUGIN_SIM : " + mSharedPreferences.getBoolean(HAS_PLUGIN_SIM, false) 
			    + "... getInsertSimCount() = " + getInsertSimCount());
		if (IccCardConstants.INTENT_VALUE_ICC_ABSENT.equals(stateExtra) 
				&& mSharedPreferences.getBoolean(HAS_PLUGIN_SIM, false)
				&& (getInsertSimCount() == 0)) 
		{
			Intent intentDialog = new Intent(mContext, SimPlugOutActivity.class);
			intentDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(intentDialog);			
		}
		
	}

    private int getInsertSimCount()
    {
    		String[] PROPERTY_ICCID = {
                "ril.iccid.sim1",
                "ril.iccid.sim2",
            };
    		String NO_SIM_VALUE = "N/A";
    		String[] currIccId = new String[2];   
    		int insertedSimCount = 0;
    		
            for (int i = 0; i < 2; i++) {
                currIccId[i] = SystemProperties.get(PROPERTY_ICCID[i]);
                if (currIccId[i] == null || "".equals(currIccId[i])) {
                    return insertedSimCount;
                }
                if (!NO_SIM_VALUE.equals(currIccId[i])) {
                	++insertedSimCount;
                }
            }    		
    		return insertedSimCount;    	    	
    }		
	
	private void isBootStatus(boolean mHasBoot)
	{
        Editor editor = mSharedPreferences.edit();
        editor.putBoolean(HAS_PLUGIN_SIM, mHasBoot);
        editor.commit();
	}	
}
//liangfeng@wind-mobi.com 2015.12.04 end