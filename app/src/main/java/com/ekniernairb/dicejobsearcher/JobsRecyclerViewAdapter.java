package com.ekniernairb.dicejobsearcher;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ekniernairb.dicejobsearcher.genericContainers.Jobs;

import java.util.List;

/**
 * Created by brian.reinke on 7/1/2017.
 */

public class JobsRecyclerViewAdapter extends RecyclerView.Adapter<JobsRecyclerViewAdapter.CustomViewHolder> {
    private List<Jobs> itemListData;
    private View listItemView;

    public JobsRecyclerViewAdapter(List<Jobs> arrayListData) {
            this.itemListData = arrayListData;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.jobs_listitem, null);
        CustomViewHolder viewHolder = new CustomViewHolder(listItemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Jobs currentItem = itemListData.get(position);

        holder.textViewTitle.setText(currentItem.getJobTitle());
        holder.textViewCompany.setText(currentItem.getCompany());
        holder.textViewLocation.setText(currentItem.getLocation());
        holder.textViewPostingDate.setText(currentItem.getPostingDate());



        /*
        * Create all the findByViewIds records that you will read in. This is needed otherwise
        * recyclerview will duplicate the views!
        *
        * I'm keeping this code in so as to show how not to do it...this duplicates records starting
        * on record #10.
        * */

//        // assign the UI their data.
//        TextView textTitleItem = (TextView) listItemView.findViewById(R.id.txt_title);
//        TextView textCompanyItem = (TextView) listItemView.findViewById(R.id.txt_company);
//        TextView textLocationItem = (TextView) listItemView.findViewById(R.id.txt_location);
//        TextView textPostingDateItem = (TextView) listItemView.findViewById(R.id.txt_postingDate);

//        textTitleItem.setText(currentItem.getJobTitle());
//        textCompanyItem.setText(currentItem.getCompany());
//        textLocationItem.setText(currentItem.getLocation());
//        textPostingDateItem.setText(currentItem.getPostingDate());

//        textItem = (TextView) listItemView.findViewById(R.id.txt_URL);
//        textItem.setText(currentItem.getDetailURL());

    }


    @Override
    public int getItemCount() {
        return (null != itemListData ? itemListData.size() : 0);
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {

        /*
        * Create all the findByViewIds records that you will read in. This is needed otherwise
        * recyclerview will duplicate the views!
        * */

        protected TextView textViewTitle;
        protected TextView textViewCompany;
        protected TextView textViewLocation;
        protected TextView textViewPostingDate;


        public CustomViewHolder(View view) {
            super(view);
            this.textViewTitle = (TextView) view.findViewById(R.id.txt_title);
            this.textViewCompany = (TextView) view.findViewById(R.id.txt_company);
            this.textViewLocation = (TextView) view.findViewById(R.id.txt_location);
            this.textViewPostingDate = (TextView) view.findViewById(R.id.txt_postingDate);
        }
    }
}
