import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

@SuppressWarnings("serial")
public class AddNewIsotope extends JFrame {
	
	private JPanel contentPane;
	private JTextField textField;
	private JTextField txtAbundance;
	private JTextField txtHalfLife;
	private JTextField txtGamma;
	private JTextField password;
	private String[] columnNames = {
			"<html>Delta E<br>(MeV)</html>",
			"<html>Proton Delta range g/cm<sup>2</sup></html>",
			"<html>Proton Average &sigma x 10-24 cm<sup>2</sup></html>",				
			"<html>Alpha Delta range g/cm<sup>2</sup></html>",
			"<html>Alpha Average &sigma x 10-24 cm<sup>2</sup></html>"
	};
	// Real output table, with nulls
			Object outputTable[][] = {
					{"1.0 - 2.5", null, null, null, null},
					{"1.5 - 2.5", null, null, null, null},
					{"2.0 - 2.5", null, null, null, null},
					{"2.5 - 3.0", null, null, null, null},
					{"3.0 - 3.5", null, null, null, null},
					{"3.5 - 4.0", null, null, null, null},
					{"4.0 - 4.5", null, null, null, null},
					{"4.5 - 5.0", null, null, null, null},
					{"5.0 - 5.5", null, null, null, null},
					{"5.5 - 6.0", null, null, null, null},
					{"6.0 - 6.5", null, null, null, null},
					{"6.5 - 7.0", null, null, null, null},
					{"7.0 - 7.5", null, null, null, null},
					{"7.5 - 8.0", null, null, null, null},	
			};

	Connection conn;

	/**
	 * Create the frame.
	 */
	public AddNewIsotope(Connection connection) {
		conn = connection;
		setResizable(false);
		setTitle("Add or Edit Isotope");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 550);
		Image icon = Toolkit.getDefaultToolkit().getImage("./QueensLogo_colour.jpg");    
		setIconImage(icon);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblAddANew = new JLabel("Add or Edit new isotope");
		lblAddANew.setForeground(new Color(0, 102, 153));
		lblAddANew.setFont(new Font("Segoe UI", Font.BOLD, 17));
		lblAddANew.setBounds(10, 11, 200, 21);
		contentPane.add(lblAddANew);
		
		//element and element drop down box

		JLabel lblElement = new JLabel("Element");
		lblElement.setBounds(20, 51, 46, 14);
		contentPane.add(lblElement);


		JComboBox<String> comboBox = new JComboBox<>();
		comboBox.setBackground(Color.WHITE);
		comboBox.setBounds(76, 48, 73, 20);
		contentPane.add(comboBox);

	    // Looks like getting the new element to be added and putting it in the comboBox to choose from
		ArrayList<String> elementStrings = BackEnd.getElements();
		comboBox.addItem(null);
		for(String elem : elementStrings) {
			comboBox.addItem(elem);
		}

		comboBox.setSelectedIndex(0);
		
		//isotope name and picker (blank label for filling in element while picking
		JLabel lblIsotopeName = new JLabel("Isotope");
		lblIsotopeName.setBounds(180, 51, 86, 14);
		contentPane.add(lblIsotopeName);
		
		JComboBox<String> pickIsotope = new JComboBox<>();
		pickIsotope.addItem(null);
		pickIsotope.setBackground(Color.WHITE);
		pickIsotope.setBounds(220, 48, 90, 20);
		contentPane.add(pickIsotope);
		pickIsotope.setSelectedIndex(0);

		JLabel label = new JLabel(""); //label for filling in element in from of text box
		label.setForeground(new Color(0, 102, 0));
		label.setVerticalAlignment(SwingConstants.TOP);
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setFont(new Font("Arial", Font.BOLD, 15));
		label.setBounds(200, 75, 26, 21);
		contentPane.add(label);

		textField = new JTextField();
		textField.setFont(new Font("Arial", Font.BOLD, 15));
		textField.setForeground(new Color(0, 102, 0));
		textField.setBounds(260, 75, 38, 20);
		textField.setColumns(10);
		textField.setVisible(false);
		contentPane.add(textField);

