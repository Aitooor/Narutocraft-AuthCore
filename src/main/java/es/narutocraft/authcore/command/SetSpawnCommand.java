package es.narutocraft.authcore.command;

import es.narutocraft.authcore.AuthCore;
import es.narutocraft.authcore.utils.LocationUtil;
import me.yushust.message.MessageHandler;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SetSpawnCommand implements CommandExecutor {

    private MessageHandler messageHandler = AuthCore.getInstance().getMessageHandler();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        if (args.length == 0) {
            messageHandler.sendReplacing(sender, "CORE.SETSPAWN.USAGE");
            return true;
        }

        if (args.length == 1 && args[0].equals("setspawn")) {
            Player player = (Player) sender;

            if (!player.isOp() || !player.hasPermission("hubcore.setspawn")) {
                return true;
            }

            AuthCore.getInstance().getConfig().set("LOCATION.SPAWN", LocationUtil.parseToString(player.getLocation()));
            AuthCore.getInstance().saveConfig();
            AuthCore.getInstance().reloadConfig();

            messageHandler.sendReplacing(sender, "CORE.SETSPAWN.PLAYER");
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
            return true;
        }

        return true;
    }
}
