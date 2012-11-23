package vazkii.tukmc;

import java.util.EnumSet;

import vazkii.codebase.common.CommonUtils;

import net.minecraft.src.GuiChat;
import net.minecraft.src.GuiScreen;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TickHandler implements ITickHandler {

	public static boolean ticked = false;

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if (!ticked && CommonUtils.getMc().ingameGUI != null) {
			CommonUtils.getMc().ingameGUI = new vazkii.tukmc.GuiIngame();
			ticked = true;
		}
		
		GuiScreen gui = CommonUtils.getMc().currentScreen;
		if ((gui != null && gui instanceof GuiChat && !(gui instanceof vazkii.tukmc.GuiChat)) || (mod_TukMC.shouldReopenChat && (gui == null || !(gui instanceof GuiChat)))) CommonUtils.getMc().displayGuiScreen(new vazkii.tukmc.GuiChat());
		mod_TukMC.shouldReopenChat = false;
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

	@Override
	public String getLabel() {
		return "TukMC";
	}

}
