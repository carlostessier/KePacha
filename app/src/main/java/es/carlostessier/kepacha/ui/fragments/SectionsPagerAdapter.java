package es.carlostessier.kepacha.ui.fragments;

/**
 *
 * Created by carlosfernandez on 30/12/14.
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;

import es.carlostessier.kepacha.R;


/**
 * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private static final int NUMBER_OF_TABS = 2;
    Context context;

    public SectionsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;

    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0: return new InboxFragment();
            case 1: return new FriendsFragment();
            default : return null;
        }

    }

    @Override
    public int getCount() {

        return NUMBER_OF_TABS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return context.getString(R.string.title_section1).toUpperCase(l);
            case 1:
                return context.getString(R.string.title_section2).toUpperCase(l);

        }
        return null;
    }
}
