package io.github.startsmercury.cr_tooltips_payak.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import finalforeach.cosmicreach.ui.actions.FollowMouseAction;
import io.github.startsmercury.cr_tooltips_payak.impl.client.FollowMouseActionExt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FollowMouseAction.class)
public class FollowMouseActionMixin implements FollowMouseActionExt {
    @ModifyExpressionValue(
        method = "act(F)Z",
        at = @At(value = "FIELD", target = "Lcom/badlogic/gdx/math/Vector2;x:F", ordinal = 0)
    )
    private float transformX(final float original) {
        return this.cr_tooltips_payak$transformX(original);
    }

    @ModifyExpressionValue(
        method = "act(F)Z",
        at = @At(value = "FIELD", target = "Lcom/badlogic/gdx/math/Vector2;y:F", ordinal = 0)
    )
    private float transformY(final float original) {
        return this.cr_tooltips_payak$transformY(original);
    }
}
