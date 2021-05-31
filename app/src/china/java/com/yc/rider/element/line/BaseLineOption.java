package com.yc.rider.element.line;

import com.didi.common.map.model.LatLng;
import com.didi.common.map.model.LineOptions;

import java.util.Arrays;
import java.util.List;


/**
 * description:line划线的配置
 *
 * @author  杨充
 * @since   2021/5/21
 */
public class BaseLineOption {

    private final LineOptions mLineOptions;

    public BaseLineOption() {
        //创建配置
        mLineOptions = new LineOptions();
    }

    /**
     * 获取配置
     * @return                              options
     */
    public LineOptions getLineOptions() {
        return mLineOptions;
    }

    public BaseLineOption type(int type) {
        switch (type) {
            case LineType.LINE_TYPE_ARGB:
                mLineOptions.type(LineOptions.LineType.LINE_TYPE_ARGB);
                break;
            case LineType.LINE_TYPE_DOTTED:
                mLineOptions.type(LineOptions.LineType.LINE_TYPE_DOTTED);
                break;
        }
        return this;
    }

    public BaseLineOption zIndex(int zindex) {
        mLineOptions.zIndex(zindex);
        return this;
    }

    /**
     * 设置线颜色
     * @param color                         颜色
     * @return
     */
    public BaseLineOption color(int color) {
        mLineOptions.color(color);
        return this;
    }

    public BaseLineOption directionArrow(boolean hasDirectionArrow) {
        mLineOptions.directionArrow(hasDirectionArrow);
        return this;
    }

    /**
     * 设置线宽度
     * @param width                         宽度
     * @return
     */
    public BaseLineOption width(double width) {
        mLineOptions.width(width);
        return this;
    }

    /**
     * 设置线结束类型
     * @param lineEndType                   类型
     * @return
     */
    public BaseLineOption lineEndType(int lineEndType) {
        mLineOptions.lineEndType(lineEndType);
        return this;
    }

    /**
     * 添加数据
     * @param points                        数据
     * @return
     */
    public BaseLineOption addAll(List<LatLng> points) {
        mLineOptions.addAll(points);
        return this;
    }

    public BaseLineOption dottedResType(int type) {
        mLineOptions.dottedResType(type);
        return this;
    }

    public BaseLineOption spacing(float space) {
        mLineOptions.spacing(space);
        return this;
    }

    /**
     * 添加数据
     * @param points                        点
     * @return
     */
    public BaseLineOption add(LatLng... points) {
        mLineOptions.add(Arrays.asList(points));
        return this;
    }

    /**
     * 线的类型
     */
    public static final class LineType {

        public static final int LINE_TYPE_ARGB = 1;
        public static final int LINE_TYPE_DOTTED = 2;

        private LineType() {

        }
    }


}
