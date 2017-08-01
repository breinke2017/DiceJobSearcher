package com.androidexample.diceapp2.staticClasses;

import com.androidexample.diceapp2.networking.QueryURI;

import java.util.ArrayList;

/**
 * Created by brian.reinke on 7/31/2017.
 */

public class SharedPrefStatic {

    public static ArrayList<QueryURI> query_URI;
    public static boolean editIntentLoaded;
    public static boolean editIntentSaved;
    public static boolean initialLoadNetworkData;

    public static String jobTextStr;
    public static String jobSkillStr;
    public static String jobLocationStr;
    public static String jobAgeStr;


    public static void buildUriQuery() {
        // setup our Uri query.
        ArrayList<QueryURI> query_URI_AL = new ArrayList<>();
        if (!jobTextStr.contentEquals("")) {
            query_URI_AL.add(new QueryURI("text", jobTextStr));
        }
        if (!jobSkillStr.contentEquals("")) {
            query_URI_AL.add(new QueryURI("skill", jobSkillStr));
        }
        if (!jobLocationStr.contentEquals("")) {
            query_URI_AL.add(new QueryURI("city", jobLocationStr));
        }
        if (!jobAgeStr.contentEquals("")) {
            query_URI_AL.add(new QueryURI("age", jobAgeStr));
        }

        if (query_URI==null) {
            query_URI=query_URI_AL;
        }
        else {
            query_URI.clear();
            query_URI=query_URI_AL;
        }

        query_URI_AL=null;
    }
}
