package com.keju.maomao.activity.mapview;

import java.util.StringTokenizer;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.keju.maomao.CommonApplication;
import com.keju.maomao.Constants;
import com.keju.maomao.R;
import com.keju.maomao.activity.base.BaseActivity;
import com.keju.maomao.bean.BarBean;
import com.umeng.analytics.MobclickAgent;

/**
 * 某个地点在地图位置
 * @author Zhoujun
 * @version 创建时间：2013-6-19 下午1:35:15
 */
public class LocationMapActivity extends BaseActivity implements OnClickListener {
	private ImageButton ibLeft;
	private Button btnRight;
	private TextView tvTitle;
	
	private static MapView bMapView;// 视图
	private OverItemT overItemT;
	
	private static View mPopView = null;// 弹出提示框
	private TextView tvName,tvAddress;
	
	private BarBean bean;
	private OverlayItem overlayItem;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MobclickAgent.onError(this);
		setContentView(R.layout.location_list_in_map);
		bean = (BarBean) getIntent().getSerializableExtra(Constants.EXTRA_DATA);
		GeoPoint p1 = new GeoPoint((int) (Double.parseDouble(bean
					.getLatitude()) * 1E6), (int) (Double.parseDouble(bean
					.getLongitude()) * 1E6));
		overlayItem = new OverlayItem(p1, null, bean.getBar_Name());
		findView();
		fillData();
		((CommonApplication) getApplication()).addActivity(this);
	}
	@Override
	public void onDestroy() {
		bMapView.destroy();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		bMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		bMapView.onResume();
		super.onResume();
	}
	
	private void findView(){
		ibLeft = (ImageButton) findViewById(R.id.ibLeft);
		ibLeft.setOnClickListener(this);
		ibLeft.setImageResource(R.drawable.ic_btn_left);
		btnRight = (Button) findViewById(R.id.btnRight);
		btnRight.setBackgroundResource(R.drawable.bg_btn_collection);
		btnRight.setOnClickListener(this);
		btnRight.setText("路线");
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText("地图");
		
		bMapView = (MapView) findViewById(R.id.bmapView);
		
		mPopView = getLayoutInflater().inflate(R.layout.popu_org_location, null);
		tvName = (TextView) mPopView.findViewById(R.id.tvName);
		tvAddress = (TextView) mPopView.findViewById(R.id.tvAddress);
	}
	private void fillData(){
		mapView();
		bMapView.addView(mPopView, new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, null,
				MapView.LayoutParams.TOP_LEFT));
		mPopView.setVisibility(View.GONE);
		
		Drawable marker = getResources().getDrawable(R.drawable.ic_org_location);
		overItemT = new OverItemT(marker,bMapView);
		overItemT.addItem(overlayItem);
		bMapView.getOverlays().add(overItemT);
		bMapView.refresh();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			break;
		case R.id.btnRight:
			Toast.makeText(this, "请安装百度地图使用该功能", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}
	/**
	 * 显示百度地图
	 * 
	 */
	public void mapView() {
		bMapView.setBuiltInZoomControls(true);// 设置启动内置的缩放控件
		MapController mMapController = bMapView.getController();// 得到bMapView的控制权，
		// 给定一个经纬度构造一个GeoPoint ，单位是微度（度*1E6）
		GeoPoint point = new GeoPoint((int) (31.232839 * 1E6),
				(int) (121.4748 * 1E6));
		mMapController.setCenter(point);// 设置地图中心点
		// mMapController.setCenter(point);// 设置地图中心点
		mMapController.setZoom(12);// 设置地图zoom级别
		// 设置在缩放动画过程中也显示overlay,默认为不绘制
		bMapView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mPopView.setVisibility(View.GONE);
				return false;
			}
		});

	}
	/**
	 * 显示园区位置，添加logo图片
	 * 
	 * @author Administrator
	 * 
	 */
	class OverItemT extends ItemizedOverlay<OverlayItem> {

		public OverItemT(Drawable mark, MapView mapView) {
			super(mark, mapView);
		}


		@Override
		// 处理当点击事件
		protected boolean onTap(final int i) {
			// 更新气泡位置,并使之显示
			GeoPoint pt = overlayItem.getPoint();
			bMapView.updateViewLayout(mPopView, new MapView.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, pt,
					MapView.LayoutParams.BOTTOM_CENTER));
			mPopView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					finish();
				}
			});
			mPopView.setVisibility(View.VISIBLE);
			tvName.setText(bean.getBar_Name());
			String address1 = null;
			String address = bean.getBar_Address();
			StringTokenizer token = new StringTokenizer(address, "$");
			String[] add = new String[3];
			int i1 = 0;
			while (token.hasMoreTokens()) {
				add[i1] = token.nextToken();
				i1++;
				address1 = add[0];
			}
			tvAddress.setText(address1+bean.getBarStreet());
			
			return true;
		}

		public boolean onTap(GeoPoint arg0, MapView arg1) {
			// 消去弹出的气泡
			mPopView.setVisibility(View.GONE);
			return super.onTap(arg0, arg1);
		}
	}
}
