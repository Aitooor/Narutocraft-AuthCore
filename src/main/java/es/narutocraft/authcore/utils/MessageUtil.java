package es.narutocraft.authcore.utils;

import java.util.List;
import java.util.ArrayList;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageUtil {

    public String translate(String string) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', string);
    }

    public List<String> translate(List<String> messages) {
        List<String> buffered = new ArrayList<>();
        messages.forEach(message -> {
            buffered.add(translate(message));
        });
        return buffered;
    }
}
