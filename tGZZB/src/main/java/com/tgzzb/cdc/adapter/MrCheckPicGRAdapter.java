package com.tgzzb.cdc.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.tgzzb.cdc.MrCheckAllPicActivity;
import com.tgzzb.cdc.R;
import com.tgzzb.cdc.bean.Traffic_Message;
import com.tgzzb.cdc.imagepicker.ImageItem;
import com.tgzzb.cdc.imagepicker.PublicWay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MrCheckPicGRAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	public List<Traffic_Message> list;
	private Context context;
	private BitmapUtils bitUtils;
	private boolean positionloaded = false;
	//存放图片
	HashMap<String, ImageItem> imageMap = new HashMap<String, ImageItem>();
	//记录已加载数量
	int count = 0;
	public MrCheckPicGRAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		list = new ArrayList<Traffic_Message>();
		bitUtils = new BitmapUtils(context);
	}

	public List<Traffic_Message> getList() {
		return list;
	}

	public void setList(List<Traffic_Message> list) {
		PublicWay.num = list.size();
		MrCheckAllPicActivity.checkBitmap.clear();
		this.list = list;
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int arg0) {
		return arg0;
	}

	// 使用map存储图片 再赋值给list

	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_gv_main, null);
			holder.image = (ImageView) convertView.findViewById(R.id.image);
			holder.text = (TextView) convertView.findViewById(R.id.text);
			holder.text.setTextSize(10);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (parent.getChildCount() == position) {
			if (positionloaded == true && position == 0) {
				return convertView;
			}
			if (position == 0) {
				positionloaded = true;
			}
			holder.setView(list.get(position), position);
		} else {

		}

		return convertView;
	}

	public class ViewHolder {
		public ImageView image;
		public TextView text;
		BitmapDisplayConfig config;

		public ViewHolder() {
			// 1,实例化BitmapDisplayConfig
			config = new BitmapDisplayConfig();
			// 2,设置bitmapConfig
			config.setBitmapConfig(Bitmap.Config.RGB_565);
			// 3,设置加载失败时显示的图片
			config.setLoadFailedDrawable(context.getResources().getDrawable(R.drawable.picloading));
			// 4,设置正在加载时显示的图片
			config.setLoadingDrawable(context.getResources().getDrawable(R.drawable.picloading));
			// 5,设置图片的最大宽高,会影响图片质量
			// config.setBitmapMaxSize(new BitmapSize(100, 100));
			// 6,设置disk缓存开启,图片会缓存到sd卡,即DiskLruCache
			bitUtils.configDiskCacheEnabled(true);
			// 7,设置Memory缓存开启,即LruCache
			bitUtils.configMemoryCacheEnabled(true);
			// 8,根据上面设置的条件展示图片
			// bitUtil.display(imageView,url,config);
		}

		public synchronized void setView(Traffic_Message message, final int position) {
			bitUtils.display(image, context.getResources().getString(R.string.request_pic) + message.getUrl(), config,
					new BitmapLoadCallBack<View>() {

						@Override
						public void onLoadCompleted(View arg0, String arg1, Bitmap arg2, BitmapDisplayConfig arg3,
								BitmapLoadFrom arg4) {
							image.setImageBitmap(arg2);
							ImageItem item = new ImageItem();
							item.setBitmap(arg2);
							count = count + 1;
							imageMap.put(String.valueOf(position), item);
							// 所有图片已加载完成
							if (count == list.size()) {
								for (int i = 0; i < list.size(); i++) {
									MrCheckAllPicActivity.checkBitmap.add(imageMap.get(String.valueOf(i)));
								}
							} 
						}
						@Override
						public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
							
						}
					});
			text.setText(message.getQymc() + "_" + message.getLjh() + "_" + message.getMessage());
		}
	}
}
