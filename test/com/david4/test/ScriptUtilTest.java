package com.david4.test;

import com.david4.common.util.ScriptUtil;

public class ScriptUtilTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for(int i=0;i<100;i++){
			Test t = new ScriptUtilTest().new Test(Integer.toString(i));
			t.start();
		}

	}

	public class Test extends Thread{
		private String str;
		Test(String str){
			this.str = str;
		}
		@Override
		public void run(){ 
			for(int i=0;i<10;i++){
				test();
			}
		}
		
		public void test(){
			String script = "var path = \""+str+"\";";
			String temp = ScriptUtil.getString(script, "path");
			if(!temp.equals(str)){
				System.err.println(temp+"="+str);
			}
		}
	}
}
