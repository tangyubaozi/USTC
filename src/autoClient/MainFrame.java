package autoClient;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextArea;
import javax.swing.JTabbedPane;
import java.awt.event.WindowAdapter;
import javax.swing.JLabel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.SystemColor;
import javax.swing.JTextPane;

public class MainFrame extends JFrame {

	private JPanel contentPane;
//	private ButtonGroup group;
	private SendBodyPanel sendBodyPane;
	private JButton buttonSend;
	private JButton buttonConnect;
	private Thread send;
	private Thread scanner;
	private Thread recv;
	private JTextArea textAreaShow;
	private CommConnection connection;
	private JButton buttonDisconnect;
	private JTabbedPane tabbedPane;
	private JPanel panel;
	private Procedure procedure;
	private JTextArea textAreaNextStep;
	private JButton buttonStartProcedure;
	private boolean ProcedureStart;
	private boolean Connected;
	
	
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(()-> {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}	
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 878, 717);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		SwingUtilities.updateComponentTreeUI(this);
		

		
		panel = new JPanel();
		panel.setBounds(0, 0, 860, 670);
		contentPane.add(panel);
		panel.setLayout(null);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(438, 13, 408, 557);
		panel.add(tabbedPane);
		sendBodyPane = new SendBodyPanel();
		tabbedPane.addTab("\u53C2\u6570\u8BBE\u7F6E", null, sendBodyPane, null);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("\u4E2D\u7EE7\u8BA1\u7B97\u5668", null, panel_1, null);
		
		buttonConnect = new JButton("\u8FDE\u63A5\u670D\u52A1\u5668");
		buttonConnect.setBounds(14, 373, 125, 50);
		panel.add(buttonConnect);
		buttonConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					connection = CommConnection.getConnection();
				} catch (ConnectException e) {
					JOptionPane.showMessageDialog(null, "服务器未上线!");
					return;
				}
				SendFrameThread sendFrameThread = new SendFrameThread(connection.getOutput());
				RecvFrameThread recvFrameThread = new RecvFrameThread(connection.getInput());
				recvFrameThread.setMainFrame(MainFrame.this);
				ScanFileThread scanFileThread = new ScanFileThread();
				send = new Thread(sendFrameThread);
				send.start();
				scanner = new Thread(scanFileThread);
				scanner.start();
				recv = new Thread(recvFrameThread);
				recv.start();
				buttonConnect.setBackground(Color.GREEN);
				Connected = true;
				setSendButtonEnabled();
				buttonConnect.setEnabled(false);
				buttonDisconnect.setEnabled(true);
			}
		});
		buttonConnect.setFont(new Font("宋体", Font.PLAIN, 18));
				
