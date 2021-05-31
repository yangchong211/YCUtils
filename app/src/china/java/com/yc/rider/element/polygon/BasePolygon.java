package com.yc.rider.element.polygon;

import com.didi.common.map.internal.IMapElement;
import com.didi.common.map.model.LatLng;
import com.didi.common.map.model.Polygon;
import com.didi.rider.business.map.ab.wrapper.RiderMapElement;
import com.didi.rlab.uni_foundation.heatmap.MapPositionModel;

import java.util.ArrayList;
import java.util.List;


/**
 * description:多边形基类
 *
 * @author  杨充
 * @since   2021/5/21
 */
public abstract class BasePolygon extends RiderMapElement {

    public abstract BasePolygonOption getOption();

    /**
     * 设置点集合
     * @param points                点集合
     */
    public abstract void setPoints(List<MapPositionModel> points);

    /**
     * 设置填充颜色
     * @param color                 填充颜色
     */
    public void setFillColor(int color) {
        IMapElement mapElement = getMapElement();
        if (null == mapElement) {
            return;
        }
        ((Polygon)mapElement).setFillColor(color);
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
