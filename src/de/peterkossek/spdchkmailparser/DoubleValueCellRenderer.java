package de.peterkossek.spdchkmailparser;

import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class DoubleValueCellRenderer extends DefaultTableCellRenderer {

	private static DecimalFormat df = new DecimalFormat("0.00");
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (cell instanceof JLabel) {
			JLabel label = (JLabel) cell;
			label.setHorizontalAlignment(JLabel.RIGHT);
			if (value instanceof Double) {
				double dValue = (double) value;
				label.setText(df.format(dValue));
			}
		}
		return cell;
	}

}
