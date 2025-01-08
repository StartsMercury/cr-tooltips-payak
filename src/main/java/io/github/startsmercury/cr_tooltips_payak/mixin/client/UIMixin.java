package io.github.startsmercury.cr_tooltips_payak.mixin.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import finalforeach.cosmicreach.gamestates.GameState;
import finalforeach.cosmicreach.items.Hotbar;
import finalforeach.cosmicreach.ui.FontRenderer;
import finalforeach.cosmicreach.ui.GameStyles;
import finalforeach.cosmicreach.ui.UI;
import io.github.startsmercury.cr_tooltips_payak.impl.client.InGameExtension;
import io.github.startsmercury.cr_tooltips_payak.impl.client.ItemCatalogExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(UI.class)
public class UIMixin {
    @Unique
    private static final float TOOLTIP_PADDING = 4.0F;

    @Unique
    private static final float TOOLTIP_PADDING_2X = 2.0F * TOOLTIP_PADDING;

    @Unique
    private final Vector2 tmpVec = new Vector2();

    @Shadow
    private Viewport uiViewport;

    @Shadow
    public static Hotbar hotbar;

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            shift = At.Shift.AFTER,
            target = "Lcom/badlogic/gdx/utils/viewport/Viewport;apply(Z)V",
            ordinal = 1
        )
    )
    private void drawItemCatalogTooltip(final CallbackInfo callback) {
        if (!UI.itemCatalog.isShown()) {
            return;
        }

        final var name = ((ItemCatalogExtension) UI.itemCatalog).getHoveredItemStackName();
        if (name == null) {
            return;
        }

        // Result overrides `tmpVec`
        FontRenderer.getTextDimensions(this.uiViewport, name, this.tmpVec);
        final var textW = this.tmpVec.x;
        final var textH = this.tmpVec.y;

        // Tooltip text starting position
        final float textX;
        final float textY;
        {
            // Input Screen Coordinates (usually mouse cursor)
            this.tmpVec.set(Gdx.input.getX(), Gdx.input.getY());

            // Screen -> World
            this.uiViewport.unproject(tmpVec);

            var x = this.tmpVec.x;
            var y = this.tmpVec.y;

            final var minY = -this.uiViewport.getWorldHeight() / 2.0F;
            final var maxX = this.uiViewport.getWorldWidth() / 2.0F;

            final var pastNorth = y - textH - TOOLTIP_PADDING < minY;
            final var pastEast = x + textW + TOOLTIP_PADDING > maxX;

            if (pastNorth || pastEast) {
                // Grow tooltip eastwards if either up or right will fit
                x -= textW - TOOLTIP_PADDING;
            }

            if (pastEast) {
                // Smooth clamp westward when cursor exits eastwards
                x = Math.min(x, maxX - textW - TOOLTIP_PADDING);
            }

            if (pastNorth) {
                // Smooth clamp downward when cursor exits upwards
                y = Math.max(y, minY + TOOLTIP_PADDING);
            } else {
                // Grow upwards if it can fit
                y -= textH;
            }

            textX = x;
            textY = y;
        }

        // Enable translucency(?): without background and text appear as a black rectangle
        Gdx.gl.glActiveTexture(33984);

        // Make it viewable: without draws are not visible
        UI.batch.setProjectionMatrix(this.uiViewport.getCamera().combined);

        // Tooltip Drawing
        UI.batch.begin();
        GameStyles.containerBackground9Patch.draw(
            UI.batch,
            textX - TOOLTIP_PADDING,
            textY - TOOLTIP_PADDING,
            0.0F,
            0.0F,
            textW + TOOLTIP_PADDING_2X,
            textH + TOOLTIP_PADDING_2X,
            1.0F,
            1.0F,
            0.0F
        );
        FontRenderer.drawText(UI.batch, this.uiViewport, name, textX, textY, false);
        UI.batch.end();
    }

    @Inject(
        method = "render()V",
        at = {
            @At(value = "INVOKE", target = "Lfinalforeach/cosmicreach/items/Hotbar;scrolled(FF)Z"),
            @At(
                value = "INVOKE",
                target = "Lfinalforeach/cosmicreach/items/Hotbar;cycleSwapGroupItem()V"
            ),
        }
    )
    private void detectTooltipUpdate(
        final CallbackInfo callback,
        final @Share("changed") LocalBooleanRef changedRef
    ) {
        changedRef.set(true);
    }

    @Inject(
        method = "render()V",
        at = @At(
            value = "INVOKE",
            ordinal = 0,
            target = "Lcom/badlogic/gdx/graphics/GL20;glClear(I)V"
        )
    )
    private void commitTooltipUpdateOnRender(
        final CallbackInfo callback,
        final @Share("changed") LocalBooleanRef changedRef
    ) {
        commitTooltip(changedRef.get());
    }

    @ModifyReturnValue(method = { "keyDown(I)Z", "scrolled(FF)Z" }, at = @At("RETURN"))
    private boolean commitTooltipUpdateOnInput(final boolean original) {
        commitTooltip(original);
        return original;
    }

    @Unique
    private void commitTooltip(final boolean changed) {
        if (!changed || !(GameState.currentGameState instanceof final InGameExtension inGame)) {
            return;
        }

        final var itemStack = hotbar.getSelectedItemStack();

        if (itemStack != null) {
            final var name = itemStack.getName();

            if (name != null) {
                inGame.commitTooltip(name);

                return;
            }
        }

        // Hide tooltip: usually when switching to an empty slot
        inGame.hideTooltip();
    }
}
