package com.tgzzb.cdc.bean;

public class DriverYwbhDdcode {
	 private String billcode;
	 private String ddcode;
	public DriverYwbhDdcode(String billcode, String ddcode) {
		super();
		this.billcode = billcode;
		this.ddcode = ddcode;
	}
	/**
	 * @return the billcode
	 */
	public String getBillcode() {
		return billcode;
	}
	/**
	 * @param billcode the billcode to set
	 */
	public void setBillcode(String billcode) {
		this.billcode = billcode;
	}
	/**
	 * @return the ddcode
	 */
	public String getDdcode() {
		return ddcode;
	}
	/**
	 * @param ddcode the ddcode to set
	 */
	public void setDdcode(String ddcode) {
		this.ddcode = ddcode;
	}
	 
}
