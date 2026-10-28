package com.example.client.mixin;

import com.example.client.ExampleModClient;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class TpsMixin {
    @Inject(method = "handleSetTime", at = @At("TAIL"))
    private void ostland$onTimeUpdate(ClientboundSetTimePacket packet, CallbackInfo ci) {
        ExampleModClient.onTimeUpdate();
    }
}
