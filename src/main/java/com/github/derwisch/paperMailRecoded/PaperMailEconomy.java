package com.github.derwisch.paperMailRecoded;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.derwisch.utils.InventoryUtils;


public class PaperMailEconomy{
	public static boolean hasMoney = true;

	/*Count the total number of Currency Items in Inventory if using the Built in Economy*/
    public static int goldCounter(Player player, Material currencyItem){    
	  int gold = 0;
	  for (ItemStack i : player.getInventory()) {
		  if ((i != null) && (i.getType() == currencyItem)){
				int counter = 0;
				counter = i.getAmount();
				gold = gold + counter;
			}
			}
	  return gold;
		}
  
    /*Take Currency Items from Inventory if using the Built in Economy*/
    @SuppressWarnings("deprecation")
	public static void takeGold(int Price, Player player, Material currencyItem){    
	  int change;
	  int goldLeft = Price;
	  for (ItemStack i : player.getInventory().getContents()){
		  if ((i != null) && (i.getType() == currencyItem)){
			  if (i.getAmount() >= goldLeft){
				  change = i.getAmount() - goldLeft;
				  if(change == 0)
				  {
					  player.getInventory().removeItem(i);
					  player.updateInventory();
					  return;
				  }
				  if(change != 0){
					  i.setAmount(change);
					  return;
				  }
			  }
			  if(i.getAmount() < goldLeft){
				goldLeft = goldLeft - i.getAmount();
				player.getInventory().removeItem(i);
				player.updateInventory();
			  	}
			  }
			  }
	}
    
    //take money from the player
    @SuppressWarnings({ "deprecation" })
	public static void takeMoney(Double price, Player player){
    if (!(paperMailRecoded.isGoldIngot())) {
    	paperMailRecoded.economy.withdrawPlayer(player.getName(), price.doubleValue());
				player.sendMessage(ChatColor.GREEN + "%price% removed from Wallet!".replace("%price%", new StringBuilder().append(ChatColor.WHITE).append(price.toString()).toString()) + ChatColor.RESET);   
    }
   else {
	   String currencyName = Material.getMaterial(Settings.CurrencyItemID).toString();
       int goldPrice = (int)Math.ceil(price.doubleValue());  
       takeGold(goldPrice, player, Material.getMaterial(Settings.CurrencyItemID));
       StringBuilder sb = new StringBuilder();
       sb.append("");
       sb.append(goldPrice);
       StringBuilder sb1 = new StringBuilder();
       sb1.append("");
       sb1.append(currencyName);
       currencyName = sb1.toString().replace("_", " ");
       player.sendMessage(ChatColor.WHITE + "%price%".replace("%price%", sb.append(ChatColor.WHITE)).toString() + " " + ChatColor.YELLOW + "%currencyName%".replace("%currencyName%", currencyName) + ChatColor.GREEN + " " + "removed from Inventory!" + ChatColor.RESET);    
      }
    }
    
    //check if player has correct amount of currency, return true if they do, false if they don't
    @SuppressWarnings("deprecation")
	public static boolean hasMoney(Double price, Player player){
	  if ((!(paperMailRecoded.isGoldIngot())) && (Settings.EnableMailCosts != false) && (price != 0)) {
		  if (paperMailRecoded.economy.getBalance(player.getName()) < price.doubleValue()) {
		  		hasMoney = false;
		  		return false;
				 }
	  }else if((paperMailRecoded.isGoldIngot()) && (Settings.EnableMailCosts != false) && (price != 0)){
		  int goldAmount = goldCounter(player, Material.getMaterial(Settings.CurrencyItemID));
		  int goldPrice = (int)Math.ceil(price.doubleValue());
		  if (goldAmount < goldPrice) {
		         hasMoney = false;
		         return false;
		  }
		  
	  }
	  hasMoney = true;
	  return true;
  }
  
    //Converts an amount of a player's money into a custom Bank Note Item
    @SuppressWarnings("deprecation") //new ItemStack(int) is deprecated, but we'll use it for now
	public static ItemStack getBankNote(int amount, Player player){
		ItemStack bankNote = new ItemStack(Settings.BankNoteNum); 
		String BANK_NOTE_NAME = PaperMailGUI.BANK_NOTE_DISPLAY + ChatColor.RED + "(" + ChatColor.GREEN + "$" + ChatColor.GOLD + amount + ChatColor.RED +  ")" + ChatColor.RESET;
		ItemMeta bankNoteMeta = bankNote.getItemMeta();
		ArrayList<String> bankNoteLore = new ArrayList<String>();
		bankNoteLore.add(ChatColor.GRAY + "Right Click this Bank" + ChatColor.RESET);
		bankNoteLore.add(ChatColor.GRAY + "Note to deposit the sum" + ChatColor.RESET);
		bankNoteLore.add(ChatColor.GRAY + "into your Account" + ChatColor.RESET);
		bankNoteLore.add(ChatColor.RESET + "$" + amount);
		bankNoteMeta.setDisplayName(BANK_NOTE_NAME);
		bankNoteMeta.setLore(bankNoteLore);
		bankNote.setItemMeta(bankNoteMeta);
		PaperMailEconomy.takeMoney((double)amount, player);
		return bankNote;
	}
    
    //Deposit the banknote into the player's bank account
    @SuppressWarnings("deprecation")
	public static void cashBankNote(Player player, double amount){
      if (!(paperMailRecoded.isGoldIngot())) {
	  String playerName = player.getName();
	  paperMailRecoded.economy.depositPlayer(playerName, amount);
      }else
      {
    	  int currencyAmt = (int)amount;
    	  ItemStack currencyItem = new ItemStack(Material.getMaterial(Settings.CurrencyItemID), currencyAmt);
    	  if(InventoryUtils.inventoryCheck(player, currencyItem)){
    		  player.getInventory().addItem(currencyItem);
    	  }else
    	  {
    		  player.sendMessage(ChatColor.RED + "Not Enough Room in your Inventory to Deposit this BankNote." + ChatColor.RESET);
    	  }
      }
      
  }
}
