package com.example.hp.map;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    private static final String TAG ="MainActivity";
    private TextView textView;
    private Button btn_daohang;

    //声明mlocationClient对象
    public AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initPermissions();
        locationTest();
        initListener();
    }

    private void initListener() {
        btn_daohang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.VIEW",
                        android.net.Uri.parse("androidamap://showTraffic?sourceApplication=softname&poiid=BGVIS1&lat=36.2&lon=116.1&level=10&dev=0"));
                intent.setPackage("com.autonavi.minimap");
                startActivity(intent);

            }
        });

    }


    private void initViews() {
        textView = findViewById(R.id.show_getAddress);
        btn_daohang = findViewById(R.id.daohang);
    }


    private void initPermissions() {
        final RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .requestEach(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.CHANGE_WIFI_STATE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN
                ).subscribe(new Consumer<Permission>() {
            @Override
            public void accept(Permission permission) throws Exception {
                if (permission.granted){
                    //所有权限申请通过
                    Log.i(TAG,"ALL Permissions Granted");
                }else if (permission.shouldShowRequestPermissionRationale){
                    //某个权限被拒绝了，没有勾选不再询问
                    Log.i(TAG,permission.name +"-DENY Need ask again");
                }else {
                    //某个权限被拒绝了，勾选了不在询问
                    Log.i(TAG,permission.name + "-DENY Don't ask again");
                }
            }
        }).isDisposed();

    }
    private void locationTest() {
        mlocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(final AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        //定位成功回调信息，设置相关消息
                        aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                        aMapLocation.getLatitude();//获取纬度
                        aMapLocation.getLongitude();//获取经度
                        aMapLocation.getAccuracy();//获取精度信息
                        aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                        aMapLocation.getCountry();//国家信息
                        aMapLocation.getProvince();//省信息
                        aMapLocation.getCity();//城市信息
                        aMapLocation.getDistrict();//城区信息
                        aMapLocation.getStreet();//街道信息
                        aMapLocation.getStreetNum();//街道门牌号信息
                        aMapLocation.getCityCode();//城市编码
                        aMapLocation.getAdCode();//地区编码
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date(aMapLocation.getTime());
                        df.format(date);//定位时间
                        Log.i(TAG, "获取纬度:" + aMapLocation.getLatitude());
                        Log.i(TAG, "获取经度:" + aMapLocation.getLongitude());
                        Log.i(TAG, "获取精度信息:" + aMapLocation.getAccuracy());
                        Log.i(TAG, "地址:" + aMapLocation.getAddress());
                        Log.i(TAG, "国家信息:" + aMapLocation.getCountry());
                        Log.i(TAG, "省信息:" + aMapLocation.getProvince());
                        Log.i(TAG, "城市信息:" + aMapLocation.getCity());
                        Log.i(TAG, "城区信息:" + aMapLocation.getDistrict());
                        Log.i(TAG, "街道信息:" + aMapLocation.getStreet());
                        Log.i(TAG, "街道门牌号信息:" + aMapLocation.getStreetNum());
                        Log.i(TAG, "城市编码:" + aMapLocation.getCityCode());
                        Log.i(TAG, "地区编码:" + aMapLocation.getAdCode());

                        textView.post(new Runnable() {
                            @Override
                            public void run() {
                                String text = "\n地址：" + aMapLocation.getAddress() + "\n街道门牌号信息：" + aMapLocation.getStreetNum();
                                textView.setText(text);
                            }
                        });

                    } else {
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError", "location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                    }
                }

            }
        });
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为6000ms
        mLocationOption.setInterval(30000);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();
    }

}
