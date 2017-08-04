package com.ekniernairb.dicejobsearcher;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.ekniernairb.dicejobsearcher.constantContainers.AppConstants;
import com.ekniernairb.dicejobsearcher.staticClasses.SharedPrefStatic;

/*
* This file is responsible for getting and saving shared preference data for Edit Search Parameters.
* */

public class EditSearchActivity extends AppCompatActivity {
    private EditText editTextJobText;
    private EditText editTextSkill;
    private EditText editTextLocation;
    private EditText editTextAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        editTextJobText.setText(SharedPrefStatic.jobTextStr);
        editTextSkill.setText(SharedPrefStatic.jobSkillStr);
        editTextLocation.setText(SharedPrefStatic.jobLocationStr);
        editTextAge.setText(SharedPrefStatic.jobAgeStr);
    }

    private void setupListeners() {
        editTextJobText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // user pressed action button (enter)
                return false;
            }
        });

        editTextSkill.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // user pressed action button (enter)
                return false;
            }
        });

        editTextLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // user pressed action button (enter)
                return false;
            }
        });

        editTextAge.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        boolean valuesWereChanged=settingChangedValues(SharedPrefStatic.jobTextStr, SharedPrefStatic.jobSkillStr,
                SharedPrefStatic.jobLocationStr, SharedPrefStatic.jobAgeStr);
        if (valuesWereChanged) {
            savePreferences();
        }
        else{
            SharedPrefStatic.editIntentSaved=false;
        }

        SharedPrefStatic.cameFromEditSearchIntent=true;
        finish();
    }

    private boolean settingChangedValues(String origJobText, String origJobSkill, String origLocation,
                                         String origAge) {
        // Have we even changed any values?  Returns true if so.
        if (!origJobText.contentEquals(editTextJobText.getText().toString()) ||
                !origJobSkill.contentEquals(editTextSkill.getText().toString()) ||
                !origLocation.contentEquals(editTextLocation.getText().toString()) ||
                !origAge.contentEquals(editTextAge.getText().toString())) {
            return true;
        }
        else return false;
    }

    private void savePreferences() {
        // Save the preferences.

        // Save settings to our location.
        SharedPrefStatic.jobTextStr = editTextJobText.getText().toString().replace(" ", "");
        SharedPrefStatic.jobSkillStr = editTextSkill.getText().toString().replace(" ", "");
        SharedPrefStatic.jobLocationStr = editTextLocation.getText().toString().replace(" ", "");
        SharedPrefStatic.jobAgeStr = editTextAge.getText().toString().replace(" ", "");

        SharedPreferences sharedPref = getSharedPreferences(AppConstants.PREF_FILENAME, 0);
        SharedPreferences.Editor editer = sharedPref.edit();
        editer.putString(AppConstants.PREF_KEY_TEXT, SharedPrefStatic.jobTextStr);
        editer.putString(AppConstants.PREF_KEY_SKILL, SharedPrefStatic.jobSkillStr);
        editer.putString(AppConstants.PREF_KEY_LOCATION, SharedPrefStatic.jobLocationStr);
        editer.putString(AppConstants.PREF_KEY_AGE, SharedPrefStatic.jobAgeStr);

        // The commit runs faster.
        editer.apply();

        // Run this every time we're building a query.
        SharedPrefStatic.buildUriQuery();
        SharedPrefStatic.editIntentSaved = true;
    }

    private void setupFindByViewIds() {
        editTextJobText = (EditText)findViewById(R.id.editText_edit_search_jobText);
        editTextSkill = (EditText)findViewById(R.id.editText_edit_search_skill);
        editTextLocation = (EditText)findViewById(R.id.editText_edit_search_location);
        editTextAge = (EditText)findViewById(R.id.editText_edit_search_age);
    }
}
