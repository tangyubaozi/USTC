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
 * �ļ�ɸѡ�ľ���ʵ��
 * ˼·�������ļ�����MD5ֵ����ʶ�ļ���ά��һ�ű��¼�����Ѿ�������ļ���
 * �Ƚ�Ŀ¼�µ��ļ�����м�¼���ж��ļ��Ĵ������
 * ɸѡ��δ������ļ���
 * @author Ttyy
 *
 */
public class DirFilter implements FileFilter{
	
//	private DataOutputStream out;
//	private DataInputStream in;
	private Path path;
	private List<FileConfig> fileconfigs;
	private Instant lastTime;
	private boolean Update = false;//�Ƿ��и��µĿ���
	private String fileSuffix = ".dat";
	
	
	public DirFilter() {
		path = Paths.get(MyProperties.getMyProperties().getSettings().getProperty(MyProperties.PATH_DIR));
		path = path.resolve("filesrecording.prop");
		fileconfigs = new ArrayList<>();
		lastTime = Instant.EPOCH;
		try {
			if(!Files.exists(path)){	//���û���ļ��򴴽�
				Files.createFile(path);
				//����DOS�����ļ���������
				Runtime.getRuntime().exec( "attrib +H \"" + path + "\"");
			}else{
				readRecording();
			}
				
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * �жϼ�¼�Ƿ��и���
	 * @return
	 */
	public boolean hasUpdate(){
		return Update;
	}
	
	/**
	 * ���ļ���ȡ��¼
	 */
	private void readRecording(){
		try (DataInputStream in = new DataInputStream(new FileInputStream(path.toFile()));){
			FileConfig fc;
			lastTime = Instant.ofEpochSecond(in.readLong());	//��ȡ�ļ���ͷ�����·���ʱ��
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
	 * ���ļ�д���¼
	 * @return	����д��ļ�¼������
	 */
	public int writeRecording(){	
		try(RandomAccessFile out = new RandomAccessFile(path.toFile(),"rw");){	//д�������ļ�������Ҫ�����д��ķ�ʽ��
			out.seek(0);
			out.writeLong(lastTime.getEpochSecond());	//���ļ���ͷд�����·���ʱ��
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
	 * ���¼�¼�������¼�иı䣬����²�д���ļ������ظ��º�ļ�¼������û�иı��򷵻�0��
	 * @return	����д��ļ�¼������
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
	 * �жϸ������ļ����Ƿ��Ѵ����ڼ�¼��
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
	 * �ҳ���¼��������ļ�����ͬ�ļ�¼
	 * @param name
	 * @return	���û�������null
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
	 * �жϸ�����MD5ֵ�Ƿ��Ѵ����ڼ�¼��
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
	 * �ҳ���¼�������MD5ֵ��ͬ�ļ�¼
	 * @param b
	 * @return	���û�������null
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
	 * ÿ��ɨ���ʱ���������ļ��ķ���ʱ����Ϊ�ж����ݣ����г�ʼ�жϣ�����ƥ���ļ��������û�м�¼�����ͣ�
	 * ����м�¼��������ļ���MD5ֵ�����MD5ֵ��ͬ������Ȼ���ͣ������ͬ����·���ʱ�䲻����
	 * �ļ�����ͬ����Ϊ�ǲ�ͬ�ļ����������ʱ�����MD5ֵ���¼��ͬ������Ȼ��¼�ļ��������ļ�����ʱ������ʾ��
	 * 
	 * ����Windows 7�У�����ʱ��Ĭ������Ϊ�رգ�������ʱ�䲻���ļ��޸Ķ��仯��ÿ������ϵͳ�Ĳ��Բ�ͬ����˲������ļ�ʱ����Ϊ�ж����ݡ�
	 */
	private boolean isNewFile(File pathname){
		byte[] MD5 = null;
		Instant time = Tools.getLastAccessTime(pathname);//��ȡ�ļ���������ʱ��
//		if(time.compareTo(lastTime)>0){	//�жϷ���ʱ���Ƿ�Ϊ����
			if(haveSameFileName(pathname.getName())){
				MD5 = Tools.getMD5(pathname);
				if(haveSameMD5(MD5)){//�ж�MD5ֵ�Ƿ����¼ֵ��ͬ
					lastTime = time;
					return false;
				}else return true;
			}else return true;
//		}else return false;
	
	}

	@Override
	/**
	 * FileFilter�ӿ��еķ������ļ�����������Ҫ�жϽ��
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
	/**	�����õ���char�ĳ��ȣ������byte��Ҫ����2 */
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
