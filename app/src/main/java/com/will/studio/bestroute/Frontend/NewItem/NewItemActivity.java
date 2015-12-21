package com.will.studio.bestroute.Frontend.NewItem;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.will.studio.bestroute.Backend.RouteDataManager;
import com.will.studio.bestroute.Backend.RouteDataManagerImpl;
import com.will.studio.bestroute.Backend.RouteItem;
import com.will.studio.bestroute.R;

public class NewItemActivity extends AppCompatActivity {

    public static final String TAG = NewItemActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_add_new_item);
        toolbar.setTitle(R.string.toolbar_title);
        setSupportActionBar(toolbar);

        final Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouteItem newItem = readFromAllText();

                RouteDataManager routeDataManager = new RouteDataManagerImpl();
                String dir = getApplicationContext().getFilesDir().getAbsolutePath();
                boolean success = routeDataManager.saveData(dir, newItem);
                if (success) {
                    finish();
                }
            }
        });

        final Button cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final Button testButton = (Button) findViewById(R.id.test_button);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private RouteItem readFromAllText() {

        EditText editText = (EditText) findViewById(R.id.From);
        String from = editText.getText().toString();
        editText = (EditText) findViewById(R.id.To);
        String to = editText.getText().toString();
        editText = (EditText) findViewById(R.id.Time);
        String time = editText.getText().toString();

        RouteItem item = new RouteItem(from, to, time);

        Log.d(TAG, "readFromAllText return: " + item.toString());

        return item;
    }


}
