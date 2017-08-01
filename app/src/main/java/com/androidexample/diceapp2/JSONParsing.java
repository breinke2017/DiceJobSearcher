package com.androidexample.diceapp2;

/**
 * Created by brian.reinke on 6/22/2017.
 */

import android.util.Log;

import com.androidexample.diceapp2.genericContainers.Jobs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*
*  Static class that does JSON parsing.
* */

public class JSONParsing {

    /**
     * Create a private constructor because no one should ever create a {@link JSONParsing} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name JSONParsing (and an object instance of JSONParsing is not needed).
     */
    private JSONParsing() {
    }


    /**
     * Return a list of {@link Jobs} objects that has been built up from
     * parsing a JSON response.
     */
    public static List<Jobs> extractJobsItems(String JSON_RESPONSE) {

        // This is a local arraylist we build...then pass back.
        List<Jobs> jobsAL = new ArrayList<>();
        int totalPages=0;
        int firstDocument=0;
        int lastDocument=0;

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // First, create a json object.  Note: Classes are Objects and they
            //  can also be data-types!
            JSONObject baseJsonResponse = new JSONObject(JSON_RESPONSE);

            // Here is where we get the page info from.
            totalPages = baseJsonResponse.getInt("count");
            firstDocument = baseJsonResponse.getInt("firstDocument");
            lastDocument = baseJsonResponse.getInt("lastDocument");

            // Next, get a list of arrays from JSON...everything we want is in the 'items' array.
            // How this API is structured: all books in the API exist in this 'items' array.
            // Each array item is a different book.
            if (totalPages>0) {
                JSONArray jobs_jsonArray = baseJsonResponse.getJSONArray("resultItemList");

                // iterate over the list of JSON values.
                Log.v("!myapp!", "firstDocument: " + firstDocument);
                Log.v("!myapp!", "lastDocument: " + lastDocument);
                Log.v("!myapp!", "total number of records " + totalPages);
                Log.v("!myapp!", "JSON records per page: " + jobs_jsonArray.length());
                for (int i = 0; i < jobs_jsonArray.length(); i++) {
                    JSONObject currentJobsObject = jobs_jsonArray.getJSONObject(i);

                    String detailUrl,jobTitle,company,location,date="";

                    detailUrl = currentJobsObject.getString("detailUrl");
                    jobTitle = currentJobsObject.getString("jobTitle");
                    company = currentJobsObject.getString("company");
                    location = currentJobsObject.getString("location");
                    date = currentJobsObject.getString("date");

                    // Adds the records to the book ArrayList
                    jobsAL.add(new Jobs(totalPages, firstDocument, lastDocument, detailUrl,
                            location, date, company, jobTitle));
                }
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.

            if (totalPages==0) {
                Log.v("!myapp!", "No pages to display for subject!");
            }
            Log.e("!myapp!", "Problem parsing the Jobs JSON results", e);
        }

        // Return the list of jobs array
        return jobsAL;
    }
}
