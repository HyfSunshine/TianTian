package com.tgzzb.cdc.imagepicker;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.tgzzb.cdc.DriverActivity;
import com.tgzzb.cdc.DriverAddInfoActivity;
import com.tgzzb.cdc.MrAddInfoActivity;
import com.tgzzb.cdc.MrCheckAllPicActivity;
import com.tgzzb.cdc.MyApp;
import com.tgzzb.cdc.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 这个是用于进行图片浏览时的界面
 *
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日 下午11:47:53
 */
public class GalleryActivity extends Activity {
	private ArrayList<ImageItem> currentBitmapList;
	private ArrayList<String> imgUrls;

	private Intent intent;
	// 返回按钮
	private Button back_bt;
	// 发送按钮
	private Button send_bt;
	// 删除按钮
	private Button del_bt;
	// 顶部显示预览图片位置的textview
	private TextView positionTextView;
	// 获取前一个activity传过来的position
	private int position;
	// 当前的位置
	private int location = 0;

	private ArrayList<View> listViews = null;
	private ViewPagerFixed pager;
	private MyPageAdapter adapter;

	public List<Bitmap> bmp = new ArrayList<Bitmap>();
	public List<String> drr = new ArrayList<String>();
	public List<String> del = new ArrayList<String>();

	RelativeLayout photo_relativeLayout;

	private TextView tv_picnum;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(Res.getLayoutID("plugin_camera_gallery"));// 切屏到主界面
		// PublicWay.activityList.add(this);
		back_bt = (Button) findViewById(Res.getWidgetID("gallery_back"));
		send_bt = (Button) findViewById(Res.getWidgetID("send_button"));
		del_bt = (Button) findViewById(Res.getWidgetID("gallery_del"));
		back_bt.setOnClickListener(new BackListener());
		send_bt.setOnClickListener(new GallerySendListener());
		del_bt.setOnClickListener(new DelListener());
		tv_picnum = (TextView) findViewById(R.id.tv_picnum);

