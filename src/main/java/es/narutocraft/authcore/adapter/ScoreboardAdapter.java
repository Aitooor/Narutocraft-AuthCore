package es.narutocraft.authcore.adapter;

import es.narutocraft.authcore.AuthCore;
import es.narutocraft.authcore.utils.MessageUtil;
import es.narutocraft.authcore.utils.scoreboard.AssembleAdapter;
import es.narutocraft.authcore.utils.scoreboard.AssembleStyle;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ScoreboardAdapter implements AssembleAdapter {

    FileConfiguration config = AuthCore.getInstance().getConfig();

    @Override
    public String getTitle(Player player) {
        return MessageUtil.translate(config.getString("SCOREBOARD.TITLE"));
    }

    @Override
    public List<String> getLines(Player player) {
        String name = "&f%player_name%";
        name = PlaceholderAPI.setPlaceholders(player.getPlayer(), name);
        String rank = "%vault_prefix%";
        rank = PlaceholderAPI.setPlaceholders(player.getPlayer(), rank);

        return MessageUtil.translate(AuthCore.getInstance().getMessageHandler().replacingMany(player, "SCOREBOARD.LINES", "%rank%", rank, "%player%", name));
    }

    @Override
    public AssembleStyle getStyle() {
        return AssembleStyle.MODERN;
    }

}
