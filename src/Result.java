import java.math.BigDecimal;
import java.util.ArrayList;

// This class is for returning a Result (set of all needed calculations for output) in one method, calculateOutputs
public class Result {
	
	public Object[][] outputTable;
	//Object[][] microshieldTable;
	public BigDecimal totalNeutronYield;
	public BigDecimal neutronDose;
	public BigDecimal neutronField;
	public BigDecimal totalRealY;
	public BigDecimal totalDecayY;
	public BigDecimal gamma;
	public ArrayList<Object[]> breakdownArray;
	public ArrayList<Object[]> energyBreakdownArray;
	public ArrayList<Object[]> overallResults;
	
	BigDecimal totalActivityDecay;
	
	Object[][] microshieldTable;
	
	public Result(Object[][] ot, double tnf, double nd, double nf, double ty, double tdy,double g, Object[][] mst, ArrayList<Object[]> bd, ArrayList<Object[]> ebd, ArrayList<Object[]> or) {
		outputTable = ot;
		if(tnf < 0.000001) {
			tnf = 0;
		}
		totalNeutronYield = BackEnd.roundToNumSigFigs(3, new BigDecimal(tnf));
		neutronDose = BackEnd.roundToNumSigFigs(3, new BigDecimal(nd));
		neutronField = BackEnd.roundToNumSigFigs(3, new BigDecimal(nf));
		totalRealY = BackEnd.roundToNumSigFigs(3, new BigDecimal(ty));
		totalDecayY = BackEnd.roundToNumSigFigs(3, new BigDecimal(tdy));
		gamma = BackEnd.roundToNumSigFigs(3, new BigDecimal(g));
		microshieldTable = mst;
		breakdownArray = bd;
		energyBreakdownArray = ebd;
		overallResults = or;
	}

}
