package xyz.nikitacartes.easywhitelist.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.UserBanList;
import net.minecraft.server.players.UserBanListEntry;
import xyz.nikitacartes.easywhitelist.EasyWhitelist;

public class EasyBanCommand {
    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("easyban")
                .requires(source -> {
                    // Check permission using LuckPerms API
                    if (source.getEntity() instanceof ServerPlayer player) {
                        return EasyWhitelist.hasPermission(player, "easywhitelist.commands.easyban", 3);
                    }
                    return source.hasPermission(3);
                })
                .then(Commands.argument("targets", StringArgumentType.word())
                        .executes(ctx -> banPlayer(
                                ctx.getSource(),
                                EasyWhitelist.getProfileFromNickname(StringArgumentType.getString(ctx, "targets")),
                                null
                        ))
                        .then(Commands.argument("reason", MessageArgument.message())
                                .executes(ctx -> banPlayer(
                                        ctx.getSource(),
                                        EasyWhitelist.getProfileFromNickname(StringArgumentType.getString(ctx, "targets")),
                                        MessageArgument.getMessage(ctx, "reason").getString()
                                ))
                        )
                )
        );
    }

    private static int banPlayer(CommandSourceStack source, GameProfile profile, String reason) {
        if (profile == null) {
            source.sendFailure(Component.literal("Player not found!"));
            return 0;
        }

        UserBanList banList = source.getServer().getPlayerList().getBans();
        if (!banList.isBanned(profile)) {
            source.sendFailure(Component.translatable("commands.ban.failed"));
        }

        PlayerList playerList = source.getServer().getPlayerList();
        ServerPlayer player = playerList.getPlayer(profile.getId());

        UserBanListEntry banListEntry = new UserBanListEntry(profile, null, source.getTextName(), null, reason);

        banList.add(banListEntry);

        if (player != null) {
            source.sendSuccess(Component.literal("Banned player " + profile.getName()), true);
            player.connection.disconnect(Component.translatable("multiplayer.disconnect.banned"));
        }

        return 1;
    }
}