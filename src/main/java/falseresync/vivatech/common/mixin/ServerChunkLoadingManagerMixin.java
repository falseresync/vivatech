package falseresync.vivatech.common.mixin;

import falseresync.vivatech.common.VivatechUtil;
import net.minecraft.server.world.ChunkLevelType;
import net.minecraft.server.world.ServerChunkLoadingManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerChunkLoadingManager.class)
public abstract class ServerChunkLoadingManagerMixin {
    @Shadow @Final ServerWorld world;

    @Inject(method = "onChunkStatusChange", at = @At("TAIL"))
    private void vivatech$onChunkStatusChange(ChunkPos chunkPos, ChunkLevelType levelType, CallbackInfo ci) {
        // consider chunk LOADED if FULL, BLOCK_TICKING, ENTITY_TICKING
        // consider chunk UNLOADED if INACCESSIBLE
        if (levelType.isAfter(ChunkLevelType.BLOCK_TICKING)) {
            VivatechUtil.CHUNK_START_TICKING.invoker().accept(world, chunkPos);
        } else {
            VivatechUtil.CHUNK_STOP_TICKING.invoker().accept(world, chunkPos);
        }
    }
}
