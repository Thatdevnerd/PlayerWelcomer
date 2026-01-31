package org.playerjoin.Kit;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class JoinKit {
    private final Player player;
    private final HashMap<String, Integer> defaultStack = new HashMap<>(Map.of(
            "Weapon_Sword_Crude", 1,
            "Tool_Pickaxe_Crude", 1,
            "Tool_Hatchet_Crude", 1,
            "Food_Wildmeat_Cooked", 12
    ));

    public JoinKit(Player player) {
        this.player = player;
    }

    public void grant() {
        AtomicReference<Short> slot = new AtomicReference<>((short) 0);
        defaultStack.forEach((item, amount) -> {
            ItemStack stack = new ItemStack(item, amount);
            this.player.getInventory().getCombinedHotbarFirst()
                    .setItemStackForSlot(slot.getAndSet((short) (slot.get() + 1)), stack);
        });
    }
}
