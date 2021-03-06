package com.ekniernairb.dicejobsearcher;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ekniernairb.dicejobsearcher.constantContainers.AppConstants;
import com.ekniernairb.dicejobsearcher.staticClasses.ColorSchemeButtons;

import java.util.ArrayList;

/**
 * Created by brian.reinke on 7/29/2017.
 */

public class WebViewActivity extends AppCompatActivity {
    // Views
    private static WebView mWebView = null;
    private static TextView mTextView = null;
    private static MenuItem mShareMenuItem;
    private static MenuItem mSettingsAddToSavedMenuItem;
    private MenuItem mMenu_savedSearches;
    private static ProgressBar mProgressBar;

    private String mWebUrl;
    private ArrayList<String> jobsUrlData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        /* Changes Activities theme colors.  Must do this prior to setContentView! */
        if (ColorSchemeButtons.hasSelectedColor) {
            //change color scheme.
            ColorSchemeButtons.setActivityTheme(this, ColorSchemeButtons.colorButtonSelected);
        } else {
            // overrides the manifest file setting for theme.
            this.setTheme(R.style.ColorSchemeDefault);
        }

        setContentView(R.layout.activity_webview);

        // Create findByViewIds...
        Log.v("!myapp!", "webviewActivity has been created **");

        mWebView = (WebView) findViewById(R.id.webview);
        mTextView = (TextView) findViewById(R.id.webview_Text_WebPageProblem);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar_webview);
        jobsUrlData = getIntent().getStringArrayListExtra(AppConstants.PutExtra_JobURLInfo);
        mWebUrl = jobsUrlData.get(0);

        // set page title with jobtitle.
        setTitle(jobsUrlData.get(1));

        // See also method jobWebPageLoaded for when job Posting completely opens.
        loadWebViewWidget(mWebUrl);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // unload all this data.
        mWebView = null;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // must detach webview.
        if (mWebView != null) {
            // Make sure you remove the WebView from its parent view before doing anything.
            mWebView.removeAllViews();


            // NOTE: This pauses JavaScript execution for ALL WebViews,
            // do not use if you have other WebViews still alive.
            // If you create another WebView after calling this,
            // make sure to call mWebView.resumeTimers().
            mWebView.pauseTimers();

            // NOTE: This can occasionally cause a segfault below API 17 (4.2)
            mWebView.destroy();
        }
        // Null out the reference so that you don't end up re-using it.
        mWebView = null;
    }


    private void loadWebViewWidget(String webURL) {
        // Loads HTML into the webview Widget.


        // First, load the website into a variable for parsing of HTML or checking website validitity.
        // The problem with Jsoup is that you need to run the connect method inside a background thread
        //   like AsyncTask.

        // See also method jobWebPageLoaded for when job Posting completely opens.
//        new LoadWebURL().execute(webURL, null, null);


        // Opens in WebView.
        // Without this, the website will load in the browser vs the webview widget.
        mWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(WebViewActivity.this, "cannot get to webpage!", Toast.LENGTH_LONG).show();
            }
        });

        // sets to hopefully improve performance.
        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(webURL);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate our menus.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_share_menu, menu);

        //todo: Enable this functionality later.
//        inflater.inflate(R.menu.app_settings_menu, menu);

        // Also, setup other menu finditems.
        mShareMenuItem = menu.findItem(R.id.menu_share);
        //todo: Add these to future versions...not using saved items currently.
//        mSettingsAddToSavedMenuItem= menu.findItem(R.id.menu_settings_addtosaved);
        //        menu_savedSearches = menu.findItem(R.id.menu_item_savedsearches);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_share_email:
                sendJobDataToEmail();
                return true;
            case R.id.menu_share_text:
                sendJobDataToText();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sendJobDataToText() {
        String message;
        Intent jobToEmailIntent = new Intent(Intent.ACTION_SENDTO);
        jobToEmailIntent.putExtra(Intent.EXTRA_SUBJECT, jobsUrlData.get(1) + " for company " + jobsUrlData.get(2));

        message = "Here is a job someone sent you:\n\n";
        message += "JobTitle: " + jobsUrlData.get(1) + "\n";
        message += "Company: " + jobsUrlData.get(2) + "\n";
        message += "Location: " + jobsUrlData.get(3) + "\n";
        message += "PostedDate: " + jobsUrlData.get(4) + "\n\n";
        message += jobsUrlData.get(0) + "\n\n";

        jobToEmailIntent.setData(Uri.parse("smsto:"));
        jobToEmailIntent.putExtra("sms_body", message);

        if (jobToEmailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(jobToEmailIntent);
        }
    }

    private void sendJobDataToEmail() {
        String message;
        Intent jobToEmailIntent = new Intent(Intent.ACTION_SENDTO);
        jobToEmailIntent.putExtra(Intent.EXTRA_SUBJECT, jobsUrlData.get(1) + " for company " + jobsUrlData.get(2));

        message = "Here is a job someone sent you:\n\n";
        message += "JobTitle: " + jobsUrlData.get(1) + "\n";
        message += "Company: " + jobsUrlData.get(2) + "\n";
        message += "Location: " + jobsUrlData.get(3) + "\n";
        message += "PostedDate: " + jobsUrlData.get(4) + "\n\n";
        message += jobsUrlData.get(0) + "\n\n";

        jobToEmailIntent.putExtra(Intent.EXTRA_TEXT, message);

        jobToEmailIntent.setData(Uri.parse("mailto:"));

        if (jobToEmailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(jobToEmailIntent);
        }
    }
}