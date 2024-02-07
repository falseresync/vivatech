package dev.falseresync.vivatech.common.block;

import dev.falseresync.vivatech.common.block.sterling_generator.SterlingGeneratorGui;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class VivatechBlockGuis {
    private static final Map<Identifier, ScreenHandlerType<?>> TO_REGISTER = new HashMap<>();

    public static final ScreenHandlerType<SterlingGeneratorGui> STERLING_GENERATOR = r(VivatechBlocks.STERLING_GENERATOR.getId(), SterlingGeneratorGui::new);

    private static <G extends ScreenHandler> ScreenHandlerType<G> r(Identifier id, Factory<G> guiFactory) {
        var type = new ScreenHandlerType<>((syncId, playerInventory) -> guiFactory.create(syncId, playerInventory, ScreenHandlerContext.EMPTY), FeatureFlags.VANILLA_FEATURES);
        TO_REGISTER.put(id, type);
        return type;
    }

    public static void register() {
        TO_REGISTER.forEach((identifier, screenHandler) -> Registry.register(Registries.SCREEN_HANDLER, identifier, screenHandler));
    }

    @FunctionalInterface
    private interface Factory<G extends ScreenHandler> {
        G create(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context);
    }
}
