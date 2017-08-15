package com.yunkyun.piececollector.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunkyun.piececollector.R;
import com.yunkyun.piececollector.activity.RecordActivity;
import com.yunkyun.piececollector.object.HistoryDivider;
import com.yunkyun.piececollector.object.Record;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by YunKyun on 2017-08-09.
 */

public class HistoryRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_RECORD = 0;
    private static final int TYPE_DIVIDER = 1;
    private Context context;
    private List<Record> recordList;
    private List<Object> outputList;

    public HistoryRecyclerAdapter(Context context) {
        this.context = context;
        outputList = new ArrayList<>();
    }

    public void setRecordList(List<Record> recordList) {
        this.recordList = recordList;
    }

    public void filterHistoryList(String text) {
        List<Record> searchedList = new ArrayList<>();
        for (Record record : recordList) {
            //use .toLowerCase() for better matches
            if (record.getTitle().contains(text)) {
                searchedList.add(record);
            }
        }

        addDividerOnList(searchedList);
    }

    public void initHistoryList() {
        addDividerOnList(this.recordList);
    }



    private void addDividerOnList(List<Record> recordList) {
        List<Object> outputList = new ArrayList<>();
        if (recordList.size() > 0) {


            // Start Header Divider
            HistoryDivider current = new HistoryDivider(recordList.get(0).getCreated());
            outputList.add(0, current);

            for (Record record : recordList) {
                HistoryDivider next = new HistoryDivider(record.getCreated());
                if (current.compareTo(next) != 0) {
                    outputList.add(next);
                    current = next;
                }
                outputList.add(record);
            }
        }

        this.outputList = outputList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case TYPE_RECORD:
                View view1 = inflater.inflate(R.layout.item_record_history, null);
                viewHolder = new ViewHolderRecord(view1);
                break;
            case TYPE_DIVIDER:
                View view2 = inflater.inflate(R.layout.item_divider_history, null);
                viewHolder = new ViewHolderDivider(view2);
                break;
            default:
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewholder, int position) {
        switch (viewholder.getItemViewType()) {
            case TYPE_RECORD:
                renderRecordItem((ViewHolderRecord) viewholder, position);
                break;
            case TYPE_DIVIDER:
                renderDividerItem((ViewHolderDivider) viewholder, position);
                break;
            default:
                break;
        }
    }

    private void renderRecordItem(ViewHolderRecord viewholder, int position) {
        final Record record = (Record) outputList.get(position);
        viewholder.title.setText(record.getTitle());
        viewholder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, RecordActivity.class);
                intent.putExtra("record", record);
                context.startActivity(intent);
            }
        });
    }

    private void renderDividerItem(ViewHolderDivider viewholder, int position) {
        HistoryDivider divider = (HistoryDivider) outputList.get(position);
        String date = String.format("%d. %d. %d.", divider.getYear(), divider.getMonth(), divider.getDay());
        viewholder.divider.setText(date);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (outputList.get(position) instanceof Record) {
            return TYPE_RECORD;
        } else if (outputList.get(position) instanceof HistoryDivider) {
            return TYPE_DIVIDER;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return outputList.size();
    }

    class ViewHolderRecord extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_record_title)
        TextView title;

        public ViewHolderRecord(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class ViewHolderDivider extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_divider)
        TextView divider;

        public ViewHolderDivider(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
