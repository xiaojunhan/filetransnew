package com.david4.filetrans.model;

public class FileInfo {

	private String name;
	private long size;
	private String date;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "FileInfo [name=" + name + ", size=" + size + ", date=" + date
				+ "]";
	}
}
