package vazkii.tukmc;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;

import java.awt.Color;
import java.awt.Dimension;

import updatemanager.client.GuiModList;
import vazkii.codebase.common.CommonUtils;
import vazkii.codebase.common.FormattingCode;
import vazkii.codebase.common.VazkiiUpdateHandler;

import net.minecraft.src.FontRenderer;
import net.minecraft.src.Tessellator;

import cpw.mods.fml.common.Mod;

public class TukMCUpdateHandler extends VazkiiUpdateHandler {

	public TukMCUpdateHandler(Mod m) {
		super(m);
	}

	@Override
	public String getModName() {
		return "TukMC";
	}

	@Override
	public String getUMVersion() {
		return TukMCReference.VERSION;
	}

	@Override
	public String getUpdateURL() {
		return TukMCReference.UPDATE_URL;
	}

	@Override
	public String getChangelogURL() {
		return TukMCReference.CHANGELOG_URL;
	}

	@Override
	public Dimension renderIcon(int x, int y, GuiModList modList) {
		String s = FormattingCode.RANDOM + "XXXX";
		FontRenderer fr = CommonUtils.getMc().fontRenderer;
		int width = fr.getStringWidth(s);
		drawDoubleOutlinedBox(x, y, width + 6, 12, TukMCReference.BOX_INNER_COLOR, TukMCReference.BOX_OUTLINE_COLOR);
		fr.drawStringWithShadow(s, x + 3, y + 2, 0xFFFFFF);
		return new Dimension(width + 12, 24);
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
		tess.addVertex(vertex1, vertex4, -90);
		tess.addVertex(vertex3, vertex4, -90);
		tess.addVertex(vertex3, vertex2, -90);
		tess.addVertex(vertex1, vertex2, -90);
		tess.draw();
		glEnable(GL_TEXTURE_2D);
		glPopMatrix();
	}

}
