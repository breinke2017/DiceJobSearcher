package com.androidexample.diceapp2;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidexample.diceapp2.constantContainers.AppConstants;
import com.androidexample.diceapp2.genericContainers.Jobs;
import com.androidexample.diceapp2.networking.NetworkStreaming;
import com.androidexample.diceapp2.staticClasses.Api_Data;
import com.androidexample.diceapp2.staticClasses.SharedPrefStatic;

import java.util.ArrayList;
import java.util.List;



// Todo: Doesn't refresh or load when user rotates screen to landscape.

/*
* Todo: Use Jsoup to load in the webpage and put in a variable.  For some reason, when I did it,
* it wasn't loading in all the HTML.  Therefore, it didn't appear correctly  in webview.  I could parse the html and take
* out all the extra Dice-advertising.
* */


//  All when changing the query (but not initial load query)!
// Todo: Bug...when a query opens.  Then if you go to page 2.  Then re-do a query.  That new query shows up on page 2, instead of the 1st page.
// Todo: Bug: If on a query, you're on page 5 then do another query but this query returns only 2 pages, then the page says "no records to display for query."

// Todo: Add caching so loads are faster.  Fox news app, for example, seems to cache because its very fast to load.


/*
* Is the news app with AsyncTask Loader.
*
* */

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Jobs>> {

    private RecyclerView mRecyclerView;
    public static List<Jobs> jobs_AL = new ArrayList<>();

    private Button but_prevpage;
    private Button but_nextpage;
    private TextView txt_RecyclerViewMessage;

    // Widget variables to keep track of.
    private MenuItem menu_savedSearches=null;
    private MenuItem menu_refresh=null;


    /*
    * Reset RecyclerView onClick counters.  Because I'm hitting next page, it triggers a
      * reload of data.  Each time this occurs, a new RecyclerView listener is created.  This
      * causes a problem because it then triggers onClick() numerous extra times.
    * */
    private int recyclerViewClickCounts;
    private int recyclerViewListenerCounter =0;
    private boolean ignoreRecyclerViewClickEvent;
    // -------------------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupFindByViewIDs();

        Api_Data.mCurrentPage = 1;

        // setup click listeners.
        setupListeners();

        // load SharedPreferences
        setupSharedPreferences(AppConstants.PREF_FILENAME);
        SharedPrefStatic.initialLoadNetworkData=true;

        /*
        * Reset RecyclerView onClick counters.  Because I'm hitting next page, it triggers a
          * reload of data.  Each time this occurs, a new RecyclerView listener is created.  This
          * causes a problem because it then triggers onClick() numerous extra times.
        * */
        resetRecyclerViewOnClickCounters();
    }

    private void setupFindByViewIDs() {
        but_prevpage = (Button) findViewById(R.id.but_prevpage);
        but_nextpage = (Button) findViewById(R.id.but_nextpage);


        txt_RecyclerViewMessage =(TextView) findViewById(R.id.txt_RecyclerViewMessage);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerviewWidget);
    }


    @Override
    protected void onStart() {
        super.onStart();

        Log.v("!myapp!", "** MainActivity started **");
//        if (SharedPrefStatic.cameFromEditSearchIntent) {
//            Api_Data.mCurrentPage = 1;
            SharedPrefStatic.cameFromEditSearchIntent = false;
//        }

        // verified.
        if (SharedPrefStatic.initialLoadNetworkData==true || (SharedPrefStatic.editIntentLoaded==true && SharedPrefStatic.editIntentSaved==true)) {

            triggerOurQuery();

            // Call after hitting this so we don't keep initiating a load.
            SharedPrefStatic.initialLoadNetworkData=false;
            SharedPrefStatic.editIntentLoaded=false;
            SharedPrefStatic.editIntentSaved=false;
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
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
//        inflater.inflate(R.menu.app_share_menu, menu);
//        inflater.inflate(R.menu.app_settings_menu, menu);

        // Also, setup other menu finditems.
        //todo: Implement these in future versions.  The refresh should be removed.
//        menu_savedSearches = menu.findItem(R.id.menu_item_savedsearches);
        menu_refresh = menu.findItem(R.id.menu_refresh);

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_item_editsearches:
                Intent i = new Intent(this, EditSearchActivity.class);
                SharedPrefStatic.editIntentLoaded=true;
                SharedPrefStatic.editIntentSaved=false;
                startActivity(i);
                return true;
            case R.id.menu_refresh:
                return true;

//                return true;
//            case R.id.searchMenu:
//                showSearchLayout(this);
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<List<Jobs>> onCreateLoader(int id, final Bundle args) {
        //Here we will initiate AsyncTaskLoader
        ProgressBar myProgressBar=(ProgressBar) this.findViewById(R.id.progressbar);
        myProgressBar.setVisibility(View.VISIBLE);

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
        if (Api_Data.mLastDocument<Api_Data.mCount) {
            but_nextpage.setVisibility(View.VISIBLE);
        }
        else {
            but_nextpage.setVisibility(View.INVISIBLE);
        }

        if (Api_Data.mCurrentPage>1) {
            but_prevpage.setVisibility(View.VISIBLE);
        }
        else {
            but_prevpage.setVisibility(View.INVISIBLE);
        }

        // Runs on each load finished, and runs after the close of every intent.

        // Loads the data into the recyclerview and displays it.

            RecyclerView.Adapter adapter = setupRecyclerViewAdapter(MainActivity.jobs_AL);

            // creates a new OnItemtouchListener for RecyclerView.
            setupRecyclerViewListeners(adapter, MainActivity.jobs_AL);
    }

    private void setupRecyclerViewListeners(RecyclerView.Adapter adapter, List<Jobs> jobs_al) {

        recyclerViewListenerCounter++;

        final List<Jobs> jobs_al_copy = jobs_al;

        //todo: I tried taking this out and for whatever reason it won't work.  The onclick fails.
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                mRecyclerView, new ClickListener() {

            @Override
            public void onClick(View view, final int position) {
                // Add your onClick() code here.

                recyclerViewClickCounts++;
                if (recyclerViewListenerCounter - recyclerViewClickCounts == 0) ignoreRecyclerViewClickEvent =false;

                // Determines internet connectivity...if working, then display the data otherwise show internet error.
                // no special query text.
                if (!ignoreRecyclerViewClickEvent){
                    boolean hasInternetConnection=checkInternetConnectivity(MainActivity.this);
                    if (hasInternetConnection) {

                        // Get information from our arraylist regarding this position.
                        Jobs curJobsItem = jobs_al_copy.get(position);
                        //    String webURL = curJobsItem.getDetailURL();

                        //                // sets up intent to open as webbrowser.
                        //                Intent i = new Intent();
                        //                i.setAction(Intent.ACTION_VIEW);
                        //                i.addCategory(Intent.CATEGORY_BROWSABLE);
                        //                i.setData(Uri.parse(webURL));
                        //                startActivity(i);

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
            txt_RecyclerViewMessage.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);

//            mRecyclerView.setEmptyView(txt_multipurpose);
        }
        else {
            txt_RecyclerViewMessage.setVisibility(View.INVISIBLE);
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
        ignoreRecyclerViewClickEvent =true;
        recyclerViewClickCounts =0;
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
        but_prevpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Api_Data.mCurrentPage -= 1;

                SharedPrefStatic.buildUriQuery();

                getLoaderManager().destroyLoader(0);

                runNetworkQuery(MainActivity.this);
            }
        });

        // Next page listener
        but_nextpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Api_Data.mCurrentPage += 1;
//                recyclerViewClickCounts++;

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
        boolean hasInternetConnection= checkInternetConnectivity(context);

        if (hasInternetConnection) {
//            GetJSONResponse getJSONResponse = new GetJSONResponse(context, queryLookup);
//            getJSONResponse.execute(null, null, null);
            //
            // Run the query here.
            getLoaderManager().initLoader(0, null, this).forceLoad();
        }
    }

    private boolean checkInternetConnectivity(Context context){
        // Determines internet connectivity...if working, then display the data otherwise show internet error.
        // no special query text.
        boolean hasInternetConnection= NetworkStreaming.checkInternetConnection(context.getApplicationContext());
        if (!hasInternetConnection) {
//            progressBar.setVisibility(View.INVISIBLE);
            txt_RecyclerViewMessage.setText(R.string.noInternetConnection_msg);
            txt_RecyclerViewMessage.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        }
        else {
            txt_RecyclerViewMessage.setVisibility(View.INVISIBLE);
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

        SharedPrefStatic.jobTextStr =sharedPreferences_ob.getString(AppConstants.PREF_KEY_TEXT, "");
        SharedPrefStatic.jobSkillStr=sharedPreferences_ob.getString(AppConstants.PREF_KEY_SKILL, "");
        SharedPrefStatic.jobLocationStr=sharedPreferences_ob.getString(AppConstants.PREF_KEY_LOCATION, "");
        SharedPrefStatic.jobAgeStr=sharedPreferences_ob.getString(AppConstants.PREF_KEY_AGE, "");

        // initial load.
        SharedPrefStatic.buildUriQuery();

//        return sharedPreferences_ob;
    }
}
