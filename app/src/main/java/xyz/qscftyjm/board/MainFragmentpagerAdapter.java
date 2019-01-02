package xyz.qscftyjm.board;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class MainFragmentpagerAdapter extends FragmentPagerAdapter {

    List<Fragment> fragList;

    public MainFragmentpagerAdapter(FragmentManager fm, List<Fragment> fragList) {
        super(fm);
        this.fragList=fragList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragList.get(position);
    }

    @Override
    public int getCount() {
        return fragList.size();
    }
}
