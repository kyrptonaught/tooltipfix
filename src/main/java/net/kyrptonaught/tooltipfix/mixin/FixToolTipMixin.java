package net.kyrptonaught.tooltipfix.mixin;

import com.google.common.collect.Lists;
import net.kyrptonaught.tooltipfix.Helper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(Screen.class)
public abstract class FixToolTipMixin {
    @Shadow
    protected TextRenderer textRenderer;
    @Shadow
    public int width;

    @Shadow
    public abstract void renderOrderedTooltip(MatrixStack matrices, List<? extends OrderedText> lines, int x, int y);

    /**
     * @author Kyrptonaught
     * @reason Fix
     */
    @Overwrite
    public void renderTooltip(MatrixStack matrices, Text text, int x, int y) {
        Helper.set(x,width);
        this.renderOrderedTooltip(matrices, Lists.transform(Helper.doFix(Collections.singletonList(text), textRenderer), Text::asOrderedText), Helper.x, y);
    }

    /**
     * @author Kyrptonaught
     * @reason Fix
     */
    @Overwrite
    public void renderTooltip(MatrixStack matrices, List<Text> lines, int x, int y) {
        Helper.set(x,width);
        this.renderOrderedTooltip(matrices, Lists.transform(Helper.doFix(lines, textRenderer), Text::asOrderedText), Helper.x, y);
    }
}