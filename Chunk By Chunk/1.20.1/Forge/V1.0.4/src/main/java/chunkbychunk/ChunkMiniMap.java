
package chunkbychunk;

import org.checkerframework.checker.units.qual.h;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.Minecraft;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.platform.GlStateManager;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class ChunkMiniMap {
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		int w = event.getWindow().getGuiScaledWidth();
		int h = event.getWindow().getGuiScaledHeight();
		Player entity = Minecraft.getInstance().player;
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		RenderSystem.setShaderColor(1, 1, 1, 1);

		if(BorderUtils.useMap(entity)) {
			int centerX = 192;
			int centerZ = -108;
			int MapSize = 2;
			event.getGuiGraphics().blit(new ResourceLocation("chunkbychunk:textures/screens/chunk_current.png"), w / 2 + centerX, h / 2 + centerZ, 0, 0, 10, 10, 10, 10);

			int xChunk = -MapSize;
			int zChunk = -MapSize;
			for(int z = centerZ - (10 * MapSize); z <= centerZ + (10 * MapSize); z += 10) {
				for(int x = centerX - (10 * MapSize); x <= centerX + (10 * MapSize); x += 10) {
					if(xChunk != 0 || zChunk != 0)
						renderChunk(event, BorderUtils.hasChunkRelitive(entity, xChunk, zChunk), x, z);
					xChunk++;
				}
				xChunk = -MapSize;
				zChunk++;
			}
		}
		
		RenderSystem.depthMask(true);
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}

	public static void renderChunk(RenderGuiEvent.Pre event, boolean owned, int x, int z) {
		int w = event.getWindow().getGuiScaledWidth();
		int h = event.getWindow().getGuiScaledHeight();
		
		String texture = owned ? "chunk_owned.png" : "chunk_invaild.png";
		event.getGuiGraphics().blit(new ResourceLocation("chunkbychunk:textures/screens/" + texture), w / 2 + x, h / 2 + z, 0, 0, 10, 10, 10, 10);
	}
}
