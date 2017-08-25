/*
 * Filename : Utils.java
 * Detail description
 *
 *
 * Author:xuyongfeng@wind-mobi.com,
 * created at 2014/04/28
 */
 
package com.android.settings.gestures;

import android.content.Intent;
import android.content.Context;
import android.content.ContentResolver;
import android.provider.Settings;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.Set;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import android.os.SystemProperties;
import java.io.File;

//add by liuqiong@wind-mobi.com 2016-02-16 begin
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
//add by liuqiong@wind-mobi.com 2016-02-16 end


public class Utils{

	  public static final boolean DEBUG = false;
	  public static final boolean GESTURES_FEATURE_SUPPORTED = SystemProperties.get("ro.wind_gestures_supported").equals("1");
	  private static final String TAG = "Utils";
		private static HashMap<String, Integer> mGesturesIndex = new HashMap<String, Integer>();
		private static HashMap<String, String> mGesturesSegs = new HashMap<String, String>();
		
		private static Utils mInstance = new Utils();
		 
		private Utils(){} 
	   
	  public static Utils getInstance(){
	      return 	mInstance;
	  }
	  
		static{
		    	mGesturesIndex.put(GesturesSettings.DESTURES_ENABLED, 0);
		    	mGesturesIndex.put(GesturesSettings.GESTURE_DOUBLE_CLICK, 1);
		    	mGesturesIndex.put(GesturesSettings.KEY_GESTURE_C, 2);
		    	mGesturesIndex.put(GesturesSettings.KEY_GESTURE_E, 3);
		    	mGesturesIndex.put(GesturesSettings.KEY_GESTURE_W, 4);
		    	mGesturesIndex.put(GesturesSettings.KEY_GESTURE_O, 5);
		    	mGesturesIndex.put(GesturesSettings.KEY_GESTURE_M, 6);
		    	mGesturesIndex.put(GesturesSettings.KEY_GESTURE_S, 7);
		    	mGesturesIndex.put(GesturesSettings.KEY_GESTURE_Z, 8);
		    	mGesturesIndex.put(GesturesSettings.KEY_GESTURE_V, 9);
                    //add by liuqiong@wind-mobi.com 2016-02-17 begin
                    mGesturesIndex.put(GesturesSettings.KEY_GESTURE_MUSIC, 10);
                    //add by liuqiong@wind-mobi.com 2016-02-17 end
		    	mGesturesSegs.put(GesturesSettings.KEY_GESTURE_C, Settings.System.GESTURE_C);
		    	mGesturesSegs.put(GesturesSettings.KEY_GESTURE_E, Settings.System.GESTURE_E);
		    	mGesturesSegs.put(GesturesSettings.KEY_GESTURE_W, Settings.System.GESTURE_W);
		    	mGesturesSegs.put(GesturesSettings.KEY_GESTURE_O, Settings.System.GESTURE_O);
		    	mGesturesSegs.put(GesturesSettings.KEY_GESTURE_M, Settings.System.GESTURE_M);
		    	mGesturesSegs.put(GesturesSettings.KEY_GESTURE_S, Settings.System.GESTURE_S);
		    	mGesturesSegs.put(GesturesSettings.KEY_GESTURE_Z, Settings.System.GESTURE_Z);
		    	mGesturesSegs.put(GesturesSettings.KEY_GESTURE_V, Settings.System.GESTURE_V);
		}
		
		
    public static boolean isGestureEnabled(ContentResolver resolver, String key){
        if(resolver != null){
            String enabledList = Settings.System.getString(resolver,Settings.System.GESTURES_ENABLED);
            if(key == null || mGesturesIndex == null || enabledList == null 
            		|| enabledList.substring(0,1).equals("0")){
                return false;	
            }
            
            int length = enabledList.length();
            int index = mGesturesIndex.get(key);

            if(index < length && index >= 0){
              String value = 	enabledList.substring(index, index+1);
              if(DEBUG){
                Log.i(TAG, "isEnabled: key="+key+", index="+index+", value="+value+", enabledList="+enabledList);
              }
              if(value == null || value.equals("0")){
                  return false;
              }else if(value.equals("1")){
                	return true;
              }
            }else{
                return false;
            }
 						
        }
        
        return false;
    }
    
    public static boolean isGestureChecked(ContentResolver resolver, String key){
        if(resolver != null){
            String enabledList = Settings.System.getString(resolver,Settings.System.GESTURES_ENABLED);
            if(key == null || mGesturesIndex == null || enabledList == null ){
                return false;	
            }
            
            int length = enabledList.length();
            int index = mGesturesIndex.get(key);

            if(index < length && index >= 0){
              String value = 	enabledList.substring(index, index+1);
              if(DEBUG){
                Log.i(TAG, "isEnabled: key="+key+", index="+index+", value="+value+", enabledList="+enabledList);
              }
              if(value == null || value.equals("0")){
                  return false;
              }else if(value.equals("1")){
                	return true;
              }
            }else{
                return false;
            }
 						
        }
        
        return false;    	
    	
    }
    
