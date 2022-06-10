package org.fh.plugins.bulletchat;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.java_websocket.WebSocket;


/**
 * 说明：视频弹幕服务端
 * 作者：FH Admin  3 13 596790
 * 官网：www.fhadmin.org
 */
public class BulletChatServerPlool {

	private static final Map<WebSocket,String> userconnections = new HashMap<WebSocket,String>();
	
	/**
	 * 向连接池中添加连接
	 * @param inbound
	 */
	public static void addUser(String user, WebSocket conn){
		userconnections.put(conn,user);	//添加连接
	}
	
	/**
	 * 移除连接池中的连接
	 * @param inbound
	 */
	public static boolean removeUser(WebSocket conn){
		if(userconnections.containsKey(conn)){
			userconnections.remove(conn);	//移除连接
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 向所有的用户发送消息
	 * @param message
	 */
	public static void sendMessage(String message){
		Set<WebSocket> keySet = userconnections.keySet();
		synchronized (keySet) {
			for (WebSocket conn : keySet) {
				String user = userconnections.get(conn);
				String LIVEBROADCAST_ID = message.split(",fh,")[1];
				if(!user.contains(LIVEBROADCAST_ID))continue;	//排除非当前直播的视频页面
				if(user != null){
					conn.send(message);
				}
			}
		}
	}

}
