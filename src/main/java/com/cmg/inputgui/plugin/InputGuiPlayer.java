package com.cmg.inputgui.plugin;

import com.cmg.inputgui.api.InputGui;
import com.cmg.inputgui.api.InputGuiBase;
import com.cmg.inputgui.api.InputGuiSign;
import com.cmg.inputgui.api.InputPlayer;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class InputGuiPlayer
  implements InputPlayer
{
  private boolean checkPackets;
  private boolean checkMove;
  private BukkitRunnable checkPacketsTask;
  private BukkitRunnable checkMoveTask;
  private Location fakeBlockLoc = null;
  private Plugin plugin;
  private Player player;
  private InputGuiBase<?> gui;
  
  public InputGuiPlayer(Plugin plugin, Player player)
  {
    this.plugin = plugin;
    this.player = player;
  }
  
  public void openTileEditor(Block block)
  {
    setCancelled();
    try
    {
      PacketContainer packet = InputGuiUtils.getOpenGuiPacket(block
        .getLocation());
      ProtocolLibrary.getProtocolManager().sendServerPacket(this.player, 
        packet);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public Player getPlayer()
  {
    return this.player;
  }
  
  public boolean isGuiOpen()
  {
    return this.gui != null;
  }
  
  public InputGuiBase<?> getCurrentGui()
  {
    return this.gui;
  }
  
  public boolean closeGui()
  {
    if (this.gui == null) {
      return false;
    }
    try
    {
      ProtocolLibrary.getProtocolManager().sendServerPacket(this.player, 
        InputGuiUtils.getCloseGuiPacket());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return true;
  }
  
  public void openGui(InputGuiBase<?> gui)
  {
    openGui(gui, 17, 3);
  }
  
  @SuppressWarnings("deprecation")
public void openGui(InputGuiBase<?> gui, int moveCheckTicks, int packetCheckTicks)
  {
    setCancelled();
    this.gui = gui;
    
    Location playerLoc = this.player.getLocation();
    Vector direction = playerLoc.getDirection().normalize().multiply(-5);
    this.fakeBlockLoc = playerLoc.add(direction);
    
    this.checkPackets = false;
    this.checkMove = false;
    if ((gui instanceof InputGuiSign))
    {
      String[] lines = (String[])gui.getDefaultText();
      try
      {
        ProtocolLibrary.getProtocolManager().sendServerPacket(
          this.player, 
          InputGuiUtils.getSignPacket(this.fakeBlockLoc, lines));
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    else if ((gui instanceof InputGui))
    {
      this.player.sendBlockChange(this.fakeBlockLoc, Material.COMMAND, 
        (byte)0);
      
      String text = (String)gui.getDefaultText();
      try
      {
        ProtocolLibrary.getProtocolManager().sendServerPacket(
          this.player, 
          
          InputGuiUtils.getTileDataPacket(this.fakeBlockLoc, text));
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    else
    {
      throw new IllegalArgumentException("Unsupported gui: '" + 
        gui.getClass().getSimpleName() + "'!");
    }
    try
    {
      ProtocolLibrary.getProtocolManager().sendServerPacket(this.player, 
        InputGuiUtils.getOpenGuiPacket(this.fakeBlockLoc));
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    this.checkPacketsTask = new BukkitRunnable()
    {
      public void run()
      {
        InputGuiPlayer.this.checkPackets = true;
        InputGuiPlayer.this.checkPacketsTask = null;
      }
    };
    this.checkMoveTask = new BukkitRunnable()
    {
      public void run()
      {
        InputGuiPlayer.this.checkMove = true;
        InputGuiPlayer.this.checkMoveTask = null;
      }
    };
    this.checkPacketsTask.runTaskLater(this.plugin, packetCheckTicks);
    this.checkMoveTask.runTaskLater(this.plugin, moveCheckTicks);
  }
  
  @SuppressWarnings("deprecation")
public void setCancelled()
  {
    if (this.checkMoveTask != null)
    {
      this.checkMoveTask.cancel();
      this.checkMoveTask = null;
    }
    if (this.gui != null)
    {
      this.gui.onCancel(this);
      this.gui = null;
    }
    if (this.fakeBlockLoc != null)
    {
      Block block = this.fakeBlockLoc.getBlock();
      this.player.sendBlockChange(this.fakeBlockLoc, block.getTypeId(), 
        block.getData());
    }
  }
  
  public void setConfirmed(Object input)
  {
    if (this.checkMoveTask != null)
    {
      this.checkMoveTask.cancel();
      this.checkMoveTask = null;
    }
    if ((this.gui instanceof InputGuiSign)) {
      ((InputGuiSign)this.gui).onConfirm(this, (String[])input);
    } else if (((this.gui instanceof InputGui)) && 
      (this.gui != null) && (input != null)) {
      try
      {
        ((InputGui)this.gui).onConfirm(this, (String)input);
      }
      catch (Exception localException) {}
    }
    this.gui = null;
  }
  
  public boolean isCheckingMovement()
  {
    return this.checkMove;
  }
  
  public boolean isCheckingPackets()
  {
    return this.checkPackets;
  }
  
  public Location getFakeBlockLocation()
  {
    return this.fakeBlockLoc;
  }
}
