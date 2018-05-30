package autoClient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import record.Recorder;

import static autoClient.DataFrame.*;

/**
 * 以单例模式构建
 * 在流程开始时建立，所有参数固定，生成初始包，
 * 在获得反馈帧后，会继续完善整个流程
 * @author Ttyy
 *
 */
public class Procedure implements Serializable{
		
	
	/**
	 * 建议每次修改后重新生成该值
	 */
	private static final long serialVersionUID = 5353510773240853037L;
	
	private static Procedure procedure;
	private static Parameter parameter;
	private int step_now;
	private int sum_steps;
	private LinkedBlockingQueue<DataFrame> procQueue;
	private long flash;
	private ArrayList<FlashAddrCell> addressList;

	
	public static final Path PATH = Paths.get(".").resolve("Procedure.dat");
	public static final Path PATH_ADDRESS_TABLE = Paths.get(".").resolve("this_station_address_table.txt");

	
	//静态代码块，用于该类初始化加载序列化存档
	static{
		try(ObjectInputStream in = new ObjectInputStream(
				new FileInputStream(PATH.toFile()))){
			
			procedure = (Procedure) in.readObject();
		} catch (FileNotFoundException | ClassNotFoundException e1) {
			procedure = null;
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private Procedure() {
		procQueue = new LinkedBlockingQueue<>();
		sum_steps = 2;
	}
	
	public void start(){
		step_now = 0;
		procQueue.add(new DataFrame(PULSE_POSITION,parameter));
	}
	
	public void start(Parameter p){
		step_now = 0;
		procQueue.add(new DataFrame(PULSE_POSITION,p));
		addressList = new ArrayList<>();
		if(Files.exists(PATH_ADDRESS_TABLE)){
			List<String> ss;
			try {
				ss = Files.readAllLines(PATH_ADDRESS_TABLE);
				String s = ss.get(ss.size()-1);
				FlashAddrCell fac = new FlashAddrCell();
				fac.readString(s);
				flash = fac.flashAddr;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}else{
			String flashString = JOptionPane.showInputDialog("输入本次Flash地址：");
			flash = Long.valueOf(flashString, 16);
		}
		saveProcedure();
		
	}
	
	
	public void recive(RecvBody recvBody){
		switch (recvBody.mod){
		case PULSE_POSITION:
			step_now = 0;
			sum_steps = recvBody.up_file_num;
			addTask();
			break;
		case RAWKEY_APD:
			break;
		case ERR_CORRECTION:
			getEmptyLength().length = recvBody.key_length[0];
			if(recvBody.key_length[1] != 0){
				getEmptyLength().length = recvBody.key_length[1];
			}
			break;
		default:
			break;
		}
		saveProcedure();
	}
	
	private void addTask(){
		int i;
		Parameter p;
		for ( i = 0; i < sum_steps; i++) {
			p = new Parameter();
			flash += 0x40;
			p.flashAddress = Tools.StringToByte(String.format("%08x", flash));
			
			FlashAddrCell fCell = new FlashAddrCell();
			fCell.flashAddr = flash;
			fCell.time = Calendar.getInstance();
			fCell.length = -1;
			fCell.used = false;
			addressList.add(fCell);
			
			p.errNum = i;			
			procQueue.add(new DataFrame(RAWKEY_APD,p));
			if(i%2 == 1){
				p = new Parameter();
				p.errNum = i;
				procQueue.add(new DataFrame(ERR_CORRECTION,p));
			}
		}
		if(sum_steps % 2 == 1){
			p = new Parameter();
			p.errNum = sum_steps - 1;
			procQueue.add(new DataFrame(ERR_CORRECTION,p));
		}
		sum_steps = procQueue.size();
 	}
	
	public DataFrame nextStep(){
		return procQueue.peek();
	}
	
	public Procedure doNextStep(Queue<DataFrame> outQueue){
		step_now ++;
		if(step_now >= sum_steps){
			finishProcedure();
		}else{
			DataFrame dataFrame = procQueue.poll();
			outQueue.add(dataFrame);
		}
		return procedure;	
	}
	
	public DataFrame pollNextStep(){
		step_now ++;
		if(step_now >= sum_steps){
			finishProcedure();
			return null;
		}else{
			return procQueue.poll();
		}
		
	}
	
	private FlashAddrCell getEmptyLength(){
		for (FlashAddrCell f : addressList) {
			if(f.length == -1){
				return f;
			}
		}
		return null;
	}
	
	public void finishProcedure(){
		if(step_now >= sum_steps){
			List<String> ss = addressList.stream()
					.map(FlashAddrCell::toString)
					.collect(Collectors.toList());
			try {
				Files.write(PATH_ADDRESS_TABLE, ss, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		procedure = null;
		try {
			Files.delete(PATH);
		} catch(NoSuchFileException e){
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
	
	public void saveProcedure(){
		try(ObjectOutputStream outputStream = new ObjectOutputStream(
				new FileOutputStream(PATH.toFile())))
		{		
			outputStream.writeObject(procedure);			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * 该类的唯一获取方式
	 * @return
	 */
	public static Procedure getProcedure(){
		if(procedure == null){
			procedure = new Procedure();
		}
		return procedure;
	}
	
	public static void setParameter(Parameter p){
		parameter = p;
	}
	public static void CreateNewProcedure(){
		procedure = new Procedure();

	}
	public static void AbandonAndCreateNew(){
		try {
			Files.delete(PATH);
		} catch (IOException e) {
			e.printStackTrace();
		}
		procedure = new Procedure();
	}
}
