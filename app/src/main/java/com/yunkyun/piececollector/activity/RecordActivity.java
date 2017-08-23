package com.yunkyun.piececollector.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import com.yunkyun.piececollector.util.AppPreferenceKey;
import com.yunkyun.piececollector.util.SharedPreferencesService;
import com.yunkyun.piececollector.util.ToastMaker;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
            dateUI.setText(record.getDateToString());
            memoUI.setText(record.getMemo());
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
                openPolaroidActivity();
                break;
            case R.id.btn_back:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    private void openPolaroidActivity() {
        Intent intent = new Intent(this, PolaroidActivity.class);
        intent.putExtra("record", record);
        startActivity(intent);
    }

    private void showEditDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        EditDialogFragment editDialog = new EditDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putString(EditDialogFragment.BUNDLE_USER_MEMO_KEY, record.getMemo());

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
                        Uri contentURI = data.getData();
                        Uri fileURI = null;
                        try {
                            fileURI = convertContentToFileUri(this, contentURI);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        File imageFile = new File(fileURI.getPath());
                        changeImage(imageFile);
                    }
                    break;
            }
        }
    }

    private Uri convertContentToFileUri(Context context, Uri uri) throws Exception {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, null, null, null, null);
            cursor.moveToNext();
            return Uri.fromFile(new File(cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))));
        } finally {
            if(cursor != null)
                cursor.close();
        }
    }

    private void changeImage(File imageFile) {
        NetworkService service = NetworkService.retrofit.create(NetworkService.class);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);

        HashMap<String, String> parameters = new HashMap<>();
        Long userID = SharedPreferencesService.getInstance().getPrefLongData(AppPreferenceKey.PREF_USER_ID_KEY);
        parameters.put("record_id", String.valueOf(record.getId()));
        parameters.put("user_id", String.valueOf(userID));

        Call<okhttp3.ResponseBody> call = service.changeImage(parameters, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e(TAG, "onResponse in uploadImage");
                try {
                    refreshImage(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure in uploadImage");
            }
        });
    }

    private void refreshImage(String imagePath) {
        Glide.with(this).load(imagePath).into(photoUI);
        ToastMaker.makeShortToast(this, "사진이 수정되었습니다.");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String userInput) {
        refreshMemo(userInput);

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("id", String.valueOf(record.getId()));
        parameters.put("memo", record.getMemo());

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

    private void refreshMemo(String userInput) {
        record.setMemo(userInput);
        memoUI.setText(userInput);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
