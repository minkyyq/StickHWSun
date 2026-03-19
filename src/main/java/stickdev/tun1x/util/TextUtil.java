package stickdev.tun1x.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class TextUtil {

    private static final Pattern HEX = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static BukkitAudiences adventure;
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    public static void init(BukkitAudiences audiences) {
        adventure = audiences;
    }

    public static String color(String input) {
        if (input == null) return "";
        input = processHex(input);
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    private static String processHex(String input) {
        Matcher m = HEX.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String code = m.group(1);
            StringBuilder rep = new StringBuilder("§x");
            for (char ch : code.toCharArray()) {
                rep.append('§').append(ch);
            }
            m.appendReplacement(sb, rep.toString());
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public static void sendMiniMessage(Player player, String message) {
        if (adventure == null) {
            player.sendMessage(color(message));
            return;
        }
        adventure.player(player).sendMessage(MINI_MESSAGE.deserialize(message));
    }

    public static String miniToLegacy(String miniMessage) {
        return LegacyComponentSerializer.legacySection().serialize(MINI_MESSAGE.deserialize(miniMessage));
    }

    public static void close() {
        if (adventure != null) {
            adventure.close();
        }
    }
}
