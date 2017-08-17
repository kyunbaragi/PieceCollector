package com.yunkyun.piececollector.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yunkyun.piececollector.GrayscaleTransformation;
import com.yunkyun.piececollector.R;
import com.yunkyun.piececollector.object.Place;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by YunKyun on 2017-08-14.
 */

public class PlaceRecyclerAdapter extends RecyclerView.Adapter<PlaceRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Place> placeList;

    public PlaceRecyclerAdapter(Context context) {
        this.context = context;
        placeList = new ArrayList<>();
    }

    public void setPlaceList(List<Place> placeList) {
        this.placeList = placeList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Place place = placeList.get(position);

        holder.title.setText(place.getTitle());

        RequestOptions grayScaleOption = RequestOptions.bitmapTransform(new GrayscaleTransformation(context));
        if(place.getVisited() == 0) {
            holder.image.setColorFilter(Color.parseColor("#BDBDBD"), PorterDuff.Mode.MULTIPLY);
            Glide.with(context).load(place.getImagePath()).apply(grayScaleOption).into(holder.image);
        } else {
            Glide.with(context).load(place.getImagePath()).into(holder.image);
            holder.title.setTextColor(ContextCompat.getColor(context, R.color.PrimaryTextBlack));
        }
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_mini_place_image)
        ImageView image;
        @BindView(R.id.tv_mini_place_title)
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
