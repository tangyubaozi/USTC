package autoClient;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
/**
 * 软件配置信息的实现类
 * 在当前目录下生成配置文件，记录配置信息
 * @author Ttyy
 *
 */
public class MyProperties {
	private static MyProperties properties;
	private Path propertiesFile;
	private Properties settings;
	
	public static final String IP = "IP";
	public static final String PATH_DIR = "PathDir";
	public static final String COMM_MODE = "CommunicationMode";
	public static final String TCP_IP = "TCP";
	public static final String SERIAL_PORT = "SerialPort";

	private MyProperties() {		
	    propertiesFile = Paths.get(".").resolve("program.properties");
		settings = new Properties();
		try(InputStream in = Files.newInputStream(propertiesFile)){
			settings.load(in);
		}catch (NoSuchFileException e) {
			defaultSetting();			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	private void defaultSetting(){
		settings.put(PATH_DIR, "C:\\Users\\Ttyy\\workspace\\QKDAutoClient\\src");
		settings.put(IP, "192.168.0.178:8899");
		settings.put(COMM_MODE, TCP_IP);
//		setIP();
//		setFileDir();
		try (OutputStream outputStream = Files.newOutputStream(propertiesFile)){
			settings.store(outputStream, "Work Config");
			
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	public void setIP(){
		String regex = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d):\\d+";
		String ip = JOptionPane.showInputDialog("输入IP地址（包括端口号）：");
		while(!ip.matches(regex)){
			ip = JOptionPane.showInputDialog("请重新输入正确的IP地址（包括端口号）：");
		}
		settings.put(IP, ip);
	}
	public void setFileDir(){
		JDialog jDialog = new JDialog();
		Font font = new Font("宋体", Font.PLAIN, 18);
		JTextField jField = new JTextField(50);
		jDialog.setSize(650,100);
		jDialog.setTitle("选择同步文件路径");
		jField.setFont(font);
		jField.addMouseListener(new MouseAdapter() {			
			@Override
			public void mousePressed(MouseEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = chooser.showOpenDialog(jField);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					jField.setText(chooser.getSelectedFile().toString());
					jField.validate();
				}
			}			
		});
		JButton okBtn = new JButton("确定");
	    okBtn.addActionListener(new ActionListener() {
	    	@Override
	    	public void actionPerformed(ActionEvent e) {
	                // 关闭对话框
	            	settings.put(PATH_DIR, jField.getText());
	            	jDialog.dispose();
	            }
	        });
	    okBtn.setFont(font);
		JPanel panel = new JPanel();
		panel.add(jField);
		panel.add(okBtn);
		jDialog.setContentPane(panel);
		jDialog.setLocationRelativeTo(null);
        //阻塞对话框
		jDialog.setModal(true);
		// 显示对话框
		jDialog.setVisible(true);
	}
	
	public static MyProperties getMyProperties(){
		if(properties==null){
			properties = new MyProperties();
		}
		return properties;
	}
	public Properties getSettings(){		
		return settings;
	}
	
}
