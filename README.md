# BossTracker

A Fabric client mod for MinePiece that automatically tracks boss respawn timers.

When you kill a boss, a countdown appears on your HUD and resets automatically when you switch servers.

## Features

- Detects boss kills via chat message format `-= Classement [Boss] =-`
- HUD overlay showing all active timers with countdown
- Auto-reset on server disconnect / reconnect
- Fully configurable via a simple JSON file
- `/resettimers` — manually reset all timers
- `/bossreload` — reload config without restarting the game

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/installer/) for Minecraft 1.21.8
2. Download [Fabric API 0.130.0+1.21.8](https://modrinth.com/mod/fabric-api/version/0.130.0+1.21.8)
3. Download the latest `bosstracker-x.x.x.jar` from Releases
4. Place both `.jar` files in your `.minecraft/mods/` folder
5. Launch Minecraft with the Fabric profile

## Configuration

Edit `.minecraft/config/bosstracker.json` to add or modify bosses:
```json
{
  "defaultRespawnSeconds": 900,
  "bosses": {
    "Sabo":      900,
    "Luffy":    900,
    "Chopper":     900,
    "Dalton":   900,
    "Ace": 900,
    "Trafalgar Water D.Law": 900,
    "Robin": 900,
    "Oz": 900,
    "Eustass Kid": 900,
    "Perona": 900,
    "Nightmare Luffy": 900,
    "Jewelry Bonney": 900,
    "Vender Decken IX": 900
  }
}
```

The key must match exactly what appears in `-= Classement [Name] =-` in chat.
Use `/bossreload` in-game to apply changes without restarting.
