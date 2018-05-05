package autoClient;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.TimeUnit;

/**
 * �ļ������߳�
 * �ļ������ʽ��256�ֽ��ļ���+8�ֽ��ļ�����+�ļ�����
 * @author Ttyy
 *
 */
public class SendFrameThread implements Runnable, OutputFrameQueue {
	
	private WritableByteChannel writeChannel;
	
	public SendFrameThread(WritableByteChannel out) {
		writeChannel = out;	
		File file = new File("C:\\Users\\Ttyy\\workspace\\QKDAutoClient\\datas\\2111 - ���� (3).dat");
		OutputQueue.add(new DataFrame(file, Tools.getMD5(file)));
	}
	

	@Override
	public void run() {

		try{
			DataFrame frame;
			while(!Thread.currentThread().isInterrupted())
			{
				if((frame = OutputQueue.poll()) != null){
					writeChannel.write(frame.getBuffer());
					if(frame.getFile()!=null){
						FileChannel fileInChannel = FileChannel.open(frame.getFile().toPath());
						fileInChannel.transferTo(0, fileInChannel.size(), writeChannel);	
					}
					TimeUnit.SECONDS.sleep(1);
				}
			}
//			closeSocket();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
//			closeSocket();
			return;
		} 
	
	}

}
