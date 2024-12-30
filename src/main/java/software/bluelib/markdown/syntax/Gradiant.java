// Copyright (c) BlueLib. Licensed under the MIT License.

package software.bluelib.markdown.syntax;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import software.bluelib.markdown.ColorConversionUtils;
import software.bluelib.markdown.IsValidUtils;
import software.bluelib.markdown.MarkdownFeature;

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
 * <li>{@link #processComponentTextWithGradiants(String, Style, MutableComponent, Pattern)} - Processes text with Gradiant formatting.</li>
 * <li>{@link #processSiblingsWithGradiants(MutableComponent, Pattern)} - Processes siblings with Gradiant formatting.</li>
 * <li>{@link #appendGradiant(String, String, Style, MutableComponent)} - Appends Gradiant formatted text to a component.</li>
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

    /**
     * Default prefix for Gradiant formatting.
     * <p>
     * Purpose: This variable stores the default prefix for applying Gradiant formatting.<br>
     * When: It is used when checking for the start of a Gradiant formatted section.<br>
     * Where: Used in the {@link Gradiant#Prefix} and {@link Gradiant#Suffix} logic.<br>
     * Additional Info: The default value is "-", but it can be modified using {@link Gradiant#setPrefix(String)} and {@link Gradiant#setSuffix(String)}.<br>
     * </p>
     *
     * @see Gradiant#setSuffix(String)
     * @see Gradiant#setPrefix(String)
     * @see Gradiant#setPrefixSuffix(String, String)
     * @see Gradiant#getSuffix()
     * @see Gradiant#getPrefix()
     * @since 1.6.0
     */
    protected static String Prefix = "_";

    /**
     * Default suffix for Gradiant formatting.
     * <p>
     * Purpose: This variable stores the default suffix for applying Gradiant formatting.<br>
     * When: It is used when checking for the end of a Gradiant formatted section.<br>
     * Where: Used in the {@link Gradiant#Prefix} and {@link Gradiant#Suffix} logic.<br>
     * Additional Info: The default value is "-", but it can be modified using {@link Gradiant#setPrefix(String)} and {@link Gradiant#setSuffix(String)}.<br>
     * </p>
     *
     * @see Gradiant#setSuffix(String)
     * @see Gradiant#setPrefix(String)
     * @see Gradiant#setPrefixSuffix(String, String)
     * @see Gradiant#getSuffix()
     * @see Gradiant#getPrefix()
     * @since 1.6.0
     */
    protected static String Suffix = "_";

    /**
     * Flag that determines whether the Gradiant feature is enabled.
     * <p>
     * Purpose: This variable holds the state of the Gradiant feature (enabled or disabled).<br>
     * When: It is checked whenever the markdown text is processed to determine whether the Gradiant should be applied.<br>
     * Where: It is used in the {@link Gradiant#isFeatureEnabled()} and {@link Gradiant#isGradiantEnabled()} methods.<br>
     * Additional Info: This feature can be enabled or disabled globally through this flag.<br>
     * </p>
     *
     * @see Gradiant#isFeatureEnabled()
     * @see Gradiant#isGradiantEnabled()
     * @since 1.6.0
     */
    public static Boolean isGradiantEnabled = true;

    /**
     * Applies the Gradiant feature to the provided component.
     * <p>
     * Purpose: This method checks whether the feature is enabled and applies the corresponding formatting to the given {@link MutableComponent}. If the feature is disabled, it logs an informational message and returns the original component.<br>
     * When: The method is called when the Gradiant feature needs to be applied to a component.<br>
     * Where: It is called in the {@link MarkdownFeature#apply(MutableComponent)} method.<br>
     * Additional Info: The method uses a {@link Pattern} to find text matching the specified prefix and suffix for Gradiant formatting.<br>
     * </p>
     *
     * @param pComponent The component to apply Gradiant formatting to.
     * @return The component with the applied Gradiant formatting.
     * @author MeAlam
     * @see MarkdownFeature#apply(MutableComponent)
     * @see MutableComponent
     * @see Pattern
     * @since 1.6.0
     */
    public MutableComponent apply(MutableComponent pComponent) {
        if (!isGradiantEnabled) {
            return pComponent;
        }

        Pattern pattern = Pattern.compile(
                Pattern.quote(getPrefix()) +
                        "#([0-9A-Fa-f]{6})(?:,#([0-9A-Fa-f]{6}))*" +
                        Pattern.quote(getSuffix())
        );

        MutableComponent result = Component.empty();

        if (pComponent.getSiblings().isEmpty()) {
            processComponentTextWithGradiants(pComponent.getString(), pComponent.getStyle(), result, pattern);
        } else {
            result = processSiblingsWithGradiants(pComponent, pattern);
        }

        return result;
    }

    /**
     * Processes the text of a component with Gradiant formatting applied.
     * <p>
     * Purpose: This method processes the text of a component by applying specific formatting when the Gradiant pattern is matched.<br>
     * When: This method is called within {@link #processComponentText} to handle Gradiant formatting of text.<br>
     * Where: It is invoked within the {@link #apply} to process the text of a component.<br>
     * Additional Info: The method delegates to {@link #appendGradiant} to apply the formatting for matched text.<br>
     * </p>
     *
     * @param pText          The text to be processed.
     * @param pOriginalStyle The original style of the component.
     * @param pResult        The component to append the processed text to.
     * @param pPattern       The pattern used to match Gradiant formatting.
     * @author MeAlam
     * @see #processComponentText
     * @see #appendGradiant
     * @see #apply
     * @see Style
     * @see Pattern
     * @since 1.6.0
     */
    protected void processComponentTextWithGradiants(String pText, Style pOriginalStyle, MutableComponent pResult, Pattern pPattern) {
        processComponentText(pText, pOriginalStyle, pResult, pPattern,
                (matcher, res) -> {
                    String Gradiant = matcher.group(1);
                    String GradiantText = matcher.group(2);
                    if (Gradiant != null && !Gradiant.isEmpty()) {
                        appendGradiant(GradiantText, Gradiant, pOriginalStyle, res);
                    }
                });
    }

    /**
     * Appends Gradiant formatted text to a component.
     * <p>
     * Purpose: This method appends Gradiant formatted text to a component, applying the specified style.<br>
     * When: This method is called when the Gradiant feature needs to format and append text.<br>
     * Where: It is invoked in {@link #processComponentTextWithGradiants} to handle formatted text.<br>
     * Additional Info: The method ensures that the appropriate style is applied to the appended text.<br>
     * </p>
     *
     * @param pGradiantText      The text to be appended.
     * @param pGradiant         The Gradiant to be applied to the text.
     * @param pOriginalStyle The original style of the component.
     * @param pResult        The component to append the formatted text to.
     * @author MeAlam
     * @see #processComponentTextWithGradiants
     * @see Style
     * @see MutableComponent
     * @see TextColor
     * @see Component
     * @see TextColor#fromRgb(int)
     * @since 1.6.0
     */
    private void appendGradiant(String pGradiantText, String pGradiant, Style pOriginalStyle, MutableComponent pResult) {
        String[] colors = pGradiant.split(",");

        if (colors.length == 2) {
            String color1 = colors[0].trim(); // First color
            String color2 = colors[1].trim(); // Second color
            System.out.println("Color 1: " + color1);
            System.out.println("Color 2: " + color2);

            if (IsValidUtils.isValidColor(color1) && IsValidUtils.isValidColor(color2)) {
                pResult.append(Component.literal(pGradiantText)
                        .setStyle(pOriginalStyle.withColor(TextColor.fromRgb(ColorConversionUtils.parseColorToHexString(color1)))));
            } else {
                pResult.append(Component.literal(pGradiantText).setStyle(pOriginalStyle));
            }
        } else {
            pResult.append(Component.literal(pGradiantText).setStyle(pOriginalStyle));
        }
    }


    /**
     * Processes the siblings of a component with Gradiant formatting.
     * <p>
     * Purpose: This method applies Gradiant formatting to the siblings of a component.<br>
     * When: This method is called to process all siblings of a component in Gradiant formatting.<br>
     * Where: It is called from {@link #apply(MutableComponent)} and related methods.<br>
     * Additional Info: The method iterates over all siblings and applies formatting to each of them individually.<br>
     * </p>
     *
     * @param pComponent The component whose siblings will be processed.
     * @param pPattern   The pattern used to match Gradiant formatting.
     * @return The component with formatted siblings.
     * @author MeAlam
     * @see #apply(MutableComponent)
     * @since 1.6.0
     */
    public MutableComponent processSiblingsWithGradiants(MutableComponent pComponent, Pattern pPattern) {
        return processSiblings(pComponent, pPattern,
                this::processComponentTextWithGradiants);
    }

    /**
     * Checks if the Gradiant feature is enabled.
     * <p>
     * Purpose: This method checks if the feature is enabled and returns the result.<br>
     * When: It is called to determine if the feature is enabled.<br>
     * Where: It is invoked in {@link #apply(MutableComponent)} to check if the feature is enabled.<br>
     * Additional Info: The method is overridden in subclasses to check the specific feature's status.<br>
     * </p>
     *
     * @return {@code true} if the feature is enabled; {@code false} otherwise.
     * @author MeAlam
     * @see #apply(MutableComponent)
     * @since 1.6.0
     */
    @Override
    protected boolean isFeatureEnabled() {
        return isGradiantEnabled;
    }

    /**
     * Gets the name of the feature.
     * <p>
     * Purpose: This method returns the name of the feature.<br>
     * When: It is called to get the name of the feature.<br>
     * Where: It is invoked in {@link #apply(MutableComponent)} to log an informational message.<br>
     * Additional Info: The method is overridden in subclasses to return the specific feature's name.<br>
     * </p>
     *
     * @return The name of the feature.
     * @author MeAlam
     * @see #apply(MutableComponent)
     * @since 1.6.0
     */
    @Override
    protected String getFeatureName() {
        return "Gradiant";
    }

    /**
     * Sets the prefix and suffix used for Gradiant formatting.
     * <p>
     * Purpose: This method allows the user to set both the prefix and suffix for Gradiant formatting.<br>
     * When: It is called to customize the prefix and suffix.<br>
     * Where: Can be invoked from any class or method.<br>
     * Additional Info: The updated prefix and suffix will affect all instances of the {@link Gradiant} feature.<br>
     * </p>
     *
     * @param pPrefix The new prefix for Gradiant.
     * @param pSuffix The new suffix for Gradiant.
     * @author MeAlam
     * @see Gradiant#setSuffix(String)
     * @see Gradiant#getSuffix()
     * @see Gradiant#setPrefix(String)
     * @see Gradiant#getPrefix()
     * @see Gradiant#Suffix
     * @see Gradiant#Prefix
     * @since 1.6.0
     */
    public static void setPrefixSuffix(String pPrefix, String pSuffix) {
        Prefix = pPrefix;
        Suffix = pSuffix;
    }

    /**
     * Sets the prefix used for Gradiant formatting.
     * <p>
     * Purpose: This method allows the user to set the prefix for Gradiant formatting.<br>
     * When: It is called to customize the prefix.<br>
     * Where: Can be invoked from any class or method.<br>
     * Additional Info: The updated prefix will affect all instances of the {@link Gradiant} feature.<br>
     * </p>
     *
     * @param pPrefix The new prefix for Gradiant.
     * @author MeAlam
     * @see Gradiant#setSuffix(String)
     * @see Gradiant#setPrefixSuffix(String, String)
     * @see Gradiant#getSuffix()
     * @see Gradiant#getPrefix()
     * @see Gradiant#Suffix
     * @see Gradiant#Prefix
     * @since 1.6.0
     */
    public static void setPrefix(String pPrefix) {
        Prefix = pPrefix;
    }

    /**
     * Sets the suffix used for Gradiant formatting.
     * <p>
     * Purpose: This method allows the user to set the suffix for Gradiant formatting.<br>
     * When: It is called to customize the suffix.<br>
     * Where: Can be invoked from any class or method.<br>
     * Additional Info: The updated suffix will affect all instances of the {@link Gradiant} feature.<br>
     * </p>
     *
     * @param pSuffix The new suffix for Gradiant.
     * @author MeAlam
     * @see Gradiant#getSuffix()
     * @see Gradiant#setPrefixSuffix(String, String)
     * @see Gradiant#setPrefix(String)
     * @see Gradiant#getPrefix()
     * @see Gradiant#Suffix
     * @see Gradiant#Prefix
     * @since 1.6.0
     */
    public static void setSuffix(String pSuffix) {
        Suffix = pSuffix;
    }

    /**
     * Gets the prefix used for Gradiant formatting.
     * <p>
     * Purpose: This method returns the current prefix used for Gradiant formatting.<br>
     * When: It is called to retrieve the prefix.<br>
     * Where: Can be invoked from any class or method.<br>
     * Additional Info: This returns the default or custom prefix depending on prior settings.<br>
     * </p>
     *
     * @return The current prefix used for Gradiant.
     * @author MeAlam
     * @see Gradiant#setSuffix(String)
     * @see Gradiant#setPrefixSuffix(String, String)
     * @see Gradiant#setPrefix(String)
     * @see Gradiant#getSuffix()
     * @see Gradiant#Suffix
     * @see Gradiant#Prefix
     * @since 1.6.0
     */
    public static String getPrefix() {
        return Prefix;
    }

    /**
     * Gets the suffix used for Gradiant formatting.
     * <p>
     * Purpose: This method returns the current suffix used for Gradiant formatting.<br>
     * When: It is called to retrieve the suffix.<br>
     * Where: Can be invoked from any class or method.<br>
     * Additional Info: This returns the default or custom suffix depending on prior settings.<br>
     * </p>
     *
     * @return The current suffix used for Gradiant.
     * @author MeAlam
     * @see Gradiant#setSuffix(String)
     * @see Gradiant#setPrefixSuffix(String, String)
     * @see Gradiant#setPrefix(String)
     * @see Gradiant#getPrefix()
     * @see Gradiant#Suffix
     * @see Gradiant#Prefix
     * @since 1.6.0
     */
    public static String getSuffix() {
        return Suffix;
    }

    /**
     * Checks if the Gradiant feature is enabled.
     * <p>
     * Purpose: This method returns the current state of the Gradiant feature (enabled or disabled).<br>
     * When: It is called to check if the Gradiant feature is enabled.<br>
     * Where: Can be invoked from any class or method.<br>
     * Additional Info: This method reflects the current state of the {@link #isGradiantEnabled} flag.<br>
     * </p>
     *
     * @return {@code true} if the Gradiant feature is enabled, {@code false} otherwise.
     * @author MeAlam
     * @see Gradiant#isGradiantEnabled
     * @since 1.6.0
     */
    public static Boolean isGradiantEnabled() {
        return isGradiantEnabled;
    }
}
