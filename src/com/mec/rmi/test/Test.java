package com.mec.rmi.test;

import com.mec.rmi.core.RMIClient;
import com.mec.rmi.core.RMIFactory;
import com.mec.rmi.core.RMIServer;

public class Test {

	public static void main(String[] args) {
		RMIServer rmis = new RMIServer();
		((RMIServer) rmis).initServer("/dothis.properties");
		RMIFactory rmiFactory = new RMIFactory();
		try {
			rmiFactory.registry(RMIExample.class, IRMIExample.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		rmis.startup();

		

		RMIClient client = new RMIClient();
		client.setIp("192.168.1.26");
		client.initClient("/lib/dothis.properties");

		Complex c = new Complex(1.2, 3.4);
		System.out.println(c.hashCode());
		IRMIExample irm = client.getProxy(IRMIExample.class);

		System.out.println("返回值:" +irm.fun3(12, "abc", c));
//		System.out.println("\n");
//		System.out.println("返回值:"+irm.fun1());
		
	
	}

}
