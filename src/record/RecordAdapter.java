package record;

import java.io.DataInput;
import java.io.DataOutput;

public class RecordAdapter implements Recordable {

	@Override
	public boolean readFromDataInput(DataInput in) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void writeToDataOutput(DataOutput out) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean readString(String s) {
		return false;
	}
	
	@Override
	public String toString(){
		return super.toString();
	}
}
