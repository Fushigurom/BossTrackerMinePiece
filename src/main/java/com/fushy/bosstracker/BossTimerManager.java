package com.fushy.bosstracker;

import com.fushy.bosstracker.config.BossConfig;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BossTimerManager {

    // DOTALL pour matcher même si le message contient des \n
    private static final Pattern KILL_PATTERN = Pattern.compile(
            "-=\\s*Classement\\s+(.+?)\\s*=-",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    // displayName → timestamp (en ms) où le boss respawn
    private static final Map<String, Long> activeTimers = new ConcurrentHashMap<>();

    /**
     * Analyse un message du chat.
     * Si le format "-= Classement X =-" est détecté, démarre un timer pour X.
     * @return le nom du boss détecté, ou null si rien trouvé
     */
    public static String handleChatMessage(String chatMessage) {
        // Strip les codes couleur Minecraft (§X) au cas où
        String clean = chatMessage.replaceAll("§[0-9a-fk-or]", "");

        Matcher matcher = KILL_PATTERN.matcher(clean);
        if (!matcher.find()) return null;

        String bossName = matcher.group(1).trim();

        // Sécurité : le nom ne doit pas contenir de \n (on prend juste la première ligne)
        if (bossName.contains("\n")) {
            bossName = bossName.substring(0, bossName.indexOf("\n")).trim();
        }

        int seconds = BossConfig.getRespawnSeconds(bossName);
        long respawnAt = System.currentTimeMillis() + (seconds * 1000L);

        activeTimers.put(bossName, respawnAt);

        String known = BossConfig.isKnownBoss(bossName) ? "" : " (inconnu → timer défaut)";
        System.out.println("[BossTracker] ✓ Timer démarré pour '" + bossName
                + "' — " + seconds / 60 + " min" + known);
        return bossName;
    }

    /** Reset tous les timers (changement de serveur ou commande manuelle). */
    public static void resetAll() {
        activeTimers.clear();
        System.out.println("[BossTracker] Tous les timers ont été réinitialisés.");
    }

    /**
     * Retourne la liste des timers actifs, triée par temps restant croissant.
     */
    public static List<TimerEntry> getActiveTimers() {
        List<TimerEntry> result = new ArrayList<>();
        long now = System.currentTimeMillis();

        for (Map.Entry<String, Long> entry : activeTimers.entrySet()) {
            long remaining = Math.max(0, entry.getValue() - now);
            result.add(new TimerEntry(entry.getKey(), remaining));
        }

        result.sort(Comparator.comparingLong(e -> e.remainingMs));
        return result;
    }

    /** Supprime les timers expirés depuis plus de 30 secondes. */
    public static void cleanExpired() {
        long now = System.currentTimeMillis();
        activeTimers.entrySet().removeIf(e -> (now - e.getValue()) > 30_000);
    }

    public static boolean hasActiveTimers() {
        return !activeTimers.isEmpty();
    }

    public record TimerEntry(String displayName, long remainingMs) {
        public String formatTime() {
            if (remainingMs <= 0) return "SPAWN!";
            long seconds = remainingMs / 1000;
            long minutes = seconds / 60;
            long secs = seconds % 60;
            return String.format("%02d:%02d", minutes, secs);
        }
    }
}
