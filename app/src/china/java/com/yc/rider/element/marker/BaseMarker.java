package com.yc.rider.element.marker;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.view.View;
import android.view.View.MeasureSpec;

import com.didi.common.map.Projection;
import com.didi.common.map.listener.OnMarkerClickListener;
import com.didi.common.map.model.BitmapDescriptor;
import com.didi.common.map.model.BitmapDescriptorFactory;
import com.didi.common.map.model.LatLng;
import com.didi.common.map.model.Marker;
import com.didi.foundation.sdk.application.FoundationApplicationListener;
import com.didi.foundation.sdk.log.LogService;
import com.didi.rider.business.map.ab.wrapper.RiderMapElement;
import com.didi.rider.business.map.ab.wrapper.RiderMapView;
import com.didi.rider.flutter.map.MapViewManager;
import com.didi.rider.net.entity.trip.StationEntity;
import com.didi.sdk.logging.Logger;


/**
 * description:地图Marker基类
 *
 * @author  杨充
 * @since   2021/5/21
 */
public abstract class BaseMarker extends RiderMapElement {

    private final Logger mLogger = LogService.getLogger("BaseMarker");
    private StationEntity mStationEntity;

    public BaseMarker() {

    }

    public BaseMarker(StationEntity stationEntity) {
        this.mStationEntity = stationEntity;
    }

    /**
     * 获取view
     * @return
     */
    public abstract View getInfoWindowView();

    /**
     * 获取marker样式配置
     * @return
     */
    public abstract BaseMarkerOption getOption();


    public StationEntity getStationEntity() {
        return mStationEntity;
    }

    /**
     * 获取图标
     */
    public Bitmap getIcon() {
        if (null == mMapElement) {
            return null;
        }
        Marker marker = (Marker) mMapElement;
        if (null == marker) {
            return null;
        }
        BitmapDescriptor descriptor = marker.getIcon();
        if (null == descriptor) {
            return null;
        }
        Bitmap icon = descriptor.getBitmap();
        return icon;
    }

    /**
     * 设置图标
     *
     * @param newIcon 新图标
     */
    public void setIcon(Bitmap newIcon) {
        if (null == mMapElement) {
            return;
        }
        Marker marker = (Marker) mMapElement;
        Application application = FoundationApplicationListener.getApplication();
        marker.setIcon(application, BitmapDescriptorFactory.fromBitmap(newIcon));
    }

    /**
     * 设置锚点
     */
    public void setAnchor(float anchorU, float anchorV) {
        if (null == mMapElement) {
            return;
        }
        Marker marker = (Marker) mMapElement;
        marker.setAnchor(anchorU, anchorV);
    }

    /**
     * 展示气泡
     */
    public void showInfoWindow() {
        RiderMapView riderMapView = MapViewManager.getInstance().getRiderMapView();
        if (null != mMapElement && null != riderMapView) {
            Marker marker = (Marker) mMapElement;
            if (null == marker) {
                return;
            }
            if (marker.isInfoWindowShown()) {
                return;
            }
            View infoWindowView = getInfoWindowView();
            if (infoWindowView != null) {
                //处理气泡边缘遮挡
                processInfoWindowEdge(infoWindowView, () -> marker.buildInfoWindow(riderMapView.getMap(),
                    FoundationApplicationListener.getApplication())
                    .showInfoWindow(infoWindowView));
            }
        }
    }

    /**
     * 隐藏气泡
     */
    public void hideInfoWindow() {
        if (null != mMapElement) {
            Marker marker = (Marker) mMapElement;
            if (null == marker) {
                return;
            }
            //隐藏视图
            marker.hideInfoWindow();
            //marker.destroyInfoWindow();
        }
    }

    /**
     * 设置marker位置
     */
    public void setPosition(double latitude, double longitude) {
        if (null != mMapElement) {
            Marker marker = (Marker) mMapElement;
            marker.setPosition(new LatLng(latitude, longitude));
        }
    }

    /**
     * 设置marker旋转
     */
    public void setRotation(float rotation) {
        if (null != mMapElement) {
            Marker marker = (Marker) mMapElement;
            marker.setRotation(rotation);
        }
    }

    /**
     * 点击事件
     */
    public void setMarkerClickListener(RiderMarkerClickListener riderMarkerClickListener) {
        if (null == riderMarkerClickListener) {
            return;
        }
        if (null != mMapElement) {
            Marker marker = (Marker) mMapElement;
            marker.setData((OnMarkerClickListener) marker1 -> {
                riderMarkerClickListener.onClick(BaseMarker.this);
                return false;
            });
        }
    }

    /**
     * 处理气泡边缘是否被遮挡
     */
    private void processInfoWindowEdge(View infoWindowView, ProcessInfoWindowEdgeListener listener) {
        RiderMapView riderMapView = MapViewManager.getInstance().getRiderMapView();
        if (null == riderMapView || null == riderMapView.getMap()) {
            return;
        }
        //计算气泡宽高
        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        infoWindowView.measure(widthMeasureSpec, heightMeasureSpec);
        mLogger.debug("view measure with:" + infoWindowView.getMeasuredWidth()
                + " height:" + infoWindowView.getMeasuredHeight());
        int infoHalfWidth = infoWindowView.getMeasuredWidth() / 2;
        int infoHeight = infoWindowView.getMeasuredHeight();
        //获取marker位置
        Marker marker = ((Marker) mMapElement);
        LatLng markerLatLng = marker.getPosition();
        Projection projection = riderMapView.getMap().getProjection();
        PointF markerPosition = projection.toScreenLocation(markerLatLng);
        mLogger.debug("position to Screen:" + markerPosition);
        float markerToLeft = markerPosition.x;
        float markerToTop = markerPosition.y;
        float markerToRight = riderMapView.getWidth() - markerToLeft;
        //获取padding
        int paddingLeft = riderMapView.getMap().getPadding().left;
        int paddingRight = riderMapView.getMap().getPadding().right;
        int paddingTop = riderMapView.getMap().getPadding().top;
        int markerHeight = (int) (marker.getMarkerSize().height * marker.getOptions().getAnchorV());
        float x = 0f, y = 0f;
        //判断是否被遮挡
        if (infoHalfWidth + paddingLeft> markerToLeft) {
            //超出左边
            x = markerToLeft - infoHalfWidth - paddingLeft;
        } else if (infoHalfWidth + paddingRight > markerToRight) {
            //超出右边
            x = infoHalfWidth - markerToRight + paddingRight;
        }
        if (markerToTop - infoHeight - markerHeight < paddingTop) {
            //超出上边
            y = markerToTop - infoHeight - paddingTop - markerHeight;
        }
        mLogger.debug("x:" + x + " ,y:" + y);
        if (0 != x || 0 != y) {
            riderMapView.scrollCamera(x, y, () -> {
                if (null != listener) {
                    listener.onFinish();
                }
            });
        } else {
            if (null != listener) {
                listener.onFinish();
            }
        }
    }

    /**
     * 处理气泡边缘监听器
     */
    private interface ProcessInfoWindowEdgeListener {
        void onFinish();
    }

    /**
     * marker点击事件
     */
    public interface RiderMarkerClickListener {
        void onClick(BaseMarker baseMarker);
    }
}
