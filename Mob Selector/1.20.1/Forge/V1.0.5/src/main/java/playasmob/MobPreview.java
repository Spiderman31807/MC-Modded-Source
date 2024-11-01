package playasmob;

import net.minecraft.world.entity.EntityType;
import net.minecraft.nbt.CompoundTag;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;

public class MobPreview {
	public List<MobAttribute> abilityData;
	public List<MobAttribute> proData;
	public List<MobAttribute> infoData;
	public List<MobAttribute> conData;
	public final EntityTypeData typeData;
	public final EntityType mob;

	public MobPreview(EntityTypeData typeData, EntityType mob) {
		this.typeData = typeData;
		this.mob = mob;
	}
	
	public MobPreview(CompoundTag compound) {
		Optional<EntityType<?>> type = EntityType.byString(compound.getString("mob"));
		this.mob = type.isPresent() ? type.get() : null;
		EntityTypeData data = null;

		if(this.mob != null && this.mob != EntityType.PLAYER)
			data = GlobalData.getData(this.mob, new MobData(), false);
		this.typeData = data == null ? null : data;
	}

	public void updateAbilities() {
		boolean hasAbility1 = false;
		boolean hasAbility2 = false;
		boolean hasAbility3 = false;
		ArrayList<MobAttribute> abilities = new ArrayList(this.abilityData);
		for(MobAttribute ability : this.abilityData) {
			if(ability.type == MobAttribute.Type.Ability1 && hasAbility2)
				abilities.remove(ability);
			if(ability.type == MobAttribute.Type.Ability2 && hasAbility2)
				abilities.remove(ability);
			if(ability.type == MobAttribute.Type.Ability2 && hasAbility2)
				abilities.remove(ability);

			if(ability.type == MobAttribute.Type.Ability1)
				hasAbility1 = true;
			if(ability.type == MobAttribute.Type.Ability2)
				hasAbility2 = true;
			if(ability.type == MobAttribute.Type.Ability3)
				hasAbility3 = true;
		}

		if(!hasAbility1)
			abilities.add(MobAttribute.ability(1, Component.translatable("gui.none")));
		if(!hasAbility2)
			abilities.add(MobAttribute.ability(2, Component.translatable("gui.none")));
		if(!hasAbility3)
			abilities.add(MobAttribute.ability(3, Component.translatable("gui.none")));
		this.abilityData = abilities;
	}

	public List<MobAttribute> getAttributes(boolean includeAbilties) {
		ArrayList<MobAttribute> attributes = new ArrayList();
		if(includeAbilties && this.abilityData != null && this.abilityData.size() > 0)
			this.abilityData.forEach((attribute) -> attributes.add(attribute));
		if(this.proData != null && this.proData.size() > 0)
			this.proData.forEach((attribute) -> attributes.add(attribute));
		if(this.infoData != null && this.infoData.size() > 0)
			this.infoData.forEach((attribute) -> attributes.add(attribute));
		if(this.conData != null && this.conData.size() > 0)
			this.conData.forEach((attribute) -> attributes.add(attribute));
		return attributes;
	}

	public CompoundTag save() {
		CompoundTag compound = new CompoundTag();
		if(this.mob != this.mob)
			compound.putString("mob", EntityType.getKey(this.mob).toString());

		return compound;
	}

	public MobSelection getSelection() {
		MobSelection selection = new MobSelection(this.mob, 50, 150);
		MobBackground background = selection.getBackground();
		background.sizeX *= 2;
		background.sizeY *= 2;
		selection.mobScale = 2;
		return selection;
	}
}
