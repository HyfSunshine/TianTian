package com.tgzzb.cdc.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tgzzb.cdc.DriverAddInfoActivity;
import com.tgzzb.cdc.MyApp;
import com.tgzzb.cdc.R;
import com.tgzzb.cdc.bean.Mr_Data;
import com.tgzzb.cdc.imagepicker.AlbumActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import net.bither.util.NativeUtil;

public class Commons {
	public static void ShowToast(Context context, String content) {
		Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
	}

	// 显示普通Dialog
	public static void ShowDialog(Context context, String title, String message) {
		AlertDialog dialog = new AlertDialog.Builder(context).setTitle(title).setMessage(message)
				.setNeutralButton("确定", null).create();
		dialog.show();
	}

	// 返回dialog对象
	public static AlertDialog getDialog(Context context, String title, String message) {
		AlertDialog dialog = new AlertDialog.Builder(context).setTitle(title).setMessage(message)
				.setNeutralButton("确定", null).create();
		return dialog;
	}

	// 显示拍照+相册的Dialog
	public static void ShowSelectPicDialog(Context context) {
		// AlertDialog dialog = new
		// AlertDialog.Builder(context).setTitle("上传照片")
		// .setItems(new String[] { "拍照", "打开相册" }, new
		// DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// if (which == 0) {
		// photo();
		// }
		// if (which == 1) {
		// Intent intent = new Intent(context, AlbumActivity.class);
		// intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		// startActivity(intent);
		// }
		// }
		// }).create();
		// dialog.show();
	}

	public static void setTitle(Activity activity, String title, OnClickListener listener) {
		TextView tv_title = (TextView) activity.findViewById(R.id.tv_title);
		tv_title.setText(title);
		activity.findViewById(R.id.title_left).setOnClickListener(listener);
	}

	/**
	 * 解析json
	 */
	public static <T> T parseJsonClass(String jsonString, Class<T> clazz) {
		T t = null;
		try {
			Gson gson = new Gson();
			String str = jsonString.substring(1, jsonString.length() - 1);
			System.out.println("str = " + str);
			t = gson.fromJson(str, clazz);
			System.out.println("json解析成功！");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("json解析失败！");
		}
		return t;
	}

	/**
	 * 解析json集合
	 */
	public static <T> T parseJsonList(String jsonString, Type typeOfT) {
		T t = null;
		try {
			Gson gson = new Gson();
			t = gson.fromJson(jsonString, typeOfT);
			System.out.println("json解析成功！");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("json解析失败！");
		}
		return t;
	}

	public static String parseXML(String string) {
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(new StringReader(string));
			int eventType = parser.getEventType();
			String json = "";
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String nodeName = parser.getName();
				switch (eventType) {
				case XmlPullParser.START_TAG:
					// 开始某个节点
					if ("string".equals(nodeName)) {
						json = parser.nextText();
					}
					break;
				case XmlPullParser.END_TAG:
					// 解析完成

					break;
				}
				eventType = parser.next();
			}

