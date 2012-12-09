package vazkii.tukmc;

import java.util.Scanner;

import vazkii.codebase.common.CommonUtils;
import vazkii.codebase.common.VazcoreReference;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;

public class ConsoleDebug implements Runnable {

	public static ConsoleDebug instance;

	protected ConsoleDebug() {
		Thread thread = new Thread(this, "Console Debug Thread");
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	public void run() {
		Scanner scanner = new Scanner(System.in);
		while (CommonUtils.getMc().running) {
			String s = scanner.nextLine();
			if (CommonUtils.getMc().thePlayer != null) {
				String replaced = s.replace('&', VazcoreReference.FORMATTING_CODE_CHAR);
				if (!MinecraftForge.EVENT_BUS.post(new ClientChatReceivedEvent(replaced))) CommonUtils.getMc().thePlayer.addChatMessage(replaced);
			}
		}
	}

}
