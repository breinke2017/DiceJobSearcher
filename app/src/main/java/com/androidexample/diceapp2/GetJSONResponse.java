package com.androidexample.diceapp2;

/**
 * Created by brian.reinke on 6/22/2017.
 */

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.androidexample.diceapp2.genericContainers.Jobs;
import com.androidexample.diceapp2.networking.NetworkStreaming;
import com.androidexample.diceapp2.staticClasses.SharedPrefStatic;

import java.net.URL;
import java.util.List;

/*
* 1) Gets JSON responses, 2) Adds them to the listView.
*  Endpoint: http://service.dice.com/api/rest/jobsearch/v1/simple.json
* */
public class GetJSONResponse extends AsyncTaskLoader<List<Jobs>> {
    private static final String SCHEME = "http://";
    private static final String AUTHORITY = "service.dice.com/";
    private static final String RESOURCE = "api/rest/jobsearch/v1/simple.json";
//    public static ArrayList<QueryURI> query_URI_AL = new ArrayList<>();

//    private ArrayList<QueryURI> query_URI_AL = new ArrayList<>();

    private URL myURLURL = null;
    private String myURLString = null;
    public String JSONResponse = null;
//    private Context mainContext = null;


    public GetJSONResponse(Context context) {
        super(context);
//        this.mainContext = context;
    }

    // URL API that we will be pulling down...
    // Endpoint: http://service.dice.com/api/rest/jobsearch/v1/simple.json


    @Override
    public void onStartLoading() {
    }


    @Override
    public List<Jobs> loadInBackground() {
        //preExecute stuff!!

        // This is not like AsyncTask...these values stay loaded. So, we want to remove them every time.
//        query_URI_AL.removeAll(SharedPrefStatic.query_URI_AL);


//        String newQueryLookup=MainActivity.queryLookup.replace(" ", "");
//        query_URI_AL.add(new QueryURI("api-key", "431fa4f4-baa3-4b01-8584-6dfe2fa215fe"));
//        query_URI_AL.add(new QueryURI("page", String.valueOf(MainActivity.currentPage)));
//        query_URI_AL.add(new QueryURI("from-date", "2017-06-01"));

        this.myURLString = NetworkStreaming.BuildUriURL(SCHEME, AUTHORITY, RESOURCE, SharedPrefStatic.query_URI);
        this.myURLURL = NetworkStreaming.ConverttoURL(myURLString);

        // Stuff to actually do in background.
        this.JSONResponse = NetworkStreaming.makeHttpRequest(this.myURLURL);

        List<Jobs> jobs_arraylist;
        jobs_arraylist = JSONParsing.extractJobsItems(this.JSONResponse);

        MainActivity.jobs_AL =jobs_arraylist;

        return jobs_arraylist;
    }
}