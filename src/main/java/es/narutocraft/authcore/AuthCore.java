package es.narutocraft.authcore;

import es.narutocraft.authcore.command.ReloadCommand;
import es.narutocraft.authcore.utils.scoreboard.Assemble;
import me.yushust.message.MessageProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.entity.Player;

import es.narutocraft.authcore.command.SetSpawnCommand;
import es.narutocraft.authcore.listeners.PlayerListener;

import es.narutocraft.authcore.adapter.ScoreboardAdapter;


import me.yushust.message.MessageHandler;
import me.yushust.message.bukkit.BukkitMessageAdapt;
import me.yushust.message.source.MessageSourceDecorator;

import lombok.Getter;

import java.io.File;

public class AuthCore extends JavaPlugin {

    @Getter private static AuthCore instance;

    @Getter private MessageHandler messageHandler;

    @Override
    public void onLoad() {
        instance = this;
        this.saveDefaultConfig();
        this.loadLanguages();
    }

    @Override
    public void onEnable() {
        instance = this;

        Bukkit.getServer().getLogger().info("[AuthCore] Enabling...");
        this.getCommand("core").setExecutor(new ReloadCommand());
        this.getCommand("setspawn").setExecutor(new SetSpawnCommand());

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        } else {
            Bukkit.getPluginManager().disablePlugin(this);
        }

        this.registerScoreboard();
        Bukkit.getServer().getLogger().info("[AuthCore] Enabled correctly");
    }

    private void registerScoreboard() {
        Assemble scoreboard = new Assemble(this, new ScoreboardAdapter());
        scoreboard.setTicks(getConfig().getLong("SCOREBOARD.TICKS"));
    }

    private void loadLanguages() {
        File langFolder = new File(this.getDataFolder(), "lang");

        try {
            langFolder.mkdir();
        } catch (Exception e) {
            e.printStackTrace();
        }

        MessageProvider messageProvider = MessageProvider
                .create(
                        MessageSourceDecorator
                                .decorate(BukkitMessageAdapt.newYamlSource(this, "lang/lang_%lang%.yml"))
                                .addFallbackLanguage("es")
                                .get(),
                        config -> {
                            config.specify(Player.class)
                                    .setLinguist(player -> player.spigot().getLocale().split("_")[0])
                                    .setMessageSender((sender, mode, message) -> sender.sendMessage(message));
                            config.specify(CommandSender.class)
                                    .setLinguist(commandSender -> "es")
                                    .setMessageSender((sender, mode, message) -> sender.sendMessage(message));
                            config.addInterceptor(s -> ChatColor.translateAlternateColorCodes('&', s));
                        }
                );

        messageHandler = MessageHandler.of(messageProvider);
    }

    private void loadFiles(String... files) {
        for (String name : files) {
            if (this.getResource(name) != null) {
                this.saveResource(name, false);
            }
        }
    }

}
