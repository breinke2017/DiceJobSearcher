package com.ekniernairb.dicejobsearcher;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ekniernairb.dicejobsearcher.constantContainers.AppConstants;
import com.ekniernairb.dicejobsearcher.genericContainers.ColorStyles;
import com.ekniernairb.dicejobsearcher.genericContainers.Jobs;
import com.ekniernairb.dicejobsearcher.networking.NetworkStreaming;
import com.ekniernairb.dicejobsearcher.staticClasses.Api_Data;
import com.ekniernairb.dicejobsearcher.staticClasses.ColorSchemeButtons;
import com.ekniernairb.dicejobsearcher.staticClasses.SharedPrefStatic;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Jobs>> {

    private RecyclerView mRecyclerView;
    public static List<Jobs> jobs_AL = new ArrayList<>();

    private Button mBut_prevpage;
    private Button mBut_nextpage;
    private TextView mTxt_RecyclerViewMessage;

    // Widget variables to keep track of.
    private MenuItem mMenu_savedSearches = null;
    private MenuItem mMenu_refresh = null;
    private MenuItem mMenu_color = null;

    private ProgressBar mMyProgressBar;

    /*
    * Reset RecyclerView onClick counters.  Because I'm hitting next page, it triggers a
      * reload of data.  Each time this occurs, a new RecyclerView listener is created.  This
      * causes a problem because it then triggers onClick() numerous extra times.
    * */
    private int mRecyclerViewClickCounts;
    private int mRecyclerViewListenerCounter = 0;
    private boolean mIgnoreRecyclerViewClickEvent;
    // -------------------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Must be done prior to setContentView
        if (ColorSchemeButtons.hasSelectedColor) {
            //change color scheme.
            setActivityTheme(this, ColorSchemeButtons.colorButtonSelected);
        } else {
            // overrides the manifest file setting for theme.
            this.setTheme(R.style.ColorSchemeDefault);
        }

        setContentView(R.layout.activity_main);

        setupFindByViewIDs();

        // load color list scheme
        ColorSchemeButtons.colorStylesList = loadColorSchemeList(ColorSchemeButtons.colorStylesList);

        Api_Data.mCurrentPage = 1;

        // setup click listeners.
        setupListeners();

        // load SharedPreferences
        setupSharedPreferences(AppConstants.PREF_FILENAME);
        SharedPrefStatic.mInitialLoadNetworkData = true;

        /*
        * Reset RecyclerView onClick counters.  Because I'm hitting next page, it triggers a
          * reload of data.  Each time this occurs, a new RecyclerView listener is created.  This
          * causes a problem because it then triggers onClick() numerous extra times.
        * */
        resetRecyclerViewOnClickCounters();
    }


    private void setupFindByViewIDs() {
        mBut_prevpage = (Button) findViewById(R.id.but_prevpage);
        mBut_nextpage = (Button) findViewById(R.id.but_nextpage);


        mTxt_RecyclerViewMessage = (TextView) findViewById(R.id.txt_RecyclerViewMessage);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerviewWidget);


        mMyProgressBar = (ProgressBar) this.findViewById(R.id.progressbar);
        mMyProgressBar = (ProgressBar) this.findViewById(R.id.progressbar);
        if (Build.VERSION.SDK_INT >= 21) {
            mMyProgressBar.setElevation(12);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        SharedPrefStatic.mCameFromEditSearchIntent = false;

        // verified.
        if (SharedPrefStatic.mInitialLoadNetworkData == true || (SharedPrefStatic.mEditIntentLoaded == true && SharedPrefStatic.mEditIntentSaved == true)) {

            triggerOurQuery();

            // Call after hitting this so we don't keep initiating a load.
            SharedPrefStatic.mInitialLoadNetworkData = false;
            SharedPrefStatic.mEditIntentLoaded = false;
            SharedPrefStatic.mEditIntentSaved = false;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*
    * Menu listeners/hooks.
    * */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate our menus.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
//        inflater.inflate(R.menu.app_share_menu, menu);
//        inflater.inflate(R.menu.app_settings_menu, menu);

        // Also, setup other menu finditems.
        //todo: Implement these in future versions.  The refresh should be removed.
        mMenu_refresh = menu.findItem(R.id.menu_refresh);
        mMenu_color = menu.findItem(R.id.menu_color);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_item_editsearches:
                Intent i = new Intent(this, EditSearchActivity.class);
                SharedPrefStatic.mEditIntentLoaded = true;
                SharedPrefStatic.mEditIntentSaved = false;
                startActivity(i);
                return true;
            case R.id.menu_refresh:
                return true;
            case R.id.menu_color:
                Intent intent = new Intent(this, ColoringSchemeActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<List<Jobs>> onCreateLoader(int id, final Bundle args) {
        //Here we will initiate AsyncTaskLoader
        mMyProgressBar.setVisibility(View.VISIBLE);

        return new GetJSONResponse(this);
    }

    @Override
    public void onLoaderReset(Loader<List<Jobs>> loader) {
//        loader = null;
    }


    @Override
    public void onLoadFinished(Loader<List<Jobs>> loader, List<Jobs> jobs_al) {
        // onLoadFinished must have a List<T> type!


        // set next/prev buttons
        if (Api_Data.mLastDocument < Api_Data.mCount) {
            mBut_nextpage.setVisibility(View.VISIBLE);
        } else {
            mBut_nextpage.setVisibility(View.INVISIBLE);
        }

        if (Api_Data.mCurrentPage > 1) {
            mBut_prevpage.setVisibility(View.VISIBLE);
        } else {
            mBut_prevpage.setVisibility(View.INVISIBLE);
        }

        // Runs on each load finished, and runs after the close of every intent.

        // Loads the data into the recyclerview and displays it.

        RecyclerView.Adapter adapter = setupRecyclerViewAdapter(MainActivity.jobs_AL);

        // creates a new OnItemtouchListener for RecyclerView.
        setupRecyclerViewListeners(adapter, MainActivity.jobs_AL);
    }

    private void setupRecyclerViewListeners(RecyclerView.Adapter adapter, List<Jobs> jobs_al) {
        mRecyclerViewListenerCounter++;

        final List<Jobs> jobs_al_copy = jobs_al;

        //todo: I tried taking this out and for whatever reason it won't work.  The onclick fails.
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                mRecyclerView, new ClickListener() {

            @Override
            public void onClick(View view, final int position) {
                // Add your onClick() code here.

                mRecyclerViewClickCounts++;
                if (mRecyclerViewListenerCounter - mRecyclerViewClickCounts == 0)
                    mIgnoreRecyclerViewClickEvent = false;

                // Determines internet connectivity...if working, then display the data otherwise show internet error.
                // no special query text.
                if (!mIgnoreRecyclerViewClickEvent) {
                    boolean hasInternetConnection = checkInternetConnectivity(MainActivity.this);
                    if (hasInternetConnection) {

                        // Get information from our arraylist regarding this position.
                        Jobs curJobsItem = jobs_al_copy.get(position);
                        //    String webURL = curJobsItem.getDetailURL();


                        // Build an arrayList that will have needed information from List<Jobs>.
                        ArrayList<String> jobsUrlData = new ArrayList<>(4);
                        jobsUrlData.add(0, jobs_al_copy.get(position).getDetailURL());
                        jobsUrlData.add(1, jobs_al_copy.get(position).getJobTitle());
                        jobsUrlData.add(2, jobs_al_copy.get(position).getCompany());
                        jobsUrlData.add(3, jobs_al_copy.get(position).getLocation());
                        jobsUrlData.add(4, jobs_al_copy.get(position).getPostingDate());


                        // sets up intent to open as WebView.
                        // loads up webview to see the data.
                        Intent mIntentWebViewActivity;
                        mIntentWebViewActivity = new Intent(MainActivity.this, WebViewActivity.class);
                        mIntentWebViewActivity.setData(Uri.parse(curJobsItem.getDetailURL()));
                        mIntentWebViewActivity.putStringArrayListExtra(AppConstants.PutExtra_JobURLInfo,
                                jobsUrlData);
                        startActivity(mIntentWebViewActivity);

                        /*
                        * Reset RecyclerView onClick counters.  Because I'm hitting next page, it triggers a
                          * reload of data.  Each time this occurs, a new RecyclerView listener is created.  This
                          * causes a problem because it then triggers onClick() numerous extra times.
                        * */
                        resetRecyclerViewOnClickCounters();
                    }
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                // Add your onLongClick() code here.
            }
        }));


        // determine if adapter (recyclerview) has objects or not.
        if (adapter.getItemCount() == 0) {
            // No records to show.
            mTxt_RecyclerViewMessage.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        } else {
            mTxt_RecyclerViewMessage.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        // display pages.
        ProgressBar progressBar = (ProgressBar) this.findViewById(R.id.progressbar);
        progressBar.setVisibility(View.INVISIBLE);
    }


    /*
    * Reset RecyclerView onClick counters.  Because I'm hitting next page, it triggers a
      * reload of data.  Each time this occurs, a new RecyclerView listener is created.  This
      * causes a problem because it then triggers onClick() numerous extra times.
    * */
    private void resetRecyclerViewOnClickCounters() {
        mIgnoreRecyclerViewClickEvent = true;
        mRecyclerViewClickCounts = 0;
    }


    private RecyclerView.Adapter setupRecyclerViewAdapter(List<Jobs> jobs_al) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        JobsRecyclerViewAdapter adapter = new JobsRecyclerViewAdapter(jobs_al);
        mRecyclerView.setAdapter(adapter);
        return adapter;
    }


    // User clicked the submit query button
    private void setupListeners() {
        // Prev page listener
        mBut_prevpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Api_Data.mCurrentPage -= 1;

                SharedPrefStatic.buildUriQuery();

                getLoaderManager().destroyLoader(0);

                runNetworkQuery(MainActivity.this);
            }
        });

        // Next page listener
        mBut_nextpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Api_Data.mCurrentPage += 1;
