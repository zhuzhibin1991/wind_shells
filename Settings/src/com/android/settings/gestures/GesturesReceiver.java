/*
 * Filename : GesturesReceiver.java
 * Detail description
 *
 *
 * Author:xuyongfeng@wind-mobi.com,
 * created at 2014/04/28
 */
 
package com.android.settings.gestures;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.os.Bundle;
import android.os.PowerManager;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.view.KeyEvent;
import com.android.internal.widget.LockPatternUtils;
import android.view.WindowManagerGlobal;
import android.os.RemoteException;
//add by liuqiong@wind-mobi.com 2016-02-19 begin
import com.mediatek.settings.FeatureOption;
//add by liuqiong@wind-mobi.com 2016-02-19 end

import java.util.List;
//libing add gestures for asus 20151009 -s
import android.os.SystemProperties;
import android.app.ActivityManager;
import android.content.Context;
//libing add gestures for asus 20151009 -e

public class GesturesReceiver extends BroadcastReceiver{
    private static final boolean DEBUG = Utils.DEBUG;
    private static final String TAG = "GesturesReceiver";

    private PackageManager mPackageManager;
    private ContentResolver mContentResolver;
    private static String mGestureKey = "";

    @Override
    public void onReceive(Context context, Intent intent){
        if(!Utils.GESTURES_FEATURE_SUPPORTED){
            return;	
        }

        String action = intent.getAction();
        Bundle data = intent.getExtras();
        if(DEBUG){
            Log.i(TAG, "action:"+action);	
        }

        if(action.equals("com.android.intent.gestures")){
            if(data != null){
                int keyCode = data.getInt("keyCode");
                String gestureKey = null;
                if(keyCode == KeyEvent.KEYCODE_F13){
                    //c, F13
                    gestureKey = GesturesSettings.KEY_GESTURE_C;
                }else if(keyCode == KeyEvent.KEYCODE_F14){
                    //e, F14
                    gestureKey = GesturesSettings.KEY_GESTURE_E;
                }else if(keyCode == KeyEvent.KEYCODE_F15){
                    //w, F15
                    gestureKey = GesturesSettings.KEY_GESTURE_W;
                }else if(keyCode == KeyEvent.KEYCODE_F16){
                    //o, F16
                    gestureKey = GesturesSettings.KEY_GESTURE_O;
                }else if(keyCode == KeyEvent.KEYCODE_F17){
                    //m, F17
                    gestureKey = GesturesSettings.KEY_GESTURE_M;
                }else if(keyCode == KeyEvent.KEYCODE_F18){
                    //s, F18
                    gestureKey = GesturesSettings.KEY_GESTURE_S;
                }else if(keyCode == KeyEvent.KEYCODE_F19){
                    //z, F19
                    gestureKey = GesturesSettings.KEY_GESTURE_Z;
                }else if(keyCode == KeyEvent.KEYCODE_F20){
                    //v, F20
                    gestureKey = GesturesSettings.KEY_GESTURE_V;
                }else if(keyCode == KeyEvent.KEYCODE_F21){
                    //double click, F21
                    gestureKey = GesturesSettings.GESTURE_DOUBLE_CLICK;
                } 
                //add by liuqiong@wind-mobi.com 2016-02-17 begin
                else if (changeMusic(context, keyCode)) {
                    return;
                } 
                //add by liuqiong@wind-mobi.com 2016-02-17 end
                else{
                    //we only handle the keyCode pre-defined
                    return;	
                }
        
                ContentResolver resolver = context.getContentResolver();
        
                boolean enabled = Utils.getInstance().isGestureEnabled(resolver, gestureKey);
                if(DEBUG){
                    Log.i(TAG, "keyCode:"+keyCode+", key="+gestureKey+", enabled="+enabled);	
                }
        
                if(enabled){
                    //modify by liuqiong@wind-mobi.com 2016-02-19 begin
                    if(GesturesSettings.GESTURE_DOUBLE_CLICK.equals(gestureKey)){
                        //unlock
                        //unLockScreen(context);
                    }else{
                        Intent gestureAnim = new Intent("com.mediatek.gestures.GESTUREANIMAL");
                        gestureAnim.putExtra("gesture_mode", gestureKey);
                        gestureAnim.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(gestureAnim);
                        
                        mGestureKey = gestureKey;
                    }
                    //unlock screen anyway                    
                    unLockScreen(context);                    
                    //modify by liuqiong@wind-mobi.com 2016-02-19 end
                }
            }
        } 
        //add by liuqiong@wind-mobi.com 2016-02-19 begin
        else if (action.equals("com.android.intent.gestures_animal")) {
            openApp(context);
        }
        //add by liuqiong@wind-mobi.com 2016-02-19 end
        else if(action.equals(Intent.ACTION_USER_PRESENT)){
            //add by liuqiong@wind-mobi.com 2016-02-19 begin
            //fix Bug #130085 : add gestures
            if(!mGestureKey.equals("") && (FeatureOption.WIND_DEF_PRO_E183L||FeatureOption.WIND_DEF_PRO_E188F||FeatureOption.WIND_DEF_PRO_E181L)) {
                context.sendBroadcast(new Intent("com.android.intent.gestures_animal_start"));
            } else {
                openApp(context);
            }
            //add by liuqiong@wind-mobi.com 2016-02-19 end
        }else if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
            boolean gestureState = Utils.getInstance().isGestureEnabled(mContentResolver == null ? context.getContentResolver() : mContentResolver, 
                GesturesSettings.DESTURES_ENABLED);            
            Utils.enableDriver(gestureState);

            //add by liuqiong@wind-mobi.com 2016-02-16 begin
            if (gestureState) {
                Utils.setSMWPValue("1");
            }
            //add by liuqiong@wind-mobi.com 2016-02-16 end
        }else if(action.equals(Intent.ACTION_PACKAGE_ADDED)){
        
        }else if(action.equals(Intent.ACTION_PACKAGE_REMOVED)){
            final String packageName = intent.getData().getSchemeSpecificPart();
            if (packageName == null || packageName.length() == 0) {
                return;
            } 
            Utils.onPackageRemoved(mContentResolver, packageName);
        }
    }
    
    private void unLockScreen(Context context){        
        try {
            WindowManagerGlobal.getWindowManagerService().setGestureAction(true);
            WindowManagerGlobal.getWindowManagerService().dismissKeyguard();
        } catch (RemoteException e) {
            Log.w(TAG , "Remote Exception", e);
        }
        
        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        //pm.dismissKeyguardLw();
        PowerManager.WakeLock wl = pm.newWakeLock(/*PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK*/
                                                  PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.ON_AFTER_RELEASE, "bright");
        wl.acquire();
        wl.release();

        try {
            WindowManagerGlobal.getWindowManagerService().setGestureAction(false);
        } catch (RemoteException e) {
            Log.w(TAG , "Remote Exception", e);
        }
    }	
    
    private boolean isAppInstalled(Intent intent){
        if(mPackageManager != null){
            List<ResolveInfo> resolveInfos = mPackageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);	
            if(resolveInfos != null && resolveInfos.size() > 0){
                return true;	
            }
        }
        return false;
    }


    //add by liuqiong@wind-mobi.com 2016-02-17 begin
    private boolean changeMusic(Context context, int keyCode) {
        boolean isChange = true;

        if (keyCode == KeyEvent.KEYCODE_F22) {
            context.sendBroadcast(new Intent("com.android.music.musicservicecommand.next"));            
        } else if (keyCode == KeyEvent.KEYCODE_F23) {
            context.sendBroadcast(new Intent("com.android.music.musicservicecommand.previous"));
        } else {
            isChange = false;
        }

        return isChange;
    }

    private void openApp(Context context) {
        if(!mGestureKey.equals("")){
                if(mPackageManager == null){
                    mPackageManager = context.getPackageManager();
                }
                if(mContentResolver == null){
                    mContentResolver = context.getContentResolver();
                }

                //unlock and launch app
                ContentResolver resolver = context.getContentResolver();
                String gestureInfo = Utils.getGestureIntent(resolver, mGestureKey);
                String[] classInfo;
                if(gestureInfo != null && !gestureInfo.equals("")){
                    classInfo = gestureInfo.split("/");
                    String packageName = classInfo[0];
                    String className = classInfo[1];	
                    if(DEBUG){
                        Log.i(TAG, "packageName:"+packageName+", className="+className);	
                    }
                    Intent gestureIntent = new Intent();                  
                    ComponentName componentName = new ComponentName(packageName, className);
                    gestureIntent.setComponent(componentName);
                    if(isAppInstalled(gestureIntent)){
                        //unLockScreen(context);
                        gestureIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        context.startActivity(gestureIntent);
                    }else{
                        //if app doesnot installed,set to disabled	
                        Utils.enableGesture(resolver, mGestureKey, false);
                    }
                }
                mGestureKey = "";
            }
    }
    //add by liuqiong@wind-mobi.com 2016-02-17 end
    
    
}
