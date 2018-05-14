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
	private boolean Update = false;	//是否有更新的开关
	/**泛型的类型名，用于实例化类型*/
	private Class<T> T0;
	private boolean hidden = false;//记录文件是否设为隐藏文件
	private boolean clearText = false;//记录是以明文字符存储，否为以二进制存储
	

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
			if(!Files.exists(path)){	//如果没有文件则创建
				createFile();
			}else{
				readRecording();
			}
							
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
			
	
	/**
	 * 从文件读取记录
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
				throw new IllegalArgumentException("读入文件的开头部分出错");
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
			JOptionPane.showMessageDialog(null, "文件打开失败，请重新选择文件！");
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
	 * 向文件写入记录
	 * @return	返回写入的记录的条数
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
		try(RandomAccessFile out = new RandomAccessFile(path.toFile(),"rw");){	//写入隐藏文件内容需要用随机写入的方式打开
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
		if(hidden)	//调用DOS设置文件隐藏属性
			Runtime.getRuntime().exec( "attrib +H \"" + path + "\"");	
	}
	
	/**
	 * 备份记录文件为.old；只备份最近一次
	 */
	public void backUp() throws IOException{
		Path old_file = Paths.get(path.toString(),".old");				
		if(Files.exists(old_file))
			Files.delete(path);
		Files.move(path, old_file);
		createFile();
	}
	
	/**
	 * 判断记录是否有更新
	 * @return
	 */
	public boolean hasUpdate(){
		return Update;
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
	 * 增加记录条目
	 * @param t	需要添加的条目
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
