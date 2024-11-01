package playasmob;

import net.minecraft.client.model.EntityModel;

public interface Hand {
	abstract boolean isOuterLayer();
	
	default EntityModel get() {
		return (EntityModel)this;
	}
}
