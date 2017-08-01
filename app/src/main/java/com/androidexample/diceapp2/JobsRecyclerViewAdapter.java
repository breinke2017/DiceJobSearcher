package com.androidexample.diceapp2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidexample.diceapp2.genericContainers.Jobs;

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

        // assign the UI their data.
        TextView textTitleItem = (TextView) listItemView.findViewById(R.id.txt_title);
        textTitleItem.setText(currentItem.getJobText());

        TextView textCompanyItem = (TextView) listItemView.findViewById(R.id.txt_company);
        textCompanyItem.setText(currentItem.getCompany());

        TextView textLocationItem = (TextView) listItemView.findViewById(R.id.txt_location);
        textLocationItem.setText(currentItem.getLocation());

        TextView textPostingDateItem = (TextView) listItemView.findViewById(R.id.txt_postingDate);
        textPostingDateItem.setText(currentItem.getPostingDate());

//        textItem = (TextView) listItemView.findViewById(R.id.txt_URL);
//        textItem.setText(currentItem.getDetailURL());
    }


    @Override
    public int getItemCount() {
        return (null != itemListData ? itemListData.size() : 0);
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView textView;

        public CustomViewHolder(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(R.id.txt_title);
        }
    }
}
