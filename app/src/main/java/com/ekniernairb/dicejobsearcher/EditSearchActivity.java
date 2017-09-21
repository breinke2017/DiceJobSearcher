package com.ekniernairb.dicejobsearcher;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.ekniernairb.dicejobsearcher.constantContainers.AppConstants;
import com.ekniernairb.dicejobsearcher.staticClasses.ColorSchemeButtons;
import com.ekniernairb.dicejobsearcher.staticClasses.SharedPrefStatic;

/*
* This file is responsible for getting and saving shared preference data for Edit Search Parameters.
* */

public class EditSearchActivity extends AppCompatActivity {
    private EditText mEditTextJobText;
    private EditText mEditTextSkill;
    private EditText mEditTextLocation;
    private EditText mEditTextAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        /* Changes Activities theme colors.  Must do this prior to setContentView! */
        if (ColorSchemeButtons.hasSelectedColor) {
            //change color scheme.
            ColorSchemeButtons.setActivityTheme(this, ColorSchemeButtons.colorButtonSelected);
        } else {
            // overrides the manifest file setting for theme.
            this.setTheme(R.style.ColorSchemeDefault);
        }


        setContentView(R.layout.activity_editsearch);

        // setup findByViewIds.
        setupFindByViewIds();

        // setup listeners
        setupListeners();

        // get initial setting values.
        getInitialValues();
    }

    private void getInitialValues() {
        // Load Widgets with Data already retrieved from Main Activity.
        mEditTextJobText.setText(SharedPrefStatic.mJobTextStr);
        mEditTextSkill.setText(SharedPrefStatic.mJobSkillStr);
        mEditTextLocation.setText(SharedPrefStatic.mJobLocationStr);
        mEditTextAge.setText(SharedPrefStatic.mJobAgeStr);
    }

    private void setupListeners() {
        mEditTextJobText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // user pressed action button (enter)
                return false;
            }
        });

        mEditTextSkill.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // user pressed action button (enter)
                return false;
            }
        });

        mEditTextLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // user pressed action button (enter)
                return false;
            }
        });

        mEditTextAge.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // user pressed action button (enter)
                return false;
            }
        });
    }


    protected void onStop() {
        // Save the preferences.
        super.onStop();
    }

    protected void onDestroy() {
        super.onDestroy();
    }


    public void onBackPressed() {
        boolean valuesWereChanged = settingChangedValues(SharedPrefStatic.mJobTextStr, SharedPrefStatic.mJobSkillStr,
                SharedPrefStatic.mJobLocationStr, SharedPrefStatic.mJobAgeStr);
        if (valuesWereChanged) {
            savePreferences();
        } else {
            SharedPrefStatic.mEditIntentSaved = false;
        }

        SharedPrefStatic.mCameFromEditSearchIntent = true;
        finish();
    }

    private boolean settingChangedValues(String origJobText, String origJobSkill, String origLocation,
                                         String origAge) {
        // Have we even changed any values?  Returns true if so.
        if (!origJobText.contentEquals(mEditTextJobText.getText().toString()) ||
                !origJobSkill.contentEquals(mEditTextSkill.getText().toString()) ||
                !origLocation.contentEquals(mEditTextLocation.getText().toString()) ||
                !origAge.contentEquals(mEditTextAge.getText().toString())) {
            return true;
        } else return false;
    }

    private void savePreferences() {
        // Save the preferences.

        // Save settings to our location.
        SharedPrefStatic.mJobTextStr = mEditTextJobText.getText().toString().replace(" ", "");
        SharedPrefStatic.mJobSkillStr = mEditTextSkill.getText().toString().replace(" ", "");
        SharedPrefStatic.mJobLocationStr = mEditTextLocation.getText().toString().replace(" ", "");
        SharedPrefStatic.mJobAgeStr = mEditTextAge.getText().toString().replace(" ", "");

        SharedPreferences sharedPref = getSharedPreferences(AppConstants.PREF_FILENAME, 0);
        SharedPreferences.Editor editer = sharedPref.edit();
        editer.putString(AppConstants.PREF_KEY_TEXT, SharedPrefStatic.mJobTextStr);
        editer.putString(AppConstants.PREF_KEY_SKILL, SharedPrefStatic.mJobSkillStr);
        editer.putString(AppConstants.PREF_KEY_LOCATION, SharedPrefStatic.mJobLocationStr);
        editer.putString(AppConstants.PREF_KEY_AGE, SharedPrefStatic.mJobAgeStr);

        // The commit runs faster.
        editer.apply();

        // Run this every time we're building a query.
        SharedPrefStatic.buildUriQuery();
        SharedPrefStatic.mEditIntentSaved = true;
    }

    private void setupFindByViewIds() {
        mEditTextJobText = (EditText) findViewById(R.id.editText_edit_search_jobText);
        mEditTextSkill = (EditText) findViewById(R.id.editText_edit_search_skill);
        mEditTextLocation = (EditText) findViewById(R.id.editText_edit_search_location);
        mEditTextAge = (EditText) findViewById(R.id.editText_edit_search_age);
    }
}
