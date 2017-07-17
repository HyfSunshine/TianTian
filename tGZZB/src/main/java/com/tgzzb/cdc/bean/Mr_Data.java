package com.tgzzb.cdc.bean;

import java.io.Serializable;

public class Mr_Data implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String billcode;
	private String dzhm;
	private String zhuanghuodate;
	private String ljh;
	private int requireqty;
	// private int id;
	// private int ordernum;
	// private int shipqty;
	// private int receiptqty;
	// private String creatdate;

	public Mr_Data(String billcode, String dzhm, String zhuanghuodate, String ljh, int requireqty) {
		super();
		this.billcode = billcode;
		this.dzhm = dzhm;
		this.zhuanghuodate = zhuanghuodate;
		this.ljh = ljh;
		this.requireqty = requireqty;
	}

	public String getBillcode() {
		return billcode;
	}

	public void setBillcode(String billcode) {
		this.billcode = billcode;
	}

	public String getDzhm() {
		return dzhm;
	}

	public void setDzhm(String dzhm) {
		this.dzhm = dzhm;
	}

	public String getZhuanghuodate() {
		return zhuanghuodate;
	}

	public void setZhuanghuodate(String zhuanghuodate) {
		this.zhuanghuodate = zhuanghuodate;
	}

	public String getLjh() {
		return ljh;
	}

	public void setLjh(String ljh) {
		this.ljh = ljh;
	}

	public int getRequireqty() {
		return requireqty;
	}

	public void setRequireqty(int requireqty) {
		this.requireqty = requireqty;
	}

	@Override
	public String toString() {
		return "Mr_Data [billcode=" + billcode + ", dzhm=" + dzhm + ", zhuanghuodate=" + zhuanghuodate + ", ljh=" + ljh
				+ ", requireqty=" + requireqty;
	}
}
