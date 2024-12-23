package com.mealam.fakemarkdownname;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import software.bluelib.markdown.MarkdownParser;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RenderEvents {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void renderName(PlayerEvent.NameFormat pEvent) {
        CompoundTag tag = pEvent.getEntity().getPersistentData();
        MutableComponent name = Component.literal(tag.getString("fakename"));
        MutableComponent styledName = MarkdownParser.parseMarkdown(name);

        System.out.println("fakename: " + styledName);
        if (tag.contains("fakename")) {
            styledName.withStyle(ChatFormatting.BOLD)
                    .withStyle(ChatFormatting.RED);
            pEvent.setDisplayname(styledName);
        } else {
            pEvent.setDisplayname(pEvent.getUsername());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onTabListRender(PlayerEvent.TabListNameFormat pEvent) {
        CompoundTag tag = pEvent.getEntity().getPersistentData();
        MutableComponent name = Component.literal(tag.getString("fakename"));
        MutableComponent styledName = MarkdownParser.parseMarkdown(name);

        System.out.println("fakename: " + styledName);
        if (tag.contains("fakename")) {
            pEvent.setDisplayName(styledName);
        } else {
            pEvent.setDisplayName(pEvent.getDisplayName());
        }
    }


}
