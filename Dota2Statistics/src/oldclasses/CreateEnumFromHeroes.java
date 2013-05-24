package oldclasses;


public class CreateEnumFromHeroes {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Heroes_old h = new Heroes_old();
		StringBuilder sb = new StringBuilder();
		for (int c = 0; c < h.heroes.length; c++) {
			if (h.heroes[c] != null) {
				sb.append(h.heroes[c].localName.replace(" ", "_").replace("-", "_").replace("'", "_").toUpperCase()+"("+(c+1)+", \"" + h.heroes[c].name +"\", \""+h.heroes[c].localName+"\"), ");
			} else {
				sb.append("HERO_WITH_I_"+c);
			}
		}
		System.out.println(sb.toString());

	}

}
