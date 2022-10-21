package com.archyx.aureliumskills.skills.defense;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.Ability;
import com.archyx.aureliumskills.ability.AbilityProvider;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PluginPlayer;
import com.archyx.aureliumskills.skills.Skills;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class DefenseAbilities extends AbilityProvider implements Listener {

    private final Random r = new Random();

    public DefenseAbilities(AureliumSkills plugin) {
        super(plugin, Skills.DEFENSE);
    }

    public void shielding(EntityDamageByEntityEvent event, PluginPlayer pluginPlayer, Player player) {
        if (plugin.getAbilityManager().isEnabled(Ability.SHIELDING)) {
            if (player.isSneaking()) {
                if (pluginPlayer.getAbilityLevel(Ability.SHIELDING) > 0) {
                    double damageReduction = 1 - (getValue(Ability.SHIELDING, pluginPlayer) / 100);
                    event.setDamage(event.getDamage() * damageReduction);
                }
            }
        }
    }

    public void mobMaster(EntityDamageByEntityEvent event, PluginPlayer pluginPlayer) {
        if (plugin.getAbilityManager().isEnabled(Ability.MOB_MASTER)) {
            if (event.getDamager() instanceof LivingEntity && !(event.getDamager() instanceof Player)) {
                if (pluginPlayer.getAbilityLevel(Ability.MOB_MASTER) > 0) {
                    double damageReduction = 1 - (getValue(Ability.MOB_MASTER, pluginPlayer) / 100);
                    event.setDamage(event.getDamage() * damageReduction);
                }
            }
        }
    }

    public void immunity(EntityDamageByEntityEvent event, PluginPlayer pluginPlayer) {
        double chance = getValue(Ability.IMMUNITY, pluginPlayer) / 100;
        if (r.nextDouble() < chance) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void noDebuff(PotionSplashEvent event) {
        if (blockDisabled(Ability.NO_DEBUFF)) return;
        for (LivingEntity entity : event.getAffectedEntities()) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                if (blockAbility(player)) return;
                for (PotionEffect effect : event.getPotion().getEffects()) {
                    PotionEffectType type = effect.getType();
                    if (type.equals(PotionEffectType.POISON) || type.equals(PotionEffectType.UNLUCK) || type.equals(PotionEffectType.WITHER) ||
                            type.equals(PotionEffectType.WEAKNESS) || type.equals(PotionEffectType.SLOW_DIGGING) || type.equals(PotionEffectType.SLOW) ||
                            type.equals(PotionEffectType.HUNGER) || type.equals(PotionEffectType.HARM) || type.equals(PotionEffectType.CONFUSION) ||
                            type.equals(PotionEffectType.BLINDNESS)) {
                        PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(player);
                        if (pluginPlayer == null) return;
                        double chance = getValue(Ability.NO_DEBUFF, pluginPlayer) / 100;
                        if (r.nextDouble() < chance) {
                            if (!player.hasPotionEffect(type)) {
                                event.setIntensity(entity, 0);
                            }
                        }
                    }
                }
            }
        }
    }

    public void noDebuffFire(PluginPlayer pluginPlayer, Player player, LivingEntity entity) {
        if (entity.getEquipment() != null) {
            ItemStack item = entity.getEquipment().getItemInMainHand();
            if (item != null && item.getEnchantmentLevel(Enchantment.FIRE_ASPECT) > 0) {
                double chance = getValue(Ability.NO_DEBUFF, pluginPlayer) / 100;
                if (r.nextDouble() < chance) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.setFireTicks(0);
                        }
                    }.runTaskLater(plugin, 1L);
                }
            }
        }
    }

    @EventHandler
    public void defenseListener(EntityDamageByEntityEvent event) {
        if (OptionL.isEnabled(Skills.DEFENSE)) {
            if (!event.isCancelled()) {
                if (event.getEntity() instanceof Player) {
                    Player player = (Player) event.getEntity();
                    if (blockAbility(player)) return;
                    PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(player);
                    if (pluginPlayer == null) return;
                    if (plugin.getAbilityManager().isEnabled(Ability.NO_DEBUFF)) {
                        if (event.getDamager() instanceof LivingEntity) {
                            LivingEntity entity = (LivingEntity) event.getDamager();
                            noDebuffFire(pluginPlayer, player, entity);
                        }
                    }
                    if (plugin.getAbilityManager().isEnabled(Ability.IMMUNITY)) {
                        immunity(event, pluginPlayer);
                    }
                }
            }
        }
    }

}
