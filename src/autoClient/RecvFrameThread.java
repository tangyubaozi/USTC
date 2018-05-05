package autoClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 * 接收信息线程
 * @author Ttyy
 *
 */
public class RecvFrameThread implements Runnable {
	private ReadableByteChannel readChannel;
	private JTextArea textArea;
	
	public RecvFrameThread(ReadableByteChannel in) {
		readChannel = in;
		
	}
	public void setJTextArea(JTextArea textArea){
		this.textArea = textArea;
	}

	@Override
	public void run() {
		ByteBuffer buffer;
		byte XOR;
		try{
			
			while(!Thread.currentThread().isInterrupted())
			{
				buffer = readFromChannel(readChannel, 4);
				if(Arrays.equals(buffer.array(), DataFrame.FRAME_START)){//如果接收到的
					XOR = Tools.XORCheck(buffer.array());
					buffer = readFromChannel(readChannel, 4);
					XOR ^= Tools.XORCheck(buffer.array());
					int frame_length = buffer.getInt();
					frame_length -= 8;
					buffer = readFromChannel(readChannel, frame_length);
					XOR ^= Tools.XORCheck(buffer.array());
					if(XOR != 0){
						//帧数据异或校验出错，异常处理，需要补充处理方式
					}
					RecvBody recvBody = DataFrame.decodeFrame(buffer);
					textArea.append(recvBody.toString());
				}
				
				TimeUnit.SECONDS.sleep(1);
			}			
		
		}catch (ServerClosedChannelException e){
			JOptionPane.showMessageDialog(null, "服务器端已断开，请断开并重连!","提示",JOptionPane.ERROR_MESSAGE);
			return;
		}catch (InterruptedException | IOException e) {
//			e.printStackTrace();
			return;
		} 

	}
	
	/**
	 * 从通道中读取指定数量的字节,如果没有读到任何字节，则阻塞100ms，继续读取
	 * @param in 		读取通道
	 * @param length	字节长度
	 * @return			读取到的字节缓存，大小与字节长度相等
	 * @throws ServerClosedChannelException	服务器端连接断开
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public static ByteBuffer readFromChannel(ReadableByteChannel in,int length)
			throws InterruptedException, ServerClosedChannelException, IOException{
		ByteBuffer buffer = ByteBuffer.allocateDirect(length);
		int index = 0;
		int re = 0;
		while(index < length){
			re = in.read(buffer);
			if(re == -1){//如果read()的返回结果为-1，则证明服务器断开
				throw new ServerClosedChannelException();
			}
			index += re;
			if(index == 0){//如果没有读到数据，则证明暂时无数据传入，进入等待
				TimeUnit.MILLISECONDS.sleep(100);
			}
		}
		buffer.flip();
		return buffer;
	}
	
	/**
	 * 获取帧
	 * @return
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public int getFrame() throws IOException, InterruptedException{
		ByteBuffer buffer;
		buffer = readFromChannel(readChannel, 4);
		if(Arrays.equals(buffer.array(), DataFrame.FRAME_START)){
			int frame_length = readFromChannel(readChannel, 4).getInt();
			frame_length -= 8;
			buffer = readFromChannel(readChannel, frame_length);
			
		}else return -1;
		return -1;
	}

}
