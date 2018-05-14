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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.AclEntry.Builder;

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
	private JTextField textField_6;
	private JComboBox<String> comboBox;
	private JComboBox<Integer> comboBox1;
	private JPanel panel_1;
	private JLabel label_6;
	private JLabel label_7;
	private JLabel label_8;
	public static final Path ObjectPath = Paths.get(".").resolve("SendBodyPanel.dat");

	/**
	 * Create the panel.
	 */
	public SendBodyPanel() {
		setLayout(null);
				
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), 
				"\u53C2\u6570\u8BBE\u7F6E", TitledBorder.LEADING, TitledBorder.TOP,
				new Font("微软雅黑", Font.PLAIN, 18), new Color(0, 0, 0)));
		panel.setBounds(14, 13, 354, 484);
		add(panel);
		panel.setLayout(null);
		
		JLabel label = new JLabel("\u79D2\u9519\u4F4D");
		label.setFont(new Font("微软雅黑", Font.PLAIN, 18));
		label.setBounds(41, 45, 126, 18);
		panel.add(label);
		
		textField = new JTextField();
		textField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textField.selectAll();
			}
		});
		textField.setBounds(195, 44, 115, 24);
		panel.add(textField);
		textField.setColumns(10);
		
		JLabel label_1 = new JLabel("\u540C\u6B65\u65B9\u5F0F");
		label_1.setFont(new Font("微软雅黑", Font.PLAIN, 18));
		label_1.setBounds(41, 243, 126, 18);
		panel.add(label_1);
		
		comboBox = new JComboBox<>();
		comboBox.setModel(new DefaultComboBoxModel<>(new String[]{"850","532"}));
		comboBox.setBounds(195, 242, 115, 24);
		panel.add(comboBox);
		
		comboBox1 = new JComboBox<>();
		comboBox1.setModel(new DefaultComboBoxModel<>(new Integer[]{100,200,50}));
		comboBox1.setBounds(195, 206, 115, 24);
		panel.add(comboBox1);
		
		JLabel label_3 = new JLabel("\u53D1\u5C04\u9891\u7387");
		label_3.setFont(new Font("微软雅黑", Font.PLAIN, 18));
		label_3.setBounds(41, 207, 126, 18);
		panel.add(label_3);
		
		JLabel label_4 = new JLabel("\u5730\u9762\u91C7\u96C6\u65F6\u95F4");
		label_4.setFont(new Font("微软雅黑", Font.PLAIN, 18));
		label_4.setBounds(41, 82, 126, 18);
		panel.add(label_4);
		
		textField_6 = new JTextField();
		textField_6.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textField_6.selectAll();
			}
		});
		textField_6.addKeyListener(new Key09Listener());
		textField_6.setColumns(10);
		textField_6.setBounds(195, 81, 115, 24);
		panel.add(textField_6);
		
		panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "\u901A\u9053\u5EF6\u65F6", 
				TitledBorder.LEADING, TitledBorder.TOP, new Font("微软雅黑", Font.PLAIN, 18), null));
		panel_1.setBounds(33, 286, 299, 185);
		panel.add(panel_1);
		panel_1.setLayout(null);
		
		textField_1 = new JTextField();
		textField_1.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textField_1.selectAll();
			}
		});
		textField_1.setBounds(162, 34, 115, 24);
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
		textField_2.setBounds(162, 72, 115, 24);
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
		textField_3.setBounds(162, 110, 115, 24);
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
		textField_4.setBounds(162, 148, 115, 24);
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
		
		//读取保存的参数设置
		readObject();

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
			parameter.SYNtype = (comboBox.getSelectedItem().equals("850")?0:1);
			parameter.channelDelay[0] = Integer.valueOf(textField_1.getText());
			parameter.channelDelay[1] = Integer.valueOf(textField_2.getText());
			parameter.channelDelay[2] = Integer.valueOf(textField_3.getText());
			parameter.channelDelay[3] = Integer.valueOf(textField_4.getText());
			parameter.sendFrequrt = (Integer) comboBox1.getSelectedItem();
			parameter.recvTime = Long.valueOf(textField_6.getText());
//			parameter.flashAddress = Tools.StringToByte(textField_7.getText());
//			parameter.errNum = Integer.valueOf(textField_8.getText());
		}catch(NullPointerException|NumberFormatException e){

			ParameterException exception = new ParameterException();
			throw exception;
		}
		
		return parameter;
	}
	
	private void initilizeText(Parameter p){
		textField.setText(String.valueOf(p.fixSecond));
		comboBox.setSelectedIndex(p.SYNtype);
		comboBox1.setSelectedItem(p.sendFrequrt);
		textField_1.setText(String.valueOf(p.channelDelay[0]));
		textField_2.setText(String.valueOf(p.channelDelay[1]));
		textField_3.setText(String.valueOf(p.channelDelay[2]));
		textField_4.setText(String.valueOf(p.channelDelay[3]));
		textField_6.setText(String.valueOf(p.recvTime));
	}
	
	/**
	 * 读取序列化后存储的参数类
	 */
	public void readObject(){
		try(ObjectInputStream in = new ObjectInputStream(
				new FileInputStream(ObjectPath.toFile()))){	
			initilizeText((Parameter) in.readObject());
		} catch (FileNotFoundException e1) {
			
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {	
			try {
				Files.delete(ObjectPath);
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
	}
	
	/**
	 * 存储参数类的序列化文件
	 */
	public void saveObject(){

		try(ObjectOutputStream outputStream = new ObjectOutputStream(
				new FileOutputStream(ObjectPath.toFile())))
		{
			Parameter p;
		
			try{ 
				p = getParameter();
			}catch(ParameterException e){
				p = new Parameter();
			}
			outputStream.writeObject(p);
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	
	}
}


/**
 * 参数设置值的存储类，可序列化
 * @author Ttyy
 *
 */
class Parameter implements Serializable{
	/**
	 * Vision 1.0
	 */
	private static final long serialVersionUID = 626703622088519446L;
	
	
	public int fixSecond;
	public int SYNtype;
	public int[] channelDelay;
	public int sendFrequrt;
	public long recvTime;
	public byte[] flashAddress;
	public int errNum;
	
	public Parameter() {
		channelDelay = new int[4];
	}
	
}
