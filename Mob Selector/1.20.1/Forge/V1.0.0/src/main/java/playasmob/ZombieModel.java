package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;

@OnlyIn(Dist.CLIENT)
public class ZombieModel extends AbstractZombieModel {
   public ZombieModel(ModelPart part) {
      super(part);
   }

   public boolean isAggressive(AbstractClientPlayer player) {
      return player.getLastHurtByMob() != null;
   }
}
