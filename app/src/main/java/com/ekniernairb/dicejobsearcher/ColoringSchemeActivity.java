package com.ekniernairb.dicejobsearcher;

/**
 * Created by brian.reinke on 9/6/2017.
 */


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ekniernairb.dicejobsearcher.genericContainers.ColorStyles;
import com.ekniernairb.dicejobsearcher.staticClasses.ColorSchemeButtons;
import com.ekniernairb.dicejobsearcher.staticClasses.ScaledDisplay;

import java.util.List;


public class ColoringSchemeActivity extends AppCompatActivity {
    private Button[] colorSchemeButton = new Button[ColorSchemeButtons.NUMBER_OF_BUTTONS];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coloringscheme);

        // Load up the buttons programtically.
        colorSchemeButton = createColorSchemeButtons(colorSchemeButton, ColorSchemeButtons.colorStylesList);

        //create listeners on the buttons.
        createButtonListeners(colorSchemeButton, ColorSchemeButtons.colorStylesList);

    }


    private void createButtonListeners(Button[] colorSchemeButton, List<ColorStyles> colorStylesList) {
        // Set click listener.  Manually doing for now because not sure how to pass in an index to the click listener.
        //   (Remember, that variables outside the inner class must be final).  And I can't do this
        // as the index changes.

        colorSchemeButton[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainAppActivity(0);
            }
        });

        colorSchemeButton[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainAppActivity(1);
            }
        });

        colorSchemeButton[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainAppActivity(2);
            }
        });

        colorSchemeButton[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainAppActivity(3);
            }
        });
    }

    private void openMainAppActivity(int colorButtonSelected) {
        ColorSchemeButtons.colorButtonSelected = colorButtonSelected;
        ColorSchemeButtons.hasSelectedColor = true;

        // we have to restart the main app to get the theme settings.
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

        finish();
    }


    private Button[] createColorSchemeButtons(Button[] colorButton, List<ColorStyles> colorStylesList) {
        // Note: height, width: Values: either MATCH_PARENT, WRAP_CONTENT or a fixed size in pixels.

    /*
    * Set coloring scheme to certain dimensions, posititioning.
    * */
        // get layout.
        LinearLayout layoutColorScheme = (LinearLayout) findViewById(R.id.linearLayout_colorButtons);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        // All buttons will have the same height, weight, and positioning.
        layoutParams.height = ScaledDisplay.dpToPx1(this, ScaledDisplay.ScaledDensity.DP, 45);
        layoutParams.width = ScaledDisplay.dpToPx1(this, ScaledDisplay.ScaledDensity.DP, 45);

        int pixelValue = ScaledDisplay.dpToPx1(this, ScaledDisplay.ScaledDensity.DP, 16);
        layoutParams.setMargins(pixelValue, pixelValue, pixelValue, pixelValue);

        // Now, create the buttons at runtime.
        for (int i = 0; i < 4; i++) {
            Button button = new Button(this);

            // sets size, position.
            button.setLayoutParams(layoutParams);


            // sets background color.
            String hex = colorStylesList.get(i).getColorPrimary();
            button.setBackgroundColor(Color.parseColor(hex));
            button.setClickable(true);
            button.setFocusable(true);

            // finally, add the button to the layout.
            layoutColorScheme.addView(button);

            colorButton[i] = button;
        }
        return colorButton;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
