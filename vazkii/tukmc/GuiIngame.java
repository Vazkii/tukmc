package vazkii.tukmc;

import static org.lwjgl.opengl.GL11.GL_ALPHA_TEST;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_DST_COLOR;
import static org.lwjgl.opengl.GL11.GL_FLAT;
import static org.lwjgl.opengl.GL11.GL_GREATER;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_DST_COLOR;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_COLOR;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_ZERO;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glShadeModel;
import static org.lwjgl.opengl.GL12.GL_RESCALE_NORMAL;
import static vazkii.tukmc.TukMCReference.BOX_EFFECT_OUTLINE_COLOR;
import static vazkii.tukmc.TukMCReference.BOX_HIGHLIGHT_COLOR;
import static vazkii.tukmc.TukMCReference.BOX_INNER_COLOR;
import static vazkii.tukmc.TukMCReference.BOX_OUTLINE_COLOR;
import static vazkii.tukmc.TukMCReference.MC_VERSION;

import java.awt.Color;
import java.util.Collection;
import java.util.List;

import org.lwjgl.opengl.GL11;

import vazkii.codebase.client.ClientUtils;
import vazkii.codebase.common.ColorCode;
import vazkii.codebase.common.CommonUtils;
import vazkii.codebase.common.FormattingCode;
import net.minecraft.client.Minecraft;

import net.minecraft.src.Block;
import net.minecraft.src.BossStatus;
import net.minecraft.src.Chunk;
import net.minecraft.src.Direction;
import net.minecraft.src.Enchantment;
import net.minecraft.src.EnchantmentHelper;
import net.minecraft.src.EnumSkyBlock;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.GuiPlayerInfo;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemFood;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.MathHelper;
import net.minecraft.src.NetClientHandler;
import net.minecraft.src.Potion;
import net.minecraft.src.PotionEffect;
import net.minecraft.src.RenderEngine;
import net.minecraft.src.RenderHelper;
import net.minecraft.src.RenderItem;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.StatCollector;
import net.minecraft.src.Tessellator;

import net.minecraftforge.common.ForgeHooks;

public class GuiIngame extends net.minecraft.src.GuiIngame {

	private long rendersElapsed = 0;
	private Minecraft mc;
	private String recordPlaying = "";
	private int recordPlayingUpFor = 0;
	private boolean recordIsPlaying = false;
	private GuiNewChat presistentChatGui;

	public GuiIngame() {
		super(CommonUtils.getMc());
		mc = CommonUtils.getMc();
		presistentChatGui = new GuiNewChat(mc);
	}

