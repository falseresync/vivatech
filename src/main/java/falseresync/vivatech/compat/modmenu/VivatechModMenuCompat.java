package falseresync.vivatech.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import falseresync.vivatech.common.config.VivatechConfig;
import me.shedaniel.autoconfig.AutoConfig;

public class VivatechModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(VivatechConfig.class, parent).get();
    }
}
