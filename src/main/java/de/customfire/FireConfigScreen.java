package de.customfire;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;

/** Das Menü, das mit Rechts-Shift aufgeht. */
public class FireConfigScreen extends Screen {
	private final Screen parent;

	private static final String[] TEXTURE_NAMES = {"Normal", "Seelenfeuer", "Bunt (einfärbbar)"};

	/** Name, Textur, Rot, Grün, Blau */
	private static final Object[][] PRESETS = {
			{"Original", FireConfig.TEX_NORMAL, 255, 255, 255},
			{"Rot", FireConfig.TEX_COLOR, 255, 40, 30},
			{"Orange", FireConfig.TEX_COLOR, 255, 140, 20},
			{"Gelb", FireConfig.TEX_COLOR, 255, 230, 40},
			{"Grün", FireConfig.TEX_COLOR, 60, 255, 60},
			{"Seelenfeuer", FireConfig.TEX_SOUL, 255, 255, 255},
			{"Blau", FireConfig.TEX_COLOR, 40, 110, 255},
			{"Lila", FireConfig.TEX_COLOR, 170, 60, 255},
			{"Pink", FireConfig.TEX_COLOR, 255, 90, 200},
			{"Weiß", FireConfig.TEX_COLOR, 255, 255, 255},
	};

	private int swatchX, swatchY;

	public FireConfigScreen(Screen parent) {
		super(Component.literal("Custom Fire – Einstellungen"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		FireConfig cfg = FireConfig.get();
		int cx = this.width / 2;
		int left = cx - 155;
		int right = cx + 5;
		int y = 34;
		int row = 24;

		// ---- linke Spalte: Feuer ----
		addRenderableWidget(Button.builder(enabledText(), b -> {
			cfg.enabled = !cfg.enabled;
			b.setMessage(enabledText());
		}).bounds(left, y, 150, 20).build());

		addRenderableWidget(new Slider(left, y + row, 150, cfg.height,
				v -> "Höhe: " + Math.round(v * 100) + "%",
				v -> cfg.height = v));

		addRenderableWidget(new Slider(left, y + row * 2, 150, cfg.opacity,
				v -> "Deckkraft: " + Math.round(v * 100) + "%",
				v -> cfg.opacity = v));

		addRenderableWidget(Button.builder(textureText(), b -> {
			cfg.texture = (cfg.texture + 1) % 3;
			b.setMessage(textureText());
		}).bounds(left, y + row * 3, 150, 20).build());

		// ---- rechte Spalte: Farbe ----
		addRenderableWidget(new Slider(right, y, 150, cfg.red / 255.0,
				v -> "Rot: " + Math.round(v * 255),
				v -> cfg.red = (int) Math.round(v * 255)));
		addRenderableWidget(new Slider(right, y + row, 150, cfg.green / 255.0,
				v -> "Grün: " + Math.round(v * 255),
				v -> cfg.green = (int) Math.round(v * 255)));
		addRenderableWidget(new Slider(right, y + row * 2, 150, cfg.blue / 255.0,
				v -> "Blau: " + Math.round(v * 255),
				v -> cfg.blue = (int) Math.round(v * 255)));
		swatchX = right;
		swatchY = y + row * 3;

		// ---- Farb-Vorlagen: 2 Reihen à 5 ----
		int presetY = y + row * 4 + 8;
		int pw = 60;
		for (int i = 0; i < PRESETS.length; i++) {
			Object[] p = PRESETS[i];
			int px = left + (i % 5) * (pw + 2);
			int py = presetY + (i / 5) * 22;
			addRenderableWidget(Button.builder(Component.literal((String) p[0]), b -> {
				cfg.texture = (int) p[1];
				cfg.red = (int) p[2];
				cfg.green = (int) p[3];
				cfg.blue = (int) p[4];
				this.rebuildWidgets();
			}).bounds(px, py, pw, 20).build());
		}

		// ---- unten ----
		int bottomY = presetY + 22 * 2 + 10;
		addRenderableWidget(Button.builder(Component.literal("Zurücksetzen"), b -> {
			cfg.reset();
			this.rebuildWidgets();
		}).bounds(left, bottomY, 150, 20).build());
		addRenderableWidget(Button.builder(Component.literal("Fertig"), b -> this.onClose())
				.bounds(right, bottomY, 150, 20).build());
	}

	private static Component enabledText() {
		return Component.literal("Feuer-Anzeige: " + (FireConfig.get().enabled ? "§aAN" : "§cAUS"));
	}

	private static Component textureText() {
		return Component.literal("Textur: " + TEXTURE_NAMES[FireConfig.get().texture]);
	}

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
		super.render(g, mouseX, mouseY, delta);
		g.drawCenteredString(this.font, this.title, this.width / 2, 14, 0xFFFFFFFF);

		// Farbvorschau
		FireConfig cfg = FireConfig.get();
		int color = 0xFF000000 | (cfg.red << 16) | (cfg.green << 8) | cfg.blue;
		g.fill(swatchX, swatchY, swatchX + 150, swatchY + 20, 0xFF000000);
		g.fill(swatchX + 1, swatchY + 1, swatchX + 149, swatchY + 19, color);
		g.drawCenteredString(this.font, Component.literal("Farbe"), swatchX + 75, swatchY + 6,
				(cfg.red + cfg.green + cfg.blue) > 450 ? 0xFF000000 : 0xFFFFFFFF);
	}

	@Override
	public void onClose() {
		FireConfig.save();
		this.minecraft.setScreen(this.parent);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	/** Einfacher Schieberegler 0..1 */
	private static class Slider extends AbstractSliderButton {
		private final DoubleFunction<String> label;
		private final DoubleConsumer onChange;

		Slider(int x, int y, int w, double value, DoubleFunction<String> label, DoubleConsumer onChange) {
			super(x, y, w, 20, Component.literal(label.apply(value)), value);
			this.label = label;
			this.onChange = onChange;
		}

		@Override
		protected void updateMessage() {
			this.setMessage(Component.literal(label.apply(this.value)));
		}

		@Override
		protected void applyValue() {
			onChange.accept(this.value);
		}
	}
}
