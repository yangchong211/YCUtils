package com.yc.rider;

import android.app.Application;

import com.ns.yc.ycutilslib.loadingDialog.stateLoad.CustomLoadingDialog;
import com.ns.yc.ycutilslib.loadingDialog.stateLoad.CustomStyleManager;

/**
 * Created by yc on 2018/1/31.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CustomStyleManager s = new CustomStyleManager();
        //在这里调用方法设置s的属性
        //code here...
        s.Anim(false).repeatTime(0).contentSize(-1).intercept(true);
        CustomLoadingDialog.initStyle(s);
    }
}
