package com.androidexample.diceapp2;

import android.view.View;

// This interface simply makes the click listeners more commonly named.
public interface ClickListener{
    public void onClick(View view, int position);
    public void onLongClick(View view, int position);
}

