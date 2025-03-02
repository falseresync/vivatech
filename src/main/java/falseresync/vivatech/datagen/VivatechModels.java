package falseresync.vivatech.datagen;

import falseresync.vivatech.common.block.VtBlocks;
import falseresync.vivatech.common.item.VtItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.ModelIds;
import net.minecraft.data.client.Models;

public class VivatechModels extends FabricModelProvider {
    private BlockStateModelGenerator blockStateModelGenerator;

    public VivatechModels(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        this.blockStateModelGenerator = blockStateModelGenerator;

        blockStateModelGenerator.registerSimpleCubeAll(VtBlocks.GENERATOR);
//        registerSimple3DBlockItemModel(VivatechBlocks.STERLING_GENERATOR);

        blockStateModelGenerator.registerSimpleCubeAll(VtBlocks.CONSUMER);
//        registerSimple3DBlockItemModel(VivatechBlocks.ELECTRIC_FURNACE);

        blockStateModelGenerator.registerSimpleCubeAll(VtBlocks.WIRE_POST);
//        registerSimple3DBlockItemModel(VivatechBlocks.WIRE_POST);
    }

    private void registerSimple3DBlockItemModel(Block block) {
        blockStateModelGenerator.registerParentedItemModel(block, ModelIds.getBlockModelId(block));
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(VtItems.CONNECTOR, Models.GENERATED);
        itemModelGenerator.register(VtItems.PLIERS, Models.GENERATED);
    }
}