		JLabel lblIsotopeTitle = new JLabel("");
		lblIsotopeTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
		lblIsotopeTitle.setForeground(new Color(0, 102, 153));
		lblIsotopeTitle.setBounds(220, 14, 273, 14);
		contentPane.add(lblIsotopeTitle);
		
		//abudance label and text box

		JLabel lblAbundance = new JLabel("Abundance");
		lblAbundance.setBounds(350, 51, 73, 14);
		contentPane.add(lblAbundance);
		
		txtAbundance = new JTextField();
		txtAbundance.setForeground(new Color(0, 102, 0));
		txtAbundance.setFont(new Font("Arial", Font.BOLD, 15));
		txtAbundance.setColumns(10);
		txtAbundance.setBounds(405, 50, 50, 20);
		contentPane.add(txtAbundance);

		JLabel label_1 = new JLabel("%");
		label_1.setBounds(460, 51, 19, 14);
		contentPane.add(label_1);
		
		//halflife label and text box
		JLabel lblHalfLife = new JLabel("Half Life");
		lblHalfLife.setBounds(500, 51, 73, 14);
		contentPane.add(lblHalfLife);

		txtHalfLife = new JTextField();
		txtHalfLife.setForeground(new Color(0, 102, 0));
		txtHalfLife.setFont(new Font("Arial", Font.BOLD, 15));
		txtHalfLife.setColumns(10);
		txtHalfLife.setBounds(540, 50, 60, 20);
		contentPane.add(txtHalfLife);

		JLabel lblS = new JLabel("s");
		lblS.setBounds(605, 51, 19, 14);
		contentPane.add(lblS);
		
		//gamma label, text box and unit
		JLabel lblGamma = new JLabel("Gamma");
		lblGamma.setBounds(650, 51, 73, 14);
		contentPane.add(lblGamma);

		txtGamma = new JTextField();
		txtGamma.setForeground(new Color(0, 102, 0));
		txtGamma.setFont(new Font("Arial", Font.BOLD, 15));
		txtGamma.setColumns(10);
		txtGamma.setBounds(690, 50, 75, 20);
		contentPane.add(txtGamma);
		
		
		//transmuted isotope label
		JLabel lblTransmuted = new JLabel("Transmuted: ");
		lblTransmuted.setFont(new Font("Segoe UI", Font.BOLD, 17));
		lblTransmuted.setForeground(new Color(0, 102, 153));
		lblTransmuted.setBounds(400, 18, 273, 14);
		contentPane.add(lblTransmuted);
		
		JPanel box = new JPanel();
		box.setBounds(340, 10, 440, 80);
		contentPane.add(box);
		box.setBorder(BorderFactory.createEtchedBorder(1));
		
		//add isotope button at the bottom
		JButton btnAddIsotope = new JButton("Save Isotope");
		btnAddIsotope.setBounds(430, 460, 110, 23);
		contentPane.add(btnAddIsotope);
		
		//passwordbox
		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblPassword.setBounds(300, 450, 300, 14);
		contentPane.add(lblPassword);
		
		password = new JTextField();
		password.setFont(new Font("Arial", Font.BOLD, 15));
		password.setForeground(new Color(0, 102, 0));
		password.setBounds(280, 470, 100, 20);
		contentPane.add(password);



		//		// Debug output table, with hardcoded values for testing
		//		Object outputTable[][] = {
		//				{"3.0 - 3.5", "0.0074", "0.09383025"},
		//				{"3.5 - 4.0", "0.00818", "0.14641875"},
		//				{"4.0 - 4.5", "0.00893", "0.21025125"},
		//				{"4.5 - 5.0", "0.00967", "0.28532775"},
		//				{"5.0 - 5.5", "0.0104", "0.37164825"},
		//				{"5.5 - 6.0", "0.0111", "0.46921275"},
		//				{"6.0 - 6.5", "0.01181", "0.57802125"},
		//				{"6.5 - 7.0", "0.01245", "0.69807375"},
		//				{"7.0 - 7.5", "0.0132", "0.82937025"},
		//				{"7.5 - 8.0", "0.0138", "0.97191075"},	
		//		};

		// Column Headers for results output table

		// Results Table to be outputted to the GUI
		DefaultTableModel model = new DefaultTableModel(outputTable, columnNames) {};

