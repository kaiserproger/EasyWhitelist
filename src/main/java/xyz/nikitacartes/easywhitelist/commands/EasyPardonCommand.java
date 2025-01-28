package xyz.nikitacartes.easywhitelist.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.UserBanList;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;

import java.util.Collection;

import com.mojang.authlib.GameProfile;
import xyz.nikitacartes.easywhitelist.EasyWhitelist;

@Mod.EventBusSubscriber(modid = "easywhitelist", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EasyPardonCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        registerCommand(event.getDispatcher());
    }

    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("easypardon")
                .requires(source -> source.hasPermission(3)) // Require OP level 3
                .then(Commands.argument("targets", StringArgumentType.word())
                        .executes(ctx -> executePardon(
                                ctx.getSource(),
                                EasyWhitelist.getProfileFromNickname(StringArgumentType.getString(ctx, "targets"))
                        ))
                )
        );
    }

    private static int executePardon(CommandSourceStack source, GameProfile profile) {
        if (profile == null) {
            source.sendFailure(net.minecraft.network.chat.Component.literal("Player not found!"));
            return 0;
        }

        PlayerList playerList = source.getServer().getPlayerList();
        UserBanList banList = playerList.getBans();

        if (banList.isBanned(profile)) {
            banList.remove(profile);
            source.sendSuccess(net.minecraft.network.chat.Component.literal("Pardoned " + profile.getName()), true);
            return 1;
        } else {
            source.sendFailure(net.minecraft.network.chat.Component.literal("Player " + profile.getName() + " is not banned!"));
            return 0;
        }
    }
}