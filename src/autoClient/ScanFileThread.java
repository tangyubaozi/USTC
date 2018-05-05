package autoClient;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
/**
 * 文件扫描线程
 * 实现OutputFrameQueue接口，用于信息帧输出
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
				//扫描目录下的文件，得到新增文件数组
				File[] files = pathdir.toFile().listFiles(dirFilter);
				if(files!=null){
					for (File file : files) {
						byte[] md5 = Tools.getMD5(file);
						//打包帧，并添加至帧队列中
						OutputQueue.put(new DataFrame(file, md5));
						//在文件扫描记录中添加记录
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
