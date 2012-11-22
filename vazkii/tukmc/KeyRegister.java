package vazkii.tukmc;

import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.KeyBinding;

import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class KeyRegister extends KeyHandler {

	public static KeyBinding showTooltipKB = new KeyBinding("TukMC Show Tooltip", Keyboard.KEY_LCONTROL);

	public KeyRegister() {
		super(new KeyBinding[] { showTooltipKB }, new boolean[] { true });
	}

	@Override
	public String getLabel() {
		return "TukMC";
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.allOf(TickType.class);
	}

}
