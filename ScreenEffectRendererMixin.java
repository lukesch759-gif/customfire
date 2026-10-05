package de.customfire.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.customfire.FireConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Vanilla (1.21.11) zeichnet das Feuer im Bild so:
 *   renderFire(PoseStack, MultiBufferSource, TextureAtlasSprite)
 *     -> poseStack.translate(+-0.24, -0.3, 0)
 *     -> 4 Ecken mit setColor(1, 1, 1, 0.9)
 * Hier ändern wir: an/aus, die -0.3 (Höhe), die Farbe/Deckkraft und die Textur.
 */
@Mixin(ScreenEffectRenderer.class)
public abstract class ScreenEffectRendererMixin {

	@Shadow @Final private MaterialSet materials;

	@Unique private static Material customfire$soulFire;
	@Unique private static Material customfire$colorFire;

	/** Feuer komplett aus. */
	@Inject(method = "renderFire", at = @At("HEAD"), cancellable = true)
	private static void customfire$hide(PoseStack poseStack, MultiBufferSource buffers, TextureAtlasSprite sprite, CallbackInfo ci) {
		FireConfig cfg = FireConfig.get();
		if (!cfg.enabled || cfg.height <= 0.001 || cfg.opacity <= 0.001) {
			ci.cancel();
		}
	}

	/** Höhe: Vanilla verschiebt um -0.3. 100% = -0.3 (normal), 0% = -1.5 (ganz weg). */
	@ModifyArg(method = "renderFire",
			at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"),
			index = 1)
	private static float customfire$height(float y) {
		double h = FireConfig.get().height;
		return y - (float) ((1.0 - h) * 1.2);
	}

	/** Farbe + Deckkraft. */
	@Redirect(method = "renderFire",
			at = @At(value = "INVOKE",
					target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setColor(FFFF)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
	private static VertexConsumer customfire$color(VertexConsumer consumer, float r, float g, float b, float a) {
		FireConfig cfg = FireConfig.get();
		return consumer.setColor(
				r * cfg.red / 255f,
				g * cfg.green / 255f,
				b * cfg.blue / 255f,
				a * (float) cfg.opacity);
	}

	/** Textur tauschen: normal / Seelenfeuer / eigene graue (einfärbbare) Flammen. */
	@ModifyArg(method = "renderScreenEffect",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/ScreenEffectRenderer;renderFire(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V"),
			index = 2)
	private TextureAtlasSprite customfire$texture(TextureAtlasSprite original) {
		int tex = FireConfig.get().texture;
		if (tex == FireConfig.TEX_NORMAL) {
			return original;
		}
		Material fire = ModelBakery.FIRE_1;
		if (customfire$soulFire == null) {
			// fire.texture() ist "minecraft:block/fire_1"
			String path = fire.texture().getPath();
			customfire$soulFire = new Material(fire.atlasLocation(),
					Identifier.withDefaultNamespace(path.replace("fire_1", "soul_fire_1")));
			customfire$colorFire = new Material(fire.atlasLocation(),
					Identifier.fromNamespaceAndPath("customfire", path.replace("fire_1", "fire_gray")));
		}
		try {
			return this.materials.get(tex == FireConfig.TEX_SOUL ? customfire$soulFire : customfire$colorFire);
		} catch (Exception e) {
			return original;
		}
	}
}
