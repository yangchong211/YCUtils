package com.yc.rider.element.marker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.IntDef;

import com.didi.foundation.sdk.application.FoundationApplicationListener;
import com.didi.foundation.sdk.log.LogService;
import com.didi.rider.R;
import com.didi.rider.business.map.ab.MapResourceManager;
import com.didi.rider.component.map.MapConstants;
import com.didi.rider.util.icon.IconAbsGenerator;
import com.didi.sdk.logging.Logger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * description:地图Marker具体实现类
 * flutter传递数据进来，绘制骑手，商家，用户各类marker
 *
 * 注意：站点的属性需要flutter传递过来，比如图标，颜色，文本等内容
 * @author  杨充
 * @since   2021/5/21
 */
public class MarkerImpl extends BaseMarker {

    private final Logger mLogger = LogService.getLogger("MarkerImpl");

    //当前商家marker
    public static final int CURRENT_MERCHANT = 1;
    //当前顾客marker
    public static final int CURRENT_CUSTOMER = 2;
    //后续商家marker
    public static final int FOLLOW_UP_MERCHANT = 3;
    //后续顾客marker
    public static final int FOLLOW_UP_CUSTOMER = 4;
    //热区marker
    public static final int HOT_AREA_MARKER = 5;

    @IntDef({CURRENT_MERCHANT, CURRENT_CUSTOMER,FOLLOW_UP_MERCHANT,FOLLOW_UP_CUSTOMER,
            HOT_AREA_MARKER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NumbersInt {

    }

    private BaseMarkerOption mBaseMarkerOption;
    private IconAbsGenerator mIconGenerator;
    //marker类型
    private int type;
    protected View mInfoWindowView;
    protected TextView mNameTextView;

    public MarkerImpl(){

    }

    public MarkerImpl(int type){
        this.type = type;
    }

    @Override
    public View getInfoWindowView() {
        if (type == CURRENT_MERCHANT){
            //当前商家站点
            if (null == mInfoWindowView) {
                mInfoWindowView = LayoutInflater.from(FoundationApplicationListener.getApplication())
                        .inflate(R.layout.rider_layout_info_window, null);
                mNameTextView = mInfoWindowView.findViewById(R.id.rider_tv_info_window_content);
                int infoWindow = MapResourceManager.getInstance().getCurrentCustomerMarkerInfoWindow(mNameTextView);
                mNameTextView.setBackgroundResource(infoWindow);
            }
            mNameTextView.setText(getStationEntity().addressName);
        } else if (type == CURRENT_CUSTOMER){
            //当前商家站点
            if (null == mInfoWindowView) {
                mInfoWindowView = LayoutInflater.from(FoundationApplicationListener.getApplication())
                        .inflate(R.layout.rider_layout_info_window, null);
                mNameTextView = mInfoWindowView.findViewById(R.id.rider_tv_info_window_content);
                int infoWindow = MapResourceManager.getInstance().getCurrentMerchantMarkerInfoWindow(mNameTextView);
                mNameTextView.setBackgroundResource(infoWindow);
            }
            mNameTextView.setText(getStationEntity().addressName);
        }
        return null;
    }

    @Override
    public BaseMarkerOption getOption() {
        if (mBaseMarkerOption==null){
            mBaseMarkerOption = new BaseMarkerOption();
        }
        return mBaseMarkerOption;
    }

    /**
     * 设置图标
     * @param iconAbsGenerator                  图标view
     */
    public void setIconAbsGenerator(IconAbsGenerator iconAbsGenerator){
        this.mIconGenerator = iconAbsGenerator;
    }

    /**
     * 重新绘制
     */
    public void setIconDraw(String priceDisplay) {
        if (mIconGenerator!=null){
            mLogger.debug("not same hotAreaLevel, change icon");
            Bitmap bitmap = mIconGenerator.setText(priceDisplay).makeIcon();
            setIcon(bitmap);
        }
    }

    /**
     * 设置不同类型的线的配置属性
     * @param type                              类型
     */
    public void setType(@NumbersInt int type){
        mBaseMarkerOption = getOption();
        this.type = type;
        switch (type){
            //当前商家站点
            case CURRENT_MERCHANT:
                Pair<Float, Float> merchantAnchorPair = MapResourceManager.getInstance().getCurrentCustomerMarkerAnchor();
                Bitmap currentMerchantBitmap = BitmapFactory.decodeResource(
                        FoundationApplicationListener.getApplication().getResources(),
                        MapResourceManager.getInstance().getCurrentCustomerMarkerIcon());
                mBaseMarkerOption = getOption();
                mBaseMarkerOption.anchor(merchantAnchorPair.first, merchantAnchorPair.second);
                mBaseMarkerOption.zIndex(MapConstants.ZINDEX_MARKERS_CURRENT_STATION)
                        .icon(currentMerchantBitmap);
                break;
            //当前顾客站点
            case CURRENT_CUSTOMER:
                Pair<Float, Float> customerAnchorPair = MapResourceManager.getInstance().getCurrentMerchantMarkerAnchor();
                Bitmap currentCustomerBitmap = BitmapFactory.decodeResource(
                        FoundationApplicationListener.getApplication().getResources(),
                        MapResourceManager.getInstance().getCurrentMerchantMarkerIcon());
                mBaseMarkerOption = getOption();
                mBaseMarkerOption.anchor(customerAnchorPair.first, customerAnchorPair.second);
                mBaseMarkerOption.zIndex(MapConstants.ZINDEX_MARKERS_CURRENT_STATION)
                        .icon(currentCustomerBitmap);
                break;
            //后续商家marker
            case FOLLOW_UP_MERCHANT:
                mBaseMarkerOption = getOption();
                mBaseMarkerOption.zIndex(MapConstants.ZINDEX_MARKERS_FOLLOW_STATION);
                Bitmap followUpBitmap = BitmapFactory.decodeResource(
                        FoundationApplicationListener.getApplication().getResources(),
                        R.drawable.rider_ic_map_flow_up_station);
                mBaseMarkerOption.icon(followUpBitmap);
                break;
            //后续顾客marker
            case FOLLOW_UP_CUSTOMER:

                break;
            //热区marker
            case HOT_AREA_MARKER:

                break;
        }
    }

}
