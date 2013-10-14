package tk.speedprog.dota2.statistics;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import org.xml.sax.SAXException;

import tk.speedprog.dota2.webapi.AbilityUpgrade;
import tk.speedprog.dota2.webapi.AditionalUnit;
import tk.speedprog.dota2.webapi.Dota2Api;
import tk.speedprog.dota2.webapi.Match;
import tk.speedprog.dota2.webapi.MatchDetails;
import tk.speedprog.dota2.webapi.MatchHistoryResult;
import tk.speedprog.dota2.webapi.PlayerStats;

import net.miginfocom.swing.MigLayout;
import javax.swing.JTextField;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.awt.Color;
import java.awt.Font;

public class MainWindow implements ActionListener {

	private JFrame frmDotastatistics;
	private JTable tablePlayedHeros;
	private JLabel lblGamesOutput, lblKillsOutput, lblDeathsOutput,
			lblAssistsOutput, lblKdaOutput, lblKdOutput, lblXpmOutput,
			lblGpmOutput;
	private JButton btnLoadHeroiconsFrom;
	private JTable tableMatchList;
	private JComboBox<Hero> comboBoxHero;
	private JComboBox<GameMode> comboBoxGameMode;
	private JComboBox<GameType> comboBoxMatchType;
	private ItemPictureHandler itemPictureHandler;
	private JComboBox<String> comboBoxSteamId32;
	private Connection con;
	public static final AvatarHandler avatarHandler = new AvatarHandler();
	public static final String anonymeAccountId = "4294967295";
	private JButton btnLoadData;
	private Thread updateThread;
	private Thread iconLoader;
	private JTextField textFieldApiKey;
	private JLabel lblSteamapikey;
	private JLabel lblKlickToMake;
	private SAXParser saxParser;
	
