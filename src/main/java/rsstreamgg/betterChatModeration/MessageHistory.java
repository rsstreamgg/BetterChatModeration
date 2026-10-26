package rsstreamgg.betterChatModeration;

import org.bukkit.entity.Player;

import java.util.*;

public class MessageHistory {
    public record MessageEntry(String message, long timestamp) {}
    private final Map<UUID, Deque<MessageEntry>> messages = new HashMap<>();

    public long HISTORY_DURATION = 5 * 60 * 1000L;
    public int MAX_MESSAGES = 50;

    public synchronized void addMessage(Player player, String message) {
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        Deque<MessageEntry> history = messages.computeIfAbsent(uuid, k -> new ArrayDeque<>());

        history.addLast(new MessageEntry(message, now));

        while (!history.isEmpty() && (now - history.peekFirst().timestamp() > HISTORY_DURATION || history.size() > MAX_MESSAGES)) history.removeFirst();
    }

    public synchronized List<MessageEntry> getRecentMessages(Player player, long duration) {
        UUID uuid = player.getUniqueId();
        long cutoff = System.currentTimeMillis() - duration;
        Deque<MessageEntry> history = messages.get(uuid);

        if (history == null) return List.of();

        return history.stream().filter(entry -> entry.timestamp() >= cutoff).toList();
    }

    public synchronized void clearMessages(Player player) {
        messages.remove(player.getUniqueId());
    }
}