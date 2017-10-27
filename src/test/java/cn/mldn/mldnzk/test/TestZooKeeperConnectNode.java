package cn.mldn.mldnzk.test;

import java.util.Iterator; 
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

public class TestZooKeeperConnectNode {
	// 定义要连接所有的主机名称（是在一个ZooKeeper集群之中的主机）
	private static final String ZK_SERVER_URL = "192.168.68.136:2181,192.168.68.137:2181,192.168.68.138" ;
	private static final int SESSION_TIMEOUT = 100 ;	// 设置统一的超时时间
	private static final String AUTH_INFO = "zkuser:mldnjava" ; // 认证信息
	private static final String GROUPNODE = "/mldndata"; // 创建的公共的父节点
	private static final String SUBNODE = GROUPNODE + "/bigdata" ; // 创建子节点
	@Test
	public void createEphemeralNode() throws Exception {	// 连接的时候一定会出现异常信息
		ZooKeeper zkClient = this.getConnection() ;	// 获取一个数据库的连接对象
		// 创建一个持久化节点，但是在创建之前一定要首先判断节点是否存在 
		if (zkClient.exists(GROUPNODE, false) == null) {	// 现在父节点不存在
			// 创建一个持久化的节点
			zkClient.create(GROUPNODE, "HelloMLDNData".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		if (zkClient.exists(SUBNODE, false) == null) {	// 现在子节点不存在
			zkClient.create(SUBNODE, "HelloMLDNData-temp".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		} 
		TimeUnit.SECONDS.sleep(5); // 执行完成后先不要立刻关闭，而是要等待5秒的时间
		zkClient.close(); 	// 关闭ZkServer连接 
	} 
	 
	@Test
	public void createPersistentNode() throws Exception {	// 连接的时候一定会出现异常信息
		ZooKeeper zkClient = this.getConnection() ;	// 获取一个数据库的连接对象
		// 创建一个持久化节点，但是在创建之前一定要首先判断节点是否存在 
		if (zkClient.exists(GROUPNODE, false) == null) {	// 现在父节点不存在
			// 创建一个持久化的节点
			zkClient.create(GROUPNODE, "HelloMLDNData".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		zkClient.close(); 	// 关闭ZkServer连接 
	} 
	private ZooKeeper getConnection() throws Exception { // 定义一个专门负责连接的方法
		ZooKeeper zkClient = new ZooKeeper(ZK_SERVER_URL, SESSION_TIMEOUT, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				System.err.println(event); // 输出连接的事件信息
			}}) ;
		zkClient.addAuthInfo("digest", AUTH_INFO.getBytes());
		return zkClient ;
	}
	
}
