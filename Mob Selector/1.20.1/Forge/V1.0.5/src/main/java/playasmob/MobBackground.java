package playasmob;

import net.minecraft.resources.ResourceLocation;

public class MobBackground {
	public final ResourceLocation texture;
	public int xOffset = 0;
	public int yOffset = 0;
	public int sizeX;
	public int sizeY;

	private MobBackground(ResourceLocation texture, int sizeX, int sizeY) {
		this.texture = texture;
		this.sizeX = (int)(sizeX * 1.75);
		this.sizeY = (int)(sizeY * 1.75);
	}

	public static MobBackground square() {
		return new MobBackground(new ResourceLocation("playasmob:textures/screens/atlas/imagebutton_square_background.png"), 24, 24);
	}

	public static MobBackground pill() {
		MobBackground background = new MobBackground(new ResourceLocation("playasmob:textures/screens/atlas/imagebutton_pill_background.png"), 22, 26);
		background.xOffset = 1;
		background.yOffset = -1;
		return background;
	}

	public static MobBackground special() {
		MobBackground background = new MobBackground(new ResourceLocation("playasmob:textures/screens/atlas/imagebutton_special_background.png"), 26, 26);
		background.xOffset = -1;
		background.yOffset = -1;
		return background;
	}
}
