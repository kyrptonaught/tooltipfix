package net.kyrptonaught.tooltipfix.mixin;

import com.google.common.collect.Lists;
import net.kyrptonaught.tooltipfix.Helper;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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

    @Shadow
    public abstract List<Text> getTooltipFromItem(ItemStack stack);

    @Inject(method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;II)V", at = @At(value = "HEAD"), cancellable = true)
    public void fix(MatrixStack matrices, Text text, int x, int y, CallbackInfo ci) {
        Helper.set(x, width);
        this.renderOrderedTooltip(matrices, Lists.transform(Helper.doFix(Collections.singletonList(text), textRenderer), Text::asOrderedText), Helper.x, y);
        ci.cancel();
    }

    @Inject(method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;II)V", at = @At("HEAD"), cancellable = true)
    public void fix(MatrixStack matrices, List<Text> lines, int x, int y, CallbackInfo ci) {
        Helper.set(x, width);
        this.renderOrderedTooltip(matrices, Lists.transform(Helper.doFix(lines, textRenderer), Text::asOrderedText), Helper.x, y);
        ci.cancel();
    }

    @Redirect(method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;getTooltipFromItem(Lnet/minecraft/item/ItemStack;)Ljava/util/List;"))
    public List<Text> fix(Screen screen, ItemStack stack, MatrixStack matrices, ItemStack argStack, int x) {
        List<Text> texts = this.getTooltipFromItem(stack);
        Helper.set(x, width);
        return Helper.doFix(texts, textRenderer);
    }
}