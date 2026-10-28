
package com.example.client.mixin;

import com.example.client.ExampleModClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class TpsMixin {
    @Inject(method = "onWorldTimeUpdate", at = @At("TAIL"))
    private void ostland$onTimeUpdate(WorldTimeUpdateS2CPacket packet, CallbackInfo ci) {
        ExampleModClient.onTimeUpdate();
    }
}
