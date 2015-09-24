package me.zakeer.startapp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by zakeer on 03-09-2015.
 */
public class PagerAdapter extends FragmentPagerAdapter {
    Context context;
    private int redirect = 0;

    public PagerAdapter(FragmentManager fm, Context context, int redirect) {
        super(fm);
        this.context = context;
        this.redirect = redirect;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        switch (position) {
            case 0:
                return (redirect == 0) ? new Report() : new ViewReports();
            case 1:
                return (redirect == 0) ? new SafeTravel() : new TravelTracking();
            case 2:
                return (redirect == 0) ? new Emergency(): new VehicleDetails() ;
            case 3:
                return (redirect == 0) ? new HelpMe() : new HelpMeTracking();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
