
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package spidermna318.bonehandles.init;

import spidermna318.bonehandles.item.WoodenSwordItem;
import spidermna318.bonehandles.item.WoodenShovelItem;
import spidermna318.bonehandles.item.WoodenPickaxeItem;
import spidermna318.bonehandles.item.WoodenHoeItem;
import spidermna318.bonehandles.item.WoodenAxeItem;
import spidermna318.bonehandles.item.TemplateItem;
import spidermna318.bonehandles.item.StoneSwordItem;
import spidermna318.bonehandles.item.StoneShovelItem;
import spidermna318.bonehandles.item.StonePickaxeItem;
import spidermna318.bonehandles.item.StoneHoeItem;
import spidermna318.bonehandles.item.StoneAxeItem;
import spidermna318.bonehandles.item.NetheriteSwordItem;
import spidermna318.bonehandles.item.NetheriteShovelItem;
import spidermna318.bonehandles.item.NetheritePickaxeItem;
import spidermna318.bonehandles.item.NetheriteHoeItem;
import spidermna318.bonehandles.item.NetheriteAxeItem;
import spidermna318.bonehandles.item.IronSwordItem;
import spidermna318.bonehandles.item.IronShovelItem;
import spidermna318.bonehandles.item.IronPickaxeItem;
import spidermna318.bonehandles.item.IronHoeItem;
import spidermna318.bonehandles.item.IronAxeItem;
import spidermna318.bonehandles.item.GoldenSwordItem;
import spidermna318.bonehandles.item.GoldenShovelItem;
import spidermna318.bonehandles.item.GoldenPickaxeItem;
import spidermna318.bonehandles.item.GoldenHoeItem;
import spidermna318.bonehandles.item.GoldenAxeItem;
import spidermna318.bonehandles.item.DiamondSwordItem;
import spidermna318.bonehandles.item.DiamondShovelItem;
import spidermna318.bonehandles.item.DiamondPickaxeItem;
import spidermna318.bonehandles.item.DiamondHoeItem;
import spidermna318.bonehandles.item.DiamondAxeItem;
import spidermna318.bonehandles.BoneHandlesMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.Item;

public class BoneHandlesModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, BoneHandlesMod.MODID);
	public static final RegistryObject<Item> WOODEN_AXE = REGISTRY.register("wooden_axe", () -> new WoodenAxeItem());
	public static final RegistryObject<Item> STONE_AXE = REGISTRY.register("stone_axe", () -> new StoneAxeItem());
	public static final RegistryObject<Item> IRON_AXE = REGISTRY.register("iron_axe", () -> new IronAxeItem());
	public static final RegistryObject<Item> GOLDEN_AXE = REGISTRY.register("golden_axe", () -> new GoldenAxeItem());
	public static final RegistryObject<Item> DIAMOND_AXE = REGISTRY.register("diamond_axe", () -> new DiamondAxeItem());
	public static final RegistryObject<Item> NETHERITE_AXE = REGISTRY.register("netherite_axe", () -> new NetheriteAxeItem());
	public static final RegistryObject<Item> TEMPLATE = REGISTRY.register("template", () -> new TemplateItem());
	public static final RegistryObject<Item> WOODEN_PICKAXE = REGISTRY.register("wooden_pickaxe", () -> new WoodenPickaxeItem());
	public static final RegistryObject<Item> STONE_PICKAXE = REGISTRY.register("stone_pickaxe", () -> new StonePickaxeItem());
	public static final RegistryObject<Item> IRON_PICKAXE = REGISTRY.register("iron_pickaxe", () -> new IronPickaxeItem());
	public static final RegistryObject<Item> GOLDEN_PICKAXE = REGISTRY.register("golden_pickaxe", () -> new GoldenPickaxeItem());
	public static final RegistryObject<Item> DIAMOND_PICKAXE = REGISTRY.register("diamond_pickaxe", () -> new DiamondPickaxeItem());
	public static final RegistryObject<Item> NETHERITE_PICKAXE = REGISTRY.register("netherite_pickaxe", () -> new NetheritePickaxeItem());
	public static final RegistryObject<Item> WOODEN_SHOVEL = REGISTRY.register("wooden_shovel", () -> new WoodenShovelItem());
	public static final RegistryObject<Item> STONE_SHOVEL = REGISTRY.register("stone_shovel", () -> new StoneShovelItem());
	public static final RegistryObject<Item> IRON_SHOVEL = REGISTRY.register("iron_shovel", () -> new IronShovelItem());
	public static final RegistryObject<Item> GOLDEN_SHOVEL = REGISTRY.register("golden_shovel", () -> new GoldenShovelItem());
	public static final RegistryObject<Item> DIAMOND_SHOVEL = REGISTRY.register("diamond_shovel", () -> new DiamondShovelItem());
	public static final RegistryObject<Item> NETHERITE_SHOVEL = REGISTRY.register("netherite_shovel", () -> new NetheriteShovelItem());
	public static final RegistryObject<Item> WOODEN_SWORD = REGISTRY.register("wooden_sword", () -> new WoodenSwordItem());
	public static final RegistryObject<Item> STONE_SWORD = REGISTRY.register("stone_sword", () -> new StoneSwordItem());
	public static final RegistryObject<Item> IRON_SWORD = REGISTRY.register("iron_sword", () -> new IronSwordItem());
	public static final RegistryObject<Item> GOLDEN_SWORD = REGISTRY.register("golden_sword", () -> new GoldenSwordItem());
	public static final RegistryObject<Item> DIAMOND_SWORD = REGISTRY.register("diamond_sword", () -> new DiamondSwordItem());
	public static final RegistryObject<Item> NETHERITE_SWORD = REGISTRY.register("netherite_sword", () -> new NetheriteSwordItem());
	public static final RegistryObject<Item> WOODEN_HOE = REGISTRY.register("wooden_hoe", () -> new WoodenHoeItem());
	public static final RegistryObject<Item> STONE_HOE = REGISTRY.register("stone_hoe", () -> new StoneHoeItem());
	public static final RegistryObject<Item> IRON_HOE = REGISTRY.register("iron_hoe", () -> new IronHoeItem());
	public static final RegistryObject<Item> GOLDEN_HOE = REGISTRY.register("golden_hoe", () -> new GoldenHoeItem());
	public static final RegistryObject<Item> DIAMOND_HOE = REGISTRY.register("diamond_hoe", () -> new DiamondHoeItem());
	public static final RegistryObject<Item> NETHERITE_HOE = REGISTRY.register("netherite_hoe", () -> new NetheriteHoeItem());
	// Start of user code block custom items
	// End of user code block custom items
}
