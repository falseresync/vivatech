package falseresync.vivatech.item;

import falseresync.vivatech.component.entity.VtEntityComponents;
import falseresync.vivatech.component.item.VtItemComponents;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SacrificialDaggerItem extends SwordItem {
    public SacrificialDaggerItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            if (user.isSneaking()) {
                user.attack(user);
            }

//            user.sendMessage(Text.literal("Collected blood " + VtEntityComponents.PLAYER_LIFESSENCE_STORAGE.get(user).getAmount()));
        }
        return super.use(world, user, hand);
    }

    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postDamageEntity(stack, target, attacker);
        if (attacker instanceof PlayerEntity player) {
            try (var tx = Transaction.openOuter()) {
                if (VtEntityComponents.PLAYER_LIFESSENCE_STORAGE.get(player).insert(10, tx) > 0) {
                    tx.commit();
                }
            }
        }
    }
}
