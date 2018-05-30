package autoClient;

import java.nio.ByteBuffer;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface CallDLL extends Library{
	CallDLL INSTANCE = Native.loadLibrary("lib\\BlocksCntDLL", CallDLL.class);
    int BlocksCnt(int t, ByteBuffer bb, String path);
}
