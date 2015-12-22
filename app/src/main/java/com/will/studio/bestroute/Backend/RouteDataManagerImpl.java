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
                if (name.startsWith(itemPrefix)) {
                    return true;
                } else {
                    return false;
                }
            }
        };
    }

    @Override
    public boolean saveItem(String destDir, RouteItem newItem) {

        String filePath = destDir + File.separator + itemPrefix + Long.toString(System.currentTimeMillis());
        return newItem.writeToDisc(filePath);
    }

    @Override
    public RouteItem readItem(String destDir) {
        RouteItem item = new RouteItem();
        item.restoreFromDisc(destDir);
        return item;
    }

    @Override
    public boolean deleteItem(String destDir) {
        return new File (destDir).delete();
    }


    @Override
    public ArrayList<RouteItem> readAllItems(String srcDir) {
        File[] fileList = new File(srcDir).listFiles(itemFilter);
        for (File file : fileList
                ) {
            itemList.add(readItem(file.getAbsolutePath()));
        }

        return itemList;
    }


}