	/**
	 * Launch the application.
	 * 
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) throws ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frmDotastatistics.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		itemPictureHandler = new ItemPictureHandler();
		try {
			saxParser = (SAXParserFactory.newInstance()).newSAXParser();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		createDatabaseIfNotExists();
		con = getDatabaseConnection();
		frmDotastatistics = new JFrame();
		frmDotastatistics.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				Path steamidPath = Paths.get(".", "steamids.txt");
				List<String> steamids = new LinkedList<String>();
				for (int c = 0; c < comboBoxSteamId32.getItemCount(); c++) {
					steamids.add(comboBoxSteamId32.getItemAt(c));
				}
				try {
					Files.write(steamidPath, steamids, StandardCharsets.UTF_8);
				} catch (IOException e) {
					e.printStackTrace();
				}
				Path apiKeyPath = Paths.get(".", "api.key");
				List<String> keys = new LinkedList<String>();
				keys.add(textFieldApiKey.getText());
				try {
					Files.write(apiKeyPath, keys, StandardCharsets.UTF_8);
				} catch (IOException e) {
					e.printStackTrace();
				}
				frmDotastatistics.dispose();
			}
		});
		frmDotastatistics.setTitle("Dota2Statistics v0.1.0.2");
		frmDotastatistics.setBounds(100, 100, 932, 500);
		frmDotastatistics.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frmDotastatistics.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		JPanel panelSettings = new JPanel();
		tabbedPane.addTab("Settings", null, panelSettings, null);
		panelSettings.setLayout(new MigLayout("", "[][grow][grow]", "[][][][]"));

		JLabel lblSteamid = new JLabel("SteamId32:");
		panelSettings.add(lblSteamid, "cell 0 0,alignx trailing");

		btnLoadData = new JButton("Load Data");
		btnLoadData.setActionCommand("load data");
		btnLoadData.addActionListener(this);

		comboBoxSteamId32 = new JComboBox<String>();
		comboBoxSteamId32.setEditable(true);
		Path settingPath = Paths.get(".", "steamids.txt");
		try {
			List<String> settingsLines = Files.readAllLines(settingPath,
					StandardCharsets.UTF_8);
			for (String line : settingsLines) {
				comboBoxSteamId32.addItem(line);
			}
		} catch (IOException e) {
			// file isn't there, all fine :)
			// e.printStackTrace();
		}
		lblSteamid.setLabelFor(comboBoxSteamId32);
		panelSettings.add(comboBoxSteamId32, "cell 1 0,growx");
		
		lblSteamapikey = new JLabel("SteamAPIKey:");
		panelSettings.add(lblSteamapikey, "cell 0 1,alignx trailing");
		
		textFieldApiKey = new JTextField();
		Path apiPath = Paths.get(".", "api.key");
		try {
			List<String> apikeyLines = Files.readAllLines(apiPath,
					StandardCharsets.UTF_8);
			if (apikeyLines.size()>0) {
				textFieldApiKey.setText(apikeyLines.get(0));
			}
		} catch (IOException e) {
			// file isn't there, all fine :)
			// e.printStackTrace();
		}
		panelSettings.add(textFieldApiKey, "cell 1 1,growx");
		textFieldApiKey.setColumns(10);
		
		lblKlickToMake = new JLabel("Klick to make a Steam API Key");
		lblKlickToMake.setForeground(Color.BLUE);
		lblKlickToMake.setFont(new Font("Tahoma", Font.ITALIC, 11));
		lblKlickToMake.setBackground(Color.WHITE);
		lblKlickToMake.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					java.awt.Desktop.getDesktop().browse(new URI("https://steamcommunity.com/login/home/?goto=%2Fdev%2Fapikey"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		panelSettings.add(lblKlickToMake, "cell 2 1");
		panelSettings.add(btnLoadData, "cell 1 2");

		btnLoadHeroiconsFrom = new JButton("Load Icons from server");
		btnLoadHeroiconsFrom.setActionCommand("load icons");
		btnLoadHeroiconsFrom.addActionListener(this);
		panelSettings.add(btnLoadHeroiconsFrom, "cell 1 3");

		JPanel panelOverview = new JPanel();
		tabbedPane.addTab("Overview", null, panelOverview, null);
		panelOverview.setLayout(new MigLayout("", "[][][][][][][][][grow]",
				"[][grow][][][]"));
		JLabel lblPlayedHeros = new JLabel("Played Heros:");
		panelOverview.add(lblPlayedHeros, "cell 0 0");

		JScrollPane scrollPane = new JScrollPane();
		panelOverview.add(scrollPane, "cell 0 1 9 1,grow");

		tablePlayedHeros = new JTable();
		tablePlayedHeros.setEnabled(false);
		tablePlayedHeros.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tablePlayedHeros.setAutoCreateRowSorter(true);
		tablePlayedHeros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tablePlayedHeros.setFillsViewportHeight(true);
		scrollPane.setViewportView(tablePlayedHeros);
		tablePlayedHeros.setModel(new DefaultTableModel(new Object[][] {},
				new String[] { "Icon", "Hero", "Games", "KD", "GPM", "XPM",
						"KDA", "% Win", "Wins/Looses", "% Hero Damage" }) {
			private static final long serialVersionUID = 6960714454150550410L;
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] { Icon.class, String.class,
					Integer.class, Double.class, Integer.class, Integer.class,
					Double.class, Double.class, String.class,
					Double.class };

			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}

			boolean[] columnEditables = new boolean[] { true, true, true, true,
					true, true, true, false, true, true};

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		tablePlayedHeros.setRowHeight(33);
		tablePlayedHeros.getTableHeader().setReorderingAllowed(false);
		applyPlayedTableSettings();

		JLabel lblPlayerstats = new JLabel("Playerstats:");
		panelOverview.add(lblPlayerstats, "cell 0 2");

		JLabel lblGames = new JLabel("Games:");
		panelOverview.add(lblGames, "cell 0 3");

		JLabel lblKills = new JLabel("Kills:");
		panelOverview.add(lblKills, "cell 1 3");

		JLabel lblDeaths = new JLabel("Deaths:");
		panelOverview.add(lblDeaths, "cell 2 3");

		JLabel lblAssists = new JLabel("Assists:");
		panelOverview.add(lblAssists, "cell 3 3");

		JLabel lblKd = new JLabel("KD:");
		panelOverview.add(lblKd, "cell 4 3");

		JLabel lblKda = new JLabel("KDA:");
		panelOverview.add(lblKda, "cell 5 3");

		JLabel lblGpm = new JLabel("GPM:");
		panelOverview.add(lblGpm, "cell 6 3");

		JLabel lblXpm = new JLabel("XPM:");
		panelOverview.add(lblXpm, "cell 7 3");

		lblGamesOutput = new JLabel("total games");
		panelOverview.add(lblGamesOutput, "cell 0 4");

		lblKillsOutput = new JLabel("Kills");
		panelOverview.add(lblKillsOutput, "cell 1 4");

		lblDeathsOutput = new JLabel("Deaths");
		panelOverview.add(lblDeathsOutput, "cell 2 4");

		lblAssistsOutput = new JLabel("Assists");
		panelOverview.add(lblAssistsOutput, "cell 3 4");

		lblKdOutput = new JLabel("KD");
		panelOverview.add(lblKdOutput, "cell 4 4");

		lblKdaOutput = new JLabel("KDA");
		panelOverview.add(lblKdaOutput, "cell 5 4");

		lblGpmOutput = new JLabel("GPM");
		panelOverview.add(lblGpmOutput, "cell 6 4");

		lblXpmOutput = new JLabel("XPM");
		panelOverview.add(lblXpmOutput, "cell 7 4");

		JPanel panelMatches = new JPanel();
		tabbedPane.addTab("Matches", null, panelMatches, null);
		panelMatches.setLayout(new MigLayout("", "[grow]", "[][grow]"));

		comboBoxHero = new JComboBox<Hero>();
		comboBoxHero.setActionCommand("hero changed");
		comboBoxHero.addActionListener(this);
		Hero[] heroes = Hero.values();
		Arrays.sort(heroes, new HeroEnumComperator());
		comboBoxHero.setModel(new DefaultComboBoxModel<Hero>(heroes));
		panelMatches.add(comboBoxHero, "flowx,cell 0 0");

		JScrollPane scrollPaneMatchListTable = new JScrollPane();
		panelMatches.add(scrollPaneMatchListTable, "cell 0 1,grow");

		tableMatchList = new JTable();
		tableMatchList.setEnabled(false);
		tableMatchList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!(e.getClickCount() == 2)) {
					return;
				}
				int row = tableMatchList.rowAtPoint(new Point(e.getX(), e
						.getY()));
				final String matchId = (String) tableMatchList.getValueAt(row, 7);
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						Connection con;
						MatchDetails md = new MatchDetails();
						try {
							con = DriverManager
									.getConnection("jdbc:sqlite:database.db");
							String getMatchQuery = "select radiantWin, totalHeroDmg, gameMode, duration from Match where matchId = '"
									+ matchId + "';";
							String getPlayerStatsQuery = "select item0, item1, item2, item3, item4, item5, kills, deaths, assists, gold, lastHits, denies, gpm, xpm, heroDmg, lvl, accountId, isDire, playerSlot, heroId, hasAdUnit from PlayerStats where matchId = '"
									+ matchId + "';";
							String getPlayerNameQuery = "select accountName from Account where accountId = ?;";
							Statement stat = con.createStatement();
							PreparedStatement getPlayerNameStat = con
									.prepareStatement(getPlayerNameQuery);
							PreparedStatement pStatInsertAccount = con
									.prepareStatement("insert into Account (accountId, accountName) values (?, ?);");
							ResultSet matchQueryRS = stat
									.executeQuery(getMatchQuery);
							if (matchQueryRS.next()) {
								md.matchId = matchId;
								md.radiantWin = matchQueryRS.getBoolean(1);
								md.gameMode = matchQueryRS.getInt(3);
								md.duration = new BigInteger(matchQueryRS.getString(4));
								ResultSet playerQueryRS = stat
										.executeQuery(getPlayerStatsQuery);
								while (playerQueryRS.next()) {
									PlayerStats ps = new PlayerStats();
									for (int c = 0; c < 6; c++) {
										ps.items[c] = playerQueryRS
												.getInt(c + 1);
									}
									ps.kills = new BigInteger(playerQueryRS.getString(7));
									ps.deaths = new BigInteger(playerQueryRS.getString(8));
									ps.assists = new BigInteger(playerQueryRS.getString(9));
									ps.gold = new BigInteger(playerQueryRS.getString(10));
									ps.lastHits = new BigInteger(playerQueryRS.getString(11));
									ps.denies = new BigInteger(playerQueryRS.getString(12));
									ps.gpm = new BigInteger(playerQueryRS.getString(13));
									ps.xpm = new BigInteger(playerQueryRS.getString(14));
									ps.heroDamage = new BigInteger(playerQueryRS.getString(15));
									ps.lvl = new BigInteger(playerQueryRS.getString(16));
									ps.player.accountId = playerQueryRS
											.getString(17);
									ps.player.isDire = playerQueryRS
											.getBoolean(18);
									ps.player.playerSlot = playerQueryRS
											.getInt(19);
									ps.player.heroId = playerQueryRS.getInt(20);
									if (!ps.player.accountId
											.equals("4294967295")) {
										getPlayerNameStat.setString(1,
												ps.player.accountId);
										ResultSet playerName = getPlayerNameStat
												.executeQuery();
										if (playerName.next()) {
											ps.player.name = playerName
													.getString(1);
										} else {
												String[] playerdata = Dota2Api.getPlayerSummaries(ps.player.accountId, textFieldApiKey.getText(), saxParser);
												pStatInsertAccount.setString(1,
														ps.player.accountId);
												pStatInsertAccount.setString(2,
														playerdata[0]);
												pStatInsertAccount.addBatch();
												ps.player.name = playerdata[0];
										}
									} else {
										ps.player.name = "Anonym";
									}
									md.players.add(ps);
								}
								playerQueryRS.close();
							}
							matchQueryRS.close();
							stat.close();
							pStatInsertAccount.executeBatch();
							pStatInsertAccount.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}

						MatchWindow mw = new MatchWindow(md, textFieldApiKey.getText(), saxParser);
						mw.frame.setVisible(true);
					}
				});
			}
		});
		tableMatchList.getTableHeader().setReorderingAllowed(false);
		tableMatchList.setAutoCreateRowSorter(true);
		tableMatchList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableMatchList.setModel(getCleanDefaultTableModelForMatchTable());
		applyMatchTableSettings();
		tableMatchList.setRowHeight(33);

		scrollPaneMatchListTable.setViewportView(tableMatchList);

		comboBoxGameMode = new JComboBox<GameMode>();
		comboBoxGameMode.setActionCommand("gamemode changed");
		comboBoxGameMode.addActionListener(this);
		comboBoxGameMode.setModel(new DefaultComboBoxModel<GameMode>(GameMode
				.values()));
		panelMatches.add(comboBoxGameMode, "cell 0 0");

		comboBoxMatchType = new JComboBox<GameType>();
		comboBoxMatchType.setModel(new DefaultComboBoxModel<GameType>(GameType
				.values()));
		comboBoxMatchType.setActionCommand("gametype changed");
		comboBoxMatchType.addActionListener(this);
		panelMatches.add(comboBoxMatchType, "cell 0 0");
	}

	@Override
	public final void actionPerformed(final ActionEvent e) {
		switch (e.getActionCommand()) {
		case "load data":
			if (updateThread != null && updateThread.isAlive()) {
				return;
			}
			btnLoadData.setText("Loading...");
			btnLoadData.setEnabled(false);
			updateThread = new Thread(new Runnable() {
				@Override
				public void run() {
					String steamId = (String) comboBoxSteamId32
							.getSelectedItem();
					if (comboBoxSteamId32.getSelectedIndex() == -1) {
						comboBoxSteamId32.addItem(steamId);
					}
					try {
						getNewMatchesForPlayer(steamId);
					} catch (SQLException e4) {
						e4.printStackTrace();
					} catch (SAXException e4) {
						e4.printStackTrace();
					} catch (IOException e4) {
						e4.printStackTrace();
					}
					try {
						if (con == null || con.isClosed()) {
							con = getDatabaseConnection();
						}
					} catch (SQLException e3) {
						e3.printStackTrace();
					}
					if (con != null) {
						Statement getAllStatsWithPid = null;
						try {
							getAllStatsWithPid = con.createStatement();
						} catch (SQLException e2) {
							e2.printStackTrace();
						}

						String sqlQueryGetPlayerStatsFrom = "select kills, deaths, assists, heroId, gpm, xpm, matchId, isDire from PlayerStats"
								+ " where accountId = '" + steamId + "';";
						String sqlQueryGetMatchWinner = "select radiantWin, gameMode, duration from Match where matchId = ?";
						String sqlQueryGetPlayerDmg = "select heroDmg, accountId from PlayerStats where matchId = ?";
						PreparedStatement psQueryGetMatchWinner = null;
						PreparedStatement psQueryPlayerDmg = null;
						try {
							psQueryGetMatchWinner = con
									.prepareStatement(sqlQueryGetMatchWinner);
							psQueryPlayerDmg = con
									.prepareStatement(sqlQueryGetPlayerDmg);
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
						ResultSet rsStatsFromPlayer = null;
						try {
							rsStatsFromPlayer = getAllStatsWithPid
									.executeQuery(sqlQueryGetPlayerStatsFrom);
						} catch (SQLException e2) {
							e2.printStackTrace();
						}
						if (getAllStatsWithPid != null
								&& rsStatsFromPlayer != null
								&& psQueryGetMatchWinner != null) {
							Hashtable<Integer, HeroStats> heroTable = new Hashtable<Integer, HeroStats>();
							int gameCount = 0, kills = 0, deaths = 0, assists = 0, gpm = 0, xpm = 0;
							try {
								while (rsStatsFromPlayer.next()) {
									// count complete kills
									String rMatchId = rsStatsFromPlayer
											.getString(7);
									psQueryGetMatchWinner
											.setString(1, rMatchId);
									ResultSet matchWinnerRs = psQueryGetMatchWinner
											.executeQuery();
									int rGameMode = matchWinnerRs.getInt(2);

									// skip matches that are shorter then 300s
									if (matchWinnerRs.getInt(3) < 300) {
										continue;
									}
									int rKills = rsStatsFromPlayer.getInt(1), rDeaths = rsStatsFromPlayer
											.getInt(2), rAssists = rsStatsFromPlayer
											.getInt(3), rGpm = rsStatsFromPlayer
											.getInt(5), rXpm = rsStatsFromPlayer
											.getInt(6), rHeroId = rsStatsFromPlayer
											.getInt(4);
									boolean rWon = false, rIsDire = rsStatsFromPlayer
											.getBoolean(8);
									boolean rRadiantWin = matchWinnerRs
											.getBoolean(1);

									if (rGameMode == GameMode.DIRETIDE.getId()
											|| rGameMode == GameMode.GREEVILING
													.getId()) {
										continue;
									}
									if (matchWinnerRs.next()) {
										if ((rRadiantWin && !rIsDire)
												|| (!rRadiantWin && rIsDire)) {
											rWon = true;
										} else {
											rWon = false;
										}
									} else {
										System.err.println("Match with id: "
												+ rMatchId + " was not found!");
									}

									// get all the dmg in the game
									psQueryPlayerDmg.setString(1, rMatchId);
									ResultSet rsPlayerDmg = psQueryPlayerDmg
											.executeQuery();
									int ownDmg = 0, totalDmg = 0;
									double rPercentHeroDmg = 0;
									while (rsPlayerDmg.next()) {
										if (rsPlayerDmg.getString(2)
												.equalsIgnoreCase(steamId)) {
											ownDmg = rsPlayerDmg.getInt(1);
										}
										totalDmg += rsPlayerDmg.getInt(1);
									}
									if (ownDmg == 0 || totalDmg == 0) {
										rPercentHeroDmg = 0;
									} else {
										rPercentHeroDmg = ((double) ownDmg)
												/ totalDmg;
									}
									gameCount += 1;
									kills += rKills;
									deaths += rDeaths;
									assists += rAssists;
									gpm += rGpm;
									xpm += rXpm;
									// check if Hero is in the Table
									if (!heroTable.containsKey(rHeroId)) {
										// make new and add
										HeroStats hs = new HeroStats();
										hs.assists += rAssists;
										hs.deaths += rDeaths;
										hs.games += 1;
										hs.gpm += rGpm;
										hs.xpm += rXpm;
										hs.heroId = rHeroId;
										hs.kills += rKills;
										hs.heroDmgPercent += rPercentHeroDmg;
										if (rWon) {
											hs.won += 1;
										}
										heroTable.put(Integer.valueOf(rHeroId),
												hs);
									} else {
										// add stats to the old :)
										HeroStats hs = heroTable.get(Integer
												.valueOf(rHeroId));
										hs.assists += rAssists;
										hs.deaths += rDeaths;
										hs.games += 1;
										hs.gpm += rGpm;
										hs.xpm += rXpm;
										hs.heroId = rHeroId;
										hs.kills += rKills;
										hs.heroDmgPercent += rPercentHeroDmg;
										if (rWon) {
											hs.won += 1;
										}
									}
								}
							} catch (SQLException e1) {
								e1.printStackTrace();
							}
							try {
								rsStatsFromPlayer.close();
							} catch (SQLException e1) {
								e1.printStackTrace();
							}
							lblGamesOutput.setText(Integer.toString(gameCount));
							lblKillsOutput.setText(Integer.toString(kills));
							lblDeathsOutput.setText(Integer.toString(deaths));
							lblAssistsOutput.setText(Integer.toString(assists));

							if (gameCount > 0) {
								lblXpmOutput.setText(Integer.toString(xpm
										/ gameCount));
								lblGpmOutput.setText(Integer.toString(gpm
										/ gameCount));
							} else {
								lblXpmOutput.setText("No Matches");
								lblGpmOutput.setText("No Matfches");
							}

							if (deaths > 0) {
								lblKdOutput.setText(String.format("%f.2",
										(double) kills / deaths));
								lblKdaOutput.setText(String
										.format("%f.2",
												((double) (kills + assists) / (double) (deaths))));
							} else {
								if (kills <= 0) {
									lblKdOutput.setText("0");
								} else {
									lblKdOutput.setText(String
											.valueOf(Double.POSITIVE_INFINITY));
								}
								if (kills + assists <= 0) {
									lblKdaOutput.setText("0");
								} else {
									lblKdOutput.setText(String
											.valueOf(Double.POSITIVE_INFINITY));
								}
							}
							DefaultTableModel nPlayedHeroesTdm = new DefaultTableModel(
									new Object[][] {}, new String[] { "Icon",
											"Hero", "Games", "KD", "GPM",
											"XPM", "K/D/A", "% Win", "Wins/Looses", "% Hero Damage" }) {
								/**
												 * 
												 */
								private static final long serialVersionUID = -4955120719819835803L;
								@SuppressWarnings("rawtypes")
								Class[] columnTypes = new Class[] { Icon.class,
										String.class, Integer.class,
										Double.class, Integer.class,
										Integer.class, Double.class,
										Double.class, Integer.class,
										Integer.class, Double.class };

