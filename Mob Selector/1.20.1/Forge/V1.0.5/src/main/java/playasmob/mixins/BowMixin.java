package playasmob.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import playasmob.MobData;
import playasmob.PlayasmobMod;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.stats.Stats;

@Mixin(BowItem.class)
public abstract class BowMixin {
	public BowItem self() {
		return (BowItem)(Object)this;
	}

	@Inject(method = "releaseUsing", at = @At("HEAD"), cancellable = true)
    private void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int useTime, CallbackInfo callback) {
      	if(entity instanceof Player player) {
      		MobData data = MobData.get(player);
			if(data == null || data.typeData == null)
				return;
				
         	boolean flag = player.getAbilities().instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) > 0;
         	ItemStack itemstack = player.getProjectile(stack);

         	int i = self().getUseDuration(stack) - useTime;
         	i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, level, player, i, !itemstack.isEmpty() || flag);
        	if(i < 0) return;

         	if(!itemstack.isEmpty() || flag) {
            	if (itemstack.isEmpty())
               		itemstack = new ItemStack(Items.ARROW);

            	float f = BowItem.getPowerForTime(i);
            	if(!((double)f < 0.1D)) {
               		boolean flag1 = player.getAbilities().instabuild || (itemstack.getItem() instanceof ArrowItem && ((ArrowItem)itemstack.getItem()).isInfinite(itemstack, stack, player));
               		if(!level.isClientSide) {
                  		ArrowItem arrowitem = (ArrowItem)(itemstack.getItem() instanceof ArrowItem ? itemstack.getItem() : Items.ARROW);
                  		AbstractArrow abstractarrow = arrowitem.createArrow(level, itemstack, player);
                  		abstractarrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, f * 3.0F, 1.0F);
                  		if(f == 1.0F)
                    		abstractarrow.setCritArrow(true);

                  		int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
                  		if(j > 0)
                    		abstractarrow.setBaseDamage(abstractarrow.getBaseDamage() + (double)j * 0.5D + 0.5D);

                  		int k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);
                  		if(k > 0)
                     		abstractarrow.setKnockback(k);

                  		if(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0)
                     		abstractarrow.setSecondsOnFire(100);

                  		stack.hurtAndBreak(1, player, (p_289501_) -> {
                     		p_289501_.broadcastBreakEvent(player.getUsedItemHand());
                  		});
                  		if(flag1 || player.getAbilities().instabuild && (itemstack.is(Items.SPECTRAL_ARROW) || itemstack.is(Items.TIPPED_ARROW)))
                     		abstractarrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;

                     	data.typeData.modifyArrow(abstractarrow);
                  		level.addFreshEntity(abstractarrow);
               		}

               		level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
               		if(!flag1 && !player.getAbilities().instabuild) {
                  		itemstack.shrink(1);
                  		if(itemstack.isEmpty())
                     		player.getInventory().removeItem(itemstack);
               		}

               		player.awardStat(Stats.ITEM_USED.get(self()));
            	}
         	}
      	}
      	callback.cancel();
   	}
}
