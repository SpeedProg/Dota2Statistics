package tk.speedprog.dota2.statistics;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * load hero icons from valve servers and save to disk.
 * @author SpeedProg
 */
final class HeroIconsLoader {

	/**
	 * main.
	 * @param args command line arguments
	 * @throws IOException thrown is shit happens
	 */
	public static void main(final String[] args) throws IOException {
		URL website;
		ReadableByteChannel rbc;
		FileOutputStream fos;
		for (Hero h : Hero.values()) {
			if (h != null) {
				System.out.println(h.getName());
				String picname = h.getName().substring(14);
				try {
					website = new URL("http://media.steampowered.com/apps/dota2/images/heroes/"+picname+"_sb.png");
					rbc = Channels.newChannel(website.openStream());
					fos = new FileOutputStream("images/heroes/"+picname+"_sb.png");
					fos.getChannel().transferFrom(rbc, 0, 1 << 24);
					fos.close();
					rbc.close();
					System.out.println("Done");
				} catch (FileNotFoundException fnfe) {
					fnfe.printStackTrace();
				}
			}
		}

	}

	/**
	 * private constructor.
	 */
	private HeroIconsLoader() {

	}
}
