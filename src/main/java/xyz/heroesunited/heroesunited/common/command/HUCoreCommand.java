package xyz.heroesunited.heroesunited.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
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

public class HUCoreCommand {
    private static final SuggestionProvider<CommandSource> SUGGEST_SUPERPOWERS = (context, builder) -> ISuggestionProvider.suggestResource(HUPackSuperpowers.getSuperpowers().values().stream().map(Superpower::getRegistryName), builder);
    private static final SuggestionProvider<CommandSource> SUGGEST_SUITS = (context, builder) -> ISuggestionProvider.suggestResource(Suit.SUITS.values().stream().map(Suit::getRegistryName), builder);
    public static final DynamicCommandExceptionType DIDNT_EXIST = new DynamicCommandExceptionType((object) -> new TranslationTextComponent("commands.heroesunited.DidntExist", object));

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("heroesunited").requires((player) -> player.hasPermission(2))
                .then(Commands.literal("slowmo").then(Commands.argument("amount", FloatArgumentType.floatArg(0.1F, 8192.0F)).executes((c) -> setSlowMotion(c.getSource(), FloatArgumentType.getFloat(c, "amount")))))
                .then(Commands.literal("suit")
                        .then(Commands.argument("players", EntityArgument.players())
                                .then(Commands.argument("suit", ResourceLocationArgument.id()).suggests(SUGGEST_SUITS)
                                        .executes((c) -> setSuit(c.getSource(), EntityArgument.getPlayers(c, "players"), getSuit(c, "suit"))))))
                .then(Commands.literal("ability")
                        .then(Commands.argument("players", EntityArgument.players())
                                .then(Commands.literal("disable").executes(c -> disableAbility(c.getSource(), EntityArgument.getPlayers(c, "players"))))))
                .then(Commands.literal("superpower")
                        .then(Commands.argument("players", EntityArgument.players())
                                .then(Commands.literal("level").then(Commands.argument("level_int", IntegerArgumentType.integer(0, 1000))
                                        .executes(c -> setSuperpowerLevel(c.getSource(), EntityArgument.getPlayers(c, "players"), IntegerArgumentType.getInteger(c, "level_int")))))
                                .then(Commands.argument("superpower", ResourceLocationArgument.id()).suggests(SUGGEST_SUPERPOWERS)
                                        .executes((c) -> setSuperpower(c.getSource(), EntityArgument.getPlayers(c, "players"), getSuperpower(c, "superpower"))))
                                .then(Commands.literal("remove").executes(c -> removeSuperpower(c.getSource(), EntityArgument.getPlayers(c, "players"))))
                        ))
        );
    }

    private static int setSlowMotion(CommandSource commandSource, float speed) {
        List<ServerPlayerEntity> players = commandSource.getLevel().getPlayers(s -> true);
        for (ServerPlayerEntity player : players) {
            player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent((k) -> {
                k.setSlowMoSpeed(speed);
                k.syncToAll();
            });
        }
        commandSource.sendSuccess(new TranslationTextComponent("commands.heroesunited.slow_mo", speed), true);
        return players.size();
    }

    private static int setSuperpower(CommandSource commandSource, Collection<ServerPlayerEntity> players, Superpower superpower) {
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
            commandSource.sendSuccess(new TranslationTextComponent("commands.heroesunited.superpower.set.single", (players.iterator().next()).getDisplayName(), superpower.getDisplayName()), true);
        else
            commandSource.sendSuccess(new TranslationTextComponent("commands.heroesunited.superpower.set.multiple", i, superpower.getDisplayName()), true);
        return players.size();
    }

    private static int setSuperpowerLevel(CommandSource commandSource, Collection<ServerPlayerEntity> players, int level) {
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
            commandSource.sendSuccess(new TranslationTextComponent("commands.heroesunited.superpowerlevel.set.single", (players.iterator().next()).getDisplayName(), level), true);
        else
            commandSource.sendSuccess(new TranslationTextComponent("commands.heroesunited.superpowerlevel.set.multiple", i, level), true);
        return players.size();
    }

    private static int removeSuperpower(CommandSource commandSource, Collection<ServerPlayerEntity> players) {
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
            commandSource.sendSuccess(new TranslationTextComponent("commands.heroesunited.superpower.removed", (players.iterator().next()).getDisplayName()), true);
        else
            commandSource.sendSuccess(new TranslationTextComponent("commands.heroesunited.superpower.removed.multiple", i), true);
        return players.size();
    }

    public static Superpower getSuperpower(CommandContext<CommandSource> context, String key) throws CommandSyntaxException {
        ResourceLocation resourceLocation = context.getArgument(key, ResourceLocation.class);
        Superpower superpower = HUPackSuperpowers.getSuperpowers().get(resourceLocation);
        if (superpower == null) {
            throw DIDNT_EXIST.create(resourceLocation);
        } else {
            return superpower;
        }
    }

    private static int disableAbility(CommandSource commandSource, Collection<ServerPlayerEntity> players) {
        for (ServerPlayerEntity player : players) {
            AbilityHelper.disable(player);
            HUPlayer.getCap(player).syncToAll();
            HUAbilityCap.getCap(player).syncToAll();
        }
        commandSource.sendSuccess(new TranslationTextComponent("commands.heroesunited.ability.disabled"), true);

        return players.size();
    }

    public static Suit getSuit(CommandContext<CommandSource> context, String key) throws CommandSyntaxException {
        ResourceLocation resourceLocation = context.getArgument(key, ResourceLocation.class);
        Suit suit = Suit.SUITS.get(resourceLocation);
        if (suit == null) {
            throw DIDNT_EXIST.create(resourceLocation);
        } else {
            return suit;
        }
    }

    private static int setSuit(CommandSource commandSource, Collection<ServerPlayerEntity> players, Suit suit) {
        Iterator iterator = players.iterator();
        while (iterator.hasNext()) {
            ServerPlayerEntity pl = (ServerPlayerEntity) iterator.next();
            HUPlayerUtil.setSuitForPlayer(pl, suit);
        }
        TranslationTextComponent display = new TranslationTextComponent(Util.makeDescriptionId("suits", suit.getRegistryName()));

        if (players.size() == 1)
            commandSource.sendSuccess(new TranslationTextComponent("commands.heroesunited.suit.set.single", (players.iterator().next()).getDisplayName(), display), true);
        else
            commandSource.sendSuccess(new TranslationTextComponent("commands.heroesunited.suit.set.multiple", display), true);
        return players.size();
    }
}