package com.tgzzb.cdc.interfaces;

import android.view.View;
import android.view.View.OnClickListener;

public abstract class MySingleClickListener implements OnClickListener {

	private long preTime;
	private int delaySecond = 500; // 默认两次点击的间隔为 500 毫秒

	public MySingleClickListener() {
		super();
	}

	/**
	 * 可设置两次点击的间隔时间
	 * 
	 * @param delaySecond
	 *            两次点击的间隔时间，单位 毫秒
	 */
	public MySingleClickListener(int delaySecond) {
		this();
		this.delaySecond = delaySecond;
	}

	@Override
	public void onClick(View view) {
		if (!isDoubleClick()) {
			onSingleClick(view);
		}
	}

	/**
	 * 用于为外部提供的覆写方法，以实现点击事件
	 */
	protected abstract void onSingleClick(View view);

	/**
	 * 判断是否是连续点击了Button
	 * 
	 * @return true 连续点击了Button false 没有连续点击Button
	 */
	private boolean isDoubleClick() {
		long lastTime = System.currentTimeMillis();
		boolean flag = lastTime - preTime < delaySecond ? true : false;
		preTime = lastTime;
		return flag;
	}
}