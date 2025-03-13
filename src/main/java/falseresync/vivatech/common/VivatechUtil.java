package falseresync.vivatech.common;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.jgrapht.Graph;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class VivatechUtil {
    public static final Event<BiConsumer<ServerWorld, ChunkPos>> CHUNK_START_TICKING = EventFactory.createArrayBacked(BiConsumer.class, callbacks -> (serverWorld, chunk) -> {
        for (BiConsumer<ServerWorld, ChunkPos> callback : callbacks) {
            callback.accept(serverWorld, chunk);
        }
    });

    public static final Event<BiConsumer<ServerWorld, ChunkPos>> CHUNK_STOP_TICKING = EventFactory.createArrayBacked(BiConsumer.class, callbacks -> (serverWorld, chunk) -> {
        for (BiConsumer<ServerWorld, ChunkPos> callback : callbacks) {
            callback.accept(serverWorld, chunk);
        }
    });

    public static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{ Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };

    private static final Function<World, Integer> memo$findViewDistance = Util.memoize((World world) -> world.isClient()
            ? MinecraftClient.getInstance().options.getClampedViewDistance()
            : ((ServerWorld) world).getChunkManager().chunkLoadingManager.watchDistance);

    public static <T> Optional<T> nextRandomEntry(ServerWorld world, TagKey<T> tag, Random random) {
        return world.getRegistryManager()
                .getOptional(tag.registry())
                .map(registry -> registry.getOrCreateEntryList(tag))
                .flatMap(entries -> entries.getRandom(random).map(RegistryEntry::value));
    }

    public static <V, E> void replaceVertexUndirected(Graph<V, E> graph, V oldVertex, V newVertex) {
        graph.addVertex(newVertex);
        for (E edge : graph.edgesOf(oldVertex)) {
            var source = graph.getEdgeSource(edge);
            if (source != oldVertex) {
                graph.addEdge(newVertex, source, edge);
            } else {
                graph.addEdge(newVertex, graph.getEdgeTarget(edge), edge);
            }
        }
        graph.removeVertex(oldVertex);
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
