package com.androidexample.diceapp2.enumContainers;

/**
 * Created by brian.reinke on 7/11/2017.
 */

public enum ADDPREF_ACTION {
    LOADFROMSHAREDPREF(0),
    LOADFROMACTIONMENU(1),
    LOADFROMCLICKEDQUERY(2);

    private final int value;

    private ADDPREF_ACTION(int value) {
        this.value = value;
    }
}
