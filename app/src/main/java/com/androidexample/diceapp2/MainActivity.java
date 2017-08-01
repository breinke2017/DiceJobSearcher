package com.androidexample.diceapp2;

import android.app.Dialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidexample.diceapp2.constantContainers.AppConstants;
import com.androidexample.diceapp2.genericContainers.Jobs;
import com.androidexample.diceapp2.networking.NetworkStreaming;
import com.androidexample.diceapp2.staticClasses.Api_Data;
import com.androidexample.diceapp2.staticClasses.SharedPrefStatic;

import java.util.ArrayList;
import java.util.List;



//Todo: One very strange thing I see is that every 10 records does a weird thing where it changes data when you scroll up or down.
//Todo: Records after 10 repeat in order.....The data underneath is correct but the widget text information is not!


// Todo: Doesn't refresh or load when user rotates screen to landscape.

/*
* Is the news app with AsyncTask Loader.
*
* */

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Jobs>> {

    // Is used in getJSONReponse because no other way to get this info.
    public static String queryLookup="";

    private RecyclerView mRecyclerView;
    public static List<Jobs> jobs_AL = new ArrayList<>();
    private ListView searchQueryListView;
    private Dialog dialog;
    private List<String> searchArrayList = new ArrayList<>();
    public static SharedPreferences sharedPref;
    private Intent mIntentWebViewActivity=null;

    private Button but_prevpage;
    private Button but_nextpage;
    private TextView txt_message;

    // Widget variables to keep track of.
    private MenuItem menu_savedSearches=null;
    private MenuItem menu_refresh=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v("!myapp!", "*** Activity created occurred!");

        setupFindByViewIDs();


        // setup click listeners.
        setupListeners();

        // sets up other items...don't want to keep creating new objects unneccarily.
        mIntentWebViewActivity = new Intent(this, WebViewActivity.class);
        Api_Data.mCurrentPage=1;

        // load SharedPreferences
        sharedPref=setupSharedPreferences(AppConstants.PREF_FILENAME);
        SharedPrefStatic.initialLoadNetworkData=true;

    }

    private void setupFindByViewIDs() {
        but_prevpage = (Button) findViewById(R.id.but_prevpage);
        but_nextpage = (Button) findViewById(R.id.but_nextpage);
        txt_message=(TextView) findViewById(R.id.txt_message);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerviewWidget);
    }


    @Override
    protected void onStart() {
        super.onStart();

        // verified.
        if (SharedPrefStatic.initialLoadNetworkData==true || (SharedPrefStatic.editIntentLoaded==true && SharedPrefStatic.editIntentSaved==true)) {

            triggerOurQuery();

            // Call after hitting this so we don't keep initiating a load.
            SharedPrefStatic.initialLoadNetworkData=false;
            SharedPrefStatic.editIntentLoaded=false;
            SharedPrefStatic.editIntentSaved=false;

            // everything gets run by triggerOurQuery() then by onLoadFinished()!
//            //  runNetworkQuery(MainActivity.this);
//            RecyclerView.Adapter adapter=setupRecyclerViewAdapter(MainActivity.jobs_AL);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.v("!myapp!", "restart occurred!");
    }


    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.v("!myapp!", "onDestroy() occurred!");
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
//                //re-check internet connection
                boolean hasInternetConnection= checkInternetConnectivity(MainActivity.this.getApplicationContext());
                return true;

//                return true;
//            case R.id.searchMenu:
//                showSearchLayout(this);
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
    * Shows the Editor to save/make changes to the query search parameters.
    * */
    private void showEditSearchesLayout(int layoutResId, Context context) {
        Log.v("!myapp!", "user clicked menu edit searches");

        View inflater = LayoutInflater.from(context).inflate(layoutResId, null);

    }


    /*
    * Shows SearchLayout when user clicks on the search menu action item.
      * Should eventually be a fragment.
      *
      * !! currently not used!!!
    * */
    private void showSearchLayout(final Context context) {
        Log.v("!myapp!", "user pressed action item");

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_searchquery);
        searchQueryListView = (ListView) dialog.findViewById(R.id.search_ListView);

        ArrayAdapter<String> adapter=
                new ArrayAdapter<String>(searchQueryListView.getContext(),
                        android.R.layout.simple_list_item_1, searchArrayList) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        TextView textView = (TextView) super.getView(position, convertView, parent);
                        textView.setTextColor(Color.BLACK);

                        return textView;
                    }
                };
        searchQueryListView.setAdapter(adapter);
        setSearchQueryListViewListener(searchQueryListView);
        dialog.setTitle("Quick Query: Pick an item to search.");
        dialog.show();
    }

    /*
    * Sets up listener for SearchQueryListView which only happens when user presses ActionItem.
    * Here is where we will populate the dialog list.
    * */
    private void setSearchQueryListViewListener(final ListView searchQueryListView) {
        searchQueryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Whatever text we selected will populate on the editText.
//                populateQueryPrefs(ADDPREF_ACTION.LOADFROMACTIONMENU, null, (String) searchQueryListView.getItemAtPosition(position));

                // We clicked on the SearchQueryListView...close the ActionMenu.
                dialog.dismiss();

                // Do the Submit Button Query actions so it does an automatic search for the user.
                triggerOurQuery();
            }
        });
    }


    @Override
    public Loader<List<Jobs>> onCreateLoader(int id, final Bundle args) {
        //Here we will initiate AsyncTaskLoader
        Log.v("!myapp!", "*** onCreateLoader occurred!");
        ProgressBar myProgressBar=(ProgressBar) this.findViewById(R.id.progressbar);
        myProgressBar.setVisibility(View.VISIBLE);

        return new GetJSONResponse(this);
    }

    @Override
    public void onLoaderReset(Loader<List<Jobs>> loader) {
        Log.v("!myapp!", "*** onLoaderReset occurred!");
        loader = null;
    }


    @Override
    public void onLoadFinished(Loader<List<Jobs>> loader, List<Jobs> jobs_al) {
        // onLoadFinished must have a List<T> type!
        Log.v("!myapp!", "*** onLoadFinished occurred!");


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

//        /*
//        * TextView txt_multipurpose actually serves two purposes: Either one or the other:
//        *    * if records, displays page #s
//        *    * if no records, displays 'no internet found', or 'no news items...'
//        * */
//        TextView txt_multipurpose = (TextView) this.findViewById(R.id.txt_multipurpose);


//        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
//            @Override
//            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
//                int action = MotionEventCompat.getActionMasked(e);
//
//                switch(action) {
//                    case (MotionEvent.ACTION_DOWN) :
//                        Log.v("!myapp!","Action was DOWN");
//
//                        // Get information from our arraylist regarding this position.
//                        News curNewsItem = news_al_copy.get(position);
//                        String webURL = curNewsItem.getWebURL();
//
//                        // sets up intent to open as webbrowser.
//                        Intent i = new Intent();
//                        i.setAction(Intent.ACTION_VIEW);
//                        i.addCategory(Intent.CATEGORY_BROWSABLE);
//                        i.setData(Uri.parse(webURL));
//                        startActivity(i);
//
//
////                    default :
////                        return super.onTouchEvent(event);
//                }
//
//
//
//                return false;
//            }
//
//            @Override
//            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
//            }
//
//            @Override
//            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//
//            }
//        });


        // Loads the data into the recyclerview and displays it.
        RecyclerView.Adapter adapter = setupRecyclerViewAdapter(MainActivity.jobs_AL);

        // creates a new OnItemtouchListener and
        setupRecyclerViewListeners(adapter, MainActivity.jobs_AL);
    }

    private void setupRecyclerViewListeners(RecyclerView.Adapter adapter, List<Jobs> jobs_al) {

        boolean hitZeroRecords = false;

        final List<Jobs> jobs_al_copy = jobs_al;

        //todo: I tried taking this out and for whatever reason it won't work.  The onclick fails.
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                mRecyclerView, new ClickListener() {

            @Override
            public void onClick(View view, final int position) {
                // Add your onClick() code here.

                // Determines internet connectivity...if working, then display the data otherwise show internet error.
                // no special query text.
                boolean hasInternetConnection=checkInternetConnectivity(MainActivity.this);
                if (hasInternetConnection) {
                    Log.v("!myapp!", "yes, we have internet connection!!");

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
                    jobsUrlData.add(0,jobs_al_copy.get(position).getDetailURL());
                    jobsUrlData.add(1,jobs_al_copy.get(position).getJobText());
                    jobsUrlData.add(2,jobs_al_copy.get(position).getCompany());
                    jobsUrlData.add(3,jobs_al_copy.get(position).getLocation());
                    jobsUrlData.add(4,jobs_al_copy.get(position).getPostingDate());

                    // sets up intent to open as WebView.
                    Log.v("!myapp!", "opening activity");
                    mIntentWebViewActivity.setData(Uri.parse(curJobsItem.getDetailURL()));
                    mIntentWebViewActivity.putStringArrayListExtra(AppConstants.PutExtra_JobURLInfo,
                            jobsUrlData);
                    startActivity(mIntentWebViewActivity);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                // Add your onLongClick() code here.
            }
        }));





        // determine if adapter (recyclerview) has objects or not.
        if (adapter.getItemCount() == 0) {
            hitZeroRecords = true;
//            mRecyclerView.setEmptyView(txt_multipurpose);
        }

        // display pages.
        ProgressBar progressBar = (ProgressBar) this.findViewById(R.id.progressbar);
        progressBar.setVisibility(View.INVISIBLE);


        // Setup our Click Listeners
        // ------------------------
        // When the user clicks on the article, it should go to the website.
        final List<Jobs> jobs_copy_al=jobs_al;

