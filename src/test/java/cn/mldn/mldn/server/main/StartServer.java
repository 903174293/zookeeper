package cn.mldn.mldn.server.main;

import java.net.InetAddress;

import cn.mldn.mldn.server.ServerListener;

public class StartServer {
	public static void main(String[] args) throws Exception {
		String ip = InetAddress.getLocalHost().getHostAddress() ;	// 获得本机的IP地址
		new ServerListener(ip) ;
	}
}
