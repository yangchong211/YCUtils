package com.yc.rider;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.yc.rider.R;
import com.ns.yc.ycutilslib.fragmentBack.BackHandlerHelper;
import com.ns.yc.ycutilslib.loadingDialog.stateLoad.CustomLoadingDialog;
import com.ns.yc.ycutilslib.loadingDialog.stateLoad.CustomStyleManager;
import com.ns.yc.ycutilslib.managerLeak.InputMethodManagerLeakUtils;
import com.ns.yc.ycutilslib.viewPager.NoSlidingViewPager;

import java.util.ArrayList;

/**
 * ================================================
 * 作    者：杨充
 * 版    本：1.0
 * 创建日期：2017/5/22
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class MainActivity extends AppCompatActivity {

    private TabLayout tab_layout;
    private NoSlidingViewPager vp_content;

    private ArrayList<String> mTitleList = new ArrayList<>();
    private ArrayList<Fragment> mFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }


    private void init() {
        initFindViewById();
        initFragmentList();
        initViewPagerAndTab();
    }


    private void initFindViewById() {
        tab_layout = (TabLayout)findViewById(R.id.tab_layout);
        vp_content = (NoSlidingViewPager)findViewById(R.id.vp_content);
    }


    private void initFragmentList() {
        mTitleList.clear();
        mFragments.clear();
        mTitleList.add("常用功能");
        mTitleList.add("生活福利");
        mTitleList.add("休息视频");
        mFragments.add(new KnowledgeCustomFragment());
        mFragments.add(new KnowledgeAndroidFragment());
        mFragments.add(new KnowledgeOtherFragment());
    }


    private void initViewPagerAndTab() {
        /**
         * 注意使用的是：getChildFragmentManager，
         * 这样setOffscreenPageLimit()就可以添加上，保留相邻2个实例，切换时不会卡
         * 但会内存溢出，在显示时加载数据
         */
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        BasePagerAdapter myAdapter = new BasePagerAdapter(supportFragmentManager, mFragments, mTitleList);
        vp_content.setAdapter(myAdapter);
        // 左右预加载页面的个数
        vp_content.setOffscreenPageLimit(3);
        myAdapter.notifyDataSetChanged();
        tab_layout.setTabMode(TabLayout.MODE_FIXED);
        tab_layout.setupWithViewPager(vp_content);
        vp_content.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==2){
                    backHandled = true;
                }else {
                    backHandled = false;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private long lastBackPress;
    public boolean backHandled;
    @Override
    public void onBackPressed() {
        if (!BackHandlerHelper.handleBackPress(this)) {
            if (System.currentTimeMillis() - lastBackPress < 1000) {
                super.onBackPressed();
            } else {
                lastBackPress = System.currentTimeMillis();
                finish();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        InputMethodManagerLeakUtils.fixInputMethodManagerLeak(this);
    }
}
