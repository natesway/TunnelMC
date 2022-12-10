package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators;

import com.nukkitx.protocol.bedrock.packet.TextPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;

@PacketIdentifier(TextPacket.class)
public class TextTranslator extends PacketTranslator<TextPacket> {

	@Override
	public void translate(TextPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		switch (packet.getType()) {
			default: {
				System.out.println("Falling back to raw translation for " + packet);
			}
			case RAW: {
				GameMessageS2CPacket gameMessageS2CPacket = new GameMessageS2CPacket(Text.of(packet.getMessage()), false);
				javaConnection.processJavaPacket(gameMessageS2CPacket);
				break;
			}
			case CHAT: {
				String formattedChatMessage = packet.getMessage();
				if (packet.getSourceName().length() > 0) {
					formattedChatMessage = "<" + packet.getSourceName() + "> " + formattedChatMessage;
				}

				GameMessageS2CPacket gameMessageS2CPacket = new GameMessageS2CPacket(Text.of(formattedChatMessage), false);
				javaConnection.processJavaPacket(gameMessageS2CPacket);
				break;
			}
			case TRANSLATION: {
				GameMessageS2CPacket gameMessageS2CPacket = new GameMessageS2CPacket(Text.translatable(packet.getMessage().replaceAll("%", ""), packet.getParameters()), false);
				javaConnection.processJavaPacket(gameMessageS2CPacket);
				break;
			}
		}
	}
}