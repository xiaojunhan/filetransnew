package com.david4.filetrans.model;

public class User {

	private String name;
	private String password;
	private String tasks;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getTasks() {
		return tasks;
	}
	public void setTasks(String tasks) {
		this.tasks = tasks;
	}
	@Override
	public String toString() {
		return "User [name=" + name + ", password=" + password + ", tasks="
				+ tasks + "]";
	}

}
