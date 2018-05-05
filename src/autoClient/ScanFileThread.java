package autoClient;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
/**
 * �ļ�ɨ���߳�
 * ʵ��OutputFrameQueue�ӿڣ�������Ϣ֡���
 * @author Ttyy
 *
 */
public class ScanFileThread implements Runnable, OutputFrameQueue {
	private Properties settings;
	private Path pathdir;
	private DirFilter dirFilter;
	
	public ScanFileThread() {		
		settings = MyProperties.getMyProperties().getSettings();
		pathdir = Paths.get(settings.getProperty("PathDir"));
		dirFilter = new DirFilter();
	}
	

	@Override
	public void run() {
		try {
			while(!Thread.currentThread().isInterrupted())
			{
				//ɨ��Ŀ¼�µ��ļ����õ������ļ�����
				File[] files = pathdir.toFile().listFiles(dirFilter);
				if(files!=null){
					for (File file : files) {
						byte[] md5 = Tools.getMD5(file);
						//���֡���������֡������
						OutputQueue.put(new DataFrame(file, md5));
						//���ļ�ɨ���¼����Ӽ�¼
						dirFilter.addFileConfig(file, md5);	
					}
					dirFilter.updateRecording();
				}
				TimeUnit.SECONDS.sleep(1);
			}
			dirFilter.updateRecording();
		} catch (InterruptedException e) {	
//			e.printStackTrace();
			dirFilter.updateRecording();
			return;
		}

	}
	

}
