import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class AdditionalResults extends JPanel {

	//Create the border on the panel and initialize the panel and column names
	Border greyBorder = BorderFactory.createDashedBorder(Color.BLACK);
	AdditionalResults thisPanel = this;
	static String[] columnNames;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static JScrollPane scrollPane;
	public JTable table;
	public JTextField txtTotalNeutronYield;

	/**
	 * Create the panel.
	 */
	@SuppressWarnings("serial")
	public AdditionalResults(String panelText, String[] cn) {
		columnNames = cn;
		setBackground(Color.WHITE);
		setBounds(10, 37, 442, 313);

		setLayout(null);

		//Create the label for the energy level contributions 
		JLabel lblEnergyLevelContributions = new JLabel(panelText);
		lblEnergyLevelContributions.setBounds(68, 0, 287, 44);
		lblEnergyLevelContributions.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblEnergyLevelContributions.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblEnergyLevelContributions);

		//Create and add a scroll pane to the panel
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 43, 411, 202);
		add(scrollPane);

		//TODO 
		// Create the label for the neutron yield
		JLabel lblTotalNeutronYield = new JLabel("<html>Total Neutron Yield (10<sup>6</sup> n/s)</html>");
		lblTotalNeutronYield.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTotalNeutronYield.setBounds(83, 254, 143, 25);
		add(lblTotalNeutronYield);
		
		txtTotalNeutronYield = new JTextField();
		txtTotalNeutronYield.setEditable(false);
		txtTotalNeutronYield.setBounds(247, 256, 174, 20);
		add(txtTotalNeutronYield);
		txtTotalNeutronYield.setColumns(10);

		Object[][] tableArray = null;	

		DefaultTableModel model;
		model = new DefaultTableModel(tableArray, columnNames) {};

			table = new JTable() {
				// Set cells to be uneditable (but still selectable)
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
				// Customize column width based on size of column data
				@Override
				public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
					Component component = super.prepareRenderer(renderer, row, column);
					int rendererWidth = component.getPreferredSize().width;
					TableColumn tableColumn = getColumnModel().getColumn(column);
					tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
					return component;
				}
			};

			table.createDefaultColumnsFromModel();
			table.setAutoCreateColumnsFromModel(true);

			scrollPane.setViewportView(table);

			table.setModel(model);

			// Set columns to have centre justification
			DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
			centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
			table.setDefaultRenderer(table.getColumnClass(1), centerRenderer);

			// Prevent columns from being resized and/or reordered
			table.getTableHeader().setReorderingAllowed(false);
			table.getTableHeader().setResizingAllowed(false);


	}

	@SuppressWarnings("serial")
	public void updateTable(ArrayList<Object[]> inArr, String[] cn) {

		DefaultTableModel model;
		model = new DefaultTableModel(arrayListToArray(inArr), cn) {};
		
		table.setModel(model);

	}

	public Object[][] arrayListToArray(ArrayList<Object[]> breakdownArray) {
		if(breakdownArray == null || breakdownArray.size() == 0) {
			return new Object[0][0];
		}
		Object[][] tableArray = new Object[breakdownArray.size()][breakdownArray.get(0).length];
		for(int i = 0; i < breakdownArray.size(); i++) {
			tableArray[i] = breakdownArray.get(i);
		}
		return tableArray;
	}

	public void refreshTable(ArrayList<Object[]> breakdownArray) {
		// Results Table to be outputted to the GUI
		Object[][] tableArray = arrayListToArray(breakdownArray);		

		DefaultTableModel model;
		model = new DefaultTableModel(tableArray, columnNames) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;};

			table = new JTable() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;
				// Set cells to be uneditable (but still selectable)
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
				// Customize column width based on size of column data
				@Override
				public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
					Component component = super.prepareRenderer(renderer, row, column);
					int rendererWidth = component.getPreferredSize().width;
					TableColumn tableColumn = getColumnModel().getColumn(column);
					tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
					return component;
				}
			};

			scrollPane.setViewportView(table);

			table.setModel(model);

			// Set columns to have centre justification
			DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
			centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
			table.setDefaultRenderer(table.getColumnClass(1), centerRenderer);

			// Prevent columns from being resized and/or reordered
			table.getTableHeader().setReorderingAllowed(false);
			table.getTableHeader().setResizingAllowed(false);


	}
}
