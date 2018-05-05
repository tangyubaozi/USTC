package autoClient;


import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

/**
 * �ļ���¼��
 * �������ڲ���һЩ��Ҫ�ļ�¼���ɶԼ�¼�����ļ������ȡ�ȡ�
 * ��Ҫ�Ե�һ��Ŀ�ļ�¼��Ϣ���������࣬�����ļ�¼����Ҫʵ��Recordable�ӿڣ�
 * ��¼�Զ������ļ�����ʽ���棻
 * ͬʱ��¼��ȡ��һList��������Ӧ���д��ݣ�
 * ��¼�ļ�����һ����ѡ���ļ���ͷ����String����ʽ���м�¼
 * @author Ttyy
 *
 * @param <T> �������������ڱ��浥һ��¼����
 */
public class Recorder<T extends Recordable>{
	
	private Path path;
	private String startOfRecord;
	private int lengthOfStart = 0;
	private List<T> list;
	private boolean Update = false;	//�Ƿ��и��µĿ���
	/**���͵�������������ʵ��������*/
	private Class<T> T0;		
	private boolean isHidden = false;//��¼�ļ��Ƿ���Ϊ�����ļ�
	private boolean isClearText = false;//��¼���������ַ��洢����Ϊ�Զ����ƴ洢
	
	private static final String FILE_HEAD = "START#";
	
	public Recorder(Path file, Class<T> kind){
		this.path = file;
		this.T0 = kind;
		list = new ArrayList<>();
		try {
			if(!Files.exists(path)){	//���û���ļ��򴴽�
				Files.createFile(path);
				if(isHidden)	//����DOS�����ļ���������
					Runtime.getRuntime().exec( "attrib +H \"" + path + "\"");
			}else{
				readRecording();
			}
							
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * ���ļ���ȡ��¼
	 */
	public void readRecording(){
		try (DataInputStream in = new DataInputStream(new FileInputStream(path.toFile()));){
			T t;
			lengthOfStart = in.readInt();
			if((lengthOfStart < 0)||(lengthOfStart>10000)){
				throw new IllegalArgumentException("�����ļ��Ŀ�ͷ���ֳ���");
			}
			if(lengthOfStart != 0){
				startOfRecord = Tools.readFixedString(lengthOfStart, in);
			}				
			while((t = getFromDataInput(in))!=null){
				list.add(t);
			}	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException | IllegalArgumentException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "�ļ���ʧ�ܣ�������ѡ���ļ���");
			return;
		} 
	}
	
	private void readByString(){
		try{
			Files.lines(path);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("finally")
	private T getFromDataInput(DataInput in){
		T t = null;
		try {
			t = T0.newInstance();
			if(t.readFromDataInput(in));
			else t = null;	
		}catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}finally {
			return t;
		}		
	}
	
	/**
	 * ���ļ�д���¼
	 * @return	����д��ļ�¼������
	 */
	public int writeRecording(){
		try(RandomAccessFile out = new RandomAccessFile(path.toFile(),"rw");){	//д�������ļ�������Ҫ�����д��ķ�ʽ��
			out.seek(0);
			out.writeInt(lengthOfStart);
			if(lengthOfStart != 0)
				out.writeChars(startOfRecord);
			for (T t : list) {
				t.writeToDataOutput(out);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list.size();
	
	}
	
	/**
	 * ���ݼ�¼�ļ�Ϊ.old��ֻ�������һ��
	 */
	private void backUp(){
		try {
			Path old_file = Paths.get(path.toString(),".old");				
			if(Files.exists(old_file))
				Files.delete(path);
			Files.move(path, old_file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
	 * ���Ӽ�¼��Ŀ
	 * @param t	��Ҫ��ӵ���Ŀ
	 */
	public void add(T t){
		list.add(t);
		Update = true;
	}
	
	public int setStartOfRecord(String s){
		startOfRecord = s;
		return lengthOfStart = s.length();
	}
	public void setHidden(boolean b){
		isHidden = b;
	}
	
	public List<T> getList(){
		return list;
	}
}
