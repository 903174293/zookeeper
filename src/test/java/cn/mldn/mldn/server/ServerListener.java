package cn.mldn.mldn.server;

import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

public class ServerListener {
	private static final String ZK_SERVER_URL = "192.168.68.136:2181,192.168.68.137:2181,192.168.68.138" ;
	private static final int SESSION_TIMEOUT = 100 ;	// 设置统一的超时时间
	private static final String AUTH_INFO = "zkuser:mldnjava" ; // 认证信息
	private static final String GROUPNODE = "/mldn-server"; // 创建的公共的父节点
	private static final String SUBNODE = GROUPNODE + "/server-"; // 创建瞬时子节点的名称
	private ZooKeeper zkClient = null ;
	public ServerListener(String ip) throws Exception { // 构造方法连接ZooKeeper
		this.getConnection(); // 进行连接
		this.handle(ip);
	}
	public void handle(String ip) throws Exception {	// 进行服务端捆绑处理
		if (this.zkClient.exists(GROUPNODE, false) == null) {	// 现在父节点不存在
			this.zkClient.create(GROUPNODE, "server-list".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT) ;
		}// 随后要创建临时子节点
		this.zkClient.create(SUBNODE, ip.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL) ;
		TimeUnit.DAYS.sleep(Long.MAX_VALUE); 
		this.zkClient.close(); 
	}
	
	private void getConnection() throws Exception { // 定义一个专门负责连接的方法
		this.zkClient = new ZooKeeper(ZK_SERVER_URL, SESSION_TIMEOUT, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
			}}) ;
		this.zkClient.addAuthInfo("digest", AUTH_INFO.getBytes());
	}
}
