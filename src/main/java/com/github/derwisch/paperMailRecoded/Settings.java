package com.github.derwisch.paperMailRecoded;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.Configuration;

public class Settings {

	// Config default values
	public static boolean EnableTextMail = true;
	public static boolean EnableItemMail = true;
	public static boolean EnableEnderchest = true;
	public static boolean EnableMailCosts = false;
	public static boolean PerItemCosts = false;
	public static boolean EnableSendMoney = false;

	public static int MailWindowRows = 4;
	public static int DefaultBoxRows = 5;
	
	public static int EnderChestRequirementID = 368;
	public static int EnderChestRequirementDV = 0;
	
	public static int CurrencyItemID = 266;
	public static double Price = 0;
	public static double ItemCost = 0;
	public static int MailItemID = 339;
	public static int MailItemDV = 1;
	public static int BankNoteNum = 339;
	public static int Increments = 1;
	public static String MailItemName = "Letter paper";
	
	public static ArrayList<UUID> InboxPlayers = new ArrayList<UUID>();
	public static List<String> InboxPlayersString = new ArrayList<String>();
	
    public static void LoadConfiguration(Configuration config) {
        try {
        	DefaultBoxRows = Math.max(3, config.getInt("general.DefaultBoxRows"));
        	MailWindowRows = Math.max(3, config.getInt("general.MailWindowRows"));
        	
        	EnableTextMail = config.getBoolean("general.EnableTextMail");
        	EnableItemMail = config.getBoolean("general.EnableItemMail");
        	EnableEnderchest = config.getBoolean("general.EnableEnderchest");
        	EnableMailCosts = config.getBoolean("general.EnableMailCosts");
        	PerItemCosts = config.getBoolean("general.EnablePerItemCosts");
        	EnableSendMoney = config.getBoolean("general.EnableSendMoney");
        	
        	Price = config.getDouble("general.CostToText");
        	ItemCost = config.getDouble("general.SendItemCost");

        	CurrencyItemID = config.getInt("general.CurrencyItemID");
        	MailItemID = config.getInt("general.MailItemID");
        	MailItemDV = config.getInt("general.MailItemDV");
        	BankNoteNum = config.getInt("general.BankNoteID");
        	MailItemName = config.getString("general.MailItemName");
        	
        	InboxPlayersString = config.getStringList("inboxPlayers");
        	for(String s : InboxPlayersString){
        		UUID uuid = com.github.derwisch.utils.UUIDFetcher.getUUID(s);
        		InboxPlayers.add(uuid);
        	}
        	
        } catch (Exception e) {
                e.printStackTrace();
        }
    }
    
    public static void SaveConfiguration(Configuration config) {
    	config.set("general.DefaultBoxRows", DefaultBoxRows);
    	config.set("general.MailWindowRows", MailWindowRows);
    	
    	config.set("general.EnableTextMail", EnableTextMail);
    	config.set("general.EnableItemMail", EnableItemMail);
    	config.set("general.EnableEnderchest", EnableEnderchest);
    	config.set("general.EnableMailCosts", EnableMailCosts);
    	config.set("general.EnablePerItemCosts", PerItemCosts);
    	config.set("general.EnableSendMoney", EnableSendMoney);
    	
    	config.set("general.CostToText", Price);
    	config.set("general.SendItemCost", ItemCost);
    	
    	config.set("general.CurrencyItemID", CurrencyItemID);
    	config.set("general.MailItemID", MailItemID);
    	config.set("general.MailItemDV", MailItemDV);
    	config.set("general.BankNoteID", BankNoteNum);
    	config.set("general.MailItemName", MailItemName);
    	    	
    	config.set("inboxPlayers", InboxPlayers);
    }
    
}
