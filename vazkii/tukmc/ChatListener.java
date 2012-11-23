package vazkii.tukmc;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.ForgeSubscribe;

public class ChatListener {

	@ForgeSubscribe
	public void onChatMsgRecieved(ClientChatReceivedEvent event) {
		TickHandler.addMsg();
	}
	
}
