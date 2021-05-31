package com.yc.rider.element.polygon;


import android.annotation.SuppressLint;

import androidx.annotation.IntDef;

import com.didi.foundation.sdk.application.FoundationApplicationListener;
import com.didi.rider.component.map.MapConstants;
import com.didi.rider.util.DisplayUtils;
import com.didi.rider.util.ThemeAdapter;
import com.didi.rlab.uni_foundation.heatmap.MapPositionModel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * description:多边形具体实现类
 * flutter传递数据进来，不管是热力图，热区，还是积压单，统一处理绘制多边形逻辑
 *
 * @author  杨充
 * @since   2021/5/21
 */
public class PolygonImpl extends BasePolygon {

    private BasePolygonOption mPolygonOption;
    //骑手工作边界多边形
    public static final int BOUND = 1;
    //热区边界
    public static final int HOT_AREA_BORDER = 2;
    //热区填充
    public static final int HOT_AREA_FILL = 3;
    //积压单格子
    public static final int GRID = 4;


    @IntDef({BOUND, HOT_AREA_BORDER, HOT_AREA_FILL,GRID})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NumbersInt {
    }


    @Override
    public BasePolygonOption getOption() {
        if (mPolygonOption==null){
            mPolygonOption = new BasePolygonOption();
        }
        return mPolygonOption;
    }

    @Override
    public void setPoints(List<MapPositionModel> points) {
        mPolygonOption = getOption();
        //添加数据
        mPolygonOption.add(convertMapLatLngList(points));
    }

    /**
     * 设置不同类型的Polygon配置
     * @param type                              类型，BOUND工作边界，HOT_AREA_BORDER热区边界；HOT_AREA_FILL热区填充；GRID积压单格子
     */
    public void setTypeLie(@NumbersInt int type){
        mPolygonOption = getOption();
        switch (type){
            //骑手工作边界多边形
            case BOUND:
                int strokeWidthBound = DisplayUtils.dpToPx(FoundationApplicationListener.getApplication(), 2);
                mPolygonOption.fillColor(0x00000000);
                mPolygonOption.zIndex(MapConstants.ZINDEX_HEATMAP_BOUND_SHAPS);
                mPolygonOption.strokeWidth(strokeWidthBound);
                mPolygonOption.strokeColor(ThemeAdapter.sHeatMapBoundShapeStrokeColor);
                break;
            //热区边界
            case HOT_AREA_BORDER:
                int strokeWidthArea = DisplayUtils.dpToPx(FoundationApplicationListener.getApplication(),
                        MapConstants.HOT_AREA_STROKE_DP);
                mPolygonOption
                        .strokeColor(ThemeAdapter.sHeatMapHotAreaStrokeColor)
                        .strokeWidth(strokeWidthArea)
                        .fillColor(MapConstants.CLEAR_COLOR)
                        .zIndex(MapConstants.ZINDEX_HEATMAP_BOUND_SHAPS);
                break;
            //热区填充
            case HOT_AREA_FILL:
                mPolygonOption
                        .fillColor(ThemeAdapter.sHeatMapHotAreaColor)
                        .zIndex(MapConstants.ZINDEX_HEATMAP_HOT_AREA);
                break;
            //积压单格子
            case GRID:
                mPolygonOption
                        //.fillColor(levelToColor(mLevel))
                        .zIndex(MapConstants.ZINDEX_HEATMAP_CELL_GRID)
                        .strokeColor(MapConstants.CLEAR_COLOR);
                break;
        }
    }

    /**
     * 设置level
     * @param level                             级别
     */
    public void checkToChangeLevel(int level) {
        int levelToColor = levelToColor(level);
        setFillColor(levelToColor);
    }

    /**
     * 级别对应的颜色
     * @param level                             level
     * @return
     */
    @SuppressLint("ReturnCount")
    private int levelToColor(int level) {
        switch (level) {
            case 1:
                return ThemeAdapter.sHeatMapColorLevel1;
            case 2:
                return ThemeAdapter.sHeatMapColorLevel2;
            case 3:
                return ThemeAdapter.sHeatMapColorLevel3;
            case 4:
                return ThemeAdapter.sHeatMapColorLevel4;
            case 5:
                return ThemeAdapter.sHeatMapColorLevel5;
            default:
                return 0x00000000;
        }
    }

}
