package com.will.studio.bestroute.frontend.main.activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.will.studio.bestroute.R;
import com.will.studio.bestroute.backend.GoogleDirectionHelper;
import com.will.studio.bestroute.backend.RouteDataManager;
import com.will.studio.bestroute.backend.RouteItem;
import com.will.studio.bestroute.frontend.main.Constants;
import com.will.studio.bestroute.frontend.main.Constants.SaveItemReturnCode;
import com.will.studio.bestroute.frontend.main.TimePickerFragment;

public class NewItemActivity extends AppCompatActivity {

    public static final String TAG = NewItemActivity.class.getSimpleName();
    private RouteItem existingRouteItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_add_new_item);
        toolbar.setTitle(R.string.new_item_toolbar_title);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        existingRouteItem = (RouteItem) getIntent().getSerializableExtra(Constants
                .EXTRA_NAME_ROUTE_ITEM);
        if (existingRouteItem != null) {
            fillBlanks(existingRouteItem);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_item_action_save:
                new SaveNewItemTask().execute();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fillBlanks(RouteItem routeItem) {
        EditText editText = (EditText) findViewById(R.id.new_item_from);
        editText.setText(routeItem.getFrom());
        editText = (EditText) findViewById(R.id.new_item_to);
        editText.setText(routeItem.getTo());
        TextView textView = (TextView) findViewById(R.id.new_item_time);
        textView.setText(routeItem.getTime());
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(this.getFragmentManager(), "timePicker");
    }

    private class SaveNewItemTask extends AsyncTask<Void, Integer, SaveItemReturnCode> {

        @Override
        protected SaveItemReturnCode doInBackground(Void... params) {
            RouteItem newItem = readFromAllText();
            if (newItem == null) {
                return SaveItemReturnCode.EMPTY_ADD;
            }
            LatLng from = GoogleDirectionHelper.getLocationFromAddress(getApplicationContext(),
                    newItem.getFrom());
            LatLng to = GoogleDirectionHelper.getLocationFromAddress(getApplicationContext(),
                    newItem.getTo());

            if (from == null) {
                return SaveItemReturnCode.ILLEGAL_FROM;
            } else if (to == null) {
                return SaveItemReturnCode.ILLEGAL_TO;
            }

            newItem.setFromLat(from.latitude);
            newItem.setFromLng(from.longitude);
            newItem.setToLat(to.latitude);
            newItem.setToLng(to.longitude);

            String dirPath = getApplicationContext().getFilesDir().getAbsolutePath();
            RouteDataManager routeDataManager = new RouteDataManager(dirPath);
            if (existingRouteItem != null) { // edit on existing item
                routeDataManager.deleteItem(existingRouteItem);
            }
            boolean success = routeDataManager.saveItem(newItem);

            Intent returnIntent = new Intent();
            if (success) {
                returnIntent.putExtra(Constants.UPDATED_ROUTE_ITEM, newItem);
                setResult(Constants.ACTIVITY_RESULT_OK, returnIntent);
                return SaveItemReturnCode.OK;
            } else {
                setResult(Constants.ACTIVITY_RESULT_NOK, returnIntent);
            }
            return SaveItemReturnCode.UNKNOWN_ERROR;
        }

        @Override
        protected void onPostExecute(SaveItemReturnCode returnCode) {
            super.onPostExecute(returnCode);

            switch (returnCode) {
                case OK:
                    finish();
                    break;
                case EMPTY_ADD:
                    Toast.makeText(getApplicationContext(), R.string.new_item_toast_fill_all_blanks,
                            Toast.LENGTH_SHORT).show();
                    break;
                case ILLEGAL_FROM:
                    Toast.makeText(getApplicationContext(), R.string.new_item_toast_illegal_from,
                            Toast.LENGTH_SHORT).show();
                    break;
                case ILLEGAL_TO:
                    Toast.makeText(getApplicationContext(), R.string.new_item_toast_illegal_to,
                            Toast.LENGTH_SHORT).show();
                    break;
                case UNKNOWN_ERROR:
                    Toast.makeText(getApplicationContext(), R.string.new_item_toast_unknown_error,
                            Toast.LENGTH_SHORT).show();
                default:
                    break;
            }
        }
    }

    private RouteItem readFromAllText() {

        EditText editText = (EditText) findViewById(R.id.new_item_from);
        String from = editText.getText().toString();
        editText = (EditText) findViewById(R.id.new_item_to);
        String to = editText.getText().toString();
        String time = ((TextView) findViewById(R.id.new_item_time)).getText().toString();

        String nullTime = this.getString(R.string.new_item_null_time);

        if (from.length() == 0 || to.length() == 0 || time.equals(nullTime)) {
            return null;
        }

        RouteItem item = new RouteItem(from, to, time);

        Log.d(TAG, "readFromAllText return: " + item.toString());

        return item;
    }


}
