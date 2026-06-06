package com.exloran.totemquick.mixin;

import com.exloran.totemquick.TotemManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class TotemPopMixin {

    /**
     * EntityStatusS2CPacket status 35 = totem patlama eventi.
     * Sunucu bu paketi gönderdikten SONRA offhand'i boşaltır,
     * bu yüzden burada swap yapınca yeni totem tam zamanında giriyor.
     */
    @Inject(
        method = "onEntityStatus",
        at = @At("HEAD")
    )
    private void onEntityStatus(EntityStatusS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // Status 35 = TOTEM_OF_UNDYING kullanıldı
        if (packet.getStatus() == 35
                && packet.getEntity(client.world) == client.player) {
            TotemManager.onTotemPop(client);
        }
    }
}
