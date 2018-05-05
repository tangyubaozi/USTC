package autoClient;


import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
	private boolean Update = false;//是否有更新的开关
	private String fileSuffix = ".dat";
	
	
	public DirFilter() {
		path = Paths.get(MyProperties.getMyProperties().getSettings().getProperty(MyProperties.PATH_DIR));
		path = path.resolve("filesrecording.prop");
		fileconfigs = new ArrayList<>();
		lastTime = Instant.EPOCH;
		try {
			if(!Files.exists(path)){	//如果没有文件则创建
				Files.createFile(path);
				//调用DOS设置文件隐藏属性
				Runtime.getRuntime().exec( "attrib +H \"" + path + "\"");
			}else{
				readRecording();
			}
				
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 判断记录是否有更新
	 * @return
	 */
	public boolean hasUpdate(){
		return Update;
	}
	
	/**
	 * 从文件读取记录
	 */
	private void readRecording(){
		try (DataInputStream in = new DataInputStream(new FileInputStream(path.toFile()));){
			FileConfig fc;
			lastTime = Instant.ofEpochSecond(in.readLong());	//读取文件开头的最新访问时间
			while((fc = FileConfig.getFromDataInput(in))!=null){
				fileconfigs.add(fc);
				if(lastTime.compareTo(fc.time)<0){
					lastTime = fc.time;
				}
			}	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
//			fileconfigs = null;
			return;
		} 
	}
	
	/**
	 * 向文件写入记录
	 * @return	返回写入的记录的条数
	 */
	public int writeRecording(){	
		try(RandomAccessFile out = new RandomAccessFile(path.toFile(),"rw");){	//写入隐藏文件内容需要用随机写入的方式打开
			out.seek(0);
			out.writeLong(lastTime.getEpochSecond());	//在文件开头写入最新访问时间
			for (FileConfig fileConfig : fileconfigs) {
				fileConfig.writeToDataOutput(out);
			}
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return fileconfigs.size();
	
	}
	/**
	 * 更新记录，如果记录有改变，则更新并写入文件，返回更新后的记录条数；没有改变则返回0；
	 * @return	返回写入的记录的条数
	 */
	public int updateRecording(){
		if(Update){
			Update = false;
			return writeRecording();
		}else{
			return 0;
		}
	}
	
	/**
	 * 判断给定的文件名是否已存在于记录中
	 * @param name
	 * @return
	 */
	public boolean haveSameFileName(String name){
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
	public FileConfig getSameNameFileConfig(String name){
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
	public boolean haveSameMD5(byte[] b){
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
	public FileConfig getSameMD5FileConfig(byte[] b){
		for (FileConfig fileConfig : fileconfigs) {
			if(MessageDigest.isEqual(fileConfig.MD5, b)){
				return fileConfig;
			}
		}
		return null;
	}
	
	public void addFileConfig(File name, byte[] MD5){
		FileConfig fc = new FileConfig(name, MD5);
		fileconfigs.add(fc);
		Instant time = Tools.getLastAccessTime(name);
		lastTime = time.compareTo(lastTime)>0 ? time:lastTime;
		Update = true;
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

class FileConfig implements Recordable, Comparable<FileConfig>{
	
	public String name;
	public byte[] MD5;
	public Instant time;
	/**	这里用的是char的长度，换算成byte需要乘以2 */
	public static final int NAMECHARSIZE = 128;
	
	public FileConfig(){
		this.MD5 = new byte[16];
	}
		
	public FileConfig(String name, byte[] MD5, long time) {
		this.name = name;
		this.MD5 = MD5;
		this.time = Instant.ofEpochSecond(time);
	}
	
	
	public FileConfig(File file, byte[] MD5){
		this.name = file.getName();
		this.MD5 = MD5;
		this.time = Tools.getLastAccessTime(file);
	}
	
	
	
	public static FileConfig getFromDataInput(DataInput in){
		FileConfig fc = new FileConfig();
		if(fc.readFromDataInput(in));
		else fc = null;
		return fc;
	}
	
	@Override
	public boolean readFromDataInput(DataInput in){
		try {
			name = readName(in);
			in.readFully(MD5);
			time = Instant.ofEpochSecond(in.readLong());
			return true;
		} catch (IOException e) {
			//e.printStackTrace();
			return false;
		}
	}
	@Override
	public void writeToDataOutput(DataOutput out){
		try {
			writeName(out);
			out.write(MD5);
			out.writeLong(time.getEpochSecond());
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	@Override
	public FileConfig newInstance(){
		return new FileConfig();
	}
	
	
	public String readName(DataInput in) throws IOException{
		return Tools.readFixedString(NAMECHARSIZE, in);
	}
	public void writeName(DataOutput out) throws IOException{
		Tools.writeFixedString(name, NAMECHARSIZE, out);
		
	}
	@Override
	public int compareTo(FileConfig other){
		return this.time.compareTo(other.time);
	}
	
}
