package com.mec.rmi.core;

import java.lang.reflect.Method;

public class RMIDefinition {
	private Class<?> klass;
	private Object object;
	private Method method;
	
	RMIDefinition() {
	}

	Class<?> getKlass() {
		return klass;
	}

	void setKlass(Class<?> klass) {
		this.klass = klass;
	}

	Object getObject() {
		return object;
	}

	void setObject(Object object) {
		this.object = object;
	}

	Method getMethod() {
		return method;
	}

	void setMethod(Method method) {
		this.method = method;
	}

	@Override
	public String toString() {
		return "类:" + klass.getName() 
			+ "\n对象:" + object 
			+ "\n方法:" + method;
	}

}
