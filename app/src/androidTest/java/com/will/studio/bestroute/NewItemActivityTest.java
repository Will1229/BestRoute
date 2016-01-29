package com.will.studio.bestroute;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.will.studio.bestroute.frontend.main.Activities.NewItemActivity;

/**
 * Created by egaozhi on 2016-01-04.
 *
 */
public class NewItemActivityTest extends ActivityInstrumentationTestCase2<NewItemActivity> {

    private NewItemActivity newItemActivity;
    private EditText fromText;
    private EditText toText;
    private TextView alarmText;
    private Button cancelButton;
    private Button saveButton;

    public NewItemActivityTest() {
        super(NewItemActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(true);
        newItemActivity = getActivity();
        fromText = (EditText) newItemActivity.findViewById(R.id.new_item_from);
        toText = (EditText) newItemActivity.findViewById(R.id.new_item_to);
        alarmText = (TextView) newItemActivity.findViewById(R.id.new_item_time);
        cancelButton = (Button) newItemActivity.findViewById(R.id.cancel_button);
        saveButton = (Button) newItemActivity.findViewById(R.id.save_button);

    }

    public void testPreconditions() {
        assertNotNull(newItemActivity);
        assertNotNull(fromText);
        assertNotNull(toText);
        assertNotNull(alarmText);
        assertNotNull(cancelButton);
        assertNotNull(saveButton);
    }
}
