package reportdeviceinfo.lenovo.cn.share;

import android.app.Application;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;

import com.avos.avoscloud.AVOSCloud;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseUI;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.PauseOnScrollListener;
import cn.finalteam.galleryfinal.ThemeConfig;
import reportdeviceinfo.lenovo.cn.share.util.GlideImageLoader;
import reportdeviceinfo.lenovo.cn.share.util.LocationUtils;

/**
 * https://leancloud.cn/docs/sdk_setup-android.html#hash7247859
 */
public class MyLeanCloudApp extends Application {
    //友盟appkey
    private String appkey = "5bff52ddb465f51c7600014d";
    private String channel = "Wandoujia";
    public static double latitude, longitude;//纬度。经度

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this, "oWeMqwdpcfalQiOU8geMnUt2-gzGzoHsz", "bsqsdNoCy7dIs0HpTt4cm1jN");
        // 放在 SDK 初始化语句 AVOSCloud.initialize() 后面，只需要调用一次即可
        AVOSCloud.setDebugLogEnabled(true);


        //        https://github.com/pengjianbo/GalleryFinal/blob/master/app/src/main/java/cn/finalteam/galleryfinal/sample/MainActivity.java
        //设置主题
        ThemeConfig theme = new ThemeConfig.Builder()
                .setTitleBarBgColor(getResources().getColor(R.color.colorPrimary))
                .setTitleBarTextColor(Color.WHITE)
                .setTitleBarIconColor(Color.WHITE)
                .setFabNornalColor(getResources().getColor(R.color.colorPrimary))
                .setFabPressedColor(Color.BLUE)
                .setCheckNornalColor(Color.WHITE)
                .setCheckSelectedColor(getResources().getColor(R.color.colorPrimary))
//                .setIconBack(R.mipmap.ic_action_previous_item)
//                .setIconRotate(R.mipmap.ic_action_repeat)
//                .setIconCrop(R.mipmap.ic_action_crop)
//                .setIconCamera(R.mipmap.ic_action_camera)
                .build();
        //配置功能
        FunctionConfig functionConfig = new FunctionConfig.Builder()
                .setEnableCamera(true)
                .setEnableEdit(true)
                .setEnableCrop(true)
                .setEnableRotate(true)
                .setCropSquare(true)
                .setEnablePreview(true)
                .build();
        CoreConfig coreConfig = new CoreConfig.Builder(this, new GlideImageLoader(), theme)
                .setFunctionConfig(functionConfig)
                .setPauseOnScrollListener(new PauseOnScrollListener(false, true) {
                    @Override
                    public void resume() {

                    }

                    @Override
                    public void pause() {

                    }
                })
                .build();
        GalleryFinal.init(coreConfig);
        //初始化环信
        initHuanXin();

        initUM();

        initLocation();
    }

    private void initLocation() {
        Location location = LocationUtils.getInstance(this).showLocation();
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            String address = "纬度：" + location.getLatitude() + "经度：" + location.getLongitude();
            Log.d("LocationUtils", address);
        }
    }

    /**
     * 友盟第三方登录
     */
    private void initUM() {
        // <!-- 微信平台 --> appid appsecret
        UMConfigure.setLogEnabled(true);
        UMConfigure.init(this, appkey, channel, UMConfigure.DEVICE_TYPE_PHONE, "");
        PlatformConfig.setWeixin("wxdc1e388c3822c80b", "3baf1193c85774b3fd9d18447d76cab0");
        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
    }

    private void initHuanXin() {
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        // 是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载，如果设为 false，需要开发者自己处理附件消息的上传和下载
        options.setAutoTransferMessageAttachments(true);
        // 是否自动下载附件类消息的缩略图等，默认为 true 这里和上边这个参数相关联
        options.setAutoDownloadThumbnail(true);
        //初始化
        EMClient.getInstance().init(this, options);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);

        EaseUI.getInstance().init(this, options);
    }


}