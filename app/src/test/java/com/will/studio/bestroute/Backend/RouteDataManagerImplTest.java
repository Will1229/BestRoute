package com.will.studio.bestroute.backend;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by egaozhi on 2015-12-21.
 */
public class RouteDataManagerImplTest extends TestCase {

    private RouteDataManager routeDataManager;
    private String testDirPath;
    private File testDir;

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
        testDir.delete();
    }


    public RouteDataManagerImplTest() {
        routeDataManager = new RouteDataManagerImpl();
        testDirPath = System.getProperty("user.home") + File.separator + "AndroidTestDir";
        testDir = new File(testDirPath);
    }

    public void testSaveAndReadOneItem() throws IOException {

        String from = "from place 111";
        String to = "to place 222";
        String time = "11:30";

        RouteItem inputItem = new RouteItem(from, to, time);

        assertNull(inputItem.getFilePath());

        routeDataManager.saveItem(testDirPath, inputItem);

        String filePath = inputItem.getFilePath();
        assertNotNull(filePath);

        File[] files = testDir.listFiles();
        assertEquals(1, files.length);
        RouteItem outputItem = routeDataManager.restoreItem(files[0].getAbsolutePath());
        assertEquals(inputItem.getFrom(), outputItem.getFrom());
        assertEquals(inputItem.getTo(), outputItem.getTo());
        assertEquals(inputItem.getTime(), outputItem.getTime());
        assertEquals(filePath, outputItem.getFilePath());
        if (!files[0].delete()){
            fail();
        }
    }

    public void testReadAllData() throws Exception {
        final int loop = 3;
        String from = "from place 111";
        String to = "to place 222";
        String time = "11:30";
        RouteItem inputItem = new RouteItem(from, to, time);

        for (int i = 0; i < loop; ++i) {
            routeDataManager.saveItem(testDirPath, inputItem);
        }

        ArrayList<RouteItem> itemArrayList = routeDataManager.getAllItems(testDirPath);
        File[] files = testDir.listFiles();
        assertEquals(loop, files.length);
        assertEquals(loop, itemArrayList.size());
    }

}
