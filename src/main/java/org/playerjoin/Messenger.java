package org.playerjoin;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.playerjoin.Kit.JoinKit;
import org.playerjoin.Utils.MessageFormatter;

import java.awt.*;
import java.util.logging.Level;


public class Messenger extends JavaPlugin {
    private HytaleLogger logger = HytaleLogger.forEnclosingClass();

    public Messenger(@NonNullDecl JavaPluginInit init) {
        super(init);
    }

    @Override
    public void setup() {
        EventRegistry eventRegistry = getEventRegistry();
        eventRegistry.registerGlobal(PlayerReadyEvent.class, this::onPlayerJoin);
        eventRegistry.registerGlobal(PlayerDisconnectEvent.class, this::onPlayerLeave);
        eventRegistry.registerGlobal(AddPlayerToWorldEvent.class, this::onPlayerJoinWorld);
    }

    @Override
    public void start() {
        logger.at(Level.INFO).log("Booting up PlayerJoinMessenger");
    }

    @Override
    public void shutdown() {
        logger.at(Level.INFO).log("Shutting down PlayerJoinMessenger");
    }

    public void onPlayerJoinWorld(AddPlayerToWorldEvent event) {
        event.setBroadcastJoinMessage(false);
    }

    public void onPlayerJoin(PlayerReadyEvent event) {
        Player eventPlayer = event.getPlayer();
        if (eventPlayer.isFirstSpawn()) {
            Universe.get().getPlayers().forEach(player -> {
                Player p = refToPlayerComponent(player);
                if (p != eventPlayer) {
                    player.sendMessage(MessageFormatter.format("Player {player} joined for the first time"
                            .replace("{player}", eventPlayer.getDisplayName())));
                } else {
                    player.sendMessage(MessageFormatter.format("§f[§a+§f] {player}"));
                }
            });
            eventPlayer.sendMessage(MessageFormatter.format("§bWelcome to hytaleworld! Because its your first time we gave you something to start"));
            new JoinKit(eventPlayer).grant();
        } else {
            Universe.get().getPlayers().forEach(player -> {
                Player p = refToPlayerComponent(player);
                if (p == eventPlayer) {
                    player.sendMessage(MessageFormatter.format("§f[§a+§f] {player}"
                            .replace("{player}", eventPlayer.getDisplayName())));
                }
            });
            eventPlayer.sendMessage(MessageFormatter.format("§bWelcome back to hytaleworld"));
        }
    }

    public void onPlayerLeave(PlayerDisconnectEvent event) {
        Universe.get().getPlayers().forEach(player -> {
            Player p = refToPlayerComponent(player);
            p.sendMessage(MessageFormatter.format("§f[§4-§f] {player}"
                    .replace("{player}", event.getPlayerRef().getUsername())));
        });
    }

    public Player refToPlayerComponent(PlayerRef p) {
        Ref<EntityStore> pRef = p.getReference();
        Store<EntityStore> store = pRef.getStore();
        return store.getComponent(pRef, Player.getComponentType());
    }
}
