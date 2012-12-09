package vazkii.tukmc;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;

import vazkii.codebase.common.ColorCode;

import net.minecraft.src.ChatClickData;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;

import cpw.mods.fml.relauncher.ReflectionHelper;

//Pulic class, the default one is default visibility
public class GuiChatConfirmLink extends net.minecraft.src.GuiConfirmOpenLink {

	final ChatClickData theChatClickData;

	final GuiChat chatGui;
	final int times;

	public GuiChatConfirmLink(GuiChat par1GuiChat, GuiScreen par2GuiScreen, String par3Str, int par4, ChatClickData par5ChatClickData) {
		super(par2GuiScreen, par3Str, par4);
		chatGui = par1GuiChat;
		theChatClickData = par5ChatClickData;
		times = mod_TukMC.getWebsiteViews(theChatClickData.getClickedUrl());
	}

	@Override
	public void initGui() {
		super.initGui();
		if (theChatClickData.getClickedUrl().contains("tinyurl.com/")) controlList.add(new GuiButton(3, width / 2 - fontRenderer.getStringWidth("Preview") / 2, 240, fontRenderer.getStringWidth("Preview") + 8, 20, "Preview"));
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
		drawCenteredString(fontRenderer, "Extra Info:", width / 2, 190, 0xFFFFFF);
		drawCenteredString(fontRenderer, times == 0 ? ColorCode.RED + "You have never been to this website." : String.format("%sYou have been to this website %s times.", ColorCode.BRIGHT_GREEN, times), width / 2, 205, 0xFFFFFF);
		if (theChatClickData.getClickedUrl().contains("tinyurl.com/")) drawCenteredString(fontRenderer, ColorCode.INDIGO + "This link is a tinyurl link, if you are wary, you can preview it by clicking here.", width / 2, 225, 0xFFFFFF);
	}

	@Override
	public void copyLinkToClipboard() {
		setClipboardString(theChatClickData.getClickedUrl());
	}

	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		if (par1GuiButton.id == 0) {
			String url = theChatClickData.getClickedUrl();
			mod_TukMC.registerOpenWebsite(url);
		}
		if (par1GuiButton.id == 3) try {
			ReflectionHelper.setPrivateValue(net.minecraft.src.GuiChat.class, chatGui, getURI(theChatClickData.getClickedUrl().replaceAll("tinyurl.com/", "preview.tinyurl.com/")), 6);
			chatGui.confirmClicked(true, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.actionPerformed(par1GuiButton);
	}

	public URI getURI(String s) {
		if (s == null) return null;
		else {
			Matcher var2 = GuiChat.pattern.matcher(s);

			if (var2.matches()) try {
				String var3 = var2.group(0);

				if (var2.group(1) == null) var3 = "http://" + var3;

				return new URI(var3);
			} catch (URISyntaxException var4) {
			}

			return null;
		}
	}
}
