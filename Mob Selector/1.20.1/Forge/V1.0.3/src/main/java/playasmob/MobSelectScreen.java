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
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.Minecraft;
import java.util.List;
import net.minecraft.client.KeyMapping;
import net.minecraft.ChatFormatting;

public class MobSelectScreen extends AbstractContainerScreen<MobSelectionMenu> {
	public MobSelectionMenu menu;
   	protected int imageWidth = 400;
   	protected int imageHeight = 400;
	public double xOffset = 50;
	public double yOffset = 50;
   	public double startX = 15;
   	public double startY = 10;
   	public int optionX = (int)startX;
   	public int optionY = (int)startY;
   	public SelectionScrollWidget scroll;

	public MobSelectScreen(MobSelectionMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
		this.menu = menu;

		PlayasmobMod.PACKET_HANDLER.sendToServer(new SelectorButton(null, "open"));
		this.scroll = new SelectionScrollWidget(imageWidth + 60, 1, 10, (imageHeight / 1.4875), this);
	}

	public MobSelection getMobOption(EntityType mob) {
		MobSelection selection = new MobSelection(mob, optionX, optionY);
		return selection;
	}

	public void nextPostion() {
		optionX += xOffset;
		if(optionX > this.imageWidth + startX) {
			optionX = (int)startX;
			optionY += yOffset;
		}
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
	}

	public int getHeight() {
		return this.optionY;
	}

	public boolean mouseClicked(double value1, double value2, int value3) {
		super.mouseClicked(value1, value2, value3);
		return this.scroll.mouseClicked(value1, value2, value3);
	}

	public boolean mouseReleased(double value1, double value2, int value3) {
		return this.scroll.mouseReleased(value1, value2, value3);
	}
	
	public boolean mouseDragged(double value1, double value2, int value3, double value4, double value5) {
		return this.scroll.mouseDragged(value1, value2, value3, value4, value5);
	}
	
