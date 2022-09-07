package es.narutocraft.authcore.listeners;

import es.narutocraft.authcore.AuthCore;
import es.narutocraft.authcore.utils.CenteredMessage;
import es.narutocraft.authcore.utils.LocationUtil;
import es.narutocraft.authcore.utils.MessageUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import me.yushust.message.MessageHandler;

import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;

import org.bukkit.block.Block;

import org.bukkit.entity.Player;

import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.EventHandler;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import org.bukkit.event.server.PluginEnableEvent;

import org.bukkit.event.weather.WeatherChangeEvent;

import org.bukkit.event.inventory.InventoryClickEvent;

import org.bukkit.configuration.file.FileConfiguration;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;

import java.lang.reflect.Field;

public class PlayerListener implements Listener {

    private final AuthCore plugin;

    public PlayerListener(AuthCore instance) {
        plugin = instance;
    }

    FileConfiguration config = AuthCore.getInstance().getConfig();

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Bukkit.getScheduler().runTaskLater(
                plugin,
                () -> {
                    Player player = event.getPlayer();
                    if (player == null) {
                        return;
                    }

                    sendTab(player);

                    String name = "&f%player_name% ";
                    name = PlaceholderAPI.setPlaceholders(event.getPlayer(), name);
                    String rank = "%vault_prefix% ";
                    rank = PlaceholderAPI.setPlaceholders(event.getPlayer(), rank);

                    CenteredMessage.Chat.sendCenteredMessage(player, "");
                    CenteredMessage.Chat.sendCenteredMessage(player, config.getString("ONJOIN.TITLE"));
                    CenteredMessage.Chat.sendCenteredMessage(player, "");
                    CenteredMessage.Chat.sendCenteredMessage(player, plugin.getMessageHandler().replacing(player, "ONJOIN.MESSAGE"));
                    CenteredMessage.Chat.sendCenteredMessage(player, "");
                    CenteredMessage.Chat.sendCenteredMessage(player, plugin.getMessageHandler().replacing(player, "ONJOIN.ONE"));
                    CenteredMessage.Chat.sendCenteredMessage(player, "");
                    CenteredMessage.Chat.sendCenteredMessage(player, rank + name + plugin.getMessageHandler().replacing(player, "ONJOIN.TWO"));

                }, 2);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        event.setJoinMessage(null);
        
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.getActivePotionEffects().clear();

        tpSpawn(player);

        Bukkit.getOnlinePlayers().stream().map(online -> {
            player.hidePlayer(online);
            return online;
        }).forEachOrdered(online -> {
            online.hidePlayer(player);
        });
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!player.isOp()) {
            if ((event.getFrom().getX() != event.getTo().getX() || event.getFrom().getZ() != event.getTo().getZ())) {
                player.teleport(event.getFrom());
            }
            
            return;
        }

        if (player.getLocation().getBlockY() < 0) {
            tpSpawn(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (!event.getPlayer().isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (!(event.getPlayer().getGameMode() == GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handleEnter(PlayerCommandPreprocessEvent event) {
        if (!event.getPlayer().isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (!(event.getPlayer().getGameMode() == GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (!(event.getPlayer().getGameMode() == GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickItem(PlayerPickupItemEvent event) {
        if (!(event.getPlayer().getGameMode() == GameMode.CREATIVE)) {
            event.setCancelled(true);
            event.getItem().remove();
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked().getGameMode() == GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasBlock()) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block != null && !(event.getPlayer().getGameMode() == GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setFoodLevel(20);
        event.setCancelled(true);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        event.setDroppedExp(0);
        event.getDrops().clear();
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (event.getPlayer() != null) {
            tpSpawn(event.getPlayer());
        }
    }

    @EventHandler
    public void onPluginLoad(PluginEnableEvent event) {

        if (config.getString("LOCATION.SPAWN") == null) {
            return;
        }

        World lobbyWorld = Bukkit.getServer().getWorld(config.getString("LOCATION.SPAWN").split(", ")[5]);

        lobbyWorld.setGameRuleValue("doDaylightCycle", "false");
        lobbyWorld.setTime(3600);
        lobbyWorld.setStorm(false);
        lobbyWorld.setWeatherDuration(0);
        lobbyWorld.setAnimalSpawnLimit(0);
        lobbyWorld.setAmbientSpawnLimit(0);
        lobbyWorld.setMonsterSpawnLimit(0);
        lobbyWorld.setWaterAnimalSpawnLimit(0);
    }

    private void tpSpawn(Player player) {
        if (LocationUtil.parseToLocation(config.getString("LOCATION.SPAWN")) == null || config.getString("LOCATION.SPAWN") == null) {
            return;
        }

        player.teleport(LocationUtil.parseToLocation(config.getString("LOCATION.SPAWN")));
    }

    private void sendTab(Player player) {

        MessageHandler message = AuthCore.getInstance().getMessageHandler();

        String bungee_total = "%bungee_total%";
        bungee_total = PlaceholderAPI.setPlaceholders(player.getPlayer(), bungee_total);

        String header = MessageUtil.translate((message.replacing(player, "TABLIST.HEADER", "%bonline%", bungee_total) == null ? "" : message.replacing(player, "TABLIST.HEADER", "%bonline%", bungee_total)));

        String playerListNames = "%vault_prefix% %player_name%";
        playerListNames = PlaceholderAPI.setPlaceholders(player.getPlayer(), playerListNames);
        player.setPlayerListName(playerListNames);

        String footer = MessageUtil.translate((message.replacing(player, "TABLIST.FOOTER") == null ? "" : message.replacing(player, "TABLIST.FOOTER")));

        PacketPlayOutPlayerListHeaderFooter headerPacket = new PacketPlayOutPlayerListHeaderFooter(ChatSerializer.a("{\"text\":\"" + header + "\"}"));

        try {

            Field field = headerPacket.getClass().getDeclaredField("b");
            field.setAccessible(true);
            field.set(headerPacket, ChatSerializer.a("{\"text\":\"" + footer + "\"}"));

        } catch (Exception exception) {

            exception.printStackTrace();

        } finally {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(headerPacket);
        }
    }
}
