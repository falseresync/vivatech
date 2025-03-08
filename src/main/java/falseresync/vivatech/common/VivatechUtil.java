package falseresync.vivatech.common;

import net.fabricmc.fabric.api.transfer.v1.item.*;
import net.fabricmc.fabric.api.transfer.v1.storage.*;
import net.fabricmc.fabric.api.transfer.v1.transaction.*;
import net.minecraft.client.*;
import net.minecraft.entity.player.*;
import net.minecraft.registry.entry.*;
import net.minecraft.registry.tag.*;
import net.minecraft.server.world.*;
import net.minecraft.util.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;

public class VivatechUtil {
    private static final Function<World, Integer> memo$findViewDistance = Util.memoize((World world) -> world.isClient()
            ? MinecraftClient.getInstance().options.getClampedViewDistance()
            : ((ServerWorld) world).getChunkManager().chunkLoadingManager.watchDistance);

    public static <T> Optional<T> nextRandomEntry(ServerWorld world, TagKey<T> tag, Random random) {
        return world.getRegistryManager()
                .getOptional(tag.registry())
                .map(registry -> registry.getOrCreateEntryList(tag))
                .flatMap(entries -> entries.getRandom(random).map(RegistryEntry::value));
    }

    /**
     * @return memoized(!) view distance
     */
    public static int findViewDistance(World world) {
        return memo$findViewDistance.apply(world);
    }

    public static long exchangeStackInSlotWithHand(PlayerEntity player, Hand hand, InventoryStorage storage, int slot, int maxAmount, @Nullable TransactionContext transaction) {
        var playerStack = player.getStackInHand(hand);
        var storedVariant = storage.getSlot(slot).getResource();

        if (storedVariant.isBlank() && !playerStack.isEmpty()) {
            return StorageUtil.move(PlayerInventoryStorage.of(player), storage, variant -> variant.matches(playerStack), maxAmount, transaction);
        }

        if (!storedVariant.isBlank() && playerStack.isEmpty()) {
            return StorageUtil.move(storage, PlayerInventoryStorage.of(player), variant -> variant.equals(storedVariant), maxAmount, transaction);
        }

        return 0;
    }
}
