// Copyright (c) BlueLib. Licensed under the MIT License.

package software.bluelib.markdown.syntax;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import software.bluelib.markdown.ColorConversionUtils;
import software.bluelib.markdown.IsValidUtils;
import software.bluelib.markdown.MarkdownFeature;

import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class for handling Gradiant formatting in Markdown.
 * <p>
 * Purpose: This class applies Gradiant formatting to the content of a {@link MutableComponent} based on the feature's {@link #Prefix} and {@link #Suffix}.<br>
 * When: The Gradiant formatting is applied when the {@link #apply(MutableComponent)} method is called.<br>
 * Where: The formatting is applied to the content of the component and its siblings, if any.<br>
 * Additional Info: This class supports handling Gradiant formatting in Markdown and can process both single text and text with components.<br>
 * </p>
 * Key Methods:
 * <ul>
 * <li>{@link #apply(MutableComponent)} - Applies the Gradiant feature to a given component.</li>
 * <li>{@link #processGradiantTextWithGradiants(String, Style, MutableComponent, Pattern)} - Processes text with Gradiant formatting.</li>
 * <li>{@link #processSiblingsWithGradiants(MutableComponent, Pattern)} - Processes siblings with Gradiant formatting.</li>
 * <li>{@link #isFeatureEnabled()} - Checks if the feature is enabled.</li>
 * <li>{@link #getFeatureName()} - Gets the name of the feature.</li>
 * <li>{@link #setPrefixSuffix(String, String)} - Sets the prefix and suffix for Gradiant formatting.</li>
 * <li>{@link #setPrefix(String)} - Sets the prefix for Gradiant formatting.</li>
 * <li>{@link #setSuffix(String)} - Sets the suffix for Gradiant formatting.</li>
 * <li>{@link #getPrefix()} - Gets the prefix for Gradiant formatting.</li>
 * <li>{@link #getSuffix()} - Gets the suffix for Gradiant formatting.</li>
 * <li>{@link #isGradiantEnabled()} - Checks if the Gradiant feature is enabled.</li>
 * </ul>
 *
 * @author MeAlam
 * @version 1.6.0
 * @see MutableComponent
 * @see Pattern
 * @see Style
 * @see MarkdownFeature
 * @see ColorConversionUtils
 * @since 1.6.0
 */
public class Gradiant extends MarkdownFeature {

    protected static String Prefix = "_";
    protected static String Suffix = "_";
    public static Boolean isGradiantEnabled = true;

    public MutableComponent apply(MutableComponent pComponent) {
        if (!isGradiantEnabled) {
            return pComponent;
        }

        Pattern pattern = Pattern.compile(Pattern.quote(getPrefix()) +
                "#([0-9A-Fa-f]{6})(?:,#([0-9A-Fa-f]{6}))*" +
                Pattern.quote(getSuffix()) + "(.*)");

        MutableComponent result = Component.empty();

        if (pComponent.getSiblings().isEmpty()) {
            processGradiantTextWithGradiants(pComponent.getString(), pComponent.getStyle(), result, pattern);
        } else {
            result = processSiblingsWithGradiants(pComponent, pattern);
        }

        return result;
    }

    protected void processGradiantTextWithGradiants(String pText, Style pOriginalStyle, MutableComponent pResult, Pattern pPattern) {
        processComponentText(pText, pOriginalStyle, pResult, pPattern,
                (matcher, res) -> {
                    String gradientStart = matcher.group(1); // First color
                    String gradientEnd = matcher.group(2);   // Second color
                    String gradientText = matcher.group(3);  // Text

                    if (gradientText.startsWith("(") && gradientText.endsWith(")")) {
                        gradientText = gradientText.substring(1, gradientText.length() - 1);
                    }

                    applyGradientToText(gradientText, gradientStart, gradientEnd, pOriginalStyle, res);
                });
    }

    private void applyGradientToText(String pGradiantText, String pGradiantStart, String pGradientEnd, Style pOriginalStyle, MutableComponent pResult) {

        if (IsValidUtils.isValidColor(pGradiantStart) && IsValidUtils.isValidColor(pGradientEnd)) {
            int startColor = ColorConversionUtils.parseColorToHexString(pGradiantStart);
            int endColor = ColorConversionUtils.parseColorToHexString(pGradientEnd);

            char[] characters = pGradiantText.toCharArray();
            int textLength = characters.length;

            for (int i = 0; i < textLength; i++) {
                float ratio = (float) i / (textLength - 1);
                int interpolatedColor = interpolateColor(startColor, endColor, ratio);

                pResult.append(Component.literal(String.valueOf(characters[i]))
                        .setStyle(pOriginalStyle.withColor(TextColor.fromRgb(interpolatedColor))));
            }
        } else {
            pResult.append(Component.literal(pGradiantText).setStyle(pOriginalStyle));
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


    public MutableComponent processSiblingsWithGradiants(MutableComponent pComponent, Pattern pPattern) {
        return processSiblings(pComponent, pPattern,
                this::processGradiantTextWithGradiants);
    }

    @Override
    protected boolean isFeatureEnabled() {
        return isGradiantEnabled;
    }

    @Override
    protected String getFeatureName() {
        return "Gradiant";
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

    public static Boolean isGradiantEnabled() {
        return isGradiantEnabled;
    }
}