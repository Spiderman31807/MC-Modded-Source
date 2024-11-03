package playasmob.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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
import net.minecraft.world.food.FoodProperties;

@Mixin(Item.class)
public abstract class ItemModMixin {
	@Unique
	public Player user;

	public Item self() {
		return (Item)(Object)this;
	}
	
	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void use(Level world, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> callback) {
		MobData data = MobData.get(player);
		ItemStack stack = player.getItemInHand(hand);
		if(data == null || data.typeData == null || !data.typeData.isFood(stack)) return;
		FoodProperties foodInfo = data.typeData.getFoodInfo(stack.getItem());
		if(foodInfo == null) foodInfo = self().getFoodProperties();
		if(foodInfo == null) return;
        if(player.canEat(foodInfo.canAlwaysEat())) {
        	this.user = player;
        	player.startUsingItem(hand);
            callback.setReturnValue(InteractionResultHolder.consume(stack));
        } else {
            callback.setReturnValue(InteractionResultHolder.fail(stack));
        }
    }
    
	@Inject(method = "isEdible", at = @At("HEAD"), cancellable = true)
    private void isEdible(CallbackInfoReturnable<Boolean> callback) {
		if(user != null && user.isUsingItem() && user.getUseItem().getItem() == self() && user instanceof CustomEating consumer && consumer.eating() != null && consumer.eating().getItem() == self())
			callback.setReturnValue(true);
    }
    
	@Inject(method = "getFoodProperties", at = @At("HEAD"), cancellable = true)
    private void getFoodProperties(CallbackInfoReturnable<FoodProperties> callback) {
		if(user != null && user.isUsingItem() && user.getUseItem().getItem() == self() && user instanceof CustomEating consumer && consumer.eating() != null && consumer.eating().getItem() == self())
			callback.setReturnValue(MobData.get(user).typeData.getFoodInfo(self()));
    }
}
