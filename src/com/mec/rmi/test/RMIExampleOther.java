package com.mec.rmi.test;

public class RMIExampleOther implements IRMIExample {

	@Override
	public int fun1() {
		System.out.println("执行Other的fun1()");
		return "wucanfun1()".hashCode();
	}

	@Override
	public void fun2(Complex c) {

	}

	@Override
	public String fun3(int num, String str, Complex c) {
		System.out.println("执行Other的fun3()");
		return "["+num+":"+str + ":" + c+"]";
	}

	@Override
	public String fun4(int a) {
		return a+"";
	}

}