//        mRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                News curNewsItem = news_copy_al.get(position);
//                String webURL = curNewsItem.getWebURL();
//
//                // sets up intent to open as webbrowser.
//                Intent i = new Intent();
//                i.setAction(Intent.ACTION_VIEW);
//                i.addCategory(Intent.CATEGORY_BROWSABLE);
//                i.setData(Uri.parse(webURL));
//                startActivity(i);
//            }
//        });



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
                runNetworkQuery(MainActivity.this);
            }
        });

        // Next page listener
        but_nextpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Api_Data.mCurrentPage += 1;

                SharedPrefStatic.buildUriQuery();
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



    /*
    * Populates populateSearchArrayList which will save to the Preferences.
    * */
    private void populateSearchArrayList(String queryValueToAdd) {
        // Adds this.queryLookup to searchArrayList should it not exist.
        if (!searchArrayList.contains(queryValueToAdd)) {
            // Not found...add to searchArrayList.
            searchArrayList.add(queryValueToAdd);
        }
    }


    public void runNetworkQuery(Context context) {
//        TextView txt_multiDisplay= (TextView) findViewById(R.id.txt_multipurpose);
//        txt_multiDisplay.setVisibility(View.VISIBLE);

        // Determines internet connectivity...if working, then display the data otherwise show internet error.
        // no special query text.
        boolean hasInternetConnection= checkInternetConnectivity(context);

        if (hasInternetConnection==true) {
            Log.v("!myapp!", "yes, we have internet connection!!");
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
        if (hasInternetConnection==false) {
            Log.v("!myapp!", "no internet connection!!");

//            progressBar.setVisibility(View.INVISIBLE);
            txt_message.setText("No internet connection.");
            txt_message.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        }
        else {
            Log.v("!myapp!", "yes, we have internet connection!!");
            txt_message.setVisibility(View.INVISIBLE);
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
    private SharedPreferences setupSharedPreferences(String prefFileName) {
        // Get Shared Preferences.
        SharedPreferences sharedPreferences_ob;

        sharedPreferences_ob = this.getSharedPreferences(prefFileName, 0);

        SharedPrefStatic.jobTextStr =sharedPreferences_ob.getString(AppConstants.PREF_KEY_TEXT, "");
        SharedPrefStatic.jobSkillStr=sharedPreferences_ob.getString(AppConstants.PREF_KEY_SKILL, "");
        SharedPrefStatic.jobLocationStr=sharedPreferences_ob.getString(AppConstants.PREF_KEY_LOCATION, "");
        SharedPrefStatic.jobAgeStr=sharedPreferences_ob.getString(AppConstants.PREF_KEY_AGE, "");

        // initial load.
        SharedPrefStatic.buildUriQuery();

        return sharedPreferences_ob;
    }
}
