package com.david4.test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class TTT {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws UnknownHostException, IOException {
//		String s = "\"CMB\".substring(1,2)";
//		ScriptEngineManager sem = new ScriptEngineManager();
//		ScriptEngine engine = sem.getEngineByExtension("js");
//		try {
//			engine.eval("if(6>5){flag=true;}else{flag =false;}");
//			engine.eval("var t = \"CMB\".substring(0,2);");
//		} catch (ScriptException ex) {
//			ex.printStackTrace();
//		}
//
//		System.out.println((engine.get("flag")));
//		System.out.println((engine.get("t")));
		test();
	}

	public static void test() throws UnknownHostException, IOException{
		String ip ="192.168.0.100";
		int port = 8090;
		byte [] addr = {(byte)10,(byte)168,0,100};
//		InetAddress inetAddress = InetAddress.getByName("190.1.28.4");
//		for(byte b :inetAddress.getAddress()){
//			System.out.println(b);
//		}
		InetAddress inetAddress = InetAddress.getByAddress(addr);
		Socket socket = new Socket(inetAddress,port);
		System.out.println((byte)190);
	}
}
