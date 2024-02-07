package dev.falseresync.vivatech.common;

import net.minecraft.util.Identifier;

public class VivatechConsts {
    public static final String MOD_ID = "vivatech";

    public static Identifier vivatech(String id) {
        return new Identifier(MOD_ID, id);
    }
}
