package com.ekniernairb.dicejobsearcher;

/**
 * Created by brian.reinke on 6/22/2017.
 */

import com.ekniernairb.dicejobsearcher.genericContainers.Jobs;
import com.ekniernairb.dicejobsearcher.staticClasses.Api_Data;

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
        Api_Data.mCount = 0;
        Api_Data.mFirstDocument = 0;
        Api_Data.mLastDocument = 0;

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // First, create a json object.  Note: Classes are Objects and they
            //  can also be data-types!
            JSONObject baseJsonResponse = new JSONObject(JSON_RESPONSE);

            // Here is where we get the page info from.

            Api_Data.mCount = baseJsonResponse.getInt("count");
            Api_Data.mFirstDocument = baseJsonResponse.getInt("firstDocument");
            Api_Data.mLastDocument = baseJsonResponse.getInt("lastDocument");

            // Next, get a list of arrays from JSON...everything we want is in the 'items' array.
            // How this API is structured: all books in the API exist in this 'items' array.
            // Each array item is a different book.
            if (Api_Data.mCount > 0) {
                JSONArray jobs_jsonArray = baseJsonResponse.getJSONArray("resultItemList");

                // iterate over the list of JSON values.
                for (int i = 0; i < jobs_jsonArray.length(); i++) {
                    JSONObject currentJobsObject = jobs_jsonArray.getJSONObject(i);

                    String detailUrl, jobTitle, company, location, date;

                    detailUrl = currentJobsObject.getString("detailUrl");
                    jobTitle = currentJobsObject.getString("jobTitle");
                    company = currentJobsObject.getString("company");
                    location = currentJobsObject.getString("location");
                    date = currentJobsObject.getString("date");

                    // Adds the records to the book ArrayList
                    jobsAL.add(new Jobs(detailUrl, location, date, company, jobTitle));
                }
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.

            if (Api_Data.mCount == 0) {
            }
        }

        // Return the list of jobs array
        return jobsAL;
    }
}