			System.out.println("xml解析成功 " + json);
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("xml解析失败");
		}
		System.out.println("xml解析失败");
		return null;
	}

	/**
	 * 请求服务器MR
	 */
	public static String getResponse(Context context, String methodString, String... params) {
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		SoapObject rpc = new SoapObject(context.getResources().getString(R.string.request_namespace), methodString);
		// SoapObject rpc = new SoapObject("http://steve.cw169.4everdns.com/",
		// methodString);

		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		// Log.e(methodString, methodString);
		if (params.length % 2 == 1) {
			Toast.makeText(context, "错误的参数队列来自函数：Commons.getResponse()", Toast.LENGTH_SHORT).show();
			return "-1";
		}
		PropertyInfo propertyInfo = null;
		for (int i = 0; i < params.length; i++) {
			if (i % 2 == 0) {
				if (params[i] != null) {
					propertyInfo = new PropertyInfo();
					propertyInfo.setName(params[i]);
				}
			} else if (propertyInfo != null) {
				propertyInfo.setValue(params[i] == null ? "" : params[i]);
				rpc.addProperty(propertyInfo);
				propertyInfo = null;
			}
		}
		envelope.setOutputSoapObject(rpc);
		HttpTransportSE ht = new HttpTransportSE(context.getResources().getString(R.string.request_url_asmx));
		ht.debug = true;
		String info = null;
		try {
			ht.call(context.getResources().getString(R.string.request_url) + methodString, envelope);
			if (envelope.bodyIn != null) {
				if (envelope.bodyIn.getClass() == SoapObject.class) {
					SoapObject soapObject = (SoapObject) envelope.bodyIn;
					info = soapObject.getProperty(0).toString();
				} else {
					// 错误！可能原因：错误的方法名或参数...
					info = "-1";
				}
			} else {
				// 网络错误，无法连接数据库!
				info = "-2";
			}
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("Soap error:", e.toString());
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} finally {

		}
		return info;
	}
	
	/**
	 * 请求服务器170
	 */
	public static String getResponse170(Context context, String methodString, String... params) {
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		SoapObject rpc = new SoapObject(context.getResources().getString(R.string.request_namespace170), methodString);
		// SoapObject rpc = new SoapObject("http://steve.cw169.4everdns.com/",
		// methodString);

		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		// Log.e(methodString, methodString);
		if (params.length % 2 == 1) {
			Toast.makeText(context, "错误的参数队列来自函数：Commons.getResponse()", Toast.LENGTH_SHORT).show();
			return "-1";
		}
		PropertyInfo propertyInfo = null;
		for (int i = 0; i < params.length; i++) {
			if (i % 2 == 0) {
				if (params[i] != null) {
					propertyInfo = new PropertyInfo();
					propertyInfo.setName(params[i]);
				}
			} else if (propertyInfo != null) {
				propertyInfo.setValue(params[i] == null ? "" : params[i]);
				rpc.addProperty(propertyInfo);
				propertyInfo = null;
			}
		}
		envelope.setOutputSoapObject(rpc);
		HttpTransportSE ht = new HttpTransportSE(context.getResources().getString(R.string.request_url_asmx170));
		ht.debug = true;
		String info = null;
		try {
			ht.call(context.getResources().getString(R.string.request_url170) + methodString, envelope);
			if (envelope.bodyIn != null) {
				if (envelope.bodyIn.getClass() == SoapObject.class) {
					SoapObject soapObject = (SoapObject) envelope.bodyIn;
					info = soapObject.getProperty(0).toString();
				} else {
					// 错误！可能原因：错误的方法名或参数...
					info = "-1";
				}
			} else {
				// 网络错误，无法连接数据库!
				info = "-2";
			}
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("Soap error:", e.toString());
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} finally {

		}
		return info;
	}

	
	
	public static String getResponse78(Context context, String methodString, String... params) {
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		SoapObject rpc = new SoapObject(context.getResources().getString(R.string.request_namespace78), methodString);
		// SoapObject rpc = new SoapObject("http://steve.cw169.4everdns.com/",
		// methodString);

		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		// Log.e(methodString, methodString);
		if (params.length % 2 == 1) {
			Toast.makeText(context, "错误的参数队列来自函数：Commons.getResponse()", Toast.LENGTH_SHORT).show();
			return "-1";
		}
		PropertyInfo propertyInfo = null;
		for (int i = 0; i < params.length; i++) {
			if (i % 2 == 0) {
				if (params[i] != null) {
					propertyInfo = new PropertyInfo();
					propertyInfo.setName(params[i]);
				}
			} else if (propertyInfo != null) {
				propertyInfo.setValue(params[i] == null ? "" : params[i]);
				rpc.addProperty(propertyInfo);
				propertyInfo = null;
			}
		}
		envelope.setOutputSoapObject(rpc);
		HttpTransportSE ht = new HttpTransportSE(context.getResources().getString(R.string.request_url_asmx78));
		ht.debug = true;
		String info = null;
		try {
			ht.call(context.getResources().getString(R.string.request_url78) + methodString, envelope);
			if (envelope.bodyIn != null) {
				if (envelope.bodyIn.getClass() == SoapObject.class) {
					SoapObject soapObject = (SoapObject) envelope.bodyIn;
					info = soapObject.getProperty(0).toString();
				} else {
					// 错误！可能原因：错误的方法名或参数...
					info = "-1";
				}
			} else {
				// 网络错误，无法连接数据库!
				info = "-2";
			}
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("Soap error:", e.toString());
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} finally {

		}
		return info;
	}

 
	
 
	/**
	 * 获取当前日期
	 */
	public static String getCurrentDate() {
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());// 设置日期格式;
		return df.format(date);
	}

	public static String getCurrentDaterTimeSSS() {
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());// 设置日期格式;
		return df.format(date);
	}

	/**
	 * 检查网络连接状态
	 */

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivityManager == null) {
			return false;
		} else {
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

			if (networkInfo != null && networkInfo.length > 0) {
				for (int i = 0; i < networkInfo.length; i++) {
					System.out.println(i + "---状态---" + networkInfo[i].getState());
					System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static Bitmap compressImage(Context context, Bitmap image, int quality, int size) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		float widthPercent = 0, heightPercent = 0;
		widthPercent = 1280;
		heightPercent = 960;
		System.out.println("image.getWidth() = " + image.getWidth());
		System.out.println("image.getHeight()" + image.getHeight());
		if (image.getWidth() < image.getHeight()) {
			widthPercent = widthPercent + heightPercent;
			heightPercent = widthPercent - heightPercent;
			widthPercent = widthPercent - heightPercent;
		}

		System.out.println("widthPercent = " + widthPercent);
		System.out.println("heightPercent = " + heightPercent);

		widthPercent = (float) (1.0 * image.getWidth() / widthPercent);
		heightPercent = (float) (1.0 * image.getHeight() / heightPercent);
		System.out.println("widthPercent = " + widthPercent);
		System.out.println("heightPercent = " + heightPercent);
		options.inSampleSize = Math.round(Math.max(widthPercent, heightPercent));
		if (Math.max(widthPercent, heightPercent) > 1) {
			image = scaleBitmapAsWidthAndHeight(context, image,
					Math.round((image.getWidth() / Math.max(widthPercent, heightPercent))),
					Math.round((image.getHeight() / Math.max(widthPercent, heightPercent))));
		}
		// Log.e("", image.getWidth()+"-"+image.getHeight());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (quality <= 0) {
			quality = 100;
		}
		image.compress(Bitmap.CompressFormat.JPEG, quality, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		if (size > 0) {
			System.out.println("baos.size = " + baos.size());
			System.out.println("size = " + size);
			while (baos.size() / 1024 > size) { // 循环判断如果压缩后图片是否大于size,大于继续压缩
				baos.reset();// 重置baos即清空baos
				image.compress(Bitmap.CompressFormat.JPEG, quality, baos);// 这里压缩options%，把压缩后的数据存放到baos中
				if (quality <= 5) {
					break;
				} else {
					quality -= 5;// 每次都减少5
				}
			}
		}
		// 把压缩后的数据baos存放到ByteArrayInputStream中
		try {
			return BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()), null, null);// 把ByteArrayInputStream数据生成图片
		} catch (Exception e) {
		} finally {
			try {
				baos.reset();
				baos.close();
				// image.recycle();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	// public static Bitmap compressImage(Context context, Bitmap image, int
	// quality, int size) {
	// BitmapFactory.Options options = new BitmapFactory.Options();
	// float widthPercent = 0, heightPercent = 0;
	// heightPercent = 960;
	// widthPercent = 1280;
	//
	// if (image.getWidth() < image.getHeight()) {
	// widthPercent = widthPercent + heightPercent;
	// heightPercent = widthPercent - heightPercent;
	// widthPercent = widthPercent - heightPercent;
	// }
	// widthPercent = (float) (1.0 * image.getWidth() / widthPercent);
	// heightPercent = (float) (1.0 * image.getHeight() / heightPercent);
	// options.inSampleSize = Math.round(Math.max(widthPercent, heightPercent));
	// if (Math.max(widthPercent, heightPercent) > 1) {
	// image = Commons.scaleBitmapAsWidthAndHeight(context, image,
	// Math.round((image.getWidth() / Math.max(widthPercent, heightPercent))),
	// Math.round((image.getHeight() / Math.max(widthPercent, heightPercent))));
	// }
	// // Log.e("", image.getWidth()+"-"+image.getHeight());
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// if (quality <= 0) {
	// quality = 100;
	// }
	// image.compress(Bitmap.CompressFormat.JPEG, quality, baos);//
	// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
	// if (size > 0) {
	// while (baos.size() / 1024 > size) { // 循环判断如果压缩后图片是否大于size,大于继续压缩
	// baos.reset();// 重置baos即清空baos
	// image.compress(Bitmap.CompressFormat.JPEG, quality, baos);//
	// 这里压缩options%，把压缩后的数据存放到baos中
	// if (quality <= 5) {
	// break;
	// } else {
	// quality -= 5;// 每次都减少5
	// }
	// }
	// }
	//
	// // 把压缩后的数据baos存放到ByteArrayInputStream中
	// try {
	// return BitmapFactory.decodeStream(new
	// ByteArrayInputStream(baos.toByteArray()), null, null);//
	// 把ByteArrayInputStream数据生成图片
	// } catch (Exception e) {
	// } finally {
	// try {
	//// isBm.close();
	// baos.reset();
	// baos.close();
	// image.recycle();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// return null;
	// }

	public static Bitmap scaleBitmapAsWidthAndHeight(Context context, Bitmap bitmap, int width, int height) {
		if (width <= 0 || height <= 0) {
			return bitmap;
		} else {
			Matrix matrix = new Matrix();
			float sx = 1.0f * width / bitmap.getWidth();
			BitmapDrawable bd = new BitmapDrawable(context.getResources(), bitmap);
			float sy = 1.0f * height / bitmap.getHeight();
			matrix.postScale(sx, sy);
			try {
				return Bitmap.createBitmap(bitmap, 0, 0, bd.getIntrinsicWidth(), bd.getIntrinsicHeight(), matrix, true);
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				bd.getBitmap().recycle();
				bitmap.recycle();
			}
			return null;
		}
	}

	// 存储进SD卡
	public static void saveBitmapFile(Bitmap bm, String filePath) {
		BufferedOutputStream bos = null;
		try {
			File dirFile = new File(filePath);
			// 检测图片是否存在
			if (dirFile.exists()) {
				dirFile.delete(); // 删除原图片
			}
			File myCaptureFile = new File(filePath);
			bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
			// 100表示不进行压缩，70表示压缩率为30%
			bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
		} catch (Exception e) {
		} finally {
			try {
				if (bos != null) {
					bos.flush();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (bos != null) {
					bos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 弹出对话框选择拍照或者打开相册

	// public static void showDialogUploadPic(final Context context,final int
	// resultCode) {
	// AlertDialog dialog1 = new AlertDialog.Builder(context).setTitle("上传照片")
	// .setItems(new String[] { "拍照", "打开相册" }, new
	// DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// if (which == 0) {
	// photo(context,resultCode);
	// }
	// if (which == 1) {
	// Intent intent = new Intent(context, AlbumActivity.class);
	// intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
	// context.startActivity(intent);
	// }
	// }
	// }).create();
	// dialog1.show();
	// }
	// public static void photo(Context context,int resultCode) {
	// String SDState = Environment.getExternalStorageState();
	// Uri imageUri = null;
	// if (TextUtils.equals(SDState, Environment.MEDIA_MOUNTED)) {
	// ContentValues values = new ContentValues(1);
	// values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
	// Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	//
	// imageUri =
	// context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
	// values);
	// openCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
	// Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
	// if (imageUri == null) {
	// return ;
	// }
	// DriverAddInfoActivity.imageUri = imageUri;
	// openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
	// // openCameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
	// ((Activity) context).startActivityForResult(openCameraIntent,
	// resultCode);
	// } else {
	//
	// }
	// }

	public static void changeWindowBg(Activity act, float alpha) {

		WindowManager.LayoutParams lp = act.getWindow().getAttributes();
		// 解决6.0屏幕不变暗的问题
		act.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		lp.alpha = alpha;
		act.getWindow().setAttributes(lp);

	}

	public static String getImageBase64(Bitmap bitmap, File file) {

		// Bitmap bp = Commons.compressImage(context, bitmap, 100, 640);
		// Commons.saveBitmapFile(bp, initFile().getPath());
		// // Commons.saveBitmapFile(bp, initFile().getParent());
		// 将Bitmap转换成字符串
		String string = null;
		ByteArrayOutputStream bStream = null;

		NativeUtil.compressBitmap(bitmap, file.getPath());
		Bitmap bp = BitmapFactory.decodeFile(file.getPath());

		try {
			bStream = new ByteArrayOutputStream();
			bp.compress(CompressFormat.JPEG, 100, bStream);// 100表示不压缩
			byte[] bytes = bStream.toByteArray();
			string = Base64.encode(bytes);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (bStream != null) {
				try {
					bStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return string;
	}

	public static File initFile() {
		String fileName = UUID.randomUUID().toString();
		File tempFile = new File(Environment.getExternalStorageDirectory(), fileName + ".jpg");
		if (tempFile.exists()) {
			tempFile.delete();
		} else {
			try {
				tempFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return tempFile;
	}

	public static ProgressDialog getDialogLoadingPic(final Context context, String title, int max,
			final boolean finish) {
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setMax(max);
		// 设置ProgressDialog 标题
		progressDialog.setTitle("司机操作");
		// 设置ProgressDialog 提示信息
		progressDialog.setMessage("正在上传图片...");
		// 设置ProgressDialog 的进度条是否不明确
		progressDialog.setIndeterminate(false);
		progressDialog.setCancelable(false);
		progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "后台上传", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (finish) {
					((Activity) context).finish();
				}
			}
		});
		return progressDialog;
	}

}
