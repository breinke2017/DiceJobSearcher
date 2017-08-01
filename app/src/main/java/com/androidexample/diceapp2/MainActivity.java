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
import android.support.annotation.Nullable;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidexample.diceapp2.constantContainers.AppConstants;
import com.androidexample.diceapp2.enumContainers.ADDPREF_ACTION;
import com.androidexample.diceapp2.genericContainers.Jobs;
import com.androidexample.diceapp2.networking.NetworkStreaming;
import com.androidexample.diceapp2.staticClasses.SharedPrefStatic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/*
* Is the news app with AsyncTask Loader.
*
* */


public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Jobs>> {

    // Constants
    public static final String STRING_KEY_SEARCH_QUERY="SearchQueryList";
    private static final int MAX_ACTIONMENU_SIZE=10;

    public static int currentPage;
    // Is used in getJSONReponse because no other way to get this info.
    public static String queryLookup="";

    private RecyclerView mRecyclerView;
    public static List<Jobs> jobs_AL = new ArrayList<>();
    private ListView searchQueryListView;
    private EditText queryEditText;
    private Dialog dialog;
    private List<String> searchArrayList = new ArrayList<>();
    private MenuItem actionItem;
    public static SharedPreferences sharedPref;
    private Intent mIntentWebViewActivity=null;

    // Widget variables to keep track of.
    private int savedSearchesCount=0;
    private MenuItem menu_savedSearches=null;
    private MenuItem menu_refresh=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v("!myapp!", "*** Activity created occurred!");

        // setup click listeners.
        setupListeners();

        // sets up other items...don't want to keep creating new objects unneccarily.
        mIntentWebViewActivity = new Intent(this, WebViewActivity.class);

        // load SharedPreferences
        sharedPref=setupSharedPreferences(AppConstants.PREF_FILENAME);
        SharedPrefStatic.initialLoadNetworkData=true;

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
        menu_savedSearches = menu.findItem(R.id.menu_item_savedsearches);
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
                //todo: For some reason  is getting hit when we click on the menu / Edit Searches...weird.
                // shouldn't need.
//                triggerOurQuery();
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

        setupRecyclerViewListeners(jobs_al);
    }

    private void setupRecyclerViewListeners(List<Jobs> jobs_al) {

        boolean hitZeroRecords = false;
        final List<Jobs> jobs_al_copy = jobs_al;

        // Loads the data into the recyclerview and displays it.
        RecyclerView.Adapter adapter = setupRecyclerViewAdapter(MainActivity.jobs_AL);


        //todo: We shouldn't be recreating this listener over and over and over!
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                mRecyclerView, new ClickListener() {

            @Override
            public void onClick(View view, final int position) {
                // Add your onClick() code here.

                // For some odd reason, when I do a click, it's hitting this multiple times.
                Log.v("!myapp!", "onClick occurred at position " + position);


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

//        Button but_prevpage = (Button) this.findViewById(R.id.but_prevpage);
//        Button but_nextpage = (Button) this.findViewById(R.id.but_nextpage);
//        if (hitZeroRecords == false) {
//            if (jobs_al.get(0).getCurrentPage() == 1) {
//                but_prevpage.setVisibility(View.INVISIBLE);
//                but_nextpage.setVisibility(View.VISIBLE);
//            } else if (jobs_al.get(0).getCurrentPage()==jobs_al.get(0).getCurrentPage()) {
//                but_nextpage.setVisibility(View.INVISIBLE);
//            } else if (jobs_al.get(0).getCurrentPage() > 1) {
//                but_prevpage.setVisibility(View.VISIBLE);
//            }
//        }

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





//        // Prev page listener
//        but_prevpage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (MainActivity.currentPage > 1) {
//                    MainActivity.currentPage -= 1;
//                }
//
//                // todo: is this okay?
//                runNetworkQuery(MainActivity.this);
//            }
//        });
//
//        // Next page listener
//        but_nextpage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity.currentPage += 1;
//
//                // todo: is this okay?
//                runNetworkQuery(MainActivity.this);
//            }
//        });
    }



    private RecyclerView.Adapter setupRecyclerViewAdapter(List<Jobs> jobs_al) {
        // Setup up the RecyclerView Adapter
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerviewWidget);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        JobsRecyclerViewAdapter adapter = new JobsRecyclerViewAdapter(jobs_al);
        mRecyclerView.setAdapter(adapter);
        return adapter;
    }




    // User clicked the submit query button
    private void setupListeners() {
//        Button button = (Button) findViewById(R.id.but_queryOk);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // resets widgets and data to a clean view.
//                triggerOurQuery();
//            }
//        });

//        this.queryEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                // User hit the enter button to submit a query.
//                triggerOurQuery();
//                return false;
//            }
//        });











    }


    /*
    *  Gets called when the user hits the submit button or when a screen rotation occurs.
    * */
    public void triggerOurQuery() {
        // Added fix: Keep the string the user entered until we do the URI lookup....the URI
        //   cannot contain spaces!
//        this.queryLookup=queryEditText.getText().toString();
//        populateQueryPrefs(ADDPREF_ACTION.LOADFROMCLICKEDQUERY, null, this.queryLookup);

        MainActivity.currentPage=1;     // reset to page 1 for any new query.
        Button but_prevpage = (Button) findViewById(R.id.but_prevpage);
        Button but_nextpage = (Button) findViewById(R.id.but_nextpage);
//        but_prevpage.setVisibility(View.GONE);
//        but_nextpage.setVisibility(View.GONE);

//        ProgressBar progressBar=(ProgressBar) findViewById(R.id.progressbar);
//        progressBar.setVisibility(View.VISIBLE);

        runNetworkQuery(MainActivity.this);
    }

    /*
    * Populates the QueryValue and EditText with the Query the user choose.
    *
    * Note: This gets triggered 3 times:
    *   1) ** When reading in SharedPreferences. **
    *   2) After user selected something in the ActionMenu.
    *   3) After screen rotation -- ??
    *
    * */
    private void populateQueryPrefs(ADDPREF_ACTION actionId, @Nullable HashSet menuQueryValues, @Nullable String menuQueryValue) {
        // First, we'll convert the HashSet to ArrayList then store it in the ArrayList.
        // Second, we'll leave the query value in EditText to be empty.

        if (actionId==ADDPREF_ACTION.LOADFROMSHAREDPREF) {
            // This is implementation when we're reading in SharedPreferences at startup.
            // Wipe out any existing data in the searchArrayList arraylist.
            searchArrayList.removeAll(searchArrayList);

            // Convert the HashSet used by SharedPreferences to an ArrayList.
            int i=0;
            for (Object curItem: menuQueryValues) {
                // Only allow so many items in the arraylist...to keep the menu smaller.
                i++;
                if (i<=MAX_ACTIONMENU_SIZE) {
                    searchArrayList.add(curItem.toString());
                }
            }

            // The last entry in the arraylist is what we'll use for the queryValues.
//            this.queryLookup=(searchArrayList.get(searchArrayList.size()-1));
//            queryEditText.setText(this.queryLookup);
        }
        else if (actionId==ADDPREF_ACTION.LOADFROMCLICKEDQUERY) {
            // We're adding values to the arrayList because user clicked the submit button.
            if (!searchArrayList.contains(menuQueryValue)) {
                // Not found...add to searchArrayList.
                // ...however, if we're over the limit, then remove the older entries.
                if (searchArrayList.size()>=MainActivity.MAX_ACTIONMENU_SIZE) {
                    searchArrayList.remove(0);
                }
                searchArrayList.add(menuQueryValue);
            }
        }
        else if (actionId==ADDPREF_ACTION.LOADFROMACTIONMENU) {
            // Loads up string from ActionMenuItem.
//            queryEditText.setText(menuQueryValue);
//            this.queryLookup = menuQueryValue;
        }



//        // Adds this.queryLookup to searchArrayList should it not exist.
//        if (!searchArrayList.contains(queryValueToAdd)) {
//            // Not found...add to searchArrayList.
//            searchArrayList.add(queryValueToAdd);
//        }
//
//
//        if (!menuQueryValues.equals("")) {
//            // We're getting queryValue from the ActionItem menu.
//
//            // We cannot have spaces in the URI otherwise it wont find anything.
//            menuQueryValues=menuQueryValues.replace(" ", "");
//            this.queryLookup=menuQueryValues;
//            queryEditText.setText(menuQueryValues);
//        }
//        else {
//            this.queryLookup = mQueryTextToSave = queryEditText.getText().toString().trim();
//        }
//
//        // Adds query to SearchArrayList arrayList.
//        populateSearchArrayList(this.queryLookup);
    }


//    private void populateQueryValue(String menuQueryValue) {
//        if (!menuQueryValue.equals("")) {
//            // We're getting queryValue from the ActionItem menu.
//
//            // We cannot have spaces in the URI otherwise it wont find anything.
//            menuQueryValue=menuQueryValue.replace(" ", "");
//            this.queryLookup=menuQueryValue;
//            queryEditText.setText(menuQueryValue);
//        }
//        else {
//            this.queryLookup = mQueryTextToSave = queryEditText.getText().toString().trim();
//        }
//
//        // Adds query to SearchArrayList arrayList.
//        populateSearchArrayList(this.queryLookup);
//    }

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
        boolean hasInternetConnection= NetworkStreaming.checkInternetConnection(context.getApplicationContext());
        if (hasInternetConnection==false) {
            Log.v("!myapp!", "no internet connection!!");

//            progressBar.setVisibility(View.INVISIBLE);
//            txt_multiDisplay.setText("No internet connection.");
//            txt_multiDisplay.setVisibility(View.VISIBLE);
        }
        else {
            Log.v("!myapp!", "yes, we have internet connection!!");
//            GetJSONResponse getJSONResponse = new GetJSONResponse(context, queryLookup);
//            getJSONResponse.execute(null, null, null);
            //
            // Run the query here.
            getLoaderManager().initLoader(0, null, this).forceLoad();
        }
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

        SharedPrefStatic.buildUriQuery();

        return sharedPreferences_ob;
    }
}
