package general;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JFrame;
import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.xml.parsers.SAXParser;

import tk.speedprog.dota2.webapi.MatchDetails;
import tk.speedprog.dota2.webapi.PlayerStats;

import java.awt.Dimension;

public class MatchWindow {
	public static final Color[] slotColor = { new Color(0x182D55),
			new Color(0x224736), new Color(0x560956), new Color(0x5D5D0E),
			new Color(0x522C0C), new Color(0xEA7DB3), new Color(0x7C8B39),
			new Color(0x55B3CC), new Color(0x065A1B), new Color(0x553A09) };
	public JFrame frame;
	private final JPanel panelDire = new JPanel();
	private JTable tableRadiant;
	private JTable tableDire;
	private JLabel lblRadiant;
	private JLabel lblDire;
	private DamageDiagramPanel panelDiagrams;
	private JSplitPane splitPane;
	private JPanel panelTables;
	private ItemPictureHandler iph;

	/**
	 * Create the application.
	 */
	public MatchWindow(MatchDetails md, String apiKey, SAXParser saxParser) {
		iph = new ItemPictureHandler();
		initialize(md, apiKey, saxParser);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings("serial")
	private void initialize(MatchDetails md, String apiKey, SAXParser saxParser) {
		frame = new JFrame();
		frame.setBounds(100, 100, 900, 524);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		panelDiagrams = new DamageDiagramPanel(md);
		panelDiagrams.setPreferredSize(new Dimension(50, 18));
		panelDiagrams.setMinimumSize(new Dimension(100, 802));

		splitPane = new JSplitPane();
		splitPane.setResizeWeight(1.0);
		splitPane.setDividerSize(10);
		splitPane.setOneTouchExpandable(true);
		splitPane.setContinuousLayout(true);
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		panelTables = new JPanel();
		panelTables.setPreferredSize(new Dimension(800, 10));
		splitPane.setLeftComponent(panelTables);
		panelTables.setLayout(new BoxLayout(panelTables, BoxLayout.Y_AXIS));
		panelDire.setPreferredSize(new Dimension(800, 10));
		panelTables.add(panelDire);
		panelDire.setLayout(new BoxLayout(panelDire, BoxLayout.Y_AXIS));

		lblDire = new JLabel("Dire:");
		panelDire.add(lblDire);

		JScrollPane scrollPaneDire = new JScrollPane();
		panelDire.add(scrollPaneDire);

		tableDire = new JTable() {
			private static final long serialVersionUID = -5352851934256186192L;

			public Component prepareRenderer(TableCellRenderer renderer,
					int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				Color color = MatchWindow.slotColor[5 + row];
				c.setBackground(color);
				return c;
			}
		};
		tableDire.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		tableDire.setForeground(Color.WHITE);
		tableDire.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"", "Player", "Hero", "Level", "K/D/A", "Gold", "CK/CD", "XP/M", "G/M", "Item1", "Item2", "Item3", "Item4", "Item5", "Item6"
			}
		) {
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] {
					ImageIcon.class, String.class, ImageIcon.class, Integer.class, String.class, Integer.class, String.class, Integer.class, Integer.class, ImageIcon.class, ImageIcon.class, ImageIcon.class, ImageIcon.class, ImageIcon.class, ImageIcon.class
			};
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			boolean[] columnEditables = new boolean[] {
				false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		tableDire.getColumnModel().getColumn(0).setResizable(false);
		tableDire.getColumnModel().getColumn(0).setPreferredWidth(33);
		tableDire.getColumnModel().getColumn(0).setMinWidth(33);
		tableDire.getColumnModel().getColumn(0).setMaxWidth(33);
		tableDire.getColumnModel().getColumn(1).setPreferredWidth(150);
		tableDire.getColumnModel().getColumn(2).setResizable(false);
		tableDire.getColumnModel().getColumn(2).setPreferredWidth(59);
		tableDire.getColumnModel().getColumn(2).setMinWidth(59);
		tableDire.getColumnModel().getColumn(2).setMaxWidth(59);
		tableDire.getColumnModel().getColumn(tableDire.getColumnModel().getColumnCount()-6).setResizable(false);
		tableDire.getColumnModel().getColumn(tableDire.getColumnModel().getColumnCount()-6).setPreferredWidth(44);
		tableDire.getColumnModel().getColumn(tableDire.getColumnModel().getColumnCount()-6).setMinWidth(44);
		tableDire.getColumnModel().getColumn(tableDire.getColumnModel().getColumnCount()-6).setMaxWidth(44);
		tableDire.getColumnModel().getColumn(tableDire.getColumnModel().getColumnCount()-5).setResizable(false);
		tableDire.getColumnModel().getColumn(tableDire.getColumnModel().getColumnCount()-5).setPreferredWidth(44);
		tableDire.getColumnModel().getColumn(tableDire.getColumnModel().getColumnCount()-5).setMinWidth(44);
		tableDire.getColumnModel().getColumn(tableDire.getColumnModel().getColumnCount()-5).setMaxWidth(44);
		tableDire.getColumnModel().getColumn(tableDire.getColumnModel().getColumnCount()-4).setResizable(false);
		tableDire.getColumnModel().getColumn(tableDire.getColumnModel().getColumnCount()-4).setPreferredWidth(44);
		tableDire.getColumnModel().getColumn(tableDire.getColumnModel().getColumnCount()-4).setMinWidth(44);
		tableDire.getColumnModel().getColumn(tableDire.getColumnModel().getColumnCount()-4).setMaxWidth(44);
		tableDire.getColumnModel().getColumn(tableDire.getColumnModel().getColumnCount()-3).setResizable(false);
		tableDire.getColumnModel().getColumn(tableDire.getColumnModel().getColumnCount()-3).setPreferredWidth(44);
		tableDire.getColumnModel().getColumn(tableDire.getColumnModel().getColumnCount()-3).setMinWidth(44);
		tableDire.getColumnModel().getColumn(tableDire.getColumnModel().getColumnCount()-3).setMaxWidth(44);
		tableDire.getColumnModel().getColumn(tableDire.getColumnModel().getColumnCount()-2).setResizable(false);
		tableDire.getColumnModel().getColumn(tableDire.getColumnModel().getColumnCount()-2).setPreferredWidth(44);
		tableDire.getColumnModel().getColumn(tableDire.getColumnModel().getColumnCount()-2).setMinWidth(44);
		tableDire.getColumnModel().getColumn(tableDire.getColumnModel().getColumnCount()-2).setMaxWidth(44);
		tableDire.getColumnModel().getColumn(tableDire.getColumnModel().getColumnCount()-1).setResizable(false);
		tableDire.getColumnModel().getColumn(tableDire.getColumnModel().getColumnCount()-1).setPreferredWidth(44);
		tableDire.getColumnModel().getColumn(tableDire.getColumnModel().getColumnCount()-1).setMinWidth(44);
		tableDire.getColumnModel().getColumn(tableDire.getColumnModel().getColumnCount()-1).setMaxWidth(44);
		scrollPaneDire.setViewportView(tableDire);
		tableDire.getTableHeader().setReorderingAllowed(false);
		tableDire.setRowHeight(33);

		JPanel panelRadiant = new JPanel();
		panelRadiant.setPreferredSize(new Dimension(800, 10));
		panelTables.add(panelRadiant);
		panelRadiant.setLayout(new BoxLayout(panelRadiant, BoxLayout.Y_AXIS));

		lblRadiant = new JLabel("Radiant:");
		panelRadiant.add(lblRadiant);

		JScrollPane scrollPaneRadiant = new JScrollPane();
		panelRadiant.add(scrollPaneRadiant);

		tableRadiant = new JTable() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -726995408962357695L;

			public Component prepareRenderer(TableCellRenderer renderer,
					int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				Color color = MatchWindow.slotColor[row];
				c.setBackground(color);
				return c;
			}
		};
		tableRadiant.getTableHeader().setReorderingAllowed(false);
		tableRadiant.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		tableRadiant.setForeground(Color.WHITE);
		tableRadiant.setFillsViewportHeight(true);
		tableRadiant.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"", "Player", "Hero", "Level", "K/D/A", "Gold", "CK/CD", "XP/M", "G/M", "Item1", "Item2", "Item3", "Item4", "Item5", "Item6"
				}
			) {
				@SuppressWarnings("rawtypes")
				Class[] columnTypes = new Class[] {
					ImageIcon.class, String.class, ImageIcon.class, Integer.class, String.class, Integer.class, String.class, Integer.class, Integer.class, ImageIcon.class, ImageIcon.class, ImageIcon.class, ImageIcon.class, ImageIcon.class, ImageIcon.class
				};
				@SuppressWarnings({ "unchecked", "rawtypes" })
				public Class getColumnClass(int columnIndex) {
					return columnTypes[columnIndex];
				}
				boolean[] columnEditables = new boolean[] {
					false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
				};
				public boolean isCellEditable(int row, int column) {
					return columnEditables[column];
				}
			});
		tableRadiant.getColumnModel().getColumn(0).setResizable(false);
		tableRadiant.getColumnModel().getColumn(0).setPreferredWidth(33);
		tableRadiant.getColumnModel().getColumn(0).setMinWidth(33);
		tableRadiant.getColumnModel().getColumn(0).setMaxWidth(33);
		tableRadiant.getColumnModel().getColumn(1).setPreferredWidth(150);
		tableRadiant.getColumnModel().getColumn(2).setResizable(false);
		tableRadiant.getColumnModel().getColumn(2).setPreferredWidth(59);
		tableRadiant.getColumnModel().getColumn(2).setMinWidth(59);
		tableRadiant.getColumnModel().getColumn(2).setMaxWidth(59);
		tableRadiant.getColumnModel().getColumn(tableRadiant.getColumnModel().getColumnCount()-6).setResizable(false);
		tableRadiant.getColumnModel().getColumn(tableRadiant.getColumnModel().getColumnCount()-6).setPreferredWidth(44);
		tableRadiant.getColumnModel().getColumn(tableRadiant.getColumnModel().getColumnCount()-6).setMinWidth(44);
		tableRadiant.getColumnModel().getColumn(tableRadiant.getColumnModel().getColumnCount()-6).setMaxWidth(44);
		tableRadiant.getColumnModel().getColumn(tableRadiant.getColumnModel().getColumnCount()-5).setResizable(false);
		tableRadiant.getColumnModel().getColumn(tableRadiant.getColumnModel().getColumnCount()-5).setPreferredWidth(44);
		tableRadiant.getColumnModel().getColumn(tableRadiant.getColumnModel().getColumnCount()-5).setMinWidth(44);
		tableRadiant.getColumnModel().getColumn(tableRadiant.getColumnModel().getColumnCount()-5).setMaxWidth(44);
		tableRadiant.getColumnModel().getColumn(tableRadiant.getColumnModel().getColumnCount()-4).setResizable(false);
		tableRadiant.getColumnModel().getColumn(tableRadiant.getColumnModel().getColumnCount()-4).setPreferredWidth(44);
		tableRadiant.getColumnModel().getColumn(tableRadiant.getColumnModel().getColumnCount()-4).setMinWidth(44);
		tableRadiant.getColumnModel().getColumn(tableRadiant.getColumnModel().getColumnCount()-4).setMaxWidth(44);
		tableRadiant.getColumnModel().getColumn(tableRadiant.getColumnModel().getColumnCount()-3).setResizable(false);
		tableRadiant.getColumnModel().getColumn(tableRadiant.getColumnModel().getColumnCount()-3).setPreferredWidth(44);
		tableRadiant.getColumnModel().getColumn(tableRadiant.getColumnModel().getColumnCount()-3).setMinWidth(44);
		tableRadiant.getColumnModel().getColumn(tableRadiant.getColumnModel().getColumnCount()-3).setMaxWidth(44);
		tableRadiant.getColumnModel().getColumn(tableRadiant.getColumnModel().getColumnCount()-2).setResizable(false);
		tableRadiant.getColumnModel().getColumn(tableRadiant.getColumnModel().getColumnCount()-2).setPreferredWidth(44);
		tableRadiant.getColumnModel().getColumn(tableRadiant.getColumnModel().getColumnCount()-2).setMinWidth(44);
		tableRadiant.getColumnModel().getColumn(tableRadiant.getColumnModel().getColumnCount()-2).setMaxWidth(44);
		tableRadiant.getColumnModel().getColumn(tableRadiant.getColumnModel().getColumnCount()-1).setPreferredWidth(44);
		tableRadiant.getColumnModel().getColumn(tableRadiant.getColumnModel().getColumnCount()-1).setMinWidth(44);
		tableRadiant.getColumnModel().getColumn(tableRadiant.getColumnModel().getColumnCount()-1).setMaxWidth(44);
		tableRadiant.setRowHeight(33);
		scrollPaneRadiant.setViewportView(tableRadiant);
		splitPane.setRightComponent(panelDiagrams);
		setTableData(md, apiKey, saxParser);
	}

	private void setTableData(MatchDetails md, String apiKey, SAXParser saxParser) {
		// "", "Player", "Hero", "Level", "K/D/A", "Gold", "CK/CD", "XP/M", "G/M", "Item1", "Item2", "Item3", "Item4", "Item5", "Item6"
		PlayerStats[] players = new PlayerStats[10];
		for (PlayerStats ps : md.players) {
			players[ps.player.playerSlot] = ps;
		}

		DefaultTableModel dtmDire = (DefaultTableModel) tableDire.getModel();
		DefaultTableModel dtmRadiant = (DefaultTableModel) tableRadiant
				.getModel();
		for (int c = 0; c < 5; c++) {
			PlayerStats p = players[c];
			if (p != null) {
				dtmRadiant.addRow(new Object[] {
						MainWindow.avatarHandler.getAvatarForSteamId32(p.player.accountId, 33, 33, apiKey, saxParser),
						p.player.name,
						new ImageIcon("images\\heroes\\"
								+ Hero.getHeroById(p.player.heroId).getName()
										.substring(14) + "_sb.png"),
						p.lvl,
						p.kills + "/" + p.deaths + "/" + p.assists,
						p.gold,
						p.lastHits+"/"+p.denies,
						p.xpm,
						p.gpm,
						iph.getIcon(Item.getItemById(p.items[0]).getName()
								.substring(5)),
						iph.getIcon(Item.getItemById(p.items[1]).getName()
								.substring(5)),
						iph.getIcon(Item.getItemById(p.items[2]).getName()
								.substring(5)),
						iph.getIcon(Item.getItemById(p.items[3]).getName()
								.substring(5)),
						iph.getIcon(Item.getItemById(p.items[4]).getName()
								.substring(5)),
						iph.getIcon(Item.getItemById(p.items[5]).getName()
								.substring(5)) });
			} else {
				dtmRadiant.addRow(new Object[] { "-", null, 0, null, 0, 0, 0,
						0, 0, null, null, null, null, null, null });
			}
		}
		for (int c = 5; c < 10; c++) {
			PlayerStats p = players[c];
			if (p != null) {
				dtmDire.addRow(new Object[] {
						MainWindow.avatarHandler.getAvatarForSteamId32(p.player.accountId, 33, 33, apiKey, saxParser),
						p.player.name,
						new ImageIcon("images\\heroes\\"
								+ Hero.getHeroById(p.player.heroId).getName()
										.substring(14) + "_sb.png"),
						p.lvl,
						p.kills + "/" + p.deaths + "/" + p.assists,
						p.gold,
						p.lastHits+"/"+p.denies,
						p.xpm,
						p.gpm,
						iph.getIcon(Item.getItemById(p.items[0]).getName()
								.substring(5)),
						iph.getIcon(Item.getItemById(p.items[1]).getName()
								.substring(5)),
						iph.getIcon(Item.getItemById(p.items[2]).getName()
								.substring(5)),
						iph.getIcon(Item.getItemById(p.items[3]).getName()
								.substring(5)),
						iph.getIcon(Item.getItemById(p.items[4]).getName()
								.substring(5)),
						iph.getIcon(Item.getItemById(p.items[5]).getName()
								.substring(5)) });
			} else {
				dtmDire.addRow(new Object[] { null, "-", null, 0, null, 0, 0, 0,
						0, null, null, null, null, null, null });
			}
		}
	}
}
