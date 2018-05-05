package autoClient;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 只有一个静态域，阻塞队列
 * 作为发送帧的输出队列
 * @author Ttyy
 *
 */
public interface OutputFrameQueue {
	LinkedBlockingQueue<DataFrame> OutputQueue = new LinkedBlockingQueue<>();
}
