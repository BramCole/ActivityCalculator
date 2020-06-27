import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

@SuppressWarnings("serial")
public class StepPanel extends JPanel {

	int tabNumber;

	public JTextField txtEnergy;
	public JTextField txtCurrent;
	public JTextField txtIrradiationTime;
	public JTextField txtDecayTime;

	/*/////  */
    public JTextField Dose;
	

	static Color lightGrey = new Color(230,230,230);

	public JTextField txtNeutronYield;
	public JTextField txtNeutronDose;
	public JTextField txtRealActivity;
	public JTextField txtNeutronField;
	public JTextField txtDecayActivity;
	public JTextField txtTotalActivityDecay;
	public JTextField txtGamma;
	JTabbedPane tabbedPane;

	Border greyBorder = BorderFactory.createDashedBorder(Color.BLACK);
	DefaultTableModel tableModel;

	Object[][] microshieldTable;
	JTable resultsTable;

	double energy;
	double current;
	double irradiationTime;
	double decayTime;

	AdditionalResults additionalPanel;
	AdditionalResults energyBreakdownPanel;
	
	public ArrayList<Object[]> breakdownArray;
	public ArrayList<Object[]> energyBreakdownArray;
	public Object[][] outputTable;

	int NUM_SECS_PER_MIN = 60;
	int NUM_SECS_PER_HOUR = NUM_SECS_PER_MIN * 60;
	int NUM_SECS_PER_DAY = NUM_SECS_PER_HOUR * 24;
	
	JButton btnClearValues1;
	
	StepPanel thisPanel = this;

