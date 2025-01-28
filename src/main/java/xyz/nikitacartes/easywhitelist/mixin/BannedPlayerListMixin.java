package xyz.nikitacartes.easywhitelist.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.players.UserBanList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(UserBanList.class)
public class BannedPlayerListMixin {
    @Inject(method = "getKeyForUser", at = @At(value = "HEAD", target = "Lnet/minecraft/server/players/UserBanList;getKeyForUser(Lcom/mojang/authlib/GameProfile;)Ljava/lang/String;"), cancellable = true)
    protected void getKeyForUser(GameProfile gameProfile, CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(gameProfile.getName());
        cir.cancel();
    }
}