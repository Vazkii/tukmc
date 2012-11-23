package vazkii.tukmc;

import net.minecraft.src.ChatClickData;
import net.minecraft.src.GuiScreen;

//Pulic class, the default one is default
public class GuiChatConfirmLink extends net.minecraft.src.GuiConfirmOpenLink {
	final ChatClickData theChatClickData;

	final GuiChat chatGui;

	public GuiChatConfirmLink(GuiChat par1GuiChat, GuiScreen par2GuiScreen, String par3Str, int par4, ChatClickData par5ChatClickData) {
		super(par2GuiScreen, par3Str, par4);
		chatGui = par1GuiChat;
		theChatClickData = par5ChatClickData;
	}

	@Override
	public void copyLinkToClipboard() {
		setClipboardString(theChatClickData.getClickedUrl());
	}
}
