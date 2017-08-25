/*
add by chusuxia@wind-mobi.com for SD update on 160308 start
*/
package com.android.settings.deviceinfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.RecoverySystem;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.android.settings.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
/*
add by chusuxia@wind-mobi.com for sdcacrd update 20150311
*/
public class SDCardUpdates extends Activity implements View.OnClickListener{
    public static String TAG = "SdcardUpdate";
	
	private Button udpatesCheck;
	private TextView updatesInfo;
	private Button updatesSystem;

	private File udpatesFile;
	private ArrayList<String> zipPaths = new ArrayList<String>();
	private StorageManager mStorageManager;
	
	private BroadcastReceiver batteryInfoReceiver;
	private int batteryPercent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sdcard_updates);
		
        udpatesCheck = (Button)findViewById(R.id.updates_check);
		udpatesCheck.setOnClickListener(this);
		updatesInfo = (TextView)findViewById(R.id.updates_info);
		updatesSystem = (Button)findViewById(R.id.updates_system);
		updatesSystem.setOnClickListener(this);
		
		batteryInfoReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
						int level = intent.getIntExtra("level", 0);
						int scale = intent.getIntExtra("scale", 100);
						batteryPercent = level * 100 / scale;
					}
				}
        };
    }
	
	private boolean checkSDCard() {
		String externalPath = null;
		StatFs stat = null;
        StorageManager mStorageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        StorageVolume[] volumes = mStorageManager.getVolumeList();
        for (StorageVolume volume : volumes) {
            if (volume.isRemovable() && Environment.MEDIA_MOUNTED.equals(mStorageManager.getVolumeState(volume.getPath()))) {
                externalPath = volume.getPath();
				Log.d(TAG, "externalPath-->" + externalPath);
                break;
            }
        }

        if (externalPath != null) {
            try {
                stat = new StatFs(externalPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
		
		if (stat != null) {
			return true;
		} else {
			return false;
		}
	}
	
	public void onClick(View view) {
		String remindContent = getResources().getString(R.string.sd_updates_condition);
		boolean updatesCheck = false;
		switch(view.getId()) {
			case R.id.updates_check:
				if (!checkSDCard()) {
					remindContent = getResources().getString(R.string.updates_no_sd);
				} else {
					updatesCheck = true;
				}			
				remindDialog(remindContent, updatesCheck);
				break;
				
			case R.id.updates_system:
				if ( batteryPercent < 35) {
					updatesCheck = false;
					remindContent = getResources().getString(R.string.updates_low_power);
					remindDialog(remindContent, updatesCheck);
					break;
				}	
				try {
					RecoverySystem.installPackage(getApplicationContext(), udpatesFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
				
			default:
				break;
		}
	}
	
	private void remindDialog(String content, boolean check) {
		final boolean updates = check;
		AlertDialog remindDialog = new AlertDialog.Builder(this)
			.setTitle(R.string.sd_updates_remind)
			.setMessage(content)
			.setPositiveButton(R.string.updates_confirm, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (updates) {
						checkUpdatePackage();
					}
				}
			})
			.setNegativeButton(R.string.updates_cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			}).show();
	}
	
	private void checkUpdatePackage() {
		mStorageManager = (StorageManager)getSystemService(Context.STORAGE_SERVICE);
        StorageVolume[] volumes = mStorageManager.getVolumeList();
		
		for (StorageVolume volume : volumes) {
			if (volume.isRemovable() && Environment.MEDIA_MOUNTED.equals(mStorageManager.getVolumeState(volume.getPath()))) {
				String tempPath = volume.getPath() + "/update.zip";
				zipPaths.add(tempPath);
					
				Log.d(TAG, "checkUpdatePackage-->" + tempPath);
            }
		}
		
		for (String zipPath : zipPaths) {
			udpatesFile = new File(zipPath);
			if (udpatesFile.exists()) {
				updatesSystem.setVisibility(View.VISIBLE);
				udpatesCheck.setVisibility(View.INVISIBLE);
				updatesInfo.setText(getResources().getString(R.string.updates_tips));
				break;
			} else {
				updatesSystem.setVisibility(View.INVISIBLE);
				udpatesCheck.setVisibility(View.VISIBLE);
				updatesInfo.setText(getResources().getString(R.string.updates_not_found));
			}
		}
	}
	
	@Override
    public void onResume() {
        super.onResume();

		registerReceiver(batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }
	
	@Override
    public void onDestroy() {
        super.onDestroy();
		
        if( batteryInfoReceiver != null ){
            unregisterReceiver(batteryInfoReceiver);
        }
    }
}
/*
add by chusuxia@wind-mobi.com for SD update on 160308 end
*/