package falseresync.vivatech.common.block;

import falseresync.vivatech.common.VivatechUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

import java.util.List;

public interface RestrictsWirePostPlacement {
    List<Direction> HORIZONTAL = List.of(VivatechUtil.HORIZONTAL_DIRECTIONS);
    List<Direction> VERTICAL = List.of(Direction.UP, Direction.DOWN);
    List<Direction> ROTATED_ABOUT_X = List.of(Direction.NORTH, Direction.SOUTH, Direction.UP, Direction.DOWN);
    List<Direction> ROTATED_ABOUT_Y = HORIZONTAL;
    List<Direction> ROTATED_ABOUT_Z = List.of(Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN);

    boolean allowsWirePostsAt(BlockView world, BlockPos pos, Direction direction);

    static boolean allowRotatedAboutFacing(Direction facing, Direction direction) {
        return (switch (facing.getAxis()) {
            case X -> ROTATED_ABOUT_X;
            case Y -> ROTATED_ABOUT_Y;
            case Z -> ROTATED_ABOUT_Z;
        }).contains(direction);
    }

    interface Disallow extends RestrictsWirePostPlacement {
        @Override
        default boolean allowsWirePostsAt(BlockView world, BlockPos pos, Direction direction) {
            return false;
        }
    }

    interface AllowHorizontal extends RestrictsWirePostPlacement {
        @Override
        default boolean allowsWirePostsAt(BlockView world, BlockPos pos, Direction direction) {
            return HORIZONTAL.contains(direction);
        }
    }

    interface AllowVertical extends RestrictsWirePostPlacement {
        @Override
        default boolean allowsWirePostsAt(BlockView world, BlockPos pos, Direction direction) {
            return VERTICAL.contains(direction);
        }
    }
}
