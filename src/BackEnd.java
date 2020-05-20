import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


/**
 * This class is responsible for processing the input given to it by the GUI class.
 * It will calculate the desired outputs and send them back to the GUI for output on the screen.
 */
public class BackEnd {

	static int NUM_SECS_PER_MIN = 60;
	static int NUM_SECS_PER_HOUR = NUM_SECS_PER_MIN * 60;
	static int NUM_SECS_PER_DAY = NUM_SECS_PER_HOUR * 24;
	static int NUM_INPUTS = 10;

	// Connection to MS Access Database
	static Connection conn = makeDatabaseConnection();

	public static int getSizeOfOutputTable(ArrayList<Element> elementObjects) {
		int size = 0;
		for(Element elem : elementObjects) {
			size += elem.isotopes.size();
		}
		return size;
	}

	public static Connection makeDatabaseConnection() {
		try {
			//Should be changed if the programme is being re exported
			conn = DriverManager.getConnection("jdbc:ucanaccess://./SpreadsheetDB-Copy.accdb;");	// Connect to MS Access Database
			
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "An Error occured while connecting to Database, please make sure the database is located in the same directory as this executable file", "Error", JOptionPane.WARNING_MESSAGE);
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);

			System.exit(-1);
		}

		return conn;
	}

	// Get element names and atomic numbers and use this data to populate dropdown menus in GUI
	// Once an element has been chosen, do not populate the other dropdowns with it
	public static ArrayList<String> getElements() {

		// Accessing database
		ArrayList<String> elementStrings = new ArrayList<>();

		try {
			Statement s = conn.createStatement();
			ResultSet elementNames = s.executeQuery("SELECT * FROM elements");	// Bring in all element names from elements table in database

			while(elementNames.next()) {
				int atomicNum = Integer.parseInt(elementNames.getString(1));
				String elemName = elementNames.getString(2);
				elementStrings.add(atomicNum + elemName); 
			}

		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,  "An error occured retreiving data from the database", "Error", JOptionPane.WARNING_MESSAGE);
		}
		return elementStrings;
	}

	// To fill elementObjects ArrayList with Element Objects created from the user-inputted element strings
	public static ArrayList<Element> populateElementObjects(String[] elementInputs, BigDecimal[] weightInputs, double[] irradiationParams) {

		ArrayList<Element> elementObjects = new ArrayList<>();

		for(int i = 0; i < elementInputs.length; i++) {
			String elemString = elementInputs[i];

			// Must check if elemString is null, because even empty comboBoxes are stored in this array
			if(elemString == null){
				continue;
			}
			Pair numAndName = BackEnd.getAtomicNumberAndName(elemString);
			int atomicNum = numAndName.atomicNum;
			String name = numAndName.name;
			double thisWeight = weightInputs[i].doubleValue();

			Element thisElem = new Element(atomicNum, name, irradiationParams, thisWeight, conn);
			elementObjects.add(thisElem);
		}	
		return elementObjects;
	}


	public static BigDecimal checkBigDecimal(String input) {
		if (input != null && input != "") {
			try {
				return new BigDecimal(input);
			} catch (Exception e) {
				return new BigDecimal(-1);
			}
		} else
			return new BigDecimal(-1);
	}

	public ArrayList<String> getUsedElements(String[] elementInputs) {

		ArrayList<String> used = new ArrayList<>();
		for(String elem : elementInputs) {
			if(elem != null) {
				used.add(elem);
			}
		}
		return used;
	}

	public static String getTotalWeight(Map<String, JTextField> textFieldMap){

		Iterator<Map.Entry<String,JTextField>> it = textFieldMap.entrySet().iterator();
		BigDecimal totalWeight = new BigDecimal(0.0);
		//double totalWeightDub = 0;
		while(it.hasNext()) {
			Map.Entry<String, JTextField> pair = (Map.Entry<String, JTextField>)it.next();
			JTextField thisTextField = pair.getValue();
			try {
				BigDecimal thisDec = new BigDecimal(thisTextField.getText());
				totalWeight = totalWeight.add(thisDec);
			}
			catch(NumberFormatException e) {
				continue;
			}
		}
		return String.valueOf(totalWeight);
	}

	public static void populateDropdowns(Map<String, JComboBox<String>> comboBoxMap) {
		// Get list of element strings from database
		ArrayList<String> elementStrings = BackEnd.getElements();

		Iterator<Map.Entry<String,JComboBox<String>>> it = comboBoxMap.entrySet().iterator();

		while(it.hasNext()) {
			Map.Entry<String, JComboBox<String>> pair = (Map.Entry<String, JComboBox<String>>)it.next();
			JComboBox<String> thisCombo = pair.getValue();

			thisCombo.addItem(null);
			for(String elem : elementStrings) {
				thisCombo.addItem(elem);
			}
		}
	}

	public static void repopulateDropdowns(Map<String, JComboBox<String>> comboBoxMap) {

		// Get list of element strings from database
		ArrayList<String> elementStrings = BackEnd.getElements();

		ArrayList<String> used = new ArrayList<>();
		ArrayList<JComboBox<String>> toRepopulate = new ArrayList<>();
		Iterator<Map.Entry<String,JComboBox<String>>> it = null;
		// Loop through all combos
		if(comboBoxMap == null) {
		} else {
			it = comboBoxMap.entrySet().iterator();
		}



		while(it.hasNext()) {
			Map.Entry<String, JComboBox<String>> pair = (Map.Entry<String, JComboBox<String>>)it.next();
			JComboBox<String> thisCombo = pair.getValue();

			if(thisCombo.getSelectedItem() != null) {	
				used.add(String.valueOf(thisCombo.getSelectedItem()));
				toRepopulate.add(thisCombo);
			}
			else {

				// Clear all existing values from dropdown
				DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
				model.removeAllElements();
				thisCombo.setModel(model);
				toRepopulate.add(thisCombo);
			}
		}

		for(JComboBox<String> thisCombo : toRepopulate) {
			thisCombo.addItem(null);
			for(String elem : elementStrings) {
				if(! used.contains(elem)) {
					thisCombo.addItem(elem);
				}
			}
		}
	}

	public static Pair getAtomicNumberAndName(String label) {
		int dividingIndex = 0;
		int atomicNum;
		String name;

		for(int i = 0; i < label.length(); i++) {
			char thisChar = label.charAt(i);
			try {
				Integer.parseInt(Character.toString((thisChar)));
			}
			catch(NumberFormatException e) {
				dividingIndex = i;
				break;
			}
		}

		atomicNum = Integer.parseInt(label.substring(0, dividingIndex));
		name = label.substring(dividingIndex);

		Pair numAndName = new Pair(atomicNum, name);

		return numAndName;
	}

	// Parses an isotope name, e.g. "fe56" into its element and mass subcomponents of "fe" and 56
	// and returns these two values as part of a Pair object
	public static String[] getMassAndName(String input) {

		int dividingIndex = 0;
		String mass;
		String name;

		for(int i = 0; i < input.length(); i++) {
			char thisChar = input.charAt(i);

			try {
				Integer.parseInt(Character.toString((thisChar)));
				break;

			}
			catch(NumberFormatException e) {
				dividingIndex = i + 1;
				//break;

			}
		}
		name = input.substring(0, dividingIndex);
		mass = input.substring(dividingIndex);

		String[] retArr = {mass, name};

		return retArr;
	}

	// To convert a variable length ArrayList into a primitive fixed length array
	public String[] arrayListToArray(ArrayList<String> inArr) {
		String[] outArr = new String[inArr.size()];
		for (int i = 0; i < inArr.size(); i++) {
			outArr[i] = inArr.get(i);
		}
		return outArr;
	}

	public static Object[][] arrayListToArrayObj(ArrayList<Object[]> inArr) {
		if(inArr == null || inArr.size() == 0 || inArr.get(0).length == 0) {
			//return new Object[0][0];
		}
		Object[][] retArr = new Object[inArr.size()][inArr.get(0).length];
		int i = 0;
		for(Object[] row: inArr) {
			retArr[i] = row;
			i++;			
		}
		return retArr;
	}

	// To get the desired Element object from elementObjects ArrayList based on its name
	public static Element getElem(String inputLabel, ArrayList<Element> elementObjects) {

		// look through elementObjects ArrayList to get the right element
		for(Element elem : elementObjects) {
			if(elem.label.equals(inputLabel)) {
				return elem;
			}
		}
		return null;
	}

	// The following are debug methods which can print various types of arrays
	public static void printArray(Object[][] inArr) {
		for (Object[] row : inArr) {
			for(Object item : row) {
				System.out.println(item + ", ");
			}
		}
	}

	public static void printArray(ArrayList<Object[]> inArr) {
		for (Object[] row : inArr) {
			for(Object item : row) {
				System.out.println(item + ", ");
			}
		}
	}

	public static void printArray(double[][] arr) {
		for(int i = 0; i < arr.length; i++) {
			for(int j = 0; j < arr[0].length;j++) {
				System.out.print(arr[i][j] + "\t");
			}
			System.out.println();
		}
	}

	public static double parseEnergyRange(String input) {
		String energy = "";
		for(int i = 0; i < input.length(); i++){

			char thisChar = input.charAt(i);
			if(thisChar == ' ') {
				break;
			}
			else {
				energy += thisChar;
			}
		}

		return Double.parseDouble(energy);
	}

	// When given a String as input this method will parse it into a double or will return -1 if the input is empty or null
	public static double checkDouble(String input) {
		if (input != null && input != "") {
			try {
				return Double.parseDouble(input);
			} catch (Exception e) {
				return -1; 
				// user input
			}
		} else
			return -1;
	}

	// Method to calculate outputs based on user inputs and output these results to the GUI
	@SuppressWarnings("serial")
	public static void addResults(String[] elementInputs, double[] irradiationParams, ArrayList<Element> elementObjects, JTabbedPane tabbedPane, 
			int tabNum, Object[][] microshieldTable, DefaultTableModel model, JTable resultsTable) {

		StepPanel thisTab = (StepPanel) tabbedPane.getComponentAt(tabNum);

		Result outputs = GUI.thisStepResults;//BackEnd.calculateOutputs(irradiationParams, elementObjects);
		Object[][] outputTable = outputs.outputTable;
		thisTab.outputTable = outputTable;

		(thisTab.txtNeutronYield).setText(String.valueOf(outputs.totalNeutronYield));
		(thisTab.additionalPanel.txtTotalNeutronYield).setText(String.valueOf(outputs.totalNeutronYield));
		(thisTab.energyBreakdownPanel.txtTotalNeutronYield).setText(String.valueOf(outputs.totalNeutronYield));
		thisTab.txtNeutronDose.setText(String.valueOf(outputs.neutronDose));
		thisTab.txtNeutronField.setText(String.valueOf(outputs.neutronField));
		thisTab.txtRealActivity.setText(String.valueOf(outputs.totalRealY));
		thisTab.txtDecayActivity.setText(String.valueOf(outputs.totalDecayY));

		microshieldTable = new Object[outputTable.length][2];

		for(int j = 0; j < outputTable.length; j++){
			microshieldTable[j] = new Object[]{outputTable[j][1], outputTable[j][2]};
		}

		// Column Headers for results output table
		String[] columnNames = {
				"<html>Target<br>Isotopes</html>",
				"<html>Transmuted<br>Isotopes</html>",
				"<html>Activity  10<sup>6</sup> bq </html>",
				"<html>Activity Decay<br> 10<sup>6</sup> bq </html>",
				"<html>Gamma Dose Rate<br> µSv/h at 30 cm </html>"
		};
		
		if(outputTable.length == 0) {
			Object[][] newArr = null;
			outputTable = newArr;
		}
		
		// Results Table to be outputted to the GUI
		model = new DefaultTableModel(outputTable, columnNames) {};
		resultsTable.setModel(model);

		// Set columns to have centre justification
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		resultsTable.setDefaultRenderer(resultsTable.getColumnClass(1), centerRenderer);

		// Prevent columns from being resized and/or reordered
		resultsTable.getTableHeader().setReorderingAllowed(false);
		resultsTable.getTableHeader().setResizingAllowed(false);
		resultsTable.getTableHeader().setPreferredSize(
			     new Dimension(485,60)
			);


	}

	//Takes as input the irradiation parameters and the elements to be irradiated and uses these inputs to caclulate the required outputs. 
	//Specifically the outputs calculated are the total neutron yield, dose, and field as well as the totalActivityDecay. 
	
	public static Result calculateOutputs(double[] irradiationParams, ArrayList<Element> elementObjects) {

		double energy = irradiationParams[0];

		int sizeOfOutputTable = getSizeOfOutputTable(elementObjects);
		Object[][] outputTable = new Object[sizeOfOutputTable][4];
		Object[][] microshieldTable = new String[sizeOfOutputTable][2];

		double totalNeutronYield = 0;
		double totalRealY = 0;
		double totalDecayY = 0;
		double neutronDose;
		double neutronField;
		double gamma = 0;

		// breakdownArray will contain arrays of Object where the first element is the energy level, the second is the isotope and the third is the neutron yield for that energy level
		ArrayList<Object[]> breakdownArray = new ArrayList<>();
		ArrayList<Object[]> energyBreakdownArray = new ArrayList<>();
		ArrayList<Object[]> overallResults = new ArrayList<>();

		if(energy <= 1 || energy > 8) {
			neutronDose = 0;
			neutronField  = 0;
		}
		else {
			int i = 0;
			for(Element elem : elementObjects) {

				for (Isotope iso : elem.isotopes) {
					String targetIsotope = iso.name;
					String transmutedIsotope = iso.transmuted;

					Object[] outputIsotopeRow = {targetIsotope, transmutedIsotope, BackEnd.roundToNumSigFigs(3, new BigDecimal(iso.outRealTimeSourceActivity)), BackEnd.roundToNumSigFigs(3, new BigDecimal(iso.outSourceDecayActivity)), BackEnd.roundToNumSigFigs(3, new BigDecimal(iso.outgamma))};	//BackEnd.roundToNumSigFigs(3, new BigDecimal(activityDecay) - in ci				
					Object[] overallResultsRow = {targetIsotope, roundToNumSigFigs(3, new BigDecimal(iso.outSourceDecayActivity)),roundToNumSigFigs(3, new BigDecimal(iso.outgamma))};

					overallResults.add(overallResultsRow);

					outputTable[i] = outputIsotopeRow;

					double thisNeutronYield = iso.outNeutronYield;	

					totalNeutronYield += thisNeutronYield;
					totalRealY += iso.outRealTimeSourceActivity;
					totalDecayY += iso.outSourceDecayActivity;	
					gamma += iso.outgamma;

					breakdownArray.addAll(iso.breakdownArray);
					
					energyBreakdownArray.addAll(iso.energyBreakdownArray);

					// Add relevant information (transmuted isotope name and isotope activity) to microshield output table
					microshieldTable[i][0] = transmutedIsotope;
					microshieldTable[i][1] = String.valueOf(iso.outSourceDecayActivity);

					i++;
				}
			}
			neutronDose = (totalNeutronYield / (4 * 250 * 250 * 3.14159))* 1000000;
			
			neutronField = neutronDose * 3600 * 0.3 *(0.001);
		}
		Result outputs = new Result(outputTable, totalNeutronYield, neutronDose, neutronField, totalRealY, totalDecayY, gamma ,microshieldTable, breakdownArray, energyBreakdownArray, overallResults);
		GUI.totalOverallResults.addAll(overallResults);
		return outputs;
	}

	
	
	
	public static double[][] calculateNT(double[][] inputArr, double atomicMass) {

		double[][] results = new double[14][7];

		for(int i = 0; i < inputArr.length; i++) {
			results[i][0] = inputArr[i][0]; //energy
			
			//numbers for proton calculations
			double deltaRange = inputArr[i][1];
			results[i][1] = deltaRange; //deltarange
			
			double nT = (deltaRange / atomicMass) * 6000;
			results[i][2] = nT; //nT
			
			results[i][3] = inputArr[i][2]; //crosssection
			
			//numbers for Alpha calculation
			deltaRange = inputArr[i][3];
			
			results[i][4] = deltaRange;//deltarange
			
			nT = (deltaRange / atomicMass) * 6000;
			results[i][5] = nT; //nT
			
			results[i][6] = inputArr[i][4]; //crosssection

		}
		
		return results;		
	}

	// This method will take an isotopeData ResultSet and a target energy and will find the energy level 
	public static int getEnergyLevelToReplace(ResultSet isotopeData, double targetEnergy) throws SQLException {
		int i = 0;
		double startEnergy = 0;
		while(isotopeData.next()) {
			//System.out.println(isotopeData.getString(3));
			double thisEnergy = Double.parseDouble(isotopeData.getString(1));
			if(startEnergy < targetEnergy && targetEnergy <= thisEnergy) {
				return i;
			}
			else if(!isotopeData.next()) {
				return -1;
			}
			isotopeData.previous();
			startEnergy = thisEnergy;
			i++;
		}

		return 0;
	}

	// This method will return the cross section and delta range, linearly interpolated from the data in the database
	public static InterpolatedValues getInterpolatedValues(double targetEnergy, String isotopeName) {

		try {	
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			String query = "SELECT E, nT, crossSection FROM " + isotopeName ;
			try {
				if(GUI.getSelectedItem().equals("Alpha")) {
				query = "SELECT E, AnT, AcrossSection FROM " + isotopeName;
				}
			}catch (Exception e){
					query = "SELECT E, nT, crossSection FROM " + isotopeName;
			}
			ResultSet isotopeData = s.executeQuery(query);
			//System.out.println(query);
			int index = getEnergyLevelToReplace(isotopeData, targetEnergy);
			if(index == -1) {
				return new InterpolatedValues(0, 0, 0);
			}


			isotopeData.absolute(index);
			
			double energyLow = Double.parseDouble(isotopeData.getString(1));
			double nTLow = Double.parseDouble(isotopeData.getString(2));
			double crossSectionLow;
			
			
			try {
				crossSectionLow = Double.parseDouble(isotopeData.getString(3));
			}
			catch(NullPointerException e) {
				crossSectionLow = 0;
			}
			
			double energyHigh;
			double nTHigh;
			double crossSectionHigh;

			if(isotopeData.next()) {

				energyHigh = Double.parseDouble(isotopeData.getString(1));
				nTHigh = Double.parseDouble(isotopeData.getString(2));
				try {
					crossSectionHigh = Double.parseDouble(isotopeData.getString(3));
				}
				catch(NullPointerException e) {
					crossSectionHigh = 0;
				}
			}
			else {
				return new InterpolatedValues(crossSectionLow, nTLow, 1);
			}

			double energyRange = energyHigh - energyLow;
			double fraction = targetEnergy - energyLow;
			double ratio = fraction / energyRange;

			double crossSectionRange = crossSectionHigh - crossSectionLow;
			double nTRange = nTHigh - nTLow;
			double interpolatedCrossSection = ((ratio * crossSectionRange) + crossSectionLow);
			double interpolatedNT = ((ratio * nTRange) + nTLow);

			return new InterpolatedValues(interpolatedCrossSection, interpolatedNT, ratio);


		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null,  "Error occured durring calculations", "Error", JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
		}

		return null;
	}



	public static long getDecayTime(Date endDateTime, Date finalDateTime) {

		return (finalDateTime.getTime() - endDateTime.getTime()) / 1000;
	}

	// This method finds the number of seconds in between the start and end times of irradiation
	// by converting each Calendar to ms, subtracting one value from the other, and converting to seconds.
	public static long getIrradiationTime(Calendar startTime, Calendar endTime) {

		long start = startTime.getTimeInMillis();
		long end = endTime.getTimeInMillis();

		long timeInSeconds = (end - start) / 1000;

		return timeInSeconds;
	}

	// This method will check to see if the endTime is less than the startTime
	// If this is the case, the endTime is before the start time and we cannot accept those user parameters
	public static boolean endTimeBeforeStartTime(Calendar startTime, Calendar endTime) {

		if(endTime.getTimeInMillis() < startTime.getTimeInMillis())
			return true;
		return false;
	}


	// This method grabs the input fields from the GUI class in a StepPanel and checks to see whether all inputs are present
	public static String checkStepInputs(int tabIndex) {

		StepPanel thisStep = (StepPanel) GUI.tabbedPane.getComponentAt(tabIndex);
		int tabNumber = thisStep.tabNumber;
		JTextField txtEnergy = thisStep.txtEnergy;
		JTextField txtCurrent = thisStep.txtCurrent;
		//ArrayList<HighlightListener> toHighlight = GUI.toHighlight;

		String errorMessage = "";

		String stepX = "STEP " + tabNumber + ": ";

		if(txtEnergy.getText().isEmpty()) {
			errorMessage += stepX + "Please enter beam energy value\n";
			GUI.toHighlight.add(new HighlightListener(txtEnergy));
		}

		if(txtCurrent.getText().isEmpty()) {
			errorMessage += stepX + "Please enter beam current value\n";
			GUI.toHighlight.add(new HighlightListener(txtCurrent));
		}
		
		if(thisStep.txtIrradiationTime.getText().isEmpty()) {
			errorMessage += stepX + "Please enter Irradiation Time value\n";
			GUI.toHighlight.add(new HighlightListener(thisStep.txtIrradiationTime));
		}
		if(thisStep.txtDecayTime.getText().isEmpty()) {
			errorMessage += stepX + "Please enter Decay Time value\n";
			GUI.toHighlight.add(new HighlightListener(thisStep.txtDecayTime));
		}
		return errorMessage;
	}

	// This method makes sure that all fields have been entered with legal values by the user
	// This method handles the Target Materials panel inputs
	public static String checkInputs() {
		String errorMessage = "";
		BigDecimal[] weightInputs = GUI.weightInputs;
		String[] elementInputs = GUI.elementInputs;
		Map<String, JComboBox<String>> comboBoxMap = GUI.comboBoxMap;
		Map<String, JTextField> textFieldMap = GUI.textFieldMap;

		boolean weightsAddUpToOne = true;
		boolean nothingEntered = true;
		boolean allElemsHaveAssociatedFractionalWeight = true;
		boolean noWeightsEnteredWithoutElement = true;

		
		BigDecimal weightSum = new BigDecimal(0);

		for(int i = 0; i < NUM_INPUTS; i++) {
			BigDecimal thisWeight = weightInputs[i];
			if(weightInputs[i].compareTo(new BigDecimal(0)) == 1)
				weightSum = weightSum.add(thisWeight);
		}

		// Check that the sum of the inputted fractional weights equals one
		if(!(weightSum.compareTo(new BigDecimal(1)) == 0)) {			
			weightsAddUpToOne = false;
		}

		// Check element/weight entry combinations
		for(int i = 0; i < NUM_INPUTS; i++) {		
			String thisElem = elementInputs[i];
			BigDecimal thisWeight = weightInputs[i];

			String thisNum = String.valueOf(i+1);
			String comboName = "comboBox" + thisNum;
			String textFieldName = "txtWeightE" + thisNum;
			JComboBox<String> thisCombo = comboBoxMap.get(comboName);
			JTextField thisWeightField = textFieldMap.get(textFieldName);

			HighlightListener comboHighlight = new HighlightListener(thisCombo);
			HighlightListener weightHighlight = new HighlightListener(thisWeightField);

			BigDecimal negOne = new BigDecimal(-1);

			if(thisWeight.compareTo(negOne) == -1) {
				GUI.toHighlight.add(weightHighlight);
			}

			if(thisWeight.compareTo(negOne) != 0 || thisElem != null)
				nothingEntered = false;

			if(thisElem != null && (thisWeight.compareTo(negOne) == 0)) {
				GUI.toHighlight.add(weightHighlight);
				allElemsHaveAssociatedFractionalWeight = false;
			}
			else
				weightHighlight.defaultBorder();

			if(thisWeight.compareTo(negOne) != 0 && thisElem == null) {
				GUI.toHighlight.add(comboHighlight);
				noWeightsEnteredWithoutElement = false;
			}	
			else
				comboHighlight.defaultBorder();
		}

		if(! weightsAddUpToOne){
			errorMessage += "Fractional weights do not add up to 1.0\n";
		}

		if(!allElemsHaveAssociatedFractionalWeight) {
			errorMessage += "Please enter a fractional weight for each element\n";
		}

		if(! noWeightsEnteredWithoutElement){
			errorMessage +=  "Please enter an element for each fractional weight\n";
		}

		if(nothingEntered) {
			GUI.toHighlight.add(new HighlightListener(comboBoxMap.get("comboBox1")));
			GUI.toHighlight.add(new HighlightListener(textFieldMap.get("txtWeightE1")));
			errorMessage += "Please enter at least one element / fractional weight pair\n";
		}
		return errorMessage;
	}

	public static void exportToFile(Object[][] content) throws IOException {

		if(content == null) {
			content = new Object[1][1];
			content[0][0] = null;
		}

		JFileChooser fileChooser = new JFileChooser();
		int option = fileChooser.showSaveDialog(null);
		String filepath = null;
		if(option == JFileChooser.APPROVE_OPTION) {
			if(! (fileChooser.getSelectedFile().getAbsolutePath() == null)) {
				filepath = fileChooser.getSelectedFile().getAbsolutePath();
			}
			if(!filepath.contains(".")) {
				filepath += ".txt";
			}
			else {
				filepath = filepath.substring(0, filepath.lastIndexOf('.')) + ".txt";
			}
		}
		else {
			// User clicked cancel
			return;
		}

		// Get user's choice of where to save file
		File file = new File(filepath);
		if(!file.exists()) 
			file.createNewFile();

		FileWriter fw;
		fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write("\n\n");
		bw.newLine();

		for(int i = 0; i < content.length; i++) {

			for(int j = 0; j < content[0].length; j++) {
				String toWrite;
				if(content[i][j] == null) 
					toWrite = "";
				else {
					if(j == 0) {
						String noDash = String.valueOf(content[i][j]);
						String[] nameAndNum = BackEnd.getMassAndName(noDash);
						String mass = nameAndNum[0];
						String name = nameAndNum[1].substring(0,1).toUpperCase() + nameAndNum[1].substring(1);

						toWrite = name + "-" + mass;
					}
					else {
						toWrite = String.valueOf(content[i][j]);
					}
				}
				bw.write(toWrite + "\t");
			}
			bw.newLine();
		}
		bw.close();

		JOptionPane.showMessageDialog(null, "Microshield file has been exported to " + filepath);
	}

	// This method writes the passed in ArrayList to the output csv file
	// The file will contain all inputs and outputs, from each step and overall
	public static void writeToCSV(ArrayList<ArrayList<ArrayList<Object>>> contents) throws IOException {

		JFileChooser fileChooser = new JFileChooser();
		int option = fileChooser.showSaveDialog(null);
		String filepath = null;
		if(option == JFileChooser.APPROVE_OPTION) {
			if(! (fileChooser.getSelectedFile().getAbsolutePath() == null)) {
				filepath = fileChooser.getSelectedFile().getAbsolutePath();
			}
		}
		else {
			// User clicked cancel
			return;
		}

		if(!filepath.contains(".")) {
			filepath += ".csv";
		}
		else {
			filepath = filepath.substring(0, filepath.lastIndexOf('.')) + ".csv";
		}
		FileWriter writer = null;
		try {
			writer = new FileWriter(filepath);

			for(ArrayList<ArrayList<Object>> array : contents) {
				for(ArrayList<Object> row: array) {
					for(int i = 0; i < row.size(); i++) {
						Object item = row.get(i);
						writer.append(item + ",");
					}
					writer.append("\n");
				}
				writer.append("\n\n");
			}
			writer.flush();
			writer.close();

		} catch (IOException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null,  "Error occured while writing to CSV file", "Error", JOptionPane.WARNING_MESSAGE);
		}

		JOptionPane.showMessageDialog(null, "Results have been exported to " + filepath);

	}


	public static int checkEnergyArray(ArrayList<Object[]> energyBreakdownArray, double targetEnergy) {
		for(int i = 0; i < energyBreakdownArray.size(); i++) {
			if((double)energyBreakdownArray.get(i)[0] == targetEnergy) {
				return i;
			}
		}

		return -1;
	}

	// Finds the index of the supplied target and returns it, or returns -1 if it cannot be found
	public static int checkEnergyArray(ArrayList<Object[]> inArr, String target) {
		for(int i = 0; i < inArr.size(); i++) {
			if(inArr.get(i)[0].equals(target)) {
				return i;
			}
		}
		return -1;
	}

	// For a 2D array with doubles as identifiers in the first column, combines entries with the same
	// identifying double by summing up their data entries of the second column
	// (e.g. for outputting to the user the neutron yield from all isotopes, broken down by energy level)
	public static ArrayList<Object[]> removeDuplicates(ArrayList<Object[]> energyBreakdownArray) {

		ArrayList<Object[]> retArr = new ArrayList<>();
		for(int i = 0 ; i < energyBreakdownArray.size(); i++) {
			double thisEnergy = ((double) energyBreakdownArray.get(i)[0]);
			double neutronYieldxAbundance;
			if(energyBreakdownArray.get(i)[1] instanceof BigDecimal)
				neutronYieldxAbundance = ((BigDecimal) energyBreakdownArray.get(i)[1]).doubleValue();
			else if(energyBreakdownArray.get(i)[1] instanceof Double)
				neutronYieldxAbundance = ((Double) energyBreakdownArray.get(i)[1]).doubleValue();
			else {
				neutronYieldxAbundance = 0;
			}
			int index = checkEnergyArray(retArr, thisEnergy);

			neutronYieldxAbundance = roundToNumSigFigs(3, BigDecimal.valueOf(neutronYieldxAbundance)).doubleValue();

			if(index == -1) {	// This energy level doesn't exist yet
				Object[] energyBreakdownRow = {thisEnergy, neutronYieldxAbundance};
				retArr.add(energyBreakdownRow);
			}

			else {
				// Energy level already exists
				retArr.get(index)[1] = roundToNumSigFigs(3, BigDecimal.valueOf((double)retArr.get(index)[1] + neutronYieldxAbundance)).doubleValue();
			}
		}
		return retArr;
	}

	// For a 2D array with Strings in the first column, removes repeated entries for the same identifying string
	// and adds the associated data together into one entry
	public static ArrayList<Object[]> removeDuplicatesString(ArrayList<Object[]> inArr) {

		ArrayList<Object[]> retArr = new ArrayList<>();
		for(int i = 0 ; i < inArr.size(); i++) {
			String thisIsotope = (String)inArr.get(i)[0];
			Object data = inArr.get(i)[1];
			double isotopeData = ((BigDecimal) data).doubleValue();
			Object data2 = inArr.get(i)[2];
			double isotopeData2 = ((BigDecimal) data2).doubleValue();

			int index = checkEnergyArray(retArr, thisIsotope);
			if(index == -1) {	// This isotope doesn't exist in retArr yet
				Object[] energyBreakdownRow = {thisIsotope, isotopeData,isotopeData2};
				retArr.add(energyBreakdownRow);
			}
			else {
				if(retArr.get(index)[1] instanceof BigDecimal) {
					retArr.get(index)[1] = ((BigDecimal)retArr.get(index)[1]).doubleValue() + isotopeData;
				}
				else
					retArr.get(index)[1] = (double)retArr.get(index)[1] + isotopeData;
				if(retArr.get(index)[2] instanceof BigDecimal) {
					retArr.get(index)[2] = ((BigDecimal)retArr.get(index)[2]).doubleValue() + isotopeData2;
				}
				else
					retArr.get(index)[2] = (double)retArr.get(index)[2] + isotopeData2;
			}
		}

		return retArr;
	}

	// This method adds a 2D array of Object to an existing ArrayList<Object[]> and outputs the combined ArrayList<Object[]>


	// Adds a 2D array of Object to an ArrayList<Object[]>
	public static ArrayList<Object[]> addToArrayList(ArrayList<Object[]> overall, Object[][] toAdd) {

		for(Object[] row : toAdd) {
			overall.add(row);
		}

		return overall;
	}

	// This method rounds the inputed BigDecimal to the inputed targetNumSigFigs and returns the new value as a BigDecimal.
	// Rounds an inputed BigDecimal to the inputer targetNumSigFigs and outputs as a BigDecimal
	//and cuts the numbers to 3 decimal figures
	public static BigDecimal roundToNumSigFigs(int targetNumSigFigs, BigDecimal in) {
		BigDecimal output = in.round(new MathContext(targetNumSigFigs, RoundingMode.HALF_UP));
		output = output.setScale(3, RoundingMode.CEILING);
		return output;
	}

	public static ArrayList<Object> createArrayListOfObject(Object[] inArr) {

		ArrayList<Object> retArr = new ArrayList<>();
		for(int i = 0; i < inArr.length; i++) {
			Object item = inArr[i];
			retArr.add(item);
		}

		return retArr;

	}

	// This method convertes an ArrayList<Object[]> into an ArrayList<ArrayList<Object>>
	public static ArrayList<ArrayList<Object>> toArrayList(ArrayList<Object[]> inArr) {
		ArrayList<ArrayList<Object>> retArr = new ArrayList<>();

		for(int i = 0; i < inArr.size(); i++) {
			ArrayList<Object> intermed = new ArrayList<>();
			for(int k = 0; k < inArr.get(0).length; k++) {
				intermed.add(inArr.get(i)[k]);
			}
			retArr.add(intermed);
		}
		return retArr;
	}

	// Import previously exported data back into the program and calculate results
	public static void importFromCSV(String csvFile) {
		try {
		resetProgram();
		ArrayList<String[]> contents = new ArrayList<>();
		BufferedReader br;
		String line;

		try{
			br = new BufferedReader(new FileReader(csvFile));
			while((line = br.readLine()) != null) {
				String[] row = line.split(",");
				//System.out.println(line);
				contents.add(row);
			}

		}
		catch(Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,  "An error occured While importing, import file may be corrupted", "Error", JOptionPane.WARNING_MESSAGE);
		}

		int stepOneLine = 0;
		int stepTwoLine = 0;
		/*  this is worrying will it only work for 2 steps?  */
		
		if(!contents.get(0)[0].equals("TARGET MATERIALS (inputs)")) {
			JOptionPane.showMessageDialog(null, "Cannot open supplied file", "Cannot open supplied file", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		for(int i = 0; i < contents.size(); i++) {
			for(int k = 0; k < contents.get(i).length; k++) {
				if(contents.get(i)[k].equals("STEP 1")) {
					stepOneLine = i;
				}
				if(contents.get(i)[k].equals("STEP 2")) {
					stepTwoLine = i;
				}
			}
		}
		stepOneLine += 3;

		int stepDiff = stepTwoLine - stepOneLine;

		Map<String, JComboBox<String>> comboBoxMap = GUI.comboBoxMap;
		Map<String, JTextField> textFieldMap = GUI.textFieldMap;

		// Set all elements and fractional weights
		for(int i = 0; i < 10; i++) {
			String element = contents.get(i + 2)[0];
			String fractionalWeight;
			try {
				fractionalWeight = contents.get(i + 2)[1];
			}
			catch (Exception e) {
				break;
			}
			String thisNum = String.valueOf(i+1);
			String comboName = "comboBox" + thisNum;
			String textFieldName = "txtWeightE" + thisNum;
			JComboBox<String> thisCombo = comboBoxMap.get(comboName);
			JTextField thisTextField = textFieldMap.get(textFieldName);
			thisCombo.setSelectedItem(element); 
			thisTextField.setText(fractionalWeight);
		}
	

/*  it appears fixed here the step thing but not great implementation ie confusing for a reader*/
		int stepLine = stepOneLine;
		int i = 0;
		for(;;) {
			i++;

			// Check if an additional step exists
			// (if the first 4 characters of the line are "STEP"
			if(stepLine > contents.size() || !contents.get(stepLine-3)[0].substring(0,4).equals("STEP"))
				break;			

			if(i > 1) {
				// Add a new StepTab
				GUI.addClosableTab(GUI.tabbedPane);
			}

			StepPanel thisStep = (StepPanel) GUI.tabbedPane.getComponentAt(i-1);
			String[] step = contents.get(stepLine);
			thisStep.txtEnergy.setText(step[0]);
			thisStep.txtCurrent.setText(step[1]);
			thisStep.txtIrradiationTime.setText(step[2].substring(0, step[2].length() - 2));
			thisStep.txtDecayTime.setText(step[3].substring(0, step[3].length() - 2)); //this substring line was wrong it had a 2 in the params instead of a 3
/*//// */
			Double irTimeDouble = Double.parseDouble(thisStep.txtIrradiationTime.getText());
			Double currentDouble = Double.parseDouble(thisStep.txtCurrent.getText());
			BigDecimal doseRounded = BackEnd.roundToNumSigFigs(3, new BigDecimal((currentDouble*Math.pow(10, -6) * irTimeDouble)));
			thisStep.Dose.setText(String.valueOf(doseRounded));

			if(stepDiff <= 0) {
				break;
			}
			stepLine += stepDiff + 3;
		}
		GUI.btnCalculate.doClick();
		}catch(Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Cannot open supplied file", "Cannot open supplied file", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
	}

	// Resets the program to the state it was in on initial launch
	public static void resetProgram() {
		// Clear all fields
		GUI.clearValues();
		((StepPanel)GUI.tabbedPane.getComponentAt(0)).btnClearValues1.doClick();
		// Close all step tabs apart from the first
		int numTabs = GUI.tabbedPane.getTabCount();
		for(int i = 1; i < numTabs - 1; i++) {
			GUI.tabbedPane.remove(1);
		}
		GUI.tabbedPane.setSelectedIndex(0);
		
		for(int k = 0; k < GUI.tabbedPane.getTabCount() - 1; k++) {
			//get tab
			StepPanel thisTab = (StepPanel) GUI.tabbedPane.getComponentAt(k);
			
			//remove rows from results table
			DefaultTableModel model = (DefaultTableModel) thisTab.resultsTable.getModel();
			model.setRowCount(0);
			
			DefaultTableModel model1 = (DefaultTableModel) thisTab.additionalPanel.table.getModel();
			model1.setRowCount(0);
			
			DefaultTableModel model2 = (DefaultTableModel) thisTab.energyBreakdownPanel.table.getModel();
			model2.setRowCount(0);
			
			thisTab.txtNeutronYield.setText("");
			(thisTab.additionalPanel.txtTotalNeutronYield).setText("");
			(thisTab.energyBreakdownPanel.txtTotalNeutronYield).setText("");
			thisTab.txtNeutronDose.setText("");
			thisTab.txtNeutronField.setText("");
			thisTab.txtRealActivity.setText("");
			thisTab.txtDecayActivity.setText("");
			
			GUI.calculateClicked = false;
			
			
		}

	}
}



