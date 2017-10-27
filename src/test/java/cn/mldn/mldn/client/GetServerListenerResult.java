package cn.mldn.mldn.client;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class GetServerListenerResult {
	private static final String ZK_SERVER_URL = "192.168.68.136:2181,192.168.68.137:2181,192.168.68.138";
	private static final int SESSION_TIMEOUT = 100; // 设置统一的超时时间
	private static final String AUTH_INFO = "zkuser:mldnjava"; // 认证信息
	private static final String GROUPNODE = "/mldn-server"; // 创建的公共的父节点
	private static int count = 0;// 用来标记获取数据的次数
	private ZooKeeper zkClient = null;

	public GetServerListenerResult() throws Exception { // 构造方法连接ZooKeeper
		this.getConnection(); // 进行连接
		System.out.println("【初始化列表信息】" + this.updateServerList());
		TimeUnit.DAYS.sleep(Long.MAX_VALUE);
		this.zkClient.close();
	}

	// 获取列表信息，key = 节点名称、value = 主机的IP
	public Map<String, String> updateServerList() throws Exception {
		Map<String, String> allServers = new LinkedHashMap<>();
		List<String> list = this.zkClient.getChildren(GROUPNODE, true); // 需要继续进行监听控制
		Iterator<String> iter = list.iterator();
		while (iter.hasNext()) {
			String nodeName = iter.next();
			String subnode = GROUPNODE + "/" + nodeName;
			allServers.put(nodeName, new String(this.zkClient.getData(subnode, false, new Stat())));
		}
		return allServers;
	}

	private void getConnection() throws Exception { // 定义一个专门负责连接的方法
		this.zkClient = new ZooKeeper(ZK_SERVER_URL, SESSION_TIMEOUT, new Watcher() {
			@Override
			public void process(WatchedEvent event) { // 监听处理的
				if (event.getType().equals(EventType.NodeChildrenChanged)) { // 子节点改变
					try {
						System.out.println("第“" + count++ + "”次获取服务器列表数据：" + updateServerList());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		this.zkClient.addAuthInfo("digest", AUTH_INFO.getBytes());
	}
}
