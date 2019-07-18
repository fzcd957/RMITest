package com.mec.rmi.test;

public class RMIExample implements IRMIExample {

	@Override
	public int fun1() {
		System.out.println("wucan");

		return 151515;
	}

	@Override
	public void fun2(Complex c) {
		System.out.println(c);
	}

	@Override
	public String fun3(int num, String str, Complex c) {
		System.out.println("方法代码被执行");


		return num + ":" + str +":" + c;
	}

	@Override
	public String fun4(int a) {
		System.out.println(a);
		return a+"nihao";
	}

}
