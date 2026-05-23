package de.epstein.slashstring;

import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class VillagerRestock {

    /**
     * Bei jedem Tick eines Villagers:
     * - Alle Uses auf 0 zurücksetzen (wirkt wie unendlich oft nutzbar)
     * - maxUses auf Integer.MAX_VALUE setzen
     * - rewardExp deaktivieren ist optional, hier lassen wir es an
     */
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Villager villager)) return;

        // Nur serverseitig ausführen
        if (villager.level().isClientSide()) return;

        for (MerchantOffer offer : villager.getOffers()) {
            // Nutzungen zurücksetzen damit der Trade nie "verbraucht" ist
            if (offer.isBlockedByPlayerCooldown() || offer.getUses() > 0) {
                offer.resetUses();
            }
            // MaxUses sehr hoch setzen damit es nie blockiert
            // MerchantOffer hat kein direktes setMaxUses, aber wir können
            // den internen Wert über Reflection setzen
            setMaxUses(offer);
        }

        // Restock-Timer zurücksetzen damit der Villager nicht "wartet"
        villager.setRestocksToday(0);
    }

    private static void setMaxUses(MerchantOffer offer) {
        try {
            var field = MerchantOffer.class.getDeclaredField("maxUses");
            field.setAccessible(true);
            field.setInt(offer, Integer.MAX_VALUE);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Falls Feldname sich ändert: Fallback ist resetUses() oben
            SlashString.LOGGER.warn("Konnte maxUses nicht per Reflection setzen: {}", e.getMessage());
        }
    }
}
