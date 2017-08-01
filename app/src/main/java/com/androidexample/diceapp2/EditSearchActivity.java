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

    private SharedPreferences sharedPref;

    private String jobTextStr ="";
    private String jobSkillStr="";
    private String jobLocationStr="";
    private String jobAgeStr="";

    private boolean newEditTextValues=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editsearch);

        Log.v("!myapp!", "*** WebActivity created!");

        // setup findByViewIds.
        setupFindByViewIds();

        // Load Widgets with Data.
        editTextJobText.setText(SharedPrefStatic.jobTextStr);
        editTextSkill.setText(SharedPrefStatic.jobSkillStr);
        editTextLocation.setText(SharedPrefStatic.jobLocationStr);
        editTextAge.setText(SharedPrefStatic.jobAgeStr);

        // setup listeners
        setupListeners();
    }

    private void setupListeners() {
        editTextJobText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // new value possibly entered.
                String tempString;
                tempString=v.getText().toString();
                if (!SharedPrefStatic.jobTextStr.contentEquals(tempString)) {
                    SharedPrefStatic.jobTextStr=tempString;

                    savePreferenceSetting(MainActivity.sharedPref);
                    SharedPrefStatic.editIntentSaved=true;
                }
                return false;
            }
        });

        editTextSkill.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // new value possibly entered.
                String tempString;
                tempString=v.getText().toString();
                if (!SharedPrefStatic.jobSkillStr.contentEquals(tempString)) {
                    SharedPrefStatic.jobSkillStr=tempString;
                    newEditTextValues=true;

                    savePreferenceSetting(MainActivity.sharedPref);
                    SharedPrefStatic.editIntentSaved=true;
                }
                return false;
            }
        });

        editTextLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // new value possibly entered.
                String tempString;
                tempString=v.getText().toString();
                if (!SharedPrefStatic.jobLocationStr.contentEquals(tempString)) {
                    SharedPrefStatic.jobLocationStr=tempString;
                    newEditTextValues=true;

                    savePreferenceSetting(MainActivity.sharedPref);
                    SharedPrefStatic.editIntentSaved=true;
                }
                return false;
            }
        });

        editTextAge.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // new value possibly entered.
                String tempString;
                tempString=v.getText().toString();
                if (!SharedPrefStatic.jobAgeStr.contentEquals(tempString)) {
                    SharedPrefStatic.jobAgeStr=tempString;
                    newEditTextValues=true;

                    savePreferenceSetting(MainActivity.sharedPref);
                    SharedPrefStatic.editIntentSaved=true;
                }
                return false;
            }
        });
    }


    protected void onStop() {
        // Save the preferences.
        super.onStop();

        Log.v("!myapp!", "*** hit onstop in EditSearchActivity!");
        Log.v("!myapp!", "*** SharedPrefStatic.query_URI is : " + SharedPrefStatic.query_URI);
    }

    protected void onDestroy() {
        super.onDestroy();

        // clear out some variables.
//        editSearchSet=null;
    }

    private void setupFindByViewIds() {
        editTextJobText = (EditText)findViewById(R.id.editText_edit_search_jobText);
        editTextSkill = (EditText)findViewById(R.id.editText_edit_search_skill);
        editTextLocation = (EditText)findViewById(R.id.editText_edit_search_location);
        editTextAge = (EditText)findViewById(R.id.editText_edit_search_age);
    }


    private void savePreferenceSetting(SharedPreferences sharedPref) {
        // Save the preference.
        SharedPreferences.Editor editer = sharedPref.edit();

        jobTextStr = editTextJobText.getText().toString().replace(" ","");
        jobSkillStr=editTextSkill.getText().toString().replace(" ","");
        jobLocationStr=editTextLocation.getText().toString().replace(" ","");
        jobAgeStr=editTextAge.getText().toString().replace(" ","");

        editer.putString(AppConstants.PREF_KEY_TEXT, jobTextStr);
        editer.putString(AppConstants.PREF_KEY_SKILL, jobSkillStr);
        editer.putString(AppConstants.PREF_KEY_LOCATION, jobLocationStr);
        editer.putString(AppConstants.PREF_KEY_AGE, jobAgeStr);
        editer.apply();

        // Run this every time we're building a query.
        SharedPrefStatic.buildUriQuery();
    }
}
