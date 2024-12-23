package com.mealam.fakemarkdownname.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bluelib.markdown.MarkdownParser;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Shadow
    private Component displayname;

    @Shadow
    public abstract Component getName();

    @Shadow
    protected abstract MutableComponent decorateDisplayNameComponent(MutableComponent mutableComponent);

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    public void getCustomDisplayName(CallbackInfoReturnable<Component> cir) {
        Player player = (Player) (Object) this;

        if (this.displayname == null) {
            this.displayname = ForgeEventFactory.getPlayerDisplayName((Player) (Object) this, this.getName());
        }
        MutableComponent styledName = this.displayname.copy();

        CompoundTag tag = player.getPersistentData();
        if (tag.contains("fakename")) {
            MutableComponent name = Component.literal(tag.getString("fakename"));
            styledName = MarkdownParser.parseMarkdown(name);
        }

        cir.setReturnValue(this.decorateDisplayNameComponent(styledName));
    }
}
