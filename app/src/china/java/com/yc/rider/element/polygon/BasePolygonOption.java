package com.yc.rider.element.polygon;

import com.didi.common.map.model.LatLng;
import com.didi.common.map.model.PolygonOptions;

import java.util.List;


/**
 * description:多边形选项配置
 *
 * @author  杨充
 * @since   2021/5/21
 */
public class BasePolygonOption {

    private final PolygonOptions mPolygonOptions;

    public BasePolygonOption() {
        //创建Polygon配置
        mPolygonOptions = new PolygonOptions();
    }

    /**
     * 获取Polygon配置
     * @return                          Polygon配置
     */
    public PolygonOptions getPolygonOptions() {
        return mPolygonOptions;
    }

    /**
     * 添加经纬度集合数据
     * @param points                    points
     * @return
     */
    public BasePolygonOption add(List<LatLng> points) {
        this.mPolygonOptions.add(points);
        return this;
    }

    /**
     * 填充颜色
     * @param fillColor                 颜色
     * @return
     */
    public BasePolygonOption fillColor(int fillColor) {
        this.mPolygonOptions.fillColor(fillColor);
        return this;
    }

    /**
     * z轴
     * @param zindex                    index
     * @return
     */
    public BasePolygonOption zIndex(int zindex) {
        this.mPolygonOptions.zIndex(zindex);
        return this;
    }

    /**
     * 宽
     * @param strokeWidth               宽
     * @return
     */
    public BasePolygonOption strokeWidth(float strokeWidth) {
        this.mPolygonOptions.strokeWidth(strokeWidth);
        return this;
    }

    /**
     * 颜色
     * @param strokeColor               颜色
     * @return
     */
    public BasePolygonOption strokeColor(int strokeColor) {
        this.mPolygonOptions.strokeColor(strokeColor);
        return this;
    }
}
