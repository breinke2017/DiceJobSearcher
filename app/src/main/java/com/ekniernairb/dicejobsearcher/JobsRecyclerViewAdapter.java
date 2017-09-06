package com.ekniernairb.dicejobsearcher;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ekniernairb.dicejobsearcher.constantContainers.AppConstants;
import com.ekniernairb.dicejobsearcher.genericContainers.Jobs;
import com.ekniernairb.dicejobsearcher.staticClasses.ColorSchemeButtons;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brian.reinke on 7/1/2017.
 */

public class JobsRecyclerViewAdapter extends RecyclerView.Adapter<JobsRecyclerViewAdapter.CustomViewHolder> {
    private List<Jobs> mItemListData;
    private View mListItemView;
    private Context mContext;
    private CardView mCardView;

    public JobsRecyclerViewAdapter(List<Jobs> arrayListData) {
        this.mItemListData = arrayListData;
    }


    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        mListItemView = LayoutInflater.from(mContext).inflate(R.layout.jobs_listitem, null);
        CustomViewHolder viewHolder = new CustomViewHolder(mListItemView);
        mCardView = (CardView) mListItemView.findViewById(R.id.card_view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Jobs currentItem = mItemListData.get(position);

        holder.mTextViewTitle.setText(currentItem.getJobTitle());
        holder.mTextViewCompany.setText(" " + currentItem.getCompany());
        holder.mTextViewLocation.setText(" " + currentItem.getLocation());
        holder.mTextViewPostingDate.setText(" " + currentItem.getPostingDate());

        String color;
        if (ColorSchemeButtons.hasSelectedColor) {
            color = ColorSchemeButtons.colorStylesList.get(ColorSchemeButtons.colorButtonSelected).getColorCardView();
            mCardView.setCardBackgroundColor(Color.parseColor(color));

            // color for textviews (next)
            color = ColorSchemeButtons.colorStylesList.get(ColorSchemeButtons.colorButtonSelected).getColorCardViewText();
        } else {
            color = "#000000";
        }


        /* These are the literal text fields */
        holder.mTextViewTitle_Literal.setTextColor(Color.parseColor(color));
        holder.mTextViewCompany_Literal.setTextColor(Color.parseColor(color));
        holder.mTextViewLocation_Literal.setTextColor(Color.parseColor(color));
        holder.mTextViewPostingDate_Literal.setTextColor(Color.parseColor(color));

            /* These are the text fields with data */
        holder.mTextViewTitle.setTextColor(Color.parseColor(color));
        holder.mTextViewCompany.setTextColor(Color.parseColor(color));
        holder.mTextViewLocation.setTextColor(Color.parseColor(color));
        holder.mTextViewPostingDate.setTextColor(Color.parseColor(color));
    }


    @Override
    public int getItemCount() {
        return (null != mItemListData ? mItemListData.size() : 0);
    }


    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /*
        * Create all the findByViewIds records that you will read in. This is needed otherwise
        * recyclerview will duplicate the views!
        * */

        // Literal fields
        protected TextView mTextViewTitle_Literal;
        protected TextView mTextViewCompany_Literal;
        protected TextView mTextViewLocation_Literal;
        protected TextView mTextViewPostingDate_Literal;

        // Data fields
        protected TextView mTextViewTitle;
        protected TextView mTextViewCompany;
        protected TextView mTextViewLocation;
        protected TextView mTextViewPostingDate;


        public CustomViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);

            /*
            * Because I'm using shadowing, I'm using 2 layers of textViews.  This is because
            * the text values have to match so that the vertical height is the same.
            * */

            /* These are the literal text fields */
            this.mTextViewTitle_Literal = (TextView) view.findViewById(R.id.txt_jobtitle);
            this.mTextViewCompany_Literal = (TextView) view.findViewById(R.id.txt_jobCompany);
            this.mTextViewLocation_Literal = (TextView) view.findViewById(R.id.txt_jobLocation);
            this.mTextViewPostingDate_Literal = (TextView) view.findViewById(R.id.txt_jobPostingDate);

            /* These are the text fields with data */
            this.mTextViewTitle = (TextView) view.findViewById(R.id.txt_title);
            this.mTextViewCompany = (TextView) view.findViewById(R.id.txt_company);
            this.mTextViewLocation = (TextView) view.findViewById(R.id.txt_location);
            this.mTextViewPostingDate = (TextView) view.findViewById(R.id.txt_postingDate);
        }


        @Override
        public void onClick(View v) {
            Toast.makeText(mContext, "recyclerview on click", Toast.LENGTH_SHORT).show();

            int position = getLayoutPosition();

            // Build an arrayList that will have needed information from List<Jobs>.
            ArrayList<String> jobsUrlData = new ArrayList<>(4);
            jobsUrlData.add(0, MainActivity.jobs_AL.get(position).getDetailURL());
            jobsUrlData.add(1, MainActivity.jobs_AL.get(position).getJobTitle());
            jobsUrlData.add(2, MainActivity.jobs_AL.get(position).getCompany());
            jobsUrlData.add(3, MainActivity.jobs_AL.get(position).getLocation());
            jobsUrlData.add(4, MainActivity.jobs_AL.get(position).getPostingDate());


            Intent mIntentWebViewActivity;
            mIntentWebViewActivity = new Intent(mContext, WebViewActivity.class);
            mIntentWebViewActivity.putStringArrayListExtra(AppConstants.PutExtra_JobURLInfo,
                    jobsUrlData);
            mContext.startActivity(mIntentWebViewActivity);
        }
    }
}
