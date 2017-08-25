package com.yunkyun.piececollector.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.Orientation;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;
import com.yunkyun.piececollector.R;
import com.yunkyun.piececollector.adapter.CollectionRecyclerAdapter;
import com.yunkyun.piececollector.call.NetworkService;
import com.yunkyun.piececollector.object.Collection;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by YunKyun on 2017-07-27.
 */

public class CollectionFragment extends Fragment implements DiscreteScrollView.OnItemChangedListener {
    @BindView(R.id.scroll_view)
    DiscreteScrollView scrollView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.tv_page_number)
    TextView pageNumber;

    public static final String TAG = "CollectionFragment";
    private CollectionRecyclerAdapter adapter;

    public static CollectionFragment newInstance() {
        CollectionFragment fragment = new CollectionFragment();
        return fragment;
    }

    public CollectionFragment() {
        // Required empty public constructor.
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);
        ButterKnife.bind(this, view);

        setRecyclerView();
        loadCollectionList();

        return view;
    }

    private void setRecyclerView() {
        scrollView.setOrientation(Orientation.HORIZONTAL);
        adapter = new CollectionRecyclerAdapter(getContext());
        scrollView.setAdapter(adapter);
        scrollView.addOnItemChangedListener(this);
        scrollView.setItemTransitionTimeMillis(500);
        scrollView.setItemTransformer(new ScaleTransformer.Builder().setMinScale(0.8f).build());
    }

    private void loadCollectionList() {
        NetworkService service = NetworkService.retrofit.create(NetworkService.class);

        Call<List<Collection>> call = service.getCollections();
        call.enqueue(new Callback<List<Collection>>() {
            @Override
            public void onResponse(Call<List<Collection>> call, Response<List<Collection>> response) {
                List<Collection> collectionList = response.body();
                Log.d(TAG, String.valueOf(collectionList.size()));
                setCollectionList(collectionList);
            }

            @Override
            public void onFailure(Call<List<Collection>> call, Throwable t) {

            }
        });
    }

    private void setCollectionList(List<Collection> collectionList) {
        adapter.setCollectionList(collectionList);
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int position) {
        String index = String.valueOf(position + 1);
        if(position + 1 < 10) {
            index = "0" + index;
        }
        pageNumber.setText(index);
    }
}
