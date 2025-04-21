package io.github.startsmercury.cr_tooltips_payak.impl.client;

import finalforeach.cosmicreach.ui.actions.FollowMouseAction;

public class FollowMouseActionImpl extends FollowMouseAction implements FollowMouseActionExt {
    @Override
    public float cr_tooltips_payak$transformX(final float x) {
        final var actor = this.actor;
        final var parent = actor.getParent();

        return clamp(x - 4.0f, parent.getX(), parent.getWidth() - actor.getWidth());
    }

    @Override
    public float cr_tooltips_payak$transformY(final float y) {
        final var actor = this.actor;
        final var parent = actor.getParent();

        return clamp(y - 4.0f, parent.getY(), parent.getHeight() - actor.getHeight());
    }

    /** A clamp min first. */
    private static float clamp(final float value, final float min, final float max) {
        return Math.min(Math.max(value, min), max);
    }
}
