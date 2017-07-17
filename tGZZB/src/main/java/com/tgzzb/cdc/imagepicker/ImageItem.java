package com.tgzzb.cdc.imagepicker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

public class ImageItem implements Parcelable {
	/** 
	 * 
	 */
//	private byte[] byteBitmap;
	public String imageId;
	public String thumbnailPath;
	public String imagePath;
	private Bitmap bitmap;
//	private int length;
//	private boolean isSelected = false;

	public ImageItem() {

	}

	public ImageItem(Parcel source) {
		this.imageId = source.readString();
		this.thumbnailPath = source.readString();
		this.imagePath = source.readString();
//		this.length = source.readInt();
//
//		byte[] temp = new byte[length];
//		source.readByteArray(temp);
//		this.bitmap = getBitmap(temp);

		// this.bitmap = source.readParcelable(Bitmap.class.getClassLoader());
		// isSelected = source.readByte() != 0; // myBoolean == true if byte !=
		// 0
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getThumbnailPath() {
		return thumbnailPath;
	}

	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

//	public boolean isSelected() {
//		return isSelected;
//	}
//
//	public void setSelected(boolean isSelected) {
//		this.isSelected = isSelected;
//	}

	public Bitmap getBitmap() {
		if (bitmap == null) {
			try {
				bitmap = Bimp.revitionImageSize(imagePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<ImageItem> CREATOR = new Creator<ImageItem>() {

		@Override
		public ImageItem createFromParcel(Parcel source) {
			return new ImageItem(source);
		}

		@Override
		public ImageItem[] newArray(int size) {
			return new ImageItem[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
//		byteBitmap = getBytes(bitmap);
//		length = byteBitmap.length;
//		System.out.println("write length = " + length);
		dest.writeString(imageId);
		dest.writeString(thumbnailPath);
		dest.writeString(imagePath);
//		dest.writeParcelable(bitmap, flags);
//		dest.writeByteArray(byteBitmap);
//		dest.writeInt(length);

//		dest.writeByte((byte) (isSelected ? 1 : 0));
	}

	private byte[] getBytes(Bitmap bitmap) {

		ByteArrayOutputStream baops = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 0, baops);
		return baops.toByteArray();
	}

	private Bitmap getBitmap(byte[] data) {
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}
}
