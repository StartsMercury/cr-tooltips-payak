package io.github.startsmercury.cr_tooltips_payak.mixin.client;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.llamalad7.mixinextras.sugar.Local;
import finalforeach.cosmicreach.gamestates.GameState;
import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.items.screens.ItemStorageScreen;
import finalforeach.cosmicreach.ui.FontRenderer;
import finalforeach.cosmicreach.ui.GameStyles;
import finalforeach.cosmicreach.ui.UI;
import finalforeach.cosmicreach.ui.widgets.CreativeCatalogWidget;
import finalforeach.cosmicreach.ui.widgets.ItemStackWidget;
import io.github.startsmercury.cr_tooltips_payak.impl.client.InGameExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin class.
 *
 * @see Mixin
 */
@Mixin(InGame.class)
public class InGameMixin extends GameState implements InGameExtension {
    @Unique
    private static final float TOOLTIP_FADE_SECONDS = 0.75F;

    @Unique
    private static final float TOOLTIP_VISIBLE_SECONDS = 1.25F;

    @Shadow
    public ItemStorageScreen hotbarScreen;

    @Unique
    private String lastTooltipName = "";

    @Shadow
    private Vector2 tmpVec;

    @Shadow
    public CreativeCatalogWidget itemCatalogWidget;

    @Unique
    private float tooltipRemainingSeconds;

    @Override
    @Unique
    public void cr_tooltips_payak$commitTooltip(final String text) {
        this.lastTooltipName = text;
        this.tooltipRemainingSeconds = InGameMixin.TOOLTIP_VISIBLE_SECONDS;
    }

    @Inject(
        method = "render()V",
        at = @At(
            value = "INVOKE",
            target = """
                Lfinalforeach/cosmicreach/ui/widgets/ContainerSlotWidget; \
                drawItemCountWithDropShadow(                              \
                    Lcom/badlogic/gdx/graphics/g2d/Batch;                 \
                    F                                                     \
                    F                                                     \
                    Lcom/badlogic/gdx/graphics/Color;                     \
                ) V                                                       \
            """
        )
    )
    private void drawItemCatalogTooltip(final CallbackInfo callback) {
        final var stageBatch = this.stage.getBatch();

        final var table = (Group) this.itemCatalogWidget.getChild(1);
        final var children = table.getChildren();
        final var items = children.items;

        for (int i = 0, end = children.size - 1; i < end; i++) {
            ((ItemStackWidget) items[i]).drawTooltip(stageBatch);
        }
    }

    @Inject(
        method = "render()V",
        at = @At(
            value = "FIELD",
            target = """
                Lfinalforeach/cosmicreach/gamestates/InGame;                                \
                inventoryScreen: Lfinalforeach/cosmicreach/items/screens/ItemStorageScreen; \
            """,
            ordinal = 0
        )
    )
    private void drawHotbarItemTooltip(final CallbackInfo callback) {
        if (!UI.renderUI) {
            return;
        }

        float worldHeight = uiViewport.getWorldHeight();

        var xStart = 0.0F;
        var yStart = worldHeight / 2.0F - 1.5F * hotbarScreen.getActor().getHeight();

        FontRenderer.getTextDimensions(this.uiViewport, this.lastTooltipName, this.tmpVec);
        xStart -= this.tmpVec.x / 2.0F;
        yStart -= this.tmpVec.y;

        final var opacity = MathUtils.clamp(
            tooltipRemainingSeconds / InGameMixin.TOOLTIP_FADE_SECONDS,
            0.0F,
            1.0F
        );

        GameState.batch.begin();

        {
            final var color = GameStyles.containerBackground9Patch.getColor();
            final var oldOpacity = color.a;
            color.a = opacity;
            GameStyles.containerBackground9Patch.draw(
                GameState.batch,
                xStart - 4.0F,
                yStart - 4.0F,
                0.0F,
                0.0F,
                this.tmpVec.x + 8.0F,
                this.tmpVec.y + 8.0F,
                1.0F,
                1.0F,
                0.0F
            );
            color.a = oldOpacity;
        }

        {
            final var color = GameState.batch.getColor();
            final var oldOpacity = color.a;
            color.a = opacity;
            GameState.batch.setColor(color);
            FontRenderer.drawText(
                GameState.batch,
                this.uiViewport,
                this.lastTooltipName,
                xStart,
                yStart,
                false
            );
            color.a = oldOpacity;
            GameState.batch.setColor(color);
        }

        GameState.batch.end();
    }

    @Override
    public void cr_tooltips_payak$hideTooltip() {
        this.tooltipRemainingSeconds = Math.min(0.0F, this.tooltipRemainingSeconds);
    }

    @Inject(
        method = "update(F)V",
        at = @At(
            value = "INVOKE",
            target = "Lfinalforeach/cosmicreach/gamestates/InGame;updateHotbarColor(F)V",
            ordinal = 0
        )
    )
    private void updateTooltipData(
        final CallbackInfo callback,
        final @Local(ordinal = 0, argsOnly = true) float delta
    ) {
        this.tooltipRemainingSeconds -= delta;
    }
}