	/**
	 * Create the panel.
	 */
	public StepPanel(int num) {

		tabNumber = num;

		// Set the input fields as instances of HighlightListener class which will highlight incorrectly filled fields

		Color grey = new Color(170,170,170);
		Border defaultBorder = BorderFactory.createLineBorder(grey);
		Border emptyBorder = BorderFactory.createEmptyBorder(0,5,0,0);
		Border compoundBorder = BorderFactory.createCompoundBorder(defaultBorder, emptyBorder);

		// Irradiation Panel
		setBackground(getColour(num));
		setBounds(0, 0, 980, 1000);
		setLayout(null);

		JLabel lblIrradiationParameters = new JLabel("Irradiation Parameters");
		lblIrradiationParameters.setFont(new Font("Segoe UI", Font.BOLD, 17));
		lblIrradiationParameters.setForeground(new Color(0, 102, 153));
		lblIrradiationParameters.setBounds(10, 11, 205, 21);
		this.add(lblIrradiationParameters);

		JPanel panelIrradiationInputs = new JPanel();
		panelIrradiationInputs.setBounds(46, 50, 888, 94);
		this.add(panelIrradiationInputs);
		panelIrradiationInputs.setLayout(null);
		panelIrradiationInputs.setBackground(Color.WHITE);
		panelIrradiationInputs.setBorder(greyBorder);

		//Beam energy input text box with label and units
		JLabel lblBeamEnergy = new JLabel("Beam Energy");
		lblBeamEnergy.setBounds(60, 11, 99, 26);
		panelIrradiationInputs.add(lblBeamEnergy);
		lblBeamEnergy.setFont(new Font("Tahoma", Font.PLAIN, 13));

		txtEnergy = new JTextField();
		txtEnergy.setBounds(60, 45, 84, 26);
		panelIrradiationInputs.add(txtEnergy);
		txtEnergy.setColumns(10);
		txtEnergy.setBorder(compoundBorder);


		JLabel lblMev = new JLabel("MeV");
		lblMev.setVerticalAlignment(SwingConstants.TOP);
		lblMev.setBounds(150, 51, 46, 20);
		panelIrradiationInputs.add(lblMev);

		//Beam Current input text box with label and units 
		JLabel lblBeamCurrent = new JLabel("Beam Current");
		lblBeamCurrent.setBounds(220, 11, 99, 26);
		panelIrradiationInputs.add(lblBeamCurrent);
		lblBeamCurrent.setFont(new Font("Tahoma", Font.PLAIN, 13));

		txtCurrent = new JTextField();
		txtCurrent.setBounds(220, 45, 84, 26);
		panelIrradiationInputs.add(txtCurrent);
		txtCurrent.setColumns(10);
		txtCurrent.setBorder(compoundBorder);

		JLabel lbla = new JLabel("\u03BCA");
		lbla.setVerticalAlignment(SwingConstants.TOP);
		lbla.setBounds(310, 51, 46, 32);
		panelIrradiationInputs.add(lbla);

		//Irradiation time text box with label and units
			JLabel lblIrradiationTime = new JLabel("Irradiation Time");
			lblIrradiationTime.setHorizontalAlignment(SwingConstants.CENTER);
			lblIrradiationTime.setFont(new Font("Tahoma", Font.PLAIN, 13));
			lblIrradiationTime.setBounds(370, 11, 127, 26);
			panelIrradiationInputs.add(lblIrradiationTime);

			JLabel lblS = new JLabel("Seconds");
			lblS.setVerticalAlignment(SwingConstants.TOP);
			lblS.setBackground(Color.WHITE);
			lblS.setBounds(480, 51, 50, 29);
			panelIrradiationInputs.add(lblS);

			txtIrradiationTime = new JTextField();
			txtIrradiationTime.setBackground(Color.WHITE);
			txtIrradiationTime.setEditable(true);
			txtIrradiationTime.setBounds(390, 45, 84, 26);
			panelIrradiationInputs.add(txtIrradiationTime);
			txtIrradiationTime.setColumns(10);
			
	   //Decay Time text box with label and units 
			JLabel lblDecayTime = new JLabel("Decay Time"); 
			lblDecayTime.setHorizontalAlignment(SwingConstants.CENTER);
			lblDecayTime.setFont(new Font("Tahoma", Font.PLAIN, 13));
			lblDecayTime.setBounds(550, 11, 127, 26);
			panelIrradiationInputs.add(lblDecayTime); 
			
			JLabel lblD = new JLabel("Seconds"); 
			lblD.setVerticalAlignment(SwingConstants.TOP);
			lblD.setBackground(Color.WHITE);
			lblD.setBounds(660,51,50,29);
			panelIrradiationInputs.add(lblD); 
			
			txtDecayTime = new JTextField(); 
			txtDecayTime.setBackground(Color.WHITE);
			txtDecayTime.setEditable(true);
			txtDecayTime.setBounds(570,45, 84, 26);
			panelIrradiationInputs.add(txtDecayTime); 
			txtDecayTime.setColumns(10);
			
			
			//dose labels and txt field
			JLabel lblDose = new JLabel("Dose"); 
			lblDose.setHorizontalAlignment(SwingConstants.CENTER);
			lblDose.setFont(new Font("Tahoma", Font.PLAIN, 13));
			lblDose.setBounds(700, 11, 127, 26);
			panelIrradiationInputs.add(lblDose); 
			
			JLabel lble = new JLabel("Coulombs"); 
			lble.setVerticalAlignment(SwingConstants.TOP);
			lble.setBackground(Color.WHITE);
			lble.setBounds(820,51,50,29);
			panelIrradiationInputs.add(lble); 
			
			Dose = new JTextField();
			Dose.setHorizontalAlignment(SwingConstants.CENTER);
			Dose.setFont(new Font("Tahoma", Font.PLAIN, 13));
			Dose.setEditable(false);
			Dose.setBounds(725,45, 84, 26);
			panelIrradiationInputs.add(Dose);
			
			//Calculations for dose done live as things are being typed in
			//Dose(coulombs) = BeamCurrent(A) * Time(seconds)
			txtCurrent.addKeyListener(new KeyAdapter() {
			      public void keyReleased(KeyEvent e) {
			        if(!txtIrradiationTime.getText().equals("") && !txtCurrent.getText().equals("")) {
			        	try{
			        		Double time = 0.0;
			        		Double Current = 0.0;
			        		try {
			        			Current = Double.parseDouble(txtCurrent.getText());
			        		}
			        		catch(Exception E){
			        			JOptionPane.showMessageDialog(null,  "Please Make sure Current if a number (ex. 12.0)", "Error", JOptionPane.WARNING_MESSAGE);
			        			txtCurrent.setText("");
			        			return;
							}
			        		try { //math for change from scienific notation
			        			time = irradiationTime = Integer.parseInt(txtIrradiationTime.getText());
			        		}catch(Exception e1) {
			        			String txt = txtIrradiationTime.getText();
								int index = txt.indexOf('E');
								if(index ==  txt.length()) {
									return;
								}
								if(index == -1) {
									JOptionPane.showMessageDialog(null,  "Please Make sure times are either in standard notation (ex. 750000) or in scientific notation (ex. 7.5E5)", "Error", JOptionPane.WARNING_MESSAGE);
									txtIrradiationTime.setText("");
									return;
								}
								time = Double.parseDouble(txt.substring(0,index));
								int power = Integer.parseInt(txt.substring(index+1));
								time = (time*Math.pow(10, power));
			        		}
			        		BigDecimal a = BackEnd.roundToNumSigFigs(3, new BigDecimal((Current*Math.pow(10, -6) * time)));
			        		Dose.setText(String.valueOf(a));
			        	}catch (Exception e2) {}
			      }
			      }
			    });
			
			txtIrradiationTime.addKeyListener(new KeyAdapter() {
			      public void keyReleased(KeyEvent e) {
			    	  if(!txtCurrent.getText().equals("") && !txtIrradiationTime.getText().equals("")) {
			        	try{
			        		Double time = 0.0;
			        		Double Current = 0.0;
			        		try {
			        			Current = Double.parseDouble(txtCurrent.getText());
			        		}
			        		catch(Exception E){
			        			JOptionPane.showMessageDialog(null,  "Please Make sure Current if a number (ex. 12.0)", "Error", JOptionPane.WARNING_MESSAGE);
			        			txtCurrent.setText("");
			        			return;
							}
			        		try { //math for change from scientific notation
			        			time = irradiationTime = Integer.parseInt(txtIrradiationTime.getText());
			        		}catch(Exception e1) {
			        			String txt = txtIrradiationTime.getText();
								int index = txt.indexOf('E');
								if(index ==  txt.length()) {
									return;
								}
								if(index == -1) {
									JOptionPane.showMessageDialog(null,  "Please Make sure times are either in standard notation (ex. 750000) or in scientific notation (ex. 7.5E5)", "Error", JOptionPane.WARNING_MESSAGE);
									txtIrradiationTime.setText("");
									return;
								}
								time = Double.parseDouble(txt.substring(0,index));
								int power = Integer.parseInt(txt.substring(index+1));
								time = (time*Math.pow(10, power));
			        		}
			        		BigDecimal a = BackEnd.roundToNumSigFigs(3, new BigDecimal((Current*Math.pow(10, -6) * time)));
			        		Dose.setText(String.valueOf(a));
			        	}catch (Exception e2) {}
			      }
				      }
				    });
			
			
	   //Sets the highlight listeners for the various inputs
		new HighlightListener(txtEnergy);
		new HighlightListener(txtCurrent);
		new HighlightListener(txtIrradiationTime); 
		new HighlightListener(txtDecayTime); 

		btnClearValues1 = new JButton("Clear Values");
		btnClearValues1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtEnergy.setText("");
				txtCurrent.setText("");
				txtIrradiationTime.setText("");
				txtDecayTime.setText(""); 
				
				
				// Unhighlight fields, if they had been highlighted
				(new HighlightListener(txtEnergy)).defaultBorder();
				(new HighlightListener(txtCurrent)).defaultBorder();
				(new HighlightListener(txtIrradiationTime)).defaultBorder(); 
				(new HighlightListener(txtDecayTime)).defaultBorder(); 
			}
		});


		JLabel lblNewLabel = new JLabel("New label");
		lblNewLabel.setBounds(214, 17, 46, 14);
		add(lblNewLabel);
		lblNewLabel.setText("Step " + tabNumber);
		lblNewLabel.setOpaque(false);

		// RESULTS PANEL

		// Create JTable which will be populated with the results of the calculations
		// done on the user's inputs
		resultsTable = new JTable() {

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

		JPanel resultsPanel = new JPanel();
		resultsPanel.setBounds(0, 160, 1000, 450);
		resultsPanel.setLayout(null);
		resultsPanel.setOpaque(false);

		JLabel lblResults = new JLabel("Results");
		lblResults.setBounds(400, 0, 110, 50);
		resultsPanel.add(lblResults);
		lblResults.setForeground(new Color(0, 102, 153));
		lblResults.setHorizontalAlignment(SwingConstants.CENTER);
		lblResults.setFont(new Font("Segoe UI", Font.BOLD, 25));
		
		//3 labels for activity decay per step

		JLabel lblTotalActivityDecay = new JLabel("Sum of Activity Decay after bombardment");
		lblTotalActivityDecay.setVerticalAlignment(SwingConstants.TOP);
		lblTotalActivityDecay.setHorizontalAlignment(SwingConstants.TRAILING);
		lblTotalActivityDecay.setBounds(523, 345, 210, 31);
		resultsPanel.add(lblTotalActivityDecay);
		
		JLabel lblCi = new JLabel("<html> 10<sup>6</sup> bq </html>");
		lblCi.setVerticalAlignment(SwingConstants.TOP);
		lblCi.setHorizontalAlignment(SwingConstants.TRAILING);
		lblCi.setBounds(895, 340, 50, 31);
		resultsPanel.add(lblCi);

		txtTotalActivityDecay = new JTextField();
		txtTotalActivityDecay.setBackground(lightGrey);
		txtTotalActivityDecay.setEditable(false);
		txtTotalActivityDecay.setBounds(736, 340, 163, 25);
		txtTotalActivityDecay.setColumns(10);
		resultsPanel.add(txtTotalActivityDecay);
		
		//3 steps for gamma dose per step
		
		JLabel lblTotalGamma = new JLabel("Sum of Gamma during irradiation ");
		lblTotalGamma.setVerticalAlignment(SwingConstants.TOP);
		lblTotalGamma.setHorizontalAlignment(SwingConstants.TRAILING);
		lblTotalGamma.setBounds(563, 375, 173, 31);
		resultsPanel.add(lblTotalGamma);
		
		JLabel lblG = new JLabel("<html> \u00B5Sv/h at 30 cm</html>");
		lblG.setVerticalAlignment(SwingConstants.TOP);
		lblG.setHorizontalAlignment(SwingConstants.TRAILING);
		lblG.setBounds(905, 370, 50, 30);
		resultsPanel.add(lblG);

		txtGamma = new JTextField();
		txtGamma.setBackground(lightGrey);
		txtGamma.setEditable(false);
		txtGamma.setBounds(736, 370, 163, 25);
		txtGamma.setColumns(10);
		resultsPanel.add(txtGamma);

		JButton btnExportToMicroshield = new JButton("Export to MicroShield");
		btnExportToMicroshield.setBounds(391, 370, 155, 23);
		resultsPanel.add(btnExportToMicroshield);
		btnExportToMicroshield.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(GUI.calculateClicked) {
				try {
					BackEnd.exportToFile(microshieldTable);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				}
				else {
					JOptionPane.showMessageDialog(thisPanel, "Please make a calculation before attempting to export results");
				}
			}
		});

		tableModel = new DefaultTableModel() {};

		// Add resultsTable to a scroll pane to enable scrolling if results overflow table space
		JScrollPane scrollPane = new JScrollPane(resultsTable);
		resultsTable.setFillsViewportHeight(true);
		scrollPane.setBounds(462, 70, 485, 264);
		scrollPane.setBorder(greyBorder);

		resultsPanel.add(scrollPane);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 40, 442, 313);
		resultsPanel.add(tabbedPane);

		// Create results panel
		// Add output components to results panel
		JPanel panel = new JPanel();
		String[] columnNames = {
				"Isotope",
				"Energy Level",
				"<html> Neutron Yield (10<sup>6</sup> n/s)</html>"
		};
		String[] energyColumnNames = {
				"Energy Level",
				"<html> Neutron Yield (10<sup>6</sup> n/s)</html>"
		};

		energyBreakdownPanel = new AdditionalResults("Energy level contributions", energyColumnNames);
		additionalPanel = new AdditionalResults("Isotope Contributions", columnNames);
	
		tabbedPane.addTab("Results Summary", null, panel, null);
		tabbedPane.addTab("Isotope Contributions", additionalPanel);
		tabbedPane.addTab("Energy level contributions", energyBreakdownPanel);
		
		panel.setBackground(Color.WHITE);
		panel.setLayout(null);

		//neutron yield
		txtNeutronYield = new JTextField();
		txtNeutronYield.setBackground(lightGrey);
		txtNeutronDose = new JTextField();
		txtNeutronField = new JTextField();
		txtRealActivity = new JTextField();
		txtDecayActivity = new JTextField();

		txtNeutronYield.setEditable(false);
		txtNeutronYield.setBounds(248, 39, 163, 20);
		panel.add(txtNeutronYield);
		txtNeutronYield.setColumns(10);

		JLabel lblTotalNeutronYield = new JLabel("<html>Total Neutron Yield (10<sup>6</sup> n/s)</html>");
		lblTotalNeutronYield.setVerticalAlignment(SwingConstants.TOP);
		lblTotalNeutronYield.setHorizontalAlignment(SwingConstants.TRAILING);
		lblTotalNeutronYield.setBounds(0, 39, 238, 34);

		panel.add(lblTotalNeutronYield);

		//neutron dose
		JLabel lblneutronDoseAt = new JLabel("<html>Neutron dose at 2.5 m (n/cm<sup>2</sup>/s)</html>");
		lblneutronDoseAt.setVerticalAlignment(SwingConstants.TOP);
		lblneutronDoseAt.setHorizontalAlignment(SwingConstants.TRAILING);
		lblneutronDoseAt.setBounds(0, 84, 238, 43);
		panel.add(lblneutronDoseAt);

		txtNeutronDose.setEditable(false);
		txtNeutronDose.setBounds(248, 86, 163, 20);
		txtNeutronDose.setBackground(lightGrey);
		panel.add(txtNeutronDose);
		txtNeutronDose.setColumns(10);

		//EOB activity
		JLabel lbltotalRealEob = new JLabel("<html>Total EOB Activity (10<sup>6</sup> bq)</html>");
		lbltotalRealEob.setVerticalAlignment(SwingConstants.TOP);
		lbltotalRealEob.setHorizontalAlignment(SwingConstants.TRAILING);
		lbltotalRealEob.setBounds(0, 190, 238, 40);
		panel.add(lbltotalRealEob);

		txtRealActivity.setEditable(false);
		txtRealActivity.setBounds(248, 192, 163, 20);
		txtRealActivity.setBackground(lightGrey);
		panel.add(txtRealActivity);
		txtRealActivity.setColumns(10);

		//neutron field
		JLabel lblneutronFieldAt = new JLabel("<html>Neutron field at 2.5m (\u00B5Sv/h)</html>");
		lblneutronFieldAt.setVerticalAlignment(SwingConstants.TOP);
		lblneutronFieldAt.setHorizontalAlignment(SwingConstants.TRAILING);
		lblneutronFieldAt.setBounds(0, 138, 238, 41);
		panel.add(lblneutronFieldAt);

		txtNeutronField.setEditable(false);
		txtNeutronField.setBounds(248, 140, 163, 20);
		txtNeutronField.setBackground(lightGrey);
		panel.add(txtNeutronField);
		txtNeutronField.setColumns(10);

		//total decay activity
		JLabel lbltotalDecayActivity = new JLabel("<html>Total Decay Activity (10<sup>6</sup> bq)</html>");
		lbltotalDecayActivity.setVerticalAlignment(SwingConstants.TOP);
		lbltotalDecayActivity.setHorizontalAlignment(SwingConstants.TRAILING);
		lbltotalDecayActivity.setBounds(0, 241, 238, 43);
		panel.add(lbltotalDecayActivity);

		txtDecayActivity.setEditable(false);
		txtDecayActivity.setBounds(248, 245, 163, 20);
		txtDecayActivity.setBackground(lightGrey);
		panel.add(txtDecayActivity);
		txtDecayActivity.setColumns(10);

		//summary
		JLabel lblSummaryOfResults = new JLabel("Summary of Results");
		lblSummaryOfResults.setHorizontalAlignment(SwingConstants.CENTER);
		lblSummaryOfResults.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblSummaryOfResults.setBounds(79, 0, 287, 44);
		panel.add(lblSummaryOfResults);
		this.add(resultsPanel);
	}

	private Color getColour(int num) {

		
		Random rand = new Random();
		int randomColour = rand.nextInt(55) + 200;

		if(num % 2 == 0)
			randomColour = 210;

		else
			randomColour = 190;
		
		int r, g, b;
		r = g = b = randomColour;
		Color thisColour = new Color(r, g, b);

		return thisColour;
	}

	public void printAttributes() {	

		System.out.println("Printing for Step " + tabNumber);
		System.out.println("energy = " + energy + "\ncurrent = " + current + "\nirradiationTime = " + irradiationTime + "\ndecayTime = " + decayTime);
	}
}
