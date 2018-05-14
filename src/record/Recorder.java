package record;


import java.awt.TexturePaint;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.JOptionPane;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.IconifyAction;

import autoClient.Tools;

public class Recorder<T extends Recordable> {
	
	private Path path;
	private String startOfRecord;
	private int lengthOfStart = 0;
	private List<T> list;
	private boolean Update = false;	//�Ƿ��и��µĿ���
	/**���͵�������������ʵ��������*/
	private Class<T> T0;
	private boolean hidden = false;//��¼�ļ��Ƿ���Ϊ�����ļ�
	private boolean clearText = false;//��¼���������ַ��洢����Ϊ�Զ����ƴ洢
	

	public static final int HIDDEN = 1;
	public static final int TEXT = 2;
	
	public Recorder(Path path, Class<T> kind, int ... args){
		this.path = path;
		this.T0 = kind;	
		if(args.length != 0){
			int flag = IntStream.of(args).sum();
			setHidden((flag & 0x1) == 1 ? true: false);
			setClearText((flag & 0x2) == 2 ? true: false);
		}
		list = new ArrayList<>();
		try {
			if(!Files.exists(path)){	//���û���ļ��򴴽�
				createFile();
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
		if(clearText){
			readFromString();
		}else{
			readFromData();
		}
	}
	
	private void readFromData(){
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
	
	private void readFromString(){
		List<String> slist;
		try {
			slist = Files.readAllLines(path);
			setStartOfRecord(slist.get(0));
			T t = null;
			for (int i = 1; i < slist.size(); i++) {
				t = T0.newInstance();
				if(t.readString(slist.get(i))){
					list.add(t);
				}
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
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
		if(hidden){
			try {
				if(!Files.isHidden(path))
					Runtime.getRuntime().exec( "attrib +H \"" + path + "\"");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(clearText){
			return writeToString();
		}else{
			return writeToData();
		}
		
	}
	
	private int writeToData(){
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
	
	private int writeToString(){
		List<String> li = new ArrayList<>(list.size()+1);
		if(startOfRecord == null){
			SimpleDateFormat bartDateFormat =
	                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ ");
			setStartOfRecord("Create in " + bartDateFormat.format(new Date(Calendar.getInstance().getTimeInMillis())));
		}
		li.add(startOfRecord);
		list.stream()
			.map(T::toString)
			.forEach(e->li.add(e));
		try {
			Files.write(path, li);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return list.size();
	}
	
	private void createFile() throws IOException{
		Files.createFile(path);
		if(hidden)	//����DOS�����ļ���������
			Runtime.getRuntime().exec( "attrib +H \"" + path + "\"");	
	}
	
	/**
	 * ���ݼ�¼�ļ�Ϊ.old��ֻ�������һ��
	 */
	public void backUp() throws IOException{
		Path old_file = Paths.get(path.toString(),".old");				
		if(Files.exists(old_file))
			Files.delete(path);
		Files.move(path, old_file);
		createFile();
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
	
	public String getStartOfRecord(){
		return startOfRecord;
	}
	
	public void setHidden(boolean b){
		hidden = b;
	}
	public boolean isClearText() {
		return clearText;
	}


	public void setClearText(boolean isClearText) {
		this.clearText = isClearText;
	}

	
	public List<T> getList(){
		return list;
	}
	
	public static void main(String[] args) {
		Path path = Paths.get(".").resolve("test.txt");
		AAA a = new AAA();
		Recorder<AAA> rrr = new Recorder<>(path, AAA.class, Recorder.HIDDEN,Recorder.TEXT);
		rrr.add(a);
		rrr.writeRecording();	
	}
}

class AAA extends RecordAdapter{
	String s = "ni hao";
	@Override
	public String toString() {	
		return s;
	}
	@Override
	public boolean readString(String s) {
		this.s = s;
		return true;
	}

}
