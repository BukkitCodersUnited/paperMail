package com.cmg.inputgui.plugin;

import com.cmg.inputgui.api.InputGuiAPI;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class InputGuiPlugin
  extends JavaPlugin
  implements InputGuiAPI, Listener
{
  private Map<String, InputGuiPlayer> players = new HashMap();
  
  public void onEnable()
  {
    getServer().getPluginManager().registerEvents(this, this);
    new InputGuiPacketListener(this);
  }
  
  public InputGuiPlayer getPlayer(Player player)
  {
    String name = player.getName();
    if (this.players.containsKey(name)) {
      return (InputGuiPlayer)this.players.get(name);
    }
    InputGuiPlayer iplayer = new InputGuiPlayer(this, player);
    this.players.put(name, iplayer);
    
    return iplayer;
  }
  
  public void removePlayer(Player player)
  {
    this.players.remove(player.getName());
  }
  
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent e)
  {
    getPlayer(e.getPlayer());
  }
  
  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent e)
  {
    removePlayer(e.getPlayer());
  }
  
  @EventHandler
  public void onPlayerMove(PlayerMoveEvent e)
  {
    InputGuiPlayer player = getPlayer(e.getPlayer());
    if (player.isCheckingMovement()) {
      player.setCancelled();
    }
  }
}
