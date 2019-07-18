package com.mec.rmi.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mec.util.ArgumentMaker;
import com.mec.util.PropertiesParser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.net.UnknownHostException;

public class RMIClient {
	private static final Gson gson = new GsonBuilder().create();
	private INode node;
	private int rmiPort;
	private String rmiIp;
	public static final int DEFAULT_PORT = 54189;

	public RMIClient() {
		this.rmiPort = DEFAULT_PORT;
	}
	

	public void initClient(String path) {  
		String portString = PropertiesParser.value("RMIClient.port");
		if (portString == null) {
			try {
				PropertiesParser.loadProperties(path);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		portString = PropertiesParser.value("RMIClient.port");
		if (portString != null) {
			rmiPort = Integer.valueOf(portString);
		}
	}

	public void setIp(String rmiIp) {
		this.rmiIp = rmiIp;
	}

	public void setPort(int port) {
		this.rmiPort = port;
	}

	private String parasToGson(Object[] args) {
		ArgumentMaker am = new ArgumentMaker();
		
		for (Object arg : args) {
			if(arg != null){
				am.addArg(arg);
			}
			else{
				return null;
			}
		}
		
		return am.toString();
	}
	
	private Object invoker(Class<?> interfaces, Method method, Object[] args) {
		Object result = null;
		try {
			Socket socket = new Socket(rmiIp, rmiPort);
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

			System.out.println((""+method).hashCode());
			dos.writeUTF(""+method);
			//  一个json字符串，{"arg0","5"}
			if(args != null){
				dos.writeUTF(parasToGson(args));
			}
			else{
				dos.writeUTF("");
			}
			
			String resString = dis.readUTF();
			result = gson.fromJson(resString, method.getReturnType());
			
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result ;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getProxy(Class<?> interfaces) {
		return (T) Proxy.newProxyInstance(interfaces.getClassLoader(), 
				new Class<?>[] { interfaces }, new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						//真正执行方法的地方
						return invoker(interfaces, method, args);
					}
				});
	}

	public Socket getSocket(){
		if(node != null){
			int port = node.getport();
			String ip = node.getServerip();

			Socket socket = null;
			try {
				socket = new Socket(ip, port);
			} catch (IOException e) {
				e.printStackTrace();
			}

			return socket;
		}
		return null;
	}

//	public INode getINode(){
//		INodeSelector select = new INodeSelector() {
//			@Override
//			public INode getNode() {
//				//获得/Node
//				return null;
//			}
//		}
//
//
//	}


}
