package com.yc.rider;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.yc.rider.R;
import com.ns.yc.ycutilslib.blurView.blur.BlurBehind;

/**
 * Created by PC on 2017/12/6.
 * 作者：PC
 */

public class BlurredActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_custom);

        BlurBehind.getInstance()
                .withAlpha(70)
                .withFilterColor(Color.parseColor("#636363"))
                .setBackground(this);
    }
}
