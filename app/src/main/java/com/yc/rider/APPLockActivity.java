package com.yc.rider;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.yc.rider.R;
import com.ns.yc.ycutilslib.slideLayout.SlitherFinishLayout;

/**
 * Created by yc on 2018/3/23.
 */

public class APPLockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        initWindow();
        initLockView();
    }


    private void initWindow() {
        //注意需要做一下判断
        if (getWindow() != null) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            // 锁屏的activity内部也要做相应的配置，让activity在锁屏时也能够显示，同时去掉系统锁屏。
            // 当然如果设置了系统锁屏密码，系统锁屏是没有办法去掉的
            // FLAG_DISMISS_KEYGUARD用于去掉系统锁屏页
            // FLAG_SHOW_WHEN_LOCKED使Activity在锁屏时仍然能够显示
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                window.getDecorView().setSystemUiVisibility(
                        // SYSTEM_UI_FLAG_LAYOUT_STABLE保持整个View稳定，使View不会因为SystemUI的变化而做layout
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                                // SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION，开发者容易被其中的HIDE_NAVIGATION所迷惑，
                                // 其实这个Flag没有隐藏导航栏的功能，只是控制导航栏浮在屏幕上层，不占据屏幕布局空间；
                                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                // SYSTEM_UI_FLAG_HIDE_NAVIGATION，才是能够隐藏导航栏的Flag；
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                // SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN，由上面可知，也不能隐藏状态栏，只是使状态栏浮在屏幕上层。
                                View.SYSTEM_UI_FLAG_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_IMMERSIVE);
            }
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && getWindow() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().getDecorView().setSystemUiVisibility(
                        // SYSTEM_UI_FLAG_LAYOUT_STABLE保持整个View稳定，使View不会因为SystemUI的变化而做layout
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                                // SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION，开发者容易被其中的HIDE_NAVIGATION所迷惑，
                                // 其实这个Flag没有隐藏导航栏的功能，只是控制导航栏浮在屏幕上层，不占据屏幕布局空间；
                                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                // SYSTEM_UI_FLAG_HIDE_NAVIGATION，才是能够隐藏导航栏的Flag；
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                // SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN，由上面可知，也不能隐藏状态栏，只是使状态栏浮在屏幕上层。
                                View.SYSTEM_UI_FLAG_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_IMMERSIVE);
            }
        }
    }


    @Override
    public void onBackPressed() {
        // 不做任何事，为了屏蔽back键
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int key = event.getKeyCode();
        switch (key) {
            case KeyEvent.KEYCODE_BACK: {
                return true;
            }
            case KeyEvent.KEYCODE_MENU: {
                return true;
            }
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void initLockView() {
        SlitherFinishLayout slide_layout = (SlitherFinishLayout)findViewById(R.id.slide_layout);
        slide_layout.setOnSlitherFinishListener(new SlitherFinishLayout.OnSlitherFinishListener() {
            @Override
            public void onSlitherFinish() {
                finish();
            }
        });
        slide_layout.setTouchView(getWindow().getDecorView());
    }

}
