package com.will.studio.bestroute.frontend.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.will.studio.bestroute.R;
import com.will.studio.bestroute.backend.RouteDataManager;
import com.will.studio.bestroute.backend.RouteItem;

import java.util.ArrayList;

/**
 * Created by egaozhi on 2016-02-02.
 * Project: BestRoute
 */

public class RouteItemListAdapter extends BaseAdapter {

    private final Context context;
    ArrayList<RouteItem> itemList;

    public RouteItemListAdapter(Context context, ArrayList<RouteItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.content_one_item, parent, false);

        final RouteItem item = itemList.get(position);

        TextView time = (TextView) convertView.findViewById(R.id.item_list_time);
        time.setText(item.getTime());
        TextView from = (TextView) convertView.findViewById(R.id.item_list_from);
        from.setText(item.getFrom());
        TextView to = (TextView) convertView.findViewById(R.id.item_list_to);
        to.setText(item.getTo());
        //TODO: show repeat

        Switch aSwitch = (Switch) convertView.findViewById(R.id.item_list_alarm_switch);
        aSwitch.setChecked(item.isSwitchedOn());

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                RouteAlarmScheduler routeAlarmScheduler = new RouteAlarmScheduler(context);
                RouteDataManager routeDataManager = new RouteDataManager(context.getFilesDir()
                        .getAbsolutePath());
                if (isChecked) {
                    routeAlarmScheduler.scheduleAlarm(item);
                    Toast.makeText(context, "Alarm on " + item.getTime() + " is switched on", Toast
                            .LENGTH_SHORT).show();
                } else {
                    routeAlarmScheduler.cancelAlarm(item);
                    Toast.makeText(context, "Alarm on " + item.getTime() + " is switched off", Toast
                            .LENGTH_SHORT).show();
                }
                item.setIsSwitchedOn(isChecked);
                routeDataManager.updateItem(item);
            }
        });

        return convertView;
    }
}

