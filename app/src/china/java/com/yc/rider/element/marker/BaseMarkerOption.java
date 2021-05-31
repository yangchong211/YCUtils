package com.yc.rider.element.marker;


import android.graphics.Bitmap;

import com.didi.common.map.model.BitmapDescriptorFactory;
import com.didi.common.map.model.LatLng;
import com.didi.common.map.model.MarkerOptions;


/**
 * description:Marker选项配置
 *
 * @author  杨充
 * @since   2021/5/21
 */
public final class BaseMarkerOption {

    private final MarkerOptions mMarkerOptions;

    public BaseMarkerOption() {
        //创建配置
        mMarkerOptions = new MarkerOptions();
    }

    public MarkerOptions getMarkerOptions() {
        return mMarkerOptions;
    }

    /**
     * 设置透明度
     * @param alpha                         透明度
     * @return
     */
    public BaseMarkerOption setAlpha(float alpha) {
        mMarkerOptions.alpha(alpha);
        return this;
    }

    /**
     * 设置标题
     * @param title                         标题
     * @return
     */
    public BaseMarkerOption setTitle(String title) {
        mMarkerOptions.title(title);
        return this;
    }

    /**
     * 设置抛锚
     * @param u                             u
     * @param v                             v
     * @return
     */
    public BaseMarkerOption anchor(float u, float v) {
        mMarkerOptions.anchor(u,v);
        return this;
    }

    public BaseMarkerOption zIndex(int zndex) {
        mMarkerOptions.zIndex(zndex);
        return this;
    }

    /**
     * 设置icon
     * @param icon                          图标
     * @return
     */
    public BaseMarkerOption icon(Bitmap icon) {
        mMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        return this;
    }

    /**
     * 动画
     * @param dodge                         dodge
     * @return
     */
    public BaseMarkerOption dodgeAnnotation(boolean dodge) {
        mMarkerOptions.dodgeAnnotation(dodge);
        return this;
    }

    /**
     * 设置位置
     * @param latitude                      经度
     * @param longitude                     纬度
     * @return
     */
    public BaseMarkerOption position(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        mMarkerOptions.position(latLng);
        return this;
    }

    /**
     * 设置位置
     * @param position                      经纬度
     */
    public BaseMarkerOption position(LatLng position) {
        mMarkerOptions.position(position);
        return this;
    }

}
