package autoClient;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
import java.util.Properties;

/**
 * TCP协议通信实现类
 * @author Ttyy
 *
 */

public class CommByTCP {
		
	private MyProperties myProperties;
	private Properties settings;
	private Socket socket;	
	private SocketChannel socketChannel;
		
	public CommByTCP() throws ConnectException{
		
		try {
			myProperties = MyProperties.getMyProperties();
			settings = myProperties.getSettings();
			String ip = settings.getProperty(MyProperties.IP);
			String[] ipStrings = ip.split(":");
//			InetAddress ipAdress = InetAddress.getByName(ipStrings[0]);
			InetAddress ipAdress = InetAddress.getLocalHost();
			int port = Integer.valueOf(ipStrings[1]); 
			InetSocketAddress socketAddress = new InetSocketAddress(ipAdress, port);
			socketChannel = SocketChannel.open(socketAddress);
			socket = socketChannel.socket();
			
		} catch (UnknownHostException e) {
			myProperties.setIP();
			e.printStackTrace();
		}catch(ConnectException e){
			throw e;
		}catch (IOException e) {	
			System.out.println("服务器错误");
			e.printStackTrace();
			
		}			
	}
	
	public Socket getSocket() {
		return socket;
	}

	public SocketChannel getSocketChannel() {
		return socketChannel;
	}
	
	public void closeSocket(){
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();			
		}
	}
	
}
