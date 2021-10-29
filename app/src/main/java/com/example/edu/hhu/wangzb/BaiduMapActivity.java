package com.example.edu.hhu.wangzb;

import androidx.appcompat.app.AppCompatActivity;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.example.edu.hhu.wangzb.R;

public class BaiduMapActivity extends AppCompatActivity implements OnMenuItemClickListener{

	private Context context;
	private MapView mapView;
	private BaiduMap baiduMap;
	private RadioGroup maptypeRadioGroup;
	private CheckBox trafficCheckBox;
	private Handler handler;
//尝试新功能
	private EditText longitudeAEditText;
	private EditText latitudeAEditText;
	private EditText longitudeBEditText;
	private EditText latitudeBEditText;
	protected String longitudeString;
	protected String latitudeString;
	private static double Earth_R = 6378.137;
	private static double rad(double d){
		return d*Math.PI/180.0;
	}

	// 负责取数据
	private SharedPreferences sp;
	// 负责保存数据
	private SharedPreferences.Editor editor;
	// 数据的分隔符
	private final String FGF = ",_,";
	//

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.map);
		init();
		setListener();
	}

	private void init() {
		context = BaiduMapActivity.this;
		sp = context.getSharedPreferences("baidumap", Context.MODE_PRIVATE);

		mapView = (MapView) findViewById(R.id.mapView);
		//mapView = (MapView) findViewById(R.id.mapView);
		//取消放大缩小键
		mapView.showZoomControls(false);
		baiduMap = mapView.getMap();
		toNewAddress(baiduMap,118.78,32.07);
		maptypeRadioGroup = (RadioGroup) findViewById(R.id.maptypeRadioGroup);
		trafficCheckBox = (CheckBox) findViewById(R.id.trafficCheckBox);
		handler = new Handler(){
			@Override
			public void handleMessage(final Message msg) {
				switch(msg.what){
				case 1:
					showToast(context, msg.obj.toString());
					break;
				}
			}
		};

		sp = getSharedPreferences("baidumap", Context.MODE_PRIVATE);
		editor = sp.edit();

	}

	private void setListener() {
		maptypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId==R.id.normalRadioButton){
					baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
					Toast.makeText(context, "普通地图", Toast.LENGTH_LONG).show();
				}
				if(checkedId==R.id.satelliteRadioButton){
					baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
					Toast.makeText(context, "卫星地图", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		trafficCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean flag) {
				if(flag){
					//开启交通图
					baiduMap.setTrafficEnabled(true);
				}else{
					//关闭交通图
					baiduMap.setTrafficEnabled(false);
				}
			}
		});
	}
	
	//添加菜单
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//groupId,itemId,orderId,名称
		menu.add(1,1,1,"经纬度定位").setOnMenuItemClickListener(this);
		menu.add(2,2,2,"城市定位").setOnMenuItemClickListener(this);
		menu.add(3,3,3,"公里数计算").setOnMenuItemClickListener(this);
		menu.add(4,4,4,"当前用户信息").setOnMenuItemClickListener(this);
		menu.add(5,5,5,"清除屏幕").setOnMenuItemClickListener(this);
		menu.add(6,6,6,"退出").setOnMenuItemClickListener(this);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuItemClick(MenuItem item) {
		int itemId = item.getItemId();
		AlertDialog.Builder builder = new AlertDialog.Builder(BaiduMapActivity.this);
		builder.setIcon(R.drawable.ic_launcher);
		switch(itemId){
		case 1:
			final View locationView = View.inflate(BaiduMapActivity.this,R.layout.dialog_location, null);
			builder.setTitle("经纬度定位");
			builder.setView(locationView);
			final EditText longitudeEditText = (EditText) locationView.findViewById(R.id.longitudeEditText);
			final EditText latitudeEditText = (EditText) locationView.findViewById(R.id.latitudeEditText);
			builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					String longitudeString = longitudeEditText.getText().toString().trim();
					if(longitudeString.trim().length()==0){
						showToast(context, "经度不能为空!");
						return;
					}
					String latitudeString = latitudeEditText.getText().toString().trim();
					if(latitudeString.trim().length()==0){
						showToast(context, "纬度不能为空!");
						return;
					}
					double longitude = Double.parseDouble(longitudeString);
					if(longitude<-180 || longitude>180){
						showToast(context, "经度的范围在-180~180之间!");
						return;
					}
					double latitude = Double.parseDouble(latitudeString);
					if(latitude<-90 || latitude>90){
						showToast(context, "纬度的范围在-90~90之间!");
						return;
					}
					// 定义Maker坐标点
					pointOverlay(baiduMap,longitude,latitude);
					// 将地图移动过去
					toNewAddress(baiduMap,longitude,latitude);
				}
			});
			builder.setNegativeButton("取消", null);
			builder.create();
			builder.show();
			break;
		case 2:
			final View citydwView = View.inflate(BaiduMapActivity.this,R.layout.dingwei, null);
			builder.setTitle("城市定位");
			builder.setView(citydwView);
			final EditText cityEditText = (EditText) citydwView.findViewById(R.id.cityEditText);
			builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					String cityString = cityEditText.getText().toString().trim();
					double j=0,w=0;
					if (cityString.equals("北京") || cityString.equals("beijing")) {
						j = 116.41667; w = 39.91667;
					}
					if (cityString.equals("南京") || cityString.equals("nanjing")) {
						j = 118.78333; w = 32.05000;
					}
					if (cityString.equals("商丘") || cityString.equals("shangqiu")) {
						j = 115.38000; w = 34.26000;
					}
					// 定义Maker坐标点
					pointOverlay(baiduMap,j,w);
					// 将地图移动过去
					toNewAddress(baiduMap,j,w);
				}
			});
			builder.setNegativeButton("取消", null);
			builder.create();
			builder.show();
			break;
		case 3:
			final View distanceView = View.inflate(BaiduMapActivity.this,R.layout.distance, null);
			builder.setTitle("公里数计算");
			builder.setView(distanceView);
			final EditText longitudeAEditText = (EditText) distanceView.findViewById(R.id.longitudeAEditText);
			final EditText latitudeAEditText = (EditText) distanceView.findViewById(R.id.latitudeAEditText);
			final EditText longitudeBEditText = (EditText) distanceView.findViewById(R.id.longitudeBEditText);
			final EditText latitudeBEditText = (EditText) distanceView.findViewById(R.id.latitudeBEditText);
			builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					String longitudeA = longitudeAEditText.getText().toString().trim();
					if(longitudeA.trim().length()==0){
						showToast(context, "A点经度不能为空!");
						return;
					}
					String latitudeA = latitudeAEditText.getText().toString().trim();
					if(latitudeA.trim().length()==0){
						showToast(context, "A点纬度不能为空!");
						return;
					}
					String longitudeB = longitudeBEditText.getText().toString().trim();
					if(longitudeB.trim().length()==0){
						showToast(context, "B点经度不能为空!");
						return;
					}
					String latitudeB = latitudeBEditText.getText().toString().trim();
					if(latitudeB.trim().length()==0){
						showToast(context, "B点纬度不能为空!");
						return;
					}
					double lonA = Double.parseDouble(longitudeA);
					double lonB = Double.parseDouble(longitudeB);
					double latA = Double.parseDouble(latitudeA);
					double latB = Double.parseDouble(latitudeB);
					double radLatA = rad(latA);
					double radLatB = rad(latB);
					double a = radLatA-radLatB;
					double b = rad(lonA)-rad(lonB);
					double d = 2*Math.asin(Math.sqrt(Math.pow(Math.sin(a/2), 2)
							+Math.cos(radLatA)*Math.cos(radLatB)*Math.pow(Math.sin(b/2), 2)));
					d = d*Earth_R;
					d = Math.round(d*100d)/100d;
					Toast.makeText(BaiduMapActivity.this, "两地的距离为："+d+"km", Toast.LENGTH_LONG).show();
					return;
				}
			});
			builder.setNegativeButton("取消", null);
			builder.create();
			builder.show();
			break;
		case 4:
			final View mesView = View.inflate(BaiduMapActivity.this,R.layout.personal_info, null);
			builder.setTitle("当前用户信息");
			builder.setView(mesView);

			final EditText nameEditText = (EditText) mesView.findViewById(R.id.nameEditText);
			final EditText accountEditText = (EditText) mesView.findViewById(R.id.accountEditText);
			final EditText phoneEditText = (EditText) mesView.findViewById(R.id.phoneEditText);
			final EditText mailEditText = (EditText) mesView.findViewById(R.id.emailEditText);
			final EditText birthdayEditText = (EditText) mesView.findViewById(R.id.birthdayEditText);
			final EditText birthplaceEditText = (EditText) mesView.findViewById(R.id.birthplaceEditText);
			final EditText favourEditText = (EditText) mesView.findViewById(R.id.favourEditText);
			final EditText introEditText = (EditText) mesView.findViewById(R.id.introductionEditText);

			String currentAccount = sp.getString("CurrentAccount", null);
			String info = sp.getString(currentAccount, null);

			accountEditText.setText(currentAccount);
			String[] infoArray = info.split(FGF);
			nameEditText.setText(infoArray[1]);
			phoneEditText.setText(infoArray[3]);
			mailEditText.setText(infoArray[4]);
			birthdayEditText.setText(infoArray[5]);
			birthplaceEditText.setText(infoArray[6]);
			favourEditText.setText(infoArray[7]);
			introEditText.setText(infoArray[8]);
