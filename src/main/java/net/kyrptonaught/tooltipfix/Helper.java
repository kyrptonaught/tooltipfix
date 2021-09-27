package net.kyrptonaught.tooltipfix;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Helper {
    public static int x, width;
    private static int mouseX;
    static boolean flipped;

    public static void set(int x, int width) {
        Helper.x = Math.max(0, x);
        Helper.width = width - 20;
        mouseX = Helper.x - 24;
        flipped = false; //width - x <= 100; // auto flip if the biggest possible length is less than 100; is this needed?
    }

    public static List<Text> doFix(List<Text> text, TextRenderer textRenderer) {
        text = new ArrayList<>(text);
        if (text.size() != 0 && (text.size() != 1 || text.get(0).getString().length() > 12)) {
            for (int i = 0; i < text.size(); i++) {
                if (isTooWide(textRenderer, text.get(i).getString())) {
                    Style style = text.get(i).getStyle();
                    List<String> words = new ArrayList<>(Arrays.asList(text.get(i).getString().split(" ")));
                    if (words.isEmpty()) return text;

                    String newLine = words.remove(0);
                    if (isTooWide(textRenderer, newLine)) {
                        if (!flipped && x > width / 2) {
                            flipped = true;
                            return doFix(text, textRenderer);
                        }
                        String oldLine = newLine;
                        while (isTooWide(textRenderer, newLine + "-")) {
                            newLine = newLine.substring(0, newLine.length() - 1);
                        }
                        words.add(0, "-" + oldLine.substring(newLine.length()));
                        newLine = newLine + "-";
                    } else
                        while (words.size() > 0 && !isTooWide(textRenderer, newLine + " " + words.get(0))) {
                            newLine = newLine + " " + words.remove(0);
                        }
                    text.set(i, new LiteralText(newLine).setStyle(style));
                    if (words.size() > 0)
                        text.add(i + 1, new LiteralText(String.join(" ", words)).setStyle(style));
                }
            }
        }
        return text;
    }

    private static boolean isTooWide(TextRenderer textRenderer, String line) {
        if (flipped) {
            if (mouseX - textRenderer.getWidth(line) > 0) {
                attemptUpdateMaxWidth(line, textRenderer);
                return false;
            }
            return true;
        }
        return x + textRenderer.getWidth(line) > width;
    }

    private static void attemptUpdateMaxWidth(String newLine, TextRenderer textRenderer) {
        int lineWidth = textRenderer.getWidth(newLine);
        if (lineWidth > mouseX - x) // new line longer than previous "max width"
            x = mouseX - lineWidth;
    }
}