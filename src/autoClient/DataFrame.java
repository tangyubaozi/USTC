package autoClient;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SECOND;
import static java.util.Calendar.YEAR;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 数据帧打包以及解帧，该类不属于UI，不要被Frame字样欺骗
 * @author Ttyy
 *
 */
public class DataFrame {
	
	public static final byte[] FRAME_START = new byte[]{70,83,84,65};	//FSTA
	public static final byte[] FRAME_END = new byte[]{69,78,68};		//END
	/**
	 * 随机数位置信息PulsePosition (0xA0)
	 * 地面成码RawkeyAPD (0xB0)
	 * 地面纠错 (0xC0)
	 * 密钥中继(0XD0)
	 * 传输文件(0XE0)
	 * 文件接收(0XF0)
	 */
	public static final int PULSE_POSITION = 0xA0;
	public static final int RAWKEY_APD = 0xB0;
	public static final int ERR_CORRECTION = 0xC0;
	public static final int KEY_RELAY = 0xD0;
	public static final int TRANSFER_FILE = 0xE0;
	public static final int RECV_FILE = 0xF0;
	
	private int frame_length = 24;
	private ByteBuffer byteBuffer;
	private ByteBuffer frame_body;
	private Parameter parameter;
	private int Mod;
	private File transFile;

	
	public DataFrame(File file, byte[] MD5){
		Mod = TRANSFER_FILE;
		transFile = file;
		frame_length += 280;//280=256+8+16
		frame_body = ByteBuffer.allocate(frame_length);	
		frame_body.clear();
		frame_body.put(file.getName().getBytes());
		frame_body.putLong(256, file.length());
		frame_body.position(264);
		frame_body.put(MD5);
		frame_body.flip();
		packFrame();
	}
	
	
	/**
	 * 以帧模式和帧体，创建数据帧；
	 * 帧体由SendBody类创建
	 * @param mod	帧模式
	 * @param body	帧体
	 */
	public DataFrame(int mod, ByteBuffer body){
		Mod = mod;
		transFile = null;
		frame_body = body;
		frame_length += body.remaining();
		packFrame();
		
	}
	/**
	 * 以帧模式和参数页参数，创建数据帧
	 * 组帧方式变化时，该方法需要更新
	 * @param mod
	 * @param p
	 */
	public DataFrame(int mod, Parameter p){
		Mod = mod;
		transFile = null;
		parameter = p;
		ByteBuffer bb = ByteBuffer.allocate(500);
		switch(mod){
		case DataFrame.PULSE_POSITION:
			bb.putInt(p.fixSecond);
			bb.putInt(p.SYNtype);
			for (int i : p.channelDelay) {
				bb.putInt(i);
			}
			bb.putInt(p.sendFrequrt);
			bb.putLong(p.recvTime);
			break;
		case DataFrame.RAWKEY_APD:
			bb.put(p.flashAddress);	
		case DataFrame.ERR_CORRECTION:
			bb.putInt(p.errNum);
			break;
		case DataFrame.KEY_RELAY:
			break;
		default:
			break;
			
		}
		bb.flip();
		frame_length += bb.remaining();
		frame_body = bb;
		packFrame();
	}
	
	public ByteBuffer getBuffer(){
		byteBuffer.flip();
		return byteBuffer;
	}
	
	public File getFile(){
		return transFile;
	}
	
	private void packFrame(){
		byteBuffer = ByteBuffer.allocateDirect(frame_length);
		byte[] a = new byte[frame_length-1];
		byteBuffer.put(FRAME_START);
		byteBuffer.putInt(frame_length);
		byteBuffer.putInt(Mod);
		byteBuffer.put(packTimeToByte());
		if(frame_body!=null)
			byteBuffer.put(frame_body);
		byteBuffer.put(FRAME_END);
		byteBuffer.rewind();
		byteBuffer.get(a);
		byteBuffer.put(Tools.XORCheck(a));			
	}
	
	public void setFrameBody(ByteBuffer bb){
		frame_body = bb;
		frame_length += bb.limit();
	}
	
