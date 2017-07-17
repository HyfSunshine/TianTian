package com.tgzzb.cdc.bean;

public class AppVersion {
	private String version;
	private String url;
	private String createdate;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getCreatedate() {
		return createdate;
	}

	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public AppVersion(String version, String url) {
		super();
		this.version = version;
		this.url = url;
	}

	public AppVersion() {
	}
}
