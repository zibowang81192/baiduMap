package com.example.edu.hhu.wangzb;//这里要修改成自己的包名

import android.content.Context;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;

import android.app.Application;

public class BaiduMapApplication extends Application {
	android.content.Context context = this;

	@Override
	public void onCreate() {
		super.onCreate();
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		SDKInitializer.initialize(context);
		// 自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
		// 包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
		SDKInitializer.setCoordType(CoordType.BD09LL);
	}

}
