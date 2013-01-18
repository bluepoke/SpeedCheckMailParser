package de.peterkossek.spdchkmailparser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumnModel;

public class MainFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private JTextField txfFolderPath;
	private JButton btnOpen;
	private JTable table;
	private JButton btnParse;
	private SpeedCheckTableModel speedCheckTableModel;
	private Properties configProperties;
	
	private static final String INI_FILE_NAME = "SpeedCheckMailParser.ini";
	
	private static final String FOLDER_PATH_INI = "folder.path";
	
	public MainFrame() {
		loadConfig();
		setMinimumSize(new Dimension(640, 400));
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout(0, 0));
		setTitle("SpeedCheck Mail Parser");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(panel, BorderLayout.CENTER);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblFolder = new JLabel("Folder:");
		GridBagConstraints gbc_lblFolder = new GridBagConstraints();
		gbc_lblFolder.insets = new Insets(0, 0, 5, 5);
		gbc_lblFolder.anchor = GridBagConstraints.EAST;
		gbc_lblFolder.gridx = 0;
		gbc_lblFolder.gridy = 0;
		panel.add(lblFolder, gbc_lblFolder);
		
		txfFolderPath = new JTextField();
		txfFolderPath.setText(configProperties.getProperty(FOLDER_PATH_INI, ""));
		GridBagConstraints gbc_txfFolderPath = new GridBagConstraints();
		gbc_txfFolderPath.insets = new Insets(0, 0, 5, 5);
		gbc_txfFolderPath.fill = GridBagConstraints.HORIZONTAL;
		gbc_txfFolderPath.gridx = 1;
		gbc_txfFolderPath.gridy = 0;
		panel.add(txfFolderPath, gbc_txfFolderPath);
		txfFolderPath.setColumns(10);
		
		btnOpen = new JButton("Open...");
		btnOpen.addActionListener(this);
		GridBagConstraints gbc_btnOpen = new GridBagConstraints();
		gbc_btnOpen.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnOpen.insets = new Insets(0, 0, 5, 0);
		gbc_btnOpen.gridx = 2;
		gbc_btnOpen.gridy = 0;
		panel.add(btnOpen, gbc_btnOpen);
		
		btnParse = new JButton("Parse");
		btnParse.addActionListener(this);
		GridBagConstraints gbc_btnParse = new GridBagConstraints();
		gbc_btnParse.insets = new Insets(0, 0, 5, 0);
		gbc_btnParse.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnParse.gridx = 2;
		gbc_btnParse.gridy = 1;
		panel.add(btnParse, gbc_btnParse);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 3;
		gbc_scrollPane.insets = new Insets(0, 0, 0, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 2;
		panel.add(scrollPane, gbc_scrollPane);
		
		table = new JTable();
		table.setFont(new Font("Monospaced", Font.PLAIN, 11));
		speedCheckTableModel = new SpeedCheckTableModel();
		table.setModel(speedCheckTableModel);
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(1).setCellRenderer(new DoubleValueCellRenderer());
		columnModel.getColumn(2).setCellRenderer(new DoubleValueCellRenderer());
		scrollPane.setViewportView(table);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source.equals(btnOpen)) {
			JFileChooser jfc = new JFileChooser();
			jfc.setMultiSelectionEnabled(false);
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int option = jfc.showOpenDialog(this);
			if (option == JFileChooser.APPROVE_OPTION) {
				String path = jfc.getSelectedFile().getAbsolutePath();
				txfFolderPath.setText(path);
				configProperties.put(FOLDER_PATH_INI, path);
				saveConfig();
			}
		} else if (source.equals(btnParse)) {
			File folder = new File(txfFolderPath.getText());
			if (!folder.exists()) {
				JOptionPane.showMessageDialog(this, folder.getAbsolutePath()+" does not exist!", "Folder does not exist", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (!folder.isDirectory()) {
				JOptionPane.showMessageDialog(this, folder.getAbsolutePath()+" is not a folder!", "Not a folder", JOptionPane.ERROR_MESSAGE);
				return;
			}
			File[] files = folder.listFiles();
			SpeedCheckData[] data = MailParser.parse(files);
			speedCheckTableModel.setData(data);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		MainFrame frame = new MainFrame();
		frame.setVisible(true);
	}

	private void loadConfig() {
		configProperties = new Properties();
		try {
			configProperties.load(new FileInputStream(INI_FILE_NAME));
		} catch (Exception e) {
			// doesn't matter
		}
	}
	
	private void saveConfig() {
		try {
			configProperties.store(new FileOutputStream(INI_FILE_NAME), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
