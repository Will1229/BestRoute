package com.will.studio.bestroute.Frontend.main.test;

import android.support.design.widget.NavigationView;
import android.test.ActivityInstrumentationTestCase2;

import com.will.studio.bestroute.R;
import com.will.studio.bestroute.Frontend.main.MainActivity;

/**
 * Created by egaozhi on 2015-12-17.
 */
public class TestMainActivity extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainActivity;
    private NavigationView navigationView;

    public TestMainActivity(Class<MainActivity> activityClass) {
        super(activityClass);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mainActivity = getActivity();
        navigationView = (NavigationView) mainActivity.findViewById(R.id.nav_view);
    }

    public void testPreconditions() {
        assertNotNull(mainActivity);
        assertNotNull(navigationView);
    }
}
