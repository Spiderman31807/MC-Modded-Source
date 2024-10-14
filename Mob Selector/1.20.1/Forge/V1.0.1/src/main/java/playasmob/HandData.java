package playasmob;

import org.joml.Vector3f;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class HandData {
	public boolean enabled = false;
	public ResourceLocation texture;
	public ResourceLocation outerTexture;
	public Vector3f position = new Vector3f();
	public Vector3f rotation = new Vector3f();
	public Vector3f scale = new Vector3f(1);
	public Hand model = null;
	public Hand outerModel = null;

	public HandData() {
	}

	public HandData(EntityRendererProvider.Context context, EntityTypeData typeData) {
		model = new HandModel(context.bakeLayer(typeData.getModelLayer(false)), false);
		outerModel = new HandModel(context.bakeLayer(typeData.getModelLayer(true)), true);
	}

	public void setPosition(float x, float y, float z) {
		position.x = x;
		position.y = y;
		position.z = z;
	}

	public void setRotation(float x, float y, float z) {
		rotation.x = x;
		rotation.y = y;
		rotation.z = z;
	}

	public void setScale(float x, float y, float z) {
		scale.x = x;
		scale.y = y;
		scale.z = z;
	}
}
