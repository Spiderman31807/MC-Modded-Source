package playasmob;

import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class SelectionScrollWidget extends AbstractScrollWidget {
	public final MobSelectScreen screen;

	public SelectionScrollWidget(double x, double y, double width, double height, MobSelectScreen screen) {
		super((int)x, (int)y, (int)width, (int)height, Component.empty());
		this.screen = screen;
	}

	public void updateWidgetNarration(NarrationElementOutput element) {
	}

	public void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
	}

	@Override
  	protected void renderBorder(GuiGraphics graphics, int x, int y, int width, int height) {
      	graphics.fill(x + 8, y - 1, x + width + 10, y + height + 1, -16777216);
   	}

	public int getHeight(int maxHeight) {
		double scroll = super.scrollAmount() / super.getMaxScrollAmount();
		return (int)(maxHeight * scroll);
	}

   	public int getInnerHeight() {
   		return screen.getHeight();
   	}

   	public double scrollRate() {
   		return 8;
   	}
}
