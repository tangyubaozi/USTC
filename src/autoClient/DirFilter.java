package autoClient;


import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import record.Recorder;

/**
 * 文件筛选的具体实现
 * 思路是利用文件名和MD5值来标识文件，维护一张表记录所有已经传输的文件；
 * 比较目录下的文件与表中记录，判断文件的传输与否；
 * 筛选出未传输的文件。
 * @author Ttyy
 *
 */
public class DirFilter implements FileFilter{
	
//	private DataOutputStream out;
//	private DataInputStream in;
	private Path path;
	private List<FileConfig> fileconfigs;
	private Instant lastTime;
	private String fileSuffix = ".dat";
	private Recorder<FileConfig> recorder;
	SimpleDateFormat bartDateFormat =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ ");
	
	public DirFilter() {
		path = Paths.get(MyProperties.getMyProperties().getSettings().getProperty(MyProperties.PATH_DIR));
		path = path.resolve("filesrecording.prop");
		recorder = new Recorder<>(path, FileConfig.class, Recorder.HIDDEN);
		fileconfigs = recorder.getList();
		try {
			lastTime = (recorder.getStartOfRecord() == null) ? Instant.EPOCH 
					: Instant.ofEpochMilli(bartDateFormat.parse(recorder.getStartOfRecord()).getTime());
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
	}
	

	/**
	 * 更新记录，如果记录有改变，则更新并写入文件，返回更新后的记录条数；没有改变则返回0；
	 * @return	返回写入的记录的条数
	 */
	public int updateRecording(){
		Date date = new Date(lastTime.toEpochMilli());
		        
		recorder.setStartOfRecord(bartDateFormat.format(date));
		return recorder.updateRecording();		
	}
	
	public void addFileConfig(File name, byte[] MD5){
		FileConfig fc = new FileConfig(name, MD5);
		recorder.add(fc);
		Instant time = Tools.getLastAccessTime(name);
		lastTime = time.compareTo(lastTime)>0 ? time:lastTime;
		
		
	}
	
	/**
	 * 判断给定的文件名是否已存在于记录中
	 * @param name
	 * @return
	 */
	private boolean haveSameFileName(String name){
		for (FileConfig fileConfig : fileconfigs) {
			if(fileConfig.name.equals(name)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 找出记录中与给定文件名相同的记录
	 * @param name
	 * @return	如果没有则输出null
	 */
	private FileConfig getSameNameFileConfig(String name){
		for (FileConfig fileConfig : fileconfigs) {
			if(fileConfig.name.equals(name)){
				return fileConfig;
			}
		}
		return null;
	}
	
	/**
	 * 判断给定的MD5值是否已存在于记录中
	 * @param b
	 * @return
	 */
	private boolean haveSameMD5(byte[] b){
		for (FileConfig fileConfig : fileconfigs) {
			if(MessageDigest.isEqual(fileConfig.MD5, b)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 找出记录中与给定MD5值相同的记录
	 * @param b
	 * @return	如果没有则输出null
	 */
	private FileConfig getSameMD5FileConfig(byte[] b){
		for (FileConfig fileConfig : fileconfigs) {
			if(MessageDigest.isEqual(fileConfig.MD5, b)){
				return fileConfig;
			}
		}
		return null;
	}


	/**
	 * 每次扫描的时候都首先用文件的访问时间作为判断依据，进行初始判断，接着匹配文件名，如果没有记录，则发送，
	 * 如果有记录，则计算文件的MD5值，如果MD5值不同，则依然发送；如果相同则更新访问时间不发送
	 * 文件名不同则认为是不同文件，如果发送时计算的MD5值与记录相同，则依然记录文件，并在文件传输时有所表示。
	 * 
	 * ！！Windows 7中，访问时间默认设置为关闭，即访问时间不随文件修改而变化。每个操作系统的策略不同，因此不能以文件时间作为判断依据。
	 */
	private boolean isNewFile(File pathname){
		byte[] MD5 = null;
		Instant time = Tools.getLastAccessTime(pathname);//读取文件的最后访问时间
//		if(time.compareTo(lastTime)>0){	//判断访问时间是否为最新
			if(haveSameFileName(pathname.getName())){
				MD5 = Tools.getMD5(pathname);
				if(haveSameMD5(MD5)){//判断MD5值是否与记录值相同
					lastTime = time;
					return false;
				}else return true;
			}else return true;
//		}else return false;
	
	}

	@Override
	/**
	 * FileFilter接口中的方法，文件过滤器的主要判断结果
	 */
	public boolean accept(File pathname) {
		if (pathname.isFile() && pathname.getName().toLowerCase().endsWith(fileSuffix)){
			if(isNewFile(pathname)){
				return true;
			}
		}
		return false;	
	}
	
	public static void main(String[] args) {
		DirFilter df = new DirFilter();
		MyProperties myProperties = MyProperties.getMyProperties();
		String string = myProperties.getSettings().getProperty(MyProperties.PATH_DIR);
		File f = new File(string);
		File[] files = f.listFiles(df);
		for (File file : files) {
			df.addFileConfig(file, Tools.getMD5(file));
		}
		df.updateRecording();
		System.out.println("Ok");
		
	}
	
	
}