//                mRecyclerViewClickCounts++;

                SharedPrefStatic.buildUriQuery();
                getLoaderManager().destroyLoader(0);
                runNetworkQuery(MainActivity.this);
            }
        });
    }


    /*
    *  Gets called when the user hits the submit button or when a screen rotation occurs.
    * */
    public void triggerOurQuery() {
        runNetworkQuery(MainActivity.this);
    }


    public void runNetworkQuery(Context context) {
//        TextView txt_multiDisplay= (TextView) findViewById(R.id.txt_multipurpose);
//        txt_multiDisplay.setVisibility(View.VISIBLE);

        // Determines internet connectivity...if working, then display the data otherwise show internet error.
        // no special query text.
        boolean hasInternetConnection = checkInternetConnectivity(context);

        if (hasInternetConnection) {
            // Run the query here.
            getLoaderManager().initLoader(0, null, this).forceLoad();
        }
    }

    private boolean checkInternetConnectivity(Context context) {
        // Determines internet connectivity...if working, then display the data otherwise show internet error.
        // no special query text.
        boolean hasInternetConnection = NetworkStreaming.checkInternetConnection(context.getApplicationContext());
        if (!hasInternetConnection) {
//            progressBar.setVisibility(View.INVISIBLE);
            mTxt_RecyclerViewMessage.setText(R.string.noInternetConnection_msg);
            mTxt_RecyclerViewMessage.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        } else {
            mTxt_RecyclerViewMessage.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
        return hasInternetConnection;
    }


    /*
*  Sets up SharedPreferences.
* Technically, we probably should have two preferences:
*   1) Most recent query value.
*   2) Set that we'll read in from .getStringSet() --> Is what we will implement now.
* */
    private void setupSharedPreferences(String prefFileName) {
        // Get Shared Preferences.
        SharedPreferences sharedPreferences_ob;

        sharedPreferences_ob = this.getSharedPreferences(prefFileName, 0);

        SharedPrefStatic.mJobTextStr = sharedPreferences_ob.getString(AppConstants.PREF_KEY_TEXT, "");
        SharedPrefStatic.mJobSkillStr = sharedPreferences_ob.getString(AppConstants.PREF_KEY_SKILL, "");
        SharedPrefStatic.mJobLocationStr = sharedPreferences_ob.getString(AppConstants.PREF_KEY_LOCATION, "");
        SharedPrefStatic.mJobAgeStr = sharedPreferences_ob.getString(AppConstants.PREF_KEY_AGE, "");

        // initial load.
        SharedPrefStatic.buildUriQuery();
    }


    /*
    * Loading pre-defined values that look look.  These are combinations taken from Google Material Design:
    *   https://material.io/color/#!/?view.left=0&view.right=0
    *
    *   View.generateViewId() generates resID (API 17 and higher)
    * */
    private List<ColorStyles> loadColorSchemeList(List<ColorStyles> colorStylesList) {
        colorStylesList.add(new ColorStyles(View.generateViewId(), "#42a5f5", "#0077c2", "#80d6ff", "#000000"));
        colorStylesList.add(new ColorStyles(View.generateViewId(), "#3F51B5", "#002984", "#757de8", "#ffffff"));
        colorStylesList.add(new ColorStyles(View.generateViewId(), "#f48fb1", "#bf5f82", "#ffc1e3", "#000000"));
        colorStylesList.add(new ColorStyles(View.generateViewId(), "#689f38", "#387002", "#99d066", "#000000"));

        return colorStylesList;
    }

    /*
    * Sets the Activities' theme.  Note: While technically not part of the Activity theme, also am including
    * CardView and the TextView objects to change too.  They are logically part of this same task.
    * */
    private void setActivityTheme(Context context, int colorButtonSelected) {
        // must be called before any Activity instantiation!
        switch (colorButtonSelected) {
            case 0:
                context.setTheme(R.style.ColorSchemeButton0);
                break;
            case 1:
                context.setTheme(R.style.ColorSchemeButton1);
                break;
            case 2:
                context.setTheme(R.style.ColorSchemeButton2);
                break;
            case 3:
                context.setTheme(R.style.ColorSchemeButton3);
                break;
        }
    }
}
