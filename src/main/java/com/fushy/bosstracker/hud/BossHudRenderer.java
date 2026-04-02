package com.fushy.bosstracker.hud;

import com.fushy.bosstracker.BossTimerManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import java.util.List;

public class BossHudRenderer implements HudRenderCallback {

    // Position du HUD (coin haut-droite par défaut)
    private static final int MARGIN_RIGHT = 5;
    private static final int MARGIN_TOP = 5;
    private static final int ROW_HEIGHT = 12;
    private static final int PADDING_X = 6;
    private static final int PADDING_Y = 4;
    private static final int PANEL_WIDTH = 160;

    // Couleurs
    private static final int COLOR_TITLE = 0xFFFFAA00;       // Orange — titre "Boss Timers"
    private static final int COLOR_BOSS_NAME = 0xFFFFFFFF;   // Blanc — nom du boss
    private static final int COLOR_TIMER = 0xFF55FF55;       // Vert — timer en cours
    private static final int COLOR_SPAWN = 0xFFFF5555;       // Rouge — boss spawn !
    private static final int COLOR_BG = 0xAA000000;          // Fond semi-transparent

    @Override
    public void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        // Nettoyage des timers expirés
        BossTimerManager.cleanExpired();

        List<BossTimerManager.TimerEntry> timers = BossTimerManager.getActiveTimers();
        if (timers.isEmpty()) return;

        int screenWidth = client.getWindow().getScaledWidth();
        int x = screenWidth - PANEL_WIDTH - MARGIN_RIGHT;
        int y = MARGIN_TOP;

        int totalRows = timers.size() + 1; // +1 pour le titre
        int panelHeight = PADDING_Y * 2 + totalRows * ROW_HEIGHT + 2;

        // Fond du panel
        context.fill(x - PADDING_X, y, x + PANEL_WIDTH + PADDING_X, y + panelHeight, COLOR_BG);

        // Titre
        context.drawTextWithShadow(
                client.textRenderer,
                "⏱ Boss Timers",
                x,
                y + PADDING_Y,
                COLOR_TITLE
        );

        // Ligne séparatrice
        int sepY = y + PADDING_Y + ROW_HEIGHT + 1;
        context.fill(x - PADDING_X, sepY, x + PANEL_WIDTH + PADDING_X, sepY + 1, 0x55FFFFFF);

        // Liste des boss
        int rowY = sepY + 3;
        for (BossTimerManager.TimerEntry entry : timers) {
            String name = entry.displayName();
            String time = entry.formatTime();
            boolean isSpawn = entry.remainingMs() <= 0;

            // Nom du boss
            context.drawTextWithShadow(
                    client.textRenderer,
                    name,
                    x,
                    rowY,
                    COLOR_BOSS_NAME
            );

            // Timer aligné à droite du panel
            int timeColor = isSpawn ? COLOR_SPAWN : COLOR_TIMER;
            int timeWidth = client.textRenderer.getWidth(time);
            context.drawTextWithShadow(
                    client.textRenderer,
                    time,
                    x + PANEL_WIDTH - timeWidth,
                    rowY,
                    timeColor
            );

            rowY += ROW_HEIGHT;
        }
    }
}
