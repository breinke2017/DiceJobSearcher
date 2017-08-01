package com.androidexample.diceapp2;

import android.view.View;

/**
 * Created by brian.reinke on 7/1/2017.
 */

public interface ClickListener{
    public void onClick(View view, int position);
    public void onLongClick(View view, int position);
}

