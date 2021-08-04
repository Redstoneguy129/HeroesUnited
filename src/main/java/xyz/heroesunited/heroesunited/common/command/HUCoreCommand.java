package xyz.heroesunited.heroesunited.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.Superpower;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;
import xyz.heroesunited.heroesunited.hupacks.HUPackSuperpowers;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class HUCoreCommand {
    private static final SuggestionProvider<ServerCommandSource> SUGGEST_SUPERPOWERS = (context, builder) -> CommandSource.suggestIdentifiers(HUPackSuperpowers.getSuperpowers().values().stream().map(Superpower::getRegistryName), builder);
    private static final SuggestionProvider<ServerCommandSource> SUGGEST_SUITS = (context, builder) -> CommandSource.suggestIdentifiers(Suit.SUITS.values().stream().map(Suit::getRegistryName), builder);
    public static final DynamicCommandExceptionType DIDNT_EXIST = new DynamicCommandExceptionType((object) -> new TranslatableText("commands.heroesunited.DidntExist", object));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("heroesunited").requires((player) -> player.hasPermissionLevel(2))
                .then(CommandManager.literal("slowmo").then(CommandManager.argument("amount", FloatArgumentType.floatArg(0.1F, 8192.0F)).executes((c) -> setSlowMotion(c.getSource(), FloatArgumentType.getFloat(c, "amount")))))
                .then(CommandManager.literal("suit")
                        .then(CommandManager.argument("players", EntityArgumentType.players())
                                .then(CommandManager.argument("suit", IdentifierArgumentType.identifier()).suggests(SUGGEST_SUITS)
                                        .executes((c) -> setSuit(c.getSource(), EntityArgumentType.getPlayers(c, "players"), getSuit(c, "suit"))))))
                .then(CommandManager.literal("ability")
                        .then(CommandManager.argument("players", EntityArgumentType.players())
                                .then(CommandManager.literal("disable").executes(c -> disableAbility(c.getSource(), EntityArgumentType.getPlayers(c, "players"))))))
                .then(CommandManager.literal("superpower")
                        .then(CommandManager.argument("players", EntityArgumentType.players())
                                .then(CommandManager.literal("level").then(CommandManager.argument("level_int", IntegerArgumentType.integer(0, 1000))
                                        .executes(c -> setSuperpowerLevel(c.getSource(), EntityArgumentType.getPlayers(c, "players"), IntegerArgumentType.getInteger(c, "level_int")))))
                                .then(CommandManager.argument("superpower", IdentifierArgumentType.identifier()).suggests(SUGGEST_SUPERPOWERS)
                                        .executes((c) -> setSuperpower(c.getSource(), EntityArgumentType.getPlayers(c, "players"), getSuperpower(c, "superpower"))))
                                .then(CommandManager.literal("remove").executes(c -> removeSuperpower(c.getSource(), EntityArgumentType.getPlayers(c, "players"))))
                        ))
        );
    }

    private static int setSlowMotion(ServerCommandSource commandSource, float speed) {
        List<ServerPlayerEntity> players = commandSource.getWorld().getPlayers(s -> true);
        for (ServerPlayerEntity player : players) {
            player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent((k) -> {
                k.setSlowMoSpeed(speed);
                k.syncToAll();
            });
        }
        commandSource.sendFeedback(new TranslatableText("commands.heroesunited.slow_mo", speed), true);
        return players.size();
    }

    private static int setSuperpower(ServerCommandSource commandSource, Collection<ServerPlayerEntity> players, Superpower superpower) {
        Iterator iterator = players.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            PlayerEntity pl = (PlayerEntity) iterator.next();
            HUPackSuperpowers.setSuperpower(pl, superpower);
            HUPlayer.getCap(pl).syncToAll();
            HUAbilityCap.getCap(pl).syncToAll();
            if (pl.getCapability(HUPlayerProvider.CAPABILITY).isPresent())
                i++;
        }
        if (i == 1)
            commandSource.sendFeedback(new TranslatableText("commands.heroesunited.superpower.set.single", (players.iterator().next()).getDisplayName(), superpower.getDisplayName()), true);
        else
            commandSource.sendFeedback(new TranslatableText("commands.heroesunited.superpower.set.multiple", i, superpower.getDisplayName()), true);
        return players.size();
    }

    private static int setSuperpowerLevel(ServerCommandSource commandSource, Collection<ServerPlayerEntity> players, int level) {
        Iterator iterator = players.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            PlayerEntity pl = (PlayerEntity) iterator.next();
            pl.getCapability(HUPlayerProvider.CAPABILITY).ifPresent((k) -> {
                if (HUPackSuperpowers.getSuperpower(pl) !=null) {
                    k.getSuperpowerLevels().get(HUPackSuperpowers.getSuperpower(pl)).setLevel(level);
                    k.syncToAll();
                }
            });
            if (pl.getCapability(HUPlayerProvider.CAPABILITY).isPresent())
                i++;
        }
        if (i == 1)
            commandSource.sendFeedback(new TranslatableText("commands.heroesunited.superpowerlevel.set.single", (players.iterator().next()).getDisplayName(), level), true);
        else
            commandSource.sendFeedback(new TranslatableText("commands.heroesunited.superpowerlevel.set.multiple", i, level), true);
        return players.size();
    }

    private static int removeSuperpower(ServerCommandSource commandSource, Collection<ServerPlayerEntity> players) {
        Iterator iterator = players.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            PlayerEntity pl = (PlayerEntity) iterator.next();
            HUPackSuperpowers.removeSuperpower(pl);
            HUPlayer.getCap(pl).syncToAll();
            HUAbilityCap.getCap(pl).syncToAll();
            if (pl.getCapability(HUPlayerProvider.CAPABILITY).isPresent())
                i++;
        }
        if (i == 1)
            commandSource.sendFeedback(new TranslatableText("commands.heroesunited.superpower.removed", (players.iterator().next()).getDisplayName()), true);
        else
            commandSource.sendFeedback(new TranslatableText("commands.heroesunited.superpower.removed.multiple", i), true);
        return players.size();
    }

    public static Superpower getSuperpower(CommandContext<ServerCommandSource> context, String key) throws CommandSyntaxException {
        Identifier resourceLocation = context.getArgument(key, Identifier.class);
        Superpower superpower = HUPackSuperpowers.getSuperpowers().get(resourceLocation);
        if (superpower == null) {
            throw DIDNT_EXIST.create(resourceLocation);
        } else {
            return superpower;
        }
    }

    private static int disableAbility(ServerCommandSource commandSource, Collection<ServerPlayerEntity> players) {
        for (ServerPlayerEntity player : players) {
            AbilityHelper.disable(player);
            HUPlayer.getCap(player).syncToAll();
            HUAbilityCap.getCap(player).syncToAll();
        }
        commandSource.sendFeedback(new TranslatableText("commands.heroesunited.ability.disabled"), true);

        return players.size();
    }

    public static Suit getSuit(CommandContext<ServerCommandSource> context, String key) throws CommandSyntaxException {
        Identifier resourceLocation = context.getArgument(key, Identifier.class);
        Suit suit = Suit.SUITS.get(resourceLocation);
        if (suit == null) {
            throw DIDNT_EXIST.create(resourceLocation);
        } else {
            return suit;
        }
    }

    private static int setSuit(ServerCommandSource commandSource, Collection<ServerPlayerEntity> players, Suit suit) {
        Iterator iterator = players.iterator();
        while (iterator.hasNext()) {
            ServerPlayerEntity pl = (ServerPlayerEntity) iterator.next();
            HUPlayerUtil.setSuitForPlayer(pl, suit);
        }
        TranslatableText display = new TranslatableText(Util.createTranslationKey("suits", suit.getRegistryName()));

        if (players.size() == 1)
            commandSource.sendFeedback(new TranslatableText("commands.heroesunited.suit.set.single", (players.iterator().next()).getDisplayName(), display), true);
        else
            commandSource.sendFeedback(new TranslatableText("commands.heroesunited.suit.set.multiple", display), true);
        return players.size();
    }
}