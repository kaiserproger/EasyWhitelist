package xyz.nikitacartes.easywhitelist;

import com.mojang.authlib.GameProfile;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import xyz.nikitacartes.easywhitelist.commands.*;

import java.util.UUID;

@Mod(EasyWhitelist.MOD_ID)
public class EasyWhitelist {
    public static final String MOD_ID = "easywhitelist";

    public EasyWhitelist() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
            modEventBus.addListener(this::setup);
        });
    }

    // Generate an offline UUID from the player's name
    public static GameProfile getProfileFromNickname(String name) {
        UUID offlineUuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes());
        return new GameProfile(offlineUuid, name);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        LogManager.getLogger().info("[EasyWhitelist] Whitelist is now name-based.");
        EasyWhitelistCommand.registerCommand(event.getDispatcher());
        EasyBanCommand.registerCommand(event.getDispatcher());
        EasyPardonCommand.registerCommand(event.getDispatcher());
        EasyOpCommand.registerCommand(event.getDispatcher());
        EasyDeOpCommand.registerCommand(event.getDispatcher());
    }

    private void setup(final FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static boolean hasPermission(ServerPlayer player, String permission, int defaultPermissionLevel) {
        try {
            // Get the LuckPerms API instance
            LuckPerms luckPerms = LuckPermsProvider.get();
            // Get the User object for the player
            User user = luckPerms.getUserManager().getUser(player.getUUID());
            if (user != null) {
                // Check if the player has the permission
                return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
            }
        } catch (IllegalStateException e) {
            // LuckPerms is not loaded or available
            LogManager.getLogger().warn("LuckPerms is not loaded. Falling back to default permissions.");
        } catch (NoClassDefFoundError e) {
            LogManager.getLogger().warn("LuckPerms is not installed. Falling back to default permissions.");
        }
        // Fallback to Forge's default permission system
        return player.hasPermissions(defaultPermissionLevel);
    }
}