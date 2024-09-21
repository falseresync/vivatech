package falseresync.vivatech.common;

import falseresync.vivatech.api.registry.AutoRegistry;
import falseresync.vivatech.client.screen.AutomatonScreen;
import falseresync.vivatech.common.block.VivatechBlocks;
import falseresync.vivatech.common.component.item.VivatechItemComponents;
import falseresync.vivatech.common.entity.AutomatonEntity;
import falseresync.vivatech.common.entity.VivatechEntities;
import falseresync.vivatech.common.item.VivatechItemGroups;
import falseresync.vivatech.common.item.VivatechItems;
import falseresync.vivatech.api.lifessence.Lifessence;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VivatechCommon implements ModInitializer {
	public static final String MOD_ID = "vivatech";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		new AutoRegistry(MOD_ID, LOGGER)
				.link(Registries.BLOCK, VivatechBlocks.class)
				.link(Registries.DATA_COMPONENT_TYPE, VivatechItemComponents.class)
				.link(Registries.ITEM, VivatechItems.class)
				.link(Registries.ITEM_GROUP, VivatechItemGroups.class)
				.link(Registries.ENTITY_TYPE, VivatechEntities.class);
		Lifessence.init();

		UseEntityCallback.EVENT.register(AutomatonEntity::interact);
	}

	public static Identifier vtId(String path) {
		return Identifier.of(MOD_ID, path);
	}

	public static Identifier vtTexId(String path) {
		return vtId("textures/" + path + ".png");
	}
}