package autoClient;

import java.net.ConnectException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Properties;
/**
 * 通信选择类
 * 用于连接通信方式和软件实现，通过MyProperties定义通信方式
 * 预期的通信方式有两种，TCP/IP协议和串口通信
 * 串口通信尚未实现
 * @author Ttyy
 *
 */
public class CommConnection {
	
	private static CommConnection connection;
	private CommByTCP tcp;
	
	private CommConnection() throws ConnectException {
		Properties setting = MyProperties.getMyProperties().getSettings();
		switch(setting.getProperty(MyProperties.COMM_MODE)){
		case MyProperties.TCP_IP:
			tcp = new CommByTCP();
			break;
		case MyProperties.SERIAL_PORT:
			break;
		default:
			break;
			
		}
	}
	
	public void closeConnect(){
		tcp.closeSocket();
		connection = null;
	}
	
	public WritableByteChannel getOutput(){
		return tcp.getSocketChannel();
	}
	
	public ReadableByteChannel getInput(){
		return tcp.getSocketChannel();
	}
	
	public static CommConnection getConnection() throws ConnectException{
		if(connection == null)
			connection = new CommConnection();
		return connection;
	}

}
