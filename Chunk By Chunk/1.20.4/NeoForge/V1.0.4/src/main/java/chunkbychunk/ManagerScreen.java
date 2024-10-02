package chunkbychunk;

import net.neoforged.neoforge.network.PacketDistributor;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashMap;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.core.Direction;

public class ManagerScreen extends AbstractContainerScreen<ManagerMenu> {
	private final static HashMap<String, Object> guistate = ManagerMenu.guistate;
	private final Level world;
	private final Player entity;

	public ManagerScreen(ManagerMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.entity = container.entity;
		this.imageWidth = 402;
		this.imageHeight = 202;
	}

	@Override
	public boolean isPauseScreen() {
		return true;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		int points = BorderUtils.getLevel(entity);
		int xp = BorderUtils.getXP(entity);
		int pointCost = BorderUtils.getLevelCost(entity);
		this.renderChunks(guiGraphics, mouseX, mouseY);
		
		Component message = Component.translatable("chuckbychunk.levels");
		Component string = Component.literal(message.getString() + " " + points);
      	guiGraphics.drawCenteredString(this.font, string, this.width / 2, this.topPos + 205, 16777215);
      	
		message = Component.translatable("chuckbychunk.next_level");
		string = Component.literal(message.getString() + " " + xp + "/" + pointCost);
      	guiGraphics.drawCenteredString(this.font, string, this.width / 2, this.topPos + 215, 16777215);
		
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableBlend();
	}

	public void renderChunks(GuiGraphics gui, int mouseX, int mouseY) {
		int xChunk = -20;
		int zChunk = -10;
		this.clearWidgets();
		for(int y = 0; zChunk < 10; y += 10) {
			for(int x = 0; xChunk < 20; x += 10) {
				boolean hasChunk = BorderUtils.hasChunkRelitive(this.entity, xChunk, zChunk);
				boolean canHaveChunk = hasChunk ? false : BorderUtils.isAdjacentRelitive(this.entity, xChunk, zChunk);
				String texture = hasChunk ? "chunk_owned.png" : (canHaveChunk ? "chunk_vaild.png" : "chunk_invaild.png");
				if(xChunk == 0 && zChunk == 0)
					texture = "chunk_current.png";
					
				if(texture == "chunk_vaild.png") {
					this.addButton(xChunk, zChunk, x, y);
				} else {
					gui.blit(new ResourceLocation("chunkbychunk:textures/screens/" + texture), this.leftPos + x, this.topPos + y, 0, 0, 10, 10, 10, 10);
				}
				xChunk++;
			}
			xChunk = -20;
			zChunk++;
		}
	}

	public void addButton(int xChunk, int zChunk, int x, int y) {
		final ChunkPos chunk = BorderUtils.getRelitiveChunk(this.entity, xChunk, zChunk);
		ImageButton ChuckButton = new ImageButton(this.leftPos + x, this.topPos + y, 10, 10, new WidgetSprites(new ResourceLocation("chunkbychunk:textures/screens/chunk_vaild.png"), new ResourceLocation("chunkbychunk:textures/screens/chunk_vaild_hover.png")), e -> {
				PacketDistributor.SERVER.noArg().send(new ManagerButton(0, new int[] { chunk.x, chunk.z }));
				ManagerButton.handleButtonAction(entity, 0, chunk);
			}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};

		guistate.put("button:chunk[" + xChunk + "," + zChunk + "]", ChuckButton);
		this.addRenderableWidget(ChuckButton);
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