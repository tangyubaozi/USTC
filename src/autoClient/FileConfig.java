package autoClient;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.time.Instant;

import record.RecordAdapter;

public class FileConfig extends RecordAdapter implements Comparable<FileConfig>{
	
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