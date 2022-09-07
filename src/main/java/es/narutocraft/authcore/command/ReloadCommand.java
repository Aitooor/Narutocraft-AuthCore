package es.narutocraft.authcore.command;

import es.narutocraft.authcore.AuthCore;
import me.yushust.message.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;

public class ReloadCommand implements CommandExecutor {

    private MessageHandler messageHandler = AuthCore.getInstance().getMessageHandler();
    private AuthCore plugin = AuthCore.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            messageHandler.sendReplacing(sender, "CORE.HELP");
            return true;
        }

        if (args.length == 1 && args[0].equals("reload")) {
            if (sender instanceof CommandSender) {
                plugin.reloadConfig();
                messageHandler.getSource().load("es");

                Bukkit.getServer().getLogger().info("[AuthCore] Reloaded successfully");
                return true;
            }

            Player player = (Player) sender;

            if (!player.isOp() || !player.hasPermission("hubcore.reload")) {
                return true;
            }

            if(sender instanceof Player) {
                player = Bukkit.getPlayer(player.getUniqueId());

                if(player == null) return true;

                plugin.reloadConfig();
                messageHandler.getSource().load("es");

                messageHandler.sendReplacing(player, "CORE.RELOAD");

                return true;
            }
        }

        return true;
    }

}
