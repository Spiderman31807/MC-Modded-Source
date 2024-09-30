
package spidermna318.bonehandles.item;

import java.util.List;

import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;

public class TemplateItem extends SmithingTemplateItem {
   	private static final Component APPLIES_TO = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.bone_upgrade.applies_to"))).withStyle(ChatFormatting.BLUE);
   	private static final Component INGREDIENTS = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.bone_upgrade.ingredients"))).withStyle(ChatFormatting.BLUE);
   	private static final Component BASE_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.bone_upgrade.base_slot_description")));
   	private static final Component ADDITIONS_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.bone_upgrade.additions_slot_description")));
	private static final Component UPGRADE = Component.translatable(Util.makeDescriptionId("upgrade", new ResourceLocation("bone_upgrade"))).withStyle(ChatFormatting.GRAY);
	private static final List<ResourceLocation> ICON_LIST = List.of(new ResourceLocation("item/empty_slot_sword"), new ResourceLocation("item/empty_slot_pickaxe"), new ResourceLocation("item/empty_slot_axe"), new ResourceLocation("item/empty_slot_shovel"), new ResourceLocation("item/empty_slot_hoe"));
   
	public TemplateItem() {
		super(APPLIES_TO, INGREDIENTS, UPGRADE, BASE_SLOT_DESCRIPTION, ADDITIONS_SLOT_DESCRIPTION, ICON_LIST, List.of(new ResourceLocation("bone_handles:item/empty_slot_bone")));
	}
}
