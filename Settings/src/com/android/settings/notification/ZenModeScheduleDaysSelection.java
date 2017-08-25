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

package com.android.settings.notification;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.android.settings.R;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
//songmeiling@wind-mobi.com 20160413 add start
import android.os.SystemProperties;
//songmeiling@wind-mobi.com 20160413 add end
public class ZenModeScheduleDaysSelection extends ScrollView {
    public static final int[] DAYS = {
        Calendar.SUNDAY,
        Calendar.MONDAY,
        Calendar.TUESDAY,
        Calendar.WEDNESDAY,
        Calendar.THURSDAY,
        Calendar.FRIDAY,
        Calendar.SATURDAY,
    };
 
    // per-instance to ensure we're always using the current locale
    //songmeiling@wind-mobi.com 20160414 add start
    public static final int[] DAYS_RUS = {
        Calendar.MONDAY,
        Calendar.TUESDAY,
        Calendar.WEDNESDAY,
        Calendar.THURSDAY,
        Calendar.FRIDAY,
        Calendar.SATURDAY,
        Calendar.SUNDAY,
    };
    public static final boolean WIND_DEF_OPTR_E183L_RUS = SystemProperties.get("ro.wind_def_optr_e183l_rus").equals("1");
    public static final boolean WIND_DEF_OPTR_E181L_RUS = SystemProperties.get("ro.wind_def_optr_e181l_rus").equals("1");
    //songmeiling@wind-mobi.com add 20160414 end
    //hanweiwei@wind-mobi.com add 2016-06-03 begin
    public static final boolean WIND_DEF_OPTR_E188F_RUS = SystemProperties.get("ro.wind_def_optr_e188f_rus").equals("1");
    //hanweiwei@wind-mobi.com add 2016-06-03 end
    //caopei@wind-mobi.com 2016.07.06 start
    public static final boolean WIND_DEF_OPTR_E169F_RUS = SystemProperties.get("ro.wind_def_optr_e169f_rus").equals("1");
    //caopei@wind-mobi.com 2016.07.06 end
    private final SimpleDateFormat mDayFormat = new SimpleDateFormat("EEEE");
    private final SparseBooleanArray mDays = new SparseBooleanArray();
    private final LinearLayout mLayout;

    public ZenModeScheduleDaysSelection(Context context, int[] days) {
        super(context);
        mLayout = new LinearLayout(mContext);
        final int hPad = context.getResources()
                .getDimensionPixelSize(R.dimen.zen_schedule_day_margin);
        mLayout.setPadding(hPad, 0, hPad, 0);
        addView(mLayout);
        if (days != null) {
            for (int i = 0; i < days.length; i++) {
                mDays.put(days[i], true);
            }
        }
        mLayout.setOrientation(LinearLayout.VERTICAL);
        final Calendar c = Calendar.getInstance();
        final LayoutInflater inflater = LayoutInflater.from(context);
        //songmeiling@wind-mobi.com 20160414 mod start
        //hanweiwei@wind-mobi.com add WIND_DEF_OPTR_E188F_RUS
        //caopei@wind-mobi.com add WIND_DEF_OPTR_E169F_RUS First day should be Monday
        if (WIND_DEF_OPTR_E181L_RUS || WIND_DEF_OPTR_E183L_RUS || WIND_DEF_OPTR_E188F_RUS||WIND_DEF_OPTR_E169F_RUS) {
            for (int i = 0; i < DAYS_RUS.length; i++) {
                final int day = DAYS_RUS[i];
                final CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.zen_schedule_rule_day,
                        this, false);
                c.set(Calendar.DAY_OF_WEEK, day);
                checkBox.setText(mDayFormat.format(c.getTime()));
                checkBox.setChecked(mDays.get(day));
                checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mDays.put(day, isChecked);
                        onChanged(getDays());
                    }
                });
                mLayout.addView(checkBox);
            }
        } else {
            for (int i = 0; i < DAYS.length; i++) {
                final int day = DAYS[i];
                final CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.zen_schedule_rule_day,
                        this, false);
                c.set(Calendar.DAY_OF_WEEK, day);
                checkBox.setText(mDayFormat.format(c.getTime()));
                checkBox.setChecked(mDays.get(day));
                checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mDays.put(day, isChecked);
                        onChanged(getDays());
                    }
                });
                mLayout.addView(checkBox);
            }
        }
        //songmeiling@wind-mobi.com 20160414 mod end
    }

    private int[] getDays() {
        final SparseBooleanArray rt = new SparseBooleanArray(mDays.size());
        for (int i = 0; i < mDays.size(); i++) {
            final int day = mDays.keyAt(i);
            if (!mDays.valueAt(i)) continue;
            rt.put(day, true);
        }
        final int[] rta = new int[rt.size()];
        for (int i = 0; i < rta.length; i++) {
            rta[i] = rt.keyAt(i);
        }
        Arrays.sort(rta);
        return rta;
    }

    protected void onChanged(int[] days) {
        // event hook for subclasses
    }
}
