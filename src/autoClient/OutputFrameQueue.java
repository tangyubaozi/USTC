package autoClient;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * ֻ��һ����̬����������
 * ��Ϊ����֡���������
 * @author Ttyy
 *
 */
public interface OutputFrameQueue {
	LinkedBlockingQueue<DataFrame> OutputQueue = new LinkedBlockingQueue<>();
}
