package playasmob;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.npc.VillagerProfession;

public class VillagerData implements VillagerDataHolder, EntityTypeData<VillagerRenderer> {
	public MobData data = null;
	public net.minecraft.world.entity.npc.VillagerData villagerData = new net.minecraft.world.entity.npc.VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1);

	public VillagerData(MobData data) {
		this.data = data;
	}

	public net.minecraft.world.entity.npc.VillagerData getVillagerData() {
		return this.villagerData;
	}

	public void setVillagerData(net.minecraft.world.entity.npc.VillagerData data) {
		this.villagerData = data;
	}

	public VillagerRenderer getRenderer(EntityRendererProvider.Context context) {
		return new VillagerRenderer(context);
	}

	public void setupHand(HandData hand) {
		hand.texture = new ResourceLocation("textures/entity/villager/villager.png");
		hand.setScale(1f, 1f, 1f);
		hand.setPosition(5.25f, 21.5f, -1.1f);
		hand.setRotation(3.2f, 1.6f, 0.15f);
		hand.outerTexture = new ResourceLocation("textures/entity/villager/type/plains.png");
	}

	public boolean isBaby() {
		return false;
	}
}
