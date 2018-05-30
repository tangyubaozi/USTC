package autoClient;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class testframe extends JFrame {

	private JPanel contentPane;
	public JPanel panel_2;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					testframe frame = new testframe();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public testframe() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 684, 548);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SwingUtilities.updateComponentTreeUI(this);
		
		JTabbedPane 你好 = new JTabbedPane(JTabbedPane.TOP);
		你好.setBounds(5, 5, 656, 496);
		contentPane.add(你好);
		
		panel_2 = new JPanel();
		你好.addTab("New tab", null, panel_2, null);
		panel_2.setLayout(null);
		
		JPanel panel = new SubXORPanel();
		panel.setBounds(14, 13, 193, 417);
		panel_2.add(panel);
		panel.setLayout(null);
		
		SubXORPanel subXORPanel = new SubXORPanel();
		subXORPanel.setLayout(null);
		subXORPanel.setBounds(221, 13, 193, 417);
		panel_2.add(subXORPanel);
		
		JButton btnPackage = new JButton("package");
		btnPackage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		btnPackage.setBounds(497, 403, 113, 27);
		panel_2.add(btnPackage);
		
		JPanel panel_1 = new JPanel();
		你好.addTab("New tab", null, panel_1, null);
		
		JButton btnNewButton_1 = new JButton("222");
		panel_1.add(btnNewButton_1);
	}
}
