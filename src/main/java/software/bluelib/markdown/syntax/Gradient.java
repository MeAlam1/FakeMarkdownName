// Copyright (c) BlueLib. Licensed under the MIT License.

package software.bluelib.markdown.syntax;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import software.bluelib.markdown.ColorConversionUtils;
import software.bluelib.markdown.IsValidUtils;
import software.bluelib.markdown.MarkdownFeature;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class for handling Gradient formatting in Markdown.
 * <p>
 * Purpose: This class applies Gradient formatting to the content of a {@link MutableComponent} based on the feature's {@link #Prefix} and {@link #Suffix}.<br>
 * When: The Gradient formatting is applied when the {@link #apply(MutableComponent)} method is called.<br>
 * Where: The formatting is applied to the content of the component and its siblings, if any.<br>
 * Additional Info: This class supports handling Gradient formatting in Markdown and can process text with any number of gradient colors.<br>
 * </p>
 * Key Methods:
 * <ul>
 * <li>{@link #apply(MutableComponent)} - Applies the Gradient feature to a given component.</li>
 * <li>{@link #processGradientText(String, Style, MutableComponent, Pattern)} - Processes text with Gradient formatting.</li>
 * <li>{@link #processSiblingsWithGradients(MutableComponent, Pattern)} - Processes siblings with Gradient formatting.</li>
 * <li>{@link #isFeatureEnabled()} - Checks if the feature is enabled.</li>
 * <li>{@link #getFeatureName()} - Gets the name of the feature.</li>
 * <li>{@link #setPrefixSuffix(String, String)} - Sets the prefix and suffix for Gradient formatting.</li>
 * <li>{@link #setPrefix(String)} - Sets the prefix for Gradient formatting.</li>
 * <li>{@link #setSuffix(String)} - Sets the suffix for Gradient formatting.</li>
 * <li>{@link #getPrefix()} - Gets the prefix for Gradient formatting.</li>
 * <li>{@link #getSuffix()} - Gets the suffix for Gradient formatting.</li>
 * <li>{@link #isGradientEnabled()} - Checks if the Gradient feature is enabled.</li>
 * </ul>
 *
 * @author MeAlam
 * @version 1.7.0
 * @see MutableComponent
 * @see Pattern
 * @see Style
 * @see MarkdownFeature
 * @see ColorConversionUtils
 * @since 1.7.0
 */
public class Gradient extends MarkdownFeature {

    protected static String Prefix = "_";
    protected static String Suffix = "_";
    public static Boolean isGradientEnabled = true;

    public MutableComponent apply(MutableComponent pComponent) {
        if (!isGradientEnabled) {
            return pComponent;
        }

        Pattern pattern = Pattern.compile(Pattern.quote(getPrefix()) +
                "#([0-9A-Fa-f]{6}(?:,#([0-9A-Fa-f]{6}))*)" +
                Pattern.quote(getSuffix()) + "\\((.*?)\\)");

        MutableComponent result = Component.empty();

        if (pComponent.getSiblings().isEmpty()) {
            processGradientText(pComponent.getString(), pComponent.getStyle(), result, pattern);
        } else {
            result = processSiblingsWithGradients(pComponent, pattern);
        }

        return result;
    }

    protected void processGradientText(String pText, Style pOriginalStyle, MutableComponent pResult, Pattern pPattern) {
        processComponentText(pText, pOriginalStyle, pResult, pPattern,
                (matcher, res) -> {
                    List<Integer> colors = extractColorsFromMatcher(matcher);
                    String gradientText = matcher.group(matcher.groupCount());

                    applyGradientToText(gradientText, colors, pOriginalStyle, res);
                });
    }

    private List<Integer> extractColorsFromMatcher(Matcher matcher) {
        List<Integer> colors = new ArrayList<>();

        String colorGroup = matcher.group(1);
        String[] colorArray = colorGroup.split(",");
        for (String color : colorArray) {
            if (IsValidUtils.isValidColor(color)) {
                colors.add(ColorConversionUtils.parseColorToHexString(color));
            }
        }

        System.out.println("Colors: " + colors);

        return colors;
    }

    private void applyGradientToText(String pGradientText, List<Integer> pColors, Style pOriginalStyle, MutableComponent pResult) {
        if (pColors.isEmpty()) {
            pResult.append(Component.literal(pGradientText).setStyle(pOriginalStyle));
            return;
        }

        if (pColors.size() == 1) {
            int color = pColors.get(0);
            pResult.append(Component.literal(pGradientText).setStyle(pOriginalStyle.withColor(TextColor.fromRgb(color))));
            return;
        }

        char[] characters = pGradientText.toCharArray();
        int textLength = characters.length;
        int colorCount = pColors.size();
        int segmentLength = textLength / (colorCount - 1);
        int remainder = textLength % (colorCount - 1);

        int charIndex = 0;

        for (int colorIndex = 0; colorIndex < colorCount - 1; colorIndex++) {
            int startColor = pColors.get(colorIndex);
            int endColor = pColors.get(colorIndex + 1);

            int currentSegmentLength = segmentLength + (colorIndex < remainder ? 1 : 0);

            for (int i = 0; i < currentSegmentLength && charIndex < textLength; i++, charIndex++) {
                float positionRatio = (float) i / (currentSegmentLength - 1);
                int interpolatedColor = interpolateColor(startColor, endColor, positionRatio);

                pResult.append(Component.literal(String.valueOf(characters[charIndex]))
                        .setStyle(pOriginalStyle.withColor(TextColor.fromRgb(interpolatedColor))));
            }
        }
    }


    private int interpolateColor(int startColor, int endColor, float ratio) {
        int startR = (startColor >> 16) & 0xFF;
        int startG = (startColor >> 8) & 0xFF;
        int startB = startColor & 0xFF;

        int endR = (endColor >> 16) & 0xFF;
        int endG = (endColor >> 8) & 0xFF;
        int endB = endColor & 0xFF;

        int r = (int) (startR + (endR - startR) * ratio);
        int g = (int) (startG + (endG - startG) * ratio);
        int b = (int) (startB + (endB - startB) * ratio);

        return (r << 16) | (g << 8) | b;
    }

    public MutableComponent processSiblingsWithGradients(MutableComponent pComponent, Pattern pPattern) {
        return processSiblings(pComponent, pPattern, this::processGradientText);
    }

    @Override
    protected boolean isFeatureEnabled() {
        return isGradientEnabled;
    }

    @Override
    protected String getFeatureName() {
        return "Gradient";
    }

    public static void setPrefixSuffix(String pPrefix, String pSuffix) {
        Prefix = pPrefix;
        Suffix = pSuffix;
    }

    public static void setPrefix(String pPrefix) {
        Prefix = pPrefix;
    }

    public static void setSuffix(String pSuffix) {
        Suffix = pSuffix;
    }

    public static String getPrefix() {
        return Prefix;
    }

    public static String getSuffix() {
        return Suffix;
    }

    public static Boolean isGradientEnabled() {
        return isGradientEnabled;
    }
}
