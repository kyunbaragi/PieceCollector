package com.yunkyun.piececollector.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yunkyun.piececollector.R;
import com.yunkyun.piececollector.util.AppPreferenceKey;
import com.yunkyun.piececollector.util.SharedPreferencesService;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by YunKyun on 2017-08-08.
 */

public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";

    @BindView(R.id.iv_profile_image)
    CircleImageView profileImage;
    @BindView(R.id.tv_profile_nickname)
    TextView profileNickname;
    @BindView(R.id.tv_profile_email)
    TextView profileEmail;

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        String nickname = SharedPreferencesService.getInstance().getPrefStringData(AppPreferenceKey.PREF_USER_NICKNAME_KEY);
        String email = SharedPreferencesService.getInstance().getPrefStringData(AppPreferenceKey.PREF_USER_EMAIL_KEY);
        String profileImagePath = SharedPreferencesService.getInstance().getPrefStringData(AppPreferenceKey.PREF_USER_PROFILE_IMAGE_PATH_KEY);

        Glide.with(getActivity()).load(profileImagePath).into(profileImage);
        profileNickname.setText(nickname);
        profileEmail.setText(email);

        return view;
    }
}
