package ru.ursmu.application.Activity;

import android.content.Context;
import android.os.Bundle;
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
            Fragment fragment = new ProfessorScheduleFragment();
            Bundle args = new Bundle();
            EducationItem[] value = mList.get(i);
            args.putSerializable(GroupScheduleFragment.MAIN_ARG, value);
            fragment.setArguments(args);
            return fragment;
        } else {
            Log.d("URSMULOG", "MyPagerAdapter GroupScheduleFragment getItem " + i);
            Fragment fragment = new GroupScheduleFragment();
            Bundle args = new Bundle();
            EducationItem[] value = mList.get(i);
            args.putSerializable(GroupScheduleFragment.MAIN_ARG, value);
            fragment.setArguments(args);
            return fragment;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return EducationItem.DayOfTheWeek[position];
    }
}
