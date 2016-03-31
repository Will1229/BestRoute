package com.will.studio.bestroute.activities;

import android.app.Activity;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.will.studio.bestroute.R;
import com.will.studio.bestroute.routeData.RouteDataManager;
import com.will.studio.bestroute.routeData.RouteItem;
import com.will.studio.bestroute.main.Constants;
import com.will.studio.bestroute.main.Constants.SaveItemReturnCode;
import com.will.studio.bestroute.main.TimePickerFragment;

public class NewItemActivity extends AppCompatActivity {

    public static final String TAG = NewItemActivity.class.getSimpleName();
    private RouteItem existingRouteItem;
    private final String currentViewId = "current_view_id";
    private ProgressBar progressBar;
    private LatLng currentFromLatLng;
    private LatLng currentToLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_add_new_item);
        existingRouteItem = (RouteItem) getIntent().getSerializableExtra(Constants
                .EXTRA_NAME_ROUTE_ITEM);
        if (existingRouteItem != null) {
            toolbar.setTitle(R.string.new_item_toolbar_title_edit);
            fillBlanks(existingRouteItem);
            currentFromLatLng = new LatLng(existingRouteItem.getFromLat(), existingRouteItem
                    .getFromLng());
            currentToLatLng = new LatLng(existingRouteItem.getToLat(), existingRouteItem
                    .getToLng());
        } else {
            toolbar.setTitle(R.string.new_item_toolbar_title);
        }

        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar = (ProgressBar) findViewById(R.id.new_item_progressBar);
        progressBar.setVisibility(View.GONE);
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
        EditText editText = (EditText) findViewById(R.id.new_item_from_text);
        editText.setText(routeItem.getFrom());
        editText = (EditText) findViewById(R.id.new_item_to_text);
        editText.setText(routeItem.getTo());
        TextView textView = (TextView) findViewById(R.id.new_item_time);
        textView.setText(routeItem.getTime());
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(this.getFragmentManager(), "timePicker");
    }

    public void showPlacePicker(View view) {
        new ShowPlacePickerTask(this).execute(view.getId());
    }

    public void clearText(View view) {
        EditText editText = null;
        if (view.getId() == R.id.new_item_clear_from_button) {
            editText = (EditText) this.findViewById(R.id.new_item_from_text);
        } else if (view.getId() == R.id.new_item_clear_to_button) {
            editText = (EditText) this.findViewById(R.id.new_item_to_text);
        }
        if (editText != null) editText.setText("");
    }

    private class ShowPlacePickerTask extends AsyncTask<Integer, Void, Void> {

        private final Activity currentActivity;

        private ShowPlacePickerTask(Activity activity) {
            this.currentActivity = activity;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Integer... params) {

            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            Intent intent = null;

            try {
                intent = builder.build(currentActivity);
            } catch (GooglePlayServicesRepairableException
                    | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }

            if (intent != null) {
                intent.putExtra(currentViewId, params[0]);
                startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST_CODE);
            }

            return null;
        }

    }

    private class SaveNewItemTask extends AsyncTask<Void, Integer, SaveItemReturnCode> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected SaveItemReturnCode doInBackground(Void... params) {
            RouteItem newItem = readFromAllText();
            if (newItem == null) {
                return SaveItemReturnCode.EMPTY_ADD;
            }
            if (currentFromLatLng == null) {
                return SaveItemReturnCode.ILLEGAL_FROM;
            } else if (currentToLatLng == null) {
                return SaveItemReturnCode.ILLEGAL_TO;
            }

            newItem.setFromLat(currentFromLatLng.latitude);
            newItem.setFromLng(currentFromLatLng.longitude);
            newItem.setToLat(currentToLatLng.latitude);
            newItem.setToLng(currentToLatLng.longitude);

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

            progressBar.setVisibility(View.GONE);

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

        EditText editText = (EditText) findViewById(R.id.new_item_from_text);
        String from = editText.getText().toString();
        editText = (EditText) findViewById(R.id.new_item_to_text);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.PLACE_PICKER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                int viewId = data.getIntExtra(this.currentViewId, -1);
                Place place = PlacePicker.getPlace(this, data);
                EditText editText = (EditText) findViewById(viewId);
                if (editText != null) {
                    editText.setText(place.getName());
                }

                if (viewId == R.id.new_item_from_text) {
                    currentFromLatLng = place.getLatLng();
                } else if (viewId == R.id.new_item_to_text) {
                    currentToLatLng = place.getLatLng();
                }
            }
        }
    }
}
