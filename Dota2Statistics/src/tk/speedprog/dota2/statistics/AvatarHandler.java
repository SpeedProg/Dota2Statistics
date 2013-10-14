package tk.speedprog.dota2.statistics;

import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.xml.parsers.SAXParser;

import tk.speedprog.dota2.webapi.Dota2Api;

public class AvatarHandler {
	private static final String PATH_TO_AVATARS = "images/avatars";
	private static final String AVATAR_SUFFIX = ".jpg";
	private HashMap<String, ImageIcon> avatars;
	AvatarHandler() {
		avatars = new HashMap<String, ImageIcon>();
		avatars.put(MainWindow.anonymeAccountId, new ImageIcon());
	}
	public ImageIcon getAvatarForSteamId32(String steamId32, int width,
			int height, String apiKey, SAXParser saxParser) {
		if (avatars.containsKey(steamId32)) {
			return avatars.get(steamId32);
		} else {
			File avatar = new File(PATH_TO_AVATARS + File.separator + steamId32
					+ AVATAR_SUFFIX);
			File avatarDir = new File(PATH_TO_AVATARS);
			if (!avatar.exists()) {
				System.out.println(avatar.getAbsolutePath() +" does not exist!");
				if (!avatarDir.exists()) {
					avatarDir.mkdirs();
				}
				URL website = null;
				ReadableByteChannel rbc;
				FileOutputStream fos;
				String[] s = Dota2Api.getPlayerSummaries(steamId32, apiKey, saxParser);

				try {
					website = new URL(s[1]);
					fos = new FileOutputStream(PATH_TO_AVATARS + File.separator
							+ steamId32 + AVATAR_SUFFIX);
					rbc = Channels.newChannel(website.openStream());
					fos.getChannel().transferFrom(rbc, 0, 1 << 24);
					fos.close();
					rbc.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			
			ImageIcon ii = new ImageIcon((new ImageIcon(PATH_TO_AVATARS + "/" + steamId32
					+ AVATAR_SUFFIX)).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH), "");
			avatars.put(steamId32, ii);
			return ii;
		}
	}
}
