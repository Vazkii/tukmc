package vazkii.tukmc;

import java.io.File;

import updatemanager.common.ModConverter;
import vazkii.codebase.common.EnumVazkiiMods;
import vazkii.codebase.common.IOUtils;
import vazkii.codebase.common.mod_Vazcore;

import com.inet.jortho.SpellChecker;

import net.minecraft.src.NBTTagCompound;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.TickRegistry;

@Mod(modid = "tukmc_Vz", name = "TukMC", version = "by Vazkii. Version [1.0] for 1.4.4/5")
public class mod_TukMC {

	public static File cacheFile;

	public static String pinnedMsg = "";
	public static boolean spellcheckerEnabled = false;
	public static boolean closeOnFinish = false;

	public static boolean shouldReopenChat = false;

	@Init
	public void onInit(FMLInitializationEvent event) {
		mod_Vazcore.loadedVzMods.add(EnumVazkiiMods.TUKMC.getAcronym());
		KeyBindingRegistry.registerKeyBinding(new KeyRegister());
		TickRegistry.registerTickHandler(new TickHandler(), Side.CLIENT);
		cacheFile = IOUtils.getCacheFile(EnumVazkiiMods.TUKMC);
		SpellChecker.registerDictionaries(getClass().getResource("/com/inet/jortho/"), "en");

		NBTTagCompound cmp = IOUtils.getTagCompoundInFile(cacheFile);
		spellcheckerEnabled = cmp.hasKey("spellcheckerEnabled") ? cmp.getBoolean("spellcheckerEnabled") : true;
		closeOnFinish = cmp.hasKey("closeOnFinish") ? cmp.getBoolean("closeOnFinish") : false;
		pinnedMsg = cmp.hasKey("pinnnedMsg") ? cmp.getString("pinnnedMsg") : "";
		new TukMCUpdateHandler(ModConverter.getMod(getClass()));
	}

	public static void setPinnedMsg(String s) {
		pinnedMsg = s;
		NBTTagCompound cmp = IOUtils.getTagCompoundInFile(cacheFile);
		cmp.setString("pinnnedMsg", s);
		IOUtils.injectNBTToFile(cmp, cacheFile);
	}

	public static void setSpellcheckerEnabled(boolean b) {
		spellcheckerEnabled = b;
		NBTTagCompound cmp = IOUtils.getTagCompoundInFile(cacheFile);
		cmp.setBoolean("spellcheckerEnabled", b);
		IOUtils.injectNBTToFile(cmp, cacheFile);
	}

	public static void setCloseOnFinish(boolean b) {
		closeOnFinish = b;
		NBTTagCompound cmp = IOUtils.getTagCompoundInFile(cacheFile);
		cmp.setBoolean("closeOnFinish", b);
		IOUtils.injectNBTToFile(cmp, cacheFile);
	}
}
