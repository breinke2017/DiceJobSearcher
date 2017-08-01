package com.androidexample.diceapp2.enumContainers;

/**
 * Created by brian.reinke on 8/1/2017.
 */

public enum UrlData {
    URL(0),
    JOBTITLE(1),
    COMPANY(2),
    LOCATION(3),
    AGE(4);

    private final int value;

    private UrlData(int value) {
        this.value = value;
    }
}
