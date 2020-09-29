package net.kyrptonaught.tooltipfix.mixin;

import com.google.common.collect.Lists;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
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

    @Inject(method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;II)V", at = @At(value = "HEAD"), cancellable = true)
    public void fix(MatrixStack matrices, Text text, int x, int y, CallbackInfo ci) {
        this.renderOrderedTooltip(matrices, Lists.transform(doFix(Collections.singletonList(text), x), Text::asOrderedText), x, y);
        ci.cancel();
    }

    @Inject(method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;II)V", at = @At("HEAD"), cancellable = true)
    public void fix(MatrixStack matrices, List<Text> lines, int x, int y, CallbackInfo ci) {
        this.renderOrderedTooltip(matrices, Lists.transform(doFix(lines, x), Text::asOrderedText), x, y);
        ci.cancel();
    }

    public List<Text> doFix(List<Text> text, int x) {
        text = new ArrayList<>(text);
        //text.add(new LiteralText("MOM MY DICK HURTS REALLY BAD").formatted(Formatting.DARK_GREEN, Formatting.UNDERLINE));
        //text.add(new LiteralText("a a a a a a a a a a a a a a a a a a a "));
        for (int i = 0; i < text.size(); i++) {
            if (x + textRenderer.getWidth(text.get(i)) > width - 20) {
                Style style = text.get(i).getStyle();
                List<String> words = new ArrayList<>(Arrays.asList(text.get(i).getString().split(" ")));

                String newLine = words.remove(0);
                while (words.size() > 0)
                    if (x + textRenderer.getWidth(newLine + " " + words.get(0)) <= width - 20)
                        newLine += " " + words.remove(0);
                    else break;

                text.set(i, new LiteralText(newLine).setStyle(style));
                if (words.size() > 0)
                    text.add(i + 1, new LiteralText(String.join(" ", words)).setStyle(style));
            }
        }
        return text;
    }
}