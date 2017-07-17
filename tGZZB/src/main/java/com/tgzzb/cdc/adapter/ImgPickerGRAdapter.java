package com.tgzzb.cdc.adapter;

import java.util.ArrayList;

import com.tgzzb.cdc.R;
import com.tgzzb.cdc.imagepicker.Bimp;
import com.tgzzb.cdc.imagepicker.ImageItem;
import com.tgzzb.cdc.imagepicker.PublicWay;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImgPickerGRAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private int selectedPosition = -1;
	private boolean shape;
	private Context context;
	private ArrayList<ImageItem> currentBitmapList;

	public ArrayList<ImageItem> getCurrentBitmapList() {
		return currentBitmapList;
	}

	public void setCurrentBitmapList(ArrayList<ImageItem> currentBitmapList) {
		this.currentBitmapList = currentBitmapList;
	}

	public boolean isShape() {
		return shape;
	}

	public void setShape(boolean shape) {
		this.shape = shape;
	}

	public ImgPickerGRAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		currentBitmapList = new ArrayList<ImageItem>();
		this.context = context;
	}

	public void update() {
		loading();
	}

	public int getCount() {
		if (currentBitmapList.size() == PublicWay.num) {
			return PublicWay.num;
		}
		return (currentBitmapList.size() + 1);
	}

	public Object getItem(int position) {
		return currentBitmapList.get(position);
	}

	public long getItemId(int arg0) {
		return arg0;
	}

	public void setSelectedPosition(int position) {
		selectedPosition = position;
	}

	public int getSelectedPosition() {
		return selectedPosition;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_published_grida, parent, false);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView.findViewById(R.id.item_grida_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position == currentBitmapList.size()) {
			holder.image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.add_pic));
			if (position == PublicWay.num) {
				holder.image.setVisibility(View.GONE);
			}
		} else {
			holder.image.setImageBitmap(currentBitmapList.get(position).getBitmap());
		}

		return convertView;
	}

	public class ViewHolder {
		public ImageView image;
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				System.out.println("msg what = 1->notifyDataSetChanged");
				notifyDataSetChanged();
				break;
			}
			super.handleMessage(msg);
		}
	};

	public void loading() {
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					if (Bimp.max == currentBitmapList.size()) {
						Message message = new Message();
						message.what = 1;
						handler.sendMessage(message);
						break;
					} else {
						Bimp.max += 1;
						Message message = new Message();
						message.what = 1;
						handler.sendMessage(message);
					}
				}
			}
		}).start();
	}
}
