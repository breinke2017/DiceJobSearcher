package com.androidexample.diceapp2.genericContainers;

/**
 * Created by brian.reinke on 7/27/2017.
 */

/*
* API: http://service.dice.com/api/rest/jobsearch/v1/simple.json
* Currently, it reads in 50 records.  So, the first page will be 1-50.  Second page will be 51-100, etc.
*
* */

public class Jobs {
    // Total records in query.
    private int mCount;

    // the current page as shown in the URL.  Is more for convienience.
    private int mCurrentPage;

    // The first document record on page.
    private int mFirstDocument;

    // The last document record on page.
    private int mLastDocument;

    // URL of job posting.
    private String mDetailURL;

    // Job title.
    private String mJobTitle;

    // Location: City, State.  The site lists this as "City, State".  To separate, parse it.
    private String mLocation;

    // Posting date.
    private String mPostingDate;

    // Company.
    private String mCompany;


    public Jobs(int currentPage, int firstDocument, int lastDocument, String detailURL,
                String location, String postingDate, String company, String jobTitle) {
        this.mCurrentPage=currentPage;
        this.mFirstDocument=firstDocument;
        this.mLastDocument=lastDocument;
        this.mDetailURL=detailURL;
        this.mLocation=location;
        this.mCompany=company;
        this.mPostingDate=postingDate;
        this.mJobTitle=jobTitle;
    }

    public int getCount() {
        return mCount;
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public int getFirstDocument() {
        return mFirstDocument;
    }

    public String getDetailURL() {
        return mDetailURL;
    }

    public String getJobText() {
        return mJobTitle;
    }

    public String getLocation() {
        return mLocation;
    }

    public String getPostingDate() {
        return mPostingDate;
    }

    public String getCompany() {
        return mCompany;
    }
}
