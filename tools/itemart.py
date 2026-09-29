"""Tiny shaded-pixel-art engine used to draw the 32x32 item sprites.

Shapes are signed-distance functions in a 32x32 unit space (y grows down).
Every shape is lit from the top-left, posterised into a colour ramp, sampled
once per pixel and finally wrapped in a dark selective outline.
"""
import math
import numpy as np
from PIL import Image

N = 32
SS = 4
W = N * SS
_ys, _xs = np.mgrid[0:W, 0:W]
X = (_xs + 0.5) / SS
Y = (_ys + 0.5) / SS
_L = np.array([-0.5, -0.62, 0.6])
_L /= np.linalg.norm(_L)


def hexc(h):
    h = h.lstrip('#')
    return np.array([int(h[i:i + 2], 16) for i in (0, 2, 4)], float)


def ramp(base, n=5, spread=1.0):
    """Dark to light ramp with cool shadows and warm highlights."""
    b = hexc(base) if isinstance(base, str) else np.array(base, float)
    out = []
    for i in range(n):
        t = i / (n - 1)
        k = 0.32 + 1.05 * t
        c = b * k
        c = c + (np.array([48, 34, 92]) - c) * max(0.0, 0.5 - t) * 0.55 * spread
        c = c + (np.array([255, 238, 190]) - c) * max(0.0, t - 0.55) * 0.5
        out.append(np.clip(c, 0, 255))
    return out


def rgb(*cols):
    return [hexc(c) for c in cols]


# ---------- signed distance helpers ----------
def circle(cx, cy, r):
    return np.hypot(X - cx, Y - cy) - r


def ellipse(cx, cy, rx, ry, rot=0.0):
    a = math.radians(rot)
    dx, dy = X - cx, Y - cy
    u = dx * math.cos(a) + dy * math.sin(a)
    v = -dx * math.sin(a) + dy * math.cos(a)
    return (np.hypot(u / rx, v / ry) - 1.0) * min(rx, ry)


def rrect(cx, cy, hw, hh, r=0.0, rot=0.0):
    a = math.radians(rot)
    dx, dy = X - cx, Y - cy
    u = dx * math.cos(a) + dy * math.sin(a)
    v = -dx * math.sin(a) + dy * math.cos(a)
    qx = np.abs(u) - (hw - r)
    qy = np.abs(v) - (hh - r)
    return np.hypot(np.maximum(qx, 0), np.maximum(qy, 0)) + np.minimum(np.maximum(qx, qy), 0) - r


def capsule(x0, y0, x1, y1, r0, r1=None):
    r1 = r0 if r1 is None else r1
    px, py = X - x0, Y - y0
    dx, dy = x1 - x0, y1 - y0
    l2 = dx * dx + dy * dy
    t = np.clip((px * dx + py * dy) / l2, 0, 1) if l2 else np.zeros_like(px)
    return np.hypot(px - t * dx, py - t * dy) - (r0 + (r1 - r0) * t)


def poly(pts):
    pts = [(float(a), float(b)) for a, b in pts]
    d = np.full(X.shape, 1e9)
    inside = np.zeros(X.shape, bool)
    n = len(pts)
    for i in range(n):
        x0, y0 = pts[i]
        x1, y1 = pts[(i + 1) % n]
        ex, ey = x1 - x0, y1 - y0
        px, py = X - x0, Y - y0
        l2 = ex * ex + ey * ey
        t = np.clip((px * ex + py * ey) / l2, 0, 1)
        d = np.minimum(d, np.hypot(px - t * ex, py - t * ey))
        cond = ((y0 > Y) != (y1 > Y)) & (X < (x1 - x0) * (Y - y0) / (y1 - y0 + 1e-12) + x0)
        inside ^= cond
    return np.where(inside, -d, d)


def ring(cx, cy, r, w):
    return np.abs(np.hypot(X - cx, Y - cy) - r) - w


def union(*s):
    return np.minimum.reduce(s)


def inter(a, b):
    return np.maximum(a, b)


def sub(a, b):
    return np.maximum(a, -b)


def shape_mask(s):
    return s < 0


class Sprite:
    def __init__(self):
        self.col = np.zeros((W, W, 3))
        self.a = np.zeros((W, W), bool)

    def paint(self, sdf, cols, relief=3.0, bias=0.0, within=None, grad=None, light=1.0, keep=None):
        """Fill sdf<0 with a lit ramp. grad=(y0,y1) shades by height instead."""
        m = sdf < 0
        if within is not None:
            m &= within
        if keep is not None:
            m &= ~keep
        cols = np.array(cols)
        n = len(cols)
        if grad is not None:
            y0, y1 = grad
            t = np.clip((Y - y0) / (y1 - y0), 0, 0.999)
            idx = ((1 - t) * n).astype(int).clip(0, n - 1)
            idx = np.clip(idx + int(bias), 0, n - 1)
        else:
            d = np.clip(-sdf / relief, 0, 1)
            h = np.sqrt(1 - (1 - d) ** 2) * relief
            gy, gx = np.gradient(h, 1.0 / SS)
            gx *= 0.55
            gy *= 0.55
            nz = np.ones_like(h)
            ln = np.sqrt(gx * gx + gy * gy + 1)
            lam = (-gx * _L[0] - gy * _L[1] + nz * _L[2]) / ln
            t = np.clip((lam - 0.32) / 0.62 * light + bias, 0, 0.999)
            idx = (t * n).astype(int)
        self.col[m] = cols[idx][m]
        self.a |= m
        return m

    def fill(self, sdf, color, within=None, keep=None):
        m = sdf < 0
        if within is not None:
            m &= within
        if keep is not None:
            m &= ~keep
        self.col[m] = hexc(color) if isinstance(color, str) else color
        self.a |= m
        return m

    def erase(self, sdf):
        self.a &= ~(sdf < 0)

    def render(self, outline=True, out_tint=(14, 10, 26)):
        c = self.col[SS // 2::SS, SS // 2::SS].copy()
        a = self.a[SS // 2::SS, SS // 2::SS].copy()
        img = np.zeros((N, N, 4), np.uint8)
        img[..., :3] = np.clip(c, 0, 255).astype(np.uint8)
        img[..., 3] = np.where(a, 255, 0)
        if outline:
            out = img.copy()
            tint = np.array(out_tint, float)
            for y in range(N):
                for x in range(N):
                    if a[y, x]:
                        continue
                    ns = [(y + 1, x), (y - 1, x), (y, x + 1), (y, x - 1)]
                    cs = [c[j, i] for j, i in ns if 0 <= j < N and 0 <= i < N and a[j, i]]
                    if not cs:
                        continue
                    m = np.mean(cs, axis=0) * 0.34 + tint * 0.55
                    out[y, x, :3] = np.clip(m, 0, 255).astype(np.uint8)
                    out[y, x, 3] = 255
            img = out
        return Image.fromarray(img, 'RGBA')
