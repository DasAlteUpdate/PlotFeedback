package me.dasneueupdate.plotfeedback;

public class GItem {
	private String name;
	private String lore;
	private short damage;
	private int id;
	private int amount;
	private int slot;
	private ItemType type;
	public GItem(String name,String lore,int id,short damage,int amount,int slot,ItemType type) {
		this.name = name;
		this.lore = lore;
		this.id = id;
		this.damage = damage;
		this.amount = amount;
		this.slot = slot;
		this.type = type;
	}
	public synchronized String getName() {
		return name;
	}
	public synchronized void setName(String name) {
		this.name = name;
	}
	public synchronized short getDamage() {
		return damage;
	}
	public synchronized void setDamage(short damage) {
		this.damage = damage;
	}
	public synchronized int getId() {
		return id;
	}
	public synchronized void setId(int id) {
		this.id = id;
	}
	public synchronized int getSlot() {
		return slot;
	}
	public synchronized void setSlot(int slot) {
		this.slot = slot;
	}
	public synchronized String getLore() {
		return lore;
	}
	public synchronized void setLore(String lore) {
		this.lore = lore;
	}
	public synchronized ItemType getType() {
		return type;
	}
	public synchronized void setType(ItemType type) {
		this.type = type;
	}
	public synchronized int getAmount() {
		return amount;
	}
	public synchronized void setAmount(int amount) {
		this.amount = amount;
	}
}
