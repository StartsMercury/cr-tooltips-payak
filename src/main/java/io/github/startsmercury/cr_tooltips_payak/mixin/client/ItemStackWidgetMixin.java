package io.github.startsmercury.cr_tooltips_payak.mixin.client;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import finalforeach.cosmicreach.ui.widgets.ItemStackWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStackWidget.class)
public abstract class ItemStackWidgetMixin extends Stack {
    @Unique
    private static final float TOOLTIP_PADDING = 4.0F;

    @Shadow
    @Final
    private static Vector2 tmpVec;

    @Inject(
        method = "drawTooltip(Lcom/badlogic/gdx/graphics/g2d/Batch;)V",
        at = @At(
            value = "INVOKE",
            shift = At.Shift.AFTER,
            target = """
                Lfinalforeach/cosmicreach/ui/FontRenderer;     \
                getTextDimensions(                             \
                    Lcom/badlogic/gdx/utils/viewport/Viewport; \
                    Ljava/lang/String;                         \
                    Lcom/badlogic/gdx/math/Vector2;            \
                ) Lcom/badlogic/gdx/math/Vector2;              \
            """
        )
    )
    private void preventTooltipOverflow(
        final CallbackInfo callback,
        final @Local(ordinal = 0) LocalFloatRef textX,
        final @Local(ordinal = 1) LocalFloatRef textY
    ) {
        final var viewport = this.getStage().getViewport();

        final var textW = tmpVec.x;
        final var textH = tmpVec.y;

        var x = textX.get();
        var y = textY.get();

        final var maxY = viewport.getWorldHeight();
        final var maxX = viewport.getWorldWidth();

        // Smooth clamp westward when cursor exits eastwards
        x = Math.min(x, maxX - textW - TOOLTIP_PADDING);

        // Smooth clamp downward when cursor exits upwards
        y = Math.min(y, maxY - textH - TOOLTIP_PADDING);

        textX.set(x);
        textY.set(y);
    }
}
