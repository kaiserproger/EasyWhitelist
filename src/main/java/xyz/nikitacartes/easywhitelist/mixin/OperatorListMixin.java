package xyz.nikitacartes.easywhitelist.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.players.ServerOpList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerOpList.class)
public class OperatorListMixin {
    @Inject(method = "getKeyForUser", at = @At(value = "HEAD", target = "Lnet/minecraft/server/players/ServerOpList;getKeyForUser(Lcom/mojang/authlib/GameProfile;)Ljava/lang/String;"), cancellable = true)
    protected void getKeyForUser(GameProfile gameProfile, CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(gameProfile.getName());
        cir.cancel();
    }
}