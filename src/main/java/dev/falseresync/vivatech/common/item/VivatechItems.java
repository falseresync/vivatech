package dev.falseresync.vivatech.common.item;

import dev.falseresync.vivatech.common.block.VivatechBlocks;
import dev.falseresync.vivatech.api.HasId;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static dev.falseresync.vivatech.common.VivatechConsts.vivatech;

public class VivatechItems {
    private static final HashMap<Identifier, Item> TO_REGISTER = new HashMap<>();
    private static final HashMap<Group, List<Item>> TO_GROUP = new HashMap<>();
    private static final FabricItemSettings DEFAULT_SETTINGS = new FabricItemSettings();
    public static final BlockItem STERLING_GENERATOR = rBlockItem(VivatechBlocks.STERLING_GENERATOR);
    public static final ItemGroup GROUP_GENERAL = FabricItemGroup.builder()
            .displayName(Text.translatable("vivatech.general"))
            .icon(Items.IRON_PICKAXE::getDefaultStack)
            .entries((displayContext, entries) -> {
                TO_GROUP.getOrDefault(Group.GENERAL, List.of()).forEach(entries::add);
            })
            .build();

    private static <T extends Block & HasId> BlockItem rBlockItem(T block) {
        return rBlockItem(block, DEFAULT_SETTINGS);
    }


    private static <T extends Block & HasId> BlockItem rBlockItem(T block, Item.Settings settings) {
        return rBlockItem(block, settings, Group.GENERAL);
    }

    private static <T extends Block & HasId> BlockItem rBlockItem(T block, Item.Settings settings, Group group) {
        var item = new BlockItem(block, settings);
        Item.BLOCK_ITEMS.put(block, item);
        TO_REGISTER.put(block.getId(), item);
        group(item, group);
        return item;
    }


    private static <T extends Item & HasId> T r(T item) {
        return r(item, Group.GENERAL);
    }

    private static <T extends Item & HasId> T r(T item, Group group) {
        TO_REGISTER.put(item.getId(), item);
        group(item, group);
        return item;
    }

    private static void group(Item item, Group group) {
        TO_GROUP.computeIfAbsent(group, key -> new ArrayList<>()).add(item);
    }

    public static void register() {
        TO_REGISTER.forEach((identifier, type) -> Registry.register(Registries.ITEM, identifier, type));
        Registry.register(Registries.ITEM_GROUP, vivatech("general"), GROUP_GENERAL);
    }

    public enum Group {
        GENERAL
    }
}