		intent = getIntent();
		String fromAddress = intent.getStringExtra("fromAddress");
		imgUrls = intent.getStringArrayListExtra("imgUrls");
		position = Integer.parseInt(intent.getStringExtra("position"));
		if(imgUrls==null){
			imgUrls = new  ArrayList<String>();
		}
		if (TextUtils.equals(fromAddress, "DriverActivity")) {
			currentBitmapList = DriverActivity.qsd_Datas;
		} else if (TextUtils.equals(fromAddress, "DriverAddInfoActivity1")) {
			currentBitmapList = DriverAddInfoActivity.qsdBitmaps;

		} else if (TextUtils.equals(fromAddress, "DriverAddInfoActivity2")) {
			currentBitmapList = DriverAddInfoActivity.psBitmaps;

		} else if (TextUtils.equals(fromAddress, "MrCheckAllPicActivity")) {
			currentBitmapList = MrCheckAllPicActivity.checkBitmap;
			send_bt.setVisibility(View.GONE);
			del_bt.setVisibility(View.GONE);
			tv_picnum.setVisibility(View.VISIBLE);

		} else if (TextUtils.equals(fromAddress, "MrAddInfoActivity1")) {
			currentBitmapList = MrAddInfoActivity.SelectBitmap;

		} else if (TextUtils.equals(fromAddress, "MrAddInfoActivity2")) {
			currentBitmapList = MrAddInfoActivity.SelectBitmap1;

		} else if (TextUtils.equals(fromAddress, "MrAddInfoActivity3")) {
			currentBitmapList = MrAddInfoActivity.SelectBitmap2;
		}else if (TextUtils.equals(fromAddress, "DriverCheckPicActivity")){
			currentBitmapList = new ArrayList<ImageItem>();
			send_bt.setVisibility(View.GONE);
			del_bt.setVisibility(View.GONE);
			tv_picnum.setVisibility(View.VISIBLE);
		}

		
		isShowOkBt();
		// 为发送按钮设置文字
		pager = (ViewPagerFixed) findViewById(Res.getWidgetID("gallery01"));
		pager.setOnPageChangeListener(pageChangeListener);
		for (int i = 0; i < currentBitmapList.size(); i++) {
			initListViews(currentBitmapList.get(i).getBitmap());
		}
		for(int i =0;i<imgUrls.size();i++){
			initListViews(i);
		}
		adapter = new MyPageAdapter(listViews);
		pager.setAdapter(adapter);
		pager.setPageMargin((int) getResources().getDimensionPixelOffset(Res.getDimenID("ui_10_dip")));
		int id = intent.getIntExtra("ID", 0);
		pager.setCurrentItem(id);
		if(imgUrls.size()!=0){
			tv_picnum.setText(id + 1 + "/" + imgUrls.size());
		}else{
			tv_picnum.setText(id + 1 + "/" + currentBitmapList.size());
		}
	}

	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		public void onPageSelected(int arg0) {
			location = arg0;
			if(imgUrls.size()!=0){
				tv_picnum.setText(arg0 + 1 + "/" + imgUrls.size());
				
			}else{
				tv_picnum.setText(arg0 + 1 + "/" + currentBitmapList.size());
			}
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		public void onPageScrollStateChanged(int arg0) {

		}
	};

	private void initListViews(Bitmap bm) {
		if (listViews == null) {
			listViews = new ArrayList<View>();
		}
		PhotoView img = new PhotoView(this);
		img.setBackgroundColor(0xff000000);
		img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		if(imgUrls!=null&&imgUrls.size()!=0){
			Glide.with(MyApp.getInstance()).load(imgUrls.get(0)).into(img);
		}else{
			img.setImageBitmap(bm);
		}
		// ImageView img = new ImageView(this);
		// img.setScaleType(ImageView.ScaleType.FIT_XY);
		// img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
		// LayoutParams.MATCH_PARENT));
		listViews.add(img);
	}
	private void initListViews(int position) {
		if (listViews == null) {
			listViews = new ArrayList<View>();
		}
		PhotoView img = new PhotoView(this);
		img.setBackgroundColor(0xff000000);
		img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		if(imgUrls!=null&&imgUrls.size()!=0){
			Glide.with(MyApp.getInstance()).load(imgUrls.get(position)).into(img);
		}
		// ImageView img = new ImageView(this);
		// img.setScaleType(ImageView.ScaleType.FIT_XY);
		// img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
		// LayoutParams.MATCH_PARENT));
		listViews.add(img);
	}
	// 返回按钮添加的监听器
	private class BackListener implements OnClickListener {

		public void onClick(View v) {
			// intent.setClass(GalleryActivity.this, ImageFile.class);
			// startActivity(intent);
			finish();
		}
	}

	// 删除按钮添加的监听器
	private class DelListener implements OnClickListener {

		public void onClick(View v) {
			if (listViews.size() == 1) {
				currentBitmapList.clear();
				Bimp.max = 0;
				send_bt.setText(Res.getString("finish") + "(" + currentBitmapList.size() + "/" + PublicWay.num + ")");
				Intent intent = new Intent("data.broadcast.action");
				sendBroadcast(intent);
				finish();
			} else {
				currentBitmapList.remove(location);
				Bimp.max--;
				pager.removeAllViews();
				listViews.remove(location);
				adapter.setListViews(listViews);
				send_bt.setText(Res.getString("finish") + "(" + currentBitmapList.size() + "/" + PublicWay.num + ")");
				adapter.notifyDataSetChanged();
			}
		}
	}

	// 完成按钮的监听
	private class GallerySendListener implements OnClickListener {
		public void onClick(View v) {
			finish();
			// intent.setClass(mContext,Peisongdetail_Activity.class);
			// startActivity(intent);
		}

	}

	public void isShowOkBt() {
		if (currentBitmapList.size() > 0) {
			send_bt.setText(Res.getString("finish") + "(" + currentBitmapList.size() + "/" + PublicWay.num + ")");
			send_bt.setPressed(true);
			send_bt.setClickable(true);
			send_bt.setTextColor(Color.WHITE);
		} else {
			send_bt.setPressed(false);
			send_bt.setClickable(false);
			send_bt.setTextColor(Color.parseColor("#E1E0DE"));
		}
	}

	/**
	 * 监听返回按钮
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (position == 1) {
				this.finish();
				// intent.setClass(GalleryActivity.this, AlbumActivity.class);
				// startActivity(intent);
			} else if (position == 2) {
				this.finish();
				// intent.setClass(GalleryActivity.this, ShowAllPhoto.class);
				// startActivity(intent);
			}
		}
		return true;
	}

	class MyPageAdapter extends PagerAdapter {

		private ArrayList<View> listViews;

		private int size;

		public MyPageAdapter(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public void setListViews(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public int getCount() {
			return size;
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPagerFixed) arg0).removeView(listViews.get(arg1 % size));
		}

		public void finishUpdate(View arg0) {
		}

		public Object instantiateItem(View container, int position) {
			try {
				((ViewPagerFixed) container).addView(listViews.get(position % size), 0);

			} catch (Exception e) {
			}
			return listViews.get(position % size);
		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
		//陆管家
	}
}
