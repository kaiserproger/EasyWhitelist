package xyz.nikitacartes.easywhitelist.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

import com.mojang.authlib.GameProfile;
import xyz.nikitacartes.easywhitelist.EasyWhitelist;

public class EasyOpCommand {

    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("easyop")
                .requires(source -> {
                    // Check permission using LuckPerms API (if available) or fallback to Forge permissions
                    if (source.getEntity() instanceof ServerPlayer player) {
                        return EasyWhitelist.hasPermission(player, "easywhitelist.commands.easyop", 4);
                    }
                    // If the source is not a player (e.g., console), allow execution with OP level 4
                    return source.hasPermission(4);
                })
                .then(Commands.argument("targets", StringArgumentType.word())
                        .executes(ctx -> executeOp(
                                ctx.getSource(),
                                EasyWhitelist.getProfileFromNickname(StringArgumentType.getString(ctx, "targets"))
                        ))
                )
        );
    }

    private static int executeOp(CommandSourceStack source, GameProfile profile) {
        if (profile == null) {
            source.sendFailure(net.minecraft.network.chat.Component.literal("Player not found!"));
            return 0;
        }

        PlayerList playerList = source.getServer().getPlayerList();

        ServerPlayer player = playerList.getPlayer(profile.getId());
        if (player != null) {
            playerList.op(profile);
            source.sendSuccess(net.minecraft.network.chat.Component.literal("Opped " + profile.getName()), true);
            return 1;
        } else {
            source.sendFailure(net.minecraft.network.chat.Component.literal("Player " + profile.getName() + " is not online!"));
            return 0;
        }
    }
}