//			nameEditText.setText(info.split("_")[1]);
//			phoneEditText.setText(info.split("_")[2]);
//			mailEditText.setText(info.split("_")[3]);
//			birthdayEditText.setText(info.split("_")[4]);
//			birthplaceEditText.setText(info.split("_")[5]);
//			favourEditText.setText(info.split("_")[6]);
//			introEditText.setText(info.split("_")[7]);

			builder.setNegativeButton("确定", null);
			builder.create();
			builder.show();
			break;
		case 5:
			baiduMap.clear();
			break;
		case 6:
			exist();
			
			break;
		default:
			break;
		}
		return true;
	}
	
	private void showToast(Context context,String content){
		Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 将地图的中兴点移动到指定点
	 * @param baiduMap 百度地图对象
	 * @param longitude 经度
	 * @param latitude 纬度
	 */
	private void toNewAddress(BaiduMap baiduMap,double longitude,double latitude){
		//设定中心点坐标
		LatLng cenpt = new LatLng(latitude,longitude);
		//定义地图状态
		MapStatus mapStatus = new MapStatus.Builder().target(cenpt).build();
		//定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
		MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
		//改变地图状态
		baiduMap.setMapStatus(mapStatusUpdate);
	}
	
	/**
	 * 绘制点标记，并将新的点标记添加到地图中
	 * @param baiduMap 百度地图对象
	 * @param longitude 经度
	 * @param latitude 纬度
	 */
	private void pointOverlay(BaiduMap baiduMap,double longitude,double latitude){
		//定义Maker坐标点
		LatLng point = new LatLng(latitude,longitude);
		//构建Marker图标
		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.mark);
		//构建MarkerOption，用于在地图上添加Marker
		OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
		//在地图上添加Marker，并显示
		baiduMap.addOverlay(option);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 是否触发按键为back键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exist();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
	
	//退出
	private void exist(){
		AlertDialog.Builder builder = new AlertDialog.Builder(BaiduMapActivity.this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("退出提醒");
		builder.setMessage("你确认退出吗？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				BaiduMapActivity.this.finish();
			}
		});
		builder.setNegativeButton("取消", null);
		builder.create();
		builder.show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

}
