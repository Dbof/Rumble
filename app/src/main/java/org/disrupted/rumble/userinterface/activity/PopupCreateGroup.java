/*
 * Copyright (C) 2014 Disrupted Systems
 * This file is part of Rumble.
 * Rumble is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Rumble is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with Rumble.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.disrupted.rumble.userinterface.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import org.disrupted.rumble.R;
import org.disrupted.rumble.message.Group;
import org.disrupted.rumble.userinterface.events.UserCreateGroup;

import de.greenrobot.event.EventBus;

/**
 * @author Marlinski
 */
public class PopupCreateGroup extends Activity {

    private static final String TAG = "PopupCreateGroup";

    private LinearLayout dismiss;
    private EditText     groupNameView;
    private CheckBox     privateGroupCheckBox;
    private ImageButton  createGroupButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_create_group);

        dismiss = (LinearLayout)(findViewById(R.id.new_group_dismiss));
        groupNameView = (EditText)(findViewById(R.id.popup_group_name));
        createGroupButton = (ImageButton)(findViewById(R.id.popup_button_create_group));
        privateGroupCheckBox = (CheckBox)(findViewById(R.id.popup_check_private));

        dismiss.setOnClickListener(onDiscardClick);
        createGroupButton.setOnClickListener(onCreateGroup);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    View.OnClickListener onDiscardClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(groupNameView.getWindowToken(), 0);
            finish();
        }
    };

    View.OnClickListener onCreateGroup = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final Activity activity = PopupCreateGroup.this;
            try {
                if (groupNameView.getText().toString().equals(""))
                    return;
                Group group = new Group(groupNameView.getText().toString());
                EventBus.getDefault().post(new UserCreateGroup(group));
            } catch (Exception e) {
                Log.e(TAG, "[!] " + e.getMessage());
            } finally {
                groupNameView.setText("");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(groupNameView.getWindowToken(), 0);
                finish();
            }
        }
    };

}
