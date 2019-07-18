package com.mec.rmi.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mec.util.PropertiesParser;

public class RMIServer implements Runnable {
	private int port;
	private ServerSocket serverSocket;
	private volatile boolean goon;
	
	public static final int DEFAULT_PORT = 54189;
	private static final Gson gson = new GsonBuilder().create();
	private static long no;
	
	public RMIServer() {
		this.port = DEFAULT_PORT;
	}

	//用于加载在properties配置文件中写入的port
	public void initServer(String path) {  
		String portString = PropertiesParser.value("RMI.port");
		if (portString == null) {
			try {
				PropertiesParser.loadProperties(path);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		portString = PropertiesParser.value("RMI.port");
		if (portString != null) {
			port = Integer.valueOf(portString);
		}
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public void startup() {
		try {
			serverSocket = new ServerSocket(port);
			goon = true;
			new Thread(this, "RMI-Server").start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void shutdown() {
		goon = false;
		if (serverSocket != null) {
			try {
				if (!serverSocket.isClosed()) {
					serverSocket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				serverSocket = null;
			}
		}
	}

	@Override
	public void run() {
		while (goon) {
			try {
				Socket client = serverSocket.accept();
				new ThreadPoolExecutor(50, 80, 2, TimeUnit.SECONDS, new SynchronousQueue<>())
						 			  .execute(new RMIInvoker(client,gson));

			} catch (IOException e) {
				if (goon != false) {
					goon = false;
					e.printStackTrace();
				}
			}
		}
	}
	
}
