package autoClient;

import javax.swing.JPanel;
import javax.swing.DefaultComboBoxModel;
import java.awt.Color;
import javax.swing.border.TitledBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.UIManager;
/**
 * 显示主界面下的参数设置JPanel
 * 整体信息的获取与整合
 * @author Ttyy
 *
 */
public class SendBodyPanel extends JPanel {
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_5;
	private JTextField textField_6;
	private JTextField textField_7;
	private JTextField textField_8;
	private JComboBox<String> comboBox;
	private JPanel panel_1;
	private JLabel label_6;
	private JLabel label_7;
	private JLabel label_8;

	/**
	 * Create the panel.
	 */
	public SendBodyPanel() {
		setLayout(null);
				
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "\u53C2\u6570\u8BBE\u7F6E", TitledBorder.LEADING, TitledBorder.TOP, new Font("微软雅黑", Font.PLAIN, 18), new Color(0, 0, 0)));
		panel.setBounds(14, 13, 365, 484);
		add(panel);
		panel.setLayout(null);
		
		JLabel label = new JLabel("\u79D2\u9519\u4F4D");
		label.setFont(new Font("微软雅黑", Font.PLAIN, 18));
		label.setBounds(40, 45, 126, 18);
		panel.add(label);
		
		textField = new JTextField();
		textField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textField.selectAll();
			}
		});
		textField.setBounds(194, 44, 115, 24);
		panel.add(textField);
		textField.setColumns(10);
		
		JLabel label_1 = new JLabel("\u540C\u6B65\u65B9\u5F0F");
		label_1.setFont(new Font("微软雅黑", Font.PLAIN, 18));
		label_1.setBounds(40, 87, 126, 18);
		panel.add(label_1);
		
		comboBox = new JComboBox<>();
		comboBox.setModel(new DefaultComboBoxModel<>(new String[]{"850","532"}));
		comboBox.setBounds(194, 86, 115, 24);
		panel.add(comboBox);
		
		JLabel label_3 = new JLabel("\u53D1\u5C04\u9891\u7387");
		label_3.setFont(new Font("微软雅黑", Font.PLAIN, 18));
		label_3.setBounds(40, 321, 126, 18);
		panel.add(label_3);
		
		JLabel lblFlash = new JLabel("Flash\u5730\u5740");
		lblFlash.setFont(new Font("微软雅黑", Font.PLAIN, 18));
		lblFlash.setBounds(40, 405, 126, 18);
		panel.add(lblFlash);
		
		JLabel label_4 = new JLabel("\u5730\u9762\u91C7\u96C6\u65F6\u95F4");
		label_4.setFont(new Font("微软雅黑", Font.PLAIN, 18));
		label_4.setBounds(40, 363, 126, 18);
		panel.add(label_4);
		
		JLabel label_5 = new JLabel("\u968F\u673A\u6570\u79CD\u5B50");
		label_5.setFont(new Font("微软雅黑", Font.PLAIN, 18));
		label_5.setBounds(40, 447, 126, 18);
		panel.add(label_5);
		
		textField_5 = new JTextField();
		textField_5.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textField_5.selectAll();
			}
		});
		textField_5.addKeyListener(new Key09Listener());
		textField_5.setColumns(10);
		textField_5.setBounds(194, 320, 115, 24);
		panel.add(textField_5);
		
		textField_6 = new JTextField();
		textField_6.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textField_6.selectAll();
			}
		});
		textField_6.addKeyListener(new Key09Listener());
		textField_6.setColumns(10);
		textField_6.setBounds(194, 362, 115, 24);
		panel.add(textField_6);
		
		textField_7 = new JTextField();
		textField_7.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textField_7.selectAll();
			}
		});
		textField_7.setDocument(new NumberLenghtLimitedDmt(8));
		textField_7.setColumns(10);
		textField_7.setBounds(194, 404, 115, 24);
		panel.add(textField_7);
		
		textField_8 = new JTextField();
		textField_8.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textField_8.selectAll();
			}
		});
		textField_8.addKeyListener(new Key09Listener());
		textField_8.setColumns(10);
		textField_8.setBounds(194, 446, 115, 24);
		panel.add(textField_8);
		
		panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "\u901A\u9053\u5EF6\u65F6", TitledBorder.LEADING, TitledBorder.TOP, new Font("微软雅黑", Font.PLAIN, 18), null));
		panel_1.setBounds(39, 119, 309, 185);
		panel.add(panel_1);
		panel_1.setLayout(null);
		
		textField_1 = new JTextField();
		textField_1.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textField_1.selectAll();
			}
		});
		textField_1.setBounds(155, 33, 115, 24);
		panel_1.add(textField_1);
		textField_1.addKeyListener(new Key09Listener());
		textField_1.setColumns(10);
		
		textField_2 = new JTextField();
		textField_2.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textField_2.selectAll();
			}
		});
		textField_2.setBounds(155, 71, 115, 24);
		panel_1.add(textField_2);
		textField_2.addKeyListener(new Key09Listener());
		textField_2.setColumns(10);
		
		textField_3 = new JTextField();
		textField_3.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textField_3.selectAll();
			}
		});
		textField_3.setBounds(155, 109, 115, 24);
		panel_1.add(textField_3);
		textField_3.addKeyListener(new Key09Listener());
		textField_3.setColumns(10);
		
		textField_4 = new JTextField();
		textField_4.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textField_4.selectAll();textField_6.selectAll();
			}
		});
		textField_4.setBounds(155, 147, 115, 24);
		panel_1.add(textField_4);
		textField_4.addKeyListener(new Key09Listener());
		textField_4.setColumns(10);
		
		JLabel label_2 = new JLabel("\u901A\u90530");
		label_2.setBounds(35, 35, 79, 18);
		panel_1.add(label_2);
		label_2.setFont(new Font("微软雅黑", Font.PLAIN, 18));
		
		label_6 = new JLabel("\u901A\u90531");
		label_6.setFont(new Font("微软雅黑", Font.PLAIN, 18));
		label_6.setBounds(35, 73, 79, 18);
		panel_1.add(label_6);
		
		label_7 = new JLabel("\u901A\u90532");
		label_7.setFont(new Font("微软雅黑", Font.PLAIN, 18));
		label_7.setBounds(35, 111, 79, 18);
		panel_1.add(label_7);
		
		label_8 = new JLabel("\u901A\u90533");
		label_8.setFont(new Font("微软雅黑", Font.PLAIN, 18));
		label_8.setBounds(35, 149, 79, 18);
		panel_1.add(label_8);

	}
	
	/**
	 * 用于获取面板上参数的设定值
	 * @return	新的参数类
	 * @throws ParameterException	面板输入的值不匹配或没有填满
	 */
	public Parameter getParameter() throws ParameterException{
		Parameter parameter = new Parameter();
		
		try{
			
			parameter.fixSecond = Integer.valueOf(textField.getText());
			parameter.SYNtype = (byte) (comboBox.getSelectedItem().equals("850")?0:1);
			parameter.channelDelay[0] = Integer.valueOf(textField_1.getText());
			parameter.channelDelay[1] = Integer.valueOf(textField_2.getText());
			parameter.channelDelay[2] = Integer.valueOf(textField_3.getText());
			parameter.channelDelay[3] = Integer.valueOf(textField_4.getText());
			parameter.sendFrequrt = Short.valueOf(textField_5.getText());
			parameter.recvTime = Long.valueOf(textField_6.getText());
			parameter.flashAddress = Tools.StringToByte(textField_7.getText());
			parameter.randomSeed = Integer.valueOf(textField_8.getText());
		}catch(NullPointerException|NumberFormatException e){

			ParameterException exception = new ParameterException();
			throw exception;
		}
		
		return parameter;
	}
}
/**
 * 参数设置
 * @author Ttyy
 *
 */
class Parameter{
	public int fixSecond;
	public byte SYNtype;
	public int[] channelDelay;
	public short sendFrequrt;
	public long recvTime;
	public byte[] flashAddress;
	public int randomSeed;
	
	public Parameter() {
		channelDelay = new int[4];
	}
	
}
