package record;

import java.io.DataInput;
import java.io.DataOutput;

public interface Recordable {
	
	
	boolean readFromDataInput(DataInput in);
	
	void writeToDataOutput(DataOutput out);
	
	boolean readString(String s);
	
	String toString();
	
}

