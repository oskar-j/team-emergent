package pool;

import java.util.List;

import org.soqqo.datagen.RandomDataGenerator;
import org.soqqo.datagen.config.DataTypes.Name;
import org.soqqo.datagen.config.GenConfig;

import EmergingTeams.DefaultAgent;

public class NamesGenerator {
	
	static RandomDataGenerator rdg = new RandomDataGenerator();

	public static void getnames() {
		List<DefaultAgent> randomPersons = rdg.generateList(
				50,
				new GenConfig().name(Name.Firstname, "firstname").name(
						Name.Lastname, "lastname"), DefaultAgent.class);

	}

}
