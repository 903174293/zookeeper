package cn.mldn.mldnzk.test;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

public class TestZooKeeperConnectNodeData {
	// 定义要连接所有的主机名称（是在一个ZooKeeper集群之中的主机）
	private static final String ZK_SERVER_URL = "192.168.68.136:2181,192.168.68.137:2181,192.168.68.138" ;
	private static final int SESSION_TIMEOUT = 100 ;	// 设置统一的超时时间
	private static final String AUTH_INFO = "zkuser:mldnjava" ; // 认证信息
	private static final String GROUPNODE = "/mldndata"; // 创建的公共的父节点
	private static final String SUBNODE = GROUPNODE + "/bigdata" ; // 创建子节点
	
	@Test
	public void deleteNodeData() throws Exception {	// 连接的时候一定会出现异常信息
		ZooKeeper zkClient = this.getConnection() ;	// 获取一个数据库的连接对象
		// 创建一个持久化节点，但是在创建之前一定要首先判断节点是否存在 
		if (zkClient.exists(GROUPNODE, false) != null) {	// 节点必须存在才可以获取数据
			zkClient.delete(GROUPNODE, -1); 
		}
		zkClient.close(); 	// 关闭ZkServer连接 
	} 
	
	@Test
	public void setNodeData() throws Exception {	// 连接的时候一定会出现异常信息
		ZooKeeper zkClient = this.getConnection() ;	// 获取一个数据库的连接对象
		// 创建一个持久化节点，但是在创建之前一定要首先判断节点是否存在 
		if (zkClient.exists(GROUPNODE, false) != null) {	// 节点必须存在才可以获取数据
			zkClient.setData(GROUPNODE, "HelloMLDN".getBytes(), -1) ;
		}
		zkClient.close(); 	// 关闭ZkServer连接 
	} 
	@Test
	public void getNodeData() throws Exception {	// 连接的时候一定会出现异常信息
		ZooKeeper zkClient = this.getConnection() ;	// 获取一个数据库的连接对象
		// 创建一个持久化节点，但是在创建之前一定要首先判断节点是否存在 
		if (zkClient.exists(GROUPNODE, false) != null) {	// 节点必须存在才可以获取数据
			Stat stat = new Stat() ;	// 定义一个对象用于保存节点的相关信息
			byte [] data = zkClient.getData(GROUPNODE, false, stat) ;
			System.out.println("节点数据：" + new String(data));
			System.out.println("节点信息：" + stat); 
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
