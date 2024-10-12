package playasmob;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EntityType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.world.level.GameType;
import net.minecraft.world.entity.player.Player;

public class MobSelectScreen extends AbstractContainerScreen<MobSelectionMenu> {
	ArrayList<MobSelection> mobs = new ArrayList();
	public MobSelectionMenu menu;
   	protected int imageWidth = 400;
   	protected int imageHeight = 400;
	public int xOffset = 10;
	public int yOffset = 25;
   	public int optionX = xOffset;
   	public int optionY = yOffset;
   	public GameType previousMode;

	public MobSelectScreen(MobSelectionMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
		this.menu = menu;

		if(this.menu.entity instanceof ServerPlayer serverPlayer) {
			previousMode = serverPlayer.gameMode.getGameModeForPlayer();
			serverPlayer.gameMode.changeGameModeForPlayer(GameType.SPECTATOR);
		}

		this.addMobOption(EntityType.PLAYER);
		for(EntityType mob : GlobalData.supported) {
			this.addMobOption(mob);
		}
	}

	public void addMobOption(EntityType mob) {
		MobSelection selection = new MobSelection(mob, optionX, optionY);
		mobs.add(selection);
		
		optionX += xOffset + selection.getBackground().sizeX;
		if(optionX > this.imageWidth) {
			optionX = xOffset;
			optionY += yOffset + selection.getBackground().sizeY;
		}
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
	}

	public boolean isPauseScreen() {
		return true;
	}
	
	@Override
	public void onClose() {
		if(this.menu.entity instanceof ServerPlayer serverPlayer)
			serverPlayer.gameMode.changeGameModeForPlayer(this.previousMode);
		super.onClose();
	}

   	public void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
   		this.renderDirtBackground(graphics);
   	}
	
   	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      	this.renderBg(graphics, partialTicks, mouseX, mouseY);
      	RenderSystem.disableDepthTest();
      	super.render(graphics, mouseX, mouseY, partialTicks);

		this.clearWidgets();
      	for(int idx = 0; idx < this.mobs.size(); ++idx) {
         	MobSelection selection = this.mobs.get(idx);
            this.renderSelection(graphics, selection);
      	}
      	
		MobData.get(this.menu.entity).resetRenderer();
   	}

   	public void renderSelection(GuiGraphics graphics, MobSelection selection) {
   		this.addButton(selection);
   		MobBackground background = selection.getBackground();
   		renderEntityType(graphics, selection.mob, selection.x + (background.sizeX / 2), selection.y + (int)(background.sizeY / 1.1));
   	}
   	
	public void addButton(MobSelection selection) {
   		MobBackground background = selection.getBackground();
		ImageButton selectorButton = new ImageButton(selection.x, selection.y, background.sizeX, background.sizeY, 0, 0, background.sizeY, background.texture, background.sizeX, background.sizeY * 2, e -> {
			PlayasmobMod.PACKET_HANDLER.sendToServer(new SelectorButton(selection.mob));
			SelectorButton.handleButtonAction(menu.entity, selection.mob);
		});

		this.addRenderableWidget(selectorButton);
	}

   	public void renderEntityType(GuiGraphics graphics, EntityType type, int x, int y) {
   		MobData.get(this.menu.entity).changeRenderer(type);
   		EntityDimensions dimensions = type.getDimensions();
   		InventoryScreen.renderEntityInInventoryFollowsAngle(graphics, x, y - (int)(Math.max(10 - (dimensions.height * 5), 0)), 25 - (int)(10 * ((dimensions.height + dimensions.width) / 2.25)), (dimensions.width < 0.6 || dimensions.height < 1) ? -1f : -0.01f, dimensions.height < 1 ? -0.75f : 0, this.menu.entity);
   	}
}