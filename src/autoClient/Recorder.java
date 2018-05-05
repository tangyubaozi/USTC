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
 * 文件记录器
 * 该类用于操作一些需要的记录，可对记录进行文件保存读取等。
 * 需要对单一条目的记录信息单独创建类，创建的记录类需要实现Recordable接口；
 * 记录以二进制文件的形式保存；
 * 同时记录读取至一List中用于在应用中传递；
 * 记录文件具有一个可选的文件开头，以String的形式进行记录
 * @author Ttyy
 *
 * @param <T> 单独创建的用于保存单一记录的类
 */
public class Recorder<T extends Recordable>{
	
	private Path path;
	private String startOfRecord;
	private int lengthOfStart = 0;
	private List<T> list;
	private boolean Update = false;	//是否有更新的开关
	/**泛型的类型名，用于实例化类型*/
	private Class<T> T0;		
	private boolean isHidden = false;//记录文件是否设为隐藏文件
	private boolean isClearText = false;//记录是以明文字符存储，否为以二进制存储
	
	private static final String FILE_HEAD = "START#";
	
	public Recorder(Path file, Class<T> kind){
		this.path = file;
		this.T0 = kind;
		list = new ArrayList<>();
		try {
			if(!Files.exists(path)){	//如果没有文件则创建
				Files.createFile(path);
				if(isHidden)	//调用DOS设置文件隐藏属性
					Runtime.getRuntime().exec( "attrib +H \"" + path + "\"");
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
	 * 向文件写入记录
	 * @return	返回写入的记录的条数
	 */
	public int writeRecording(){
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
	
	/**
	 * 备份记录文件为.old；只备份最近一次
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
	public void setHidden(boolean b){
		isHidden = b;
	}
	
	public List<T> getList(){
		return list;
	}
}
