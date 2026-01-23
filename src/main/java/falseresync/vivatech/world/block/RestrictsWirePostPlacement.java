package falseresync.vivatech.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;

import java.util.List;

public interface RestrictsWirePostPlacement {
    Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    List<Direction> HORIZONTAL = List.of(HORIZONTAL_DIRECTIONS);
    List<Direction> VERTICAL = List.of(Direction.UP, Direction.DOWN);
    List<Direction> ROTATED_ABOUT_X = List.of(Direction.NORTH, Direction.SOUTH, Direction.UP, Direction.DOWN);
    List<Direction> ROTATED_ABOUT_Y = HORIZONTAL;
    List<Direction> ROTATED_ABOUT_Z = List.of(Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN);

    boolean allowsWirePostsAt(BlockGetter world, BlockPos pos, Direction direction);

    static boolean allowRotatedAboutFacing(Direction facing, Direction direction) {
        return (switch (facing.getAxis()) {
            case X -> ROTATED_ABOUT_X;
            case Y -> ROTATED_ABOUT_Y;
            case Z -> ROTATED_ABOUT_Z;
        }).contains(direction);
    }

    interface Disallow extends RestrictsWirePostPlacement {
        @Override
        default boolean allowsWirePostsAt(BlockGetter world, BlockPos pos, Direction direction) {
            return false;
        }
    }

    interface AllowHorizontal extends RestrictsWirePostPlacement {
        @Override
        default boolean allowsWirePostsAt(BlockGetter world, BlockPos pos, Direction direction) {
            return HORIZONTAL.contains(direction);
        }
    }

    interface AllowVertical extends RestrictsWirePostPlacement {
        @Override
        default boolean allowsWirePostsAt(BlockGetter world, BlockPos pos, Direction direction) {
            return VERTICAL.contains(direction);
        }
    }
}
