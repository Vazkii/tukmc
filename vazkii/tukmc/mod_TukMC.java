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

import net.minecraftforge.common.MinecraftForge;

@Mod(modid = "tukmc_Vz", name = "TukMC", version = "by Vazkii. Version [1.0.2] for 1.4.4/5")
public class mod_TukMC {

	public static File cacheFile;

	public static String pinnedMsg = "";
	public static boolean spellcheckerEnabled = true;
	public static boolean closeOnFinish = false;
	public static boolean displayNotification = true;
	public static boolean formatMessage = true;

	public static boolean shouldReopenChat = false;

	@Init
	public void onInit(FMLInitializationEvent event) {
		mod_Vazcore.loadedVzMods.add(EnumVazkiiMods.TUKMC.getAcronym());
		KeyBindingRegistry.registerKeyBinding(new KeyRegister());
		TickRegistry.registerTickHandler(new TickHandler(), Side.CLIENT);
		cacheFile = IOUtils.getCacheFile(EnumVazkiiMods.TUKMC);
		SpellChecker.registerDictionaries(getClass().getResource("/com/inet/jortho/"), "en");
		MinecraftForge.EVENT_BUS.register(new ChatListener());

		NBTTagCompound cmp = IOUtils.getTagCompoundInFile(cacheFile);
		spellcheckerEnabled = cmp.hasKey("spellcheckerEnabled") ? cmp.getBoolean("spellcheckerEnabled") : true;
		displayNotification = cmp.hasKey("displayNotification") ? cmp.getBoolean("displayNotification") : true;
		closeOnFinish = cmp.hasKey("closeOnFinish") ? cmp.getBoolean("closeOnFinish") : false;
		pinnedMsg = cmp.hasKey("pinnnedMsg") ? cmp.getString("pinnnedMsg") : "";
		formatMessage = cmp.hasKey("formatMessage") ? cmp.getBoolean("formatMessage") : true;
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

	public static void setDisplayNotification(boolean b) {
		displayNotification = b;
		NBTTagCompound cmp = IOUtils.getTagCompoundInFile(cacheFile);
		cmp.setBoolean("displayNotification", b);
		IOUtils.injectNBTToFile(cmp, cacheFile);
	}
	
	public static void setFormatMessage(boolean b) {
		formatMessage = b;
		NBTTagCompound cmp = IOUtils.getTagCompoundInFile(cacheFile);
		cmp.setBoolean("formatMessage", b);
		IOUtils.injectNBTToFile(cmp, cacheFile);
	}
	
	public static void registerOpenWebsite(String s) {
		NBTTagCompound cmp = IOUtils.getTagCompoundInFile(cacheFile);
		NBTTagCompound subCmp = !cmp.hasKey("websites") ? new NBTTagCompound() : cmp.getCompoundTag("websites");
		int visits = subCmp.hasKey(s) ? subCmp.getInteger(s) : 0;
		subCmp.setInteger(s, visits+1);
		cmp.setCompoundTag("websites", subCmp);
		IOUtils.injectNBTToFile(cmp, cacheFile);
	}
	
	public static int getWebsiteViews(String s) {
		NBTTagCompound cmp = IOUtils.getTagCompoundInFile(cacheFile);
		if(!cmp.hasKey("websites"))
			return 0;
		NBTTagCompound subCmp = cmp.getCompoundTag("websites");
		return !subCmp.hasKey(s) ? 0 : subCmp.getInteger(s);
	}
}
