package playasmob;

import net.minecraft.resources.ResourceLocation;

public class MobBackground {
	public final ResourceLocation texture;
	public final int sizeX;
	public final int sizeY;

	private MobBackground(ResourceLocation texture, int sizeX, int sizeY) {
		this.texture = texture;
		this.sizeX = (int)(sizeX * 1.75);
		this.sizeY = (int)(sizeY * 1.75);
	}

	public static MobBackground square() {
		return new MobBackground(new ResourceLocation("playasmob:textures/screens/atlas/imagebutton_square_background.png"), 24, 24);
	}

	public static MobBackground pill() {
		return new MobBackground(new ResourceLocation("playasmob:textures/screens/atlas/imagebutton_pill_background.png"), 22, 26);
	}

	public static MobBackground special() {
		return new MobBackground(new ResourceLocation("playasmob:textures/screens/atlas/imagebutton_special_background.png"), 26, 26);
	}
}
