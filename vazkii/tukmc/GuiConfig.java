package vazkii.tukmc;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import vazkii.codebase.common.FormattingCode;
import vazkii.tukmc.Config.Node;

import net.minecraft.src.FontRenderer;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.Tessellator;

public class GuiConfig extends GuiScreen {

	int box;
	static List<String> names = Arrays.asList(Config.nodes.keySet().toArray(new String[Config.nodes.size()]));

	@Override
	public void initGui() {
		super.initGui();
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
		FontRenderer fr = mc.fontRenderer;
		int width = mc.displayWidth;
		int height = mc.displayHeight;
		String configStr = FormattingCode.ITALICS + "TukMC Config";
		int configStrWidth = fr.getStringWidth(configStr);
		drawDoubleOutlinedBox(width / 4 - configStrWidth - 6, height / 4 - 115, configStrWidth * 2 + 12, 30, TukMCReference.BOX_INNER_COLOR, TukMCReference.BOX_OUTLINE_COLOR);
		drawDoubleOutlinedBox(width / 4 - 120, height / 4 - 90, 240, 192, TukMCReference.BOX_INNER_COLOR, TukMCReference.BOX_OUTLINE_COLOR);
		GL11.glPushMatrix();
		GL11.glScalef(2F, 2F, 2F);
		drawCenteredString(fr, configStr, width / 8, height / 8 - 55, 0xFFFFFF);
		GL11.glPopMatrix();
		int i = 0;
		boolean isOnBox = par1 >= 225 && par1 <= 460 && par2 >= height / 4 - 90 && par2 <= height / 4 + 60 + Config.nodes.size() * 11;
		box = !isOnBox ? -1 : (par2 - 90) / 11;
		if (box >= Config.nodes.size()) box = -1;
		if (box >= 0) drawOutlinedBox(width / 4 - 118, height / 4 - 87 + box * 11, 236, 10, TukMCReference.BOX_HIGHLIGHT_COLOR, TukMCReference.BOX_OUTLINE_COLOR);
		for (String s : names) {
			Node node = Config.nodes.get(s);
			fr.drawStringWithShadow(node.getDisplayName(), width / 4 - 115, height / 4 - 86 + i * 11, 0xFFFFFF);
			boolean enabled = node.isEnabled();
			if (Config.get(Config.NODE_COLORBLIND_MODE)) {
				String enabledStr = enabled ? "On" : "Off";
				fr.drawStringWithShadow(enabledStr, width / 4 + 117 - fr.getStringWidth(enabledStr), height / 4 - 85 + i * 11, 0xFFFFFF);
			} else drawOutlinedBox(width / 4 + 111, height / 4 - 85 + i * 11, 5, 5, enabled ? 0xFF00 : 0xFF0000, TukMCReference.BOX_OUTLINE_COLOR);
			++i;
		}
	}

	@Override
	protected void mouseClicked(int par1, int par2, int par3) {
		super.mouseClicked(par1, par2, par3);
		if (box >= 0) {
			String name = names.get(box);
			Node node = Config.nodes.get(name);
			node.set(!node.isEnabled());
			Config.saveNode(node);
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
