package com.yunkyun.piececollector.activity;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kyleduo.switchbutton.SwitchButton;
import com.yunkyun.piececollector.R;
import com.yunkyun.piececollector.object.Record;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by YunKyun on 2017-08-22.
 */

public class PolaroidActivity extends BaseActivity {
    @BindView(R.id.polaroid)
    LinearLayout layout;
    @BindView(R.id.iv_polaroid)
    ImageView polaroid;
    @BindView(R.id.tv_memo_field)
    TextView memoText;
    @BindView(R.id.tv_date_field)
    TextView dateText;
    @BindView(R.id.sb_memo)
    SwitchButton memoSwitch;
    @BindView(R.id.sb_date)
    SwitchButton dateSwitch;
    @BindView(R.id.sb_gray_scale)
    SwitchButton grayScaleSwitch;


    private static final String TAG = "PolaroidActivity";
    private Record record;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polaroid);
        ButterKnife.bind(this);

        initPolaroidImage();
        setOnSwitchListener();
    }

    private void setOnSwitchListener() {

        dateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dateText.setVisibility(View.VISIBLE);
                } else {
                    dateText.setVisibility(View.INVISIBLE);
                }
            }
        });

        memoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    memoText.setVisibility(View.VISIBLE);
                } else {
                    memoText.setVisibility(View.INVISIBLE);
                }
            }
        });

        grayScaleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setGrayScale(polaroid);
                } else {
                    polaroid.clearColorFilter();
                }
            }
        });
    }

    private void setGrayScale(ImageView view){
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        view.setColorFilter(cf);
    }

    private void initPolaroidImage() {
        Intent intent = getIntent();
        if (intent != null) {
            record = intent.getParcelableExtra("record");

            dateText.setText(record.getDateToString());
            memoText.setText(record.getMemo());

            Glide.with(this).load(record.getImagePath()).into(polaroid);
        }
    }

    @OnClick({R.id.btn_close, R.id.btn_ok})
    void onButtonClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_close:
                onBackPressed();
                break;
            case R.id.btn_ok:
                sharePolaroidImage();
                break;
        }
    }

    private void sharePolaroidImage() {
        Bitmap bitmap = getBitmap(layout);
        File imageFile = saveBitmapImage(bitmap, layout.getMeasuredHeight(), layout.getMeasuredWidth());
        sendShareIntent(imageFile);

        finish();
    }

    private void sendShareIntent(File imageFile) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");

        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(intent, 0);
        if (resInfo.isEmpty()) {
            return;
        }

        List<Intent> shareIntentList = new ArrayList<>();

        for (ResolveInfo info : resInfo) {
            Intent shareIntent = (Intent) intent.clone();

            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageFile));

            shareIntent.setPackage(info.activityInfo.packageName);
            shareIntentList.add(shareIntent);
        }

        Intent chooserIntent = Intent.createChooser(shareIntentList.remove(0), "select");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, shareIntentList.toArray(new Parcelable[]{}));
        startActivity(chooserIntent);
    }

    private Bitmap getBitmap(LinearLayout layout) {
        layout.setDrawingCacheEnabled(true);
        layout.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(layout.getDrawingCache());
        layout.setDrawingCacheEnabled(false);

        return bitmap;
    }

    private File saveBitmapImage(Bitmap getbitmap, float height, float width) {
        File dir = new File(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_PICTURES, "PieceCollector");
        if (!dir.exists()) {
            dir.mkdir();
        }

        String timeStamp = new SimpleDateFormat("yyyyMMddkkmmss").format(new Date());
        File imageFile = new File(dir, timeStamp + ".png");

        if (!imageFile.exists()) {
            try {
                imageFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(imageFile);

            Bitmap well = getbitmap;
            Bitmap save = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            Canvas now = new Canvas(save);
            now.drawRect(new Rect(0, 0, (int) width, (int) height), paint);
            now.drawBitmap(well, new Rect(0, 0, well.getWidth(), well.getHeight()), new Rect(0, 0, (int) width, (int) height), null);

            if (save != null) {
                save.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            }

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }

        return imageFile;
    }
}
