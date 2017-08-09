package com.yunkyun.piececollector.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.yunkyun.piececollector.R;
import com.yunkyun.piececollector.object.Record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by YunKyun on 2017-07-29.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Record> recordList;

    public RecyclerAdapter(Context context) {
        this.context = context;
        recordList = new ArrayList<>();
    }

    public void setRecordList(List<Record> placeList) {
        this.recordList = placeList;
        shuffleRecordList();
    }

    public void shuffleRecordList() {
        Collections.shuffle(recordList);
    }

    public void addRecordList(Record place) {
        recordList.add(place);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Record record = recordList.get(position);

        Glide.with(context).load(record.getImagePath()).transition(DrawableTransitionOptions.withCrossFade(1500)).into(holder.image);
        holder.image.setOnClickListener(new View.OnClickListener() {
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
        @BindView(R.id.iv_place_image)
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
