package xyz.nikitacartes.easywhitelist.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.UserWhiteListEntry;
import xyz.nikitacartes.easywhitelist.EasyWhitelist;


public class EasyWhitelistCommand {
    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("easywhitelist")
                .requires(source -> source.hasPermission(3)) // Require OP level 3
                .then(Commands.literal("add")
                        .requires(source -> source.hasPermission(3)) // Require OP level 3
                        .then(Commands.argument("targets", StringArgumentType.word())
                                .executes(ctx -> executeAdd(
                                        ctx.getSource(),
                                        EasyWhitelist.getProfileFromNickname(StringArgumentType.getString(ctx, "targets"))
                                ))
                        )
                )
                .then(Commands.literal("remove")
                        .requires(source -> source.hasPermission(3)) // Require OP level 3
                        .then(Commands.argument("targets", StringArgumentType.word())
                                .executes(ctx -> executeRemove(
                                        ctx.getSource(),
                                        EasyWhitelist.getProfileFromNickname(StringArgumentType.getString(ctx, "targets"))
                                ))
                        )
                )
        );
    }

    private static int executeAdd(CommandSourceStack source, GameProfile profile) {
        if (profile == null) {
            source.sendFailure(Component.literal("Player not found!"));
            return 0;
        }

        PlayerList playerList = source.getServer().getPlayerList();
        playerList.getWhiteList().add(new UserWhiteListEntry(profile));
        source.sendSuccess(Component.literal("Added " + profile.getName() + " to the whitelist"), true);
        return 1;
    }

    private static int executeRemove(CommandSourceStack source, GameProfile profile) {
        if (profile == null) {
            source.sendFailure(Component.literal("Player not found!"));
            return 0;
        }

        PlayerList playerList = source.getServer().getPlayerList();
        playerList.getWhiteList().remove(profile);
        source.sendSuccess(Component.literal("Removed " + profile.getName() + " from the whitelist"), true);
        return 1;
    }
}