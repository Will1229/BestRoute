package com.will.studio.bestroute;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.will.studio.bestroute.frontend.main.activities.MainActivity;

/**
 * Created by egaozhi on 2016-01-04.
 *
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainActivity;
    private View mainList;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(true);
        mainActivity = getActivity();
        mainList = mainActivity.findViewById(R.id.main_item_list);

    }

    public void testPreconditions() {
        assertNotNull(mainActivity);
        assertNotNull(mainList);
    }
}
