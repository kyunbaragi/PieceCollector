package com.yunkyun.piececollector.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yunkyun.piececollector.R;
import com.yunkyun.piececollector.object.Record;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by YunKyun on 2017-08-09.
 */

public class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Record> recordList;

    public HistoryRecyclerAdapter(Context context) {
        this.context = context;
        recordList = new ArrayList<>();
    }

    public void setRecordList(List<Record> placeList) {
        this.recordList = placeList;
    }

    @Override
    public HistoryRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record_history, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryRecyclerAdapter.ViewHolder holder, int position) {
        Record record = recordList.get(position);
        holder.title.setText(record.getTitle());
        holder.title.setOnClickListener(new View.OnClickListener() {
            // TODO: Start PlaceActivity.
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Image Click!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_record_title)
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
