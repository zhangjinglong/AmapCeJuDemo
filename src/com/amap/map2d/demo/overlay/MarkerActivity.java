package com.amap.map2d.demo.overlay;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.a.am;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.InfoWindowAdapter;
import com.amap.api.maps2d.AMap.OnInfoWindowClickListener;
import com.amap.api.maps2d.AMap.OnMapLoadedListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.AMap.OnMarkerDragListener;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.Projection;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.Polyline;
import com.amap.api.maps2d.model.PolylineOptions;
import com.amap.map2d.demo.util.AMapUtil;
import com.amap.map2d.demo.util.Constants;
import com.amap.map2d.demo.util.ToastUtil;
import com.amap.map2d.demo.R;



/**
 * AMapV1地图中简单介绍一些Marker的用法.
 */
public class MarkerActivity extends Activity implements OnMapLoadedListener,
		OnClickListener, InfoWindowAdapter {
	private MarkerOptions markerOption;
	private AMap aMap;
	private MapView mapView;
	////////////////////////////////////////
	private List<LatLng> points;//所有点击的点的坐标
	private List<Marker> markers;
	private List<Polyline> lines;
	private long totalJuli=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.marker_activity);
		points=new ArrayList<LatLng>();
		markers=new ArrayList<Marker>();
		lines=new ArrayList<Polyline>();
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState); // 此方法必须重写
		init();
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		Button clearMap = (Button) findViewById(R.id.clearMap);
		clearMap.setOnClickListener(this);
		Button resetMap = (Button) findViewById(R.id.resumeMap);
		resetMap.setOnClickListener(this);
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
	}

	private void setUpMap() {
		aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
		aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
		//添加地图点击事件
		aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
			
			@Override
			public void onMapClick(LatLng arg0) {
				// TODO Auto-generated method stub
//				ToastUtil.show(MarkerActivity.this, "坐标是:" + "latitude:"
//				+arg0.latitude+"\nlongitude:"+arg0.longitude);
				points.add(arg0);
				drawMarker(points);
				
			}
		} );
	}
	public void drawMarker(List<LatLng> points){
			markerOption = new MarkerOptions();
			markerOption.position(points.get(points.size()-1));
			markerOption.draggable(true);
			markerOption.icon(BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
			if(points.size()>=2){
				juli(points);
				markerOption.title(getFormatJuli(totalJuli));
			}else{
				markerOption.title("0m");
			}
			Marker marker = aMap.addMarker(markerOption);
			marker.showInfoWindow();
			markers.add(marker);
	}
	//对距离进行初始化
		public String getFormatJuli(long juli){
			DecimalFormat df = new DecimalFormat(".#");
			int allJuli1 = (int) Math.round(juli);// 清除小数部分
			if (allJuli1 >= 1000) {
				return df.format(allJuli1 / 1000.00) + "km";
			} else {
				return allJuli1 + "m";
			}
		}
		public void juli(List<LatLng> points){
			if(points.size()>=2){
			totalJuli+=AMapUtils.calculateLineDistance(points.get(points.size()-2), points.get(points.size()-1));
			drawline(points);
			}
			}
	public void drawline(List<LatLng> points){
		Polyline polyline=aMap.addPolyline((new PolylineOptions()).add(
				points.get(points.size()-2), points.get(points.size()-1)).color(
				Color.BLUE));
		polyline.setWidth(6);
		lines.add(polyline);
	}
	
	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	/**
	 * 监听amap地图加载成功事件回调
	 */
	@Override
	public void onMapLoaded() {
		// 设置所有maker显示在当前可视区域地图中
		LatLngBounds bounds = new LatLngBounds.Builder()
				.include(Constants.XIAN).include(Constants.CHENGDU)
				.include(Constants.ZHENGZHOU).build();
		aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/**
		 * 清空地图上所有已经标注的marker
		 */
		case R.id.clearMap:
			if (aMap != null) {
				aMap.clear();
				points.clear();
				totalJuli=0;
			}
			break;
		/**
		 * 重新标注所有的marker
		 */
		case R.id.resumeMap:
			if(points.size()>1){
				totalJuli-=AMapUtils.calculateLineDistance(points.get(points.size()-2), points.get(points.size()-1));
				points.remove(points.size()-1);
				Toast.makeText(getApplicationContext(), "markers:"+(markers.size()-1), 5000).show();
				Toast.makeText(getApplicationContext(), "lines:"+(lines.size()-1), 5000).show();
				markers.get(markers.size()-1).remove();//删除marker
				markers.remove(markers.size()-1);
				markers.get(markers.size()-1).showInfoWindow();
				lines.get(lines.size()-1).remove();//删除折线
				lines.remove(lines.size()-1);
			}else{
				if (aMap != null) {
					aMap.clear();
					points.clear();
					totalJuli=0;
				}
			}
			mapView.invalidate();
			break;
	
		default:
			break;
		}
	}

	@Override
	public View getInfoContents(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
