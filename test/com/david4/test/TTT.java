package com.david4.test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class TTT {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String s = "\"CMB\".substring(1,2)";
		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine engine = sem.getEngineByExtension("js");
		try {
			engine.eval("if(6>5){flag=true;}else{flag =false;}");
			engine.eval("var t = \"CMB\".substring(0,2);");
		} catch (ScriptException ex) {
			ex.printStackTrace();
		}

		System.out.println((engine.get("flag")));
		System.out.println((engine.get("t")));
	}

}
