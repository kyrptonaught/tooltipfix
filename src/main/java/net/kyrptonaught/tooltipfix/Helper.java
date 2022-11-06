package net.kyrptonaught.tooltipfix;

import net.kyrptonaught.tooltipfix.mixin.OrderedTextToolTipAccessor;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;

public class Helper {

    public static void newFix(List<TooltipComponent> components, TextRenderer textRenderer, int x, int width) {
/*
        int forcedWidth = 0;
        for(TooltipComponent component : components){
            if(!(component instanceof OrderedTextTooltipComponent)){
                int width2 = component.getWidth(textRenderer);
                if(width2 > forcedWidth)
                    forcedWidth = width2;
            }
        }
*/
        wrapNewLines(components);
        wrapLongLines(components, textRenderer, width - 20 - x);
    }


    public static void wrapLongLines(List<TooltipComponent> components, TextRenderer textRenderer, int maxSize) {
        for (int i = 0; i < components.size(); i++) {
            if (components.get(i) instanceof OrderedTextTooltipComponent orderedTextTooltipComponent) {
                Text text = OrderedTextToTextVisitor.get(((OrderedTextToolTipAccessor) orderedTextTooltipComponent).getText());

                List<TooltipComponent> wrapped = textRenderer.wrapLines(text, maxSize).stream().map(TooltipComponent::of).toList();
                components.remove(i);
                components.addAll(i, wrapped);
            }
        }
    }

    public static void wrapNewLines(List<TooltipComponent> components) {
        for (int i = 0; i < components.size(); i++) {
            if (components.get(i) instanceof OrderedTextTooltipComponent orderedTextTooltipComponent) {
                Text text = OrderedTextToTextVisitor.get(((OrderedTextToolTipAccessor) orderedTextTooltipComponent).getText());

                List<Text> children = text.getSiblings();
                for (int j = 0; j < children.size() - 1; j++) {
                    String code = children.get(j).getString() + children.get(j + 1).getString();
                    if (code.equals("\\n")) {
                        components.set(i, TooltipComponent.of(textWithChildren(children, 0, j).asOrderedText()));
                        components.add(i + 1, TooltipComponent.of(textWithChildren(children, j + 2, children.size()).asOrderedText()));
                        break;
                    }
                }
            }
        }
    }

    private static Text textWithChildren(List<Text> children, int from, int end) {
        MutableText text = Text.literal("");

        for (int i = from; i < end; i++)
            text.append(children.get(i));

        return text;
    }

}