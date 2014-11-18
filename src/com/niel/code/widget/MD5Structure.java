package com.niel.code.widget;


public class MD5Structure {
	public static final int MD5_EMPTY = 0;
	public static final int MD5_FINISH = 1;
	public static final int TYPE_FILE = 10;
	public static final int TYPE_FOLDER = 11;
	private String path;
	private String name;
	private String md5;
	private String percentage;
	private int status;
	private int type;
	
	public MD5Structure(String path, String name, String md5, String percentage) {
		this.path = path;
		this.name = name;
		if(md5 == null) {
			this.status = MD5_EMPTY;
			this.md5 = null;
		} else if(md5.length() != 0) {
			this.status = MD5_FINISH;
			this.md5 = md5;
		}
		this.percentage = percentage;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setMD5(String md5) {
		if(md5 != null)
			this.status = MD5_FINISH;
		this.md5 = md5;
	}
	
	public void setPercentage(int values) {
		this.percentage = values + "";
	}
	
	public int getType() {
		return type;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getName() {
		return name;
	}
	
	public String getMD5() {
		return md5;
	}
	
	public String getPercentage() {
		return percentage;
	}
	
	public int getStatus() {
		return status;
	}
}