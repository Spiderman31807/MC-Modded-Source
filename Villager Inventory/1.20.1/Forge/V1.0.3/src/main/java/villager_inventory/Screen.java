package villager_inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashMap;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.InventoryMenu;

public class Screen extends AbstractContainerScreen<Menu> {
	private final static HashMap<String, Object> guistate = Menu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private final Menu menu;
	private final LivingEntity boundEntity;

	public Screen(Menu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.menu = container;
		this.world = menu.world;
		this.x = menu.x;
		this.y = menu.y;
		this.z = menu.z;
		this.entity = menu.entity;
		this.boundEntity = (LivingEntity)menu.boundEntity;
		this.imageWidth = 176;
		this.imageHeight = 166;
	}

	private static final ResourceLocation texture = new ResourceLocation("villager_inventory:textures/screens/inventory.png");

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		if(!(menu.boundEntity instanceof Villager))
			this.minecraft.player.closeContainer();
			
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		InventoryScreen.renderEntityInInventoryFollowsAngle(guiGraphics, this.leftPos + 52, this.topPos + 72, 30, 0f + (float) Math.atan((this.leftPos + 52 - mouseX) / 40.0), (float) Math.atan((this.topPos + 20 - mouseY) / 40.0), this.boundEntity);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

		int foodLevel = this.menu.countFoodPointsInInventory();
		this.renderHunger(guiGraphics, foodLevel, this.leftPos + 105, this.topPos + 13);
		this.renderHunger(guiGraphics, foodLevel - 12, this.leftPos + 105, this.topPos + 23);

		int heartLevel = Math.min(14, (foodLevel * 14) / 12);
		ResourceLocation heart = new ResourceLocation("villager_inventory:textures/screens/breed_" + heartLevel + ".png");
		if(heartLevel > 0)
			guiGraphics.blit(heart, this.leftPos + 84, this.topPos + 13, 0, 0, 18, 18, 18, 18);
		
		RenderSystem.disableBlend();
	}


	public void renderHunger(GuiGraphics guiGraphics, int food, int Xpos, int Ypos) {
		for (int i = 2; i < 13; i += 2) {
    		String HungerBar = "";
		
    		if(food > i - 2)
    			HungerBar = "hunger_half";
    		if(food > i - 1)
    			HungerBar = "hunger_full";
  			if(HungerBar != "")
  				guiGraphics.blit(new ResourceLocation("villager_inventory:textures/screens/" + HungerBar + ".png"), Xpos, Ypos, 0, 0, 9, 9, 9, 9);
			Xpos += 10;
		}
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		
		return super.keyPressed(key, b, c);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
	}
}