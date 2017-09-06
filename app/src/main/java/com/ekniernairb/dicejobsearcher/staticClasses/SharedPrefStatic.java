package com.ekniernairb.dicejobsearcher.staticClasses;

import com.ekniernairb.dicejobsearcher.networking.QueryURI;

import java.util.ArrayList;

/**
 * Created by brian.reinke on 7/31/2017.
 */

public class SharedPrefStatic {

    public static ArrayList<QueryURI> query_URI;
    public static boolean mEditIntentLoaded;
    public static boolean mEditIntentSaved;
    public static boolean mInitialLoadNetworkData;
    public static boolean mCameFromEditSearchIntent;

    public static String mJobTextStr;
    public static String mJobSkillStr;
    public static String mJobLocationStr;
    public static String mJobAgeStr;


    public static void buildUriQuery() {
        // setup our Uri query.

        // This variable is very temporary.
        ArrayList<QueryURI> query_URI_AL = new ArrayList<>();

        if (!mJobTextStr.contentEquals("")) {
            query_URI_AL.add(new QueryURI("text", mJobTextStr));
        }
        if (!mJobSkillStr.contentEquals("")) {
            query_URI_AL.add(new QueryURI("skill", mJobSkillStr));
        }
        if (!mJobLocationStr.contentEquals("")) {
            query_URI_AL.add(new QueryURI("city", mJobLocationStr));
        }
        if (!mJobAgeStr.contentEquals("")) {
            query_URI_AL.add(new QueryURI("age", mJobAgeStr));
        }

        query_URI_AL.add(new QueryURI("page", String.valueOf(Api_Data.mCurrentPage)));


        if (query_URI == null) {
            query_URI = query_URI_AL;
        } else {
            query_URI.clear();
            query_URI = query_URI_AL;
        }

//        query_URI_AL=null;
    }
}
