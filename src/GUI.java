import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.accessibility.AccessibleContext;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * This class handles the GUI creation
 * It collects the user input data and sends to BackEnd to process
 * then populates the results section with the calculations returned from BackEnd
 */

@SuppressWarnings("serial")
public class GUI extends JFrame {
	private static JPanel contentPane;
	// Connection to MS Access Database
	static Connection conn;

	static int NUM_INPUTS = 10;
	static Object[][] microshieldTable;

	//Creates the fractional weight text input fields
	private static JTextField txtWeightE1;
	private static JTextField txtWeightE2;
	private static JTextField txtWeightE3;
	private static JTextField txtWeightE4;
	private static JTextField txtWeightE5;
	private static JTextField txtWeightE6;
	private static JTextField txtWeightE7;
	private static JTextField txtWeightE8;
	private static JTextField txtWeightE9;
	private static JTextField txtWeightE10;
	
    //Creates the text field for the overall activity decay
	private JTextField txtOverallActivityDecay;
	private JTextField txtOverallGamma;

	// Constants used to calculate irradiation and decay times
	int NUM_SECS_PER_MIN = 60;
	int NUM_SECS_PER_HOUR = NUM_SECS_PER_MIN * 60;
	int NUM_SECS_PER_DAY = NUM_SECS_PER_HOUR * 24;

	ArrayList<Element> elementObjects;
	Date finalDateTime;
	static ArrayList<Object[]> totalOverallResults;

	// Input components
	static BigDecimal[] weightInputs;
	static String[] elementInputs;
	static ArrayList<HighlightListener> toHighlight = new ArrayList<>();
	static Map<String, JTextField> textFieldMap;

	static JTabbedPane tabbedPane;

	static JButton btnClearValues2;


	// Map to store references to comboboxes and their variable names
	static Map<String, JComboBox<String>> comboBoxMap;

	// Result object to store the results of calling BackEnd.calculateOutputs on a step's input data
	static Result thisStepResults;

	static JButton btnCalculate;
	// Boolean to keep track of whether or not a calculation has yet been made
	// If it is false, functions like export to CSV are not available
	static boolean calculateClicked = false;

	GUI g = this;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		conn = BackEnd.makeDatabaseConnection();	// Connect to MS Access Database

