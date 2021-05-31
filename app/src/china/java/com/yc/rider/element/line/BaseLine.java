package com.yc.rider.element.line;

import com.didi.common.map.model.LatLng;
import com.didi.rider.business.map.ab.wrapper.RiderMapElement;
import com.didi.rlab.uni_foundation.heatmap.MapPositionModel;

import java.util.ArrayList;
import java.util.List;


/**
 * description:line基类
 *
 * @author  杨充
 * @since   2021/5/21
 */
public abstract class BaseLine extends RiderMapElement {


    public abstract BaseLineOption getOption();

    /**
     * 设置点集合
     * @param points                    点集合
     */
    public void setPoints(List<LatLng> points) {

    }

    /**
     * 设置开始点和结束点
     * @param start                     开始位置
     * @param end                       结束位置
     */
    public void setPoints(LatLng start, LatLng end) {

    }

    /**
     * 转换成地图sdk经纬度
     */
    public List<LatLng> convertMapLatLngList(List<MapPositionModel> points) {
        ArrayList<LatLng> latLngs = new ArrayList<>();
        for (MapPositionModel point : points) {
            latLngs.add(new LatLng(point.getLat(), point.getLng()));
        }
        return latLngs;
    }

}
