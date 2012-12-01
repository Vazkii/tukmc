package vazkii.tukmc;

import java.util.EnumSet;

import vazkii.codebase.common.CommonUtils;

import net.minecraft.src.GuiChat;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiSleepMP;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TickHandler implements ITickHandler {

	public static boolean ticked = false;

	private static int lastRemoval = 0;
	private static int msgs = 0;

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
		if (gui != null && gui instanceof GuiChat && !(gui instanceof vazkii.tukmc.GuiChat) || mod_TukMC.shouldReopenChat && (gui == null || !(gui instanceof GuiChat))) CommonUtils.getMc().displayGuiScreen(new vazkii.tukmc.GuiChat());
		mod_TukMC.shouldReopenChat = false;

		if(gui instanceof GuiSleepMP)
			((vazkii.tukmc.GuiChat)CommonUtils.getMc().currentScreen).setBed();
		
		if (type.equals(EnumSet.of(TickType.CLIENT))) {
			if (lastRemoval > 0) --lastRemoval;
			if (lastRemoval <= 0 && msgs > 0) {
				--msgs;
				lastRemoval = 10;
			}
		}
	}

	public static void addMsg() {
		msgs++;
		lastRemoval = 40;
	}

	public static int getMsgs() {
		return msgs;
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT, TickType.RENDER);
	}

	@Override
	public String getLabel() {
		return "TukMC";
	}

}
