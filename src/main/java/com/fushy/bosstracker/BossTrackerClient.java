package com.fushy.bosstracker;

import com.fushy.bosstracker.config.BossConfig;
import com.fushy.bosstracker.hud.BossHudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class BossTrackerClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Chargement de la config (boss names + timers)
        BossConfig.load();

        // Enregistrement du HUD
        HudRenderCallback.EVENT.register(new BossHudRenderer());

        // Détection de changement de serveur
        ServerChangeDetector.register();

        // Commandes client
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            BossCommand.register(dispatcher);
        });

        System.out.println("[BossTracker] Mod initialisé ! Prêt pour MinePiece.");
    }
}
