package autoClient;

import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import javax.swing.JFormattedTextField;
import java.awt.Canvas;

public class XORCalculatorPanel extends JPanel {
	private JTextField textField;
	private JTextField textField_1;

	/**
	 * Create the panel.
	 */
	public XORCalculatorPanel() {
		setLayout(null);
		
		JButton btnNewButton = new JButton("使用该设置");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnNewButton.setBounds(261, 505, 133, 39);
		add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("<");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnNewButton_1.setBounds(14, 326, 41, 36);
		add(btnNewButton_1);
		
		JButton button = new JButton(">");
		button.setBounds(124, 326, 41, 36);
		add(button);
		
		textField = new JTextField();
		textField.setColumns(2);
		textField.setBounds(69, 326, 41, 36);
		textField.setText("0");
		textField.setHorizontalAlignment(SwingConstants.CENTER);
		add(textField);
		
		
		JLabel lblNewLabel = new JLabel("0 B ",SwingConstants.RIGHT);
		lblNewLabel.setBounds(42, 277, 94, 36);
		lblNewLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("密钥使用量");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(161, 276, 85, 39);
		add(lblNewLabel_1);
		
		JLabel label_1 = new JLabel("中继密钥字节数");
		label_1.setHorizontalAlignment(SwingConstants.CENTER);
		label_1.setBounds(24, 387, 112, 39);
		add(label_1);
		
		JLabel label_2 = new JLabel("下传密钥长度");
		label_2.setHorizontalAlignment(SwingConstants.CENTER);
		label_2.setBounds(261, 387, 112, 39);
		add(label_2);
		
		JLabel label_3 = new JLabel("0 B ", SwingConstants.RIGHT);
		label_3.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		label_3.setBounds(34, 438, 94, 36);
		add(label_3);
		
		JLabel label_4 = new JLabel("0 B ", SwingConstants.RIGHT);
		label_4.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		label_4.setBounds(271, 438, 94, 36);
		add(label_4);
		
		JLabel label_5 = new JLabel("密钥段数");
		label_5.setHorizontalAlignment(SwingConstants.CENTER);
		label_5.setBounds(163, 325, 82, 39);
		add(label_5);
		
		JButton button_1 = new JButton("<");
		button_1.setBounds(243, 326, 41, 36);
		add(button_1);
		
		JButton button_2 = new JButton(">");
		button_2.setBounds(353, 326, 41, 36);
		add(button_2);
		
		textField_1 = new JTextField();
		textField_1.setText("0");
		textField_1.setHorizontalAlignment(SwingConstants.CENTER);
		textField_1.setColumns(2);
		textField_1.setBounds(298, 326, 41, 36);
		add(textField_1);
		
		JLabel label = new JLabel("0 B ", SwingConstants.RIGHT);
		label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		label.setBounds(271, 277, 94, 36);
		add(label);
		
		Canvas canvas = new Canvas();
		canvas.setBounds(10, 10, 384, 252);
		add(canvas);

	}
}
