package de.epstein.slashstring;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;

@Mod(SlashString.MODID)
public class SlashString {

    public static final String MODID = "slashstring";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SlashString(IEventBus modEventBus) {
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(VillagerRestock::onLivingTick);
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        StringCommand.register(event.getDispatcher());
    }
}
