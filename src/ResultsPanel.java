import java.awt.Component;
import java.awt.Dimension;
//import net.miginfocom.swing.MigLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

@SuppressWarnings("serial")
public class ResultsPanel{
	private static JTable table;
	public static JTextField txtActivityDecay;
	public static JFrame frame;
	public static JPanel contentPane;

	/**
	 * Create the panel.
	 */
	public static void createResultsPanel() {
		frame = new JFrame();
		frame.setTitle("OverallResults");
		//
		frame.setLayout(null);
		frame.setBounds(0, 0, 500, 500);
		frame.setResizable(false);
		
		contentPane = new JPanel();
		contentPane.setBounds(0,0,500,500);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.add(contentPane);
		frame.setSize(500,490);
		
		frame.setVisible(false);
		frame.setLocationRelativeTo(null);
		
		//icon for corner
		Image icon = Toolkit.getDefaultToolkit().getImage("./QueensLogo_colour.jpg");    
		frame.setIconImage(icon);
		
		JLabel lblNewLabel = new JLabel("Overall Results");
		lblNewLabel.setBounds(161, 18, 116, 19);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		contentPane.add(lblNewLabel);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(450,380));
		contentPane.add(scrollPane);
		
		//results table
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
		table.setFillsViewportHeight(true);
		scrollPane.setViewportView(table);
	}
	
	public static void populateResultsPane(Object[][] tableData, String str) {
		frame.setVisible(true);
		
		String[] columnNames = {"Isotope", "<html> Activity Decay (10<sup>6</sup> bq)</html>", "<html> Gamma Dose Rate  in µSv/h at 30 cm</html>" };
		DefaultTableModel model;
		model = new DefaultTableModel(tableData, columnNames) {};
		table.setModel(model);
		table.getTableHeader().setPreferredSize(
			     new Dimension(485,50)
			);
		
		//center justification for results
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		centerRenderer.setVerticalAlignment(DefaultTableCellRenderer.CENTER);
		table.setDefaultRenderer(table.getColumnClass(1), centerRenderer);
		
	}
}
