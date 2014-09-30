package com.github.derwisch.paperMailRecoded;

import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.cmg.inputgui.api.InputGui;
import com.cmg.inputgui.api.InputGuiAPI;
import com.cmg.inputgui.api.InputPlayer;
 
public class PaperMailListener implements Listener {
	
	public double sendAmount = 0;
	InputGuiAPI api = (InputGuiAPI) Bukkit.getPluginManager().getPlugin("InputGuiAPI");
	
    @EventHandler
    public void playerJoin(PlayerJoinEvent event) throws IOException, InvalidConfigurationException {
		Inbox inbox = Inbox.GetInbox(event.getPlayer().getUniqueId());
		if (inbox == null) {
			Inbox.AddInbox(event.getPlayer().getUniqueId());
		}
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getItemInHand();
        ItemMeta inHandMeta = itemInHand.getItemMeta();
        if (itemInHand != null && inHandMeta != null && itemInHand.getType() == Material.getMaterial(Settings.MailItemID) && itemInHand.getDurability() == Settings.MailItemDV && inHandMeta.getDisplayName().equals((ChatColor.WHITE + Settings.MailItemName + ChatColor.RESET)) && player.hasPermission(Permissions.SEND_ITEM_PERM)) {
        	new PaperMailGUI(player, true).Show();
        }
        
        Block clickedBlock = event.getClickedBlock(); 
        if(clickedBlock != null && clickedBlock.getState() instanceof Sign) {
            Sign s = (Sign)event.getClickedBlock().getState();
            if(s.getLine(1).toLowerCase().contains("[mailbox]")) {
                if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                	new PaperMailGUI(player).Show();
                } else if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                	Inbox inbox = null;
					try {
						inbox = Inbox.GetInbox(player.getUniqueId());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvalidConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	inbox.openInbox();
                	event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onInventoryClick_MailGUI_Send(InventoryClickEvent event) throws IOException, InvalidConfigurationException {
    	if (event.getInventory().getName() != paperMailRecoded.NEW_MAIL_GUI_TITLE)
    		return;
    	double ItemCost = Settings.ItemCost;
    	Inventory inventory = event.getInventory();
    	Player player = ((Player)inventory.getHolder());
    	double numItems = 0;
    	PaperMailGUI itemMailGUI = PaperMailGUI.GetGUIfromPlayer((Player)inventory.getHolder());
    	ItemStack currentItem = event.getCurrentItem();
    	ItemMeta currentItemMeta = (currentItem == null) ? null : currentItem.getItemMeta();
    	ItemStack recipientItem = inventory.getItem(0);
    	boolean writtenBookBoolean = (recipientItem != null && recipientItem.getType() == Material.WRITTEN_BOOK);
    	boolean cancel = false;
    	for (int i = 0; i < inventory.getSize(); i++) {
    		ItemStack CraftStack = inventory.getItem(i);
    		if (CraftStack != null && CraftStack.hasItemMeta() && CraftStack.getItemMeta().hasDisplayName())
    		{
    		ItemMeta itemMeta = CraftStack.getItemMeta();
    		if (itemMeta.getDisplayName() != PaperMailGUI.SEND_BUTTON_ON_TITLE && 
    				itemMeta.getDisplayName() != PaperMailGUI.CANCEL_BUTTON_TITLE && 
    				itemMeta.getDisplayName() != PaperMailGUI.ENDERCHEST_BUTTON_TITLE &&
    				CraftStack.getType() != Material.WRITTEN_BOOK && 
    				CraftStack != null &&
    				itemMeta.getDisplayName() != PaperMailGUI.MONEY_SEND_BUTTON_TITLE) {
    				if((Settings.EnableMailCosts == true) && (itemMeta.getDisplayName() == PaperMailGUI.MONEY_SEND_BUTTON_TITLE) && (CraftStack.getAmount() > 1)){
    					sendAmount = CraftStack.getAmount();
    				}
    				numItems++;
    		}
    	}}
    	if(Settings.PerItemCosts == true){
    		ItemCost = numItems * Settings.ItemCost;
    	}
    	if((Settings.EnableSendMoney) && (sendAmount > 1)){
        	ItemCost = ItemCost + sendAmount;
        }
    	if (currentItemMeta != null && currentItemMeta.getDisplayName() == PaperMailGUI.SEND_BUTTON_ON_TITLE) {
    		if (((Settings.EnableMailCosts == true) && (writtenBookBoolean) && (ItemCost != 0) && (!player.hasPermission(Permissions.COSTS_EXEMPT)) && ((sendAmount > 1) && Settings.EnableSendMoney)) || ((Settings.EnableMailCosts == true) && (writtenBookBoolean) && (ItemCost != 0) && (!player.hasPermission(Permissions.COSTS_EXEMPT)) && ((sendAmount == 0) || Settings.EnableSendMoney == false))){
    			if (PaperMailEconomy.hasMoney(ItemCost, player) == true){
                	cancel = false;
                }else if(PaperMailEconomy.hasMoney(ItemCost, player) == false){
                    player.sendMessage(ChatColor.RED + "Not enough money to send your mail, mail not sent!");
                    cancelSend(event, itemMailGUI);
            		cancel = true;
                }
            }else if(player.hasPermission(Permissions.COSTS_EXEMPT) && (PaperMailEconomy.hasMoney(sendAmount, player) && (Settings.EnableMailCosts == true) && ((sendAmount > 1) && Settings.EnableSendMoney)))
            {
            	cancel = false;
            }
    		if(player.hasPermission(Permissions.COSTS_EXEMPT) && (!PaperMailEconomy.hasMoney(sendAmount, player) && (Settings.EnableMailCosts == true) && ((sendAmount > 1) && Settings.EnableSendMoney)))
    		{
    			player.sendMessage(ChatColor.DARK_RED + "You are trying to send more money than you have. Mail not sent." + ChatColor.RESET);
    			cancelSend(event, itemMailGUI);
        		cancel = true;
    		}
    		if(((Settings.EnableSendMoney == true) && (PaperMailEconomy.hasMoney(sendAmount, player) == false) && (sendAmount > 1)) && (writtenBookBoolean) && ((Settings.EnableMailCosts == false) || ItemCost == 0 || player.hasPermission(Permissions.COSTS_EXEMPT)))
    		{
    			player.sendMessage(ChatColor.DARK_RED + "You are trying to send more money than you have. Mail not sent." + ChatColor.RESET);
    			cancelSend(event, itemMailGUI);
        		cancel = true;
    		}
            if (!writtenBookBoolean) {
    			((Player)inventory.getHolder()).sendMessage(ChatColor.RED + "No recipient defined" + ChatColor.RESET);
    			cancelSend(event, itemMailGUI);
        		cancel = true;
	    		}
            if(writtenBookBoolean && ((Settings.EnableMailCosts == false) || (Settings.ItemCost == 0) || player.hasPermission(Permissions.COSTS_EXEMPT)) && ((Settings.EnableSendMoney == false) || (((Settings.EnableSendMoney == true) && (sendAmount == 0)))) && (cancel != true)) {
            	cancel = false;
    		}
            
            if (cancel != true)
            {
            	sendMail(itemMailGUI, event, inventory);
            	itemMailGUI.close();
        		itemMailGUI.SetClosed();
        		event.setCancelled(true);
            }
    }
 }
    
    @EventHandler
    public void onInventoryClick_MailGUI_Cancel(InventoryClickEvent event) {
    	if (event.getInventory().getName() != paperMailRecoded.NEW_MAIL_GUI_TITLE)
    		return;
    	
    	Inventory inventory = event.getInventory();
    	PaperMailGUI itemMailGUI = PaperMailGUI.GetGUIfromPlayer((Player)inventory.getHolder());
    	ItemStack currentItem = event.getCurrentItem();
    	ItemMeta currentItemMeta = (currentItem == null) ? null : currentItem.getItemMeta();

    	if (currentItemMeta != null && currentItemMeta.getDisplayName() == PaperMailGUI.CANCEL_BUTTON_TITLE) {
    		itemMailGUI.Result = SendingGUIClickResult.CANCEL;
    		itemMailGUI.close();
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void onInventoryClick_MailGUI_Enderchest(InventoryClickEvent event) {
    	if (event.getInventory().getName() != paperMailRecoded.NEW_MAIL_GUI_TITLE)
    		return;
    	
    	ItemStack currentItem = event.getCurrentItem();
    	ItemMeta currentItemMeta = (currentItem == null) ? null : currentItem.getItemMeta();
    	
    	if (currentItemMeta != null && currentItemMeta.getDisplayName() == PaperMailGUI.ENDERCHEST_BUTTON_TITLE) {
    		Player player = (Player)event.getInventory().getHolder();
    		PaperMailGUI itemMailGUI = PaperMailGUI.GetGUIfromPlayer(player);
    		itemMailGUI.Result = SendingGUIClickResult.OPEN_ENDERCHEST;
    		event.setCancelled(true);
    		OpenInventory(player, player.getEnderChest());
    	}
    }

    private static Player player = null;
    private static Inventory inventory = null;    
    public static void OpenInventory(Player player, Inventory inventory) {
    	PaperMailListener.player = player;
    	PaperMailListener.inventory = inventory;
    	paperMailRecoded.server.getScheduler().scheduleSyncDelayedTask(paperMailRecoded.instance, new Runnable() {
    	    public void run() {
    	    	openInventory(PaperMailListener.player, PaperMailListener.inventory);
    	    }
    	}, 0L);
    }
    
    private static void openInventory(Player player, Inventory inventory) {
		player.closeInventory();
    	player.openInventory(inventory);
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onInventoryClick_MailGUI_Recipient(InventoryClickEvent event) {
    	if (event.getInventory().getName() != paperMailRecoded.NEW_MAIL_GUI_TITLE)
    		return;
    	
    	ItemStack currentItem = event.getCurrentItem();
    	ItemStack cursorItem = event.getCursor();
    	ItemMeta currentItemMeta = (currentItem == null) ? null : currentItem.getItemMeta();
    	Player player = (Player) event.getView().getPlayer();
    	if (currentItemMeta != null && currentItemMeta.getDisplayName() == PaperMailGUI.RECIPIENT_TITLE) {
    		event.setCurrentItem(null);
    		if (cursorItem.getType() == Material.WRITTEN_BOOK) {
    			event.setCurrentItem(cursorItem);
	    		event.setCursor(new ItemStack(Material.AIR));
	    		player.updateInventory();
	    		event.setCancelled(true);
    		} else {
        		event.setCancelled(true);
        	}
    	}
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) throws IOException, InvalidConfigurationException {
    	Inventory inventory = event.getInventory();
    	
    	if (inventory.getName() == paperMailRecoded.NEW_MAIL_GUI_TITLE) {
    		Player player = (Player) event.getPlayer();
    		PaperMailGUI gui = PaperMailGUI.GetGUIfromPlayer(player);
    		World world = player.getWorld();
    		Location playerLocation = player.getLocation();
    		if ((gui.Result == SendingGUIClickResult.CANCEL) && (gui.Result != null)) {
    			gui.SetClosed();
    			ItemStack[] inventoryContents = inventory.getContents();
    			if (inventoryContents != null) {
		    		for (ItemStack itemStack : inventoryContents) {
		    			if (itemStack == null)
		    				continue;
		    			
		    			ItemMeta itemMeta = itemStack.getItemMeta();
		    			if (itemMeta != null &&
		    					(itemMeta.getDisplayName() == PaperMailGUI.CANCEL_BUTTON_TITLE ||
		    					itemMeta.getDisplayName() == PaperMailGUI.ENDERCHEST_BUTTON_TITLE ||
		    					itemMeta.getDisplayName() == PaperMailGUI.RECIPIENT_TITLE ||
		    					itemMeta.getDisplayName() == PaperMailGUI.SEND_BUTTON_ON_TITLE ||
		    					itemMeta.getDisplayName() == PaperMailGUI.RECIPIENT_TITLE) ||
		    					itemMeta.getDisplayName() == PaperMailGUI.MONEY_SEND_BUTTON_TITLE) {
		    				continue;
		    			}
		    			world.dropItemNaturally(playerLocation, itemStack);    			
		    		}
    			}
    		}
    		
    	}

    	if (inventory.getName() == paperMailRecoded.INBOX_GUI_TITLE) {
    		Player player = (Player) event.getPlayer();
    		Inbox inbox = Inbox.GetInbox(player.getUniqueId());
    		inbox.SaveInbox();
    	}
    	
    	
    	if (inventory.getType().toString() == "ENDER_CHEST") {
        	Player player = (Player)event.getPlayer();
        	PaperMailGUI gui = PaperMailGUI.GetOpenGUI(player.getName());
        	if (gui != null) {
        		OpenInventory(player, gui.Inventory);
        		gui.Result = SendingGUIClickResult.CANCEL;
        	}
    		
    	}
    }
    
    
    @SuppressWarnings("unused")
	@EventHandler
    public void onInventoryClick_MailGUI_sendMoney(InventoryClickEvent event) {
    	if (event.getInventory().getName() != paperMailRecoded.NEW_MAIL_GUI_TITLE)
    		return;
    	//	
    	if(paperMailRecoded.protocolCheck()){	
    		String GUIAmount = "";
    		Player p = (Player) event.getWhoClicked();
    		InputPlayer iplayer = api.getPlayer(p);
    		iplayer.openGui(new InputGui(){
    			public String getDefaultText() {
    				return "Amount";
				} 
    		 
    		    public void onConfirm(InputPlayer player, String input) {
    		        player.getPlayer().sendMessage(this.getDefaultText() + " -> " + input);
    		    }
    		 
    		    public void onCancel(InputPlayer player) {
   
    		    }
    		 
    		});
    	}else{	//begin else
    	//
    	Inventory inv = event.getInventory();
    	inv.setMaxStackSize(127);
    	ItemStack currentItem = event.getCurrentItem();
    	ItemMeta currentItemMeta = (currentItem == null) ? null : currentItem.getItemMeta();
    	
    	if ((inv.getName() != null) && (inv.getName() == paperMailRecoded.NEW_MAIL_GUI_TITLE)) {
    		if ((currentItemMeta != null) && (currentItemMeta.getDisplayName() == PaperMailGUI.MONEY_SEND_BUTTON_TITLE) && (event.getClick().isLeftClick())) {
    			if(currentItem.getAmount() == 0){
    				currentItem.setAmount(1);
    			}else{
    			currentItem.setAmount(currentItem.getAmount() + Settings.Increments);
    			}
    			sendAmount = currentItem.getAmount();           
    			event.setCancelled(true);
    		}
    		if ((currentItemMeta != null) && (currentItemMeta.getDisplayName() == PaperMailGUI.MONEY_SEND_BUTTON_TITLE) && (event.getClick().isRightClick())) {
    			if(currentItem.getAmount() == 0){
    				currentItem.setAmount(1);
    			}else{
    			currentItem.setAmount(currentItem.getAmount() - Settings.Increments);
    			}
    			sendAmount = currentItem.getAmount();
    			event.setCancelled(true);
    		}
    	}
    	}//end else
    }
    
    
    
    //Create Method On Right Click Item Bank Note Deposit
    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event){
    	Player p = event.getPlayer();
    	if ((p.getItemInHand().hasItemMeta()) && (p.getItemInHand().getItemMeta().hasDisplayName()) && (p.getItemInHand().getItemMeta().getDisplayName().contains(PaperMailGUI.BANK_NOTE_DISPLAY)) && ((event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) || (event.getAction().equals(Action.RIGHT_CLICK_AIR)))){
        		ItemStack bankNote = p.getItemInHand();
        		ItemMeta noteMeta = bankNote.getItemMeta();
        		List<String> noteParse = noteMeta.getLore();
        		String noteAmount = noteParse.get(noteParse.size() - 1); 
        		noteAmount = noteAmount.replaceAll("[^0-9]", "");
        		int fromString = java.lang.Integer.parseInt(noteAmount);
        		double deposit = fromString;
        		PaperMailEconomy.cashBankNote(p, deposit);
        		if(bankNote.getAmount() < 2)
        		{
        			p.setItemInHand(new ItemStack(Material.AIR));
        		}else{
        			bankNote.setAmount(bankNote.getAmount() - 1);
        			p.setItemInHand(bankNote);
        		}
        		p.sendMessage(ChatColor.GREEN + "$" + deposit + " was deposited into your Account!" + ChatColor.RESET);
        		event.setCancelled(true);
        }
    }
    
    public void sendMail(PaperMailGUI itemMailGUI, InventoryClickEvent event, Inventory inventory) throws IOException, InvalidConfigurationException{
    	itemMailGUI.Result = SendingGUIClickResult.SEND;
		itemMailGUI.SendContents();
		itemMailGUI.close();
		itemMailGUI.SetClosed();
		event.setCancelled(true);
    	itemMailGUI = null;
    	PaperMailGUI.RemoveGUI(((Player) inventory.getHolder()).getName());
    	
    }
    
    public void cancelSend(InventoryClickEvent event, PaperMailGUI itemMailGUI){
    	itemMailGUI.Result = SendingGUIClickResult.CANCEL;
		itemMailGUI.close();
		itemMailGUI.SetClosed();
		event.setCancelled(true);
    }
}
