package xyz.heroesunited.heroesunited.common.command;

import com.mojang.brigadier.CommandDispatcher;
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
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.hupacks.HUPackSuperpowers;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;

import java.util.Collection;
import java.util.Iterator;

public class HUCoreCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("heroesunited").requires((player) -> player.hasPermissionLevel(2))
                .then(Commands.literal("suit")
                        .then(Commands.argument("players", EntityArgument.players())
                                .then(Commands.argument("suit", ResourceLocationArgument.resourceLocation()).suggests(SUGGEST_SUITS)
                                        .executes((c) -> setSuit(c.getSource(), EntityArgument.getPlayers(c, "players"), getSuit(c, "suit"))))))
                .then(Commands.literal("ability")
                        .then(Commands.argument("players", EntityArgument.players())
                                .then(Commands.literal("disable").executes(c -> disableAbility(c.getSource(), EntityArgument.getPlayers(c, "players"))))))
                .then(Commands.literal("superpower")
                        .then(Commands.argument("players", EntityArgument.players())
                                .then(Commands.argument("superpower", ResourceLocationArgument.resourceLocation()).suggests(SUGGEST_SUPERPOWERS)
                                        .executes((c) -> setSuperpower(c.getSource(), EntityArgument.getPlayers(c, "players"), getSuperpower(c, "superpower"))))
                                .then(Commands.literal("remove").executes(c -> removeSuperpower(c.getSource(), EntityArgument.getPlayers(c, "players"))))
                        ))
        );
    }

    private static int setSuperpower(CommandSource commandSource, Collection<ServerPlayerEntity> players, Superpower superpower) {
        Iterator iterator = players.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            PlayerEntity pl = (PlayerEntity) iterator.next();
            pl.getCapability(HUPlayerProvider.CAPABILITY).ifPresent((k) -> {
                k.setSuperpower(superpower);
                k.sync();
            });
            if (pl.getCapability(HUPlayerProvider.CAPABILITY).isPresent())
                i++;
        }
        if (i == 1) commandSource.sendFeedback(new TranslationTextComponent("commands.heroesunited.superpower.set.single", (players.iterator().next()).getDisplayName(), superpower.getDisplayName()), true);
        else commandSource.sendFeedback(new TranslationTextComponent("commands.heroesunited.superpower.set.multiple", i, superpower.getDisplayName()), true);
        return players.size();
    }

    private static int removeSuperpower(CommandSource commandSource, Collection<ServerPlayerEntity> players) {
        Iterator iterator = players.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            PlayerEntity pl = (PlayerEntity) iterator.next();
            pl.getCapability(HUPlayerProvider.CAPABILITY).ifPresent((k) -> {
                k.setSuperpower(null);
                k.sync();
            });
            if (pl.getCapability(HUPlayerProvider.CAPABILITY).isPresent())
                i++;
        }
        if (i == 1) commandSource.sendFeedback(new TranslationTextComponent("commands.heroesunited.superpower.removed", (players.iterator().next()).getDisplayName()), true);
        else commandSource.sendFeedback(new TranslationTextComponent("commands.heroesunited.superpower.removed.multiple", i), true);
        return players.size();
    }

    public static Superpower getSuperpower(CommandContext<CommandSource> context, String key) throws CommandSyntaxException {
        ResourceLocation resourceLocation = context.getArgument(key, ResourceLocation.class);
            Superpower superpower = HUPackSuperpowers.getInstance().getSuperpowers().get(resourceLocation);
        if (superpower == null) {
            throw DIDNT_EXIST.create(resourceLocation);
        } else {
            return superpower;
        }
    }


    private static int disableAbility(CommandSource commandSource, Collection<ServerPlayerEntity> players) {
        Iterator iterator = players.iterator();
        while (iterator.hasNext()) {
            PlayerEntity pl = (PlayerEntity) iterator.next();
            pl.getCapability(HUPlayerProvider.CAPABILITY).ifPresent((a) -> {
                AbilityHelper.disable(pl);
                a.sync();
            });
        }
        commandSource.sendFeedback(new TranslationTextComponent("commands.heroesunited.ability.disabled"), true);

        return players.size();
    }

    public static Suit getSuit(CommandContext<CommandSource> context, String key) throws CommandSyntaxException {
        ResourceLocation resourceLocation = context.getArgument(key, ResourceLocation.class);
        Suit suit = Suit.SUITS.getValue(resourceLocation);
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
        TranslationTextComponent display = new TranslationTextComponent(Util.makeTranslationKey("suits", suit.getRegistryName()));

        if (players.size() == 1) commandSource.sendFeedback(new TranslationTextComponent("commands.heroesunited.suit.set.single", (players.iterator().next()).getDisplayName(), display), true);
        else commandSource.sendFeedback(new TranslationTextComponent("commands.heroesunited.suit.set.multiple", display), true);
        return players.size();
    }

    private static final SuggestionProvider<CommandSource> SUGGEST_SUPERPOWERS = (context, builder) -> {
        Collection<Superpower> superpowers = HUPackSuperpowers.getInstance().getSuperpowers().values();
        return ISuggestionProvider.func_212476_a(superpowers.stream().map(Superpower::getRegistryName), builder);
    };

    private static final SuggestionProvider<CommandSource> SUGGEST_SUITS = (context, builder) -> {
        Collection<Suit> suits = Suit.SUITS.getValues();
        return ISuggestionProvider.func_212476_a(suits.stream().map(Suit::getRegistryName), builder);
    };

    public static final DynamicCommandExceptionType DIDNT_EXIST = new DynamicCommandExceptionType((object) ->
            new TranslationTextComponent("commands.heroesunited.DidntExist", object)
    );
}