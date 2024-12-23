// Copyright (c) BlueLib. Licensed under the MIT License.

package software.bluelib.event;

import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import software.bluelib.markdown.MarkdownParser;

/**
 * A {@code public class} responsible for handling server chat events and formatting chat messages using Markdown.
 * <p>
 * This class listens for chat messages on the server and applies Markdown formatting to the message content
 * using the {@link MarkdownParser}. The formatted message is then set as the new message to be broadcasted.
 * </p>
 *
 * @author MeAlam
 * @version 1.4.0
 * @since 1.1.0
 */
@Mod.EventBusSubscriber
public class ChatHandler {

    /**
     * A {@code public static} method that handles server chat events and formats the message using Markdown.
     * <p>
     * This method listens to the {@link ServerChatEvent} and applies Markdown formatting to the message using
     * the {@link MarkdownParser}. The formatted message is then set as the new message.
     * </p>
     *
     * @param pEvent {@link ServerChatEvent} - The event containing the original chat message to format.
     * @author MeAlam
     * @since 1.1.0
     */
    @SubscribeEvent
    public static void onServerChat(ServerChatEvent pEvent) {
        Component originalMessage = pEvent.getMessage();
        Component formattedMessage = MarkdownParser.parseMarkdown(originalMessage);
        pEvent.setMessage(formattedMessage);
    }

    @SubscribeEvent
    public static void onChatReceived(ClientChatReceivedEvent pEvent) {
        Component originalMessage = pEvent.getMessage();
        Component formattedMessage = MarkdownParser.parseMarkdown(originalMessage);
        pEvent.setMessage(formattedMessage);
    }
}
