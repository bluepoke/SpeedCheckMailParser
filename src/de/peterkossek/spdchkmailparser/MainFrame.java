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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

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
	private JPanel panel_1;
	private JButton btnVisualize;

	private SpeedCheckData[] parsedData;
	
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
		
		panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.gridwidth = 3;
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.insets = new Insets(0, 0, 5, 5);
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 1;
		panel.add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{0, 0, 0};
		gbl_panel_1.rowHeights = new int[]{0, 0};
		gbl_panel_1.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		btnParse = new JButton("Parse");
		GridBagConstraints gbc_btnParse = new GridBagConstraints();
		gbc_btnParse.insets = new Insets(0, 0, 0, 5);
		gbc_btnParse.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnParse.gridx = 0;
		gbc_btnParse.gridy = 0;
		panel_1.add(btnParse, gbc_btnParse);
		
		btnVisualize = new JButton("Visualize");
		btnVisualize.addActionListener(this);
		GridBagConstraints gbc_btnVisualize = new GridBagConstraints();
		gbc_btnVisualize.gridx = 1;
		gbc_btnVisualize.gridy = 0;
		panel_1.add(btnVisualize, gbc_btnVisualize);
		btnParse.addActionListener(this);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 3;
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
			parsedData = MailParser.parse(files);
			Arrays.sort(parsedData, SpeedCheckData.COMPARATOR);
			speedCheckTableModel.setData(parsedData);
		} else if (source.equals(btnVisualize)) {
			TimeSeriesCollection tsc = new TimeSeriesCollection();
			TimeSeries ts = new TimeSeries("");
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
			int previousDay = 0;
			Calendar calendar = Calendar.getInstance();
			for (SpeedCheckData scd : parsedData) {
				Date date = scd.getDate();
				double value = scd.getDownSpeed();
				calendar.setTime(date);
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				if (day != previousDay) {
					previousDay = day;
					ts = new TimeSeries(sdf.format(date));
					tsc.addSeries(ts);
				}
				ts.add(new Minute(calendar.get(Calendar.MINUTE), calendar.get(Calendar.HOUR_OF_DAY), 1, 1, 2000), value);
			}
			JFreeChart timeSeriesChart = ChartFactory.createTimeSeriesChart("Graph", "Date", "Speed", tsc, true, true, false);
			XYPlot plot = (XYPlot) timeSeriesChart.getPlot();
			DateAxis dateAxis = (DateAxis) plot.getDomainAxis();
			SimpleDateFormat sdfAxis = new SimpleDateFormat("HH:mm");
			dateAxis.setDateFormatOverride(sdfAxis);
			dateAxis.setVerticalTickLabels(true);
			ChartFrame cf = new ChartFrame("Chart", timeSeriesChart);
			cf.setMinimumSize(new Dimension(600, 400));
			cf.setLocationRelativeTo(this);
			cf.setVisible(true);
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
