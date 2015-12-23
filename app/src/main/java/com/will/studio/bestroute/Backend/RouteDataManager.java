package com.will.studio.bestroute.Backend;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by egaozhi on 2015-12-21.
 */
public interface RouteDataManager {

    boolean saveItem(String destDir, RouteItem newItem);

    RouteItem getItem(String srcDir);

    boolean deleteItem(String destDir, String name);

    boolean deleteAllItems(String destDir);

    ArrayList<RouteItem> getAllItems(String srcDir);

    RouteItem restoreItem(String destDir);

    void restoreAllItems(String srcDir);
}