/*		group = new ButtonGroup();
  		JRadioButton rdbtnpulseposition = new JRadioButton("\u968F\u673A\u6570\u4F4D\u7F6E\u4FE1\u606F");
		rdbtnpulseposition.setBounds(51, 518, 163, 27);
		panel.add(rdbtnpulseposition);
		rdbtnpulseposition.setFont(new Font("宋体", Font.PLAIN, 18));
		rdbtnpulseposition.setActionCommand(String.valueOf(DataFrame.PULSE_POSITION));
		group.add(rdbtnpulseposition);
		
		JRadioButton rdbtnrawkeyapd = new JRadioButton("\u5730\u9762\u6210\u7801");
		rdbtnrawkeyapd.setBounds(51, 550, 163, 27);
		panel.add(rdbtnrawkeyapd);
		rdbtnrawkeyapd.setFont(new Font("宋体", Font.PLAIN, 18));
		rdbtnrawkeyapd.setActionCommand(String.valueOf(DataFrame.RAWKEY_APD));
		group.add(rdbtnrawkeyapd);
		
		JRadioButton rdbtnerr = new JRadioButton("\u5730\u9762\u7EA0\u9519");
		rdbtnerr.setBounds(51, 582, 163, 27);
		panel.add(rdbtnerr);
		rdbtnerr.setFont(new Font("宋体", Font.PLAIN, 18));
		rdbtnerr.setActionCommand(String.valueOf(DataFrame.ERR_CORRECTION));
		group.add(rdbtnerr);
		
		JRadioButton rdbtnkeyrelay = new JRadioButton("\u5BC6\u94A5\u4E2D\u7EE7");
		rdbtnkeyrelay.setBounds(51, 614, 163, 27);
		panel.add(rdbtnkeyrelay);
		rdbtnkeyrelay.setFont(new Font("宋体", Font.PLAIN, 18));
		rdbtnkeyrelay.setActionCommand(String.valueOf(DataFrame.KEY_RELAY));
		group.add(rdbtnkeyrelay);
*/		
		buttonSend = new JButton("\u53D1\u9001\u6307\u4EE4");
		buttonSend.setBounds(297, 520, 127, 50);
		panel.add(buttonSend);
		buttonSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				textAreaShow.append(getTimeNow()+"发送命令："+procedure.nextStep()+"\n");
				procedure = procedure.doNextStep(OutputFrameQueue.OutputQueue);
				updateText();
				if(procedure == null){
					buttonStartProcedure.setEnabled(true);
					ProcedureStart = false;
					setSendButtonEnabled();
				}
			}
		});
		

		
		buttonSend.setFont(new Font("宋体", Font.PLAIN, 18));
		buttonSend.setEnabled(false);
		
		buttonDisconnect = new JButton("\u65AD\u5F00");
		buttonDisconnect.setBounds(153, 373, 69, 50);
		panel.add(buttonDisconnect);
		buttonDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonConnect.setBackground(Color.RED);
				
				send.interrupt();
				scanner.interrupt();
				recv.interrupt();
				connection.closeConnect();
				buttonConnect.setEnabled(true);
				buttonDisconnect.setEnabled(false);
				Connected = false;
				setSendButtonEnabled();
			}
		});
		buttonDisconnect.setFont(new Font("宋体", Font.PLAIN, 18));
		buttonDisconnect.setEnabled(false);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(14, 13, 410, 347);
		panel.add(scrollPane);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		textAreaShow = new JTextArea();
		textAreaShow.setWrapStyleWord(true);
		textAreaShow.setLineWrap(true);
		textAreaShow.setFont(new Font("宋体", Font.PLAIN, 18));
		scrollPane.setViewportView(textAreaShow);
		
		buttonStartProcedure = new JButton("开始流程");
		buttonStartProcedure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					procedure = Procedure.getProcedure();
					procedure.start(sendBodyPane.getParameter());
				}catch (ParameterException|NullPointerException e1){
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "参数设置有误，请重设！");
					return;
				}
				buttonStartProcedure.setEnabled(false);
				updateText();
				
				ProcedureStart = true;
				setSendButtonEnabled();
				
				textAreaShow.append(getTimeNow()+"开始数据处理流程\n");
			}
		});
		//设置放弃流程的方式
		buttonStartProcedure.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				//选取条件：当开始流程按钮为不可选状态下，Ctrl+鼠标右键唤醒确认面板
				int CtrlAndMouse = InputEvent.CTRL_DOWN_MASK|InputEvent.BUTTON3_DOWN_MASK;
				if((e.getModifiersEx() & CtrlAndMouse)== CtrlAndMouse){
					if(!buttonStartProcedure.isEnabled()){
						int choose = JOptionPane.showConfirmDialog(null, "确认放弃本次流程？","警告！",JOptionPane.YES_NO_OPTION);
						if(choose == JOptionPane.YES_OPTION){
							Procedure.AbandonAndCreateNew();
							buttonStartProcedure.setEnabled(true);
							ProcedureStart = false;
							setSendButtonEnabled();
						}					
					}
				}
			}
		
		});
		buttonStartProcedure.setBounds(297, 373, 125, 50);
		panel.add(buttonStartProcedure);
		
		textAreaNextStep = new JTextArea();
		textAreaNextStep.setBackground(new Color(240, 240, 240));
		textAreaNextStep.setFont(new Font("宋体", Font.PLAIN, 18));
		textAreaNextStep.setBounds(14, 436, 408, 71);
		textAreaNextStep.setLineWrap(true);
		textAreaNextStep.setWrapStyleWord(true);
		textAreaNextStep.setEditable(false);
		textAreaNextStep.setText("(*^▽^*)");
		
		panel.add(textAreaNextStep);
		
		JButton btnTest = new JButton("test");
		btnTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ByteBuffer bbb = ByteBuffer.allocate(8);
				bbb.putInt(1);
				bbb.putInt(6);
				bbb.flip();
				OutputFrameQueue.OutputQueue.add(new DataFrame(DataFrame.PULSE_POSITION, bbb));			
			}
		});
		btnTest.setBounds(14, 630, 113, 27);
		panel.add(btnTest);
		
		
		//窗口关闭前保存界面的参数设置
		addWindowListener(new WindowAdapter() {	
			@Override
			public void windowClosing(WindowEvent e) {
				sendBodyPane.saveObject();
				if(procedure != null)
					procedure.saveProcedure();
			}
		});
		
		InitializeProcedure();
	}
	
	/**
	 * 读取序列化后存储的参数类
	 */
	private void InitializeProcedure(){
		procedure = Procedure.getProcedure();
		if(procedure.nextStep()!=null){
			buttonStartProcedure.setEnabled(false);
			updateText();
			
			ProcedureStart = true;
			setSendButtonEnabled();
			
			textAreaShow.append(getTimeNow()+"数据处理流程已开始\n");
			textAreaShow.selectAll();
		}
	}
	
	public void appendTextAreaShow(String s){
		textAreaShow.append(s);
		textAreaShow.selectAll();
	}
	/**
	 * 更新下一步step的信息
	 */
	public void updateText(){
		try{
			if(procedure.nextStep() != null){
				textAreaNextStep.setText(procedure.nextStep().toString());
			}else{
				textAreaNextStep.setText("等待指令传回");
			}
		} catch (NullPointerException e){
			textAreaNextStep.setText("流程已结束");
		}
		
	}
	/**
	 * 设置发送按钮的可用性
	 */
	public void setSendButtonEnabled(){
		if(Connected && ProcedureStart){
			buttonSend.setEnabled(true);
		}else{
			buttonSend.setEnabled(false);
		}
	}
	
	public static String getTimeNow(){
		Date date = new Date();
		SimpleDateFormat bartDateFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");        
		return bartDateFormat.format(date);
	}
}
