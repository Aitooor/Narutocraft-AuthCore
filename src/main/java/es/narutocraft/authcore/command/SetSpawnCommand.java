package es.narutocraft.authcore.command;

import es.narutocraft.authcore.AuthPlugin;
import es.narutocraft.authcore.utils.LocationUtil;
import es.narutocraft.authcore.utils.MessageUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SetSpawnCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (!player.isOp()) {
            return true;
        }

        AuthPlugin.getInstance().getConfig().set("LOCATION.SPAWN", LocationUtil.parseToString(player.getLocation()));
        AuthPlugin.getInstance().saveConfig();
        AuthPlugin.getInstance().reloadConfig();

        player.sendMessage(MessageUtil.translate("&aThe spawn has been set."));
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
        return true;
    }

}
