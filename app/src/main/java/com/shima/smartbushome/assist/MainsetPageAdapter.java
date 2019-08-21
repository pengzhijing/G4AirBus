package com.shima.smartbushome.assist;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.shima.smartbushome.mainsetting.DB_settingFragment;
import com.shima.smartbushome.mainsetting.Ip_settingFragment;
import com.shima.smartbushome.mainsetting.Net_settingFragment;

/**
 * Created by Administrator on 2016/11/7.
 */
public class MainsetPageAdapter extends FragmentPagerAdapter {
   /* final int PAGE_COUNT = 4;
    private String tabTitles[] = new String[]{"Database","IP Setting","G4Server","Theme"};*/
   final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[]{"Database","IP Setting","G4Server"};
    private Context context;

    public MainsetPageAdapter(FragmentManager fm,Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment result=new Fragment();
        switch (position){
            case 0: result= DB_settingFragment.newInstance(); break;
            case 1:result=  Ip_settingFragment.newInstance(); break;
            case 2:result=  Net_settingFragment.newInstance(); break;
          //  case 3:result= ThemeFragment.newInstance();break;
        }
        return result;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
