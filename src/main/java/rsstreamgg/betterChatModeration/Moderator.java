package rsstreamgg.betterChatModeration;

import org.bukkit.entity.Player;

import java.util.*;
import java.util.regex.Pattern;

public class Moderator {
    private static final Pattern URL_PATTERN = Pattern.compile("\\b(?:https?://)?(?:[a-z0-9-]+\\s*\\.\\s*)+[a-z]{2,6}\\b");
    private static final Pattern IP_ADDRESS_PATTERN = Pattern.compile("\\b[0-9]{1,3}\\s*\\.\\s*[0-9]{1,3}\\s*\\.\\s*[0-9]{1,3}\\s*\\.\\s*[0-9]");
    private static final Pattern DISCORD_INVITE = Pattern.compile("(?i)(?:https?://)?(?:www\\.)?(?:discord\\.gg/|discord(?:app)?\\.com/invite/)[a-zA-Z0-9-]+");
    private static final String[] MIGRATION_PHRASES = { "dm me on discord", "message me on discord", "add me on discord", "join my discord", "talk to me on discord instead", "message me on telegram", "add me on snapchat", "add me snap", "snap me", "add me on snap" };
    private static final String[] SECRECY_PHRASES = { "don't tell staff", "dont tell staff", "staff can't see", "staff cant see", "take this private" };

    public boolean detect(Player player, String message) {
        // Returns allow_message: true if the message is allowed to be seen by the public, otherwise false.

        boolean allow_message = true;

        if (message.trim().equals("")) return false;

        if (player.hasPermission(BetterChatModeration.FLAGS_EXCLUDE_PERMISSION)) return allow_message;

        BetterChatModeration.messageHistory.addMessage(player, message);

        // Off-platform
        if (detectOffPlatform(message)) {
            BetterChatModeration.flagManager.flagPlayer(player, FlagManager.FLAGS.OFF_PLATFORM);
            allow_message = false;
        }

        // Link Checks
        if (detectURL(message)) {
            BetterChatModeration.flagManager.flagPlayer(player, FlagManager.FLAGS.LINKS);
            allow_message = false;
        }

        // IP Address Checks
        if (detectIPaddress(message)) {
            BetterChatModeration.flagManager.flagPlayer(player, FlagManager.FLAGS.IP_ADDRESS);
            allow_message = false;
        }

        return allow_message;
    }

    public boolean detectOffPlatform(String message) {
        String normalised = message.toLowerCase().replaceAll("[\\p{Cf}\\p{Zs}]", "");
        int score = 0;

        // Invite Link
        if (DISCORD_INVITE.matcher(normalised).find()) score += 3;

        // Asking to move platforms
        for (String phrase : MIGRATION_PHRASES) {
            if (normalised.contains(phrase)) {
                score += 2;
                break;
            }
        }

        // Avoiding moderation
        for (String phrase : SECRECY_PHRASES) {
            if (normalised.contains(phrase)) {
                score += 2;
                break;
            }
        }

        return score >= 3;
    }

    public boolean detectURL(String message) {
        if (message == null || message.trim().isEmpty()) {
            return false;
        }

        String normalised = message.toLowerCase().replaceAll("\\p{Cf}", "");

        return URL_PATTERN.matcher(normalised).find();
    }

    public boolean detectIPaddress(String message) {
        if (message == null || message.trim().isEmpty()) {
            return false;
        }

        String normalised = message.toLowerCase().replaceAll("\\p{Cf}", "");

        return IP_ADDRESS_PATTERN.matcher(normalised).find();
    }
}
