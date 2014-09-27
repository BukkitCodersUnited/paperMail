package com.github.derwisch.paperMailRecoded;
//general Java imports
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;





import java.util.UUID;




//    minecraft internals
import net.minecraft.server.v1_7_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_7_R3.NBTTagCompound;
import net.minecraft.server.v1_7_R3.NBTTagList;









//    bukkit/craftbukkit imports
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_7_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.github.derwisch.utils.InventoryUtils;
import com.github.derwisch.utils.Utf8YamlConfiguration;

public class Inbox {
	
	public static ArrayList<Inbox> Inboxes = new ArrayList<Inbox>();
	
	
	public static void SaveAll() throws IOException {
		for (Inbox inbox : Inboxes) {
			inbox.SaveInbox();
		}
	}
	
	public static Inbox GetInbox(UUID uuid) throws IOException, InvalidConfigurationException {
		for (Inbox inbox : Inboxes) {
			if (inbox.playerUUID.equals(uuid)) {
				return inbox;
			}
		}
		//if player does not yet exist or have an inbox
		AddInbox(uuid);
		return GetInbox(uuid);
	}
	
	public static void AddInbox(UUID uuid) throws IOException, InvalidConfigurationException {
		if (!Settings.InboxPlayers.contains(uuid)) {
			Settings.InboxPlayers.add(uuid);
		}
		Inbox inbox = new Inbox(uuid);
		Inboxes.add(inbox);
	}
	
	public static void RemoveInbox(UUID uuid) {
		for (Inbox inbox : Inboxes) {
			if (inbox.playerUUID.equals(uuid)) {
				Inboxes.remove(inbox);
			}
		}
	}
	
	public UUID playerUUID;
	public Inventory inventory;
	public Chest inboxChest;
	
	private NBTTagCompound c = new NBTTagCompound();
	private FileConfiguration playerConfig;
	private ConfigAccessor configAccessor;
	private File file;
	private File yamlfile;
	
	public Inbox(UUID uuid) throws IOException, InvalidConfigurationException {
		this.playerUUID = uuid;
		String filename = uuid.toString() + ".txt";
		String yamlname = uuid.toString() + ".yml";
		this.configAccessor = new ConfigAccessor(paperMailRecoded.instance, "players\\" + uuid + ".yml");
		this.playerConfig = configAccessor.getConfig();
		configAccessor.saveConfig();
		file = new File(paperMailRecoded.instance.getDataFolder(), "players\\" + filename);
		yamlfile = new File(paperMailRecoded.instance.getDataFolder(), "players\\" + yamlname);
		configAccessor.saveConfig();
		initMailBox();
		loadChest();
		loadItems();
	}
	
	private void initMailBox() {
		Player player = Bukkit.getServer().getPlayer(playerUUID);
		this.inventory = Bukkit.createInventory(player, 36, paperMailRecoded.INBOX_GUI_TITLE);
	}
	
	private void loadChest() {
		String worldName = playerConfig.getString("chest.world");
		worldName = (worldName != null) ? worldName : "";
		World world = Bukkit.getWorld(worldName);
		int x = playerConfig.getInt("chest.x");
		int y = playerConfig.getInt("chest.y");
		int z = playerConfig.getInt("chest.z");

		Block block = (world != null) ? world.getBlockAt(x, y, z) : null;
		
		inboxChest = (block != null && block.getType() == Material.CHEST) ? (Chest)block.getState() : null; 
	}
	
	private void loadItems() throws IOException, InvalidConfigurationException {
		int i = 0;
		ItemStack oldstack = null;
		ItemStack stack = null;
		String itemString = null;
		//Load Current stack save format
		YamlConfiguration yaml = new Utf8YamlConfiguration();
		yaml.load(yamlfile);
		//Load old old stacks for conversion, set slots to empty after load
			do {
			  itemString = yaml.getString("newitemstack." + i);
			  if (itemString != null) {
		        stack = InventoryUtils.stringToItemStack(itemString);
		        inventory.addItem(stack);
		      }
		      i++;
		    }while (itemString != null);
			i = 0;
			do {
			oldstack = playerConfig.getItemStack("itemstack." + i);
			if (oldstack != null){
			if (InventoryUtils.inventoryCheck(inventory, oldstack) == true)
			{
				playerConfig.set("itemstack." + i, "");
				inventory.addItem(oldstack);
			}
			}
			i++;
			} while (oldstack != null);
			//Load old stacks for conversion, delete the username.txt if any found after load
			if(file.exists())
			{	
			try {
				c = NBTCompressedStreamTools.a(new FileInputStream(file));
				file.delete();
			} 	catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		}
		NBTTagList list = c.getList("inventory", 10);
		CraftItemStack cis = null;
		for(int l = 0; l < list.size(); l++){
			  NBTTagCompound item = new NBTTagCompound();
			  item = list.get(l);
			  if(item != null)
			  {
				  int index = item.getInt("index");
				  net.minecraft.server.v1_7_R3.ItemStack is = net.minecraft.server.v1_7_R3.ItemStack.createStack(item); //net.minecraft.server item stack, not bukkit item stack
				  cis = CraftItemStack.asCraftMirror(is);
				  if (InventoryUtils.inventoryCheck(inventory, cis) == true)
				  {
					if(oldstack != cis){
						inventory.setItem(index,cis);
				  	 }
				  }
			 }
		  }
	}

