package com.yunkyun.piececollector.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.yunkyun.piececollector.PlaceItem;
import com.yunkyun.piececollector.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by YunKyun on 2017-07-29.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private Context context;
    private List<PlaceItem> placeList;

    public RecyclerAdapter(Context context) {
        this.context = context;
        placeList = new ArrayList<>();
        placeList.add(new PlaceItem());
    }

    public void setPlaceList(List<PlaceItem> placeList) {
        this.placeList = placeList;
    }

    public void addPlaceList(PlaceItem place) {
        placeList.add(place);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        PlaceItem place = placeList.get(position);
        holder.image.setOnClickListener(new View.OnClickListener(){
            // TODO: Start PlaceActivity.
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Image Click!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_place_image) ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
