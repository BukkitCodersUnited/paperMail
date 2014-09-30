package com.cmg.inputgui.plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.injector.BukkitUnwrapper;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.nbt.NbtBase;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class InputGuiUtils
{
  public static PacketContainer getSignPacket(Location location, String[] text)
  {
    PacketContainer packet = new PacketContainer(PacketType.Play.Server.UPDATE_SIGN);
    
    String[] text1 = new String[4];
    for (int i = 0; i < text1.length; i++) {
      if (i < text.length) {
        text1[i] = text[i];
      } else {
        text1[i] = "";
      }
    }
    packet.getIntegers().write(0, Integer.valueOf(location.getBlockX()));
    packet.getIntegers().write(1, Integer.valueOf(location.getBlockY()));
    packet.getIntegers().write(2, Integer.valueOf(location.getBlockZ()));
    packet.getStringArrays().write(0, text);
    
    return packet;
  }
  
  public static PacketContainer getCloseGuiPacket()
  {
    PacketContainer packet = new PacketContainer(PacketType.Play.Server.CLOSE_WINDOW);
    packet.getIntegers().write(0, Integer.valueOf(0));
    return packet;
  }
  
  public static PacketContainer getTileDataPacket(Location location, String text)
  {
    PacketContainer packet = new PacketContainer(PacketType.Play.Server.TILE_ENTITY_DATA);
    
    List<NbtBase<?>> tags = new ArrayList<NbtBase<?>>();
    tags.add(NbtFactory.of("id", "Control"));
    tags.add(NbtFactory.of("Command", text == null ? "" : text));
    tags.add(NbtFactory.of("x", location.getBlockX()));
    tags.add(NbtFactory.of("y", location.getBlockY()));
    tags.add(NbtFactory.of("z", location.getBlockZ()));
    
    packet.getIntegers().write(0, Integer.valueOf(location.getBlockX()));
    packet.getIntegers().write(1, Integer.valueOf(location.getBlockY()));
    packet.getIntegers().write(2, Integer.valueOf(location.getBlockZ()));
    packet.getIntegers().write(3, Integer.valueOf(2));
    packet.getNbtModifier().write(0, NbtFactory.ofCompound("", tags));
    
    return packet;
  }
  
  public static PacketContainer getOpenGuiPacket(Location location)
  {
    if (ProtocolLibrary.getProtocolManager().getMinecraftVersion().getVersion().startsWith("1.6."))
    {
      PacketContainer packet = new PacketContainer(
        PacketType.Play.Server.OPEN_SIGN_ENTITY);
      
      packet.getIntegers().write(0, Integer.valueOf(0));
      packet.getIntegers().write(1, Integer.valueOf(location.getBlockX()));
      packet.getIntegers().write(2, Integer.valueOf(location.getBlockY()));
      packet.getIntegers().write(3, Integer.valueOf(location.getBlockZ()));
      
      return packet;
    }
    PacketContainer packet = new PacketContainer(
      PacketType.Play.Server.OPEN_SIGN_ENTITY);
    
    packet.getIntegers().write(0, Integer.valueOf(location.getBlockX()));
    packet.getIntegers().write(1, Integer.valueOf(location.getBlockY()));
    packet.getIntegers().write(2, Integer.valueOf(location.getBlockZ()));
    
    return packet;
  }
  
  public static PacketContainer getSetSlotPacket(InventoryView view, int slot, ItemStack item)
  {
    PacketContainer packet = new PacketContainer(PacketType.Play.Server.SET_SLOT);
    
    packet.getIntegers().write(0, Integer.valueOf(getWindowId(view)));
    packet.getIntegers().write(1, Integer.valueOf(slot));
    packet.getItemModifier().write(0, item);
    
    return packet;
  }
  
  public static int getWindowId(InventoryView view)
  {
    try
    {
      Object container = new BukkitUnwrapper().unwrapItem(view);
      return container.getClass().getField("windowId").getInt(container);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return 0;
  }
  
  public static ItemStack getResult(AnvilInventory inventory)
  {
    try
    {
      Object handle = inventory.getClass()
        .getMethod("getResultInventory", new Class[0])
        .invoke(inventory, new Object[0]);
      Object item = handle.getClass().getMethod("getItem", new Class[] { Integer.TYPE })
        .invoke(handle, new Object[] { Integer.valueOf(0) });
      return item == null ? null : 
        MinecraftReflection.getBukkitItemStack(item);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  public static void setItemName(InventoryView view, String name)
  {
    if (!(view.getTopInventory() instanceof AnvilInventory)) {
      return;
    }
    Object container = new BukkitUnwrapper().unwrapItem(view);
    try
    {
      container.getClass().getMethod("a", new Class[] { String.class }).invoke(container, new Object[] { getResult(name) });
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static String getResult(String string)
  {
    StringBuilder builder = new StringBuilder();
    for (char character : string.toCharArray()) {
      if (isAllowedChatCharacter(character)) {
        builder.append(character);
      }
    }
    return builder.toString();
  }
  
  public static boolean isAllowedChatCharacter(char character)
  {
    return (character >= ' ') && (character != '');
  }
}
