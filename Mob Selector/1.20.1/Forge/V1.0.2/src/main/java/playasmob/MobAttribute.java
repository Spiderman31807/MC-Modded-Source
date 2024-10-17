package playasmob;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;

public class MobAttribute {
	public final Component text;
	public final Type type;

	public MobAttribute(Type type, Component text) {
		this.type = type;
		this.text = text;
	}

	public static MobAttribute ability(int ability, MutableComponent text) {
		Type abilityType = switch(ability) {
			default -> Type.Ability1;
			case 2 -> Type.Ability2;
			case 3 -> Type.Ability3;
		};
		
		return new MobAttribute(abilityType, text.withStyle(ChatFormatting.DARK_GRAY));
	}

	public static MobAttribute pro(MutableComponent text) {
		return new MobAttribute(Type.Pro, text.withStyle(ChatFormatting.GREEN));
	}

	public static MobAttribute info(MutableComponent text) {
		return new MobAttribute(Type.Info, text.withStyle(ChatFormatting.GRAY));
	}

	public static MobAttribute con(MutableComponent text) {
		return new MobAttribute(Type.Con, text.withStyle(ChatFormatting.RED));
	}

	public static enum Type {
		Ability1,
		Ability2,
		Ability3,
		Pro,
		Info,
		Con;
	}
}
