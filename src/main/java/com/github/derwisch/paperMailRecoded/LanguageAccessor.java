package com.github.derwisch.paperMailRecoded;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;

import com.github.derwisch.utils.Utf8YamlConfiguration;

public class LanguageAccessor {
	
	public static String newMailGUITitle = "";
	public static String inboxGUITitle = "";
	public static String vaultLinked = "";
	public static String thruVault = "";
	
	private static String fileName = "language.yml";
	private static File dataFolder;
	private static File file;
	
    public static void saveDefaultLang()
    {
    	fileInit();
    	if(!(file.exists())){
        	paperMailRecoded.plugin.saveResource(fileName, false);
        }    	
    }
     
	public static void LoadLanguage() {
		fileInit();
		FileConfiguration config = Utf8YamlConfiguration.loadConfiguration(file);
		try 
		{
			//Loading and Unloading Messages
			vaultLinked = config.getString("LoadMessages.vaultLinked");
			thruVault = config.getString("LoadMessages.thruVault");
			//GUI TitleBars
			newMailGUITitle = config.getString("GUITitles.newMailGUITitle");
			inboxGUITitle = config.getString("GUITitles.inboxGUITitle");
		} catch (Exception e) {
            e.printStackTrace();
		}
    }
	
	private static void fileInit()
	{
		dataFolder = paperMailRecoded.plugin.getDataFolder();
    	file = new File(dataFolder, fileName);
	}
}