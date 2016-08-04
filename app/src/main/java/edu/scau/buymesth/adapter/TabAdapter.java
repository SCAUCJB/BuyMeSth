package edu.scau.buymesth.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Jammy on 2016/8/1.
 */
public class TabAdapter extends FragmentPagerAdapter{


    List<Fragment> fragment_list;

    public TabAdapter(FragmentManager fm, List<Fragment> fragment_list){
        super(fm);
        this.fragment_list = fragment_list;
    }

    @Override
    public Fragment getItem(int position) {
        return fragment_list.get(position);
    }

    @Override
    public int getCount() {
        return fragment_list.size();
    }

}
