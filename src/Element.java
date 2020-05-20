import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Element {

	int atomicNum;
	String name;
	String label;	// label is atomic number concatenated with element name/symbol, e.g. 1H
	ArrayList<Isotope> isotopes = new ArrayList<>();	// to hold isotopes of this element
	double fractionalWeight;	// user inputted fractional weight for this element

	// User inputted parameters
	double energy;
	double current;
	double irradiationTime;
	double decayTime;

	public Element(int z, String n, double[] irradiationParams, double weight, Connection conn) {
		isotopes = new ArrayList<>();
		atomicNum = z;
		name = n;
		label = atomicNum + name;
		energy = irradiationParams[0];
		current = irradiationParams[1];
		irradiationTime = irradiationParams[2];
		decayTime = irradiationParams[3];
		fractionalWeight = weight;

		try {
			Statement s = conn.createStatement();

			// Get all isotopes of this element
			ResultSet isotopeNames = s.executeQuery("SELECT isotope FROM isotopes WHERE atomicnum='" + atomicNum + "'");

			while(isotopeNames.next()) {
				// Make an Isotope Object for each isotope of this element, store in isotopes ArrayList
				String isotopeName = isotopeNames.getString(1);
				if(fractionalWeight != 0) {
					Isotope thisIsotope = new Isotope(isotopeName, conn, irradiationParams, fractionalWeight);
					isotopes.add(thisIsotope);
				}
			}
		} catch (SQLException exc) {
			exc.printStackTrace();
		}
		//isotopes doesn't exist error the calculations continue but only gives 0 for the isotopes of this element
		if(isotopes.isEmpty() == true) {
			JOptionPane.showMessageDialog(null,  "No Isotopes exist in database for " + label, "Error", JOptionPane.WARNING_MESSAGE);
		}
	}

	// Debug method for printing out isotopes of this element
	public void printIsotopes() {
		System.out.println("Printing from printIsotopes for element: " + toString());
		if(isotopes != null) {

			for(Isotope elem : isotopes) {
				System.out.println(elem);
			}
		}
	}

	public String toString() {
		return label;
	}

	public void setName(String newName) {
		name = newName;
	}
}
