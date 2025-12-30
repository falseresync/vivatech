package falseresync.vivatech.common;

import it.unimi.dsi.fastutil.Pair;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.jgrapht.Graph;

import java.util.HashSet;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class VivatechUtil {
    public static final Event<BiConsumer<ServerLevel, ChunkPos>> CHUNK_START_TICKING = EventFactory.createArrayBacked(BiConsumer.class, callbacks -> (serverWorld, chunk) -> {
        for (BiConsumer<ServerLevel, ChunkPos> callback : callbacks) {
            callback.accept(serverWorld, chunk);
        }
    });

    public static final Event<BiConsumer<ServerLevel, ChunkPos>> CHUNK_STOP_TICKING = EventFactory.createArrayBacked(BiConsumer.class, callbacks -> (serverWorld, chunk) -> {
        for (BiConsumer<ServerLevel, ChunkPos> callback : callbacks) {
            callback.accept(serverWorld, chunk);
        }
    });

    public static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    private static final Function<Level, Integer> memo$findViewDistance = Util.memoize((Level world) -> world.isClientSide()
            ? Minecraft.getInstance().options.getEffectiveRenderDistance()
            : ((ServerLevel) world).getChunkSource().chunkMap.serverViewDistance);

    public static <T> Optional<T> nextRandomEntry(ServerLevel world, TagKey<T> tag, RandomSource random) {
        return world.registryAccess()
                .registry(tag.registry())
                .map(registry -> registry.getOrCreateTag(tag))
                .flatMap(entries -> entries.getRandomElement(random).map(Holder::value));
    }

    public static <V, E> void replaceVertexIgnoringUndirectedEdgeEquality(Graph<V, E> graph, V oldVertex, V newVertex) {
        if (!oldVertex.equals(newVertex)) {
            graph.addVertex(newVertex);
            var toAddBack = new HashSet<Pair<V, E>>(); // Because CMEs
            for (E edge : graph.edgesOf(oldVertex)) {
                var source = graph.getEdgeSource(edge);
                if (source != oldVertex) {
                    toAddBack.add(Pair.of(source, edge));
                } else {
                    toAddBack.add(Pair.of(graph.getEdgeTarget(edge), edge));
                }
            }
            for (var pair : toAddBack) {
                graph.removeEdge(pair.right());
                graph.addEdge(newVertex, pair.left(), pair.right());
            }
            graph.removeVertex(oldVertex);
        }
    }

    /**
     * @return memoized(!) view distance
     */
    public static int findViewDistance(Level world) {
        return memo$findViewDistance.apply(world);
    }

    public static long exchangeStackInSlotWithHand(Player player, InteractionHand hand, InventoryStorage storage, int slot, int maxAmount, @Nullable TransactionContext transaction) {
        var playerStack = player.getItemInHand(hand);
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
