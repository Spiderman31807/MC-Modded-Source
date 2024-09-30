
package spidermna318.bonehandles.item;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.AxeItem;

public class StoneAxeItem extends AxeItem {
	public StoneAxeItem() {
		super(new Tier() {
			public int getUses() {
				return 174;
			}

			public float getSpeed() {
				return 4f;
			}

			public float getAttackDamageBonus() {
				return 7f;
			}

			public int getLevel() {
				return 1;
			}

			public int getEnchantmentValue() {
				return 5;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(Blocks.COBBLESTONE), new ItemStack(Blocks.COBBLED_DEEPSLATE), new ItemStack(Blocks.BLACKSTONE));
			}
		}, 1, -3.2f, new Item.Properties());
	}
}
