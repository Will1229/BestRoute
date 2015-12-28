package com.will.studio.bestroute.Backend;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Created by egaozhi on 2015-12-21.
 */
public class RouteDataManagerImpl implements RouteDataManager {

    private ArrayList<RouteItem> itemList = null;
    private FilenameFilter itemFilter = null;
    private String itemPrefix = "Route_item_";

    public RouteDataManagerImpl() {
        itemList = new ArrayList<>();

        itemFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(itemPrefix);
            }
        };
    }

    @Override
    public boolean saveItem(String destDir, RouteItem newItem) {

        String filePath = destDir + File.separator + itemPrefix + Long.toString(System.currentTimeMillis());
        itemList.add(newItem);
        return newItem.writeToDisc(filePath);
    }

    @Override
    public boolean deleteItem(String destDir, String name) {
        return false;
    }

    @Override
    public boolean deleteAllItems(String destDir) {
        for (RouteItem item : itemList
                ) {
            item.delete();
        }
        itemList.clear();
        return false;
    }

    @Override
    public RouteItem getItem(String destDir) {
        // TODO
        return null;
    }

    @Override
    public ArrayList<RouteItem> getAllItems(String srcDir) {
        return itemList;
    }

    @Override
    public RouteItem restoreItem(String destDir) {
        return RouteItem.restoreFromDisc(destDir);
    }

    @Override
    public void restoreAllItems(String srcDir) {
        File[] fileList = new File(srcDir).listFiles(itemFilter);
        itemList.clear();
        for (File file : fileList
                ) {
            RouteItem item = RouteItem.restoreFromDisc(file.getAbsolutePath());
            itemList.add(item);
        }
    }

}
