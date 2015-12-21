package com.will.studio.bestroute.Backend.test;

import com.will.studio.bestroute.Backend.RouteDataManager;
import com.will.studio.bestroute.Backend.RouteDataManagerImpl;
import com.will.studio.bestroute.Backend.RouteItem;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;

/**
 * Created by egaozhi on 2015-12-21.
 */
public class TestRouteDataManagerImpl extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        if (!testDir.exists()) {
            testDir.mkdir();
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        testDir.delete();
    }

    private RouteDataManager routeDataManager;
    private File testDir;

    public TestRouteDataManagerImpl() {
        routeDataManager = new RouteDataManagerImpl();
        testDir = new File(System.getProperty("user.home") + File.separator + "AndroidTestDir");
    }

    public void testSaveAndReadOneItem() throws IOException {

        String from = "from place 111";
        String to = "to place 222";
        String time = "11:30";
        RouteItem inputItem = new RouteItem(from, to, time);
        routeDataManager.saveData(testDir.getAbsolutePath(), inputItem);
        File[] files = testDir.listFiles();
        assertEquals(1, files.length);
        RouteItem outputItem = routeDataManager.readData(files[0].getAbsolutePath());
        assertEquals(inputItem.getFrom(), outputItem.getFrom());
        assertEquals(inputItem.getTo(), outputItem.getTo());
        assertEquals(inputItem.getTime(), outputItem.getTime());
        files[0].delete();
    }
}
