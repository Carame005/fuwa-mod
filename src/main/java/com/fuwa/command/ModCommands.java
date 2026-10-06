package com.fuwa.command;

import com.fuwa.event.MeteoriteFallEvents;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Debug / admin commands for Fuwa events.
 * <pre>
 * /fuwa meteorite [fuwa|prunce|auto] [player]
 * </pre>
 */
public final class ModCommands {
    private static final SimpleCommandExceptionType INVALID_COMPANION =
            new SimpleCommandExceptionType(Component.translatable("commands.fuwa.meteorite.invalid_companion_hint"));

    private static final SuggestionProvider<CommandSourceStack> COMPANION_SUGGESTIONS =
            (context, builder) -> SharedSuggestionProvider.suggest(List.of("auto", "fuwa", "prunce"), builder);

    private ModCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("fuwa")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("meteorite")
                        .executes(ctx -> forceMeteorite(ctx, null, List.of(ctx.getSource().getPlayerOrException())))
                        .then(Commands.argument("companion", StringArgumentType.word())
                                .suggests(COMPANION_SUGGESTIONS)
                                .executes(ctx -> forceMeteorite(ctx,
                                        StringArgumentType.getString(ctx, "companion"),
                                        List.of(ctx.getSource().getPlayerOrException())))
                                .then(Commands.argument("player", EntityArgument.players())
                                        .executes(ctx -> forceMeteorite(ctx,
                                                StringArgumentType.getString(ctx, "companion"),
                                                EntityArgument.getPlayers(ctx, "player"))))));

        dispatcher.register(root);
    }

    private static int forceMeteorite(CommandContext<CommandSourceStack> ctx, @Nullable String companionArg,
                                      Collection<ServerPlayer> players) throws CommandSyntaxException {
        Boolean spawnFuwa = parseCompanion(companionArg);
        int spawned = 0;

        for (ServerPlayer player : players) {
            if (MeteoriteFallEvents.forceMeteorite(player, spawnFuwa)) {
                spawned++;
            }
        }

        if (spawned == 0) {
            ctx.getSource().sendFailure(Component.translatable("commands.fuwa.meteorite.failed"));
            return 0;
        }

        final int count = spawned;
        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.fuwa.meteorite.success", count),
                true);
        return count;
    }

    @Nullable
    private static Boolean parseCompanion(@Nullable String companionArg) throws CommandSyntaxException {
        if (companionArg == null || companionArg.isEmpty()) {
            return null;
        }

        return switch (companionArg.toLowerCase(Locale.ROOT)) {
            case "auto" -> null;
            case "fuwa" -> true;
            case "prunce" -> false;
            default -> throw INVALID_COMPANION.create();
        };
    }
}
