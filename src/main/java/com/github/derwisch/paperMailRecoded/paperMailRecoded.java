package com.github.derwisch.paperMailRecoded;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class paperMailRecoded extends JavaPlugin {
	
	public static paperMailRecoded instance;
	public static Server server;
	public static Logger logger;
	public static Economy economy = null;
	public static paperMailRecoded plugin;
	public static boolean serverOnlineMode = true;
	
	private PaperMailListener listener;
	private FileConfiguration configuration;
	
    @Override
    public void onEnable() {
    	instance = this;
    	server = this.getServer();
    	logger = this.getLogger();
    	
    	//Load Config
    	loadConfig(this);
    	initLanguage();
    	//Load Economy
    	if (setupEconomy().booleanValue())
    		System.out.println(this + ": linked into " + economy.getName() + ", via Vault");
        if ((setupEconomy() == false) && (Settings.EnableMailCosts == true)) {
        	System.out.println(this + ": Vault economy not found, switching to Default Economy!");
        }
        //Check for online mode to see if we need to support UUIDs
        if(!(server.getOnlineMode())){
        	System.out.println(this + ": This server is running in OFFLINE mode. Support for UUIDs disabled.");
        	serverOnlineMode = server.getOnlineMode();
        }
        //Initialize CommandExecutor
        initCommandExecutor(this);
    	
    	listener = new PaperMailListener();
        this.getServer().getPluginManager().registerEvents(listener, this);
        
        initializeRecipes();
        initializeInboxes();
        
    	logger.info("Enabled PaperMail");
    }
    
    public static final String NEW_MAIL_GUI_TITLE = ChatColor.BLACK + LanguageAccessor.newMailGUITitle + ChatColor.RESET;
	public static final String INBOX_GUI_TITLE = ChatColor.BLACK + LanguageAccessor.inboxGUITitle + ChatColor.RESET;
    
	@Override
    public void onDisable() {
		try {
			Inbox.SaveAll();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Settings.SaveConfiguration(configuration);
		this.saveConfig();
    	getLogger().info("Disabled PaperMail");
    }
	
	private void initLanguage()
	{
		File f = new File(getDataFolder()+File.separator+"language.yml");
        LanguageAccessor.saveDefault(f);
        LanguageAccessor.LoadLanguage(f); 
	}
	
	private void loadConfig(paperMailRecoded instance)
	{
		instance = this;
		saveDefaultConfig();
    	configuration = this.getConfig();
    	Settings.LoadConfiguration(configuration);
	}
	
	private void initCommandExecutor(paperMailRecoded instance)
	{
		instance = this;
		PaperMailCommandExecutor commandExecutor = new PaperMailCommandExecutor(this); 
    	getCommand("papermail").setExecutor(commandExecutor);
    	getCommand("sendtext").setExecutor(commandExecutor);
    	getCommand("createbox").setExecutor(commandExecutor);
	}
    
    @SuppressWarnings("deprecation")
	private void initializeRecipes() {
		ItemStack letterPaper = new ItemStack(Settings.MailItemID);
		ItemMeta letterPaperMeta = letterPaper.getItemMeta();
		ArrayList<String> letterPaperLore = new ArrayList<String>();
		letterPaperMeta.setDisplayName(ChatColor.WHITE + Settings.MailItemName + ChatColor.RESET);
		letterPaperLore.add(ChatColor.GRAY + "Used to send a letter" + ChatColor.RESET);
		letterPaperMeta.setLore(letterPaperLore);
    	letterPaper.setItemMeta(letterPaperMeta);
    	letterPaper.setDurability((short)Settings.MailItemDV);
		
		ShapelessRecipe letterPaperRecipe = new ShapelessRecipe(letterPaper);
		letterPaperRecipe.addIngredient(Material.PAPER);
		letterPaperRecipe.addIngredient(Material.INK_SACK);
		letterPaperRecipe.addIngredient(Material.FEATHER);
		
		this.getServer().addRecipe(letterPaperRecipe);
    }

    @SuppressWarnings("deprecation")
	private void initializeInboxes() {
		for (Player player : getServer().getOnlinePlayers()) {
			if (player == null) {
				continue;
			}
			try {
				Inbox.AddInbox(player.getUniqueId());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (OfflinePlayer offPlayer : getServer().getOfflinePlayers()) {
			
			Player player = offPlayer.getPlayer();
			
			if (player == null) {
				continue;
			}
			try {
				Inbox.AddInbox(player.getUniqueId());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
    
    public static boolean isGoldIngot() {
        return economy == null;
     }

     
   @SuppressWarnings("rawtypes")
   public Boolean setupEconomy()
     {
       Plugin vault = getServer().getPluginManager().getPlugin("Vault");
      if (vault == null) {
        return Boolean.valueOf(false);
       }
     RegisteredServiceProvider economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
     if (economyProvider != null) {
        economy = (Economy)economyProvider.getProvider();
       }

        return Boolean.valueOf(economy != null);
     }
}
