package org.fh.plugins.bulletchat;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.java_websocket.WebSocket;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

/**
 * 说明：视频弹幕服务端
 * 作者：FH Admin  3 13 596790
 * 官网：www.fhadmin.org
 */
public class BulletChatServer extends WebSocketServer{

	public BulletChatServer(int port) throws UnknownHostException {
		super(new InetSocketAddress(port));
	}

	public BulletChatServer(InetSocketAddress address) {
		super(address);
	}

	/**
	 * 客户端发送消息到服务器时触发事件
	 */
	@Override
	public void onMessage(WebSocket conn, String message){
		message = message.toString();
		if(null != message && message.startsWith("[video313596790]")){
			this.userjoin(message.replaceFirst("\\[video313596790\\]", ""),conn);
		}else{
			BulletChatServerPlool.sendMessage(message.toString());//向所有在线用户发送消息
		}
	}

	public void onFragment( WebSocket conn, Framedata fragment ) {
	}
	
	/**
	 * 用户加入处理
	 * @param user
	 */
	public void userjoin(String user, WebSocket conn){
		BulletChatServerPlool.addUser(user,conn);							//向连接池添加当前的连接对象
	}
	
	/**
	 * 用户下线处理
	 * @param user
	 */
	public void userLeave(WebSocket conn){
		BulletChatServerPlool.removeUser(conn);							 	//在连接池中移除连接
	}
	
	/**
	 * 触发连接事件
	 */
	@Override
	public void onOpen( WebSocket conn, ClientHandshake handshake ) {}

	/**
	 * 触发关闭事件
	 */
	@Override
	public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
		userLeave(conn);
	}
	
	/**
	 * 触发异常事件
	 */
	@Override
	public void onError( WebSocket conn, Exception ex ) {
		if( conn != null ) {}
	}
	
	@Override
	public void onStart() {}

}
