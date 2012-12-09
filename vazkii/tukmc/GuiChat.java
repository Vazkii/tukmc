package vazkii.tukmc;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static vazkii.tukmc.TukMCReference.BOX_INNER_COLOR;
import static vazkii.tukmc.TukMCReference.BOX_OUTLINE_COLOR;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javaQuery.j2ee.tinyURL;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import updatemanager.common.UpdateManager;
import vazkii.codebase.common.ColorCode;
import vazkii.codebase.common.EnumVazkiiMods;
import vazkii.codebase.common.FormattingCode;
import vazkii.codebase.common.IOUtils;
import net.minecraft.client.Minecraft;

import net.minecraft.src.ChatClickData;
import net.minecraft.src.ChatLine;
import net.minecraft.src.MathHelper;
import net.minecraft.src.NetClientHandler;
import net.minecraft.src.Packet19EntityAction;
import net.minecraft.src.StringUtils;
import net.minecraft.src.Tessellator;

import cpw.mods.fml.relauncher.ReflectionHelper;

public class GuiChat extends net.minecraft.src.GuiChat {

	public static final Pattern pattern = Pattern.compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,3})(/\\S*)?$");
	String username;
	String tooltip = "";
	public static final String CHARS = "GSCLNWPUO";
	boolean isBed;

	@Override
	public void initGui() {
		super.initGui();
		username = FormattingCode.BOLD + "<" + mc.thePlayer.username + "> ";
		inputField = new SpellcheckingTextbox(fontRenderer, fontRenderer.getStringWidth(username) + 17 * 2, (height - 98) * 2, 360, 4);
		inputField.setMaxStringLength(100);
		inputField.setEnableBackgroundDrawing(false);
		inputField.setFocused(true);
		inputField.setCanLoseFocus(false);
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawDoubleOutlinedBox(15, height - 100, 224, 8, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
		GL11.glPushMatrix();
		GL11.glScalef(0.5F, 0.5F, 0.5F);
		fontRenderer.drawString(username, 17 * 2, (height - 98) * 2, 0xFFFFFF);
		fontRenderer.drawString(ColorCode.GREY + "Tip: Shift clicking will scroll 7x faster!", CHARS.length() * 26 + 20, height * 2 - 211, 0xFFFFFF);
		inputField.drawTextBox();
		GL11.glPopMatrix();
		int x = Mouse.getX();
		int y = Mouse.getY();
		int max = CHARS.length() * 26 + 3;
		int min = 28;
		int box = (x - 9) / ((max - min) / CHARS.length()) - 1;
		boolean is = y >= 202 && y <= 223 && x >= min && x <= max;
		for (int i = 0; i < CHARS.length(); i++) {
			drawDoubleOutlinedBox(15 + i * 12, height - (is && i == box ? 114 : 112), 9, is && box == i ? 12 : 10, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			fontRenderer.drawStringWithShadow("" + CHARS.charAt(i), 17 + i * 12, height - 111, 0xFFFFFF);
		}
		if (tooltip != "") {
			String[] tokens = tooltip.split(";");
			int length = 12;
			for (String s : tokens)
				length = Math.max(length, fontRenderer.getStringWidth(s));
					drawDoubleOutlinedBox(14, height - 114 - tokens.length * 12, length + 6, tokens.length * 12, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
					if (box <= CHARS.length() - 1) {
						drawOutlinedBox(15 + box * 12, height - 114, 9, 1, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
						drawSolidRect(14 + box * 12, height - 115, 26 + box * 12, height - 114, BOX_INNER_COLOR);
						drawSolidRect(15 + box * 12, height - 115, 24 + box * 12, height - 112, BOX_INNER_COLOR);
					}
					int i = 0;
					for (String s : tokens) {
						fontRenderer.drawStringWithShadow(s, 18, height - 112 - tokens.length * 12 + i * 12, 0xFFFFFF);
						++i;
					}
		}
		if (!MathHelper.stringNullOrLengthZero(mod_TukMC.pinnedMsg)) {
			String pin = "Pinned:";
			drawDoubleOutlinedBox(15, height - 211, fontRenderer.getStringWidth(pin) + 6, 14, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			fontRenderer.drawStringWithShadow(pin, 18, height - 210, 0xFFFFFF);
			drawDoubleOutlinedBox(15, height - 200, fontRenderer.getStringWidth(mod_TukMC.pinnedMsg) + 6, 14, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			fontRenderer.drawStringWithShadow(mod_TukMC.pinnedMsg, 18, height - 197, 0xFFFFFF);
		}
		if (isBed) {
			drawDoubleOutlinedBox(260, height - 80, 185, 16, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			fontRenderer.drawStringWithShadow("Press ESC twice to leave your bed.", 265, height - 76, 0xFFFFFF);
		}
		if (Keyboard.isKeyDown(KeyRegister.showTooltipKB.keyCode)) if (mc.ingameGUI.getChatGUI() != null) {
			ChatClickData clickData = mc.ingameGUI.getChatGUI().func_73766_a(Mouse.getX() * 2, Mouse.getY() * 2);
			if (clickData != null) {
				ChatLine line = ReflectionHelper.getPrivateValue(ChatClickData.class, clickData, 2);
				if (line != null && line instanceof TimedChatLine) {
					String time = "Recieved: " + ((TimedChatLine) line).getTime() + ColorCode.GREY + ((TimedChatLine) line).getElapsedTime();
					int timeWidth = fontRenderer.getStringWidth(time);
					drawDoubleOutlinedBox(Mouse.getX() / 2 + 2, height - Mouse.getY() / 2 - 16, timeWidth + 4, 12, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
					fontRenderer.drawStringWithShadow(time, Mouse.getX() / 2 + 4, height - Mouse.getY() / 2 - 14, 0xFFFFFF);
				}
			}
		}
	}

	public void setBed() {
		isBed = true;
	}

	private void wakeEntity() {
		NetClientHandler var1 = mc.thePlayer.sendQueue;
		var1.addToSendQueue(new Packet19EntityAction(mc.thePlayer, 3));
	}

	@Override
	protected void keyTyped(char par1, int par2) {
		if (par2 == 1 && isBed) wakeEntity();

		if (par2 == 28 && mod_TukMC.closeOnFinish) mod_TukMC.shouldReopenChat = true;
		super.keyTyped(par1, par2);
	}

	@Override
	public void handleMouseInput() {
		int var1 = Mouse.getEventDWheel();
		int max = CHARS.length() * 26 + 3;
		int min = 28;
		int x = Mouse.getX();
		int y = Mouse.getY();
		int box = (x - 9) / ((max - min) / CHARS.length()) - 1;
		if (y >= 202 && y <= 223 && x >= min && x <= max) switch (box) {
			case 0:
				tooltip = "Converts the text in the chat field into a;Let me Google That For You link.";
				break;
			case 1:
				tooltip = "Shortens a link using tinyurl. " + ColorCode.RED + "(May take;" + ColorCode.RED + "a while)";
				break;
			case 2:
				tooltip = (mod_TukMC.spellcheckerEnabled ? "Disables" : "Enables") + " the Spellchecker";
				break;
			case 3:
				tooltip = (mod_TukMC.closeOnFinish ? "Unlocks" : "Locks") + " the Chat GUI (" + (mod_TukMC.closeOnFinish ? "doesn't exit" : "exits") + " after;saying something)";
				break;
			case 4:
				tooltip = (mod_TukMC.displayNotification ? "Disables" : "Enables") + " notifications for new messages.";
				break;
			case 5:
				tooltip = "Wipes the Chat.";
				break;
			case 6:
				tooltip = "Pins the text in the chat field to the;screen.";
				break;
			case 7:
				tooltip = "Unpins the text pinned to the screen.";
				break;
			case 8:
				tooltip = "Prints all out all the chat to a text;file. " + ColorCode.RED + "(Must be perssing SHIFT)";
		}
		else tooltip = "";

		int relativeWidth = Mouse.getEventX() * width / mc.displayWidth;
		int relativeHeight = height - Mouse.getEventY() * height / mc.displayHeight - 1;

		if (Mouse.getEventButtonState()) {
			int button = Mouse.getEventButton();
			Minecraft.getSystemTime();
			mouseClicked(relativeWidth, relativeHeight, button);
		} else if (Mouse.getEventButton() != -1) mouseMovedOrUp(relativeWidth, relativeHeight, Mouse.getEventButton());

		if (var1 != 0) {
			if (var1 > 1) var1 = 1;

			if (var1 < -1) var1 = -1;

			if (isShiftKeyDown()) var1 *= 7;

			mc.ingameGUI.getChatGUI().scroll(var1);
		}
	}

	@Override
	protected void mouseClicked(int par1, int par2, int par3) {
		int x = Mouse.getX();
		int y = Mouse.getY();
		int max = CHARS.length() * 26 + 3;
		int min = 28;
		int box = (x - 9) / ((max - min) / CHARS.length()) - 1;
		if (y >= 202 && y <= 223 && x >= min && x <= max) {
			mc.sndManager.playSoundFX("random.click", 1F, 1F);
			switch (box) {
				case 0: {
					URI uri = getURI();
					if (uri == null) {
						String text = inputField.getText();
						if (MathHelper.stringNullOrLengthZero(text)) break;

						String s = "http://lmgtfy.com/?q=" + text.replaceAll(" ", "+");
						inputField.setText(s);
					}
					break;
				}
				case 1: {
					URI uri = getURI();
					if (UpdateManager.online && uri != null) {
						String text = inputField.getText();
						if (text.contains("tinyurl.com/")) break;
						tinyURL url = new tinyURL();
						inputField.setText(url.getTinyURL(text).replaceAll("http://preview.", ""));
					}
					break;
				}
				case 2: {
					mod_TukMC.setSpellcheckerEnabled(!mod_TukMC.spellcheckerEnabled);
					break;
				}
				case 3: {
					mod_TukMC.setCloseOnFinish(!mod_TukMC.closeOnFinish);
					break;
				}
				case 4: {
					mod_TukMC.setDisplayNotification(!mod_TukMC.displayNotification);
					break;
				}
				case 5: {
					mc.ingameGUI.getChatGUI().func_73761_a();
					break;
				}
				case 6: {
					String text = inputField.getText();
					if (!MathHelper.stringNullOrLengthZero(text)) {
						mod_TukMC.setPinnedMsg(text);
						inputField.setText("");
					}
					break;
				}
				case 7: {
					inputField.setText(mod_TukMC.pinnedMsg);
					mod_TukMC.setPinnedMsg("");
					break;
				}
				case 8: {
					if (isShiftKeyDown()) {
						File cacheFolder = IOUtils.getCacheFile(EnumVazkiiMods.TUKMC).getParentFile();
						File subFolder = new File(cacheFolder, "TukMC ChatLogs");
						if (!subFolder.exists()) subFolder.mkdir();
						File log = new File(subFolder, new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".txt");
						try {
							log.createNewFile();
							BufferedWriter writer = new BufferedWriter(new FileWriter(log));
							List<TimedChatLine> chatLines = ((vazkii.tukmc.GuiNewChat) mc.ingameGUI.getChatGUI()).getChatLines();
							ListIterator<TimedChatLine> it = chatLines.listIterator();
							while (it.hasNext())
								it.next();
							while (it.hasPrevious()) {
								TimedChatLine line = it.previous();
								String lineString = "[" + line.getTime() + " (" + line.getMillisOfCreation() + ")" + "] " + line.getChatLineString() + "\r";
								writer.write(StringUtils.stripControlCodes(lineString));
							}
							writer.close();
							mc.thePlayer.addChatMessage("Chat saved to " + log.getAbsolutePath());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					break;
				}
			}
		}
		if (par3 == 0 && mc.gameSettings.chatLinks) {
			ChatClickData var4 = mc.ingameGUI.getChatGUI().func_73766_a(Mouse.getX() * 2, Mouse.getY() * 2);

			if (var4 != null) {
				URI var5 = var4.getURI();

				if (var5 != null) {
					if (mc.gameSettings.chatLinksPrompt) {
						ReflectionHelper.setPrivateValue(net.minecraft.src.GuiChat.class, this, var5, 6);
						mc.displayGuiScreen(new GuiChatConfirmLink(this, this, var4.getClickedUrl(), 0, var4));
					}

					return;
				}
			}
		}

		inputField.mouseClicked(par1 * 2, par2 * 2, par3);
	}

	public URI getURI() {
		String var1 = inputField.getText();

		if (var1 == null) return null;
		else {
			Matcher var2 = pattern.matcher(var1);

			if (var2.matches()) try {
				String var3 = var2.group(0);

				if (var2.group(1) == null) var3 = "http://" + var3;

				return new URI(var3);
			} catch (URISyntaxException var4) {
			}

			return null;
		}
	}

	public void drawOutlinedBox(int x, int y, int width, int height, int color, int outlineColor) {
		glPushMatrix();
		glScalef(0.5F, 0.5F, 0.5F);
		drawSolidRect(x * 2 - 2, y * 2 - 2, (x + width) * 2 + 2, (y + height) * 2 + 2, outlineColor);
		drawSolidRect(x * 2 - 1, y * 2 - 1, (x + width) * 2 + 1, (y + height) * 2 + 1, color);
		glPopMatrix();
	}

	public void drawDoubleOutlinedBox(int x, int y, int width, int height, int color, int outlineColor) {
		glPushMatrix();
		glScalef(0.5F, 0.5F, 0.5F);
		drawSolidRect(x * 2 - 2, y * 2 - 2, (x + width) * 2 + 2, (y + height) * 2 + 2, color);
		drawSolidRect(x * 2 - 1, y * 2 - 1, (x + width) * 2 + 1, (y + height) * 2 + 1, outlineColor);
		drawSolidRect(x * 2, y * 2, (x + width) * 2, (y + height) * 2, color);
		glPopMatrix();
	}

	public void drawSolidRect(int vertex1, int vertex2, int vertex3, int vertex4, int color) {
		glPushMatrix();
		Color color1 = new Color(color);
		Tessellator tess = Tessellator.instance;
		glDisable(GL_TEXTURE_2D);
		tess.startDrawingQuads();
		tess.setColorOpaque(color1.getRed(), color1.getGreen(), color1.getBlue());
		tess.addVertex(vertex1, vertex4, zLevel);
		tess.addVertex(vertex3, vertex4, zLevel);
		tess.addVertex(vertex3, vertex2, zLevel);
		tess.addVertex(vertex1, vertex2, zLevel);
		tess.draw();
		glEnable(GL_TEXTURE_2D);
		glPopMatrix();
	}

}
