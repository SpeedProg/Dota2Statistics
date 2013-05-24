package general;
import java.util.Comparator;

public class HeroEnumComperator implements Comparator<Hero> {

	@Override
	public int compare(Hero arg0, Hero arg1) {
		if (arg0.getName().equals("npc_dota_all_heroes")) {
			if (!arg1.getName().equals("npc_dota_all_heroes")) {

				return -1;
			} else {
				return 0;
			}
		} else if (arg1.getName().equals("npc_dota_all_heroes")) {
			if (!arg0.getName().equals("npc_dota_all_heroes")) {
				return 1;
			} else {
				return 0;
			}
		} else {
			return arg0.getLocalName().compareTo(arg1.getLocalName());
		}
	}

}
