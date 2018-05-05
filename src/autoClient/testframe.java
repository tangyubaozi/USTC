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

public class testframe extends JFrame {

	private JPanel contentPane;

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
		
		JTabbedPane ��� = new JTabbedPane(JTabbedPane.TOP);
		���.setBounds(5, 5, 656, 496);
		contentPane.add(���);
		
		JPanel panel_2 = new JPanel();
		���.addTab("New tab", null, panel_2, null);
		panel_2.setLayout(null);
		
		JPanel panel = new SubXORPanel();
		panel.setBounds(14, 13, 193, 417);
		panel_2.add(panel);
		panel.setLayout(null);
		
		SubXORPanel subXORPanel = new SubXORPanel();
		subXORPanel.setLayout(null);
		subXORPanel.setBounds(354, 13, 193, 417);
		panel_2.add(subXORPanel);
		
		JPanel panel_1 = new JPanel();
		���.addTab("New tab", null, panel_1, null);
		
		JButton btnNewButton_1 = new JButton("222");
		panel_1.add(btnNewButton_1);
	}
}