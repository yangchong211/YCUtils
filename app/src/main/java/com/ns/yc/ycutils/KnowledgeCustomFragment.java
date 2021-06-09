package com.ns.yc.ycutils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ns.yc.ycutilslib.blurView.RealTimeBlurView;
import com.ns.yc.ycutilslib.blurView.blur.BlurBehind;
import com.ns.yc.ycutilslib.loadingDialog.LoadDialog;
import com.ns.yc.ycutilslib.loadingDialog.ViewLoading;
import com.ns.yc.ycutilslib.loadingDialog.stateLoad.CustomLoadingDialog;
import com.ns.yc.ycutilslib.rippleLayout.MaterialRippleLayout;
import com.ns.yc.ycutilslib.switchButton.SwitchButton;

/**
 * ================================================
 * 作    者：杨充
 * 版    本：1.0
 * 创建日期：2017/5/22
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class KnowledgeCustomFragment extends BaseFragment implements View.OnClickListener {

    private MainActivity activity;
    private TextView tv0;
    private TextView tv1;
    private ViewLoading mLoading;
    private TextView tv2;
    private SwitchButton sb_btn;
    private LinearLayout ll;
    private TextView tv3;
    private TextView tv4;
    private TextView tv5;
    private TextView tv6;
    private TextView tv7;
    private TextView tv8;
    private TextView tv9;
    private TextView tv10;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }


    @Override
    public int getContentView() {
        return R.layout.fragment_custom;
    }

    @Override
    public void initView() {
        tv0 = (TextView) activity.findViewById(R.id.tv_0);
        tv1 = (TextView) activity.findViewById(R.id.tv_1);
        tv2 = (TextView) activity.findViewById(R.id.tv_2);
        tv3 = (TextView) activity.findViewById(R.id.tv_3);
        tv4 = (TextView) activity.findViewById(R.id.tv_4);
        tv5 = (TextView) activity.findViewById(R.id.tv_5);
        tv6 = (TextView) activity.findViewById(R.id.tv_6);
        tv7 = (TextView) activity.findViewById(R.id.tv_7);
        tv8 = (TextView) activity.findViewById(R.id.tv_8);
        tv9 = (TextView) activity.findViewById(R.id.tv_9);
        tv10 = (TextView) activity.findViewById(R.id.tv_10);
        ll = (LinearLayout) activity.findViewById(R.id.ll);
        sb_btn = (SwitchButton) activity.findViewById(R.id.sb_btn);
        initRipper();
    }

    private void initRipper() {
        MaterialRippleLayout.on(tv4)
                .rippleColor(Color.parseColor("#999999"))
                .rippleAlpha(0.2f)
                .rippleHover(true)
                .create();
        MaterialRippleLayout.on(ll)
                .rippleColor(Color.parseColor("#999999"))
                .rippleAlpha(0.2f)
                .rippleHover(true)
                .create();
    }

    @Override
    public void initListener() {
        tv0.setOnClickListener(this);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        sb_btn.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {

            }
        });
        tv5.setOnClickListener(this);
        tv6.setOnClickListener(this);
        tv7.setOnClickListener(this);
        tv8.setOnClickListener(this);
        tv9.setOnClickListener(this);
        tv10.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    private boolean intercept_back_event = false;


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_0:
                Intent intent0 = new Intent(activity, SwtichActivity.class);
                startActivity(intent0);
                break;
            case R.id.tv_1:
                startShowLoading();
                break;
            case R.id.tv_2:
                startShowDialog();
                break;
            case R.id.tv_5:
                BlurBehind.getInstance().execute(activity, new BlurBehind.OnBlurListener() {
                    @Override
                    public void onBlurComplete() {
                        Intent intent = new Intent(activity, BlurredActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    }
                });
                break;
            case R.id.tv_6:
                LoadDialog.show(activity,"加载中",false,false);
                new CountDownTimer(3000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        LoadDialog.dismiss(activity);
                    }
                }.start();
                break;
            case R.id.tv_7:
                final CustomLoadingDialog ld7 = new CustomLoadingDialog(activity);
                ld7.setInterceptBack(intercept_back_event).setLoadingText("加载中...");
                ld7.show();
                new CountDownTimer(3000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        ld7.close();
                    }
                }.start();
                break;
            case R.id.tv_8:
                final CustomLoadingDialog ld8 = new CustomLoadingDialog(activity);
                CustomLoadingDialog.Speed speed = CustomLoadingDialog.Speed.SPEED_TWO;
                ld8.setLoadingText("加载中")
                        .setSuccessText("加载成功")
                        .setInterceptBack(intercept_back_event)
                        .setLoadSpeed(speed)
                        .setRepeatCount(2000)
                        .setDrawColor(getResources().getColor(R.color.colorAccent))
                        .show();
                new CountDownTimer(3000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        ld8.loadSuccess();
                    }
                }.start();
                break;
            case R.id.tv_9:
                final CustomLoadingDialog ld9 = new CustomLoadingDialog(activity);
                CustomLoadingDialog.Speed speed9 = CustomLoadingDialog.Speed.SPEED_TWO;
                ld9.setLoadingText("加载中")
                        .setFailedText("加载失败")
                        .setInterceptBack(intercept_back_event)
                        .setLoadSpeed(speed9)
                        .setRepeatCount(2000)
                        .setDrawColor(getResources().getColor(R.color.colorAccent))
                        //延时5秒自动关闭，默认1秒
                        .setShowTime(5000)
                        .show();
                new CountDownTimer(3000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        ld9.loadFailed();
                    }
                }.start();
                break;
            case R.id.tv_10:
                Intent lockScreen = new Intent(activity, APPLockActivity.class);
                lockScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(lockScreen);
                break;
            default:
                break;
        }
    }

    private void startShowLoading() {
        // 添加Loading
        mLoading = new ViewLoading(activity ,2,"更新中") {
            @Override
            public void loadCancel() {

            }
        };
        if (!mLoading.isShowing()) {
            mLoading.show();
        }

        // 2秒后关闭
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (mLoading != null && mLoading.isShowing()) {
                    mLoading.dismiss();
                }
            }
        }, 2000);
    }


    private void startShowDialog() {
        View popMenuView = activity.getLayoutInflater().inflate(R.layout.dialog, null);
        RealTimeBlurView blur_view = (RealTimeBlurView) popMenuView.findViewById(R.id.blur_view);
        float v = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, activity.getResources().getDisplayMetrics());
        blur_view.setBlurRadius(v);
        final PopupWindow popMenu = new PopupWindow(popMenuView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        popMenu.setClippingEnabled(false);
        popMenu.setFocusable(true);         //点击其他地方关闭
        popMenu.setAnimationStyle(R.style.main_menu_animstyle);
        popMenu.showAtLocation(popMenuView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }


}
