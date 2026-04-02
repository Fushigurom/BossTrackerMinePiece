package com.fushy.bosstracker;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

/**
 * Détecte le changement de serveur uniquement via les events réseau Fabric.
 * Beaucoup plus fiable que la détection par chat.
 */
public class ServerChangeDetector {

    public static void register() {
        // Déclenché à chaque vraie déconnexion réseau
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            System.out.println("[BossTracker] Déconnexion détectée → Reset des timers");
            BossTimerManager.resetAll();
        });

        // Déclenché à chaque vraie connexion à un serveur
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            System.out.println("[BossTracker] Connexion détectée → Reset des timers");
            BossTimerManager.resetAll();
        });
    }
}
