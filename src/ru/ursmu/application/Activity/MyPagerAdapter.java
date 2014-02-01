package ru.ursmu.application.Activity;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import ru.ursmu.application.JsonObject.EducationItem;
import ru.ursmu.application.Realization.EducationWeek;

public class MyPagerAdapter extends FragmentPagerAdapter {

    private EducationWeek mList;
    Context mContext;
    private Boolean mFlag;

    public MyPagerAdapter(FragmentManager fm, EducationWeek pages, Context c, Boolean isProfessor) {
        super(fm);

        mList = pages;
        Log.d("URSMULOG", "MyPagerAdapter create");
        mContext = c;
        mFlag = isProfessor;
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public Fragment getItem(int i) {
        //ScheduleAdapter.clearIconPair();

        if (mFlag) {
            Log.d("URSMULOG", "MyPagerAdapter ProfessorScheduleFragment getItem " + i);
            EducationItem[] value = mList.get(i);
            return ProfessorScheduleFragment.getInstance(value);
        } else {
            Log.d("URSMULOG", "MyPagerAdapter GroupScheduleFragment getItem " + i);
            EducationItem[] value = mList.get(i);
            return GroupScheduleFragment.getInstance(value);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return EducationItem.DayOfTheWeek[position];
    }
}
