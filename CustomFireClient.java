package de.customfire;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomFireClient implements ClientModInitializer {
	public static final String MOD_ID = "customfire";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static KeyMapping openKey;

	@Override
	public void onInitializeClient() {
		FireConfig.load();

		KeyMapping.Category category = KeyMapping.Category.register(
				Identifier.fromNamespaceAndPath(MOD_ID, "main"));

		// Standard: Rechts-Shift. Kann in Optionen > Steuerung geändert werden.
		openKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.customfire.open",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_RIGHT_SHIFT,
				category));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openKey.consumeClick()) {
				if (client.screen == null) {
					client.setScreen(new FireConfigScreen(null));
				}
			}
		});

		LOGGER.info("Custom Fire geladen - Rechts-Shift öffnet das Menü");
	}
}
