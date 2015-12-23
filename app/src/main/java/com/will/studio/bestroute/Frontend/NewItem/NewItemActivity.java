package com.will.studio.bestroute.Frontend.NewItem;

import android.app.Activity;
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
    private View.OnClickListener onClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_add_new_item);
        toolbar.setTitle(R.string.toolbar_title);
        setSupportActionBar(toolbar);

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.save_button:
                        RouteItem newItem = readFromAllText();

                        if (newItem != null) {
                            RouteDataManager routeDataManager = new RouteDataManagerImpl();
                            String dir = getApplicationContext().getFilesDir().getAbsolutePath();
                            boolean success = routeDataManager.saveItem(dir, newItem);
                            if (success) {
                                setResult(RESULT_OK);
                                finish();
                            }
                        }
                        break;
                    case R.id.cancel_button:
                        finish();
                        break;
                    case R.id.test_button:
                        break;
                }
            }
        };

        final Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(onClickListener);

        final Button cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(onClickListener);

        final Button testButton = (Button) findViewById(R.id.test_button);
        testButton.setOnClickListener(onClickListener);
    }

    private RouteItem readFromAllText() {

        EditText editText = (EditText) findViewById(R.id.From);
        String from = editText.getText().toString();
        if (from.length() == 0) return null;
        editText = (EditText) findViewById(R.id.To);
        String to = editText.getText().toString();
        if (to.length() == 0) return null;
        editText = (EditText) findViewById(R.id.Time);
        String time = editText.getText().toString();
        if (time.length() == 0) return null;

        RouteItem item = new RouteItem(from, to, time);

        Log.d(TAG, "readFromAllText return: " + item.toString());

        return item;
    }


}
