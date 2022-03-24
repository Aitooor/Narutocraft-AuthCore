package es.narutocraft.authcore;

import es.narutocraft.authcore.utils.MessageUtil;
import es.narutocraft.authcore.utils.scoreboard.Assemble;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.entity.Player;

import es.narutocraft.authcore.command.SetSpawnCommand;
import es.narutocraft.authcore.listeners.PlayerListener;

import es.narutocraft.authcore.adapter.ScoreboardAdapter;

import es.narutocraft.authcore.nmessage.UserLinguist;
import es.narutocraft.authcore.nmessage.UserMessageSender;

import me.yushust.message.MessageHandler;
import me.yushust.message.source.MessageSource;
import me.yushust.message.bukkit.BukkitMessageAdapt;
import me.yushust.message.source.MessageSourceDecorator;

import lombok.Getter;

import java.io.File;

public class AuthPlugin extends JavaPlugin {

    @Getter private static AuthPlugin instance;

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

        this.getCommand("setspawn").setExecutor(new SetSpawnCommand());

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        } else {
            Bukkit.getPluginManager().disablePlugin(this);
        }

        this.registerScoreboard();
    }

    private void registerScoreboard() {
        Assemble scoreboard = new Assemble(this, new ScoreboardAdapter());
        scoreboard.setTicks(getConfig().getLong("SCOREBOARD.TICKS"));
    }

    private void loadLanguages() {
        MessageSourceDecorator messageSourceDecorator = MessageSourceDecorator
                .decorate(BukkitMessageAdapt
                        .newYamlSource(this, new File(getDataFolder(), "languages")));
        
        MessageSource messageSource = messageSourceDecorator
                .addFallbackLanguage("es").get();

        this.loadFiles("languages/lang_es.yml");
        
        this.messageHandler = MessageHandler.of(
                messageSource,
                config -> {
                    config.addInterceptor(MessageUtil::translate);

                    config.specify(Player.class)
                            .setMessageSender(new UserMessageSender())
                            .setLinguist(new UserLinguist());
                }
        );
    }

    private void loadFiles(String... files) {
        for (String name : files) {
            if (this.getResource(name) != null) {
                this.saveResource(name, false);
            }
        }
    }

}
