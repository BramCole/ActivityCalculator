import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


@SuppressWarnings("serial")
public class CellColourRenderer extends JLabel implements TableCellRenderer  {
	
	public CellColourRenderer() {
		setOpaque(true);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, 
			boolean isSelected, boolean hasFocus, int row, int column) {

		// set background of cells in left column to light grey
		// to emphasize that they are not editable
		setBackground(new Color(240,240,240));
		
		// Set text colour to black
		this.setForeground(Color.BLACK);
		
		// Labels for left column
		String[] energyRanges = {
				"1.0",
				"1.5",
				"2.0",
				"2.5",
				"3.0",
				"3.5",
				"4.0",
				"4.5",
				"5.0",
				"5.5",
				"6.0",
				"6.5",
				"7.0",
				"7.5"
		};
		
		// Centre justify the energy range labels in the left column
		setHorizontalAlignment(JLabel.CENTER);
		// Set the text in the left column equal to those in String[] energyRanges
		setText(energyRanges[row]);
		return this;
	}
}