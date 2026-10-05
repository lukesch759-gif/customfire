package de.customfire;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Alle Einstellungen. Wird in .minecraft/config/customfire.json gespeichert. */
public class FireConfig {
	/** 0 = normales Feuer, 1 = Seelenfeuer (blau), 2 = eigene graue Flammen (voll einfärbbar) */
	public static final int TEX_NORMAL = 0;
	public static final int TEX_SOUL = 1;
	public static final int TEX_COLOR = 2;

	public boolean enabled = true;
	/** 0.0 = ganz unten (unsichtbar) ... 1.0 = normale Minecraft-Höhe */
	public double height = 0.5;
	/** 0.0 = durchsichtig ... 1.0 = normal */
	public double opacity = 1.0;
	public int red = 255;
	public int green = 255;
	public int blue = 255;
	public int texture = TEX_NORMAL;

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static FireConfig instance = new FireConfig();

	public static FireConfig get() {
		return instance;
	}

	private static Path file() {
		return FabricLoader.getInstance().getConfigDir().resolve("customfire.json");
	}

	public static void load() {
		Path path = file();
		if (Files.exists(path)) {
			try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
				FireConfig loaded = GSON.fromJson(reader, FireConfig.class);
				if (loaded != null) {
					instance = loaded;
				}
			} catch (Exception e) {
				CustomFireClient.LOGGER.warn("Konnte customfire.json nicht lesen, nutze Standard", e);
			}
		}
		instance.clamp();
	}

	public static void save() {
		instance.clamp();
		try {
			Files.createDirectories(file().getParent());
			try (Writer writer = Files.newBufferedWriter(file(), StandardCharsets.UTF_8)) {
				GSON.toJson(instance, writer);
			}
		} catch (Exception e) {
			CustomFireClient.LOGGER.warn("Konnte customfire.json nicht speichern", e);
		}
	}

	public void reset() {
		enabled = true;
		height = 0.5;
		opacity = 1.0;
		red = green = blue = 255;
		texture = TEX_NORMAL;
	}

	private void clamp() {
		height = Math.max(0.0, Math.min(1.0, height));
		opacity = Math.max(0.0, Math.min(1.0, opacity));
		red = Math.max(0, Math.min(255, red));
		green = Math.max(0, Math.min(255, green));
		blue = Math.max(0, Math.min(255, blue));
		if (texture < TEX_NORMAL || texture > TEX_COLOR) {
			texture = TEX_NORMAL;
		}
	}
}
