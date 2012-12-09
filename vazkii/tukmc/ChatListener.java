package vazkii.tukmc;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;

import vazkii.codebase.common.ColorCode;
import vazkii.codebase.common.CommonUtils;
import vazkii.codebase.common.FormattingCode;
import vazkii.tukmc.McMMOIntegration.LevelUpData;
import vazkii.tukmc.McMMOIntegration.SkillData;

import net.minecraft.src.SoundManager;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

public class ChatListener {

	@ForgeSubscribe
	public void onChatMsgRecieved(ClientChatReceivedEvent event) {
		SoundManager snd = CommonUtils.getMc().sndManager;

		if (!(event instanceof ChatRecievedEventNoReact) && Config.get(Config.NODE_MCMMO)) {
			if (McMMOIntegration.passMessage(event.message)) {
				event.setCanceled(true);
				if (snd != null && mod_TukMC.displayNotification) snd.playSoundFX("random.orb", 1F, 1F);
				return;
			}
			LevelUpData levelData = LevelUpData.fromString(event.message);
			if (levelData != null) {
				McMMOIntegration.setLevelUpData(levelData);
				if (snd != null && mod_TukMC.displayNotification && levelData.getLevel() % 5 == 0) snd.playSoundFX("random.levelup", 3F, 1F);
				event.setCanceled(true);
				return;
			}
			SkillData skillData = SkillData.fromString(event.message);
			if (skillData != null) {
				McMMOIntegration.addSkillData(skillData);
				if (snd != null && mod_TukMC.displayNotification) snd.playSoundFX("random.orb", 1F, 1F);
				event.setCanceled(true);
				return;
			}
		}

		if (!Config.get(Config.NODE_COLORBLIND_MODE) && !(event instanceof ChatRecievedEventNoReact)) {
			String[] msg = event.message.split(" ");
			String[] cmFailsafe = new String[msg.length]; // ConcurrentModificationException
			// failsafe
			String newMsg = "";
			for (int i = 0; i < msg.length; i++) {
				String s = msg[i];
				if (getURI(s) != null) cmFailsafe[i] = ColorCode.AQUA + s + FormattingCode.RESET;
				else cmFailsafe[i] = s;
			}

			for (int i = 0; i < cmFailsafe.length; i++) {
				String s = cmFailsafe[i];
				newMsg = newMsg.concat(s + (i == cmFailsafe.length ? "" : " "));
			}
			event.setCanceled(true);

			if (!MinecraftForge.EVENT_BUS.post(new ChatRecievedEventNoReact(newMsg))) CommonUtils.getMc().ingameGUI.getChatGUI().printChatMessage(newMsg);
		}

		TickHandler.addMsg();
		if (snd != null && mod_TukMC.displayNotification) snd.playSoundFX("random.orb", 1F, 1F);
	}

	public static class ChatRecievedEventNoReact extends ClientChatReceivedEvent {

		public ChatRecievedEventNoReact(String message) {
			super(message);
		}

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
