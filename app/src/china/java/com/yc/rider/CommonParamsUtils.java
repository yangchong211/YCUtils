package com.didi.rider.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import com.didi.foundation.sdk.application.FoundationApplicationListener;
import com.didi.foundation.sdk.service.LocaleService;
import com.didi.payment.base.cons.PayParam;
import com.didi.rider.BuildConfig;
import com.didi.rider.app.application.RiderApplicationLifeCycle;
import com.didi.rider.app.device.RiderDeviceInfo;
import com.didi.rider.app.environment.Constants;
import com.didi.rider.app.flutter.HttpPlugin;
import com.didi.rider.app.hummer.common.HMGlobalParams;
import com.didi.rider.app.hybird.RiderGlobalJsBridge;
import com.didi.rider.app.hybird.RiderHybridUrlUtil;
import com.didi.rider.app.sdk.payment.ParamsHelper;
import com.didi.rider.app.sdk.payment.RiderPaymentParamsBuilder;
import com.didi.rider.data.user.UserRepo;
import com.didi.rider.net.Clock;
import com.didi.rider.net.RiderRpcManager;
import com.didi.rider.net.io.RiderBasicFormSerializer;
import com.didi.rider.service.location.RiderLocationService;
import com.didi.sdk.security.SecurityUtil;
import com.didi.sdk.util.SystemUtil;
import com.didi.sdk.util.TextUtil;
import com.didi.sofa.utils.SystemUtils;
import com.didichuxing.bigdata.dp.locsdk.DIDILocation;
import com.didichuxing.foundation.util.NetworkUtil;
import com.didichuxing.omega.sdk.init.OmegaSDK;
import com.didichuxing.swarm.launcher.util.SwarmServices;
import com.didichuxing.swarm.toolkit.AuthenticationService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class CommonParamsUtils {

    private CommonParamsUtils(){
        //避免被构造
    }

    /**
     * 通过注解限定类型
     * TYPE_BASIC                   网络请求公参，具体是：{@link RiderBasicFormSerializer}
     * TYPE_HUMMER                  hummer，具体是：{@link HMGlobalParams }
     * TYPE_HYBRID                  hybrid，具体是：{@link RiderGlobalJsBridge}
     * TYPE_FLUTTER                 flutter，具体是：{@link HttpPlugin }
     * TYPE_PAY                     支付sdk，具体是：{@link ParamsHelper }
     * TYPE_APOLLO                  apollo配置，具体是：{@link RiderApplicationLifeCycle }
     * TYPE_SERVER                  客服，具体是：具体是：{@link RiderHybridUrlUtil }
     * TYPE_PAY_QUERY               支付sdk，具体是：{@link RiderPaymentParamsBuilder }
     *
     * 疑惑：支付sdk，添加公参有点混乱
     */
    @SuppressLint(" InterfaceIsType")
    @Retention(RetentionPolicy.SOURCE)
    public  @interface ParamsType {
        int TYPE_BASIC = 1;
        int TYPE_HUMMER = 2;
        int TYPE_HYBRID = 3;
        int TYPE_FLUTTER = 4;
        int TYPE_PAY = 5;
        int TYPE_APOLLO = 6;
        int TYPE_SERVER = 7;
        int TYPE_PAY_QUERY = 8;
    }

    /**
     * 注解限定符，避免外部传入非法类型
     */
    @IntDef({ParamsType.TYPE_BASIC,ParamsType.TYPE_HUMMER,ParamsType.TYPE_HYBRID,
            ParamsType.TYPE_FLUTTER,ParamsType.TYPE_PAY,ParamsType.TYPE_APOLLO,
            ParamsType.TYPE_SERVER,ParamsType.TYPE_PAY_QUERY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Params{}

    @SuppressLint("InnerTypeLast")
    private void testDemo(){
        //不允许这样使用
        //getCommonParamsJson(1);

        //获取json
        JSONObject commonParamsJson = getCommonParamsJson(ParamsType.TYPE_BASIC);

        //获取map
        Map<String, Object> commonParamsMap = getCommonParamsMap(ParamsType.TYPE_BASIC);

        // todo
        // 1。暂时改动公参影响太大，在该类中复制各个添加公参代码到本类，方便统一修改和维护
        // 2。注意key用config配置字段，避免使用params.put("token", token); 尽量使用params.put(Config.TOKEN, token);
    }

    /**
     * 添加公共参数
     * @param type                          类型，限定类型
     * @return                              map集合数据
     */
    @SuppressLint("InnerTypeLast")
    public static JSONObject getCommonParamsJson(@Params int type){
        //公参
        if (type == ParamsType.TYPE_BASIC
                //flutter，具体是：HttpPlugin
                || type == ParamsType.TYPE_FLUTTER
                //hummer，具体是：HMGlobalParams
                || type == ParamsType.TYPE_HUMMER){
            return appendCommonParams(type);
            //hybrid，具体是：RiderGlobalJsBridge
        } else if (type == ParamsType.TYPE_HYBRID){
            return getGlobalJsBridgeParams();
        }
        //避免返回为空对象
        return new JSONObject();
    }

    /**
     * 添加公共参数
     * @param type                          类型
     * @return                              map集合数据
     */
    @SuppressLint({"InnerTypeLast", "ReturnCount"})
    public static Map<String, Object> getCommonParamsMap(@Params int type){
        //客服，具体是：RiderHybridUrlUtil
        if (type == ParamsType.TYPE_SERVER){
            return getParamsForCustomerServiceUrl();
            //支付sdk，具体是：ParamsHelper
        } else if (type == ParamsType.TYPE_PAY){
            return getCommonPayParams();
            //支付sdk，具体是：RiderPaymentParamsBuilder
        } else if (type == ParamsType.TYPE_PAY_QUERY){
            return getHttpQueryParams();
            //apollo配置，具体是：link RiderApplicationLifeCycle
        } else if (type == ParamsType.TYPE_APOLLO){
            return getApolloParams();
        }
        //避免返回为空对象
        return new HashMap<>();
    }

    /**
     * 公共，RiderBasicFormSerializer公参可以直接用这个
     * flutter参数，HttpPlugin公参可以直接用这个
     * post网络rpc请求参数，具体是：HMGlobalParams
     * 获取公共参数
     * @return
     */
    @SuppressLint("InnerTypeLast")
    private static JSONObject appendCommonParams(@Params int type){
        JSONObject json = new JSONObject();
        try {
            final Application app = FoundationApplicationListener.getApplication();
            final AuthenticationService authService = SwarmServices.lookup(AuthenticationService.class);
            if (authService.isAuthenticated()) {
                json.put("token", authService.getToken());
            }
            final DIDILocation location = RiderLocationService.getInstance().getLastKnownLocation();

            json.put("phone", UserRepo.getInstance().getPhone());
            json.put("appVersion", BuildConfig.VERSION_NAME);
            json.put("versionCode", BuildConfig.VERSION_CODE);
            json.put("clientType", Constants.CLIENT_TYPE);
            json.put("bizId", Constants.BUSINESS_ID);
            json.put("uid", UserRepo.getInstance().getUid());
            //android 版本
            json.put("osVersion", Build.VERSION.RELEASE + ":" + Build.VERSION.SDK_INT);
            json.put("osType", Constants.OS_TYPE);
            json.put("deviceType", Build.MANUFACTURER + ":" + Build.MODEL);
            json.put("countyGroupID", UserRepo.getInstance().getCountyGroupID());
            json.put("cityId", UserRepo.getInstance().getCityId());
            json.put("suuid", "");
            json.put("deviceId", RiderDeviceInfo.getDeviceId());
            json.put("omegaId", OmegaSDK.getOmegaId());
            json.put("channel", "");
            json.put("mapType", "");

            json.put("networkType", SystemUtils.getNetworkType(app));
            json.put("lang", Locale.getDefault().getLanguage());
            json.put("timestamp", Clock.currentTimeSeconds());
            json.put("requestId", Clock.currentTimeSeconds());

            String ip = SystemUtils.getIp(app);
            json.put("ip", TextUtils.isEmpty(ip) ? "" : ip);
            json.put("operatorName", SystemUtils.getOperationName(app));
            json.put("wifiName", SystemUtils.getWifiName(app));
            json.put("locale", LocaleService.getInstance().getCurrentLocaleTag());

            json.put("businessType", "r");
            json.put("terminalType", 1);
            json.put("brand", Constants.BRAND);
            if (null != location) {
                json.put("lat", location.getLatitude());
                json.put("lng", location.getLongitude());
            } else {
                json.put("lat", 0);
                json.put("lng", 0);
            }

            if (type == ParamsType.TYPE_FLUTTER){
                if (null != location) {
                    json.put("accuracy", location.getAccuracy());
                } else {
                    json.put("accuracy", Integer.MAX_VALUE);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return json;
    }

    /**
     * hybrid，具体是：RiderGlobalJsBridge
     * @return
     */
    @SuppressLint("InnerTypeLast")
    private static JSONObject getGlobalJsBridgeParams() {
        JSONObject json = new JSONObject();
        DIDILocation location = RiderLocationService.getInstance().getLastKnownLocation();
        try {
            json.put("phone", UserRepo.getInstance().getPhone());
            json.put("token", UserRepo.getInstance().getToken());
            json.put("riderId", UserRepo.getInstance().getUserId());
            json.put("uid", UserRepo.getInstance().getUid());
            json.put("countyGroupId", UserRepo.getInstance().getCountyGroupID());
            json.put("countyId", UserRepo.getInstance().getCountyID());
            json.put("cityId", UserRepo.getInstance().getCityId());
            json.put("deviceId", RiderDeviceInfo.getDeviceId());
            json.put("lang", Locale.getDefault().getLanguage());
            json.put("country", UserRepo.getInstance().getCountry());
            json.put("appVersion", BuildConfig.VERSION_NAME);
            json.put("versionCode", BuildConfig.VERSION_CODE);
            json.put("clientType", Constants.CLIENT_TYPE);
            json.put("bizId", Constants.BUSINESS_ID);
            //android 版本
            json.put("osVersion", Build.VERSION.RELEASE + ":" + Build.VERSION.SDK_INT);
            json.put("osType", Constants.OS_TYPE);
            json.put("deviceType", Build.MANUFACTURER + ":" + Build.MODEL);
            json.put("suuid", "");
            json.put("channel", "");
            json.put("mapType", "");
            json.put("networkType", "");
            json.put("timestamp", Clock.currentTimeSeconds());
            json.put("requestId", Clock.currentTimeSeconds());

            json.put("businessType", "r");
            json.put("terminalType", 1);
            json.put("brand", Constants.BRAND);

            if (null != location) {
                json.put("lat", location.getLatitude());
                json.put("lng", location.getLongitude());
            } else {
                json.put("lat", 0);
                json.put("lng", 0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 具体是支付
     * Sofa API增加的公共参数 <p> 在访问Url时也会增加以下公参,参数详情参考代码中的注释
     * @return 用Map存储的公共参数, key为参数, value为对应的值
     */
    @SuppressLint("InnerTypeLast")
    private static Map<String, Object> getCommonPayParams() {
        Map<String, Object> params = new HashMap<>();
        final Application app = FoundationApplicationListener.getApplication();
        String token = UserRepo.getInstance().getToken();
        if (!TextUtil.isEmpty(token)) {
            params.put(Config.TOKEN, token);
        }
        params.put(Config.BIZ_ID, Constants.BUSINESS_ID);// 业务线id
        params.put(Config.APP_VERSION, SystemUtil.getVersionName(app));// 客户端版本号
        params.put(Config.VERSION_CODE, SystemUtil.getVersionCode());// 客户端的versionCode
        params.put(Config.OS_TYPE_KEY, Config.ANDROID);//// 表示系统类型，Android为2
        params.put(Config.OS_VERSION, Build.VERSION.RELEASE);// 设备的系统版本
        params.put(Config.DEVICE_TYPE, SystemUtil.getModel());     // 手机型号，比如Vivo x60
        params.put(Config.DEVICE_BRAND, TextUtils.isEmpty(android.os.Build.BRAND) ? ""
                : android.os.Build.BRAND);  // 手机品牌，比如Vivo
        params.put(Config.CLIENT_TYPE, Constants.CLIENT_TYPE);// 客户端类型
        params.put(Config.BRAND, Constants.BRAND);// 客户端类型
        params.put(Config.TERMINAL_TYPE, "1");// 端类型信息
        params.put(Config.NETWORK_TYPE, NetworkUtil.getNetworkTypeString(app));// 网络类型,比如3G\4G\wifi

        /**
         * 手机的串号,注意iOS该字段上传的是设备ID(iOS公共增加的),而Android的设备id有专门的函数生成,所以两端这个概念不是一致的
         * 目前sofa是通过系统函数获取的,而公共免密接口中的imei是通过{@link SystemUtil#getIMEI()}函数获取的,两个值不一样
         */
        params.put(Config.SUUID, SecurityUtil.getSUUID());
        params.put(Config.UUID, SecurityUtil.getUUID());
        params.put(Config.DEVICE_ID, RiderDeviceInfo.getDeviceId());
        final DIDILocation location = RiderLocationService.getInstance().getLastKnownLocation();
        if (null != location) {
            params.put(Config.LAT, location.getLatitude());
            params.put(Config.LNG, location.getLongitude());
            params.put(Config.POI_LAT, location.getLatitude());
            params.put(Config.POI_LNG, location.getLongitude());
        }
        params.put(Config.COUNTY_GROUP_ID, UserRepo.getInstance().getCountyGroupID());
        params.put(Config.COUNTY_ID, UserRepo.getInstance().getCountyID());
        params.put(Config.CITY_ID, UserRepo.getInstance().getCityId());
        params.put(Config.CHANNEL, SystemUtil.getChannelId());
        params.put(Config.TIMESTAMP, Clock.currentTimeSeconds());
        params.put(Config.IMEI, SystemUtil.getIMEI());
        String ip = SystemUtils.getIp(app);
        params.put(Config.IP, TextUtils.isEmpty(ip) ? "" : ip);
        params.put(Config.MAP_TYPE, "gmap");
        params.put(Config.LOCATION_TYPE, getLocationType());
        params.put(Config.WIFI_NAME, SystemUtils.getWifiName(app));
        params.put(Config.LOCALE, LocaleService.getInstance().getCurrentLocaleTag());
        params.put(Config.TERMINALID, Config.TERMINAL_ID_VALUE);
        params.put(Config.USE_PAY_WEB_CONTAINER, 1);
        return params;
    }

    /**
     * 支付sdk，具体是：RiderPaymentParamsBuilder
     * @return
     */
    @SuppressLint("InnerTypeLast")
    @NonNull
    private static HashMap<String, Object> getHttpQueryParams() {
        HashMap<String, Object> params = new HashMap<>();
        // Http请求基础参数
        params.put(Config.DEVICE_ID, RiderDeviceInfo.getDeviceId());
        params.put(Config.APP_VERSION, BuildConfig.VERSION_NAME);
        params.put(Config.SUUID, SecurityUtil.getSUUID());
        params.put(Config.DATA_TYPE, 1);
        params.put(Config.TERMINAL_ID, Config.TERMINAL_ID_VALUE);
        params.put(Config.TOKEN, UserRepo.getInstance().getToken());
        params.put(Config.UUID, SecurityUtil.getUUID());
        params.put(Config.LANG, LocaleService.getInstance().getCurrentLocaleTag());
        final DIDILocation location = RiderLocationService.getInstance().getLastKnownLocation();
        if (null != location) {
            params.put(Config.LATITUDE, location.getLatitude());
            params.put(Config.LONGITUDE, location.getLongitude());
        } else {
            params.put(Config.LATITUDE, 0);
            params.put(Config.LONGITUDE, 0);
        }
        params.put(Config.COUNTY_GROUP_ID, UserRepo.getInstance().getCountyGroupID());
        params.put(Config.COUNTY_ID, UserRepo.getInstance().getCountyID());
        params.put(Config.CITY_ID, UserRepo.getInstance().getCityId());
        params.put(Config.LOCATION_COUNTRY, UserRepo.getInstance().getCountry());
        params.put(Config.LOCATION_CITY_ID, UserRepo.getInstance().getCityId());
        return params;
    }

    /**
     * 客服，具体是：RiderHybridUrlUtil
     * 客服入口 url 需要拼接公参
     * <p>
     * 客服入口 PRD：http://wiki.intra.xiaojukeji.com/pages/viewpage.action?pageId=176383624
     * 国际化在线客服页面URL汇总：http://wiki.intra.xiaojukeji.com/pages/viewpage.action?pageId=180464544
     *
     * @return 参数列表
     */
    @SuppressLint("InnerTypeLast")
    @NonNull
    private static Map<String, Object> getParamsForCustomerServiceUrl() {
        Map<String, Object> params = new HashMap<>();
        params.put("utc_offset", "");
        params.put("appversion", BuildConfig.VERSION_NAME);
        params.put("token", UserRepo.getInstance().getToken());
        final DIDILocation lastKnownLocation = RiderLocationService.getInstance().getLastKnownLocation();
        if (lastKnownLocation != null) {
            params.put("lat", String.valueOf(lastKnownLocation.getLatitude()));
            params.put("lng", String.valueOf(lastKnownLocation.getLongitude()));
        } else {
            params.put("lat", "0");
            params.put("lng", "0");
        }
        params.put("countyGroupId", UserRepo.getInstance().getCountyGroupID());
        params.put("countyId", UserRepo.getInstance().getCountyID());
        params.put("city_id", UserRepo.getInstance().getCityId());
        params.put("lang", LocaleService.getInstance().getCurrentLocaleTag());
        params.put("source", "soda_global_d_home");
        params.put("soda_type", "d");
        params.put("app_platform", "5");
        return params;
    }

    /**
     *
     * @return
     */
    @SuppressLint("InnerTypeLast")
    @NonNull
    private static Map<String, Object> getApolloParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("rlab_business_id", "" + Constants.BUSINESS_ID);
        String countryCode = RiderLocationService.getInstance().getCountryCode();
        if (!TextUtils.isEmpty(countryCode)) {
            params.put("rlab_location_country", "" + countryCode);
        }
        //添加区域id，主要是配合运营需求。
        params.put("rlab_county_group_id", UserRepo.getInstance().getCountyGroupID());
        params.put("rlab_county_id", UserRepo.getInstance().getCountyID());
        params.put("brand", Build.BRAND);
        params.put("model", Build.MODEL);
        params.put("channel", String.valueOf(UserRepo.getInstance().getUserChannel()));
        return params;
    }


    /**
     * 统一命名参数
     * 驼峰命名，方便后期统一修改字段和管理字段
     */
    @SuppressLint("HideUtilityClassConstructor")
    private static final class Config {
        /** token */
        static final String TOKEN = "token";
        /** 客户端版本号 */
        static final String APP_VERSION = "appVersion";
        /**客户端的versionCode**/
        static final String VERSION_CODE = "versionCode";
        /**表示系统类型，Android为2**/
        static final String OS_TYPE_KEY = "osType";
        /**系统版本,比如4.4**/
        static final String OS_VERSION = "osVersion";
        /** 滴滴定义的一个标识id,免密会用到 */
        static final String SUUID = "suuid";
        /** 滴滴定义的一个标识id,免密会用到 */
        static final String UUID = "uuid";
        /** 设备唯一id */
        static final String DEVICE_ID = "deviceId";
        /** 端来源类型，区分独立端与嵌入式 */
        static final String CLIENT_TYPE = "clientType";
        /** 手机当前纬度 */
        static final String LAT = "lat";
        /** 手机当前经度 */
        static final String LNG = "lng";
        /** 业务线id */
        static final String BIZ_ID = "bizId";
        /** 手机型号，比如Vivo */
        static final String DEVICE_TYPE = "deviceType";
        /** 网络类型,比如3G\4G\wifi */
        static final String NETWORK_TYPE = "networkType";
        /** POI定位Id */
        static final String POI_ID = "poiId";
        /** POI定位纬度 */
        static final String POI_LAT = "poiLat";
        /** POI定位经度 */
        static final String POI_LNG = "poiLng";
        /** 应用市场渠道 */
        static final String CHANNEL = "channel";
        /** 时间戳 */
        static final String TIMESTAMP = "timestamp";
        /** 手机品牌名称X60s，比如Vivo */
        static final String DEVICE_BRAND = "deviceBrand";
        /** poi返回的displayName字段 */
        static final String POI_DISPLAY_NAME = "poiName";
        /** poi返回的cityId */
        static final String POI_CITY_ID = "poiCityId";
        /** 外部唤起app的渠道 */
        static final String ENTER_CHANNEL = "enterChannel";
        /** 活动id */
        static final String EXT_ACTIVITY_ID = "extActivityId";
        /** 冷启动时的活动id */
        static final String FIRST_ACTIVITY_ID = "firstActivityId";
        /** 冷启动时的渠道id */
        static final String FIRST_CHANNEL_ID = "firstChannelId";
        /** 国际化增加的语言参数 */
        static final String LOCALE = "locale";
        /** 国际化增加的数据类型 */
        static final String DATA_TYPE = "dataType";
        /** 支付渠道号 */
        static final String TERMINALID = "terminalId";
        /** 地图类型 */
        static final String MAP_TYPE = "mapType";
        /** 设备imei号 */
        static final String IMEI = "imei";
        /** ip，与专快统一 */
        static final String IP = "ip";
        /** 定位类型 (GPS/LBS) */
        static final String LOCATION_TYPE = "locationType";
        /** 运营商名称 (中国联通 中国移动 中国电信) */
        static final String OPERATOR_NAME = "operatorName";
        /** linux内核 */
        static final String LINUX_KERNEL = "linuxKernel";
        /** wifi名称 */
        static final String WIFI_NAME = "wifiName";
        /** wifi无线网卡mac地址 */
        static final String WIFI_MAC = "wifiMac";
        /** 端上生成唯一的id给服务端 */
        static final String REQUEST_ID = "requestId";
        /** 内部业务类型 */
        private static final String BUSINESS_TYPE = "businessType";
        /**外卖品牌类型**/
        private static final String BRAND = "brand";
        /**端类型信息**/
        private static final String TERMINAL_TYPE = "terminalType";
        /** 区分App，1-国内乘客端; 7-Global乘客端 */
        public static final String TERMINAL_ID = "terminal_id";
        /**手机当前所在的区域单元id**/
        private static final String COUNTY_GROUP_ID = "countyGroupId";
        /**手机当前所在的区域id**/
        private static final String COUNTY_ID = "countyId";
        /** 手机当前定位维度 */
        public static final String LATITUDE = "lat";
        /** 手机当前定位经度 */
        public static final String LONGITUDE = "lng";
        /** 手机当前定位的城市id */
        public static final String CITY_ID = "city_id";
        /** 手机当前位置的国家码 */
        public static final String LOCATION_COUNTRY = "location_country";
        /** 手机当前定位的城市id */
        public static final String LOCATION_CITY_ID = "location_cityid";
        /** 手机当前语言（使用钱包界面功能时必填） */
        public static final String LANG = "lang";
        /** 1, 收银台内置容器 , 0 不使用收银台内置容器 */
        public static final String USE_PAY_WEB_CONTAINER = "use_pay_web_container";



        /** 代表来源为Android，ios为1，webapp为3 */
        public static final int ANDROID = 2;
        public static final String TERMINAL_ID_VALUE = "300104";
    }

    /**
     * 获取定位类型
     */
    @SuppressLint("InnerTypeLast")
    private static int getLocationType() {
        int locType = -1;
        DIDILocation didiLocation = RiderLocationService.getInstance().getLastKnownLocation();
        if (didiLocation != null) {
            String type = didiLocation.getProvider();
            if (DIDILocation.GPS_PROVIDER.equals(type)) {
                locType = 1;
            } else if (DIDILocation.WIFI_PROVIDER.equals(type)) {
                locType = 2;
            } else if (DIDILocation.CELL_PROVIDER.equals(type)) {
                locType = 3;
            } else {
                locType = -1;
            }
        }
        return locType;
    }

}
