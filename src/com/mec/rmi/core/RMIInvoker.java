package com.mec.rmi.core;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.Map;

public class RMIInvoker implements Runnable {
	private static final Type type = new TypeToken<Map<String, String>>() {}.getType();
	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;
	private Gson gson;
	
	RMIInvoker(Socket socket, Gson gson) {
		this.gson = gson;
		this.socket = socket;
		try {
			this.dis = new DataInputStream(socket.getInputStream());
			this.dos = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//  得到参数(类型，值)的数组
	private Object[] getParas(String paraString, Class<?>[] paraType) {
		Map<String, String> paraStringMap = gson.fromJson(paraString, type);
		
		int paraCount = paraStringMap.size();
		if (paraCount <= 0) {
			return new Object[] {};
		}
		Object[] paras = new Object[paraCount];

		for (int index = 0; index < paraStringMap.size(); index++) {
			String key = "arg" + index;
			String value = paraStringMap.get(key);
			paras[index] = gson.fromJson(value, paraType[index]); // 将value转换为对应类型的value
		}
		
		return paras;
	}
	
	@Override
	public void run() {
		try {
			String methodId = dis.readUTF();   //arg[i]
			System.out.println(methodId+"receive");
			String paraString = dis.readUTF();	//values

			RMIDefinition rmiDefinition = new RMIFactory().getRmiDefinition(methodId); //从rmimap中得到对应的Definition(klass,object,mehtod)
			if (rmiDefinition == null) {
				throw new Exception(methodId + "没有找到与之匹配的方法！");
			}
			Object object = rmiDefinition.getObject();
			Method method = rmiDefinition.getMethod();
			if(!paraString.equals("")){
				Object[] paras = getParas(paraString, method.getParameterTypes());  //通过参数的类型和值得到一个参数的数组
				Object result = method.invoke(object, paras);  //反射执行该方法
				dos.writeUTF(gson.toJson(result));
			}
			else{
				Object result = method.invoke(object,new Object[]{});
				dos.writeUTF(gson.toJson(result));
			}

			//将方法的返回值送往Client
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
