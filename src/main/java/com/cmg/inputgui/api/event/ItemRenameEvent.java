package com.cmg.inputgui.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;

public class ItemRenameEvent
  extends InventoryEvent
  implements Cancellable
{
  private static final HandlerList handlers = new HandlerList();
  private Player player;
  private String newName;
  private String oldName;
  private boolean cancel;
  private boolean reset;
  
  public ItemRenameEvent(Player player, InventoryView view, String oldName, String newName)
  {
    super(view);
    this.newName = newName;
    this.oldName = oldName;
    this.player = player;
  }
  
  public Player getPlayer()
  {
    return this.player;
  }
  
  public AnvilInventory getInventory()
  {
    return (AnvilInventory)getView().getTopInventory();
  }
  
  public String getOldName()
  {
    return this.oldName;
  }
  
  public String getNewName()
  {
    return this.newName;
  }
  
  public void setNewName(String name)
  {
    this.newName = name;
  }
  
  public boolean isResetted()
  {
    return this.reset;
  }
  
  public void setResetted(boolean reset)
  {
    this.reset = reset;
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
