package com.yunkyun.piececollector.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yunkyun.piececollector.R;
import com.yunkyun.piececollector.call.NetworkService;
import com.yunkyun.piececollector.fragment.EditDialogFragment;
import com.yunkyun.piececollector.object.Record;
import com.yunkyun.piececollector.util.ToastMaker;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by YunKyun on 2017-07-29.
 */

public class RecordActivity extends BaseActivity implements EditDialogFragment.NoticeDialogListener {
    @BindView(R.id.iv_photo)
    ImageView photoUI;
    @BindView(R.id.tv_detail_title)
    TextView titleUI;
    @BindView(R.id.tv_detail_date)
    TextView dateUI;
    @BindView(R.id.tv_detail_memo)
    TextView memoUI;

    private static final String TAG = "RecordActivity";
    private static final int REQUEST_PICK_PHOTO_FROM_ALBUM = 0;
    private Record record;
    private String memo;
    private Uri photoUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            record = intent.getParcelableExtra("record");
            Glide.with(this).load(record.getImagePath()).into(photoUI);
            titleUI.setText(record.getTitle());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(record.getCreated());

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            String date = String.format("%d. %d. %d.", year, month, day);

            dateUI.setText(date);
            memo = record.getMemo();
            if (memo == null) {
                memo = "메모를 남겨주세요.";
            }
            memoUI.setText(memo);
        }
    }

    @OnClick({R.id.btn_edit, R.id.btn_pick_photo, R.id.btn_share, R.id.btn_back})
    void onButtonClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_edit:
                showEditDialog();
                break;
            case R.id.btn_pick_photo:
                showGallery();
                break;
            case R.id.btn_share:
                break;
            case R.id.btn_back:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    private void showEditDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        EditDialogFragment editDialog = new EditDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putString(EditDialogFragment.BUNDLE_USER_MEMO_KEY, memo);

        editDialog.setArguments(bundle);
        editDialog.show(fragmentManager, EditDialogFragment.TAG);
    }

    private void showGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_PICK_PHOTO_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PICK_PHOTO_FROM_ALBUM:
                    if (data != null) {
                        photoUri = data.getData();
                        File imageFile = new File(photoUri.toString());
                        ToastMaker.makeShortToast(this, imageFile.getName());
                    }

                    break;
            }
        }
    }

    /*private void uploadImage(Place place) {
        NetworkService service = NetworkService.retrofit.create(NetworkService.class);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image*//*"), imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);

        HashMap<String, String> parameters = new HashMap<>();

        Long userID = SharedPreferencesService.getInstance().getPrefLongData(AppPreferenceKey.PREF_USER_ID_KEY);

        parameters.put("user_id", String.valueOf(userID));
        parameters.put("place_id", String.valueOf(place.getId()));

        Call<okhttp3.ResponseBody> call = service.postImage(parameters, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e(TAG, "onResponse in uploadImage");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure in uploadImage");
            }
        });
    }*/

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String userInput) {
        memoUI.setText(userInput);
        record.setMemo(userInput);
        memo = userInput;

        Log.e(TAG, "onDialogPositiveClick");

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("id", String.valueOf(record.getId()));
        parameters.put("memo", memo);

        NetworkService service = NetworkService.retrofit.create(NetworkService.class);
        Call<okhttp3.ResponseBody> call = service.postMemo(parameters);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e(TAG, "onResponse");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure");
            }
        });
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
