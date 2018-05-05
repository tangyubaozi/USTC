package autoClient;

import java.util.concurrent.LinkedBlockingQueue;
/**
 * 
 * @author Ttyy
 *
 */
public interface InputFrameQueue {
	LinkedBlockingQueue<DataFrame> InputQueue = new LinkedBlockingQueue<>();

}
