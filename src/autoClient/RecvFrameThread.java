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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 * ������Ϣ�߳�
 * @author Ttyy
 *
 */
public class RecvFrameThread implements Runnable {
	private ReadableByteChannel readChannel;
	private MainFrame mainFrame;
	

	public RecvFrameThread(ReadableByteChannel in) {
		readChannel = in;
		
	}

	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}
	
	@Override
	public void run() {
		ByteBuffer buffer;
		byte XOR;
		try{
			
			while(!Thread.currentThread().isInterrupted())
			{
				buffer = readFromChannel(readChannel, 4);
				if(Arrays.equals(buffer.array(), DataFrame.FRAME_START)){//������յ���
					XOR = Tools.XORCheck(buffer.array());
					buffer = readFromChannel(readChannel, 4);
					XOR ^= Tools.XORCheck(buffer.array());
					int frame_length = buffer.getInt();
					frame_length -= 8;
					buffer = readFromChannel(readChannel, frame_length);
					XOR ^= Tools.XORCheck(buffer.array());
					if(XOR != 0){
						//֡�������У������쳣������Ҫ���䴦��ʽ
					}
					RecvBody recvBody = DataFrame.decodeFrame(buffer);
					if(recvBody.mod == DataFrame.TRANSFER_FILE){
						String sp = MyProperties.getMyProperties().getSettings().getProperty(MyProperties.PATH_DIR);
						Path path = Paths.get(sp).resolve("recv").resolve(recvBody.file_name);
//						Files.createFile(path);
						buffer = readFromChannel(readChannel, (int)recvBody.file_length);
						//�����ļ��ᱻ����
						FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.WRITE,StandardOpenOption.CREATE);
						fileChannel.write(buffer);
						OutputFrameQueue.OutputQueue.add(new DataFrame(path.toFile(), true));
					}
					mainFrame.appendTextAreaShow(recvBody.toString());
					Procedure.getProcedure().recive(recvBody);
					mainFrame.updateText();
				}
				
				TimeUnit.SECONDS.sleep(1);
			}			
		
		}catch (ServerClosedChannelException e){
			JOptionPane.showMessageDialog(null, "���������ѶϿ�����Ͽ�������!","��ʾ",JOptionPane.ERROR_MESSAGE);
			return;
		}catch (InterruptedException | IOException e) {
//			e.printStackTrace();
			return;
		} 

	}
	
	/**
	 * ��ͨ���ж�ȡָ���������ֽ�,���û�ж����κ��ֽڣ�������100ms��������ȡ
	 * @param in 		��ȡͨ��
	 * @param length	�ֽڳ���
	 * @return			��ȡ�����ֽڻ��棬��С���ֽڳ������
	 * @throws ServerClosedChannelException	�����������ӶϿ�
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public static ByteBuffer readFromChannel(ReadableByteChannel in,int length)
			throws InterruptedException, ServerClosedChannelException, IOException{
		ByteBuffer buffer = ByteBuffer.allocate(length);
		int index = 0;
		int re = 0;
		while(index < length){
			re = in.read(buffer);
			if(re == -1){//���read()�ķ��ؽ��Ϊ-1����֤���������Ͽ�
				throw new ServerClosedChannelException();
			}
			index += re;
			if(index == 0){//���û�ж������ݣ���֤����ʱ�����ݴ��룬����ȴ�
				TimeUnit.MILLISECONDS.sleep(100);
			}
		}
		buffer.flip();
		return buffer;
	}
	
	/**
	 * ��ȡ֡
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
