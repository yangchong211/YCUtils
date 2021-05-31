package com.yc.rider.element.line;

import androidx.annotation.IntDef;

import com.didi.common.map.model.LatLng;
import com.didi.rider.business.map.ab.MapResourceManager;
import com.didi.rider.business.map.ab.wrapper.line.RiderLineOption;
import com.didi.rider.component.map.MapConstants;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * description:线具体实现类
 * flutter传递数据进来，所有线的绘制使用这个类
 *
 * @author  杨充
 * @since   2021/5/21
 */
public class LineImpl extends BaseLine{

    private BaseLineOption mBaseLineOption;
    //当前路线
    public static final int CURRENT = 1;
    //后续路线
    public static final int FOLLOW_UP = 2;
    //步行路线
    public static final int WALK = 3;

    @IntDef({CURRENT, FOLLOW_UP, WALK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NumbersInt {
    }

    @Override
    public BaseLineOption getOption() {
        if (mBaseLineOption==null){
            mBaseLineOption = new BaseLineOption();
        }
        return mBaseLineOption;
    }

    @Override
    public void setPoints(List<LatLng> points) {
        super.setPoints(points);
        mBaseLineOption = getOption();
        mBaseLineOption.addAll(points);
    }

    @Override
    public void setPoints(LatLng start, LatLng end) {
        super.setPoints(start, end);
        mBaseLineOption = getOption();
        mBaseLineOption.add(start,end);
    }

    /**
     * 设置不同类型的线
     * @param type                              类型，CURRENT当前线，FOLLOW_UP后续线，WALK步行线
     */
    public void setTypeLie(@NumbersInt int type){
        mBaseLineOption = getOption();
        switch (type){
            case CURRENT:
                mBaseLineOption.type(RiderLineOption.LineType.LINE_TYPE_ARGB);
                mBaseLineOption.zIndex(MapConstants.ZINDEX_ROUTE_CURRENT);
                mBaseLineOption.color(MapResourceManager.getInstance().getCurrentRouteColor());
                mBaseLineOption.directionArrow(true);
                mBaseLineOption.width(MapResourceManager.getInstance().getCurrentRouteWidth());
                mBaseLineOption.lineEndType(RiderLineOption.LineEndType.LINE_END_TYPE_ROUND);
                break;
            case FOLLOW_UP:
                mBaseLineOption.type(RiderLineOption.LineType.LINE_TYPE_ARGB);
                mBaseLineOption.zIndex(MapConstants.ZINDEX_ROUTE_FOLLOW_UP);
                mBaseLineOption.color(MapResourceManager.getInstance().getFollowRouteColor());
                mBaseLineOption.directionArrow(true);
                mBaseLineOption.lineEndType(RiderLineOption.LineEndType.LINE_END_TYPE_ROUND);
                mBaseLineOption.width(MapResourceManager.getInstance().getFollowRouteWidth());
                break;
            case WALK:
                mBaseLineOption.type(RiderLineOption.LineType.LINE_TYPE_DOTTED);
                mBaseLineOption.zIndex(MapConstants.ZINDEX_ROUTE_CURRENT);
                mBaseLineOption.color(MapResourceManager.getInstance().getWalkRouteColor());
                //mBaseLineOption.dottedResType(RiderLineOption.DottedResType.ARROW_BULE);
                //mBaseLineOption.customImageNameInAssets("rider_map_walk_line.png");
                mBaseLineOption.width(MapResourceManager.getInstance().getWalkRouteWidth());
                mBaseLineOption.spacing(MapResourceManager.getInstance().getWalkRouteWidth());
                break;
        }
    }

}
