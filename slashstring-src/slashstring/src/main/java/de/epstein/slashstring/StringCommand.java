package de.epstein.slashstring;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Inventory;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StringCommand {

    // Cooldown in Millisekunden (3 Sekunden)
    private static final long COOLDOWN_MS = 3000L;
    private static final Map<UUID, Long> cooldowns = new HashMap<>();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("string")
                .requires(source -> source.hasPermission(2))
                .executes(ctx -> execute(ctx.getSource()))
        );
    }

    private static int execute(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        UUID uuid = player.getUUID();
        long now = System.currentTimeMillis();

        // Cooldown prüfen
        if (cooldowns.containsKey(uuid)) {
            long remaining = COOLDOWN_MS - (now - cooldowns.get(uuid));
            if (remaining > 0) {
                source.sendFailure(Component.literal(
                    "§cNoch " + (remaining / 1000 + 1) + "s Cooldown!"
                ));
                return 0;
            }
        }

        Inventory inv = player.getInventory();
        int filled = 0;

        // Slots 0-35: Hotbar (0-8) + Hauptinventar (9-35)
        for (int i = 0; i < 36; i++) {
            ItemStack slot = inv.getItem(i);
            if (slot.isEmpty()) {
                inv.setItem(i, new ItemStack(Items.STRING, 64));
                filled++;
            }
        }

        cooldowns.put(uuid, now);

        if (filled > 0) {
            source.sendSuccess(() -> Component.literal(
                "§a" + filled + " Slots mit Fäden gefüllt!"
            ), false);
        } else {
            source.sendFailure(Component.literal("§cKein freier Platz im Inventar!"));
        }

        return filled;
    }
}
