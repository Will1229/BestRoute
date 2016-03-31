package com.will.studio.bestroute.routeData;

import android.util.Log;

import com.akexorcist.googledirection.model.Direction;

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
        inputItem.setIsSwitchedOn(true);

        assertNull(inputItem.getFilePath());

        int requestCode1 = inputItem.getAlarmRequestCode();
        assertNotNull(requestCode1);
        routeDataManager.saveItem(inputItem);

        String filePath = inputItem.getFilePath();
        assertNotNull(filePath);

        File[] files = testDir.listFiles();
        assertEquals(1, files.length);
        RouteItem outputItem = routeDataManager.restoreItemFromDisc(files[0].getAbsolutePath());
        assertEquals(inputItem, outputItem);

        // Edit
        from = "from place 222";
        to = "to place 333";
        time = "11:33";
        inputItem = new RouteItem(from, to, time);

        inputItem.setFromLat(fromLat);
        inputItem.setFromLng(fromLng);
        inputItem.setToLat(toLat);
        inputItem.setToLng(toLng);
        inputItem.setIsSwitchedOn(false);

        int requestCode2 = inputItem.getAlarmRequestCode();
        assertTrue(requestCode1 != requestCode2);
        routeDataManager.deleteItem(outputItem);
        routeDataManager.saveItem(inputItem);

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

        for (int i = 0; i < loop; ++i) {
            String from = "from place " + Integer.toString(i);
            String to = "to place" + Integer.toString(i * 10);
            String time = "10:" + Integer.toString(10 + i);
            RouteItem inputItem = new RouteItem(from, to, time);
            inputItem.setDirection(new Direction());
            assertNotNull(inputItem.getDirection());
            routeDataManager.saveItem(inputItem);
        }

        ArrayList<RouteItem> itemArrayList = routeDataManager.getAllItems();
        for (RouteItem item : itemArrayList
                ) {
            assertNotNull(item.getDirection());
        }
        File[] files = testDir.listFiles();
        assertEquals(loop, files.length);
        assertEquals(loop, itemArrayList.size());

        RouteDataManager anotherRouteDataManager = new RouteDataManager(testDirPath);
        anotherRouteDataManager.restoreAllItemsFromDisc();
        itemArrayList = routeDataManager.getAllItems();
        files = testDir.listFiles();
        assertEquals(loop, files.length);
        assertEquals(loop, itemArrayList.size());

        itemArrayList = anotherRouteDataManager.getAllItems();
        for (RouteItem item : itemArrayList
                ) {
            assertNull(item.getDirection());
        }

        anotherRouteDataManager.deleteAllItems();
        itemArrayList = anotherRouteDataManager.getAllItems();

        files = testDir.listFiles();
        assertEquals(0, files.length);
        assertEquals(0, itemArrayList.size());
    }

    public void testUpdateOneItem() {
        // New
        String from = "from place 111";
        String to = "to place 222";
        String time = "11:30";
        double fromLat = 1.1;
        double fromLng = 2.2;
        double toLat = 3.3;
        double toLng = 4.4;

        RouteItem originItem = new RouteItem(from, to, time);
        originItem.setFromLat(fromLat);
        originItem.setFromLng(fromLng);
        originItem.setToLat(toLat);
        originItem.setToLng(toLng);
        originItem.setIsSwitchedOn(true);
        assertNull(originItem.getFilePath());
        routeDataManager.saveItem(originItem);

        originItem.setIsSwitchedOn(false);
        routeDataManager.updateItem(originItem);

        File[] files = testDir.listFiles();
        assertEquals(1, files.length);
        RouteItem updatedItem = routeDataManager.restoreItemFromDisc(files[0].getAbsolutePath());
        assertFalse(updatedItem.isSwitchedOn());

        if (!files[0].delete()) {
            fail();
        }
    }
}
