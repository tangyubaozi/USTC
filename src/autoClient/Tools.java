package autoClient;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Calendar;
import static java.util.Calendar.*;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Properties;
import java.util.TimeZone;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
/**
 * 工具类，所有的方法为静态方法
 * 没有域
 * @author Ttyy
 *
 */
public final class Tools {
	
	public static String readFixedString(int size, DataInput in)
	throws IOException{
		StringBuilder b = new StringBuilder(size);
		int i = 0;
		boolean more = true;
		while(more && i < size){
			char ch = in.readChar();
			i++;
			if(ch == 0) more = false;
			else b.append(ch);
		}
		in.skipBytes(2*(size-i));
		return b.toString();
	}
	
	public static void writeFixedString(String s, int size, DataOutput out)
	throws IOException{
		for (int i = 0; i < size; i++) {
			char ch = 0;
			if(i<s.length()) ch = s.charAt(i);
			out.writeChar(ch);
		}
	}
	
	/**
	 * 使用通道进行文件传输，采用TCP协议和SocketChannel
	 * @param file
	 * @param socketChannel
	 */
	public static void fileToSocket(File file, SocketChannel socketChannel){
		ByteBuffer buffer = ByteBuffer.allocate(264);
		try(FileChannel fileInChannel = new FileInputStream(file).getChannel();){
			
			buffer.clear();
			buffer.put(file.getName().getBytes());
			buffer.putLong(256, file.length());
			buffer.flip();
			socketChannel.write(buffer);
			fileInChannel.transferTo(0, fileInChannel.size(), socketChannel);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	/**
	 * 传输文件，并在此过程中计算文件MD5值，
	 * @param file	需要传输的文件
	 * @param out	传输目的流 
	 * @return 		MD5值
	 */
	public static byte[] doMD5andTranslate(File file, OutputStream out){
		MessageDigest digest;
		byte[] bb = new byte[264];
		ByteBuffer buffer = ByteBuffer.allocate(264);
		try(BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));){
			buffer.clear();
			buffer.put(file.getName().getBytes());
			buffer.putLong(256, file.length());
			buffer.flip();
			buffer.get(bb);
			out.write(bb);
			digest = MessageDigest.getInstance("MD5");
			DigestOutputStream digestout = new DigestOutputStream(out, digest);
			digestout.on(true);
			byte[] bytes = new byte[1024];
			int i = 0;
			while ((i = in.read(bytes)) != -1) {
				digestout.write(bytes, 0, i);
	        }
			bb = digest.digest();
			digest.reset();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return bb;
	}
	
	public static byte[] getMD5(File file){
		MessageDigest digest;
		byte[] result = null;
		try(BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));){
			digest = MessageDigest.getInstance("MD5");
			byte[] bytes = new byte[1024];
			int i = 0;
			while ((i = in.read(bytes)) != -1) {
				digest.update(bytes, 0, i);
	        }
			result = digest.digest();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public static byte XORCheck(byte[] b){
		byte result = 0;
		for(int i = 0; i < b.length; i++){
			result ^= b[i];
		}
		return result;
	}
	
	public static Instant getLastAccessTime(File file){
		BasicFileAttributes bf = null;
		try {
			//读取文件属性
			bf = Files.readAttributes(file.toPath(),BasicFileAttributes.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bf.lastAccessTime().toInstant();//读取文件的最后访问时间
	}
	
	/**
	 * 字符串转字节，每集齐两个字符才能转换
	 * @param s		字符串
	 * @return		字节数组
	 */
	public static byte[] StringToByte(String s){
		int n = s.length()/2;
		byte[] b = new byte[n];
		char[] c = s.toUpperCase().toCharArray();
		for (int i = 0; i < n; i++) {
		    int pos = i * 2;
		    b[i] = (byte) (toByte(c[pos]) << 4 | toByte(c[pos + 1]));
		}
		return b;
	}
	public static int toByte(char c) {
	    byte b = (byte) "0123456789ABCDEF".indexOf(c);
	    return b;
	}
	
	public static void main(String[] args){

		System.out.println("ok");
	}

}

/**
 * 输入框JTextField限制字节数及过滤非16进制数据输入
 * @author Ttyy
 *
 */
class NumberLenghtLimitedDmt extends PlainDocument {
	 private int limit; 
	 public NumberLenghtLimitedDmt(int limit) {
	    super();
	       this.limit = limit;
	    }  
	 public void insertString(int offset, String  str, AttributeSet attr)
			   		throws BadLocationException {   
	       if (str == null){
	        return;
	       }
	       if ((getLength() + str.length()) <= limit) {
	       
	       char[] upper = str.toCharArray();
	       int length=0;
	       for (int i = 0; i < upper.length; i++) {       
	           if ((upper[i]>='0'&&upper[i]<='9')
	        		 || (upper[i]>='a'&&upper[i]<='f')
	        		 || (upper[i]>='A'&&upper[i]<='F')){           
	              upper[length++] = upper[i];
	           }
	       }
	         super.insertString(offset, new String(upper,0,length), attr);
	   }
	}
}
/**
 * 键盘监听器，用来过滤非数字输入。
 * @author Ttyy
 */
class Key09Listener extends KeyAdapter{

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		int keyChar = e.getKeyChar();                 
        if((keyChar >= '0' && keyChar <= '9')||(keyChar == '-')){
	
        }else{  
            e.consume(); //关键，屏蔽掉非法输入  
        }  		
	}		
}

class ParameterException extends RuntimeException{
	public ParameterException(){}
	
	public ParameterException(String gripe){
		super(gripe);
	}
}

class ServerClosedChannelException extends java.io.IOException{
	public ServerClosedChannelException(){}
}