								@SuppressWarnings({ "unchecked", "rawtypes" })
								public Class getColumnClass(int columnIndex) {
									return columnTypes[columnIndex];
								}

								boolean[] columnEditables = new boolean[] {
										false, false, false, false, false,
										false, false, false, false };

								public boolean isCellEditable(int row,
										int column) {
									return columnEditables[column];
								}
							};
							// "Icon", "Hero", "Times played", "KD", "GPM",
							// "XPM",
							// "KDA",
							// "Wins", "Looses"
							for (HeroStats hs : heroTable.values()) {
								double kd = 0;
								double kda = 0;
								if (hs.deaths > 0) {
									kd = ((double) hs.kills / hs.deaths);
									kda = (((double) hs.kills + hs.assists) / hs.deaths);
								} else {
									if (hs.kills <= 0) {
										kd = 0;
									} else {
										kd = Double.POSITIVE_INFINITY;
									}
									if (hs.kills + hs.assists <= 0) {
										kda = 0;
									} else {
										kda = Double.POSITIVE_INFINITY;
									}
								}
								Hero hero = Hero.getHeroById(hs.heroId);
								if (hero == null) {
									System.out.println("Cound not find Hero with ID: "+hs.heroId);
								}
								ImageIcon heroIcon = null;
								if (hero != null) {
									heroIcon = new ImageIcon(
											"images\\heroes\\"
													+ hero.getName().substring(
																	14)
													+ "_sb.png");
								}
								String heroName = "Unknow ID:"+hs.heroId;
								if (hero != null) {
									heroName = hero.getLocalName();
								}
								nPlayedHeroesTdm
										.addRow(new Object[] {
												heroIcon,
												heroName,
												hs.games,
												kd,
												hs.gpm / hs.games,
												hs.xpm / hs.games,
												kda,
												((double) hs.won / hs.games) * 100,
												hs.won+"/"+(hs.games - hs.won),
												((hs.heroDmgPercent * 100) / hs.games) });
							}
							tablePlayedHeros.setModel(nPlayedHeroesTdm);
							applyPlayedTableSettings();
						}
					} else {
						System.out.println("con == null");
					}
					try {
						con.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					SwingUtilities.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							updateMatches();
							btnLoadData.setText("Load Data");
							btnLoadData.setEnabled(true);
						}
					});
					
				}
			});
			updateThread.start();
			
			break;
		case "hero changed":
		case "gametype changed":
		case "gamemode changed":
			updateMatches();
			break;
		case "load icons":
			btnLoadHeroiconsFrom.setEnabled(false);
			btnLoadHeroiconsFrom.setText("Loading please wait...");
			btnLoadHeroiconsFrom.setEnabled(false);
			if (iconLoader == null || !iconLoader.isAlive()) {
				iconLoader = new Thread(new Runnable() {
					@Override
					public void run() {
						URL website;
						ReadableByteChannel rbc;
						FileOutputStream fos;
						File iconPath = new File("images/heroes/");
						if (!iconPath.exists()) {
							iconPath.mkdirs();
						}
						for (Hero h : Hero.values()) {
							if (h != null) {
								String picname = h.getName().substring(14);
								File outFile = new File("images/heroes/"
												+ picname + "_sb.png");
								if (outFile.exists()) {
									continue;
								}
								boolean tryagain = false;
								do {
									
									try {
										website = new URL(
												"http://media.steampowered.com/apps/dota2/images/heroes/"
														+ picname + "_sb.png");
										rbc = Channels.newChannel(website.openStream());
										fos = new FileOutputStream("images/heroes/"
												+ picname + "_sb.png");
										fos.getChannel().transferFrom(rbc, 0, 1 << 24);
										fos.close();
										rbc.close();
									} catch (FileNotFoundException fnfe) {
										// fnfe.printStackTrace();
									} catch (MalformedURLException mue) {
										mue.printStackTrace();
									} catch (IOException ioe) {
										tryagain = true;
									}
								} while (tryagain);
							}
						}
						iconPath = new File("images/items/");
						if (!iconPath.exists()) {
							iconPath.mkdirs();
						}
						for (Item i : Item.values()) {
							if (i != null) {
								boolean tryagain = false;
								do {
									String picname = i.getName().substring(5);
									try {
										website = new URL(
												"http://media.steampowered.com/apps/dota2/images/items/"
														+ picname + "_lg.png");
										rbc = Channels.newChannel(website.openStream());
										fos = new FileOutputStream("images/items/"
												+ picname + "_sb.png");
										fos.getChannel().transferFrom(rbc, 0, 1 << 24);
										fos.close();
										rbc.close();
									} catch (FileNotFoundException fnfe) {
										// fnfe.printStackTrace();
									} catch (MalformedURLException mue) {
										mue.printStackTrace();
									} catch (IOException ioe) {
										tryagain = true;
									}
								} while (tryagain);
							}
						}
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								btnLoadHeroiconsFrom.setText("Load Icons from server");
								btnLoadHeroiconsFrom.setEnabled(true);
							}
						});
						
						}
				});
			}
			iconLoader.start();
			break;
		default:
			break;
		}
	}

	private final void applyMatchTableSettings() {
		tableMatchList.getColumnModel().getColumn(0).setResizable(false);
		tableMatchList.getColumnModel().getColumn(0).setPreferredWidth(59);
		tableMatchList.getColumnModel().getColumn(0).setMinWidth(59);
		tableMatchList.getColumnModel().getColumn(0).setMaxWidth(59);
		tableMatchList.getColumnModel().getColumn(1).setPreferredWidth(150);
		tableMatchList.getColumnModel().getColumn(11).setPreferredWidth(44);
		tableMatchList.getColumnModel().getColumn(11).setMinWidth(44);
		tableMatchList.getColumnModel().getColumn(11).setMaxWidth(44);
		tableMatchList.getColumnModel().getColumn(12).setPreferredWidth(44);
		tableMatchList.getColumnModel().getColumn(12).setMinWidth(44);
		tableMatchList.getColumnModel().getColumn(12).setMaxWidth(44);
		tableMatchList.getColumnModel().getColumn(13).setPreferredWidth(44);
		tableMatchList.getColumnModel().getColumn(13).setMinWidth(44);
		tableMatchList.getColumnModel().getColumn(13).setMaxWidth(44);
		tableMatchList.getColumnModel().getColumn(14).setPreferredWidth(44);
		tableMatchList.getColumnModel().getColumn(14).setMinWidth(44);
		tableMatchList.getColumnModel().getColumn(14).setMaxWidth(44);
		tableMatchList.getColumnModel().getColumn(15).setPreferredWidth(44);
		tableMatchList.getColumnModel().getColumn(15).setMinWidth(44);
		tableMatchList.getColumnModel().getColumn(15).setMaxWidth(44);
		tableMatchList.getColumnModel().getColumn(16).setPreferredWidth(44);
		tableMatchList.getColumnModel().getColumn(16).setMinWidth(44);
		tableMatchList.getColumnModel().getColumn(16).setMaxWidth(44);
	}

	private final void applyPlayedTableSettings() {
		tablePlayedHeros.getColumnModel().getColumn(0).setResizable(false);
		tablePlayedHeros.getColumnModel().getColumn(0).setPreferredWidth(59);
		tablePlayedHeros.getColumnModel().getColumn(0).setMinWidth(59);
		tablePlayedHeros.getColumnModel().getColumn(0).setMaxWidth(59);
		tablePlayedHeros.getColumnModel().getColumn(1).setPreferredWidth(150);
	}

	private void addGameToDefaultTableModel(DefaultTableModel dtm,
			ResultSet playerStatsRs, ResultSet matchStatsRs)
			throws SQLException {
		/*
		 * "Icon", "Hero", "Result", "Time played", "Duration", "G/M", "X/M",
		 * "Match ID", "Gametype", "KDA", "Slot1", "Slot2", "Slot3", "Slot4",
		 * "Slot5", "Slot6"
		 */
		/*
		 * "Icon", "Hero", "Result", "Time played", "Duration", "G/M", "X/M",
		 * "Match ID", "Gametype", "K/D/A", "Slot1", "Slot2", "Slot3", "Slot4",
		 * "Slot5", "Slot6"
		 */
		boolean matchWon = false;
		if ((matchStatsRs.getBoolean(1) && !playerStatsRs.getBoolean(3))
				|| (!matchStatsRs.getBoolean(1) && playerStatsRs.getBoolean(3))) {
			matchWon = true;
		} else {
			matchWon = false;
		}
		int duration = matchStatsRs.getInt(3);
		int dH = duration / 3600;
		duration = duration % 3600;
		int dM = duration / 60;
		duration = duration % 60;
		int dS = duration;
		long cTime = System.currentTimeMillis();
		cTime = cTime / 1000;
		long timeDiff = cTime - matchStatsRs.getInt(2);
		int tDays = (int) (timeDiff / 86400);
		timeDiff = timeDiff % 86400;
		int tHours = (int) (timeDiff / 3600);
		timeDiff = timeDiff % 3600;
		int tMins = (int) (timeDiff / 60);
		timeDiff = timeDiff % 60;
		int tSecs = (int) timeDiff;
		// 15 6
		Hero h = Hero.getHeroById(playerStatsRs.getInt(2));
		GameMode gameMode = GameMode.getGameModeFromId(matchStatsRs.getInt(4));
		String modeNameString = null;
		if (gameMode == null) {
			modeNameString = "Unknown Mode ID: "+matchStatsRs.getInt(4);
		} else {
			modeNameString = gameMode.toString();
		}
		dtm.addRow(new Object[] {
				new ImageIcon("images\\heroes\\" + h.getName().substring(14)
						+ "_sb.png"),
				h.getLocalName(),
				matchWon ? "Match Won" : "Match Lost",
				String.format("%03dd %02dh %02dm %02ds", tDays, tHours, tMins,
						tSecs),
				String.format("%02d:%02d:%02d", dH, dM, dS),
				playerStatsRs.getInt(4),
				playerStatsRs.getInt(5),
				playerStatsRs.getString(1),
				modeNameString,
				playerStatsRs.getString(6) + "/" + playerStatsRs.getString(7)
						+ "/" + playerStatsRs.getString(8),
				(playerStatsRs.getInt(15) / (double) matchStatsRs.getInt(6)) * 100,
				itemPictureHandler.getIcon(Item
						.getItemById(playerStatsRs.getInt(9)).getName()
						.substring(5)),
				itemPictureHandler.getIcon(Item
						.getItemById(playerStatsRs.getInt(10)).getName()
						.substring(5)),
				itemPictureHandler.getIcon(Item
						.getItemById(playerStatsRs.getInt(11)).getName()
						.substring(5)),
				itemPictureHandler.getIcon(Item
						.getItemById(playerStatsRs.getInt(12)).getName()
						.substring(5)),
				itemPictureHandler.getIcon(Item
						.getItemById(playerStatsRs.getInt(13)).getName()
						.substring(5)),
				itemPictureHandler.getIcon(Item
						.getItemById(playerStatsRs.getInt(14)).getName()
						.substring(5)) });
	}

	private DefaultTableModel getCleanDefaultTableModelForMatchTable() {
		return new DefaultTableModel(new Object[][] {}, new String[] { "Icon",
				"Hero", "Result", "Time played", "Duration", "G/M", "X/M",
				"Match ID", "Gametype", "K/D/A", "% Hero Damage", "Slot1",
				"Slot2", "Slot3", "Slot4", "Slot5", "Slot6" }) {
			/**
					 * 
					 */
			private static final long serialVersionUID = -6465508226105182185L;
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] { Icon.class, String.class,
					String.class, String.class, String.class, Integer.class,
					Integer.class, String.class, String.class, String.class,
					Double.class, Icon.class, Icon.class, Icon.class,
					Icon.class, Icon.class, Icon.class };

			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}

			boolean[] columnEditables = new boolean[] { true, true, true, true,
					true, true, true, true, true, true, false, true, true,
					true, true, true, true };

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		};
	}

	private Connection getDatabaseConnection() {
		Connection con = null;
		try {
			con = DriverManager.getConnection("jdbc:sqlite:database.db");
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return con;
	}

	private void updateMatches() {
		try {
			if (con == null || con.isClosed()) {
				con = getDatabaseConnection();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String steamId = (String) comboBoxSteamId32.getSelectedItem();
		int heroId = ((Hero) comboBoxHero.getSelectedItem()).getId();
		GameMode setGMode = (GameMode) comboBoxGameMode.getSelectedItem();
		GameType setGType = (GameType) comboBoxMatchType.getSelectedItem();
		// clear the table
		DefaultTableModel nMatchesDtm = getCleanDefaultTableModelForMatchTable();
		String matchesQuery = null;
		if (heroId == -1) {
			matchesQuery = "select matchId, heroId, isDire, gpm, xpm, kills, deaths, assists, item0, item1, item2, item3, item4, item5, heroDmg from PlayerStats where accountId = '"
					+ steamId + "';";
		} else {
			matchesQuery = "select matchId, heroId, isDire, gpm, xpm, kills, deaths, assists, item0, item1, item2, item3, item4, item5, heroDmg from PlayerStats where accountId = '"
					+ steamId + "' and heroId = " + heroId + ";";
		}
		Statement statPlayerStats = null;
		PreparedStatement statMatchStats = null;
		try {
			statPlayerStats = con.createStatement();
			statMatchStats = con
					.prepareStatement("select radiantWin, startTime, duration, gameMode, lobbyType, totalHeroDmg "
							+ "from Match where matchId = ?;");
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		if (statPlayerStats == null || statMatchStats == null) {
			return;
		}
		ResultSet playerStatsRs = null;
		try {
			playerStatsRs = statPlayerStats.executeQuery(matchesQuery);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		if (playerStatsRs == null) {
			return;
		}
		ResultSet matchStatsRs = null;
		try {
			while (playerStatsRs.next()) {
				String matchId = playerStatsRs.getString(1);
				statMatchStats.setString(1, matchId);
				matchStatsRs = statMatchStats.executeQuery();
				if (matchStatsRs.next()) {
					GameMode gameMode = GameMode.getGameModeFromId(matchStatsRs
							.getInt(4));
					if ((setGMode == GameMode.ALL_GAME_MODES || gameMode == setGMode)
							&& (setGType == GameType.ALL_GAMES || setGType == gameMode
									.getGameType())) {
						if ((setGType == GameType.STAT_GAME
								&& matchStatsRs.getInt(5) == 0 && gameMode
								.getGameType() != GameType.NONE_STAT_GAME)
								|| setGType == GameType.ALL_GAMES
								|| (setGType == GameType.NONE_STAT_GAME && (matchStatsRs
										.getInt(5) == 1 || gameMode
										.getGameType() == GameType.NONE_STAT_GAME))) {
							addGameToDefaultTableModel(nMatchesDtm,
									playerStatsRs, matchStatsRs);
						}
					}
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		tableMatchList.setModel(nMatchesDtm);
		applyMatchTableSettings();
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void getNewMatchesForPlayer(String steamId) throws SQLException,
			SAXException, IOException {
		Collection<String> addedIds = new HashSet<String>();
		Collection<String> addedAccounts = new HashSet<String>();
		if (con == null || con.isClosed()) {
			con = getDatabaseConnection();
		}
		PreparedStatement pStatMatch = con
				.prepareStatement("insert into Match(matchId, radiantWin,"
						+ "duration, startTime,"
						+ "towerStatusRadiant, towerStatusDire,"
						+ "barracksStatusRadiant, barracksStatusDire,"
						+ "cluster, firstBloodTime, lobbyType,"
						+ "humanPlayers, leagueid, positiveVotes,"
						+ "negativeVotes, gameMode, totalHeroDmg, totalHeroHealing, totalTowerDmg, matchSeqNum) values(?,"
						+ " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
						+ " ?, ?, ?, ?, ?, ?, ?);");
		PreparedStatement pStatPlayerStats = con
				.prepareStatement("insert into PlayerStats (matchId, item0, item1, item2,"
						+ "item3, item4, item5,"
						+ "kills, deaths, assists, leaverStatus, gold,"
						+ "lastHits, denies, gpm, xpm, goldSpent,"
						+ "heroDmg, towerDmg,"
						+ "heroHealing, lvl,"
						+ "accountId,"
						+ "isDire, playerSlot, heroId, hasAdUnit)"
						+ "values(?, ?, ?, ?, ?, ?, ?,"
						+ "?, ?, ?, ?, ?, ?, ?, ?, ?,"
						+ "?, ?, ?, ?, ?, ?, ?, ?," + "?, ?);"); // 25
		PreparedStatement statInsertAdUnit = con
				.prepareStatement("insert into "
						+ "AditionalUnit (matchId, playerSlot, unitname, item0, item1, item2, item3, item4, item5) values "
						+ "(?, ?, ?, ?, ?, ?, ?, ?, ?);");
		PreparedStatement statInsertAbilityUpgrade = con
				.prepareStatement("insert into AbilityUpgrade (matchId, playerSlot, ability, time, level) values (?, ?, ?, ?, ?);");
		PreparedStatement pStatGetMatch = con
				.prepareStatement("select matchId from Match where matchId = ?;");
		PreparedStatement pStatGetAccount = con
				.prepareStatement("select accountId, accountName from Account where accountId = ?;");
		PreparedStatement pStatInsertAccount = con
				.prepareStatement("insert into Account (accountId, accountName) values (?, ?);");
		MatchHistoryResult matches = null;
		String lastMatch = null;
		boolean getNext = true;
		int matchCount = 0;
		do {
			matches = Dota2Api.getMatchHistory(steamId, lastMatch, textFieldApiKey.getText(), saxParser);
			// 15: Cannot get match history for a user that hasn't allowed it
			if (!(matches.status == 15))
			for (int matchCounter = 0; matchCounter < matches.size(); matchCounter++) {
				System.out.println("for loop!");
				Match m = matches.get(matchCounter);
				// check if the match is already in the
				// database
				// Thread.sleep(1000);
				pStatGetMatch.setString(1, m.match_id);
				ResultSet rs = pStatGetMatch.executeQuery();
				if (!rs.next() && !addedIds.contains(m.match_id)) {
					addedIds.add(m.match_id);
					matchCount++;
					MatchDetails md = Dota2Api.getMatchDetails(m.match_id, textFieldApiKey.getText(), saxParser);

					pStatMatch.setString(1, md.matchId);
					pStatMatch.setBoolean(2, md.radiantWin);
					pStatMatch.setString(3, md.duration.toString());
					pStatMatch.setString(4, md.startTime.toString());
					pStatMatch.setString(5, md.towerStatusRadiant.toString());
					pStatMatch.setString(6, md.towerStatusDire.toString());
					pStatMatch.setString(7, md.barracksStatusRadiant.toString());
					pStatMatch.setString(8, md.barracksStatusDire.toString());
					pStatMatch.setString(9, md.cluster);
					pStatMatch.setString(10, md.firstBloodTime.toString());
					pStatMatch.setString(11, md.lobbyType.toString());
					pStatMatch.setString(12, md.humanPlayers.toString());
					pStatMatch.setString(13, md.leagueid.toString());
					pStatMatch.setString(14, md.positiveVotes.toString());
					pStatMatch.setString(15, md.negativeVotes.toString());
					pStatMatch.setInt(16, md.gameMode);

					// add up herodmg of all players
					BigInteger totalHeroDmg = new BigInteger("0");
					BigInteger totalTowerDmg = new BigInteger("0");
					BigInteger totalHeroHealing = new BigInteger("0");
					for (PlayerStats ps : md.players) {
						totalHeroDmg  = totalHeroDmg.add(ps.heroDamage);
						totalTowerDmg = totalTowerDmg.add(ps.towerDamage);
						totalHeroHealing = ps.heroHealing;
					}
					pStatMatch.setString(17, totalHeroDmg.toString());
					pStatMatch.setString(18, totalHeroHealing.toString());
					pStatMatch.setString(19, totalTowerDmg.toString());
					pStatMatch.setString(20, md.matchSeqNumber);
					pStatMatch.addBatch();
					for (PlayerStats ps : md.players) {
						pStatGetAccount.setString(1, ps.player.accountId);
						ResultSet accountRS = pStatGetAccount.executeQuery();
						if (!accountRS.next() && !addedAccounts.contains(ps.player.accountId)) {
							String[] playerdata = Dota2Api
									.getPlayerSummaries(ps.player.accountId, textFieldApiKey.getText(), saxParser);
							pStatInsertAccount
									.setString(1, ps.player.accountId);
							try {
								pStatInsertAccount.setString(2, playerdata[0]);
							} catch (SQLException e) {
								e.printStackTrace();
							}
							addedAccounts.add(ps.player.accountId);
							pStatInsertAccount.addBatch();
						}
						pStatPlayerStats.setString(1, md.matchId);
						pStatPlayerStats.setInt(2, ps.items[0]);
						pStatPlayerStats.setInt(3, ps.items[1]);
						pStatPlayerStats.setInt(4, ps.items[2]);
						pStatPlayerStats.setInt(5, ps.items[3]);
						pStatPlayerStats.setInt(6, ps.items[4]);
						pStatPlayerStats.setInt(7, ps.items[5]);
						pStatPlayerStats.setString(8, ps.kills.toString());
						pStatPlayerStats.setString(9, ps.deaths.toString());
						pStatPlayerStats.setString(10, ps.assists.toString());
						pStatPlayerStats.setString(11, ps.leaverStatus.toString());
						pStatPlayerStats.setString(12, ps.gold.toString());
						pStatPlayerStats.setString(13, ps.lastHits.toString());
						pStatPlayerStats.setString(14, ps.denies.toString());
						pStatPlayerStats.setString(15, ps.gpm.toString());
						pStatPlayerStats.setString(16, ps.xpm.toString());
						pStatPlayerStats.setString(17, ps.goldSpent.toString());
						pStatPlayerStats.setString(18, ps.heroDamage.toString());
						pStatPlayerStats.setString(19, ps.towerDamage.toString());
						pStatPlayerStats.setString(20, ps.heroHealing.toString());
						pStatPlayerStats.setString(21, ps.lvl.toString());
						pStatPlayerStats.setString(22, ps.player.accountId);
						pStatPlayerStats.setBoolean(23, ps.player.isDire);
						pStatPlayerStats.setInt(24, ps.player.playerSlot);
						pStatPlayerStats.setInt(25, ps.player.heroId);
						pStatPlayerStats.setInt(26,
								ps.aditionalUnites.size() > 0 ? 1 : 0);
						pStatPlayerStats.addBatch();
						for (AditionalUnit au : ps.aditionalUnites) {
							// matchId, playerSlot, unitname, item0, item1,
							// item2, item3, item4, item5
							statInsertAdUnit.setString(1, md.matchId);
							statInsertAdUnit.setInt(2, ps.player.playerSlot);
							statInsertAdUnit.setString(3, au.unitname);
							for (int ic = 0; ic < 6; ic++) {
								statInsertAdUnit.setInt(4 + ic, au.item[ic]);
							}
							statInsertAdUnit.addBatch();
						}

						for (AbilityUpgrade abu : ps.abilityUpgrades) {
							// matchId, playerSlot, ability, time, level
							statInsertAbilityUpgrade.setString(1, md.matchId);
							statInsertAbilityUpgrade.setInt(2,
									ps.player.playerSlot);
							statInsertAbilityUpgrade.setInt(3, abu.ability);
							statInsertAbilityUpgrade.setInt(4, abu.time);
							statInsertAbilityUpgrade.setInt(5, abu.level);
							statInsertAbilityUpgrade.addBatch();
						}
					}
				} else {
					System.out.println("This Match was allready in database or is in queue to be added!");
					rs.close();
					// check if we got only one result
					// then it is his last match
					if (matches.size() < 2) {
						System.out.println("Only one result!");
						getNext = false;
					} else if (matchCounter > 0) {
						System.out.println("MatchCounter > 0");
						getNext = false;
						break;
					}
				}
				class BUpdateRunnable implements Runnable {
					int mCount;
					public BUpdateRunnable(int mCount) {
						this.mCount = mCount;
					}
					
					@Override
					public void run() {
						btnLoadData.setText("Loading...got: " + mCount);
					}
					
				}
				SwingUtilities.invokeLater(new BUpdateRunnable(matchCount));
				
			}
			System.out.println("For loop over");
			if (matches.size() > 0) {
				lastMatch = matches.get(matches.size() - 1).match_id;
			} else {
				getNext = false;
			}
			con.setAutoCommit(false);
			pStatMatch.executeBatch();
			pStatPlayerStats.executeBatch();
			statInsertAbilityUpgrade.executeBatch();
			statInsertAdUnit.executeBatch();
			pStatInsertAccount.executeBatch();
			con.setAutoCommit(true);
			System.out.println("Changes commited");
		} while (getNext);
		pStatGetMatch.close();
		pStatMatch.close();
		pStatPlayerStats.close();
		con.close();
	}

	private void createDatabaseIfNotExists() {
		File db = new File("database.db");
		if (!db.exists()) {
			try {
				Connection con = DriverManager.getConnection("jdbc:sqlite:database.db");
				Statement stat = con.createStatement();
				stat.execute("create table Match (matchId INTEGER PRIMARY KEY,"
						+ "radiantWin INTEGER,"
						+ "duration INTEGER, startTime INTEGER,"
						+ "towerStatusRadiant INTEGER, towerStatusDire INTEGER,"
						+ "barracksStatusRadiant INTEGER, barracksStatusDire INTEGER,"
						+ "cluster TEXT, firstBloodTime INTEGER, lobbyType INTEGER,"
						+ "humanPlayers INTEGER, leagueid INTEGER, positiveVotes INTEGER,"
						+ "negativeVotes INTEGER, gameMode INTEGER, totalHeroDmg INTEGER,"
						+ " totalHeroHealing INTEGER, totalTowerDmg INTEGER,"
						+ " matchSeqNum TEXT);");
				stat.execute("create table PlayerStats (matchId INTEGER, item0 INTEGER,"
						+ "item1 INTEGER, item2 INTEGER,"
						+ "item3 INTEGER, item4 INTEGER, item5 INTEGER,"
						+ "kills INTEGER, deaths INTEGER,"
						+ "assists INTEGER,"
						+ "leaverStatus INTEGER, gold INTEGER,"
						+ "lastHits INTEGER, denies INTEGER,"
						+ "gpm INTEGER, xpm INTEGER,"
						+ "goldSpent INTEGER,"
						+ "heroDmg INTEGER, towerDmg INTEGER,"
						+ " heroHealing INTEGER, lvl INTEGER,"
						+ " accountId TEXT, isDire INTEGER,"
						+ "playerSlot INTEGER, heroId INTEGER, hasAdUnit INTEGER, PRIMARY KEY (matchId, playerSlot));");
				stat.execute("create table AditionalUnit (matchId TEXT,"
						+ " unitname TXT, playerSlot INTEGER, "
						+ "item0 INTEGER, item1 INTEGER, item2 INTEGER, item3 INTEGER,"
						+ "item4 INTEGER, item5 INTEGER, PRIMARY KEY (matchId, playerSlot, unitname));");
				stat.execute("create table AbilityUpgrade"
						+ " (matchId TEXT, playerSlot INTEGER,"
						+ " ability INTEGER,"
						+ " time INTEGER, level INTEGER);");
				stat.execute("create table Account (accountId TEXT, accountName TEXT, PRIMARY KEY(accountId));");
				stat.execute("CREATE UNIQUE INDEX [IDX_ACCOUNT_ACCOUNTID] ON [Account]("
						+"[accountId]  DESC"
						+")");
				stat.execute("CREATE UNIQUE INDEX [IDX_MATCH_MATCHID] ON [Match]("
						+"[matchId]  DESC"
						+")");
				stat.execute("CREATE INDEX [IDX_PLAYERSTATS_MATCHIDPLAYERSLOTACCOUNTID] ON [PlayerStats]("
						+"[matchId]  DESC,"
						+"[playerSlot]  DESC,"
						+"[accountId]  DESC"
						+")");
				stat.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