	private void saveChest() {
		if (inboxChest == null) {
			return;
		}
		
		String worldName = inboxChest.getLocation().getWorld().getName();
		int x = inboxChest.getLocation().getBlockX();
		int y = inboxChest.getLocation().getBlockY();
		int z = inboxChest.getLocation().getBlockZ();

		 playerConfig.set("chest.world", worldName);
		 playerConfig.set("chest.x", x);
		 playerConfig.set("chest.y", y);
		 playerConfig.set("chest.z", z);

		 configAccessor.saveConfig();
	}
	
	//Save all items in the recipients or user's Papermail inbox inventory to yaml
	private void saveItems() throws IOException {
		YamlConfiguration yaml = new Utf8YamlConfiguration();
		for (int i = 0; i < Settings.DefaultBoxRows * 9; i++) {
		      CraftItemStack stack = (CraftItemStack)this.inventory.getItem(i);
		      if (stack != null) {
		        String item = InventoryUtils.itemstackToString(stack);
		        yaml.set("newitemstack." + i, item);
		      }
		      if (stack == null)
		      {
		        String item = null;
		        yaml.set("newitemstack." + i, item);
		      }
		    }
		yaml.save(yamlfile);
	}
	
	public void openInbox() {
		Player player = Bukkit.getServer().getPlayer(playerUUID);
		
		player.openInventory(inventory);
	}
	
	public void SetChest(Chest newChest) {
		inboxChest = newChest;
		saveChest();
	}
	
	
	public void AddItem(ItemStack itemStack, Player sender) throws IOException, InvalidConfigurationException {
		Player player = Bukkit.getServer().getPlayer(playerUUID);
		World world = sender.getWorld();
		Location senderLocation = sender.getLocation();
		if (inboxChest != null) {
			if (inboxChest.getInventory().addItem(itemStack).keySet().toArray().length > 0) {
				if (inventory.addItem(itemStack).keySet().toArray().length > 0) {
					if((player != null) && (itemStack != null)){
						if (InventoryUtils.inventoryCheck(player.getInventory(), itemStack) == true)
						{
							player.getInventory().addItem(itemStack);
							
						}else {
							world.dropItemNaturally(senderLocation, itemStack);
							  }
						}
					}
				}
		} else {
			if (inventory.addItem(itemStack).keySet().toArray().length > 0) {
				if((player != null) && (itemStack != null))
				{
				if (InventoryUtils.inventoryCheck(player.getInventory(), itemStack) == true)
					{
					player.getInventory().addItem(itemStack);
					}
				}else{
					world.dropItemNaturally(senderLocation, itemStack);
				}
			}
		}
		saveItems();
	}
	
	public void AddItems(Collection<ItemStack> items, Player sender) throws IOException, InvalidConfigurationException {
		Player player = Bukkit.getServer().getPlayer(playerUUID);
		@SuppressWarnings("unused")
		boolean full = true;
		for (ItemStack itemStack : items) {
					AddItem(itemStack, sender);
			if((player != null) && (itemStack != null)){
					full = InventoryUtils.inventoryCheck(player.getInventory(), itemStack);
			}
		}
		if(full = false){
			sender.sendMessage(ChatColor.DARK_RED + "The Recipient does not have enough space for some of your items. Check the ground for items not sent." + ChatColor.RESET);
		}
		sender.sendMessage(ChatColor.DARK_GREEN + "Message sent!" + ChatColor.RESET);
	}
	//Loads a string from a yaml file with a section name(string) and index number(int)
	public static String loadStringFromYaml(File file,int index, String section) throws IOException, InvalidConfigurationException {
		YamlConfiguration yaml = new Utf8YamlConfiguration();
		yaml.load(file);
		String item = yaml.getString(section + index);
		return item;
	}
	//Saves a string to a yaml file with a section name(string) and index number(int)
	public static void saveStringtoYaml(File file, int index, String str, String section) throws IOException, InvalidConfigurationException {
		YamlConfiguration yaml = new Utf8YamlConfiguration();
		yaml.set(section + index, str);
		yaml.save(file);
	}

	public void SaveInbox() throws IOException {
		saveItems();
		saveChest();
	}
}
