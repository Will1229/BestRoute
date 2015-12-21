package com.will.studio.bestroute.Backend;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by egaozhi on 2015-12-21.
 */
public interface RouteDataManager {

    boolean saveData(String destDir, RouteItem newItem);

    RouteItem readData(String srcDir);

    ArrayList<RouteItem> readAllData(String srcDir);
}
