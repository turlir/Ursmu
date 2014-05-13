package ru.ursmu.application.Activity;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import ru.ursmu.application.JsonObject.EducationItem;
import ru.ursmu.application.Realization.EducationWeek;

public class MyPagerAdapter extends FragmentPagerAdapter {

    private EducationWeek mList;
    private Boolean mFlag;

    private final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction = null;
    private Fragment mCurrentPrimaryItem = null;

    public MyPagerAdapter(FragmentManager fm, EducationWeek pages, Context c, Boolean isProfessor) {
        super(fm);
        mFragmentManager = fm;

        mList = pages;
        //Log.d("URSMULOG", "MyPagerAdapter create");
        mFlag = isProfessor;
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public void startUpdate(ViewGroup container) {
    }

    @Override
    public Fragment getItem(int i) {
        //ScheduleAdapter.clearIconPair();

        if (mFlag) {
            //Log.d("URSMULOG", "MyPagerAdapter ProfessorScheduleFragment getItem " + i);
            EducationItem[] value = mList.get(i);
            return ProfessorScheduleFragment.getInstance(value);
        } else {
            //Log.d("URSMULOG", "MyPagerAdapter GroupScheduleFragment getItem " + i);
            EducationItem[] value = mList.get(i);
            return GroupScheduleFragment.getInstance(value);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }

        final long itemId = getItemId(position);

        // Do we already have this fragment?
        String name = makeFragmentName(container.getId(), itemId);
        Fragment fragment = mFragmentManager.findFragmentByTag(name);
        if (fragment != null) {
            //Log.d("URSMULOG", "Attaching item #" + itemId + ": f=" + fragment);
            mCurTransaction.attach(fragment);
        } else {
            fragment = getItem(position);
            //Log.d("URSMULOG", "Adding item #" + itemId + ": f=" + fragment);
            mCurTransaction.add(container.getId(), fragment,
                    makeFragmentName(container.getId(), itemId));
        }
        if (fragment != mCurrentPrimaryItem) {
            fragment.setMenuVisibility(false);
            fragment.setUserVisibleHint(false);
        }

        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
        //Log.d("URSMULOG", "Detaching item #" + getItemId(position) + ": f=" + object
               // + " v=" + ((Fragment) object).getView());
        mCurTransaction.detach((Fragment) object);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment) object;
        if (fragment != mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem.setMenuVisibility(false);
                mCurrentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            mCurrentPrimaryItem = fragment;
        }
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (mCurTransaction != null) {
            mCurTransaction.commitAllowingStateLoss();
            mCurTransaction = null;
            mFragmentManager.executePendingTransactions();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment) object).getView() == view;
    }


    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return EducationItem.DayOfTheWeek[position];
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }
}