	public static byte[] packTimeToByte(){
		ByteBuffer buffer = ByteBuffer.allocate(8);
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		buffer.putShort((short)c.get(YEAR));
		buffer.putShort((short)(c.get(MONTH)+1));
		buffer.put((byte)c.get(DAY_OF_MONTH));
		buffer.put((byte)c.get(HOUR_OF_DAY));
		buffer.put((byte)c.get(MINUTE));
		buffer.put((byte)c.get(SECOND));
		return buffer.array();
	}
	/**
	 * 解帧方法，该方法为静态方法
	 * @param buffer	接收到的数据
	 * @return			接收帧的具体信息
	 */
	public static RecvBody decodeFrame(ByteBuffer buffer){
		RecvBody recvBody = new RecvBody();
		int mod = buffer.getInt();
		recvBody.mod = mod;
		
		Calendar.Builder c = new Calendar.Builder();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		c.set(YEAR, buffer.getShort());
		c.set(MONTH, buffer.getShort()-1);
		c.set(DAY_OF_MONTH, buffer.get());
		c.set(HOUR_OF_DAY, buffer.get());
		c.set(MINUTE, buffer.get());
		c.set(SECOND, buffer.get());		
		recvBody.time = c.build();
		
		if(mod == TRANSFER_FILE){
			CharBuffer cb = buffer.asCharBuffer();
			cb.limit(FileConfig.NAMECHARSIZE);
			String name = cb.toString();
			int index = name.indexOf(0);
			recvBody.file_name = name.substring(0, index);
			buffer.position(buffer.position()+FileConfig.NAMECHARSIZE*2);
			recvBody.file_length = buffer.getLong();
			recvBody.MD5 = new byte[16];
			buffer.get(recvBody.MD5);
		}else{
			recvBody.success = buffer.getInt()==1?true:false;
			switch(mod){
				case PULSE_POSITION:
					recvBody.up_file_num = buffer.getInt();
					break;
				case RAWKEY_APD:
					recvBody.err_key_rate = buffer.getFloat();
					break;
				case ERR_CORRECTION:
					int[] bs = new int[2];
					for (int i = 0; i < bs.length; i++) {
						bs[i] = buffer.getInt();
					}
					recvBody.key_length = bs;
					break;
				case KEY_RELAY:
					break;				
				case RECV_FILE:
					CharBuffer cb = buffer.asCharBuffer();
					cb.limit(FileConfig.NAMECHARSIZE);
					String name = cb.toString();
					int index = name.indexOf(0);
					recvBody.file_name = name.substring(0, index);
					break;
				default:
					break;
			}
		}
		
		return recvBody;
	}
	
	@Override
	public String toString(){
		StringBuilder sBuilder = new StringBuilder();
		switch(Mod){
		case PULSE_POSITION:
			sBuilder.append("时间同步 # ");
			sBuilder.append("时间 ");
			sBuilder.append(parameter.recvTime);
			
			break;
		case RAWKEY_APD:
			sBuilder.append("地面成码 # 序号 ");
			sBuilder.append(parameter.errNum);
			sBuilder.append("#地址 ");
			String s = String.format("%02x%02x%02x%02x", 
					parameter.flashAddress[0],parameter.flashAddress[1],
					parameter.flashAddress[2],parameter.flashAddress[3]);
			sBuilder.append(s);
			break;
		case ERR_CORRECTION:
			sBuilder.append("地面纠错 # 序号 ");
			if(parameter.errNum % 2 == 1){
				sBuilder.append(parameter.errNum-1);
				sBuilder.append(" and ");	
			}
			sBuilder.append(parameter.errNum);
			break;
		case KEY_RELAY:
			// TODO 
			break;
		case TRANSFER_FILE:
			sBuilder.append("发送文件 # 文件名 ");
			sBuilder.append(transFile.getName());
			break;
		case RECV_FILE:
			// TODO 
			sBuilder.append("收到文件 # 文件名 ");
			break;
		default:
			break;

		}		
		return sBuilder.toString();
	}
	
}

/**
 * 接收帧的信息
 * @author Ttyy
 *
 */
class RecvBody{
	public Calendar time;
	public int mod;
	public boolean success;
	public int up_file_num;
	public float err_key_rate;
	public int[] key_length;
	public String file_name;
	public long file_length;
	public byte[] MD5;
		
	@Override
	public String toString(){
		StringBuilder s = new StringBuilder();
		Date date = time.getTime();
		SimpleDateFormat bartDateFormat =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ ");        
		s.append(bartDateFormat.format(date));
		switch(mod){
		case DataFrame.PULSE_POSITION:
			s.append("随机数位置指令运行：");
			s.append(YON(success));
			s.append("生成上行文件个数：");
			s.append(up_file_num+"\n");
			break;
		case DataFrame.RAWKEY_APD:
			s.append("地面成码指令运行：");
			s.append(YON(success));
			s.append("误码率：");			
			s.append(String.format("%.2f%% ", err_key_rate*100));			
			s.append("\n");
			break;
		case DataFrame.ERR_CORRECTION:
			s.append("地面纠错指令运行：");
			s.append(YON(success));
			s.append("最终密钥长度：");
			for (int i = 0; i < 2;i++) {
				s.append(key_length[i]);
				s.append(' ');
			}
			s.append("\n");	
			break;
		case DataFrame.KEY_RELAY:
			s.append("密钥中继指令运行：");
			s.append(YON(success));
			s.append("\n");	
			break;	
		case DataFrame.TRANSFER_FILE:
			s.append("准备接收文件：");
			s.append(file_name);
			s.append("\n");
			break;	
		case DataFrame.RECV_FILE:
			s.append("发送文件：");
			s.append(file_name);
			s.append(' ');
			s.append(YON(success));
			s.append("\n");
			break;
		default:
			break;
		}
		return s.toString();
	}
	public String YON(boolean b){
		return b?"成功":"失败";
	}
}
