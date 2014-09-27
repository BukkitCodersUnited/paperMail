package com.github.derwisch.paperMailRecoded;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;

import com.github.derwisch.utils.Utf8YamlConfiguration;

public class LanguageAccessor {
	
	public static String newMailGUITitle = null;
	public static String inboxGUITitle = null;
	
	
	public static void saveDefault(File f)
	{
		if (!(f.exists()))
		{
			paperMailRecoded.plugin.saveResource("language.yml", false);
		}
	}
	
	public static void LoadLanguage(File f) {
		FileConfiguration config = Utf8YamlConfiguration.loadConfiguration(f);
		try 
		{
			//GUI TitleBars
			newMailGUITitle = config.getString("GUITitles.newMailGUITitle");
			inboxGUITitle = config.getString("GUITitles.inboxGUITitle");
        	
        } catch (Exception e) {
                e.printStackTrace();
        }
    }
    
}