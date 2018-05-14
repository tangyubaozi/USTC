package autoClient;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import record.Recordable;

public class FlashAddrCell implements Recordable{
	public Calendar time;
	public long flashAddr;
	public long length;
	public float errRate;
	public boolean used;
	public static final SimpleDateFormat bartDateFormat =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");  
	
	@Override
	public String toString(){
		StringBuilder s = new StringBuilder();
		Date date = time.getTime();       
		s.append(bartDateFormat.format(date));
		s.append(" ");
		s.append(String.format("%08x", flashAddr));
		s.append(" ");
		s.append(length);
		s.append(" ");
		s.append(used);
		return s.toString();
	}
	
	public boolean readString(String string){
		String s;
		try(Scanner scanner = new Scanner(string)) {		
			s = scanner.next();
			time = Calendar.getInstance();		
			time.setTime(bartDateFormat.parse(s));
			flashAddr = Long.valueOf(scanner.next(), 16);
			length = scanner.nextLong();
			used = scanner.nextBoolean();
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean readFromDataInput(DataInput in) {
		try {
			time = Calendar.getInstance();
			time.setTimeInMillis(in.readLong());
			flashAddr = in.readLong();
			length = in.readLong();
			used = in.readBoolean();
			
			return true;
		} catch (IOException e) {
			//e.printStackTrace();
			return false;
		}
	}

	@Override
	public void writeToDataOutput(DataOutput out) {
		try {
			out.writeLong(time.getTimeInMillis());
			out.writeLong(flashAddr);
			out.writeLong(length);
			out.writeBoolean(used);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}