		DefaultCellEditor singleClick = new DefaultCellEditor(new JTextField());
		singleClick.setClickCountToStart(1);

		JTable table = new JTable() {
			
			// Set left column to be uneditable and right column to be editable
			// (all are selectable)
			@Override
			public boolean isCellEditable(int row, int column) {
				if(column == 0) {
					return false;

				}
				else
					return true;
			}
			// Customize column widths
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component component = super.prepareRenderer(renderer, row, column);
				TableColumn tableColumn = getColumnModel().getColumn(column);
				if(column == 0)
					tableColumn.setPreferredWidth(75);
				else {
					tableColumn.setPreferredWidth(350);
				}

				return component;
			}
			
		};

		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		table.setRowSelectionAllowed(false);
		JTableHeader header = table.getTableHeader();
		header.setDefaultRenderer(new HeaderRenderer(table));

		// Allow right column to take up all remaining space after left column has been sized
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		tableFormat(table,model);

		table.setDefaultEditor(table.getColumnClass(1), singleClick);
		table.setDefaultEditor(table.getColumnClass(2), singleClick);

		// Add JTable to JScrollPane
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(10, 100, 774, 339);
		//scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		contentPane.add(scrollPane);

		//fill the isotope combo box
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				String selectedItem = (String) comboBox.getSelectedItem();
				
				if(selectedItem != null) {
					Pair numAndName = BackEnd.getAtomicNumberAndName(selectedItem);
					String thisName = numAndName.name.toLowerCase();
					//fill isotope combobox
					pickIsotope.removeAllItems();
					label.setVisible(false);
					label.setText(thisName);
					Pair nameAndNum = BackEnd.getAtomicNumberAndName(selectedItem.toString());
					
					ResultSet preExistingIsotope;
					try {
						Statement s = conn.createStatement();
						String query = "SELECT isotope FROM isotopes WHERE atomicnum='" + nameAndNum.atomicNum + "'";
						preExistingIsotope = s.executeQuery(query);
						
						pickIsotope.addItem(null);
						while (preExistingIsotope.next()) {
					        pickIsotope.addItem(preExistingIsotope.getString(1));
					    }
					} catch (SQLException e) {
						System.out.println(e.getMessage());
					}
					pickIsotope.addItem("New Isotope");
					pickIsotope.setSelectedIndex(0);
				}
				else {
					pickIsotope.removeAllItems();
					pickIsotope.addItem(null);
					label.setVisible(false);
				}
			}
		});
		
		//when an isotope is selected		
		pickIsotope.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				String selectedItem = (String) pickIsotope.getSelectedItem();
				if(selectedItem != null) {
					if(selectedItem == "New Isotope") {
						textField.setText("");
						textField.setVisible(true);
						label.setVisible(true);
						DefaultTableModel model = new DefaultTableModel(outputTable, columnNames) {};
						tableFormat(table, model);
						txtGamma.setText("");
						txtAbundance.setText("");
						txtHalfLife.setText(""); 
						lblTransmuted.setText("Transmuted: ");
					} else {
						label.setVisible(false);
						textField.setVisible(false);
						textField.setText("");
						fill(selectedItem, table, connection);
						try {
							Pair nameAndNum = BackEnd.getAtomicNumberAndName(comboBox.getSelectedItem().toString());
							int isotopeNumber = 0;
							
							//get isotope number
							int i = 0;
							while(isotopeNumber == 0 && i < selectedItem.length() ) {
								try {
									isotopeNumber = Integer.valueOf(selectedItem.substring(i));
									break;
								} catch (Exception E) { i++;}
							}
							
							//get transmuted element
							String transmutedElem = elementStrings.get(nameAndNum.atomicNum).toLowerCase();
							Pair nameAndNumTransmuted = BackEnd.getAtomicNumberAndName(transmutedElem);
							String transmutedName = nameAndNumTransmuted.name;
							String transmutedIsotope = transmutedName + String.valueOf(isotopeNumber);
							lblTransmuted.setText("Transmuted: " + transmutedIsotope);
						}catch(Exception Exception) {}
					}
			}
		}
		});
		
		btnAddIsotope.addActionListener(new ActionListener() {
			//@Override
			public void actionPerformed(ActionEvent e) {
				if(!password.getText().toString().equals("RMTLadmin")) {
					JOptionPane.showMessageDialog(null,  "Correct Password Needed to Save Isotope!", "Incorrect Password", JOptionPane.ERROR_MESSAGE);
					return;
				}
				// Capture user input
				String element = label.getText();
				int isotopeNumber = 0;
				double abundance = 0;
				double halfLife = 0;
				double gamma = 0;
				String errorMessage = "";

				if(element.isEmpty()) {
					(new HighlightListener(comboBox)).highlightBorder();
					errorMessage += "Please enter an element\n";
				}

				try {
					if(pickIsotope.getSelectedItem() == null) {
						(new HighlightListener(pickIsotope)).highlightBorder();
						errorMessage += "<html>Please enter an isotope for <em>isotope number</em>\n";
					}else if(pickIsotope.getSelectedItem() == "New Isotope"){
						isotopeNumber = Integer.parseInt(textField.getText());
					}else {
						for(int i = 0; i < pickIsotope.getSelectedItem().toString().length(); i++){
							try {
								isotopeNumber = Integer.parseInt(pickIsotope.getSelectedItem().toString().substring(i));
							} catch(Exception e1){}
						}
					}
				}
				catch(NumberFormatException err) {
					(new HighlightListener(textField)).highlightBorder();
					errorMessage += "<html>Please enter a number for <em>isotope number</em>\n";
				}


				try {
					abundance = Double.parseDouble(txtAbundance.getText());
				}
				catch(NumberFormatException err) {
					(new HighlightListener(txtAbundance)).highlightBorder();
					errorMessage += "<html>Please enter a number for <em>abundance</em>\n";
				}

				try {
					halfLife = Double.parseDouble(txtHalfLife.getText());
				}
				catch(NumberFormatException err) {
					(new HighlightListener(txtHalfLife)).highlightBorder();
					errorMessage += "<html>Please enter a number for <em>half life</em>\n";
				}
				
				try {
					gamma = Double.parseDouble(txtGamma.getText());
				}
				catch(NumberFormatException err) {
					(new HighlightListener(txtGamma)).highlightBorder();
					errorMessage += "<html>Please enter a number for <em>Gamma</em>\n";
				}

				boolean allFieldsFilled = true;
				boolean allNumbers = true;
				double[][] inputArr = new double[14][5];
				for(int row = 0; row < inputArr.length; row++) {
					double thisEnergy = BackEnd.parseEnergyRange((String)table.getModel().getValueAt(row, 0));
					double thisDeltaRange = 0;
					double thisCrossSection = 0;
					double thisADeltaRange = 0;
					double thisACrossSection = 0;
					try {
						thisDeltaRange = Double.valueOf((String)table.getModel().getValueAt(row, 1));
						thisCrossSection = Double.valueOf((String)table.getModel().getValueAt(row, 2));
						thisADeltaRange = Double.valueOf((String)table.getModel().getValueAt(row, 3));
						thisACrossSection = Double.valueOf((String)table.getModel().getValueAt(row, 4));
					}
					catch(NumberFormatException err) {
						allNumbers = false;
						continue;
					}
					catch (NullPointerException err) {
						allFieldsFilled = false;
						continue;
					}
					inputArr[row][0] = thisEnergy;
					inputArr[row][1] = thisDeltaRange;
					inputArr[row][2] = thisCrossSection;
					inputArr[row][3] = thisADeltaRange;
					inputArr[row][4] = thisACrossSection;
				}

				if(! allNumbers) {	
					errorMessage += "Inputted values must be numbers\n";
				}

				if(! allFieldsFilled) {
					errorMessage += "Please enter all values\n";
				}

				if(! errorMessage.isEmpty()) {
					JOptionPane.showMessageDialog(null,  errorMessage, "Error", JOptionPane.WARNING_MESSAGE);	
				}
				else {
					// All user parameters have been entered correctly
					// Proceed with process execution
					BackEnd.printArray(inputArr);
					
					//get isotope name
					String isotopeName;
					if(pickIsotope.getSelectedItem() == "New Isotope"){
						isotopeName = element.toLowerCase() + String.valueOf(isotopeNumber);
					} else {
						isotopeName = pickIsotope.getSelectedItem().toString().toLowerCase();
					}

					// Check if this isotope already exists in the database
					try {
						Statement s = conn.createStatement();
						String query = "SELECT * FROM isotopes WHERE isotope='"+isotopeName+"'";
						ResultSet preExistingIsotope = s.executeQuery(query);

						if(preExistingIsotope.next() && pickIsotope.getSelectedItem().toString() == "New Isotope") {
							// That means this isotope is already in the database!
							JOptionPane.showMessageDialog(null,  "This isotope already exists!", "Error", JOptionPane.ERROR_MESSAGE);
						}

						else {
							// Proceed with adding the new isotope
							double[][] results = BackEnd.calculateNT(inputArr, isotopeNumber);
							// Get atomic number of original element
							String label = comboBox.getSelectedItem().toString();
							Pair nameAndNum = BackEnd.getAtomicNumberAndName(label);

							int atomicNum = nameAndNum.atomicNum;
							
							//transmuted element
							String transmutedElem = elementStrings.get(atomicNum).toLowerCase();
							Pair nameAndNumTransmuted = BackEnd.getAtomicNumberAndName(transmutedElem);
							String transmutedName = nameAndNumTransmuted.name;
							String transmutedIsotope = transmutedName + String.valueOf(isotopeNumber);
						
							//Add to the isotopes table
							// null, atomicNum, isotopeName, abundance, transmutedIsotope, halfLife

							//INSERT
							//for if is a new isotope add line to isotopes and add table for stuff
							if(pickIsotope.getSelectedItem().toString() == "New Isotope") {
								String insertQuery = "INSERT INTO isotopes (atomicnum,isotope,abundance,transmuted,halflife, gamma) VALUES ('"
									+ atomicNum + "','" + isotopeName + "','" + abundance + "','" 
									+ transmutedIsotope + "','" + halfLife + "','" + gamma +  "');";
								s.execute(insertQuery);
								
								// Add a new table called isotopeName
								// fill it with rows for each energy breakdown:
								// null, energy, deltaRange,nT, crossSection, AnT, AcrossSection
								String addTableQuery = "CREATE TABLE " + isotopeName +
									" (ID COUNTER PRIMARY KEY, "
									+ "E VARCHAR(255), "
									+ "range VARCHAR(255), "
									+ "nT VARCHAR(255), "
									+ "crossSection VARCHAR(255),"
									+ "Arange VARCHAR(255), "
									+ "AnT VARCHAR(255), "
									+ "AcrossSection VARCHAR(255));";

								s.execute(addTableQuery);
							} else { //if the isotope is not new update like in isotopes table and clear isotope table
;								String insertQuery = "UPDATE isotopes SET atomicnum = '"+atomicNum+"', abundance = '"+ abundance +
										"',transmuted = '"+transmutedIsotope+"', halflife = '"+halfLife+
										"' WHERE isotope = '"+ isotopeName+"'";
								s.execute(insertQuery);
								
								String addTableQuery = "DELETE FROM " + isotopeName+ "";

								s.execute(addTableQuery);
							}
							
							
							//otherwise update already in place isotopes

							// INSERT new data into empty table (new or cleared)
							for(int i = 0; i < results.length; i++) {
								double energy = results[i][0];
								double range = results[i][1];
								double nT = results[i][2];
								double crossSection = results[i][3];
								double Arange = results[i][4];
								double AnT = results[i][5];
								double AcrossSection = results[i][6];

								String insertIntoNewTableQuery = "INSERT INTO "+isotopeName+" (E,range,nT,crossSection,Arange,AnT, AcrossSection) VALUES ('"
										+ energy + "','" + range + "','" + nT + "','" 
										+ crossSection + "','" + Arange +"','" + AnT + "','" + AcrossSection +"');"; 

								s.execute(insertIntoNewTableQuery); 
							}
							JOptionPane.showMessageDialog(null,  "Isotope Saved Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
						}

					} catch (SQLException err) {
						err.printStackTrace();
					}	
				}
			}
		});

		//things that get checked every time the text field is touched
		textField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				validate(e);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				validate(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				validate(e);
			}

			public void validate(DocumentEvent e) {
				String labelText = label.getText();		
				if(labelText != null && !labelText.isEmpty() && textField != null & !textField.getText().isEmpty()) {
					int isotopeNum = 0;
					try {
						isotopeNum = Integer.parseInt(textField.getText());
					}
					catch(NumberFormatException err) {
						return;
					}
					lblIsotopeTitle.setText(": " + labelText.toLowerCase() + isotopeNum);
					
					Pair nameAndNum = BackEnd.getAtomicNumberAndName(comboBox.getSelectedItem().toString());
					String isotopeNumber = textField.getText();
					
					//get transmuted element to show in the UI
					String transmutedElem = elementStrings.get(nameAndNum.atomicNum).toLowerCase();
					Pair nameAndNumTransmuted = BackEnd.getAtomicNumberAndName(transmutedElem);
					String transmutedName = nameAndNumTransmuted.name;
					String transmutedIsotope = transmutedName + String.valueOf(isotopeNumber);
					lblTransmuted.setText("Transmuted: " + transmutedIsotope);
				}
				if(labelText == null || textField == null || textField.getText().isEmpty()) {
					lblIsotopeTitle.setText("");
					lblTransmuted.setText("Transmuted: ");
				}
			}
		});	
	}
	
	private void tableFormat(JTable table, DefaultTableModel model) {
		table.setModel(model);
		table.getColumnModel().getColumn(0).setCellRenderer(new CellColourRenderer());

		// Prevent columns from being resized and/or reordered
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);
		//set row heights
		int headerHeight = table.getTableHeader().getPreferredSize().height;
		table.setRowHeight((200-headerHeight)/10);

		for(int row = 0; row < outputTable.length; row++) {
			table.setRowHeight(row, 30);
		}
	}
	
	private void fill(String name, JTable table, Connection connection) {
		ResultSet preExistingIsotope;
		Object newTable[][] = {
				{"1.0 - 2.5", null, null, null, null},
				{"1.5 - 2.5", null, null, null, null},
				{"2.0 - 2.5", null, null, null, null},
				{"2.5 - 3.0", null, null, null, null},
				{"3.0 - 3.5", null, null, null, null},
				{"3.5 - 4.0", null, null, null, null},
				{"4.0 - 4.5", null, null, null, null},
				{"4.5 - 5.0", null, null, null, null},
				{"5.0 - 5.5", null, null, null, null},
				{"5.5 - 6.0", null, null, null, null},
				{"6.0 - 6.5", null, null, null, null},
				{"6.5 - 7.0", null, null, null, null},
				{"7.0 - 7.5", null, null, null, null},
				{"7.5 - 8.0", null, null, null, null},	
		};
		try {
			//fill in table
			Statement s = connection.createStatement();
			String query = "SELECT range, crossSection, Arange, AcrossSection FROM "+name;
			preExistingIsotope = s.executeQuery(query);
			int i=0;
			while(preExistingIsotope.next()){
				newTable[i][1] = preExistingIsotope.getString(1);
				newTable[i][2] = preExistingIsotope.getString(2);
				newTable[i][3] = preExistingIsotope.getString(3);
				newTable[i][4] = preExistingIsotope.getString(4);
				i++;
			}
		}catch(Exception E){
			System.out.println(E.getMessage());
		}
		try {
			Statement s = connection.createStatement();
			String query = "SELECT abundance, halflife, gamma FROM isotopes WHERE isotope='"+name+"'";
			ResultSet preExistingIsotope1 = s.executeQuery(query);
			preExistingIsotope1.next();
			txtAbundance.setText(preExistingIsotope1.getString(1));
			txtHalfLife.setText(preExistingIsotope1.getString(2));
			txtGamma.setText(preExistingIsotope1.getString(3));
		}catch(Exception E){
			System.out.println(E.getMessage());
		}
		DefaultTableModel newmodel = new DefaultTableModel(newTable, columnNames) {};
		table.setModel(newmodel);
		tableFormat(table, newmodel);
		//fill table 
	}
}
