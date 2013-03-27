package com.david4.filetrans.model;

import java.util.List;

import com.david4.common.model.TaskModel;

public class FileTransTaskModel extends TaskModel {
	private From from;
	private List<To> to;
	private List<Delete> delete;

	public class From {
		private String type;
		private String serverid;
		private String path;
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getServerid() {
			return serverid;
		}
		public void setServerid(String serverid) {
			this.serverid = serverid;
		}
		public String getPath() {
			return path;
		}
		public void setPath(String path) {
			this.path = path;
		}
		@Override
		public String toString() {
			return "From [type=" + type + ", serverid=" + serverid + ", path="
					+ path + "]";
		}
	}
	public class To {
		private String type;
		private String serverid;
		private String path;
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getServerid() {
			return serverid;
		}
		public void setServerid(String serverid) {
			this.serverid = serverid;
		}
		public String getPath() {
			return path;
		}
		public void setPath(String path) {
			this.path = path;
		}
		@Override
		public String toString() {
			return "To [type=" + type + ", serverid=" + serverid + ", path="
					+ path + "]";
		}
	}
	public class Delete {
		private String type;
		private String serverid;
		private String path;
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getServerid() {
			return serverid;
		}
		public void setServerid(String serverid) {
			this.serverid = serverid;
		}
		public String getPath() {
			return path;
		}
		public void setPath(String path) {
			this.path = path;
		}
		@Override
		public String toString() {
			return "Delete [type=" + type + ", serverid=" + serverid
					+ ", path=" + path + "]";
		}
	}
	public From getFrom() {
		return from;
	}
	public void setFrom(From from) {
		this.from = from;
	}
	public List<To> getTo() {
		return to;
	}
	public void setTo(List<To> to) {
		this.to = to;
	}
	public List<Delete> getDelete() {
		return delete;
	}
	public void setDelete(List<Delete> delete) {
		this.delete = delete;
	}
}
