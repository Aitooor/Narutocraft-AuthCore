package es.narutocraft.authcore.nmessage;

import me.yushust.message.bukkit.SpigotLinguist;
import me.yushust.message.language.Linguist;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class UserLinguist implements Linguist<Player> {

    private final SpigotLinguist spigotLinguist;

    public UserLinguist(){
        this.spigotLinguist = new SpigotLinguist();
    }

    @Override
    public @Nullable String getLanguage(Player player) {
        return spigotLinguist.getLanguage(player);
    }
}
