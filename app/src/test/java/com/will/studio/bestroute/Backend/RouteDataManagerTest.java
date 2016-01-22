package com.will.studio.bestroute.backend;

import android.util.Log;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by egaozhi on 2015-12-21.
 * Project: BestRoute
 */
public class RouteDataManagerTest extends TestCase {

    private RouteDataManager routeDataManager;
    private File testDir;
    private String testDirPath;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        if (!testDir.exists()) {
            if (!testDir.mkdir()) {
                fail();
            }
        }
        File[] fileList = testDir.listFiles();
        for (File f : fileList
                ) {
            if (!f.delete()) {
                fail();
            }
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if (!testDir.delete()) {
            Log.d(getClass().getName(), "Cannot delete " + testDir.toString());
        }
    }


    public RouteDataManagerTest() {
        testDirPath = System.getProperty("user.home") + File.separator + "AndroidTestDir";
        testDir = new File(testDirPath);
        routeDataManager = new RouteDataManager(testDirPath);
    }

    public void testSaveAndReadAndEditOneItem() throws IOException {
        // New
        String from = "from place 111";
        String to = "to place 222";
        String time = "11:30";
        double fromLat = 1.1;
        double fromLng = 2.2;
        double toLat = 3.3;
        double toLng = 4.4;

        RouteItem inputItem = new RouteItem(from, to, time);
        inputItem.setFromLat(fromLat);
        inputItem.setFromLng(fromLng);
        inputItem.setToLat(toLat);
        inputItem.setToLng(toLng);

        assertNull(inputItem.getFilePath());

        int requestCode1 = inputItem.getAlarmRequestCode();
        assertNotNull(requestCode1);
        routeDataManager.saveItem(inputItem, null);

        String filePath = inputItem.getFilePath();
        assertNotNull(filePath);

        File[] files = testDir.listFiles();
        assertEquals(1, files.length);
        RouteItem outputItem = routeDataManager.restoreItemFromDisc(files[0].getAbsolutePath());
        assertEquals(inputItem, outputItem);

        // Edit
        filePath = inputItem.getFilePath();
        from = "from place 222";
        to = "to place 333";
        time = "11:33";
        inputItem = new RouteItem(from, to, time);

        inputItem.setFromLat(fromLat);
        inputItem.setFromLng(fromLng);
        inputItem.setToLat(toLat);
        inputItem.setToLng(toLng);

        int requestCode2 = inputItem.getAlarmRequestCode();
        assertTrue(requestCode1 != requestCode2);
        routeDataManager.saveItem(inputItem, filePath);

        files = testDir.listFiles();
        assertEquals(1, files.length);
        outputItem = routeDataManager.restoreItemFromDisc(files[0].getAbsolutePath());
        assertEquals(inputItem, outputItem);

        if (!files[0].delete()) {
            fail();
        }
    }

    public void testSaveAndRestoreAndDeleteAllItems() throws Exception {
        final int loop = 3;
        String from = "from place 111";
        String to = "to place 222";
        String time = "11:30";
        RouteItem inputItem = new RouteItem(from, to, time);

        for (int i = 0; i < loop; ++i) {
            routeDataManager.saveItem(inputItem, null);
        }

        ArrayList<RouteItem> itemArrayList = routeDataManager.getAllItems();
        File[] files = testDir.listFiles();
        assertEquals(loop, files.length);
        assertEquals(loop, itemArrayList.size());

        RouteDataManager anotherRouteDataManager = new RouteDataManager(testDirPath);
        anotherRouteDataManager.restoreAllItemsFromDisc();
        itemArrayList = routeDataManager.getAllItems();
        files = testDir.listFiles();
        assertEquals(loop, files.length);
        assertEquals(loop, itemArrayList.size());

    }

}
