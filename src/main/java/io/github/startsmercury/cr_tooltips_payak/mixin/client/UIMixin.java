package io.github.startsmercury.cr_tooltips_payak.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import finalforeach.cosmicreach.gamestates.GameState;
import finalforeach.cosmicreach.ui.Hotbar;
import finalforeach.cosmicreach.ui.UI;
import io.github.startsmercury.cr_tooltips_payak.impl.client.InGameExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(UI.class)
public class UIMixin {
    @Shadow
    public static Hotbar hotbar;

    @Inject(
        method = "render()V",
        at = {
            @At(value = "INVOKE", target = "Lfinalforeach/cosmicreach/ui/Hotbar;scrolled(FF)Z"),
            @At(
                value = "INVOKE",
                target = "Lfinalforeach/cosmicreach/ui/Hotbar;cycleSwapGroupItem()V"
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
                inGame.cr_tooltips_payak$commitTooltip(name);

                return;
            }
        }

        // Hide tooltip: usually when switching to an empty slot
        inGame.cr_tooltips_payak$hideTooltip();
    }
}