    public static boolean enableGesture(ContentResolver resolver, String key, boolean enabled){
    	  if(resolver != null){
        	  int index = mGesturesIndex.get(key);
						if(key.equals(GesturesSettings.DESTURES_ENABLED)){
									enableDriver(enabled);
						}
            String enabledList = Settings.System.getString(resolver,Settings.System.GESTURES_ENABLED);
            int length = enabledList.length();
            if(index < length && index >= 0){
        	      StringBuffer buffer = new StringBuffer(enabledList);
        	  
        	      if(enabled){
        	  	      buffer.replace(index, index + 1, "1");
        	      }else{
        	  	      buffer.replace(index, index + 1, "0");
        		    }
        		    Settings.System.putString(resolver, Settings.System.GESTURES_ENABLED, buffer.toString());
        	  }else{
        	      return false;	
        	  }
        }
        return true;
    }
    
    public static boolean setGestureIntent(ContentResolver resolver, String key, String info){
    	
        String data_seg = null;
        
        if(key == null){
            return false;	
        }
        
        data_seg = mGesturesSegs.get(key);
				if(data_seg != null){        
            Settings.System.putString(resolver, data_seg, info);    	
        }else{
            return false;	
        }
        return true;
    }
    
    public static String getGestureIntent(ContentResolver resolver, String key){
        String data_seg = null;
        
        if(key == null){
            return null;	
        }
				data_seg = mGesturesSegs.get(key);
				if(data_seg == null){
				    return null;	
				} 
        
        return Settings.System.getString(resolver, data_seg);
    }
    
    public static  boolean isAppInstalled(Context context, Intent intent){
    	  PackageManager manager = context.getPackageManager();
    	  if(manager != null){
		        List<ResolveInfo> resolveInfos = manager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);	
		        if(resolveInfos != null && resolveInfos.size() > 0){
		            return true;	
		        }
      	}
        
        return false;
    }    
    
    public static void enableDriver(boolean enabled){
        //remove by liuqiong@wind-mobi.com 2016-02-18 begin
        /*
				String enableCode = "";
				if(enabled){
					  enableCode = "1";
				}else{
				    enableCode = "0";
				}   
				//String path = "/sys/class/ctp_gesture/device/gesture";
				String path = "/sys/devices/bus.2/11008000.I2C1/i2c-1/1-005d/gesture";
				String path_2 = "/sys/devices/mx_tsp/gesture_wind";
				Log.i(TAG, "enableDriver: enabled=" + enabled + " path: " + path + " path_2: " + path_2);

				File file = new File(path);
				if (!file.exists()){
					path = path_2;
				}

				PrintWriter printWriter;
				try{
                                    ///sys/devices/platform/mt-i2c.0/i2c-0/0-005d/gesture
						printWriter = new PrintWriter(path);
				    printWriter.write(enableCode.toCharArray());
					  printWriter.flush();
				    printWriter.close();
				}catch(FileNotFoundException e){
				    e.printStackTrace();	
				}   
        */
        //remove by liuqiong@wind-mobi.com 2016-02-18 end
    }
    
    public static void onPackageRemoved(ContentResolver resolver, String packageName){
        Set<String> set = mGesturesSegs.keySet();
        for(Iterator<String> iterator = set.iterator(); iterator.hasNext();){
            String key = iterator.next();
            String info = getGestureIntent(resolver, key);
            if(info != null && !info.isEmpty()){
                String[] classInfo = info.split("/");	
                if(packageName.equals(classInfo[0])){
                	  setGestureIntent(resolver, key, "");
                	  enableGesture(resolver, key, false);
                }
            }
        }
    }

    //add by liuqiong@wind-mobi.com 2016-02-16 begin
    public static void setSMWPValue(String val) {
        String tpType = null;
		try {
			byte[] bytes = new byte[256];
			InputStream is = new FileInputStream("/sys/class/wind_device/device_info/ctp_info");
			int count = is.read(bytes);
			is.close();
			tpType = new String(bytes, 0, count);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
		if (tpType == null || tpType.length() <= 0) {
			return;
		}
        File smwp = null;
        
        if (tpType.contains("GT615")) {
            smwp = new File("/sys/devices/virtual/GT915L/gt915l/gesture");
        } else if (tpType.contains("MSG28XX")) {
            smwp = new File("/proc/class/ms-touchscreen-msg20xx/device/gesture_wakeup_mode");
            if (val.equals("1")) {
                val = "0xFFFF";
            }
        //add by sunsiyuan@wind-mobi.com 2016-06-17 begin
        }else if(tpType.contains("FT3427")){
			smwp = new File("/sys/devices/mx_tsp/gesture_wakeup_node");
		}
		//add by sunsiyuan@wind-mobi.com 2016-06-17 end
		else {
			smwp = new File("/proc/android_touch/SMWP");
		}
        FileWriter fw;
        try {
            fw = new FileWriter(smwp);
            fw.write(val);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //add by liuqiong@wind-mobi.com 2016-02-16 end
}
