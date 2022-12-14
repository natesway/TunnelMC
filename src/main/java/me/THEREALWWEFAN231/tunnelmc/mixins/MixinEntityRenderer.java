package me.THEREALWWEFAN231.tunnelmc.mixins;

import me.THEREALWWEFAN231.tunnelmc.mixins.interfaces.IMixinTextRenderer;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {

	@Shadow public abstract TextRenderer getTextRenderer();

	@Redirect(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I"))
	public int renderLabelIfPresent(TextRenderer instance, Text text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, boolean seeThrough, int backgroundColor, int light) {
		TextRenderer textRenderer = getTextRenderer();
		Function<Integer, Float> getY = (i) -> y - i * (textRenderer.fontHeight + 1);
		Function<String, Float> getX = (str) -> (float)-textRenderer.getWidth(str) / 2;

		List<String> lines = new ArrayList<>(List.of(text.getString().split("\n")));
		Collections.reverse(lines);

		int lastDraw = (int) x;
		for(int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			lastDraw = textRenderer.draw(line, getX.apply(line), getY.apply(i), color, shadow, matrix, vertexConsumers, seeThrough, 0, light);
		}

		if (backgroundColor != 0) { // a hack to change background width and height
			String widestLine = lines.stream()
					.max(Comparator.comparingDouble(textRenderer.getTextHandler()::getWidth)).get();

			float alpha = (backgroundColor >> 24 & 255) / 255.0F;
			float red = (backgroundColor >> 16 & 255) / 255.0F;
			float green = (backgroundColor >> 8 & 255) / 255.0F;
			float blue = (backgroundColor & 255) / 255.0F;
			GlyphRenderer.Rectangle rectangle = new GlyphRenderer.Rectangle(
					getX.apply(widestLine) - 1.0F,
					y + 9.0F,
					textRenderer.getTextHandler().getWidth(widestLine) / 2 + 1.0F,
					getY.apply(lines.size() - 1) - 1.0F,
					0.01F, red, green, blue, alpha);

			FontStorage fontStorage = ((IMixinTextRenderer) textRenderer).getFontStorageAccessor()
					.apply(text.getStyle().getFont());
			GlyphRenderer glyphRenderer = fontStorage.getRectangleRenderer();
			VertexConsumer vertexConsumer = vertexConsumers.getBuffer(glyphRenderer.getLayer(
					seeThrough ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL));

			glyphRenderer.drawRectangle(rectangle, matrix, vertexConsumer, light);
		}

        return lastDraw;
	}
}