	@Override
	public void renderGameOverlay(float par1, boolean par2, int par3, int par4) {
		++rendersElapsed;
		ScaledResolution res = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		int height = res.getScaledHeight();
		int width = res.getScaledWidth();
		FontRenderer fr = mc.fontRenderer;
		mc.entityRenderer.setupOverlayRendering();
		glEnable(GL_BLEND);
		if (Minecraft.isFancyGraphicsEnabled()) renderVignette(mc.thePlayer.getBrightness(par1), width, height);
		else glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		ItemStack head = mc.thePlayer.inventory.armorItemInSlot(3);
		if (mc.gameSettings.thirdPersonView == 0 && head != null && head.itemID == Block.pumpkin.blockID) renderPumpkinBlur(width, height);

		if (!hasPotion(Potion.confusion)) {
			float portalTime = mc.thePlayer.prevTimeInPortal + (mc.thePlayer.timeInPortal - mc.thePlayer.prevTimeInPortal) * par1;

			if (portalTime > 0.0F) renderPortalOverlay(portalTime, width, height);
		}

		glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		zLevel = -90.0F;
		drawDoubleOutlinedBox(6, height - 98, 5, 5, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
		drawDoubleOutlinedBox(width - 10, height - 98, 5, 5, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);

		drawOutlinedBox(170, height - 13, width-260, 1, BOX_OUTLINE_COLOR, BOX_INNER_COLOR);
		drawOutlinedBox(8, height - 13, 40, 1, BOX_OUTLINE_COLOR, BOX_INNER_COLOR);
		drawOutlinedBox(8, height - 92, 1, 80, BOX_OUTLINE_COLOR, BOX_INNER_COLOR);
		drawOutlinedBox(width - 48, height - 13, 40, 1, BOX_OUTLINE_COLOR, BOX_INNER_COLOR);
		drawOutlinedBox(width - 8, height - 92, 1, 80, BOX_OUTLINE_COLOR, BOX_INNER_COLOR);
		glPushMatrix();
		glScalef(0.5F, 0.5F, 0.5F);
		drawSolidRect(15, height * 2 - 27, 23, height * 2 - 23, BOX_OUTLINE_COLOR);
		drawSolidRect(width * 2 - 18, height * 2 - 27, width * 2 - 13, height * 2 - 23, BOX_OUTLINE_COLOR);
		drawSolidRect(14, height * 2 - 186, 20, height * 2 - 185, BOX_OUTLINE_COLOR);
		drawSolidRect(width * 2 - 18, height * 2 - 186, width * 2 - 10, height * 2 - 185, BOX_OUTLINE_COLOR);
		glPopMatrix();
		drawDoubleOutlinedBox(width / 2 - 90, height - 22, 180, 20, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);

		InventoryPlayer inv = mc.thePlayer.inventory;
		for (int i = 0; i < 9; ++i) {
			int i1 = width / 2 - 88 + i * 20;
			int i2 = height - 20;
			boolean isHighlight = inv.currentItem == i;
			boolean isSlot = inv.mainInventory[i] != null;

			if (isSlot) drawDoubleOutlinedBox(i1, i2, 16, 16, isHighlight ? BOX_HIGHLIGHT_COLOR : BOX_INNER_COLOR, inv.mainInventory[i].hasEffect() && !isHighlight ? BOX_EFFECT_OUTLINE_COLOR : BOX_OUTLINE_COLOR);
			else if (isHighlight) drawDoubleOutlinedBox(i1 + 1, i2 + 1, 14, 14, BOX_HIGHLIGHT_COLOR, BOX_HIGHLIGHT_COLOR);
		}
		glEnable(GL_RESCALE_NORMAL);
		RenderHelper.enableGUIStandardItemLighting();
		glDisable(GL_BLEND);
		for (int i = 0; i < 9; ++i) {
			int i1 = width / 2 - 88 + i * 20;
			int i2 = height - 20;

			renderSlot(i, i1, i2, par1, fr);
		}
		RenderHelper.disableStandardItemLighting();
		glDisable(GL_RESCALE_NORMAL);
		boolean shouldDrawHUD = mc.playerController.shouldDrawHUD();

		if (shouldDrawHUD) {
			drawDoubleOutlinedBox(width / 2 - 90, height - 42, 80, 10, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			drawDoubleOutlinedBox(width / 2 - 90, height - 29, 80, 4, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			int healthBottom = hasPotion(Potion.regeneration) ? 0xd82424 : 0;
			int healthTop = hasPotion(Potion.wither) ? 0 : 0x901414;
			if (hasPotion(Potion.poison)) healthBottom = 0x375d12;
			int hp = mc.thePlayer.getHealth();
			int food = mc.thePlayer.getFoodStats().getFoodLevel();
			drawSolidGradientRect(width / 2 - 90, height - 42, hp * 4, 10, healthBottom, healthTop);
			int foodHeal = 0;
			boolean overkill = false;
			if(food != 20) {
				int barWidth = 0;
				ItemStack stack = mc.thePlayer.getCurrentEquippedItem();
				if(stack != null) {
					Item item = stack.getItem();
					if(item != null && item instanceof ItemFood) {
						foodHeal = ((ItemFood)item).getHealAmount();
						barWidth = Math.min(20, food + foodHeal);
						if(food + foodHeal > 20)
							overkill = true;
					}
				}
				if(barWidth > 0)
					drawSolidGradientRect(width / 2 - 90, height - 29, barWidth * 4, 4, overkill ? 0 :  0xd82424, 0x901414);
			}
			drawSolidGradientRect(width / 2 - 90, height - 29, food * 4, 4, hasPotion(Potion.hunger) ? 0x0c1702 : 0x6a410b, hasPotion(Potion.hunger) ? 0x1d3208 : 0x8e5409);
			glPushMatrix();
			glScalef(0.5F, 0.5F, 0.5F);
			if(foodHeal > 0)
				fr.drawString("Will Heal: " + foodHeal + (overkill ? (" (Over " + (food + foodHeal - 20) + ")") : ""), width - 178, height * 2 - 57, 0xFFFFFF);

			fr.drawStringWithShadow((hp < 5 ? ColorCode.RED : "") + "" + hp, width - 33, height * 2 - 84, 0xFFFFFF);
			fr.drawStringWithShadow((food < 5 ? ColorCode.RED : "") + "" + food, width - 33, height * 2 - 58, 0xFFFFFF);
			glPopMatrix();
			int lvl = mc.thePlayer.experienceLevel;
			String lvlStr = ColorCode.BRIGHT_GREEN + "" + lvl;
			if (lvl > 0) {
				drawDoubleOutlinedBox(width / 2 - fr.getStringWidth(lvlStr) / 2 - 1, height - 35, fr.getStringWidth(lvlStr) + 2, 10, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
				fr.drawStringWithShadow(lvlStr, width / 2 - fr.getStringWidth(lvlStr) / 2, height - 34, 0xFFFFFF);
			}
			drawDoubleOutlinedBox(width / 2 + 10, height - 29, 80, 4, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			drawSolidGradientRect(width / 2 + 10, height - 29, (int) (mc.thePlayer.experience * 80), 4, 0x05d714, 0x8fea96);
			glPushMatrix();
			glScalef(0.5F, 0.5F, 0.5F);
			int relativeXP = (int) Math.floor(mc.thePlayer.experience * mc.thePlayer.xpBarCap());
			String lvlXP = ColorCode.BRIGHT_GREEN + "" + relativeXP;
			fr.drawStringWithShadow(lvlXP, (int) (width + fr.getStringWidth(lvlXP) * 2 + mc.thePlayer.experience * 160 + 2), height * 2 - 62, 0xFFFFFF);
			fr.drawStringWithShadow("" + mc.thePlayer.xpBarCap(), width + 180, height * 2 - 52, 0xFFFFFF);
			glPopMatrix();

			if (mc.thePlayer.isInsideOfMaterial(Material.water)) {
				int record = recordIsPlaying ? 20 : 0;
				int air = mc.thePlayer.getAir() + 20;
				drawDoubleOutlinedBox(width / 2 - 80, height - 60 - record, 160, 5, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
				drawSolidGradientRect(width / 2 - 80, height - 60 - record, air / 2, 5, air < 60 ? 0xff1818 : 0x18cbff, air < 60 ? 0xff8c8c : 0x8ce5ff);
				String airStr = "Air:";
				int offset = (int) (air >= 60 ? 0 : Math.sin(rendersElapsed) * 10);
				fr.drawStringWithShadow(airStr, width / 2 - fr.getStringWidth(airStr) / 2 + offset, height - 72 - record, 0xFFFFFF);
			}
		}

		String status = "";
		int fallDmg = MathHelper.ceiling_float_int(mc.thePlayer.fallDistance - 3.0F);
		if (mc.thePlayer.isSneaking()) status = "Sneaking";
		if (mc.thePlayer.isSprinting()) status = "Sprinting";
		if (mc.thePlayer.capabilities.isFlying) status = "Flying";
		else if (fallDmg > 0 && !mc.thePlayer.capabilities.isCreativeMode) status = "Falling: " + ColorCode.RED + fallDmg;
		fr.drawStringWithShadow(mc.thePlayer.username + (status.equals("") ? "" : " - ") + status, width / 2 + 9, height - 33 - (shouldDrawHUD ? 8 : 0), 0xFFFFFF);

		glBindTexture(GL_TEXTURE_2D, mc.renderEngine.getTexture("/gui/icons.png"));
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ONE_MINUS_SRC_COLOR);
		drawTexturedModalRect(width / 2 - 7, height / 2 - 7, 0, 0, 16, 16);
		glDisable(GL_BLEND);

		drawDoubleOutlinedBox(40, height - 20, 140, 16, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
		fr.drawStringWithShadow("Online: " + mc.thePlayer.sendQueue.playerInfoList.size(), 44, height - 16, 0xFFFFFF);
		int armorValue = ForgeHooks.getTotalArmorValue(mc.thePlayer);
		String armor = armorValue > 0 ? "Armor: " + armorValue * 5 + "%" : "Unarmored";
		fr.drawStringWithShadow(armor, 176 - fr.getStringWidth(armor), height - 16, 0xFFFFFF);

		drawDoubleOutlinedBox(width - 180, height - 20, 140, 16, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
		fr.drawStringWithShadow("FPS: " + ClientUtils.getFPS(), width - 176, height - 16, 0xFFFFFF);
		String ping = ClientUtils.getPing() + "ms." + (mc.isIntegratedServerRunning() ? mc.isSingleplayer() ? " (SP)" : " (LAN)" : " (MP)");
		fr.drawStringWithShadow(ping, width - 44 - fr.getStringWidth(ping), height - 16, 0xFFFFFF);

		if (recordIsPlaying) {
			float color = recordPlayingUpFor - par1;
			int colorValue = (int) (color * 256.0F / 20.0F);
			int colorRgb = 0xFFFFFF;
			if (colorValue > 255) colorValue = 255;
			if (colorValue > 0) {
				colorRgb = Color.HSBtoRGB(color / 50.0F, 0.7F, 0.6F) & 16777215;
				Color colorInstance = new Color(colorRgb);
				int lenght = fr.getStringWidth(recordPlaying);

				drawDoubleOutlinedBox(width / 2 - lenght / 2 - 20, height - 70, lenght + 40, 20, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
				glBindTexture(GL_TEXTURE_2D, mc.renderEngine.getTexture("/particles.png"));
				glDisable(GL_DEPTH_TEST);
				glColor3f(colorInstance.getRed() / 255F, colorInstance.getGreen() / 255F, colorInstance.getBlue() / 255F);
				drawTexturedModalRect(width / 2 - lenght / 2 - 18, height - 68, 0, 64, 16, 16);
				glColor3f(colorInstance.getRed() / 255F, colorInstance.getGreen() / 255F, colorInstance.getBlue() / 255F);
				drawTexturedModalRect(width / 2 + lenght / 2, height - 68, 0, 64, 16, 16);
				glEnable(GL_DEPTH_TEST);
				fr.drawStringWithShadow(recordPlaying, width / 2 - lenght / 2, height - 65, colorRgb);
			}

			if (recordPlayingUpFor <= 0) recordIsPlaying = false;
		}

		if (mc.gameSettings.showDebugInfo) {
			glPushMatrix();
			fr.drawStringWithShadow("Minecraft " + MC_VERSION + " (" + mc.debug + ")", 2, 2, 0xFFFFFF);
			fr.drawStringWithShadow(mc.debugInfoRenders(), 2, 12, 0xFFFFFF);
			fr.drawStringWithShadow(mc.getEntityDebug(), 2, 22, 0xFFFFFF);
			fr.drawStringWithShadow(mc.debugInfoEntities(), 2, 32, 0xFFFFFF);
			fr.drawStringWithShadow(mc.getWorldProviderName(), 2, 42, 0xFFFFFF);
			long maxMemory = Runtime.getRuntime().maxMemory();
			long totalMemory = Runtime.getRuntime().totalMemory();
			long freeMemory = Runtime.getRuntime().freeMemory();
			long usedMemory = totalMemory - freeMemory;
			String string = "Used memory: " + usedMemory * 100L / maxMemory + "% (" + usedMemory / 1024L / 1024L + "MB) of " + maxMemory / 1024L / 1024L + "MB";
			drawString(fr, string, width - fr.getStringWidth(string) - 2, 2, 14737632);
			string = "Allocated memory: " + totalMemory * 100L / maxMemory + "% (" + totalMemory / 1024L / 1024L + "MB)";
			drawString(fr, string, width - fr.getStringWidth(string) - 2, 12, 14737632);
			int posX = MathHelper.floor_double(mc.thePlayer.posX);
			int posY = MathHelper.floor_double(mc.thePlayer.posY);
			int posZ = MathHelper.floor_double(mc.thePlayer.posZ);
			drawString(fr, String.format("x: %.5f (%d) // c: %d (%d)", Double.valueOf(mc.thePlayer.posX), Integer.valueOf(posX), Integer.valueOf(posX >> 4), Integer.valueOf(posX & 15)), 2, 64, 14737632);
			drawString(fr, String.format("y: %.3f (feet pos, %.3f eyes pos)", Double.valueOf(mc.thePlayer.boundingBox.minY), Double.valueOf(mc.thePlayer.posY)), 2, 72, 14737632);
			drawString(fr, String.format("z: %.5f (%d) // c: %d (%d)", Double.valueOf(mc.thePlayer.posZ), Integer.valueOf(posZ), Integer.valueOf(posZ >> 4), Integer.valueOf(posZ & 15)), 2, 80, 14737632);
			int direction = MathHelper.floor_double(mc.thePlayer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
			drawString(fr, "f: " + direction + " (" + Direction.directions[direction] + ") / " + MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw), 2, 88, 14737632);

			if (mc.theWorld != null && mc.theWorld.blockExists(posX, posY, posZ)) {
				Chunk chunk = mc.theWorld.getChunkFromBlockCoords(posX, posZ);
				drawString(fr, "lc: " + (chunk.getTopFilledSegment() + 15) + " b: " + chunk.getBiomeGenForWorldCoords(posX & 15, posZ & 15, mc.theWorld.getWorldChunkManager()).biomeName + " bl: " + chunk.getSavedLightValue(EnumSkyBlock.Block, posX & 15, posY, posZ & 15) + " sl: " + chunk.getSavedLightValue(EnumSkyBlock.Sky, posX & 15, posY, posZ & 15) + " rl: " + chunk.getBlockLightValue(posX & 15, posY, posZ & 15, 0), 2, 96, 14737632);
			}

			drawString(fr, String.format("ws: %.3f, fs: %.3f, g: %b, fl: %d", Float.valueOf(mc.thePlayer.capabilities.getWalkSpeed()), Float.valueOf(mc.thePlayer.capabilities.getFlySpeed()), Boolean.valueOf(mc.thePlayer.onGround), Integer.valueOf(mc.theWorld.getHeightValue(posX, posZ))), 2, 104, 14737632);
			glPopMatrix();
		}

		presistentChatGui.drawChat(getUpdateCounter());
		if(TickHandler.getMsgs() != 0 && mod_TukMC.displayNotification) {
			String s = "! " + ColorCode.RED + TickHandler.getMsgs() + ColorCode.WHITE + " !";
			drawDoubleOutlinedBox(195, height - 89, fr.getStringWidth(s)+6, 12, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			fr.drawStringWithShadow(s, 198, height - 87, 0xFFFFFF);
		}

		if (BossStatus.bossName != null && BossStatus.field_82826_b > 0) {
			drawDoubleOutlinedBox(width / 2 - 126, 31, 5, 5, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			drawDoubleOutlinedBox(width / 2 + 121, 31, 5, 5, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);

			drawOutlinedBox(width / 2 - 119, 33, 238, 1, BOX_OUTLINE_COLOR, BOX_INNER_COLOR);
			glPushMatrix();
			glDisable(GL_DEPTH_TEST);
			glScalef(0.5F, 0.5F, 0.5F);
			drawSolidRect(width - 243, 65, width + 240, 69, BOX_OUTLINE_COLOR);
			glEnable(GL_DEPTH_TEST);
			glPopMatrix();
			BossStatus.field_82826_b--;
			drawOutlinedBox(width / 2 - fr.getStringWidth(BossStatus.bossName) / 2 - 3, 17, fr.getStringWidth(BossStatus.bossName) + 6, 14, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			drawDoubleOutlinedBox(width / 2 - 91, 28, 182, 10, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			int renderHealth = (int) (BossStatus.healthScale * 182);
			drawSolidGradientRect(width / 2 - 91, 28, renderHealth, 10, 0, 0x25092e);
			fr.drawStringWithShadow(ColorCode.PURPLE + BossStatus.bossName, width / 2 - fr.getStringWidth(BossStatus.bossName) / 2, 18, 0xFFFFFF);
			String hp = (BossStatus.healthScale < 0.1 ? ColorCode.RED : "") + "" + Math.round(BossStatus.healthScale * 100) + "%";
			if (!(BossStatus.healthScale < 0)) fr.drawStringWithShadow(hp, width / 2 - fr.getStringWidth(hp) / 2, 29, 0xFFFFFF);
		}

		if (mc.gameSettings.keyBindPlayerList.pressed && (!mc.isIntegratedServerRunning() || mc.thePlayer.sendQueue.playerInfoList.size() > 1)) {
			mc.mcProfiler.startSection("playerList");
			NetClientHandler var37 = mc.thePlayer.sendQueue;
			List var39 = var37.playerInfoList;
			int var13 = var37.currentServerMaxPlayers;
			int var40 = var13;
			int var38;

			for (var38 = 1; var40 > 20; var40 = (var13 + var38 - 1) / var38)
				++var38;

			int var16 = 300 / var38;

			if (var16 > 150) var16 = 150;

			int var17 = (width - var38 * var16) / 2;
			byte var44 = 18;
			drawDoubleOutlinedBox(var17 - 2, var44 - 2, var16 * var38 + 3, 9 * var40 + 3, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			int var19;
			int var20;
			int var47;

			for (var19 = 0; var19 < var13; ++var19) {
				var20 = var17 + var19 % var38 * var16;
				var47 = var44 + var19 / var38 * 9;
				drawOutlinedBox(var20, var47, var16 - 1, 8, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glEnable(GL11.GL_ALPHA_TEST);

				if (var19 < var39.size()) {
					GuiPlayerInfo var46 = (GuiPlayerInfo) var39.get(var19);
					fr.drawStringWithShadow(var46.name, var20, var47, 16777215);
					mc.renderEngine.bindTexture(mc.renderEngine.getTexture("/gui/icons.png"));
					byte var50 = 0;
					byte var49;

					if (var46.responseTime < 0) var49 = 5;
					else if (var46.responseTime < 150) var49 = 0;
					else if (var46.responseTime < 300) var49 = 1;
					else if (var46.responseTime < 600) var49 = 2;
					else if (var46.responseTime < 1000) var49 = 3;
					else var49 = 4;

					zLevel += 100.0F;
					drawTexturedModalRect(var20 + var16 - 12, var47, 0 + var50 * 10, 176 + var49 * 8, 10, 8);
					GL11.glPushMatrix();
					GL11.glScalef(0.5F, 0.5F, 0.5F);
					GL11.glDisable(GL11.GL_DEPTH_TEST);
					String ms = var46.responseTime + " ms.";
					fr.drawStringWithShadow(ms, (var20 + var16 - 9 - fr.getStringWidth(ms) / 2) * 2, var47 * 2, 16777215);
					glEnable(GL11.GL_DEPTH_TEST);
					glPopMatrix();
					zLevel -= 100.0F;
				}
			}
		}

		Collection<PotionEffect> potions = mc.thePlayer.getActivePotionEffects();
		int xPotOffset = 0;
		int yPotOffset = 0;
		int itr = 0;
		for (PotionEffect effect : potions) {
			Potion pot = Potion.potionTypes[effect.getPotionID()];
			if (itr % 8 == 0) {
				xPotOffset = 0;
				yPotOffset += 1;
			}
			String effectStr = Potion.getDurationString(effect);
			drawDoubleOutlinedBox(width - 30 - xPotOffset * 21, height - 9 - yPotOffset * 28, fr.getStringWidth(effectStr) / 2 + 2, 8, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			drawDoubleOutlinedBox(width - 30 - xPotOffset * 21, height - 26 - yPotOffset * 28, 18, 18, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_LIGHTING);
			glDisable(GL_DEPTH_TEST);
			int index = pot.getStatusIconIndex();
			mc.renderEngine.bindTexture(mc.renderEngine.getTexture("/gui/inventory.png"));
			if (pot.hasStatusIcon()) drawTexturedModalRect(width - 30 - xPotOffset * 21, height - 26 - yPotOffset * 28, 0 + index % 8 * 18, 198 + index / 8 * 18, 18, 18);
			glEnable(GL_DEPTH_TEST);

			String level = StatCollector.translateToLocal("enchantment.level." + (effect.getAmplifier() + 1));

			if (level.length() < 5 && !level.equals(StatCollector.translateToLocal("enchantment.level.1"))) fr.drawStringWithShadow(level, width - 29 - xPotOffset * 21, height - 25 - yPotOffset * 28, 0xFFFFFF);

			glPushMatrix();
			glScalef(0.5F, 0.5F, 0.5F);
			fr.drawStringWithShadow((effect.func_82720_e() ? ColorCode.RED : "") + effectStr, (width - 29 - xPotOffset * 21) * 2, (height - 6 - yPotOffset * 28) * 2, 0xFFFFFF);
			glPopMatrix();
			++itr;
			++xPotOffset;
		}

		tooltip: {
			if (KeyRegister.showTooltipKB.pressed) {
				ItemStack stack = mc.thePlayer.getCurrentEquippedItem();
				if (stack == null) break tooltip;
				int loc = mc.thePlayer.inventory.currentItem;

				int x = width / 2 - 88 + loc * 20;
				int y = height - 20;

				List<String> tokensList = stack.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);
				if (tokensList.isEmpty()) break tooltip;

				glPushMatrix();
				glDisable(GL_DEPTH_TEST);

				int lenght = 12;
				for (String s : tokensList)
					lenght = Math.max(lenght, fr.getStringWidth(s));
						drawDoubleOutlinedBox(x, y - tokensList.size() * 12 - 5, lenght + 4, tokensList.size() * 12, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
						int i = 1;
						for (String s : tokensList) {
							if (i == 1) s = "\u00a7" + Integer.toHexString(stack.getRarity().rarityColor) + s;
							else s = "\u00a77" + s;
							if (i == 1) fr.drawStringWithShadow(s, x + 2, y - (tokensList.size() + 1) * 12 + i * 12 - 3, 0xFFFFFF);
							else fr.drawString(s, x + 2, y - (tokensList.size() + 1) * 12 + i * 12 - 3, 0xFFFFFF);
							++i;
						}
						glEnable(GL_DEPTH_TEST);
						glPopMatrix();
			}
		}
	}

	public void drawDoubleOutlinedBox(int x, int y, int width, int height, int color, int outlineColor) {
		drawDoubleOutlinedBox(x, y, width, height, color, outlineColor, color);
	}

	public void drawDoubleOutlinedBox(int x, int y, int width, int height, int color, int outlineColor, int outline2Color) {
		glPushMatrix();
		glScalef(0.5F, 0.5F, 0.5F);
		drawSolidRect(x * 2 - 2, y * 2 - 2, (x + width) * 2 + 2, (y + height) * 2 + 2, color);
		drawSolidRect(x * 2 - 1, y * 2 - 1, (x + width) * 2 + 1, (y + height) * 2 + 1, outlineColor);
		drawSolidRect(x * 2, y * 2, (x + width) * 2, (y + height) * 2, outline2Color);
		glPopMatrix();
	}

	public void drawOutlinedBox(int x, int y, int width, int height, int color, int outlineColor) {
		glPushMatrix();
		glScalef(0.5F, 0.5F, 0.5F);
		drawSolidRect(x * 2 - 2, y * 2 - 2, (x + width) * 2 + 2, (y + height) * 2 + 2, outlineColor);
		drawSolidRect(x * 2 - 1, y * 2 - 1, (x + width) * 2 + 1, (y + height) * 2 + 1, color);
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

	public void drawSolidGradientRect(int x, int y, int width, int height, int color1, int color2) {
		drawSolidGradientRect0(x * 2, y * 2, (x + width) * 2, (y + height) * 2, color1, color2);
	}

	public void drawSolidGradientRect0(int vertex1, int vertex2, int vertex3, int vertex4, int color1, int color2) {
		glPushMatrix();
		glScalef(0.5F, 0.5F, 0.5F);
		Color color1Color = new Color(color1);
		Color color2Color = new Color(color2);
		glDisable(GL_TEXTURE_2D);
		glDisable(GL_ALPHA_TEST);
		glShadeModel(GL_SMOOTH);
		Tessellator tess = Tessellator.instance;
		tess.startDrawingQuads();
		tess.setColorOpaque(color1Color.getRed(), color1Color.getGreen(), color1Color.getBlue());
		tess.addVertex(vertex1, vertex4, zLevel);
		tess.addVertex(vertex3, vertex4, zLevel);
		tess.setColorOpaque(color2Color.getRed(), color2Color.getGreen(), color2Color.getBlue());
		tess.addVertex(vertex3, vertex2, zLevel);
		tess.addVertex(vertex1, vertex2, zLevel);
		tess.draw();
		glShadeModel(GL_FLAT);
		glEnable(GL_ALPHA_TEST);
		glEnable(GL_TEXTURE_2D);
		glPopMatrix();
	}

	private void renderSlot(int slot, int x, int y, float ticks, FontRenderer font) {
		RenderEngine render = mc.renderEngine;
		RenderItem itemRenderer = new RenderItem();
		ItemStack stack = mc.thePlayer.inventory.mainInventory[slot];

		if (stack != null) {
			int dmg = stack.getItemDamageForDisplay();
			int color = (int) Math.round(255.0D - dmg * 255.0D / stack.getMaxDamage());
			int shiftedColor = 255 - color << 16 | color << 8;
			Color shiftedColor1 = new Color(shiftedColor);

			if (stack != null && stack.hasEffect()) {
				glDepthFunc(GL_GREATER);
				glDisable(GL_LIGHTING);
				glDepthMask(false);
				render.bindTexture(render.getTexture("/misc/glint.png"));
				zLevel -= 50.0F;
				glEnable(GL_BLEND);
				if (mc.thePlayer.inventory.currentItem == slot) glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
				else glBlendFunc(GL_DST_COLOR, GL_DST_COLOR);
				if (slot == mc.thePlayer.inventory.currentItem) {
					if (!stack.isItemDamaged()) glColor4f(0.5F, 0.25F, 0.8F, 0.4F);
					else glColor4f(shiftedColor1.getRed() / 255F, shiftedColor1.getGreen() / 255F, shiftedColor1.getBlue() / 255F, 0.4F);
					renderGlint(x * 431278612 + y * 32178161, x, y, 16, 16);
				}
				glDisable(GL_BLEND);
				glDepthMask(true);
				zLevel += 50.0F;
				glEnable(GL_LIGHTING);
				glDepthFunc(GL_LEQUAL);
			}
			itemRenderer.renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, stack, x, y);

			glDisable(GL_LIGHTING);
			glDisable(GL_DEPTH_TEST);

			int offset = 0;

			if (stack.isItemStackDamageable()) {
				String dmgStr = "" + (stack.getMaxDamage() - dmg + 1);
				offset = 6;
				int unbreakLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
				glPushMatrix();
				glScalef(0.5F, 0.5F, 0.5F);
				font.drawStringWithShadow(dmgStr, (x + 16 - font.getStringWidth(dmgStr) / 2) * 2, (y + 11) * 2, dmg == 0 ? 0xFFFFFF : shiftedColor);
				if (unbreakLvl > 0) font.drawStringWithShadow(ColorCode.PINK + "" + /*
				 * StatCollector
				 * .
				 * translateToLocal
				 * (
				 * "enchantment.level."
				 * +
				 */unbreakLvl/* ) */, (x + 1) * 2, (y + 1) * 2, 0xFFFFFF);
				glScalef(1F, 1F, 1F);
				glPopMatrix();

			}

			if (stack.stackSize > 1) {
				String size = FormattingCode.BOLD + "" + stack.stackSize;
				int sizeWidth = font.getStringWidth(size);
				glPushMatrix();
				glScalef(0.5F, 0.5F, 0.5F);
				font.drawStringWithShadow(size, (x + 16 - sizeWidth / 2) * 2, (y + 12 - offset) * 2, 0xFFFFFF);
				glScalef(1F, 1F, 1F);
				glPopMatrix();
			}

			glEnable(GL_LIGHTING);
			glEnable(GL_DEPTH_TEST);
		}
	}

	@Override
	public void setRecordPlayingMessage(String record) {
		recordPlaying = record;
		recordPlayingUpFor = 60;
		recordIsPlaying = true;
	}

	@Override
	public void updateTick() {
		if (recordPlayingUpFor > 0) --recordPlayingUpFor;
		super.updateTick();
	}

	// The method in GuiIngame is private, full override was necessary.
	// I don't know what some of the params are, so I left them all as parX
	private void renderGlint(int par1, int par2, int par3, int par4, int par5) {
		for (int i = 0; i < 2; ++i) {
			float var7 = 0.00390625F;
			float var8 = 0.00390625F;
			float var9 = Minecraft.getSystemTime() % (3000 + i * 1873) / (3000.0F + i * 1873) * 256F;
			float var10 = 0F;
			float var12 = i == 1 ? -1F : 4F;
			Tessellator tess = Tessellator.instance;
			tess.startDrawingQuads();
			tess.addVertexWithUV(par2, par3 + par5, zLevel, (var9 + par5 * var12) * var7, (var10 + par5) * var8);
			tess.addVertexWithUV(par2 + par4, par3 + par5, zLevel, (var9 + par4 + par5 * var12) * var7, (var10 + par5) * var8);
			tess.addVertexWithUV(par2 + par4, par3 + 0, zLevel, (var9 + par4) * var7, var10 * var8);
			tess.addVertexWithUV(par2 + 0, par3 + 0, zLevel, var9 * var7, var10 * var8);
			tess.draw();
		}
	}

	// Hopefully to clean code, will be used a fair bit
	public boolean hasPotion(Potion pot) {
		return mc.thePlayer.isPotionActive(pot.id);
	}

	private void renderPumpkinBlur(int par1, int par2) {
		glDisable(GL_DEPTH_TEST);
		glDepthMask(false);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glColor4f(1F, 1F, 1F, 1F);
		glDisable(GL_ALPHA_TEST);
		glBindTexture(GL_TEXTURE_2D, mc.renderEngine.getTexture("%blur%/misc/pumpkinblur.png"));
		Tessellator tess = Tessellator.instance;
		tess.startDrawingQuads();
		tess.addVertexWithUV(0D, par2, -90D, 0D, 1D);
		tess.addVertexWithUV(par1, par2, -90D, 1D, 1D);
		tess.addVertexWithUV(par1, 0D, -90D, 1D, 0D);
		tess.addVertexWithUV(0D, 0D, -90D, 0D, 0D);
		tess.draw();
		glDepthMask(true);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_ALPHA_TEST);
		glColor4f(1F, 1F, 1F, 1F);
	}

	private void renderVignette(float par1, int par2, int par3) {
		par1 = 1.0F - par1;
		if (par1 < 0.0F) par1 = 0.0F;
		if (par1 > 1.0F) par1 = 1.0F;

		prevVignetteBrightness = (float) (prevVignetteBrightness + (par1 - prevVignetteBrightness) * 0.01);
		glDisable(GL_DEPTH_TEST);
		glDepthMask(false);
		glBlendFunc(GL_ZERO, GL_ONE_MINUS_SRC_COLOR);
		glColor4f(prevVignetteBrightness, prevVignetteBrightness, prevVignetteBrightness, 1F);
		glBindTexture(GL_TEXTURE_2D, mc.renderEngine.getTexture("%blur%/misc/vignette.png"));
		Tessellator tess = Tessellator.instance;
		tess.startDrawingQuads();
		tess.addVertexWithUV(0D, par3, -90D, 0D, 1D);
		tess.addVertexWithUV(par2, par3, -90D, 1D, 1D);
		tess.addVertexWithUV(par2, 0D, -90D, 1D, 0D);
		tess.addVertexWithUV(0D, 0D, -90D, 0D, 0D);
		tess.draw();
		glDepthMask(true);
		glEnable(GL_DEPTH_TEST);
		glColor4f(1F, 1F, 1F, 1F);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}

	private void renderPortalOverlay(float par1, int par2, int par3) {
		if (par1 < 1.0F) {
			par1 *= par1;
			par1 *= par1;
			par1 = par1 * 0.8F + 0.2F;
		}

		glDisable(GL_ALPHA_TEST);
		glDisable(GL_DEPTH_TEST);
		glDepthMask(false);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glColor4f(1F, 1F, 1F, par1);
		glBindTexture(GL_TEXTURE_2D, mc.renderEngine.getTexture("/terrain.png"));
		float var4 = (Block.portal.blockIndexInTexture % 16) / 16.0F;
		float var5 = (Block.portal.blockIndexInTexture / 16) / 16.0F;
		float var6 = (Block.portal.blockIndexInTexture % 16 + 1) / 16.0F;
		float var7 = (Block.portal.blockIndexInTexture / 16 + 1) / 16.0F;
		Tessellator tess = Tessellator.instance;
		tess.startDrawingQuads();
		tess.addVertexWithUV(0D, par3, -90D, var4, var7);
		tess.addVertexWithUV(par2, par3, -90D, var6, var7);
		tess.addVertexWithUV(par2, 0D, -90D, var6, var5);
		tess.addVertexWithUV(0D, 0D, -90D, var4, var5);
		tess.draw();
		glDepthMask(true);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_ALPHA_TEST);
		glColor4f(1F, 1F, 1F, 1F);
	}

	@Override
	public GuiNewChat getChatGUI() {
		return presistentChatGui;
	}
}
