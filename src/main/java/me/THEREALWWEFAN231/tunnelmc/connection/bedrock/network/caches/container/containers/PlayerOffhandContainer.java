package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.containers;

import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.BedrockContainer;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.BedrockContainers;

public class PlayerOffhandContainer extends BedrockContainer {

	public static final int SIZE = 1;

	public PlayerOffhandContainer() {
		super(PlayerOffhandContainer.SIZE, BedrockContainers.PLAYER_OFFHAND_COTNAINER_ID);
	}

}