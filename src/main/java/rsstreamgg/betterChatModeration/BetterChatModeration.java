package rsstreamgg.betterChatModeration;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class BetterChatModeration extends JavaPlugin implements Listener {
    public static FlagManager flagManager = new FlagManager();
    public static Moderator moderator = new Moderator();
    public static MessageHistory messageHistory = new MessageHistory();

    public static String FLAGS_EXCLUDE_PERMISSION = "bcm.exclude";
    public static String LOGS_PERMISSION = "bcm.logs";

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("BetterChatModeration enabled!");
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        String message = PlainTextComponentSerializer.plainText().serialize(event.message());
        event.setCancelled(!moderator.detect(event.getPlayer(), message));
    }

    @Override
    public void onDisable() {
        getLogger().info("BetterChatModeration disabled!");
    }
}
