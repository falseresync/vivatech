package falseresync.vivatech.client.book;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.serialization.JsonOps;
import falseresync.vivatech.Vivatech;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.*;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.Nullable;

import java.io.*;
import java.util.Objects;

import static net.minecraft.client.gui.screens.inventory.BookViewScreen.BOOK_LOCATION;

public class VivatechBookScreen extends Screen {
    public static final Identifier BOOK_TEX = Vivatech.id("textures/gui/book.png");
    private Component pageText;

    public VivatechBookScreen() {
        super(Component.literal("Vivatech Book"));
    }

    @Override
    protected void init() {
        this.addRenderableWidget(
                Button.builder(CommonComponents.GUI_DONE, button -> this.onClose()).pos((this.width - 200) / 2, this.menuControlsTop()).width(200).build()
        );

        var registryOps = Minecraft.getInstance().player.registryAccess().createSerializationContext(JsonOps.INSTANCE);
        var reader = new JsonReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("assets/vivatech/book/pages/introduction.json")));
        var tag = JsonParser.parseReader(reader);
        pageText = ComponentSerialization.CODEC.parse(registryOps, tag).getOrThrow();
    }

    @Override
    public boolean keyPressed(final KeyEvent event) {
        if (super.keyPressed(event)) {
            return true;
        } else {
            return switch (event.key()) {
                case InputConstants.KEY_PAGEUP -> {
//                    this.backButton.onPress(event);
                    yield true;
                }
                case InputConstants.KEY_PAGEDOWN -> {
//                    this.forwardButton.onPress(event);
                    yield true;
                }
                default -> false;
            };
        }
    }

    @Override
    public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
        super.render(graphics, mouseX, mouseY, a);
        this.visitText(graphics.textRenderer(GuiGraphics.HoveredTextEffects.TOOLTIP_AND_CURSOR), false);
    }

    private void visitText(final ActiveTextCollector collector, final boolean clickableOnly) {
        FormattedText pageText = ComponentUtils.mergeStyles(
                this.pageText,
                Style.EMPTY.withoutShadow().withColor(0xFF000000));

//        private static final Style PAGE_TEXT_STYLE = Style.EMPTY.withoutShadow().withColor(-16777216);
        var cachedPageComponents = this.font.split(pageText, 114);

        int left = this.backgroundLeft();
        int top = this.backgroundTop();
        if (!clickableOnly) {
            collector.accept(TextAlignment.RIGHT, left + 148, top + 16, Component.empty());
        }

        int shownLines = Math.min(128 / 9, cachedPageComponents.size());

        for (int i = 0; i < shownLines; i++) {
            FormattedCharSequence component = (FormattedCharSequence) cachedPageComponents.get(i);
            collector.accept(left + 36, top + 30 + i * 9, component);
        }
    }

    @Override
    public void renderBackground(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
        super.renderBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, BOOK_LOCATION, this.backgroundLeft(), this.backgroundTop(), 0.0F, 0.0F, 192, 192, 256, 256);
    }

    private int backgroundLeft() {
        return (this.width - 192) / 2;
    }

    private int backgroundTop() {
        return 2;
    }

    protected int menuControlsTop() {
        return this.backgroundTop() + 192 + 2;
    }

    @Override
    public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
        if (event.button() == 0) {
            ActiveTextCollector.ClickableStyleFinder finder = new ActiveTextCollector.ClickableStyleFinder(this.font, (int)event.x(), (int)event.y());
            this.visitText(finder, true);
            Style clickedStyle = finder.result();
            if (clickedStyle != null && this.handleClickEvent(clickedStyle.getClickEvent())) {
                return true;
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    protected boolean handleClickEvent(@Nullable final ClickEvent event) {
        if (event == null) {
            return false;
        }

        LocalPlayer player = Objects.requireNonNull(this.minecraft.player, "Player not available");

        switch (event) {
            case ClickEvent.ChangePage(int page):
//                this.forcePage(page - 1);
                break;
            case ClickEvent.RunCommand(String command):
                clickCommandAction(player, command, null);
                break;
            default:
                defaultHandleGameClickEvent(event, this.minecraft, this);
        }

        return true;
    }


    @Override
    public boolean isInGameUi() {
        return true;
    }
}
