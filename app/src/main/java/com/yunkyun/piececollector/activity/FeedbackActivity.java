package com.yunkyun.piececollector.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yunkyun.piececollector.R;
import com.yunkyun.piececollector.call.NetworkService;
import com.yunkyun.piececollector.util.AppPreferenceKey;
import com.yunkyun.piececollector.util.SharedPreferencesService;
import com.yunkyun.piececollector.util.ToastMaker;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by YunKyun on 2017-08-17.
 */

public class FeedbackActivity extends BaseActivity {
    @BindView(R.id.tv_feedback_title)
    TextView title;
    @BindView(R.id.tv_feedback_description)
    TextView description;
    @BindView(R.id.et_feedback)
    EditText feedbackContent;
    @BindView(R.id.btn_send_feedback)
    Button sendFeedback;

    private static final String TAG = "FeedbackActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_send_feedback, R.id.btn_close})
    void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send_feedback:
                sendFeedback();
                break;
            case R.id.btn_close:
                onBackPressed();
                break;
        }
    }

    private void sendFeedback() {
        String feedbackText = feedbackContent.getText().toString();

        if(feedbackText.isEmpty()) {
            ToastMaker.makeShortToast(this, "의견을 입력해주세요.");
        } else {
            HashMap<String, String> parameters = new HashMap<>();

            Long userID = SharedPreferencesService.getInstance().getPrefLongData(AppPreferenceKey.PREF_USER_ID_KEY);

            parameters.put("user_id", String.valueOf(userID));
            parameters.put("feedback", feedbackText);

            NetworkService service = NetworkService.retrofit.create(NetworkService.class);

            Call<ResponseBody> call = service.sendFeedback(parameters);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()) {
                        title.setText("피드백이 제출되었습니다.");
                        description.setText("의견을 공유해 주셔서 감사합니다. 보내주신 피드백이나 버그 신고는 개별적으로 연락을 드리지 않습니다. 양해를 부탁드립니다.");
                        feedbackContent.setVisibility(View.GONE);
                        sendFeedback.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d(TAG, "onFailure");
                }
            });
        }
    }
}
