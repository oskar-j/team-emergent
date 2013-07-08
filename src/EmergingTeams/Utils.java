package EmergingTeams;

import java.util.ArrayList;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

public class Utils {

	public static ArrayList<Double> readDoubleList(String param) {
		ArrayList<Double> result = new ArrayList<Double>();
		Parameters params = RunEnvironment.getInstance().getParameters();
		String tableAsString = (String) params.getValue(param);
		for (String element : tableAsString.split(",")) {
			result.add(Double.parseDouble(element));
		}
		return result;
	}
}
