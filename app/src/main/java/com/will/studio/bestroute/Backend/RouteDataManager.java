package com.will.studio.bestroute.backend;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by egaozhi on 2015-12-21.
 * Project: BestRoute
 */
public class RouteDataManager {

    private ArrayList<RouteItem> itemList = null;
    private FilenameFilter itemFilter = null;
    private String itemPrefix = "Route_item_";
    private String dirPath;

    public RouteDataManager(String dirPath) {
        this.dirPath = dirPath;
        itemList = new ArrayList<>();
        itemFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(itemPrefix);
            }
        };
    }

    public boolean saveItem(RouteItem newItem, String path) {
        String filePath = path;
        if (filePath == null || !filePath.contains(itemPrefix) || !filePath.contains(dirPath)) {
            filePath = dirPath + File.separator + itemPrefix + Long.toString(System.currentTimeMillis());
        }

        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(filePath);
            oos = new ObjectOutputStream(fos);
            newItem.setFilePath(filePath);
            oos.writeObject(newItem);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        itemList.add(newItem);
        Log.d(getClass().getName(), "save item to " + filePath);
        return true;
    }

    public boolean deleteAllItems() {
        for (RouteItem item : itemList
                ) {
            deleteItem(item.getFilePath());
        }
        itemList.clear();
        return false;
    }

    public void deleteItem(String filePath) {
        File file = new File(filePath);
        if (!file.delete()) {
            Log.d(getClass().getName(), "Unable to delete " + filePath);
        }
    }

    public ArrayList<RouteItem> getAllItems() {
        return itemList;
    }


    public void restoreAllItemsFromDisc() {
        File[] fileList = new File(dirPath).listFiles(itemFilter);
        itemList.clear();
        for (File file : fileList
                ) {
            RouteItem item = restoreItemFromDisc(file.getAbsolutePath());
            if (item != null) {
                itemList.add(item);
            }
        }
    }

    public RouteItem restoreItemFromDisc(final String filePath) {

        RouteItem item = null;

        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(filePath);
            ois = new ObjectInputStream(fis);
            item = (RouteItem) ois.readObject();
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (final Exception ioe) {
                ioe.printStackTrace();
            }
        }
        Log.d(getClass().getName(), "restore item from " + filePath);
        return item;
    }

}
