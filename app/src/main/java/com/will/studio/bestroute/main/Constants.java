package com.will.studio.bestroute.main;

/**
 * Created by egaozhi on 2016-01-15.
 * Project: BestRoute
 */
public interface Constants {
    String NOTIFICATION_ID = "notification_id";
    int NOTIFICATION_ID_VALUE = 1;

    String EXTRA_NAME_ROUTE_ITEM = "route_item";

    // request code
    int NOTIFICATION_REQUEST_CODE = 1;
    int UPDATE_ITEM_REQUEST_CODE = 2;
    int PLACE_PICKER_REQUEST_CODE = 3;

    String UPDATED_ROUTE_ITEM = "updated_route_item";

    int ACTIVITY_RESULT_OK = 1;
    int ACTIVITY_RESULT_NOK = 2;
    int ACTIVITY_RESULT_CANCEL = 3;

    enum SaveItemReturnCode {OK, EMPTY_ADD, ILLEGAL_FROM, ILLEGAL_TO, UNKNOWN_ERROR}

    @SuppressWarnings("SpellCheckingInspection")
    String APP_KEY = "AIzaSyDPQ1GwAKKQZaxH1cmyVbx0FLDwKqKlJD8";

    int MAX_ITEM_NUM = 3;
}
