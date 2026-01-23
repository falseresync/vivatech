package falseresync.vivatech.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class VivatechDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		var pack = fabricDataGenerator.createPack();
		pack.addProvider(VivatechBlockLootProvider::new);
		pack.addProvider(VivatechBlockTagProvider::new);
		pack.addProvider(VivatechVanillaRecipeProvider::new);
		pack.addProvider(VivatechModelProvider::new);
	}
}
