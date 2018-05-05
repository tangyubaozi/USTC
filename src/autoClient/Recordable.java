package autoClient;

import java.io.DataInput;
import java.io.DataOutput;

public interface Recordable {
	
	
	public boolean readFromDataInput(DataInput in);
	
	public void writeToDataOutput(DataOutput out);
	
	Recordable newInstance();
}

