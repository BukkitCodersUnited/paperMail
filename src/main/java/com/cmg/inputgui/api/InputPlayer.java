package com.cmg.inputgui.api;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public abstract interface InputPlayer
{
  public abstract Player getPlayer();
  
  public abstract boolean isGuiOpen();
  
  public abstract InputGuiBase<?> getCurrentGui();
  
  public abstract boolean closeGui();
  
  public abstract void openGui(InputGuiBase<?> paramInputGuiBase);
  
  public abstract void openGui(InputGuiBase<?> paramInputGuiBase, int paramInt1, int paramInt2);
  
  public abstract void openTileEditor(Block paramBlock);
}
