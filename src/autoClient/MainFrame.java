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
import java.net.ConnectException;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextArea;
import javax.swing.JTabbedPane;

public class MainFrame extends JFrame {

	private JPanel contentPane;
	private ButtonGroup group;
	private SendBodyPanel sendBodyPane;
	private JButton buttonSend;
	private JButton buttonConnect;
	private Thread send;
	private Thread scanner;
	private Thread recv;
	private JTextArea textArea;
	private CommConnection connection;
	private JButton btnNewButton;
	private JTabbedPane tabbedPane;
	private JPanel panel;
	
	

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
		setBounds(100, 100, 778, 630);
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
		
		group = new ButtonGroup();
		
		panel = new JPanel();
		panel.setBounds(0, 0, 755, 583);
		contentPane.add(panel);
		panel.setLayout(null);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(347, 13, 408, 557);
		panel.add(tabbedPane);
		sendBodyPane = new SendBodyPanel();
		tabbedPane.addTab("\u53C2\u6570\u8BBE\u7F6E", null, sendBodyPane, null);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("\u4E2D\u7EE7\u8BA1\u7B97\u5668", null, panel_1, null);
		
		buttonConnect = new JButton("\u8FDE\u63A5\u670D\u52A1\u5668");
		buttonConnect.setBounds(14, 300, 125, 50);
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
				recvFrameThread.setJTextArea(textArea);
				ScanFileThread scanFileThread = new ScanFileThread();
				send = new Thread(sendFrameThread);
				send.start();
				scanner = new Thread(scanFileThread);
				scanner.start();
				recv = new Thread(recvFrameThread);
				recv.start();
				buttonConnect.setBackground(Color.GREEN);
				buttonSend.setEnabled(true);
				buttonConnect.setEnabled(false);
				btnNewButton.setEnabled(true);
			}
		});
		buttonConnect.setFont(new Font("宋体", Font.PLAIN, 18));
		
		JRadioButton rdbtnpulseposition = new JRadioButton("\u968F\u673A\u6570\u4F4D\u7F6E\u4FE1\u606F");
		rdbtnpulseposition.setBounds(24, 359, 163, 27);
		panel.add(rdbtnpulseposition);
		rdbtnpulseposition.setFont(new Font("宋体", Font.PLAIN, 18));
		rdbtnpulseposition.setActionCommand(String.valueOf(DataFrame.PULSE_POSITION));
		group.add(rdbtnpulseposition);
		
		JRadioButton rdbtnrawkeyapd = new JRadioButton("\u5730\u9762\u6210\u7801");
		rdbtnrawkeyapd.setBounds(24, 391, 163, 27);
		panel.add(rdbtnrawkeyapd);
		rdbtnrawkeyapd.setFont(new Font("宋体", Font.PLAIN, 18));
		rdbtnrawkeyapd.setActionCommand(String.valueOf(DataFrame.RAWKEY_APD));
		group.add(rdbtnrawkeyapd);
		
		JRadioButton rdbtnerr = new JRadioButton("\u5730\u9762\u7EA0\u9519");
		rdbtnerr.setBounds(24, 423, 163, 27);
		panel.add(rdbtnerr);
		rdbtnerr.setFont(new Font("宋体", Font.PLAIN, 18));
		rdbtnerr.setActionCommand(String.valueOf(DataFrame.ERR_CORRECTION));
		group.add(rdbtnerr);
		
		JRadioButton rdbtnkeyrelay = new JRadioButton("\u5BC6\u94A5\u4E2D\u7EE7");
		rdbtnkeyrelay.setBounds(24, 455, 163, 27);
		panel.add(rdbtnkeyrelay);
		rdbtnkeyrelay.setFont(new Font("宋体", Font.PLAIN, 18));
		rdbtnkeyrelay.setActionCommand(String.valueOf(DataFrame.KEY_RELAY));
		group.add(rdbtnkeyrelay);
		
		buttonSend = new JButton("\u53D1\u9001\u6307\u4EE4");
		buttonSend.setBounds(14, 491, 137, 50);
		panel.add(buttonSend);
		buttonSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				try{
					String bm = group.getSelection().getActionCommand();
					OutputFrameQueue.OutputQueue.add(new DataFrame(Integer.valueOf(bm), sendBodyPane.getParameter()));
				}catch (ParameterException|NullPointerException e){
					JOptionPane.showMessageDialog(null, "参数设置有误，请重设！");					
				}
			}
		});
		buttonSend.setFont(new Font("宋体", Font.PLAIN, 18));
		buttonSend.setEnabled(false);
		
		btnNewButton = new JButton("\u65AD\u5F00");
		btnNewButton.setBounds(153, 300, 69, 50);
		panel.add(btnNewButton);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonConnect.setBackground(Color.RED);
				buttonSend.setEnabled(false);
				send.interrupt();
				scanner.interrupt();
				recv.interrupt();
				connection.closeConnect();
				buttonConnect.setEnabled(true);
				btnNewButton.setEnabled(false);
			}
		});
		btnNewButton.setFont(new Font("宋体", Font.PLAIN, 18));
		btnNewButton.setEnabled(false);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(14, 13, 320, 274);
		panel.add(scrollPane);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		textArea = new JTextArea();
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setFont(new Font("宋体", Font.PLAIN, 18));
		scrollPane.setViewportView(textArea);
		
		
		
	}
}
