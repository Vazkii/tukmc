package vazkii.tukmc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;

import net.minecraft.src.ChatClickData;
import net.minecraft.src.ChatLine;
import net.minecraft.src.GuiChat;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.StringTranslate;
import net.minecraft.src.StringUtils;

public class GuiNewChat extends net.minecraft.src.GuiNewChat {
	private final Minecraft mc;
	private final List sentMessages = new ArrayList();
	private final List chatLines = new ArrayList();
	private int field_73768_d = 0;

	public GuiNewChat(Minecraft par1Minecraft) {
		super(par1Minecraft);
		mc = par1Minecraft;
	}

	@Override
	public void drawChat(int par1) {
		if (mc.gameSettings.chatVisibility != 2) {
			byte var2 = 15;
			boolean var3 = false;
			int var5 = chatLines.size();
			float var6 = mc.gameSettings.chatOpacity * 0.9F + 0.1F;

			if (var5 > 0) {
				if (getChatOpen()) var3 = true;

				int var7;
				int var9;
				int var12;
				for (var7 = 0; var7 + field_73768_d < chatLines.size() && var7 < var2; ++var7) {
					ChatLine var8 = (ChatLine) chatLines.get(var7 + field_73768_d);

					if (var8 != null) {
						var9 = par1 - var8.getUpdatedCounter();
						double var10 = var9 / 200.0D;
						var10 = 1.0D - var10;
						var10 *= 10.0D;

						if (var10 < 0.0D) var10 = 0.0D;

						if (var10 > 1.0D) var10 = 1.0D;

						var10 *= var10;
						var12 = (int) (255.0D * var10);

						if (var3) var12 = 255;

						var12 = (int) (var12 * var6);
						int var14 = -var7 * 9;
						GL11.glEnable(GL11.GL_BLEND);
						String var15 = var8.getChatLineString();
						GL11.glPushMatrix();
						GL11.glScalef(0.5F, 0.5F, 0.5F);
						drawRect(22, mc.displayHeight - 53 + var14, 380, mc.displayHeight - 53 + var14 + 9, Integer.MIN_VALUE);
						if (!mc.gameSettings.chatColours) var15 = StringUtils.stripControlCodes(var15);
						mc.fontRenderer.drawStringWithShadow(var15, 30, mc.displayHeight - 53 + var14, 16777215);
						GL11.glPopMatrix();
					}
				}
			}
		}
	}

	@Override
	public void func_73761_a() {
		chatLines.clear();
		sentMessages.clear();
	}

	@Override
	public void printChatMessage(String par1Str) {
		printChatMessageWithOptionalDeletion(par1Str, 0);
	}

	@Override
	public void printChatMessageWithOptionalDeletion(String par1Str, int par2) {
		boolean var3 = getChatOpen();
		boolean var4 = true;

		if (par2 != 0) deleteChatLine(par2);

		Iterator var5 = mc.fontRenderer.listFormattedStringToWidth(par1Str, 320).iterator();

		while (var5.hasNext()) {
			String var6 = (String) var5.next();

			if (var3 && field_73768_d > 0) scroll(1);

			if (!var4) var6 = " " + var6;

			var4 = false;
			chatLines.add(0, new ChatLine(mc.ingameGUI.getUpdateCounter(), var6, par2));
		}

		while (chatLines.size() > 500)
			chatLines.remove(chatLines.size() - 1);
	}

	@Override
	public List getSentMessages() {
		return sentMessages;
	}

	@Override
	public void addToSentMessages(String par1Str) {
		if (sentMessages.isEmpty() || !((String) sentMessages.get(sentMessages.size() - 1)).equals(par1Str)) sentMessages.add(par1Str);
	}

	@Override
	public void resetScroll() {
		field_73768_d = 0;
	}

	@Override
	public void scroll(int par1) {
		field_73768_d += par1;
		int var2 = chatLines.size();

		if (field_73768_d > var2 - 14) field_73768_d = var2 - 14;

		if (field_73768_d <= 0) field_73768_d = 0;
	}

	@Override
	public ChatClickData func_73766_a(int par1, int par2) {
		if (!getChatOpen()) return null;
		else {
			ScaledResolution var3 = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
			int var4 = var3.getScaleFactor();
			int var5 = par1 / var4 - 3;
			int var6 = par2 / var4 - 40;

			if (var5 >= 0 && var6 >= 0) {
				int var7 = Math.min(20, chatLines.size());

				if (var5 <= 320 && var6 < mc.fontRenderer.FONT_HEIGHT * var7 + var7) {
					int var8 = var6 / (mc.fontRenderer.FONT_HEIGHT + 1) + field_73768_d;
					return new ChatClickData(mc.fontRenderer, (ChatLine) chatLines.get(var8), var5, var6 - (var8 - field_73768_d) * mc.fontRenderer.FONT_HEIGHT + var8);
				} else return null;
			} else return null;
		}
	}

	@Override
	public void addTranslatedMessage(String par1Str, Object... par2ArrayOfObj) {
		printChatMessage(StringTranslate.getInstance().translateKeyFormat(par1Str, par2ArrayOfObj));
	}

	@Override
	public boolean getChatOpen() {
		return mc.currentScreen instanceof GuiChat;
	}

	@Override
	public void deleteChatLine(int par1) {
		Iterator var2 = chatLines.iterator();
		ChatLine var3;

		do {
			if (!var2.hasNext()) return;

			var3 = (ChatLine) var2.next();
		} while (var3.getChatLineID() != par1);

		var2.remove();
	}
}
