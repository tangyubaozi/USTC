package autoClient;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import record.Recorder;

import javax.swing.JLabel;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;

import java.awt.Color;
import java.io.File;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Canvas;

public class SubXORPanel extends JPanel {
	private JTextField textField;
	private Recorder recorder;
	private long[] sumLengths;
	private int sizeUnused;
	private JLabel label;
	private List<FlashAddrCell> list;

	/**
	 * Create the panel.
	 */
	public SubXORPanel() {

		setLayout(null);
		
		AbstractAction letHigh = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				int n = Integer.valueOf(textField.getText());
				n++;
				if(n <= sizeUnused){
					textField.setText(String.valueOf(n));
					label.setText(sumLengths[n]+" B ");
				}
			}
		};
		AbstractAction letLow = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				int n = Integer.valueOf(textField.getText());
				n--;
				if(n >= 0){
					textField.setText(String.valueOf(n));
					label.setText(sumLengths[n]+" B ");
				}
			}
		};
		
		JButton button = new JButton("<");
		button.addActionListener(letHigh);
		button.setBounds(14, 280, 41, 36);
		add(button);
		
		JButton button_1 = new JButton(">");
		button_1.addActionListener(letLow);
		button_1.setBounds(124, 280, 41, 36);
		add(button_1);
		
		textField = new JTextField();
		textField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textField.selectAll();
			}
			@Override
			public void focusLost(FocusEvent e) {
				int n = Integer.valueOf(textField.getText());
				if(n<0)
					n = 0;
				if(n>sizeUnused)
					n = sizeUnused;
				textField.setText(String.valueOf(n));
				label.setText(sumLengths[n]+" B ");
			}
		});
		textField.setEnabled(false);;
		textField.setHorizontalAlignment(SwingConstants.CENTER);
		textField.setColumns(2);
		textField.setBounds(69, 280, 41, 36);
		add(textField);
		
		label = new JLabel("打开文件", SwingConstants.CENTER);
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//选择文件并读入记录数据
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setCurrentDirectory(new File(".\\"));
				int returnVal = chooser.showOpenDialog(null);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					recorder = new Recorder<FlashAddrCell>(file.toPath(), FlashAddrCell.class, Recorder.HEADLESS, Recorder.TEXT);
					resetData();
					textField.setEnabled(true);
					textField.setText("0");
					label.setHorizontalAlignment(SwingConstants.RIGHT);
					label.setText("0 B ");
				}
					
			}
		});
		label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		label.setBounds(42, 231, 94, 36);
		add(label);
		
		Canvas canvas = new Canvas();
		canvas.setBounds(14, 10, 151, 215);
		add(canvas);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				requestFocus();
			}
		});
		//映射键盘快捷键
		InputMap iMap = this.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,0), "high");
		iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,0), "high");
		iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,0), "low");
		iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,0), "low");
		
		ActionMap aMap = this.getActionMap();
		aMap.put("high", letHigh);
		aMap.put("low", letLow);
		
		
	}
	
	public SubXORPanel(File file){
		this();
		
	}
	
	private void resetData(){
		list = recorder.getList();
		int i = 0 ;
		for (FlashAddrCell f : list) {
			if(f.used){
				i++;
				continue;
			}
			break;
		}
		sizeUnused = list.size() - i;
		sumLengths = new long[sizeUnused+1];
		for (int j = 1; i < list.size(); j++, i++) {
			sumLengths[j] = sumLengths[j-1] + list.get(i).length;
			
		}
		
	}
	
	public int getNumOfData(){
		return Integer.valueOf(textField.getText());
	}

}
