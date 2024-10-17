package playasmob;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.model.geom.EntityModelSet;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

public class GlowLayer<E extends LivingEntity, M extends EntityModel<E>> extends RenderLayer<E, M> {
	public EntityTypeData data;

	public GlowLayer(RenderLayerParent<E, M> parent, EntityModelSet modelSet, EntityTypeData data) {
    	super(parent);
    	this.data = data;
   	}
	
   	public void render(PoseStack pose, MultiBufferSource buffer, int light, E entity, float value2, float value3, float value4, float value5, float value6, float value7) {
		if(!data.forceGlow(entity))
			return;
			
       	VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.outline(this.getTextureLocation(entity)));
        this.getParentModel().renderToBuffer(pose, vertexconsumer, light, LivingEntityRenderer.getOverlayCoords(entity, 0), 1, 1, 1, 0);
	}
}
