package com.yunkyun.piececollector.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.yunkyun.piececollector.R;
import com.yunkyun.piececollector.activity.RecordActivity;
import com.yunkyun.piececollector.object.Record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by YunKyun on 2017-07-29.
 */

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Record> recordList;

    public MainRecyclerAdapter(Context context) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record_main, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Record record = recordList.get(position);

        // CardView 생성 animation 정의
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animation.setDuration(1000);
        holder.image.startAnimation(animation);

        Glide.with(context)
                .load(record.getImagePath())
                .apply(new RequestOptions().override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL))
                .into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            // TODO: Start RecordActivity.
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, RecordActivity.class);
                intent.putExtra("record", record);
                context.startActivity(intent);
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
