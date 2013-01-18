package de.peterkossek.spdchkmailparser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.table.DefaultTableModel;

public class SpeedCheckTableModel extends DefaultTableModel {
	
	private static final long serialVersionUID = 1L;

	private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	
	Class<?>[] columnTypes = new Class[] {
		String.class, Double.class, Double.class, String.class
	};
	static String[] columnTitles = new String[] {"Date", "Upstream", "Downstream", "Ticket"};
	
	private ArrayList<SpeedCheckData> data = new ArrayList<SpeedCheckData>();

	public SpeedCheckTableModel() {
		super(columnTitles, 0);
	}

	public Class<?> getColumnClass(int columnIndex) {
		return columnTypes[columnIndex];
	}
	
	
	public void clear() {
		data.clear();
		fireTableDataChanged();
	}
	
	public void add(SpeedCheckData entry) {
		data.add(entry);
		fireTableDataChanged();
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		SpeedCheckData rData = data.get(row);
		switch (column) {
		case 0:
			return sdf.format(rData.getDate());
		case 1:
			return rData.getUpSpeed();
		case 2:
			return rData.getDownSpeed();
		case 3:
			return rData.getTicketNumber();
		default:
			return null;
		}
	}

	public void setData(SpeedCheckData[] newData) {
		data = new ArrayList<SpeedCheckData>(Arrays.asList(newData));
		fireTableDataChanged();
	}
	
	@Override
	public int getColumnCount() {
		return columnTitles.length;
	}
	
	@Override
	public int getRowCount() {
		if (data == null)
			return 0;
		return data.size();
	}
}