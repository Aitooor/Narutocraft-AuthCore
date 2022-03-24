package es.narutocraft.authcore.nmessage;

import es.narutocraft.authcore.utils.MessageUtil;
import me.yushust.message.send.MessageSender;

import org.bukkit.entity.Player;

public class UserMessageSender implements MessageSender<Player> {

    @Override
    public void send(Player user, String mode, String message) {
        user.sendMessage(MessageUtil.translate(message));
    }
}
