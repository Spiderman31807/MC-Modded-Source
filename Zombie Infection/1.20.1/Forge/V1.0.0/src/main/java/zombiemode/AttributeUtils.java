package zombiemode;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.Attribute;

public class AttributeUtils {
	public static List<Attribute> getAll() {
		ArrayList<Attribute> attributes = new ArrayList();
		attributes.add(Attributes.MAX_HEALTH);
		attributes.add(Attributes.FOLLOW_RANGE);
		attributes.add(Attributes.KNOCKBACK_RESISTANCE);
		attributes.add(Attributes.MOVEMENT_SPEED);
		attributes.add(Attributes.FLYING_SPEED);
		attributes.add(Attributes.ATTACK_DAMAGE);
		attributes.add(Attributes.ATTACK_KNOCKBACK);
		attributes.add(Attributes.ATTACK_SPEED);
		attributes.add(Attributes.ARMOR);
		attributes.add(Attributes.ARMOR_TOUGHNESS);
		attributes.add(Attributes.LUCK);
		attributes.add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
		attributes.add(Attributes.JUMP_STRENGTH);
		return attributes;
	}

	public static void replace(LivingEntity target, AttributeSupplier attributes) {
		for(Attribute attribute : getAll()) {
			if(attributes.hasAttribute(attribute))
				replace(target, attributes, attribute);
		}
	}
	
	public static void replace(LivingEntity target, AttributeSupplier attributes, Attribute type) {
		AttributeInstance attribute = target.getAttributes().getInstance(type);
		if(attribute != null)
			attribute.setBaseValue(attributes.getBaseValue(type));
	}
	
	public static void replace(LivingEntity target, double value, Attribute type) {
		AttributeInstance attribute = target.getAttributes().getInstance(type);
		if(attribute != null)
			attribute.setBaseValue(value);
	}
}
