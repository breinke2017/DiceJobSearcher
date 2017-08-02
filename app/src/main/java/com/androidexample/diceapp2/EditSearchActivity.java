package com.androidexample.diceapp2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.androidexample.diceapp2.constantContainers.AppConstants;
import com.androidexample.diceapp2.staticClasses.SharedPrefStatic;

/*
* This file is responsible for getting and saving shared preference data for Edit Search Parameters.
* */

public class EditSearchActivity extends AppCompatActivity {
    private EditText editTextJobText;
    private EditText editTextSkill;
    private EditText editTextLocation;
    private EditText editTextAge;

    private String original_jobTextStr ="";
    private String original_jobSkillStr="";
    private String original_jobLocationStr="";
    private String original_jobAgeStr="";

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
        original_jobTextStr=SharedPrefStatic.jobTextStr;
        original_jobSkillStr=SharedPrefStatic.jobSkillStr;
        original_jobLocationStr=SharedPrefStatic.jobLocationStr;
        original_jobAgeStr=SharedPrefStatic.jobAgeStr;

        editTextJobText.setText(original_jobTextStr);
        editTextSkill.setText(original_jobSkillStr);
        editTextLocation.setText(original_jobLocationStr);
        editTextAge.setText(original_jobAgeStr);
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

        // clear out some variables.
//        editSearchSet=null;
    }


    public void onBackPressed() {
        Log.v("!myapp!", "on back pressed!");
        boolean valuesWereChanged=settingChangedValues(this.original_jobTextStr, this.original_jobSkillStr, this.original_jobLocationStr,
                this.original_jobAgeStr);
        if (valuesWereChanged) {
            savePreferences();
        }
        else{
            SharedPrefStatic.editIntentSaved=false;
        }
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
