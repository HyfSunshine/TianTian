package com.tgzzb.cdc.adapter;

import java.util.ArrayList;

import com.bumptech.glide.Glide;
import com.tgzzb.cdc.R;
import com.tgzzb.cdc.bean.DriverIMGAddress;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DriverCheckPicAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<DriverIMGAddress> imgAddress;
	private ArrayList<String> imgUrls = new ArrayList<String>();

	public DriverCheckPicAdapter(Context context) {
		super();
		this.context = context;
		imgAddress = new ArrayList<DriverIMGAddress>();
	}

	/**
	 * @return the imgAddress
	 */
	public ArrayList<DriverIMGAddress> getImgAddress() {
		return imgAddress;
	}

	/**
	 * @param imgAddress
	 *            the imgAddress to set
	 */
	public void setImgAddress(ArrayList<DriverIMGAddress> imgAddress) {
		this.imgAddress = imgAddress;
	}

	/**
	 * @return the imgUrls
	 */
	public ArrayList<String> getImgUrls() {
		return imgUrls;
	}

	/**
	 * @param imgUrls the imgUrls to set
	 */
	public void setImgUrls(ArrayList<String> imgUrls) {
		this.imgUrls = imgUrls;
	}

	@Override
	public int getCount() {
		return imgAddress.size();
	}

	@Override
	public Object getItem(int position) {
		return imgAddress.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_gv_main, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.image);
			holder.tv = (TextView) convertView.findViewById(R.id.text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String imgaddress = imgAddress.get(position).getAddress();
		Glide.with(context).load(imgaddress).placeholder(R.drawable.picloading).error(R.drawable.default_pic)
				.into(holder.iv);
		int lastSlash = imgaddress.lastIndexOf("/");
		String imgName = imgaddress.substring(lastSlash + 1);
		holder.tv.setText(imgName);

		// //图片加载完成之后获取Bitmap存储起来用于图片预览
		//// Drawable dw = resource.getCurrent() ;
		//// BitmapDrawable bmdw = (BitmapDrawable) dw;
		//// Bitmap bm = bmdw.getBitmap();
		// ImageItem imgItem = new ImageItem();
		// imgItem.setBitmap(arg0);
		// picMap.put(position, imgItem);//按顺序存到Hashmap
		// // 所有图片已加载完成，按顺序将hashmap中保存的图片转存到ArrayList
		// if (count == imgAddress.size()) {
		// System.out.println("所有图片已加载完成");
		// for (int i = 0; i < imgAddress.size(); i++) {
		// imgItems.add(picMap.get(i));
		// imgUrls.add(imgAddress.get(i).getAddress());
		// }
		// }

		return convertView;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		ArrayList<String> tempArray = new ArrayList<String>();
		for (int i = 0; i < imgAddress.size(); i++) {
			tempArray.add(imgAddress.get(i).getAddress());
		}
		imgUrls = tempArray;
	}

	class ViewHolder {
		ImageView iv;
		TextView tv;

	}
}
