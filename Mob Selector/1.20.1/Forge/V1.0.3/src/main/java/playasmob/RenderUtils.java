package playasmob;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.Font;
import java.util.ArrayList;

public class RenderUtils {
	public static void abilityIcon(GuiGraphics graphics, String texture, boolean useable, int x, int y) {
		abilityIcon(graphics, texture, useable, x, y, -1, null);
	}
	
	public static void abilityIcon(GuiGraphics graphics, String texture, boolean useable, int x, int y, int cooldown, KeyMapping key) {
		if(!useable || cooldown > 0)
    		RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, 1);
		graphics.blit(new ResourceLocation("playasmob:textures/screens/" + texture + "_icon.png"), x, y, 0, 0, 16, 16, 16, 16);
		if(cooldown != -1 && key != null) {
			Component text = cooldown > 0 ? Component.literal("" + ((cooldown / 20) + 1)) : key.getTranslatedKeyMessage();
			graphics.drawString(Minecraft.getInstance().font, text, x - (6 + (Minecraft.getInstance().font.width(text.getVisualOrderText()))), y + 4, -1);
		}
    	RenderSystem.setShaderColor(1, 1, 1, 1);
	}
}