package falseresync.vivatech.item;

import falseresync.vivatech.component.entity.VtEntityComponents;
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
            user.sendMessage(Text.literal("Collected blood " + VtEntityComponents.COLLECTED_BLOOD.get(user).getAmount()));
        }
        return super.use(world, user, hand);
    }

    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postDamageEntity(stack, target, attacker);
        if (attacker instanceof PlayerEntity player) {
            VtEntityComponents.COLLECTED_BLOOD.get(player).add(10);
        }
    }
}
