package playasmob.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.jetbrains.annotations.Nullable;

import playasmob.RegenModifer;
import playasmob.MobData;

import net.minecraft.world.food.FoodData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {
	@Unique
   	public int healTimer;
	@Unique
	public Player owner;

	public FoodData self() {
		return (FoodData)(Object)this;
	}
	
	public MobData getData() {
		if(this.owner != null)
			return MobData.get(this.owner);
		return null;
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void trackOwner(Player player, CallbackInfo callback) {
		this.owner = player;
	}

	@ModifyVariable(method = "tick", at = @At(value = "STORE"))
    private boolean disablePassiveRegen(boolean gamerule, Player player) {
    	MobData data = getData();
        if(data == null || data.typeData == null || data.typeData.modifyRegen() == RegenModifer.None)
        	return gamerule;
        if(data.typeData.modifyRegen() == RegenModifer.Constant && this.healTimer++ % 40 == 0)
        	player.heal(1);
        return false;
    }

	@Inject(method = "eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
	private void eat(Item item, ItemStack stack, CallbackInfo callback) {
    	MobData data = getData();
    	if(data == null || data.typeData == null)
    		return;
		if(data.typeData.notFood().contains(item))
    		callback.cancel();
		if(item.isEdible() || data.typeData.customFood().contains(stack.getItem())) {
			FoodProperties foodInfo = data.typeData.customFood().contains(stack.getItem()) ? data.typeData.getFoodInfo(item) : stack.getFoodProperties(this.owner);
			float saturation = data.typeData.getSaturation(stack, foodInfo.getSaturationModifier());
			int nutrition = data.typeData.getNutrition(stack, foodInfo.getNutrition());
	    	self().eat(nutrition, saturation);
	    	callback.cancel();
		}
    }
}