	public boolean mouseScrolled(double value1, double value2, double value3) {
		return this.scroll.mouseScrolled(value1, value2, value3);
	}

	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return this.scroll.keyPressed(keyCode, scanCode, modifiers);
	}

	public boolean isPauseScreen() {
		return true;
	}

   	public void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
   		this.renderDirtBackground(graphics);
   	}
	
   	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      	this.renderBg(graphics, partialTicks, mouseX, mouseY);
      	RenderSystem.disableDepthTest();
      	super.render(graphics, mouseX, mouseY, partialTicks);
      	MobData data = MobData.get(this.menu.entity);
      	MobPreview preview = data.getPreview();
      	SelectionGroup group = data.getGroup();

		this.clearWidgets();
		if(preview != null && preview.mob != null) {
			this.renderPreview(graphics, preview);
		} else if(group != null) {
			this.renderGroup(graphics, group);
		} else {
			this.renderOptions(graphics);
		}

		MobData.get(this.menu.entity).resetRenderer();
   	}

   	public void renderPreview(GuiGraphics graphics, MobPreview preview) {
   		if(!this.renderSelection(graphics, preview.getSelection(), "select")) {
   			PlayasmobMod.PACKET_HANDLER.sendToServer(new SelectorButton(null, "back"));
   			return;
   		}
   		
		List<MobAttribute> attriutes = preview.getAttributes(false);
		double StartY = (attriutes.size() * 10);
		int YLevel = (int)(40 - StartY);
		for(MobAttribute attriute : attriutes) {
			graphics.drawCenteredString(Minecraft.getInstance().font, attriute.text, (int)(this.width * 0.65), (int)((this.height / 2) + YLevel), 0);
			YLevel += 10;
		}

      	this.backButton();
		List<MobAttribute> abilities = preview.abilityData;
		if(abilities == null || abilities.size() == 0)
			return;

		for(MobAttribute ability : abilities) {
			YLevel = (int)getAbilityY(ability.type);
			Component keybind = getAbiltiyBind(ability.type);
			if(keybind != null)
				graphics.drawCenteredString(Minecraft.getInstance().font, keybind, (int)(this.width * 0.18), YLevel, 0);
			graphics.drawCenteredString(Minecraft.getInstance().font, ability.text, (int)(this.width * 0.18), YLevel + 10, 0);
		}
   	}

   	public double getAbilityY(MobAttribute.Type type) {
   		if(type == MobAttribute.Type.Ability1)
   			return 50;
   		if(type == MobAttribute.Type.Ability2)
   			return 85;
   		if(type == MobAttribute.Type.Ability3)
   			return 120;
   		return 0;
   	}

   	public Component getAbiltiyBind(MobAttribute.Type type) {
   		KeyMapping key = null;
		if(type == MobAttribute.Type.Ability1)
			key = Keybinds.Ability1;
		if(type == MobAttribute.Type.Ability2)
			key = Keybinds.Ability2;
		if(type == MobAttribute.Type.Ability3)
			key = Keybinds.Ability3;

		String keyType = ("playasmob.binding." + type).toLowerCase();
		Component binding = Component.translatable("key.keyboard.unknown").withStyle(ChatFormatting.DARK_GRAY);
   		if(!key.isUnbound() && key != null)
   			binding = key.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.DARK_GRAY);
   		return Component.translatable(keyType).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.BOLD).append(binding);
   	}

   	public void renderGroup(GuiGraphics graphics, SelectionGroup group) {
		this.optionX = (int)this.startX;
		this.optionY = (int)this.startY;
		for(EntityType mob : group.mobs) {
         	MobSelection selection = getMobOption(mob);
            if(selection.y > 0 && selection.y < this.height) {
            	if(this.renderSelection(graphics, selection, "preview"))
            		this.nextPostion();
         	}
      	}

      	this.backButton();
   	}

   	public void renderOptions(GuiGraphics graphics) {
		if(this.optionY > this.height)
			this.addRenderableWidget(this.scroll);
		int heightMod = this.scroll.getHeight(Math.max((this.optionY - this.height), 0));

		this.optionX = (int)this.startX;
		this.optionY = (int)this.startY;
		MobSelection playerSelection = getMobOption(EntityType.PLAYER);
        playerSelection.y -= heightMod;
        if(playerSelection.y > 0 && playerSelection.y < this.height) {
        	this.renderSelection(graphics, playerSelection, "select");
        	this.nextPostion();
        }
        	
      	for(SelectionGroup group : SelectionScreenData.groups) {
         	MobSelection selection = getMobOption(group.displayMob);
         	selection.background = group.background;
         	selection.y -= heightMod;
            if(selection.y > 0 && selection.y < this.height) {
            	if(this.renderSelection(graphics, selection, "openGroup"))
            		this.nextPostion();
         	}
      	}
        	
      	for(EntityType mob : SelectionScreenData.unsorted) {
         	MobSelection selection = getMobOption(mob);
         	selection.background = MobBackground.pill();
         	selection.y -= heightMod;
         	if(selection.y > 0 && selection.y < this.height) {
            	if(this.renderSelection(graphics, selection, "preview"))
            		this.nextPostion();
         	}
      	}
   	}

   	public boolean renderSelection(GuiGraphics graphics, MobSelection selection, String message) {
   		MobBackground background = selection.getBackground();
   		if(renderEntityType(graphics, selection.mob, selection.x + (background.sizeX / 2), selection.y + (int)(background.sizeY / 1.1), selection.mobScale))
   			return this.addButton(selection, message);
   		return false;
   	}

   	public void backButton() {
		ImageButton back = new ImageButton((int)(this.imageWidth * 1.15), 4, 13, 13, 0, 0, 13, new ResourceLocation("playasmob:textures/screens/atlas/imagebutton_back.png"), 13, 26, e -> {
			PlayasmobMod.PACKET_HANDLER.sendToServer(new SelectorButton(null, "back"));
			SelectorButton.handleButtonAction(menu.entity, null, "back");
		});

		this.addRenderableWidget(back);
   	}
   	
	public boolean addButton(MobSelection selection, String message) {
   		MobBackground background = selection.getBackground();
		ImageButton selectorButton = new ImageButton(selection.x, selection.y, background.sizeX, background.sizeY, 0, 0, background.sizeY, background.texture, background.sizeX, background.sizeY * 2, e -> {
			PlayasmobMod.PACKET_HANDLER.sendToServer(new SelectorButton(selection.mob, message));
			SelectorButton.handleButtonAction(menu.entity, selection.mob, message);
		});

		this.addRenderableWidget(selectorButton);
		return true;
	}

   	public boolean renderEntityType(GuiGraphics graphics, EntityType type, int x, int y, int scale) {
   		MobData data = MobData.get(this.menu.entity);
   		data.changeRenderer(type);
   		EntityTypeData entityData = data.typeRender;
   		if(entityData == null && type != EntityType.PLAYER)
   			return false;
   		double scaleModifer = type == EntityType.PLAYER ? 1 : entityData.selectionSizeMultiplier();
   		EntityDimensions dimensions = type.getDimensions();
   		double entitySize = ((25 - (10 * ((dimensions.height + dimensions.width) / 2.25))) * scale) * scaleModifer;
   		InventoryScreen.renderEntityInInventoryFollowsAngle(graphics, x, y - (int)(Math.max(10 - (dimensions.height * 5), 0)), (int)entitySize, (dimensions.width < 0.6 || dimensions.height < 1) ? -1f : -0.01f, dimensions.height < 1 ? -0.75f : 0, this.menu.entity);
		return true;
   	}
}