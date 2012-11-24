package vazkii.tukmc;

import vazkii.codebase.common.CommonUtils;

import net.minecraft.src.SoundManager;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.ForgeSubscribe;

public class ChatListener {

	@ForgeSubscribe
	public void onChatMsgRecieved(ClientChatReceivedEvent event) {
		TickHandler.addMsg();
		SoundManager snd = CommonUtils.getMc().sndManager;
		if (snd != null && mod_TukMC.displayNotification) snd.playSoundFX("random.orb", 1F, 1F);
	}

}