		//		 To run the GUI
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI frame = new GUI();
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
					Image icon = Toolkit.getDefaultToolkit().getImage("./QueensLogo_colour.jpg");    
					frame.setIconImage(icon);
					ResultsPanel.createResultsPanel();
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null,  "Error occured while starting up" + e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
	}

	/*	This method adds a button which will create a new tab to the current last tab of the tabbed pane.
	 * 	When clicked, the button will add a new "StepPanel" tab to the tabbedPane
	 * 	It uses the number of existing tabs to insert a new tab after the last tab.
	 */
	public static void addButtonToTab(JTabbedPane tabbedPane) {

		//Gets the number of tabs currently open
		int numTabs = tabbedPane.getTabCount();

		//Adding the button to create a new tab
		JButton addTabButton = new JButton("+");
		addTabButton.setPreferredSize(new Dimension(20,20));
		addTabButton.setOpaque(true);
		addTabButton.setBorderPainted(true);
		addTabButton.setMargin(new Insets(0,0,0,0));
		addTabButton.setToolTipText("Add a new tab");
		addTabButton.setContentAreaFilled(true);
		addTabButton.setFocusPainted(true);
		addTabButton.setFocusable(false);

		tabbedPane.setTabComponentAt(numTabs - 1, addTabButton);
		tabbedPane.setEnabledAt(numTabs - 1, false);

		//Adds the ActionListener which will insert a new tab when the button is pressed
		ActionListener addNewTabListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				//Adds the closable tab to the panel
				addClosableTab(tabbedPane);
				tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 2);
			}
		};
		addTabButton.addActionListener(addNewTabListener);
	}

	public static void clearValues() {
				String comboName;
				String textFieldName;
				JComboBox<String> thisCombo;
				JTextField thisWeight;

				for(int i = 0; i < NUM_INPUTS; i++) {
					String thisNum = String.valueOf(i+1);
					comboName = "comboBox" + thisNum;
					textFieldName = "txtWeightE" + thisNum;
					thisCombo = comboBoxMap.get(comboName);
					thisWeight = textFieldMap.get(textFieldName);

					// First item of each dropdown is null to signify not using this field
					thisCombo.setSelectedIndex(0);
					(new HighlightListener(thisCombo)).defaultBorder();
					(new HighlightListener(thisWeight)).defaultBorder();
					thisWeight.setText("");
				}
	}
	
	// This method will add the next closable tab to the tabbedPane, called from a click of the addTabButton
	public static void addClosableTab(JTabbedPane tabbedPane) {
		int numTabs = tabbedPane.getTabCount();
		StepPanel secondLastTab = (StepPanel)tabbedPane.getComponentAt(numTabs - 2);	// Grab the second last tab (last tab is null "add new tab" tab)

		int tabNum = secondLastTab.tabNumber + 1;
		int index = numTabs - 1;
		StepPanel panel = new StepPanel(tabNum);
		String tabName = "Step " + String.valueOf(tabNum);
		tabbedPane.addTab(tabName, panel);

		// Create the close button
		JButton closeButton = makeCloseButton();

		JPanel tabPanel = new JPanel();
		JLabel tabLabel = new JLabel(tabName);
		tabLabel.setOpaque(false);

		tabPanel.add(tabLabel);
		tabPanel.setOpaque(false);
		if(tabNum > 1)
			
			//In the case of more than one tab add the close button and add an actionlistener
			tabPanel.add(closeButton);

		tabPanel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				tabbedPane.setSelectedIndex(tabbedPane.indexOfComponent(panel));	
			}

		});
		tabbedPane.insertTab(tabName, null, panel, null, index);
		tabbedPane.setTabComponentAt(index, tabPanel);

		addButtonToTab(tabbedPane);

		//Add response when the close button is clicked
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int tabToRemove = tabbedPane.indexOfComponent(panel);
				
				//Checks to ensure more than one (and null) step panel exists before closing
				if(tabbedPane.getTabCount() > 2) {
					
					//Gets the index of the tabbed pane to remove
					int selectedTabIndex = tabbedPane.getSelectedIndex();
					String[] options = {"Yes, close " + tabName, "Cancel"};
					int choice = JOptionPane.showOptionDialog(contentPane, "Are you sure you want to close " + tabName + "?" , "Close " + tabName, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
					if(choice == 0) {
						tabbedPane.remove(tabToRemove);

						if(selectedTabIndex == tabToRemove) {  //if removing tab we are currently viewing then it cycles to another tab once it closes
							if(selectedTabIndex != 0)
								tabbedPane.setSelectedIndex(tabToRemove - 1);
						}
					}
				}
				else
					JOptionPane.showMessageDialog(null,  "Must have at least one step!", "Error", JOptionPane.WARNING_MESSAGE);
			}
		});
	}

	public static JButton makeCloseButton() {
		JButton closeButton = new JButton("x");
		int size = 18;
		closeButton.setPreferredSize(new Dimension(size, size));
		closeButton.setToolTipText("Close this tab");
		closeButton.setContentAreaFilled(true);
		closeButton.setFocusPainted(true);
		closeButton.setFocusable(false);
		closeButton.setBorder(BorderFactory.createEmptyBorder());
		closeButton.setRolloverEnabled(true);
		closeButton.setMargin(new Insets(10,10,10,10));

		return closeButton;
	}


	//tabs to add steps
	public static void addFirstClosableTab(JTabbedPane tabbedPane) {
		int tabNum = 1;

		String tabName = "Step " + String.valueOf(tabNum);
		StepPanel panel = new StepPanel(tabNum);
		//TODO
		tabbedPane.addTab(tabName, panel);	

		JPanel tabPanel = new JPanel();
		JLabel tabLabel = new JLabel(tabName);
		tabLabel.setFocusable(false);
		tabLabel.setOpaque(false);
		tabPanel.setOpaque(false);
		tabPanel.setFocusable(false);

		JButton closeButton = makeCloseButton();

		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(tabbedPane.getTabCount() > 2) {
					String[] options = {"Yes, close " + tabName, "Cancel"};
					int choice = JOptionPane.showOptionDialog(contentPane, "Are you sure you want to close " + tabName + "?" , "Close " + tabName, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
					if(choice == 0)
						tabbedPane.remove(0);
				}
				else
					JOptionPane.showMessageDialog(null,  "Must have at least one step!", "Error", JOptionPane.WARNING_MESSAGE);
			}
		});

		tabPanel.add(tabLabel);
		tabPanel.add(closeButton);
		tabPanel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				tabbedPane.setSelectedIndex(0);
			}

		});
		//TODO
		tabbedPane.insertTab(tabName, null, panel, null, 0);
		tabbedPane.setTabComponentAt(0, tabPanel);
	}
	
	//the drop down picker to change from a proton calculation to an alpha calculation
	static String[] calcs = {"Proton(p,n)", "Alpha(a,n)"};
	private static JComboBox<String> switchCalculation = new JComboBox<String>(calcs);
	
	public static String getSelectedItem(){
        return (String) switchCalculation.getSelectedItem();
	}

	/**
	 * Create the frame.
	 */
	public GUI() {	

		// Set Look and Feel of application to "Windows"
		try {

			for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if("Windows".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null,  "Error occured with User Interface", "Error", JOptionPane.WARNING_MESSAGE);

		}

		setResizable(true);
		setTitle("Activity Calculator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1012, 930);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);

		// Create custom borders to be used on user input components
		Color grey = new Color(170,170,170);
		Border defaultBorder = BorderFactory.createLineBorder(grey);
		Border emptyBorder = BorderFactory.createEmptyBorder(0,2,0,0);
		// compoundBorder includes a grey outline and a 2px empty border padding inside
		Border compoundBorder = BorderFactory.createCompoundBorder(defaultBorder, emptyBorder);



		// Encompassing panel which contains all other UI elements
		JPanel overallPanel = new JPanel();

		overallPanel.setBounds(0, 0, 984, 850);
		overallPanel.setPreferredSize(new Dimension(984,850));
		overallPanel.setLayout(null);

		// Encompassing scroll Pane
		JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		scrollPane.setBounds(0, 0, 984, 969);
		scrollPane.setViewportView(overallPanel);
		scrollPane.setPreferredSize(new Dimension(500,500));
		setContentPane(scrollPane);

		// TabbedPane holds Steps in tabs and allows new tabs to be added
		tabbedPane = new JTabbedPane();
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.setBounds(10, 200, 964, 600);
		overallPanel.add(tabbedPane);

		// First tab holding "step 1" in the tabbed pane
		// Add a null tab which will act as the "add new tab" button
		StepPanel stepPanel = new StepPanel(0);
		tabbedPane.addTab("nulltab", null, stepPanel);
		addButtonToTab(tabbedPane);
		addFirstClosableTab(tabbedPane);
		tabbedPane.setSelectedIndex(0);
		//addClosableTab(tabbedPane);

		// Target Materials Panel
		JPanel panelTargetMaterials = new JPanel();
		panelTargetMaterials.setBackground(Color.WHITE);
		panelTargetMaterials.setBounds(10, 10, 970, 170);
		overallPanel.add(panelTargetMaterials);
		panelTargetMaterials.setLayout(null);

		JLabel lblTargetMaterials = new JLabel("Target Materials");
		lblTargetMaterials.setForeground(new Color(0, 102, 153));
		lblTargetMaterials.setFont(new Font("Segoe UI", Font.BOLD, 17));
		lblTargetMaterials.setBounds(10, 11, 205, 21);
		panelTargetMaterials.add(lblTargetMaterials);

		// Panel to hold the user input components related to the target materials
		JPanel panelTargetInputs = new JPanel();
		panelTargetInputs.setBounds(20, 43, 937, 110);
		panelTargetMaterials.add(panelTargetInputs);

		// Parameters used to evenly space the comboboxes and fractional weight input text fields
		int init = 120;
		int spacing = 75;
		int y = 25;
		int y2 = 65;
		int width = 65;
		int height = 20;

		// Create 10 comboboxes on new panel
		JComboBox<String> comboBox1 = new JComboBox<String>();
		JComboBox<String> comboBox2 = new JComboBox<String>();
		JComboBox<String> comboBox3 = new JComboBox<String>();
		JComboBox<String> comboBox4 = new JComboBox<String>();
		JComboBox<String> comboBox5 = new JComboBox<String>();
		JComboBox<String> comboBox6 = new JComboBox<String>();
		JComboBox<String> comboBox7 = new JComboBox<String>();
		JComboBox<String> comboBox8 = new JComboBox<String>();
		JComboBox<String> comboBox9 = new JComboBox<String>();
		JComboBox<String> comboBox10 = new JComboBox<String>();

		// Create action listener which will repopulate the dropdowns (so that they don't include the option to select a
		// previously chosen element)
		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				BackEnd.repopulateDropdowns(comboBoxMap);
			}
		};

		// Create target elements / fractional weight user input components
		panelTargetInputs.setLayout(null);

		JLabel lblTotalWeight = new JLabel("<html>Total<br>weight</html>");
		lblTotalWeight.setBounds(886, 14, 37, 40);
		panelTargetInputs.add(lblTotalWeight);

		JTextPane totalWeightTextArea = new JTextPane();
		totalWeightTextArea.setText("0.0");
		totalWeightTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		totalWeightTextArea.setBackground(SystemColor.control);
		totalWeightTextArea.setBounds(873, 60, 65, 20);
		panelTargetInputs.add(totalWeightTextArea);

		StyledDocument doc = totalWeightTextArea.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);

		comboBox1.addActionListener(actionListener);
		comboBox1.setBackground(Color.WHITE);
		comboBox1.setBounds(init, y, width, height);
		panelTargetInputs.add(comboBox1);
		comboBox1.setBorder(compoundBorder);

		comboBox2.addActionListener(actionListener);
		comboBox2.setBackground(Color.WHITE);
		comboBox2.setBounds(init + spacing, y, width, height);
		panelTargetInputs.add(comboBox2);
		comboBox2.setBorder(compoundBorder);

		comboBox3.addActionListener(actionListener);
		comboBox3.setBackground(Color.WHITE);
		comboBox3.setBounds(init + 2 * spacing, y, width, height);
		panelTargetInputs.add(comboBox3);
		comboBox3.setBorder(compoundBorder);

		comboBox4.addActionListener(actionListener);
		comboBox4.setBackground(Color.WHITE);
		comboBox4.setBounds(init + 3 * spacing, y, width, height);
		panelTargetInputs.add(comboBox4);
		comboBox4.setBorder(compoundBorder);

		comboBox5.addActionListener(actionListener);
		comboBox5.setBackground(Color.WHITE);
		comboBox5.setBounds(init + 4 * spacing, y, width, height);
		panelTargetInputs.add(comboBox5);
		comboBox5.setBorder(compoundBorder);

		comboBox6.addActionListener(actionListener);
		comboBox6.setBackground(Color.WHITE);
		comboBox6.setBounds(init + 5 * spacing, y, width, height);
		panelTargetInputs.add(comboBox6);
		comboBox6.setBorder(compoundBorder);

		comboBox7.addActionListener(actionListener);
		comboBox7.setBackground(Color.WHITE);
		comboBox7.setBounds(init + 6 * spacing, y, width, height);
		panelTargetInputs.add(comboBox7);
		comboBox7.setBorder(compoundBorder);

		comboBox8.addActionListener(actionListener);
		comboBox8.setBackground(Color.WHITE);
		comboBox8.setBounds(init + 7 * spacing, y, width, height);
		panelTargetInputs.add(comboBox8);
		comboBox8.setBorder(compoundBorder);

		comboBox9.addActionListener(actionListener);
		comboBox9.setBackground(Color.WHITE);
		comboBox9.setBounds(init + 8 * spacing, y, width, height);
		panelTargetInputs.add(comboBox9);
		comboBox9.setBorder(compoundBorder);

		comboBox10.addActionListener(actionListener);
		comboBox10.setBackground(Color.WHITE);
		comboBox10.setBounds(init + 9 * spacing, y, width, height);
		panelTargetInputs.add(comboBox10);
		comboBox10.setBorder(compoundBorder);

		JLabel lblMaterials = new JLabel("Element");
		lblMaterials.setBounds(31, 28, 46, 14);
		panelTargetInputs.add(lblMaterials);

		JLabel lblFractionalWeight = new JLabel("Fractional Weight");
		lblFractionalWeight.setBounds(10, 68, 100, 14);
		panelTargetInputs.add(lblFractionalWeight);

		txtWeightE1 = new JTextField();
		txtWeightE1.setBounds(init, y2, width, height);
		panelTargetInputs.add(txtWeightE1);
		txtWeightE1.setColumns(10);
		txtWeightE1.setBorder(compoundBorder);

		txtWeightE2 = new JTextField();
		txtWeightE2.setColumns(10);
		txtWeightE2.setBounds(init + spacing, y2, width, height);
		panelTargetInputs.add(txtWeightE2);
		txtWeightE2.setBorder(compoundBorder);

		txtWeightE3 = new JTextField();
		txtWeightE3.setColumns(10);
		txtWeightE3.setBounds(init + 2 * spacing, y2, width, height);
		panelTargetInputs.add(txtWeightE3);
		txtWeightE3.setBorder(compoundBorder);

		txtWeightE4 = new JTextField();
		txtWeightE4.setColumns(10);
		txtWeightE4.setBounds(init + 3 * spacing, y2, width, height);
		panelTargetInputs.add(txtWeightE4);
		txtWeightE4.setBorder(compoundBorder);

		txtWeightE5 = new JTextField();
		txtWeightE5.setColumns(10);
		txtWeightE5.setBounds(init + 4 * spacing, y2, width, height);
		panelTargetInputs.add(txtWeightE5);
		txtWeightE5.setBorder(compoundBorder);

		txtWeightE6 = new JTextField();
		txtWeightE6.setColumns(10);
		txtWeightE6.setBounds(init + 5 * spacing, y2, width, height);
		panelTargetInputs.add(txtWeightE6);
		txtWeightE6.setBorder(compoundBorder);

		txtWeightE7 = new JTextField();
		txtWeightE7.setColumns(10);
		txtWeightE7.setBounds(init + 6 * spacing, y2, width, height);
		panelTargetInputs.add(txtWeightE7);
		txtWeightE7.setBorder(compoundBorder);

		txtWeightE8 = new JTextField();
		txtWeightE8.setColumns(10);
		txtWeightE8.setBounds(init + 7 * spacing, y2, width, height);
		panelTargetInputs.add(txtWeightE8);
		txtWeightE8.setBorder(compoundBorder);

		txtWeightE9 = new JTextField();
		txtWeightE9.setColumns(10);
		txtWeightE9.setBounds(init + 8 * spacing, y2, width, height);
		panelTargetInputs.add(txtWeightE9);
		txtWeightE9.setBorder(compoundBorder);

		txtWeightE10 = new JTextField();
		txtWeightE10.setColumns(10);
		txtWeightE10.setBounds(init + 9 * spacing, y2, width, height);
		panelTargetInputs.add(txtWeightE10);
		txtWeightE10.setBorder(compoundBorder);

		// Set the input fields as instances of HighlightListener class which will highlight incorrectly filled fields
		new HighlightListener(txtWeightE1);
		new HighlightListener(txtWeightE2);
		new HighlightListener(txtWeightE3);
		new HighlightListener(txtWeightE4);
		new HighlightListener(txtWeightE5);
		new HighlightListener(txtWeightE6);
		new HighlightListener(txtWeightE7);
		new HighlightListener(txtWeightE8);
		new HighlightListener(txtWeightE9);
		new HighlightListener(txtWeightE10);

		new HighlightListener(comboBox1);
		new HighlightListener(comboBox2);
		new HighlightListener(comboBox3);
		new HighlightListener(comboBox4);
		new HighlightListener(comboBox5);
		new HighlightListener(comboBox6);
		new HighlightListener(comboBox7);
		new HighlightListener(comboBox8);
		new HighlightListener(comboBox9);
		new HighlightListener(comboBox10);

		// Store name : variable references in a map to easily access variables in future
		comboBoxMap = new HashMap<String, JComboBox<String>>();
		comboBoxMap.put("comboBox1", comboBox1);
		comboBoxMap.put("comboBox2", comboBox2);
		comboBoxMap.put("comboBox3", comboBox3);
		comboBoxMap.put("comboBox4", comboBox4);
		comboBoxMap.put("comboBox5", comboBox5);
		comboBoxMap.put("comboBox6", comboBox6);
		comboBoxMap.put("comboBox7", comboBox7);
		comboBoxMap.put("comboBox8", comboBox8);
		comboBoxMap.put("comboBox9", comboBox9);
		comboBoxMap.put("comboBox10", comboBox10);

		textFieldMap = new HashMap<>();
		textFieldMap.put("txtWeightE1", txtWeightE1);
		textFieldMap.put("txtWeightE2", txtWeightE2);
		textFieldMap.put("txtWeightE3", txtWeightE3);
		textFieldMap.put("txtWeightE4", txtWeightE4);
		textFieldMap.put("txtWeightE5", txtWeightE5);
		textFieldMap.put("txtWeightE6", txtWeightE6);
		textFieldMap.put("txtWeightE7", txtWeightE7);
		textFieldMap.put("txtWeightE8", txtWeightE8);
		textFieldMap.put("txtWeightE9", txtWeightE9);
		textFieldMap.put("txtWeightE10",txtWeightE10);

		// Create document listener to update the total weight sum display
		// as the user enters fractional weights
		DocumentListener updateLabel = new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateLabel();				
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				updateLabel();
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				updateLabel();				
			}
			public void updateLabel() {
				String totalWeight = BackEnd.getTotalWeight(textFieldMap);
				totalWeightTextArea.setText(totalWeight);
			}
		};

		// Iterate through the text fields in the text field map, adding the updateLabel document listener to each one
		// Also, add functionality so that when a field is "tabbed" to by the user, the contents of the field will be selected
		// (default functionality is to not have the contents highlighted)
		Iterator<Map.Entry<String,JTextField>> it = textFieldMap.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<String, JTextField> pair = (Map.Entry<String, JTextField>)it.next();
			JTextField thisTextField = pair.getValue();
			thisTextField.getDocument().addDocumentListener(updateLabel);
			thisTextField.addFocusListener(new java.awt.event.FocusAdapter() {
				public void focusGained(java.awt.event.FocusEvent evt) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							thisTextField.selectAll();
						}
					});
				}
			});
		}

		// Fill the dropdowns with the elements from the database
		BackEnd.populateDropdowns(comboBoxMap);

		JButton btnAddANew = new JButton("Add or edit an isotope");
		btnAddANew.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				AddNewIsotope addFrame = new AddNewIsotope(conn);
				addFrame.setLocationRelativeTo(null);
				addFrame.setVisible(true);
			}
		});
		btnAddANew.setBounds(202, 9, 138, 23);
		panelTargetMaterials.add(btnAddANew);


		//debug autopopulate button (make visible to use)
		JButton btnAutopopulate = new JButton("AUTO_POPULATE");
		btnAutopopulate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				comboBox1.setSelectedIndex(27);

	
				txtWeightE1.setText("1");
				
				for(int i = 0; i < tabbedPane.getTabCount() - 1; i++) {
					StepPanel thisStep = (StepPanel) tabbedPane.getComponentAt(i);
					thisStep.txtEnergy.setText("8");
					thisStep.txtCurrent.setText("12.5");
				    thisStep.txtIrradiationTime.setText("584000");
				    thisStep.txtDecayTime.setText("584000");
				}

			}
		});
		btnAutopopulate.setBounds(0,0,10,10);
		panelTargetMaterials.add(btnAutopopulate);
		btnAutopopulate.setVisible(false);

		// When calculate button is pressed, capture user inputs, use them to perform calculations
		// and output resulting values
		btnCalculate = new JButton("Calculate");
		btnCalculate.setForeground(new Color(0, 100, 0));
		btnCalculate.setBounds(10, 810, 170, 58);
		overallPanel.add(btnCalculate);

		//total activity decay
		txtOverallActivityDecay = new JTextField();
		txtOverallActivityDecay.setBackground(StepPanel.lightGrey);
		txtOverallActivityDecay.setEditable(false);
		txtOverallActivityDecay.setBounds(804, 810, 132, 20);
		overallPanel.add(txtOverallActivityDecay);
		txtOverallActivityDecay.setColumns(10);

		JLabel lblTotalActivityDecay = new JLabel("Total Activity Decay(sum of each step)");
		lblTotalActivityDecay.setHorizontalAlignment(SwingConstants.TRAILING);
		lblTotalActivityDecay.setVerticalAlignment(SwingConstants.TOP);
		lblTotalActivityDecay.setBounds(605, 810, 195, 34);
		overallPanel.add(lblTotalActivityDecay);

		JLabel lblCi = new JLabel("<html> 10<sup>6</sup> bq </html>");
		lblCi.setBounds(946, 805, 38, 20);
		overallPanel.add(lblCi);
		
		//total gamma
		txtOverallGamma = new JTextField();
		txtOverallGamma.setBackground(StepPanel.lightGrey);
		txtOverallGamma.setEditable(false);
		txtOverallGamma.setBounds(804, 845, 132, 20);
		overallPanel.add(txtOverallGamma);
		txtOverallGamma.setColumns(10);

		JLabel lblGamma = new JLabel("Total Gamma (sum of each step)");
		lblGamma.setHorizontalAlignment(SwingConstants.TRAILING);
		lblGamma.setVerticalAlignment(SwingConstants.TOP);
		lblGamma.setBounds(628, 850, 164, 34);
		overallPanel.add(lblGamma);

		JLabel lblG = new JLabel("<html>µSv/h at 30cm</html>");
		lblG.setBounds(946, 835, 40, 40);
		overallPanel.add(lblG);

		//overall results button
		JButton overallResultsBtn = new JButton("See Overall Results");
		MouseAdapter errorMouseListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// Make a popup to tell the user they cannot get results until they make a calculation
				JOptionPane.showMessageDialog(null,  "Cannot see results until a calculation is made", "Error", JOptionPane.WARNING_MESSAGE);
			}
		};
		overallResultsBtn.addMouseListener(errorMouseListener);
		overallResultsBtn.setBounds(540, 825, 130, 25);
		overallPanel.add(overallResultsBtn);
		
		switchCalculation.setBackground(Color.WHITE);
		switchCalculation.setBounds(190, 825, 150, 30);
		overallPanel.add(switchCalculation);
		switchCalculation.setBorder(compoundBorder);
		
		/*
		 * reset button
		 * resets all values in all boxes
		*/
		JButton btnReset = new JButton("RESET ALL VALUES");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BackEnd.resetProgram();
				MouseListener[] list = overallResultsBtn.getMouseListeners();
				overallResultsBtn.removeMouseListener(list[0]);
				overallResultsBtn.addMouseListener(errorMouseListener);
			}
		});
		btnReset.setBounds(500, 9, 150, 23);
		panelTargetMaterials.add(btnReset);
		
		// When calculate button is clicked:
		btnCalculate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				toHighlight.clear();
				calculateClicked = true;
				// Fill weightInputs and elementInputs arrays with user inputed fractional weights and elements
				weightInputs = new BigDecimal[NUM_INPUTS];
				elementInputs = new String[NUM_INPUTS];

				String comboName;
				String textFieldName;
				JComboBox<String> thisCombo;
				JTextField thisWeightField;
				for(int i = 0; i < NUM_INPUTS; i++) {
					String thisNum = String.valueOf(i+1);
					comboName = "comboBox" + thisNum;
					textFieldName = "txtWeightE" + thisNum;
					thisCombo = comboBoxMap.get(comboName);
					thisWeightField = textFieldMap.get(textFieldName);

					// Fill the array at this index with an empty string if a value is not inputed
					if(thisCombo.getSelectedItem() == null)
						elementInputs[i] = null;
					else
						elementInputs[i] = String.valueOf(thisCombo.getSelectedItem());
					weightInputs[i] = BackEnd.checkBigDecimal(thisWeightField.getText().toString());
				}
				String errorMessage = "";

				// Data to do with individual steps
				for(int k = 0; k < tabbedPane.getTabCount() - 1; k++) {				
					errorMessage += BackEnd.checkStepInputs(k);
				}

				errorMessage += BackEnd.checkInputs();

				if(! errorMessage.isEmpty()){
					JOptionPane.showMessageDialog(null,  errorMessage, "Error", JOptionPane.WARNING_MESSAGE);
					for(HighlightListener field : toHighlight) {
					field.highlightBorder();
					}
				}

				else {	// If all inputs have been entered correctly

					double totalActivityDecay = 0;
					double totalGamma = 0;

					totalOverallResults = new ArrayList<>();

					// For each step in the tabbedPane, calculate results
					for(int k = 0; k < tabbedPane.getTabCount() - 1; k++) {
						StepPanel thisStep = (StepPanel) tabbedPane.getComponentAt(k);

						

						// Capture user inputed energy and current values
						double energy = BackEnd.checkDouble(thisStep.txtEnergy.getText().toString());
						thisStep.energy = energy;
						double current = BackEnd.checkDouble(thisStep.txtCurrent.getText().toString());
						thisStep.current = current;
						
						int irradiationTime = 0;
						int decayTime = 0;
						
						boolean error = false;
						//gets the irradiation time from numbers inputed and if needed translates scientific notation
						a:try {
							irradiationTime = Integer.parseInt(thisStep.txtIrradiationTime.getText());
						} catch (Exception e1){
							String txt = thisStep.txtIrradiationTime.getText();
							int index = txt.indexOf('E');
							if(index == -1) {
								new HighlightListener(thisStep.txtIrradiationTime).highlightBorder();
								error = true;
								break a;
							}
							Double time = Double.parseDouble(txt.substring(0,index));
							int power = Integer.parseInt(txt.substring(index+1));
							irradiationTime = (int) (time*Math.pow(10, power));
						}
						
						//gets the decay time from numbers inputed and if needed translates scientific notation
						b:try {
							decayTime = Integer.parseInt(thisStep.txtDecayTime.getText());
						} catch (Exception e1){
							String txt = thisStep.txtDecayTime.getText();
							int index = txt.indexOf('E');
							if(index == -1) {
								new HighlightListener(thisStep.txtDecayActivity).highlightBorder();
								error = true;
								break b;
							}
							Double time = Double.parseDouble(txt.substring(0,index));
							int power = Integer.parseInt(txt.substring(index+1));
							decayTime = (int) (time*Math.pow(10, power));
						}
						//if the numbers in the times are not numbers (normal or scientific notation) if not error and exit calculation
						if(error == true) {
							JOptionPane.showMessageDialog(null,  "Please Make sure times are either in standard notation (ex. 750000) or in scientific notation (ex. 7.5E5)", "Error", JOptionPane.WARNING_MESSAGE);
							break;
						}
						
						thisStep.irradiationTime = irradiationTime;
						thisStep.decayTime = decayTime; 
						

						double[] irradiationParams = new double[4];
						irradiationParams[0] = energy;
						irradiationParams[1] = current;
						irradiationParams[2] = irradiationTime;
						irradiationParams[3] = decayTime;
						
						//the actual calculation
						elementObjects = BackEnd.populateElementObjects(elementInputs, weightInputs, irradiationParams);	
						thisStepResults = BackEnd.calculateOutputs(irradiationParams, elementObjects);
						thisStep.microshieldTable = thisStepResults.microshieldTable;

						thisStep.breakdownArray = thisStepResults.breakdownArray;
						thisStep.energyBreakdownArray = thisStepResults.energyBreakdownArray;

						double thisTotalActivityDecay = (thisStepResults.totalDecayY).doubleValue();
						double thisTotalGamma = (thisStepResults.gamma).doubleValue();
						totalActivityDecay += thisTotalActivityDecay;
						totalGamma += thisTotalGamma;

						thisStep.txtTotalActivityDecay.setText(String.valueOf(thisTotalActivityDecay));
						thisStep.txtGamma.setText(String.valueOf(thisTotalGamma));

						BackEnd.addResults(elementInputs, irradiationParams, elementObjects, tabbedPane, k, microshieldTable, thisStep.tableModel, thisStep.resultsTable);

						String[] columnNames = {
								"Isotope",
								"Energy Level",
								"<html>Neutron Yield (10<sup>6</sup> n/s)</html>"
						};
						String[] energyColumnNames = {
								"Energy Level",
								"<html>Neutron Yield (10<sup>6</sup> n/s)</html>"
						};

						// Update JTables in tabbed pane of each step with the appropriate calculated arrays
						thisStep.additionalPanel.updateTable(thisStepResults.breakdownArray, columnNames);
						thisStep.energyBreakdownPanel.updateTable(BackEnd.removeDuplicates(thisStepResults.energyBreakdownArray), energyColumnNames);

					}
					// Remove all existing listeners
					String activityDecayString = String.valueOf(totalActivityDecay);
					String gammastring = String.valueOf(totalGamma);
					overallResultsBtn.removeMouseListener(errorMouseListener);
					
					MouseAdapter overallResultsClicked = new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							ResultsPanel.populateResultsPane(BackEnd.arrayListToArrayObj(BackEnd.removeDuplicatesString(totalOverallResults)),activityDecayString);	
						}
					};
					
					// Add new listener
					overallResultsBtn.addMouseListener(overallResultsClicked);

					txtOverallActivityDecay.setText(activityDecayString);
					txtOverallGamma.setText(gammastring);
				}			
			}
		});

		JButton btnExportResults = new JButton("Export Results to CSV");

		btnExportResults.setBounds(370, 825, 140, 25);
		overallPanel.add(btnExportResults);

		// Import from CSV
		JButton btnImportResults = new JButton("Import from CSV");
		btnImportResults.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		
				JFileChooser fileChooser = new JFileChooser();
				int result = fileChooser.showOpenDialog(g);
				if(result == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					
					if(file.exists() && !file.isDirectory()) {
						BackEnd.importFromCSV(file.getAbsolutePath());
					}
					else {
						JOptionPane.showMessageDialog(null,  "Invalid file", "Invalid file", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		});
		btnImportResults.setBounds(800, 10, 160, 25);
		panelTargetMaterials.add(btnImportResults);

		// When export results button is clicked
		btnExportResults.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				// Only export if calculation has been done and inputs are correct
				if(calculateClicked) {
					boolean guiOK = BackEnd.checkInputs().isEmpty();
					
					boolean stepsOK = true;
					for(int i = 1; i < tabbedPane.getTabCount() - 1; i++) {
						if(!BackEnd.checkStepInputs(i).isEmpty())
							stepsOK = false;					
					}
					
					if(guiOK && stepsOK) {

						ArrayList<ArrayList<ArrayList<Object>>> outputArray = new ArrayList<>();

						// 1. Overall Inputs ArrayList<ArrayList<Object>> -> 2D Array
						ArrayList<ArrayList<Object>> overallInputs = new ArrayList<>();
						overallInputs.add(BackEnd.createArrayListOfObject(new Object[]{"TARGET MATERIALS (inputs)"}));
						overallInputs.add(BackEnd.createArrayListOfObject(new Object[]{"Element", "Fractional Weight"}));

						for(int i = 0; i < elementObjects.size(); i++) {
							ArrayList<Object> thisRow = new ArrayList<>();
							thisRow.add(elementObjects.get(i).label);
							thisRow.add(elementObjects.get(i).fractionalWeight);
							overallInputs.add(thisRow);
						}

						// Below code all intended to write outputs to csv

						overallInputs.add(BackEnd.createArrayListOfObject(new Object[]{"Final Date & Time"}));
						overallInputs.add(BackEnd.createArrayListOfObject(new Object[]{finalDateTime}));

						outputArray.add(overallInputs);

						// 2. Overall Outputs
						ArrayList<ArrayList<Object>> overallOutputs = new ArrayList<>();
						overallOutputs.add(BackEnd.createArrayListOfObject(new Object[]{"OVERALL OUTPUTS (isotope & activity decay contribution over all steps)"}));
						overallOutputs.add(BackEnd.createArrayListOfObject(new Object[]{"Isotope", "Total Activity Decay"}));
						ArrayList<Object[]> overallResultsRows = new ArrayList<>();
						for(Object[] row :  totalOverallResults) {
							overallResultsRows.add(row);
						}
						ArrayList<Object[]> intermed = BackEnd.removeDuplicatesString(overallResultsRows);

						overallOutputs.addAll(BackEnd.toArrayList(intermed));
						overallOutputs.add(BackEnd.createArrayListOfObject(new Object[]{""}));
						overallOutputs.add(BackEnd.createArrayListOfObject(new Object[]{"Total Activity Decay", txtOverallActivityDecay.getText()}));

						outputArray.add(overallOutputs);

						// 3. Step Breakdown Label
						ArrayList<ArrayList<Object>> stepBreakdown = new ArrayList<>();
						stepBreakdown.add(BackEnd.createArrayListOfObject(new Object[]{"STEP BREAKDOWNS"}));


						// STEPS - loop through step tabs
						for(int i = 0; i < tabbedPane.getTabCount() - 1; i++) {
							// 4. Step X
							StepPanel thisStep = (StepPanel) tabbedPane.getComponentAt(i);
							stepBreakdown.add(BackEnd.createArrayListOfObject(new Object[]{""}));
							stepBreakdown.add(BackEnd.createArrayListOfObject(new Object[]{"STEP " + (i+1)}));

							// 5. Step Inputs
							stepBreakdown.add(BackEnd.createArrayListOfObject(new Object[]{"INPUTS"}));	
							stepBreakdown.add(BackEnd.createArrayListOfObject(new Object[]{"Beam Energy (MeV)","Beam Current (µA)", "Start Date & Time", "End Date & Time", "Irradiation Time (s)", "Decay Time (s)"}));
							stepBreakdown.add(BackEnd.createArrayListOfObject(new Object[]{thisStep.energy,thisStep.current, thisStep.irradiationTime, thisStep.decayTime}));	

							// 6. Step Outputs
							stepBreakdown.add(BackEnd.createArrayListOfObject(new Object[]{""}));
							stepBreakdown.add(BackEnd.createArrayListOfObject(new Object[]{"OUTPUTS"}));
							// i) Results Summary
							stepBreakdown.add(BackEnd.createArrayListOfObject(new Object[]{"Results Summary"}));
							stepBreakdown.add(BackEnd.createArrayListOfObject(new Object[]{"Total Neutron Yield (10^6 n/s)","Neutron dose at 2.5 m (n/cm^2/s)", "Neutron field at 2.5m (µSv/h)", "Total Real EOB Activity (10^7 bq)", "Total Decay Activity (10^7 bq)"}));
							stepBreakdown.add(BackEnd.createArrayListOfObject(new Object[]{thisStep.txtNeutronYield.getText(), thisStep.txtNeutronDose.getText(), thisStep.txtNeutronField.getText(), thisStep.txtRealActivity.getText(), thisStep.txtDecayActivity.getText()}));

							// ii) Isotope Contributions
							stepBreakdown.add(BackEnd.createArrayListOfObject(new Object[]{""}));
							stepBreakdown.add(BackEnd.createArrayListOfObject(new Object[]{"Isotope Contributions"}));
							stepBreakdown.add(BackEnd.createArrayListOfObject(new Object[]{"Isotope", "Energy level", "Neutron Yield"}));
							for(Object[] row : thisStep.breakdownArray) {
								stepBreakdown.add(BackEnd.createArrayListOfObject(row));
							}
							
							// iii) Energy Level Contributions
							stepBreakdown.add(BackEnd.createArrayListOfObject(new Object[]{""}));
							stepBreakdown.add(BackEnd.createArrayListOfObject(new Object[]{"Energy Level Contributions"}));
							stepBreakdown.add(BackEnd.createArrayListOfObject(new Object[]{"Energy level", "Neutron Yield"}));
							thisStep.energyBreakdownArray = BackEnd.removeDuplicates(thisStep.energyBreakdownArray);
							for(Object[] row : thisStep.energyBreakdownArray) {
								stepBreakdown.add(BackEnd.createArrayListOfObject(row));
							}

							// iv) Transmuted Isotopes
							stepBreakdown.add(BackEnd.createArrayListOfObject(new Object[]{""}));
							stepBreakdown.add(BackEnd.createArrayListOfObject(new Object[]{"Transmuted Isotopes"}));
							stepBreakdown.add(BackEnd.createArrayListOfObject(new Object[]{"Target Isotopes", "Transmuted Isotopes", "Activity (ci)", "Activity Decay (ci)"}));
							for(Object[] row : thisStep.outputTable) {
								stepBreakdown.add(BackEnd.createArrayListOfObject(row));
							}
						}

						outputArray.add(stepBreakdown);


						try {
							BackEnd.writeToCSV(outputArray);
						} catch (IOException e2) {
							e2.printStackTrace();
						}
					}
					else {
						JOptionPane.showMessageDialog(contentPane, "Please make a calculation before attempting to export results");
					}
				}
				else {
					// Bring up a popup to tell user that no results have been calculated, therefore nothing will be exported to csv
					JOptionPane.showMessageDialog(contentPane, "Please make a calculation before attempting to export results");
				}
			}
		});
	}

}



