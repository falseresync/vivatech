package falseresync.vivatech.world.item;

import falseresync.vivatech.client.book.VivatechBookScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class VivatechBookItem extends Item {
    public VivatechBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            Minecraft.getInstance().setScreen(new VivatechBookScreen());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }
}
