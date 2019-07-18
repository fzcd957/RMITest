package com.mec.rmi.core;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public class RMIFactory {
	private static final Map<String, RMIDefinition> rmiMap;
	static {
		rmiMap = new HashMap<>();
	}
	
	public RMIFactory() {
	}
	
	private RMIFactory registry(Class<?> klass, Class<?> interfaces, Object object) throws Exception {
		if (!interfaces.isInterface()) {
			throw new Exception("[" +  interfaces.getName() + "]不是接口！");
		}
		if (!interfaces.isAssignableFrom(klass)) {
			throw new Exception("[" + klass.getName() + "]不是接口[" + interfaces.getName() + "]的实现类");
		}
		Object obj = object == null ? klass.newInstance() : object;
		
		Method[] interfaceMethods = interfaces.getDeclaredMethods();
		for (Method interfaceMethod : interfaceMethods) {
			String methodName = interfaceMethod.getName();
			Class<?>[] paraTypes = interfaceMethod.getParameterTypes();
			
			Method method = klass.getMethod(methodName, paraTypes);  //反射机制得到该方法
			RMIDefinition rmiDefinition = new RMIDefinition();
			rmiDefinition.setKlass(klass);
			rmiDefinition.setObject(obj);
			rmiDefinition.setMethod(method);

			String rmiId = ""+interfaceMethod;
			rmiMap.put(rmiId, rmiDefinition);
		}
		
		return this;
	}
	
	public RMIFactory registry(Class<?> klass, Class<?> interfaces) throws Exception {
		return registry(klass, interfaces, null);
	}
	
	public RMIFactory registry(Object object, Class<?> interfaces) throws Exception {
		return registry(object.getClass(), interfaces, object);
	}
	
	RMIDefinition getRmiDefinition(String id) {
		RMIDefinition rmi = rmiMap.get(id);
		
		return rmi;
	}
	
}
