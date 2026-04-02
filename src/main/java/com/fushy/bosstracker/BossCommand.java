package com.fushy.bosstracker;

import com.fushy.bosstracker.config.BossConfig;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class BossCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {

        // /resettimers — reset tous les timers actifs
        dispatcher.register(
                ClientCommandManager.literal("resettimers")
                        .executes(ctx -> {
                            BossTimerManager.resetAll();
                            ctx.getSource().sendFeedback(
                                    Text.literal("§a[BossTracker] Tous les timers ont été réinitialisés !")
                            );
                            return 1;
                        })
        );

        // /bosstracker — liste les timers actifs dans le chat
        dispatcher.register(
                ClientCommandManager.literal("bosstracker")
                        .executes(ctx -> {
                            var timers = BossTimerManager.getActiveTimers();
                            if (timers.isEmpty()) {
                                ctx.getSource().sendFeedback(
                                        Text.literal("§e[BossTracker] Aucun timer actif.")
                                );
                            } else {
                                ctx.getSource().sendFeedback(
                                        Text.literal("§6[BossTracker] Timers actifs :")
                                );
                                for (var entry : timers) {
                                    ctx.getSource().sendFeedback(
                                            Text.literal("  §f" + entry.displayName() + " §7→ §a" + entry.formatTime())
                                    );
                                }
                            }
                            return 1;
                        })
        );

        // /bossreload — recharge la config sans redémarrer Minecraft
        dispatcher.register(
                ClientCommandManager.literal("bossreload")
                        .executes(ctx -> {
                            BossConfig.load();
                            ctx.getSource().sendFeedback(
                                    Text.literal("§a[BossTracker] Config rechargée ! "
                                            + BossConfig.bosses.size() + " boss(es) chargés.")
                            );
                            return 1;
                        })
        );
    }
}

