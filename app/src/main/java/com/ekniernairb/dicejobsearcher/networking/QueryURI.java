package com.ekniernairb.dicejobsearcher.networking;

/**
 * Created by brian.reinke on 6/23/2017.
 */

final public class QueryURI {
    private String mKey;
    private String mValue;

    public QueryURI(String key, String value) {
        this .mKey=key;
        this .mValue=value;
    }

    public String getKey() {
        return mKey;
    }

    public String getValue() {
        return mValue;
    }
}
