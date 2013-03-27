package com.david4.common.model;

public class PathModel {
	/**
	 * 当前路径
	 */
	private String path;
	/**
	 * 下一个文件或文件夹名
	 */
	private String next;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getNext() {
		return next;
	}

	public void setNext(String next) {
		this.next = next;
	}

	@Override
	public String toString() {
		return "PathModel [path=" + path + ", next=" + next + "]";
	}
}