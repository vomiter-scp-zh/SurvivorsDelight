package com.vomiter.survivorsdelight.client.tint;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public final class TintingVertexConsumer implements VertexConsumer {
    private final VertexConsumer delegate;
    private final int tr, tg, tb, ta;
    private final float fr, fg, fb; // 0..1

    public TintingVertexConsumer(VertexConsumer delegate, int r, int g, int b, int a) {
        this.delegate = delegate;
        this.tr = r; this.tg = g; this.tb = b; this.ta = a;
        this.fr = r / 255f; this.fg = g / 255f; this.fb = b / 255f;
    }

    @Override
    public void putBulkData(PoseStack.Pose pose, BakedQuad quad,
                            float[] brightness, float r, float g, float b,
                            int[] lights, int overlay, boolean useShade) {
        delegate.putBulkData(pose, quad, brightness, r * fr, g * fg, b * fb, lights, overlay, useShade);
    }

    @Override
    public void putBulkData(PoseStack.Pose pose, BakedQuad quad,
                            float r, float g, float b, int light, int overlay) {
        delegate.putBulkData(pose, quad, r * fr, g * fg, b * fb, light, overlay);
    }

    @Override
    public VertexConsumer color(int r, int g, int b, int a) {
        int nr = (r * tr) / 255;
        int ng = (g * tg) / 255;
        int nb = (b * tb) / 255;
        int na = (a * ta) / 255;
        return delegate.color(nr, ng, nb, na);
    }

    @Override
    public VertexConsumer vertex(Matrix4f matrix, float x, float y, float z) { return delegate.vertex(matrix, x, y, z); }

    @Override
    public VertexConsumer vertex(double x, double y, double z) { return delegate.vertex(x, y, z); }

    @Override
    public VertexConsumer uv(float u, float v) { return delegate.uv(u, v); }

    @Override
    public VertexConsumer overlayCoords(int u, int v) {
        return delegate.overlayCoords(u, v);
    }

    @Override
    public VertexConsumer uv2(int u, int v) {
        return delegate.uv2(u, v);
    }

    @Override
    public VertexConsumer normal(float nx, float ny, float nz) {
        return delegate.normal(nx, ny, nz);
    }

    @Override
    public VertexConsumer overlayCoords(int overlay) { return delegate.overlayCoords(overlay); }

    @Override
    public VertexConsumer uv2(int light) { return delegate.uv2(light); }

    @Override
    public VertexConsumer normal(Matrix3f normal, float nx, float ny, float nz) { return delegate.normal(normal, nx, ny, nz); }

    @Override
    public void endVertex() { delegate.endVertex(); }

    @Override
    public void defaultColor(int r, int g, int b, int a) { delegate.defaultColor(r, g, b, a); }

    @Override
    public void unsetDefaultColor() { delegate.unsetDefaultColor(); }
}
