package com.archyx.aureliumskills.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PluginPlayer;
import com.archyx.aureliumskills.lang.CommandMessage;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.util.math.NumberUtil;
import com.archyx.aureliumskills.util.text.TextUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

@CommandAlias("mana")
public class ManaCommand extends BaseCommand {

    private final AureliumSkills plugin;

    public ManaCommand(AureliumSkills plugin) {
        this.plugin = plugin;
    }

    @Default
    @CommandPermission("aureliumskills.mana")
    @Description("Display your or another player's current and max mana")
    public void onMana(CommandSender sender, @Flags("other") @CommandPermission("aureliumskills.mana.other") @Optional Player player) {
        if (sender instanceof Player && player == null) {
            Player target = (Player) sender;
            PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(target);
            if (pluginPlayer == null) return;
            Locale locale = pluginPlayer.getLocale();
            sender.sendMessage(AureliumSkills.getPrefix(locale) + TextUtil.replace(Lang.getMessage(CommandMessage.MANA_DISPLAY, locale)
                    , "{current}", NumberUtil.format1(pluginPlayer.getMana())
                    , "{max}", NumberUtil.format1(pluginPlayer.getMaxMana())));
        } else if (player != null) {
            PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(player);
            if (pluginPlayer == null) return;
            Locale locale = pluginPlayer.getLocale();
            sender.sendMessage(AureliumSkills.getPrefix(locale) + TextUtil.replace(Lang.getMessage(CommandMessage.MANA_DISPLAY_OTHER, locale)
                    , "{player}", player.getName()
                    , "{current}", NumberUtil.format1(pluginPlayer.getMana())
                    , "{max}", NumberUtil.format1(pluginPlayer.getMaxMana())));
        } else {
            sender.sendMessage(AureliumSkills.getPrefix(Lang.getDefaultLanguage()) + Lang.getMessage(CommandMessage.MANA_CONSOLE_SPECIFY_PLAYER, Lang.getDefaultLanguage()));
        }
    }

    @Subcommand("add")
    @CommandPermission("aureliumskills.mana.add")
    @CommandCompletion("@players @nothing false|true")
    @Description("Adds mana to a player")
    public void onManaAdd(CommandSender sender, @Flags("other") Player player, double amount, @Default("true") boolean allowOverMax, @Default("false") boolean silent) {
        PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(player);
        if (pluginPlayer == null) return;
        Locale locale = pluginPlayer.getLocale();
        if (amount >= 0) {
            if (allowOverMax && OptionL.getBoolean(Option.WISDOM_ALLOW_OVER_MAX_MANA)) {
                pluginPlayer.setMana(pluginPlayer.getMana() + amount);
                if (!silent) {
                    sender.sendMessage(AureliumSkills.getPrefix(locale) + TextUtil.replace(Lang.getMessage(CommandMessage.MANA_ADD, locale)
                            , "{amount}", NumberUtil.format2(amount)
                            , "{player}", player.getName()));
                }
            } else {
                if (pluginPlayer.getMana() + amount <= pluginPlayer.getMaxMana()) {
                    pluginPlayer.setMana(pluginPlayer.getMana() + amount);
                    if (!silent) {
                        sender.sendMessage(AureliumSkills.getPrefix(locale) + TextUtil.replace(Lang.getMessage(CommandMessage.MANA_ADD, locale)
                                , "{amount}", NumberUtil.format2(amount)
                                , "{player}", player.getName()));
                    }
                } else {
                    double added = pluginPlayer.getMaxMana() - pluginPlayer.getMana();
                    if (added >= 0) {
                        pluginPlayer.setMana(pluginPlayer.getMaxMana());
                        if (!silent) {
                            sender.sendMessage(AureliumSkills.getPrefix(locale) + TextUtil.replace(Lang.getMessage(CommandMessage.MANA_ADD, locale)
                                    , "{amount}", NumberUtil.format2(added)
                                    , "{player}", player.getName()));
                        }
                    } else {
                        if (!silent) {
                            sender.sendMessage(AureliumSkills.getPrefix(locale) + TextUtil.replace(Lang.getMessage(CommandMessage.MANA_ADD, locale)
                                    , "{amount}", String.valueOf(0)
                                    , "{player}", player.getName()));
                        }
                    }
                }
            }
        } else {
            if (!silent) {
                sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.MANA_AT_LEAST_ZERO, locale));
            }
        }
    }

    @Subcommand("remove")
    @CommandPermission("aureliumskills.mana.remove")
    @CommandCompletion("@players")
    @Description("Removes mana from a player")
    public void onManaRemove(CommandSender sender, @Flags("other") Player player, double amount, @Default("false") boolean silent) {
        PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(player);
        if (pluginPlayer == null) return;
        Locale locale = pluginPlayer.getLocale();
        if (amount >= 0) {
            if (pluginPlayer.getMana() - amount >= 0) {
                pluginPlayer.setMana(pluginPlayer.getMana() - amount);
                if (!silent) {
                    sender.sendMessage(AureliumSkills.getPrefix(locale) + TextUtil.replace(Lang.getMessage(CommandMessage.MANA_REMOVE, locale)
                            , "{amount}", NumberUtil.format2(amount)
                            , "{player}", player.getName()));
                }
            } else {
                double removed = pluginPlayer.getMana();
                pluginPlayer.setMana(0);
                if (!silent) {
                    sender.sendMessage(AureliumSkills.getPrefix(locale) + TextUtil.replace(Lang.getMessage(CommandMessage.MANA_REMOVE, locale)
                            , "{amount}", NumberUtil.format2(removed)
                            , "{player}", player.getName()));
                }
            }
        } else {
            if (!silent) {
                sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.MANA_AT_LEAST_ZERO, locale));
            }
        }
    }

    @Subcommand("set")
    @CommandPermission("aureliumskills.mana.set")
    @CommandCompletion("@players @nothing false|true")
    @Description("Sets the mana of player")
    public void onManaSet(CommandSender sender, @Flags("other") Player player, double amount, @Default("true") boolean allowOverMax, @Default("false") boolean silent) {
        PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(player);
        if (pluginPlayer == null) return;
        Locale locale = pluginPlayer.getLocale();
        if (amount >= 0) {
            if (allowOverMax && OptionL.getBoolean(Option.WISDOM_ALLOW_OVER_MAX_MANA)) {
                pluginPlayer.setMana(amount);
                if (!silent) {
                    sender.sendMessage(AureliumSkills.getPrefix(locale) + TextUtil.replace(Lang.getMessage(CommandMessage.MANA_SET, locale)
                            , "{amount}", NumberUtil.format2(amount)
                            , "{player}", player.getName()));
                }
            } else {
                if (amount <= pluginPlayer.getMaxMana()) {
                    pluginPlayer.setMana(amount);
                    if (!silent) {
                        sender.sendMessage(AureliumSkills.getPrefix(locale) + TextUtil.replace(Lang.getMessage(CommandMessage.MANA_SET, locale)
                                , "{amount}", NumberUtil.format2(amount)
                                , "{player}", player.getName()));
                    }
                } else {
                    pluginPlayer.setMana(pluginPlayer.getMaxMana());
                    if (!silent) {
                        sender.sendMessage(AureliumSkills.getPrefix(locale) + TextUtil.replace(Lang.getMessage(CommandMessage.MANA_SET, locale)
                                , "{amount}", NumberUtil.format2(pluginPlayer.getMaxMana())
                                , "{player}", player.getName()));
                    }
                }
            }
        } else {
            if (!silent) {
                sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.MANA_AT_LEAST_ZERO, locale));
            }
        }
    }

}
