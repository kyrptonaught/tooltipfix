package net.kyrptonaught.tooltipfix;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Helper {
    public static List<Text> doFix(List<Text> text, int x, int width, TextRenderer textRenderer) {
        text = new ArrayList<>(text);
       for (int i = 0; i < text.size(); i++) {
            if (isTooWide(x, width, textRenderer, text.get(i).getString())) {
                Style style = text.get(i).getStyle();
                List<String> words = new ArrayList<>(Arrays.asList(text.get(i).getString().split(" ")));

                String newLine = words.remove(0);
                if (isTooWide(x, width, textRenderer, newLine)) {
                    String oldLine = newLine;
                    while (isTooWide(x, width, textRenderer, newLine + "-"))
                        newLine = newLine.substring(0, newLine.length() - 1);
                    words.add(0, "-" + oldLine.substring(newLine.length()));
                    newLine = newLine + "-";

                } else
                    while (words.size() > 0)
                        if (!isTooWide(x, width, textRenderer, newLine + " " + words.get(0)))
                            newLine += " " + words.remove(0);
                        else break;

                text.set(i, new LiteralText(newLine).setStyle(style));
                if (words.size() > 0)
                    text.add(i + 1, new LiteralText(String.join(" ", words)).setStyle(style));
            }
        }


        return text;
    }

    private static boolean isTooWide(int x, int width, TextRenderer textRenderer, String line) {
        return x + textRenderer.getWidth(line) > width - 20;
    }
}
