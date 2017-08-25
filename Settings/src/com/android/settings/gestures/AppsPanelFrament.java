/*
 * Filename : AppsPanelFrament.java
 * Detail description
 *
 *
 * Author:xuyongfeng@wind-mobi.com,
 * created at 2014/04/28
 */

package com.android.settings.gestures;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.SettingsActivity;

import com.android.settings.R;
//add by liuqiong@wind-mobi.com 2016-01-19 begin
import com.android.internal.logging.MetricsLogger;
//add by liuqiong@wind-mobi.com 2016-01-19 end

public class AppsPanelFrament extends SettingsPreferenceFragment implements OnItemClickListener{
	
	  private GridView mGridView;
	  private Context mContext;
	  private PackageManager mPackageManager;
	  private List<ResolveInfo> mAllApps;
	  private GridViewAdapter mAdapter;
	  private String mKey;
	  private String packageName;
	  private String className;
	  
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);	
    }
    
    @Override
    public void onActivityCreated(Bundle b){
        super.onActivityCreated(b);
        mContext = getActivity();
        mPackageManager = getActivity().getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        mAllApps = mPackageManager.queryIntentActivities(intent, 0);
        Collections.sort(mAllApps, new ResolveInfo.DisplayNameComparator(mPackageManager));
        mAdapter = new GridViewAdapter(mContext, mAllApps);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.gesture_apps_fragment, container, false);
        mGridView = (GridView)view.findViewById(R.id.gridView);
        return view;	
    }
    
    @Override
    public void onResume(){
        super.onResume();	
        Bundle bundle = getArguments();
        mKey = bundle.getString("key");
    }
    
    class GridViewAdapter extends BaseAdapter{
        private List<ResolveInfo> mAllApps;
        private Context mContext;
        
        public GridViewAdapter(Context context, List<ResolveInfo> allApps){
            mContext = context;
            mAllApps = allApps;	
        }
        
        @Override
        public int getCount(){
            return mAllApps.size();	
        }
        
        @Override
        public Object getItem(int position){
            return null;
        }
        
        @Override
        public long getItemId(int position){
            return 0;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
        	  ViewHolder holder = null;
        	  if(convertView == null){
        	  	  convertView = LayoutInflater.from(mContext).inflate(R.layout.gesture_application_item, null);
        	  	  holder = new ViewHolder();
        	  	  holder.icon = (ImageView)convertView.findViewById(R.id.icon);
            		holder.label = (TextView)convertView.findViewById(R.id.title);
        	  	  convertView.setTag(holder);
        	  }else{
        	      holder = (ViewHolder)convertView.getTag();
        	  }

            ResolveInfo info = mAllApps.get(position);
            Drawable appIcon = info.loadIcon(mPackageManager);
            if(appIcon != null){
                holder.icon.setImageDrawable(appIcon);	
            }
            
            CharSequence label = info.loadLabel(mPackageManager);
            if(label != null){
                holder.label.setText(label);	
            }
            
            return convertView;
        }
        

    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id){
                	
        ResolveInfo info = mAllApps.get(position);
        packageName = info.activityInfo.packageName;
        className = info.activityInfo.name;
        if(mKey != null && packageName != null & className != null){
            Utils.setGestureIntent(getContentResolver(), mKey, packageName+"/"+className);
        }
        ((SettingsActivity)getActivity()).finishPreferencePanel(this, 0, null);
    }

    class ViewHolder{
        public ImageView icon;
        public TextView label;
    }

    //add by liuqiong@wind-mobi.com 2016-01-19 begin
    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.ACCESSIBILITY;
    }
    //add by liuqiong@wind-mobi.com 2016-01-19 end
}