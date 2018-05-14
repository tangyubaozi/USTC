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
	 * ���¼�¼�������¼�иı䣬����²�д���ļ������ظ��º�ļ�¼������û�иı��򷵻�0��
	 * @return	����д��ļ�¼������
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
	 * �жϸ������ļ����Ƿ��Ѵ����ڼ�¼��
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
	 * �ҳ���¼��������ļ�����ͬ�ļ�¼
	 * @param name
	 * @return	���û�������null
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
	 * �жϸ�����MD5ֵ�Ƿ��Ѵ����ڼ�¼��
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
	 * �ҳ���¼�������MD5ֵ��ͬ�ļ�¼
	 * @param b
	 * @return	���û�������null
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
