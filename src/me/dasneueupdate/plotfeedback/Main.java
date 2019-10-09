package me.dasneueupdate.plotfeedback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.object.PlotArea;
import me.dasneueupdate.plotfeedback.utils.FileUtils;

public class Main extends JavaPlugin implements Listener {
	private HashMap<Integer, GItem> items = new HashMap<Integer, GItem>();
	private ArrayList<String> bewertung = new ArrayList<String>();
	private String[] signlines;
	private int backgroundid = 351;
	private int backgrounddurability = 10;
	private String noplotfound = "§cDu befindest dich auf keinem Plot!";
	private String guititle = "§aBewerten";
	private int seletectedpointdurability = 10;
	private int unseletectedpointdurability = 8;
	private String selectedcolor = "a";
	private String unselectedcolor = "7";
	private int unseletectedpointid;
	private int seletectedpointid;
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		try {
			File jar = getJAR().getParentFile();
			File folder = new File(jar.getAbsoluteFile() + File.separator + "PlotFeedback");
			File guiconfigfile = new File(jar.getAbsoluteFile() + File.separator + "PlotFeedback" + File.separator + "guiconfig.txt");
			File feedbackdesignconfigfile = new File(jar.getAbsoluteFile() + File.separator + "PlotFeedback" + File.separator + "feedbackdesignconfig.txt");
			File signlinesfile = new File(jar.getAbsoluteFile() + File.separator + "PlotFeedback" + File.separator + "signlines.txt");
			if (!folder.exists()) folder.mkdirs();
			if (!guiconfigfile.exists()) {
				guiconfigfile.createNewFile();
				writedefaultdatatofile(guiconfigfile, "/strings.txt");
			}
			if (!feedbackdesignconfigfile.exists()) {
				feedbackdesignconfigfile.createNewFile();
				writedefaultdatatofile(feedbackdesignconfigfile, "/config.txt");
			}
			if (!signlinesfile.exists()) {
				signlinesfile.createNewFile();
				writedefaultdatatofile(signlinesfile, "/signlines.txt");
			}
			saveDefaultConfig();
			loadconfigyml();
			String[] lines = FileUtils.readExternalLines(guiconfigfile);
			for (String line:lines) {
				if (line.startsWith("//")) continue;
				String[] lin = line.split(",");
				String name = lin[0];
				String lore = lin[1];
				int id = Integer.parseInt(lin[2]);
				int amount = Integer.parseInt(lin[3]);
				short damage = Short.parseShort(lin[4]);
				int slot = XYtoSlot(Integer.parseInt(lin[5]),Integer.parseInt(lin[6]));
				ItemType type = ItemType.valueOf(lin[7]);
				GItem item = new GItem(name.replaceAll("&", "§"),lore.replaceAll("&", "§"), id, damage,amount, slot,type);
				items.put(slot, item);
			}
			String[] lines1 = FileUtils.readExternalLines(feedbackdesignconfigfile);
			for (String line:lines1) {
				if (line.startsWith("//")) continue;
				bewertung.add(line);
			}
			signlines = FileUtils.readExternalLines(signlinesfile);
			for (int i = 0;i < signlines.length;i++) {
				signlines[i] = signlines[i].replaceAll("&", "§");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public File getJAR() throws URISyntaxException {
		return new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
	}
	public void writedefaultdatatofile(File file,String internalpath) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(file);
		String[] data = FileUtils.readInternalLines(internalpath);
		for (String d:data) {
			writer.write(d + "\n");
		}
		writer.flush();
		writer.close();
	}
	public void loadconfigyml() {
		FileConfiguration cfg = getConfig();
		int backgroundid = cfg.getInt("backgroundid");
		int backgrounddurability = cfg.getInt("backgrounddurability");
		int seletectedpointid = cfg.getInt("seletectedpointid");
		int unseletectedpointid = cfg.getInt("unseletectedpointid");
		String plotnotfound = cfg.getString("noplotfound");
		String guititle = cfg.getString("guititle");
		int seletectedpointdurability = cfg.getInt("seletectedpointdurability");
		int unseletectedpointdurability = cfg.getInt("unseletectedpointdurability");
		String selectedcolor = cfg.getString("selectedcolor");
		String unselectedcolor = cfg.getString("unselectedcolor");
		this.backgroundid = backgroundid;
		this.backgrounddurability = backgrounddurability;
		this.seletectedpointdurability = seletectedpointdurability;
		this.unseletectedpointdurability = unseletectedpointdurability;
		this.seletectedpointid = seletectedpointid;
		this.unseletectedpointid = unseletectedpointid;
		if (plotnotfound != null && plotnotfound.length() > 0) this.noplotfound = plotnotfound.replaceAll("&", "§");
		if (guititle != null && guititle.length() > 0) 
			this.guititle = 
			guititle.replaceAll("&", "§");
		if (selectedcolor != null && selectedcolor.length() > 0) this.selectedcolor = selectedcolor;
		if (unselectedcolor != null && unselectedcolor.length() > 0) this.unselectedcolor = unselectedcolor;
	}
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getView().getTitle().equals(guititle)) {
			e.setCancelled(true);
			ItemStack is = e.getCurrentItem();
			GItem item = items.get(e.getSlot());
			Inventory inv = e.getInventory();
			if (item == null || inv == null || is == null) return;
			if (item.getType() == ItemType.CONFIRM) {
				int ipoints = 0;
				for (int i = 0;i < 54;i++) {
					ItemStack citem = inv.getItem(i);
					if (citem.getTypeId() == seletectedpointid && citem.getDurability() == seletectedpointdurability) {
						int points = citem.getAmount();
						ipoints += points;
					}
				}
//				e.getWhoClicked().sendMessage("§aPoints: " + ipoints);
				org.bukkit.Location playerloc = e.getWhoClicked().getLocation();
				com.intellectualcrafters.plot.object.Plot plot = getPlot(playerloc);
				if (plot == null) {
					e.getWhoClicked().sendMessage(noplotfound);
					return;
				}else {
					com.intellectualcrafters.plot.object.Location loc = plot.getDefaultHome();
					placeFeedback(loc, ipoints, e.getWhoClicked().getName());
				}
				e.getWhoClicked().closeInventory();
			}
			if (item.getType() == ItemType.CANCEL) {
				e.getWhoClicked().closeInventory();
			}
			if (item.getType() == ItemType.POINT) {
				if (is.getDurability() == unseletectedpointdurability) {
					is.setDurability((short) seletectedpointdurability);
					is.setTypeId(seletectedpointid);
					ItemMeta meta = is.getItemMeta();
					meta.setDisplayName(is.getItemMeta().getDisplayName().replaceAll(unselectedcolor, selectedcolor));
					is.setItemMeta(meta);
				}else {
					return;
				}
				int start = getlast(e.getSlot(), 9);
				for (int i = 0;i < 9;i++) {
					int slot = start + i;
					if (slot != e.getSlot()) {
						ItemStack citem = inv.getItem(slot);
						if (citem.getTypeId() != is.getTypeId()) continue;
						citem.setDurability((short) unseletectedpointdurability);
						citem.setTypeId(unseletectedpointid);
						ItemMeta meta = citem.getItemMeta();
						meta.setDisplayName(citem.getItemMeta().getDisplayName().replaceAll(selectedcolor, unselectedcolor));
						citem.setItemMeta(meta);
					}
				}
			}
		}
	}
	public void placeFeedback(com.intellectualcrafters.plot.object.Location loc,int points,String name) {
		org.bukkit.Location loc1 = new org.bukkit.Location(Bukkit.getWorld(loc.getWorld()), loc.getX() + 2, loc.getY() - 1, loc.getZ() - 1);
		org.bukkit.Location loc2 = new org.bukkit.Location(Bukkit.getWorld(loc.getWorld()), loc.getX() + 3, loc.getY() - 1, loc.getZ() - 1);
		org.bukkit.Location loc3 = new org.bukkit.Location(Bukkit.getWorld(loc.getWorld()), loc.getX() + 4, loc.getY() - 1, loc.getZ() - 1);
		org.bukkit.Location signloc = new org.bukkit.Location(Bukkit.getWorld(loc.getWorld()), loc.getX() + 2, loc.getY() - 1, loc.getZ() - 2);
		for (String b:bewertung) {
			String[] lines = b.split(",");
			String[] between = lines[0].split("-");
			int i1 = Integer.parseInt(between[0]);
			int i2 = Integer.parseInt(between[1]);
			if (points >= i1 && points <= i2) {
				int idblock1 = Integer.parseInt(lines[1]);
				int idblock2 = Integer.parseInt(lines[2]);
				int idblock3 = Integer.parseInt(lines[3]);
				loc1.getBlock().setTypeId(idblock3);
				loc2.getBlock().setTypeId(idblock2);
				loc3.getBlock().setTypeId(idblock1);
				signloc.getBlock().setType(Material.WALL_SIGN);
				Sign sign = (Sign)signloc.getBlock().getState();
//				sign.setLine(0, "test");
				for (int i= 0;i < signlines.length;i++) {
					sign.setLine(i, signlines[i].replaceAll("%player", name).replaceAll("%points", String.valueOf(points)));
				}
				sign.update();
				break;
			}
		}
	}
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		if (e.getMessage().equalsIgnoreCase("/p bewerten")) {
			if (p.hasPermission("plotfeedback.use")) {
				e.setCancelled(true);
				org.bukkit.Location playerloc = p.getLocation();
				com.intellectualcrafters.plot.object.Plot plot = getPlot(playerloc);
				if (plot == null) {
					p.sendMessage(noplotfound);
					return;
				}
				Inventory inv = Bukkit.createInventory(null, 54,guititle);
				for (int i = 0;i < 54;i++) {
					ItemStack item = createItem(new ItemStack(backgroundid), "§0", new String[0]);
					item.setDurability((short) backgrounddurability);
					inv.setItem(i, item);
				}
				for (GItem gitem:items.values()) {
					ItemStack item = new ItemStack(gitem.getId());
					String[] lore = gitem.getLore().split(":");
					item = createItem(item, gitem.getName(), lore);
					item.setDurability(gitem.getDamage());
					item.setAmount(gitem.getAmount());
					inv.setItem(gitem.getSlot(), item);
				}
				p.openInventory(inv);
			}
		}
		
		
		if (e.getMessage().equalsIgnoreCase("/p bewertendebug")) {
			if (p.hasPermission("plotfeedback.use")) {
				e.setCancelled(true);
				org.bukkit.Location playerloc = p.getLocation();
				com.intellectualcrafters.plot.object.Plot plot = getPlot(playerloc);
				if (plot == null) {
					p.sendMessage(noplotfound);
					return;
				}
				Inventory inv = Bukkit.createInventory(null, 54,guititle);
				for (int i = 0;i < 54;i++) {
					ItemStack item = createItem(new ItemStack(160), "§0", new String[0]);
					item.setDurability((short) 7);
					inv.setItem(i, item);
				}
				for (GItem gitem:items.values()) {
					ItemStack item = new ItemStack(gitem.getId());
					int[] xy = SlottoXY(gitem.getSlot());
					int x = xy[0];
					int y = xy[1];
					String[] lore = gitem.getLore().split(":");
					String[] a = new String[lore.length + 2];
					for (int i = 0;i < a.length;i++) {
						if (i < lore.length) {
							a[i] = lore[i];
						}
					}
					a[a.length - 2] = "§aX: " + x;
					a[a.length - 1] = "§aY: " + y;
					
					item = createItem(item, gitem.getName(), a);
					item.setDurability(gitem.getDamage());
					item.setAmount(gitem.getAmount());
					inv.setItem(gitem.getSlot(), item);
				}
				p.openInventory(inv);
			}
		}
	}
	public int XYtoSlot(int x,int y) {
		return y * 9 + x;
	}
	public int[] SlottoXY(int slot) {
		int last = getlast(slot,9);
		int y = last / 9;
		int x = slot - last;
		return new int[] {x,y};
	}
	public com.intellectualcrafters.plot.object.Plot getPlot(org.bukkit.Location playerloc) {
		com.intellectualcrafters.plot.object.Location loc = new com.intellectualcrafters.plot.object.Location(playerloc.getWorld().getName(), (int)playerloc.getX(), (int)playerloc.getY(), (int)playerloc.getZ());
		PlotArea area = PS.get().getPlotAreaAbs(loc);
		com.intellectualcrafters.plot.object.Plot plot = area.getOwnedPlot(loc);
		return plot;
	}
	public int getlast(int i,int i1) {
//		int f = i;
//		while ((float)f / f1 != f / f1) {
//			f--;
//		}
		return i / i1 * 9;
	}
	public static ItemStack createItem(ItemStack item, String name, String[] lore) {
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(name);
		im.setLore(Arrays.asList(lore));
		item.setItemMeta(im);
		return item;
	}
}
