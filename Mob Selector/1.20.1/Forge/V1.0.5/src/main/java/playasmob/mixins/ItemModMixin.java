package playasmob.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import playasmob.CustomEating;
import playasmob.MobData;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;

@Mixin(Item.class)
public abstract class ItemModMixin {
	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void use(Level world, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> callback) {
		MobData data = MobData.get(player);
		ItemStack stack = player.getItemInHand(hand);
		if(data == null || data.typeData == null) return;
		if(stack.isEdible() || !data.typeData.customFood().contains(stack.getItem())) return;
        if(player.canEat(data.typeData.getFoodInfo(stack.getItem()).canAlwaysEat())) {
        	player.startUsingItem(hand);
            callback.setReturnValue(InteractionResultHolder.consume(stack));
        } else {
            callback.setReturnValue(InteractionResultHolder.fail(stack));
        }
    }
    
	@Inject(method = "finishUsingItem", at = @At("HEAD"), cancellable = true)
    private void finishUsingItem(ItemStack stack, Level world, LivingEntity entity, CallbackInfoReturnable<ItemStack> callback) {
		if(entity instanceof CustomEating consumer) {
			ItemStack eating = consumer.eating();
			if(eating.equals(stack))
				callback.setReturnValue(entity.eat(world, stack));
		}
    }
}
