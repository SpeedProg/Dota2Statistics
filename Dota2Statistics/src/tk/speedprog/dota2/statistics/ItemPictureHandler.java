package tk.speedprog.dota2.statistics;
import java.awt.Image;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;



public class ItemPictureHandler {
	private HashMap<String, Icon> icons;
	private static String ICON_PATH = "images/items/";
	private static String ICON_SUFFIX = "_sb.png";
	public ItemPictureHandler() {
		icons = new HashMap<String, Icon>();
	}

	public Icon getIcon(String name) {
		if (icons.containsKey(name)) {
			return icons.get(name);
		} else {
			ImageIcon img = new ImageIcon(ICON_PATH+name+ICON_SUFFIX);
			img = new ImageIcon(img.getImage().getScaledInstance(44, 33, Image.SCALE_SMOOTH));
			icons.put(name, img);
			return img;
		}
	}
}
