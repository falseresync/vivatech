package falseresync.vivatech.common.mixin;

import falseresync.vivatech.common.VivatechUtil;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin {
    @Shadow @Final ServerLevel level;

    @Inject(method = "onFullChunkStatusChange", at = @At("TAIL"))
    private void vivatech$onFullChunkStatusChange(ChunkPos chunkPos, FullChunkStatus levelType, CallbackInfo ci) {
        // consider chunk LOADED if FULL, BLOCK_TICKING, ENTITY_TICKING
        // consider chunk UNLOADED if INACCESSIBLE
        if (levelType.isOrAfter(FullChunkStatus.BLOCK_TICKING)) {
            VivatechUtil.CHUNK_START_TICKING.invoker().accept(level, chunkPos);
        } else {
            VivatechUtil.CHUNK_STOP_TICKING.invoker().accept(level, chunkPos);
        }
    }
}
