package cn.mldn.mldnzk.test;

import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

public class TestZooKeeperConnectNodeWatch {
	// 定义要连接所有的主机名称（是在一个ZooKeeper集群之中的主机）
	private static final String ZK_SERVER_URL = "192.168.68.136:2181,192.168.68.137:2181,192.168.68.138" ;
	private static final int SESSION_TIMEOUT = 100 ;	// 设置统一的超时时间
	private static final String AUTH_INFO = "zkuser:mldnjava" ; // 认证信息
	private static final String GROUPNODE = "/mldndata"; // 创建的公共的父节点
	private ZooKeeper zkClient ;
	
	@Test 
	public void watchSubNode() throws Exception {	// 连接的时候一定会出现异常信息
		this.getConnection(); // 连接处理
		// 创建一个持久化节点，但是在创建之前一定要首先判断节点是否存在 
		if (this.zkClient.exists(GROUPNODE, false) == null) {	// 现在父节点不存在
			// 创建一个持久化的节点
			this.zkClient.create(GROUPNODE, "HelloMLDNData".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		} 
		// 获取节点数据，但是在获取的时候要求进行监听处理
		this.zkClient.exists(GROUPNODE, true) ;
		TimeUnit.DAYS.sleep(Long.MAX_VALUE); // 长时间休眠
		this.zkClient.close(); 	// 关闭ZkServer连接  
	} 
	
	@Test 
	public void watchNode() throws Exception {	// 连接的时候一定会出现异常信息
		this.getConnection(); // 连接处理
		// 创建一个持久化节点，但是在创建之前一定要首先判断节点是否存在 
		if (this.zkClient.exists(GROUPNODE, false) == null) {	// 现在父节点不存在
			// 创建一个持久化的节点
			this.zkClient.create(GROUPNODE, "HelloMLDNData".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		} 
		// 获取节点数据，但是在获取的时候要求进行监听处理
		Stat stat = new Stat() ;
		byte data [] = this.zkClient.getData(GROUPNODE, true, stat) ;
		System.out.println("【GET-DATA】获取数据：" + new String(data));
		TimeUnit.DAYS.sleep(Long.MAX_VALUE); // 长时间休眠
		this.zkClient.close(); 	// 关闭ZkServer连接  
	} 
	
	private void getConnection() throws Exception { // 定义一个专门负责连接的方法
		this.zkClient = new ZooKeeper(ZK_SERVER_URL, SESSION_TIMEOUT, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				System.err.println(event); // 输出连接的事件信息
				System.err.println("\t|- 事件产生路径：" + event.getPath());
				System.err.println("\t|- 事件类型：" + event.getType());
				System.err.println("\t|- 事件状态：" + event.getState());
				try {
					zkClient.exists(GROUPNODE, true) ;// 继续做监听
				} catch (Exception e) {
					e.printStackTrace();
				}	
			}}) ;
		this.zkClient.addAuthInfo("digest", AUTH_INFO.getBytes());
	}
	
}
