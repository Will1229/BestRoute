package com.will.studio.bestroute.Backend;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by egaozhi on 2015-12-21.
 */
public interface RouteDataManager {

    boolean saveItem(String destDir, RouteItem newItem);

    RouteItem readItem(String srcDir);

    boolean deleteItem(String destDir);

    ArrayList<RouteItem> readAllItems(String srcDir);
}
