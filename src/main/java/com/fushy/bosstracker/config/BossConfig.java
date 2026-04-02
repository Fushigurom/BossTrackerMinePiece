package com.fushy.bosstracker.config;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

/**
 * Config ultra-simple : un seul fichier JSON à éditer à la main.
 *
 * Format du fichier bosstracker.json :
 * {
 *   "defaultRespawnSeconds": 900,
 *   "bosses": {
 *     "Sabo":   900,
 *     "Arlong": 900,
 *     "Krieg":  900,
 *     "Mihawk": 1800
 *   }
 * }
 *
 * → La clé = nom exact du boss tel qu'il apparaît dans "-= Classement [NOM] =-"
 * → La valeur = durée de respawn en secondes (900 = 15 min)
 *
 * Pour ajouter un boss : rajouter une ligne  "NomBoss": secondes
 * Pour changer un timer : modifier la valeur
 * La config est rechargée via /bossreload en jeu.
 */
public class BossConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("bosstracker.json");

    /** Durée par défaut si un boss est détecté mais pas dans la liste */
    public static int defaultRespawnSeconds = 900;

    /** Map : nom du boss (insensible à la casse) → secondes de respawn */
    public static final Map<String, Integer> bosses = new LinkedHashMap<>();

    public static void load() {
        if (!CONFIG_PATH.toFile().exists()) {
            createDefault();
            return;
        }
        try (Reader reader = new FileReader(CONFIG_PATH.toFile())) {
            JsonObject root = GSON.fromJson(reader, JsonObject.class);
            if (root == null) { createDefault(); return; }

            if (root.has("defaultRespawnSeconds")) {
                defaultRespawnSeconds = root.get("defaultRespawnSeconds").getAsInt();
            }

            bosses.clear();
            if (root.has("bosses")) {
                JsonObject bossObj = root.getAsJsonObject("bosses");
                for (Map.Entry<String, JsonElement> e : bossObj.entrySet()) {
                    bosses.put(e.getKey().toLowerCase(), e.getValue().getAsInt());
                }
            }
            System.out.println("[BossTracker] Config chargée : " + bosses.size() + " boss(es).");
        } catch (IOException e) {
            System.err.println("[BossTracker] Erreur lecture config : " + e.getMessage());
            createDefault();
        }
    }

    public static void save() {
        try (Writer writer = new FileWriter(CONFIG_PATH.toFile())) {
            // Reconstruit un objet avec les noms en casse d'origine pour la lisibilité
            JsonObject root = new JsonObject();
            root.addProperty("defaultRespawnSeconds", defaultRespawnSeconds);

            JsonObject bossObj = new JsonObject();
            // On stocke en minuscule en mémoire mais on affiche proprement
            for (Map.Entry<String, Integer> e : bosses.entrySet()) {
                String display = Character.toUpperCase(e.getKey().charAt(0)) + e.getKey().substring(1);
                bossObj.addProperty(display, e.getValue());
            }
            root.add("bosses", bossObj);
            GSON.toJson(root, writer);
        } catch (IOException e) {
            System.err.println("[BossTracker] Erreur sauvegarde config : " + e.getMessage());
        }
    }

    /**
     * Cherche le timer configuré pour ce nom de boss.
     * @param bossName nom extrait du message chat (ex: "Sabo")
     * @return secondes de respawn, ou defaultRespawnSeconds si inconnu
     */
    public static int getRespawnSeconds(String bossName) {
        return bosses.getOrDefault(bossName.toLowerCase(), defaultRespawnSeconds);
    }

    /**
     * Vérifie si ce boss est connu dans la config.
     * Si false, le timer démarre quand même avec defaultRespawnSeconds.
     */
    public static boolean isKnownBoss(String bossName) {
        return bosses.containsKey(bossName.toLowerCase());
    }

    private static void createDefault() {
        bosses.clear();
        defaultRespawnSeconds = 900;
        // Boss MinePiece — complète cette liste avec les vrais noms
        // Le nom doit correspondre exactement à ce qui apparaît dans "-= Classement [NOM] =-"
        bosses.put("sabo",   900);
        bosses.put("arlong", 900);
        bosses.put("krieg",  900);
        bosses.put("mihawk", 900);
        bosses.put("crocodile", 900);
        save();
        System.out.println("[BossTracker] Config par défaut créée → " + CONFIG_PATH);
        System.out.println("[BossTracker] Édite le fichier pour ajouter tes boss !");
    }
}
