package com.cmg.inputgui.api.event;

import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CommandBlockEditEvent
  extends Event
  implements Cancellable
{
  private static final HandlerList handlers = new HandlerList();
  private Player player;
  private CommandBlock block;
  private String newCommand;
  private String oldCommand;
  private boolean cancel;
  
  public CommandBlockEditEvent(Player player, CommandBlock block, String oldCommand, String newCommand)
  {
    this.player = player;
    this.block = block;
    this.oldCommand = oldCommand;
    this.newCommand = newCommand;
  }
  
  public Player getPlayer()
  {
    return this.player;
  }
  
  public CommandBlock getBlock()
  {
    return this.block;
  }
  
  public String getOldCommand()
  {
    return this.oldCommand;
  }
  
  public String getNewCommand()
  {
    return this.newCommand;
  }
  
  public void setNewCommand(String command)
  {
    this.newCommand = command;
  }
  
  public boolean isCancelled()
  {
    return this.cancel;
  }
  
  public void setCancelled(boolean cancel)
  {
    this.cancel = cancel;
  }
  
  public HandlerList getHandlers()
  {
    return handlers;
  }
  
  public static HandlerList getHandlerList()
  {
    return handlers;
  }
}
