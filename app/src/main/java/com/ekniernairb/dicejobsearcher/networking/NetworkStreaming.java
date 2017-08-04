package com.ekniernairb.dicejobsearcher.networking;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/*
* All the pieces needed to do the following are in this class:
 *      * URIBuilder
 *      * Check internet connection
  *     * HTTP GET connection
  *     * Read from Stream
*
* */

public class NetworkStreaming {

    private NetworkStreaming() { }

    /*
    * ConverttoURL is needed because we have to convert the URL-string to URL-object.
    * */
    public static URL ConverttoURL(String url_string) {
        URL urlString=null;
        try {
            urlString = new URL(url_string);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return urlString;
    }

    public static String BuildUriURL(String scheme, String authority, String resource, ArrayList<QueryURI> queryAL) {
        // Builds the complete URL for URI (in String format).
        // Input 'url_string' must be only the http://<domain> portion!
        // URL we want:
        //String URL_string = URL we want: https://content.guardianapis.com/us-news?api-key=431fa4f4-baa3-4b01-8584-6dfe2fa215fe


        /*
        * Do syntax checking on scheme.
        * */
        String correctedScheme=scheme.trim();
        correctedScheme=correctedScheme.replace(" ","");
        correctedScheme=correctedScheme.replace("/","");
        correctedScheme=correctedScheme.replace("/","");
        if (!correctedScheme.substring(correctedScheme.length()-1).contentEquals(":")) {
            correctedScheme+=":";
        }


        /*
        * Do syntax checking on authority.
        * */
        String correctedAuthority=authority.trim();
        correctedAuthority=correctedAuthority.replace(" ","");

        // remove 'slashes' should it have it..we'll add it programtically.
        correctedAuthority=correctedAuthority.replace("/","");
        if (!correctedScheme.substring(correctedScheme.length()-1).contentEquals(":")) {
            correctedScheme+=":";
        }


        /*
        * Do syntax checking on resource.
        * */
        String correctedResource = resource.trim();
        correctedResource = correctedResource.replace(" ","");
        if (correctedResource.length()!=0) {
            if (correctedResource.substring(correctedResource.length() - 1).contentEquals("/")) {
                correctedResource = correctedResource.substring(0, correctedResource.length() - 2);
            }
        }

        /*
        * Build the query.
        * */
        String queryPath="";
        int i=0;
        for (QueryURI curItem : queryAL) {
            i++;
            if (i==1) {
                queryPath+="?";
            }
            else if (i>1) {
                queryPath+="&";
            }
            queryPath+=curItem.getKey() + "=" + curItem.getValue();
        }

        Uri myURL=Uri.parse(correctedScheme + "//" + correctedAuthority + "/" + correctedResource + queryPath);

/*        Am not using because the builder is too limiting for our purposes.
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(scheme)
                .authority(authority)
                .appendPath("books")
                .appendPath("v1")
                .appendPath("volumes")
                .appendQueryParameter("q", this.mQueryText)
                .appendQueryParameter("maxResults", "40");
        String myUrl = builder.build().toString();*/

        return myURL.toString();
    }


    public static String makeHttpRequest(URL url) {
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        String jsonResponse = "";
        StringBuilder JsonResponseBuilder = new StringBuilder();

        if (url == null) {
            // returns empty.
            return jsonResponse;
        }


        try {
            // Making an HTTP(s) Get connection.
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);     // in milliseconds
            urlConnection.setConnectTimeout(15000);  // in milliseconds
            urlConnection.connect();   // where it establishes the connection.

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
                JsonResponseBuilder.append(jsonResponse);
            }

            //TODO: Add response codes to inform the user.
        } catch (IOException e) {
            //TODO: Handle the exception
            Log.e("!myapp!", "Some IO exception when creating the connection");

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
//                    e.printStackTrace();
                    Log.e("!myapp!", "ioexception;" + e.getMessage());
                }
            }
        }

        jsonResponse=JsonResponseBuilder.toString();
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) {
        // In our case, returns a JSON response.
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader =
                    new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = null;

            try {
                // readLine() must be in a try/catch because there could be an unhandled eception.
                line = reader.readLine();
            } catch (IOException e) {
//                e.printStackTrace();
            }

            while(line != null) {
                output.append(line);
                try {
                    line = reader.readLine();
                } catch (IOException e) {
//                    e.printStackTrace();
                }
            }
        }
        return output.toString();
    }


    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
