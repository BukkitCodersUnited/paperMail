package com.cmg.inputgui.plugin;

import com.cmg.inputgui.api.InputGuiSign;
import com.cmg.inputgui.api.event.CommandBlockEditEvent;
import com.cmg.inputgui.api.event.ItemRenameEvent;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.ListeningWhitelist.Builder;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.injector.GamePhase;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.google.common.base.Charsets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

public class InputGuiPacketListener
  implements PacketListener
{
  private InputGuiPlugin plugin;
  
  public InputGuiPacketListener(InputGuiPlugin plugin)
  {
    this.plugin = plugin;
    ProtocolLibrary.getProtocolManager().addPacketListener(this);
  }
  
  public ListeningWhitelist getSendingWhitelist()
  {
    return 
    





      ListeningWhitelist.newBuilder().normal().gamePhase(GamePhase.PLAYING).options(new ListenerOptions[0]).types(new PacketType[] { PacketType.Play.Server.BLOCK_CHANGE, PacketType.Play.Server.BLOCK_CHANGE, PacketType.Play.Server.CLOSE_WINDOW, PacketType.Play.Server.OPEN_WINDOW, PacketType.Play.Server.RESPAWN }).build();
  }
  
  public ListeningWhitelist getReceivingWhitelist()
  {
    return 
    







      ListeningWhitelist.newBuilder().normal().gamePhase(GamePhase.PLAYING).options(new ListenerOptions[0]).types(new PacketType[] { PacketType.Play.Client.CUSTOM_PAYLOAD, PacketType.Play.Client.CHAT, PacketType.Play.Client.ARM_ANIMATION, PacketType.Play.Client.BLOCK_PLACE, PacketType.Play.Client.WINDOW_CLICK, PacketType.Play.Client.USE_ENTITY, PacketType.Play.Client.BLOCK_DIG, PacketType.Play.Client.CLOSE_WINDOW, PacketType.Play.Client.SET_CREATIVE_SLOT, PacketType.Play.Client.UPDATE_SIGN }).build();
  }
  
  public Plugin getPlugin()
  {
    return this.plugin;
  }
  
  @SuppressWarnings("deprecation")
public void onPacketSending(PacketEvent e)
  {
    PacketContainer packet = e.getPacket();
    InputGuiPlayer player = this.plugin.getPlayer(e.getPlayer());
    if (!player.isGuiOpen()) {
      return;
    }
    PacketType type = e.getPacketType();
    if (type.equals(PacketType.Play.Server.BLOCK_CHANGE))
    {
      int x = ((Integer)packet.getIntegers().read(0)).intValue();
      int y = ((Integer)packet.getIntegers().read(1)).intValue();
      int z = ((Integer)packet.getIntegers().read(2)).intValue();
      Material material;
      if (ProtocolLibrary.getProtocolManager().getMinecraftVersion().getVersion().startsWith("1.6.")) {
        material = Material.getMaterial(((Integer)packet.getIntegers().read(3)).intValue());
      } else {
        material = (Material)packet.getBlocks().read(0);
      }
      Location l = player.getFakeBlockLocation();
      if ((l.getBlockX() == x) && (l.getBlockY() == y) && (l.getBlockZ() == z) && 
        (material != Material.COMMAND)) {
        e.setCancelled(true);
      }
    }
    else if ((player.isCheckingPackets()) && (
      (type.equals(PacketType.Play.Server.CLOSE_WINDOW)) || 
      (type.equals(PacketType.Play.Server.OPEN_WINDOW))))
    {
      player.setCancelled();
    }
  }
  
  public void onPacketReceiving(PacketEvent e)
  {
    PacketContainer packet = e.getPacket();
    
    Player player = e.getPlayer();
    InputGuiPlayer iplayer = this.plugin.getPlayer(player);
    
    PacketType type = e.getPacketType();
    if (type.equals(PacketType.Play.Client.CUSTOM_PAYLOAD))
    {
      String tag = (String)packet.getStrings().read(0);
      byte[] data = (byte[])packet.getByteArrays().read(0);
      if (tag.equals("MC|AdvCdm"))
      {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bis);
        try
        {
          byte action = 0;
          if (!ProtocolLibrary.getProtocolManager().getMinecraftVersion().getVersion().startsWith("1.6.")) {
            action = dis.readByte();
          }
          if (action == 1) {
            return;
          }
          int x = dis.readInt();
          int y = dis.readInt();
          int z = dis.readInt();
          String string;
          if (ProtocolLibrary.getProtocolManager().getMinecraftVersion().getVersion().startsWith("1.6."))
          {
            StringBuilder builder = new StringBuilder();
            
            short stringLength = dis.readShort();
            for (int i = 0; i < stringLength; i++) {
              builder.append(dis.readChar());
            }
            string = builder.toString();
          }
          else
          {
            byte[] bytes = new byte[dis.available()];
            dis.read(bytes);
            string = new String(bytes, Charsets.UTF_8).trim();
          }
          if ((iplayer.isGuiOpen()) && 
            (!(iplayer.getCurrentGui() instanceof InputGuiSign)))
          {
            Location l = iplayer.getFakeBlockLocation();
            if ((l == null) || (l.getBlockX() != x) || 
              (l.getBlockY() != y) || (l.getBlockZ() != z))
            {
              iplayer.setCancelled();
              return;
            }
            e.setCancelled(true);
            iplayer.setConfirmed(string);
          }
          else
          {
            Block block = e.getPlayer().getWorld()
              .getBlockAt(x, y, z);
            BlockState state = block.getState();
            if ((state instanceof CommandBlock))
            {
              CommandBlock cblock = (CommandBlock)state;
              CommandBlockEditEvent event = new CommandBlockEditEvent(
                player, cblock, cblock.getCommand(), string);
              Bukkit.getPluginManager().callEvent(event);
              if (event.isCancelled())
              {
                e.setCancelled(true);
                return;
              }
              String command = event.getNewCommand();
              
              ByteArrayOutputStream bos = new ByteArrayOutputStream();
              DataOutputStream dos = new DataOutputStream(bos);
              
              dos.writeInt(x);
              dos.writeInt(y);
              dos.writeInt(z);
              
              dos.writeShort(command.length());
              dos.writeChars(command);
              
              packet.getByteArrays().write(0, bos.toByteArray());
              
              dos.close();
              bos.close();
            }
          }
          dis.close();
          bis.close();
        }
        catch (IOException ex)
        {
          ex.printStackTrace();
        }
      }
      else if (tag.equals("MC|ItemName"))
      {
        InventoryView view = player.getOpenInventory();
        if ((view != null) && 
          ((view.getTopInventory() instanceof AnvilInventory)))
        {
          AnvilInventory inv = (AnvilInventory)view
            .getTopInventory();
          
          ItemStack renamed = inv.getItem(0);
          if (renamed == null) {
            return;
          }
          ItemMeta meta = renamed.getItemMeta();
          String oldName = (meta != null) && (meta.hasDisplayName()) ? meta
            .getDisplayName() : null;
          String newName = null;
          if (ProtocolLibrary.getProtocolManager().getMinecraftVersion().getVersion().startsWith("1.6.")) {
            newName = (data == null) || (data.length < 1) ? "" : 
              new String(data);
          } else {
            newName = (data == null) || (data.length < 1) ? "" : 
              new String(data, Charsets.UTF_8);
          }
          ItemRenameEvent event = new ItemRenameEvent(player, view, 
            oldName, newName);
          Bukkit.getPluginManager().callEvent(event);
          if ((event.isCancelled()) || (event.getNewName() == null))
          {
            new UpdateAnvilSlots(player, view);
            e.setCancelled(true);
            return;
          }
          if (event.isResetted()) {
            newName = "";
          } else {
            newName = event.getNewName();
          }
          InputGuiUtils.setItemName(view, newName);
          new UpdateAnvilSlots(player, view);
          e.setCancelled(true);
        }
      }
    }
    else if (type.equals(PacketType.Play.Client.UPDATE_SIGN))
    {
      int x = ((Integer)packet.getIntegers().read(0)).intValue();
      int y = ((Integer)packet.getIntegers().read(1)).intValue();
      int z = ((Integer)packet.getIntegers().read(2)).intValue();
      
      String[] lines = (String[])packet.getStringArrays().read(0);
      if ((iplayer.isGuiOpen()) && 
        ((iplayer.getCurrentGui() instanceof InputGuiSign)))
      {
        Location l = iplayer.getFakeBlockLocation();
        if ((l == null) || (l.getBlockX() != x) || (l.getBlockY() != y) || 
          (l.getBlockZ() != z))
        {
          iplayer.setCancelled();
          return;
        }
        e.setCancelled(true);
        iplayer.setConfirmed(lines);
      }
    }
    else if ((iplayer.isGuiOpen()) && 
      (iplayer.isCheckingPackets()) && (
      (type.equals(PacketType.Play.Client.CHAT)) || 
      (type.equals(PacketType.Play.Client.ARM_ANIMATION)) || 
      (type.equals(PacketType.Play.Client.BLOCK_PLACE)) || 
      (type.equals(PacketType.Play.Client.WINDOW_CLICK)) || 
      (type.equals(PacketType.Play.Client.USE_ENTITY)) || 
      (type.equals(PacketType.Play.Client.BLOCK_DIG)) || 
      (type.equals(PacketType.Play.Client.CLOSE_WINDOW)) || 
      (type.equals(PacketType.Play.Client.SET_CREATIVE_SLOT))))
    {
      iplayer.setCancelled();
    }
    if (type.equals(PacketType.Play.Client.WINDOW_CLICK))
    {
      InventoryView view = player.getOpenInventory();
      if ((view != null) && 
        ((view.getTopInventory() instanceof AnvilInventory)))
      {
        int slot = ((Integer)packet.getIntegers().read(1)).intValue();
        if (slot == 2) {
          new UpdateAnvilSlots(player, view);
        }
      }
    }
  }
  
  private class UpdateAnvilSlots
    extends BukkitRunnable
  {
    private InventoryView view;
    private Player player;
    
    public UpdateAnvilSlots(Player player, InventoryView view)
    {
      runTaskLater(InputGuiPacketListener.this.plugin, 1L);
      this.view = view;
      this.player = player;
    }
    
    public void run()
    {
      AnvilInventory inv = (AnvilInventory)this.view.getTopInventory();
      
      ItemStack item1 = inv.getItem(0);
      ItemStack item2 = inv.getItem(1);
      ItemStack item3 = InputGuiUtils.getResult(inv);
      
      PacketContainer packet1 = InputGuiUtils.getSetSlotPacket(this.view, 
        0, item1);
      PacketContainer packet2 = InputGuiUtils.getSetSlotPacket(this.view, 
        1, item2);
      PacketContainer packet3 = InputGuiUtils.getSetSlotPacket(this.view, 
        2, item3);
      try
      {
        ProtocolLibrary.getProtocolManager().sendServerPacket(
          this.player, packet1);
        ProtocolLibrary.getProtocolManager().sendServerPacket(
          this.player, packet2);
        ProtocolLibrary.getProtocolManager().sendServerPacket(
          this.player, packet3);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
  }
}
