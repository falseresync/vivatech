package falseresync.vivatech.datagen;

import falseresync.vivatech.datagen.VivatechBlockLootTableProvider;
import falseresync.vivatech.datagen.VivatechBlockTagProvider;
import falseresync.vivatech.datagen.VivatechEntityTagProvider;
import falseresync.vivatech.datagen.VivatechItemTagProvider;
import falseresync.vivatech.datagen.VivatechModelProvider;
import falseresync.vivatech.datagen.VivatechVanillaRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class VivatechDatagen implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		var pack = fabricDataGenerator.createPack();
		pack.addProvider(VivatechBlockLootTableProvider::new);
		pack.addProvider(VivatechBlockTagProvider::new);
		pack.addProvider(VivatechItemTagProvider::new);
		pack.addProvider(VivatechEntityTagProvider::new);
		pack.addProvider(VivatechVanillaRecipeProvider::new);
		pack.addProvider(VivatechModelProvider::new);
	}
}
