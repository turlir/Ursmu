package ru.example.ursmu.Activity;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class MyPagerAdapter
        extends PagerAdapter {

    private ArrayList<ListView> pages;
    //GroupScheduleActivity mContext;

    public MyPagerAdapter(ArrayList<ListView> pages) {
        this.pages = pages;
        //this.mContext = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(pages.get(position));
        return pages.get(position);

    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public ListView getItem(int posi) {
        return pages.get(posi);
    }

}
