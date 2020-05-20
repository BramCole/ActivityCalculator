import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Isotope {
	String name;
	double abundance;
	double halfLife;
	double gamma;

	double energy;
	double current;
	double irradiationTime;
	double decayTime;
	double fractionalWeight;

	// Final outputs
	String transmuted;
	double outNeutronYield;
	double outRealTimeSourceActivity;
	double outSourceDecayActivity;
	double outgamma;
	double doseRate;

	ArrayList<Object[]> breakdownArray = new ArrayList<>();
	ArrayList<Object[]> energyBreakdownArray = new ArrayList<>();

	double[][] data;			// 4 columns filled with database data from isotope data sheet
	// Exactly 10 rows

	//		 DATA array

	//			[0]		[1]		[2]		[3]				[]			[]			[]			
	//			Energy	Range	nT		crossSection	ARange		AnT		AcrossSection
	//	[0]		1		--		--		--				--			--			--
	//	[1]		1.5		--		--		--				--			--			--
	//	[2]		2		--		--		--				--			--			--
	//	[3]		2.5		--		--		--				--			--			--
	//	[4]		3		--		--		--				--			--			--
	//	[5]		3.5		--		--		--				--			--			--
	//	[6]		4		--		--		--				--			--			--
	//	[7]		4.5		--		--		--				--			--			--
	//	[8]		5		--		--		--				--			--			--
	//	[9]		5.5		--		--		--				--			--			--
	//	[10]	6		--		--		--				--			--			--
	//	[11]	6.5		--		--		--				--			--			--
	//	[12]	7		--		--		--				--			--			--
	//	[13]	7.5		--		--		--				--			--			--

	ArrayList<double[]> calculations;	// 6 columns filled with values calculated from data in DATA array
	// Variable number of rows (up to 10) based on user-entered beam energy

	//		CALCULATIONS array

	//			[0]		[1]				[2]						[3]						[4]		[5]
	//			Energy	neutronYield	neutronYieldxAbundance	summationNeutronYield	realY	decayY
	//	[0]		1		--		--		--				--			--			--
	//	[1]		1.5		--		--		--				--			--			--
	//	[2]		2		--		--		--				--			--			--
	//	[3]		2.5		--		--		--				--			--			--
	//	[4]		3		--		--		--				--			--			--
	//	[5]		3.5		--		--		--				--			--			--
	//	[6]		4		--		--		--				--			--			--
	//	[7]		4.5		--		--		--				--			--			--
	//	[8]		5		--		--		--				--			--			--
	//	[9]		5.5		--		--		--				--			--			--
	//	[10]	6		--		--		--				--			--			--
	//	[11]	6.5		--		--		--				--			--			--
	//	[12]	7		--		--		--				--			--			--
	//	[13]	7.5		--		--		--				--			--			--

	public Isotope(String n, Connection conn, double[] irradiationParams, double fw) {
		
		// Initializing variables and getting input values 
		transmuted = "o";
		outNeutronYield = 0;
		outRealTimeSourceActivity = 0;
		outSourceDecayActivity = 0;

		name = n;
		data = new double[14][4];
		calculations = new ArrayList<>();

		energy = irradiationParams[0];
		current = irradiationParams[1];
		irradiationTime = irradiationParams[2];
		decayTime = irradiationParams[3];
		fractionalWeight = fw;

		try {
			Statement s = conn.createStatement();

			// Get data related to this isotope from this specific isotope's table in database
			String query = "SELECT E, range, nT, crossSection FROM " + name;
			try {
				if(GUI.getSelectedItem().equals("Alpha")) {
				query = "SELECT E, Arange, AnT, AcrossSection FROM " + name;
				}
			}catch (Exception e){
					JOptionPane.showMessageDialog(null,  "Data does not exist to do this calculation", "Error", JOptionPane.WARNING_MESSAGE);
					return;
			}
			ResultSet isotopeData;
			try {
				isotopeData = s.executeQuery(query);
			} catch(Exception E) {
					JOptionPane.showMessageDialog(null,  "Isotope " + name+ " is listed in overall isotopes tabel, but individual isotope table is mising", "Error", JOptionPane.WARNING_MESSAGE);
					return;
			}
			
			int i = 0;
			while(isotopeData.next()) {
				double[] row = new double[4];
				row[0] = isotopeData.getDouble(1);	//energy
				row[1] = isotopeData.getDouble(2);	//range
				row[2] = isotopeData.getDouble(3);	//nT
				row[3] = isotopeData.getDouble(4);	//crossSection
				data[i] = row;
				i++;
			}
			
			//see if this isotope has all 0s
			
		} catch (SQLException exc) {
			exc.printStackTrace();
		}	

		// Get data related to this isotope from 'isotopes' table in database
		Statement s2;
		try {
			s2 = conn.createStatement();
			String query2 = "SELECT abundance, transmuted, halflife , gamma FROM isotopes WHERE isotope='" + name + "'";
			ResultSet isotopeData2 = s2.executeQuery(query2);
			while(isotopeData2.next()) {
				abundance = isotopeData2.getDouble(1);
				transmuted = isotopeData2.getString(2);
				halfLife = isotopeData2.getDouble(3);
				gamma = isotopeData2.getDouble(4);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		double tot = 0;
		for(int j = 0; j < data.length; j++) {
			for(int k = 1; k < data[j].length; k++) {
				tot += data[j][k];
			}
		}
		
		//error message for when the data in the db is empty or 0s
		String error = "Some data for isotope "+ name +" is empty or 0";
		if(tot == 0) {
			error += "\nThe Data Table is empty, or all 0s for this calculation";
		}if(abundance == 0) {
			error += "\n Abundance = 0";
		}if(halfLife == 0) {
			error += "\n Halflife = 0";
		}if(gamma == 0) {
			error += "\n Gamma = 0";
		}
		
		if(!error.equals("Some data for isotope "+ name +" is empty or 0")) {
			JOptionPane.showMessageDialog(null,  error, "Warning", JOptionPane.WARNING_MESSAGE);
		}

		int energyIndex = getEnergyIndex(energy);

		if(energyIndex == -1){
			outNeutronYield = 0;
			outRealTimeSourceActivity = 0;
			outSourceDecayActivity = 0;
		}
		// Only do the calculations up to the specified energy level
		for (int i = 0; i <= energyIndex; i++) {
			@SuppressWarnings("unused")
			InterpolatedValues result;		

			double thisEnergy = data[i][0];

			double nT;
			double crossSection;
			double ratio;
			double neutronYield;

			if(i == energyIndex) {
				result = BackEnd.getInterpolatedValues(energy, name);
				double nT1 = data[i][2];
				double crossSection1 = data[i][3];
				nT = result.nT;
				crossSection = result.crossSection;
				ratio = result.ratio;
				double neutronYield1 = nT1 * crossSection1 * current * 60.0;
				neutronYield = (nT * crossSection * current * 60.0 * ratio) + neutronYield1;
			}

			else {
				nT = data[i][2];
				crossSection = data[i][3];
				neutronYield = nT * crossSection * current * 60.0;
			}

			// Calculations for CALCULATIONS array
			
			// Multiply here to adjust the neutron yield and dose values
			double neutronYieldxAbundance = neutronYield * (abundance/100) * 10;

			// Get the neutron yield of this energy level
			// Make a row for the breakdown table using this level and this isotope
			Object[] breakdownRow = {name, thisEnergy, BackEnd.roundToNumSigFigs(3, new BigDecimal(neutronYieldxAbundance * fractionalWeight))};
			breakdownArray.add(breakdownRow);
			Object[] energyBreakdownRow = {thisEnergy, BackEnd.roundToNumSigFigs(3, new BigDecimal(neutronYieldxAbundance * fractionalWeight))};
			energyBreakdownArray.add(energyBreakdownRow);

			double summationNeutronYield;
			if(i == 0) {
				summationNeutronYield = neutronYieldxAbundance;
			}
			else {
				summationNeutronYield = neutronYieldxAbundance + calculations.get(i-1)[3];	// add previous summationNeutronYield value
			}
			double ln2 = Math.log(2); 	// = 0.693147
			double lambda = ln2/halfLife;
			double realY = summationNeutronYield * (1 - (Math.exp(-lambda * irradiationTime))); 
			double decayY = realY * (Math.exp(-lambda * decayTime));

			// Put these 5 calculations into a new row in the CALCULATIONS array
			double[] row = new double[6];
			
			row[0] = data[i][0];
			row[1] = neutronYield;
			row[2] = neutronYieldxAbundance;
			row[3] = summationNeutronYield;
			row[4] = realY;
			row[5] = decayY;
			
			calculations.add(row);
			
			//System.out.println(row[0] + " " + row[1] + " " + row[2] + " " + row[3] + " " + row[4] + " " + row[5] + " ");
			//System.out.println(summationNeutronYield);
			
			if(i == energyIndex) {
				outNeutronYield = summationNeutronYield * fractionalWeight;
				
				outRealTimeSourceActivity = realY * fractionalWeight;
				if(outRealTimeSourceActivity < 0.000001) {
					outRealTimeSourceActivity = 0;
				}
				
				//gamma calculation
				//To calculate the gamma field, you multiply the radioactivity (in Bq) times (yK):    y = A x yK  
				//D2 = D1 x 100^2 / 30^2 for 30 cm distance
				outgamma = (outRealTimeSourceActivity*1000000) * gamma;
				outgamma = outgamma * Math.pow(100, 2) / Math.pow(30, 2);
				outgamma = outgamma*1000000;
				
				outSourceDecayActivity = decayY * fractionalWeight;
				if(outSourceDecayActivity < 0.000001) {
					outSourceDecayActivity = 0;
				}
			}
			
		}
	}

	// Method to get the row index associated with the user-inputed energy value 
	// (i.e. which bin it falls into)
	public int getEnergyIndex(double energyLevel) {
		if(energyLevel <= 1 || energyLevel > 8) {
			return -1;
		}
		
		for(int i = 0; i < data.length; i++) {
			if(i != data.length - 1) {
				if(data[i][0] < energyLevel && data[i+1][0] >= energyLevel){
					return i;
				}
			}
			else {
				return data.length - 1;
			}
		}
		return 0;
	}

	// Debug method to print the DATA and CALCULATIONS array for this isotope
	// (as well as abundance, activity and activity decay)
	public void printData() {
		System.out.println("Printing from printData for isotope: " + toString());
		System.out.println("abundance: " + abundance);
		System.out.println("DATA");
		for(double[] i : data) {
			for(double k : i) {
				System.out.print(String.format("%.3f", k) + "\t\t\t");
			}
			System.out.println();
		}

		System.out.println("CALCULATIONS");
		for(double[] i : calculations) {
			for(double k : i) {
				System.out.print(k + "\t\t\t");
			}
			System.out.println();
		}

		//System.out.println("Activity: " + outActivity + "\tActivity Decay: " + outActivityDecay);
	}

	public String toString() {
		return name;
	}
}
