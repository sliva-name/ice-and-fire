"""Draws the 32x32 sprites for the oath/relic/blessing items.

Usage: python tools/gen_item_sprites.py [out_dir] [name ...]
"""
import math
import os
import sys

from itemart import *  # noqa: F401,F403

HERE = os.path.dirname(os.path.abspath(__file__))
OUT = os.path.join(HERE, '..', 'src', 'main', 'resources', 'assets', 'iceandfire', 'textures', 'item')

WOOD = ramp('#8a5a2b')
LEATHER = ramp('#7a4a2a')
BRASS = ramp('#c9973a')
GOLD = ramp('#e2b13c')
SILVER = ramp('#a9b4c2')
IRON = ramp('#5b6270')
BONE = ramp('#e2d3ae')
CLOTH = ramp('#e6dfca')


def lerp(a, b, t):
    return (a[0] + (b[0] - a[0]) * t, a[1] + (b[1] - a[1]) * t)


def band(p, ang, hl, hw):
    """Thin rectangle across a path point. ang is the path direction in degrees."""
    return rrect(p[0], p[1], hw, hl, 0.3, ang)


def line(p0, p1, w=0.55):
    return capsule(p0[0], p0[1], p1[0], p1[1], w)


def bird_rattle(s):
    handle = capsule(16, 19.5, 16.6, 29.5, 1.8, 1.4)
    s.paint(handle, WOOD, 1.8)
    for y in (23, 26.5):
        s.fill(rrect(16.2, y, 2.0, 0.55), '#3d2412', within=handle < 0)
    head = ellipse(16, 13, 7.0, 8.0)
    body = s.paint(head, ramp('#d9b46a'), 6)
    for k, off in enumerate((-4.2, 0.0, 4.2)):
        s.fill(capsule(16 + off, 7.8, 16 + off * 0.85, 18.0, 0.7), '#a83a2c', within=body)
    for p in ((12, 10.5), (19.5, 11.5), (13.5, 16), (18.5, 16.5), (16, 9)):
        s.fill(circle(p[0], p[1], 0.6), '#4a2c14', within=body)
    s.fill(circle(12.3, 9.2, 1.3), '#f6e3ae', within=body)
    s.paint(rrect(16.2, 20.6, 3.6, 0.9, 0.5), ramp('#5a3a1e'), 1)
    for x, c in ((7.5, '#c0392b'), (24.5, '#e8e0c8')):
        s.fill(line((x, 17.6), (x, 21.6), 0.4), '#3d2a18')
        s.paint(circle(x, 23, 1.5), ramp(c), 1.5)
    s.paint(capsule(17.5, 6.2, 22.5, 2.6, 1.1, 0.5), ramp('#3f9a63'), 1.2)
    s.paint(capsule(14.5, 6.4, 10.5, 3.0, 1.0, 0.4), ramp('#c9463a'), 1.2)


def blackfrost_heart(s):
    heart = union(circle(11.6, 12.5, 6.6), circle(20.4, 12.5, 6.6), poly([(5.4, 14.2), (26.6, 14.2), (16, 28)]))
    for a in range(0, 360, 60):
        r = math.radians(a + 15)
        p = (16 + math.cos(r) * 11, 15 + math.sin(r) * 11)
        q = (16 + math.cos(r) * 15, 15 + math.sin(r) * 15)
        if q[1] < 27:
            s.paint(capsule(p[0], p[1], q[0] * 0.5 + p[0] * 0.5, q[1] * 0.5 + p[1] * 0.5, 1.3, 0.25), ramp('#7fd8f0'), 1.2)
    body = s.paint(heart, rgb('#070b22', '#101d47', '#1b3f7c', '#2f75b4', '#7fcbea'), 6.5)
    for pts in (((16, 6.5), (14.5, 12), (17, 15.5), (15, 22.5)), ((23.5, 10.5), (20.5, 13), (21.5, 18))):
        for a, b in zip(pts, pts[1:]):
            s.fill(line(a, b, 0.5), '#b8f2ff', within=body)
    s.paint(circle(16, 15.5, 2.6), rgb('#3c8ccf', '#7fd0f2', '#d6f6ff', '#ffffff'), 2.6, within=body)
    s.fill(ellipse(10.5, 8.6, 2.2, 1.2, -35), '#c8f1ff', within=body)


def frost_ward(s):
    left = poly([(16, 6), (8.6, 16.5), (16, 29.5)])
    right = poly([(16, 6), (23.4, 16.5), (16, 29.5)])
    s.paint(left, ramp('#4f8fd0'), 5, light=0.7)
    s.paint(right, ramp('#8fd8ff'), 5, light=0.8)
    up = poly([(16, 6), (8.6, 16.5), (23.4, 16.5)])
    s.paint(up, ramp('#b9ecff'), 2.5, bias=0.15, keep=(right < 0) & (Y > 17))
    s.fill(line((16, 6.5), (16, 29), 0.35), '#e9fbff')
    s.fill(line((9, 16.5), (23, 16.5), 0.35), '#dff6ff')
    s.fill(circle(12.2, 13.2, 1.0), '#ffffff')
    s.paint(ring(16, 3.7, 2.1, 0.75), SILVER, 1)
    s.fill(circle(16, 6.4, 1.0), '#dfeaf3')


def gaze_gauze(s):
    cloth = rrect(16, 16, 13, 7.5, 2.2, -12)
    cloth = sub(cloth, poly([(28.5, 6.5), (32, 10), (28.6, 12.5)]))
    body = s.paint(cloth, CLOTH, 4)
    for y in (10.8, 21.4):
        s.fill(rrect(16, y, 13, 0.55, 0, -12), '#7a8fbf', within=body)
    for x in (5.5, 26.5):
        s.fill(rrect(x, 16 - (x - 16) * 0.21, 0.6, 7.2, 0, -12), '#7a8fbf', within=body)
    eye = poly([(9.2, 16.8), (12.5, 14.2), (16, 13.3), (19.5, 13.8), (22.8, 15.4), (19.6, 15.6), (16, 16.2), (12.5, 17.2)])
    s.fill(eye, '#3a4570', within=body)
    for a, b in (((12, 17.4), (10.6, 19.6)), ((15.5, 16.4), (15.5, 19.2)), ((19, 15.9), (20.4, 18.4))):
        s.fill(line(a, b, 0.5), '#3a4570', within=body)
    s.fill(circle(16, 14.7, 1.4), '#e2e6f5', within=body)
    for k in range(4):
        s.fill(line((27.5, 8 + k * 1.3), (29.6, 7.4 + k * 1.3), 0.3), '#cfc7b0')


def grave_candle(s):
    s.paint(ellipse(16, 27, 9.5, 3.0), ramp('#4c525d'), 3)
    s.paint(ellipse(16, 25.8, 7.4, 2.0), ramp('#666d79'), 2)
    wax = rrect(16, 18, 3.9, 8.5, 1.2)
    body = s.paint(wax, BONE, 3.4)
    s.fill(ellipse(16, 10.2, 3.9, 1.2), '#f3ead0', within=body)
    for x, ln in ((13.2, 5), (18.7, 8)):
        s.paint(capsule(x, 11, x, 11 + ln, 1.05, 1.15), BONE, 1.1)
        s.paint(circle(x, 11 + ln + 0.5, 1.2), BONE, 1.2)
    s.fill(line((16, 9.8), (16, 8.2), 0.45), '#2a1c14')
    flame = union(ellipse(16, 6.6, 2.4, 3.0), poly([(13.8, 5.8), (18.2, 5.8), (16.1, 1.0)]))
    s.paint(flame, rgb('#0f6d4d', '#22b083', '#6ff0b5', '#d5ffe6'), 2.6)
    s.fill(ellipse(16, 6.9, 0.9, 1.5), '#f4fff8')


def hall_pie(s):
    s.paint(ellipse(16, 20, 14, 9.2), ramp('#9aa4b4'), 4)
    s.paint(ellipse(16, 19.3, 11.6, 7.4), ramp('#b7c0cd'), 3)
    crust = ellipse(16, 17.6, 11.2, 8.2)
    s.paint(crust, ramp('#d99a3d'), 5)
    fill = ellipse(16, 17.0, 8.6, 5.6)
    fm = s.paint(fill, rgb('#5a0f16', '#93202a', '#c9403f', '#e8705a'), 3.5)
    for k in range(-3, 4):
        s.paint(capsule(16 + k * 3.6 - 6, 10, 16 + k * 3.6 + 6, 25, 1.05), ramp('#e6ad4a'), 1.0, within=fm)
        s.paint(capsule(16 + k * 3.6 + 6, 10, 16 + k * 3.6 - 6, 25, 1.05), ramp('#d99a3d'), 1.0, within=fm)
    for a in range(0, 360, 24):
        r = math.radians(a)
        p = (16 + math.cos(r) * 9.8, 17.4 + math.sin(r) * 6.9)
        s.fill(circle(p[0], p[1], 0.65), '#8a5418', within=(crust < 0) & ~(fill < 0))
    s.fill(ellipse(11.5, 12.6, 2.6, 0.8, -15), '#f7d78a', within=(crust < 0) & ~(fill < 0))


def hive_seal(s):
    for x, y, a, c in ((11.4, 26, 14, '#9a1f2b'), (20.6, 26, -14, '#b3262f')):
        s.paint(poly([(x - 2.6, 18), (x + 2.6, 18), (x + 2.8 + a * 0.2, 31), (x + a * 0.05, 28.6), (x - 2.6 + a * 0.2, 31)]), ramp(c), 2.5)
    wax = circle(16, 13.5, 9.2)
    for a in range(0, 360, 36):
        r = math.radians(a + 8)
        wax = union(wax, circle(16 + math.cos(r) * 8.8, 13.5 + math.sin(r) * 8.8, 1.7))
    body = s.paint(wax, ramp('#b9781f'), 6)
    s.paint(ring(16, 13.5, 6.6, 0.8), ramp('#8f5514'), 1, within=body)
    hexa = [(16 + math.cos(math.radians(60 * i + 30)) * 5.0, 13.5 + math.sin(math.radians(60 * i + 30)) * 5.0) for i in range(6)]
    s.paint(poly(hexa), ramp('#dea23a'), 3.6, within=body)
    inner = [(16 + math.cos(math.radians(60 * i + 30)) * 2.3, 13.5 + math.sin(math.radians(60 * i + 30)) * 2.3) for i in range(6)]
    s.paint(poly(inner), ramp('#8f5514'), 2, within=body)
    s.fill(ellipse(10.8, 8.0, 2.4, 0.9, -40), '#f5cf7a', within=body)


def march_horn(s):
    pts = [((4.5, 26.5), 1.5), ((8.5, 21.5), 2.1), ((13.5, 17.2), 2.9), ((19, 13.6), 3.8), ((25, 9.4), 5.0)]
    horn = None
    for (a, ra), (b, rb) in zip(pts, pts[1:]):
        c = capsule(a[0], a[1], b[0], b[1], ra, rb)
        horn = c if horn is None else union(horn, c)
    body = s.paint(horn, BONE, 4)
    ang = 38
    for p, hl, hw in (((11.0, 19.2), 3.2, 0.9), ((23.8, 10.3), 5.4, 1.1)):
        s.paint(rrect(p[0], p[1], hw, hl, 0.4, ang - 90 + 90), BRASS, 1.0, within=body)
    s.paint(circle(4.4, 26.6, 1.7), BRASS, 1.5)
    s.fill(circle(4.4, 26.6, 0.6), '#2a1c14')
    s.fill(ellipse(17, 14.3, 3.0, 0.6, -38), '#fff7df', within=body)
    s.fill(ellipse(27.0, 7.6, 2.2, 4.4, 40), '#2e2016', within=body)
    s.fill(ellipse(26.4, 8.4, 1.1, 2.6, 40), '#4c3826', within=body)


def moon_phial(s):
    s.paint(rrect(16, 3.6, 2.5, 2.3, 0.7), WOOD, 2)
    s.paint(rrect(16, 6.3, 3.5, 1.2, 0.5), ramp('#a9cfdc'), 1.2)
    s.paint(rrect(16, 10.5, 2.7, 4.2, 0.6), ramp('#a9cfdc'), 2.6, bias=0.1)
    glass = circle(16, 20.2, 9.6)
    g = s.paint(glass, ramp('#9cc4d2'), 6)
    liquid = inter(circle(16, 20.4, 8.2), Y - 14.5)
    s.paint(liquid, rgb('#6f8a2c', '#a9c24e', '#dcec86', '#faffc4'), 8, within=g)
    s.fill(ellipse(16, 14.5, 7.4, 1.2), '#e9f7a8', within=g & (Y < 15.6))
    moon = sub(circle(15.6, 20.6, 4.6), circle(18.2, 19.4, 4.0))
    s.fill(moon, '#fffbe0')
    s.fill(circle(21.5, 24.5, 0.7), '#fffbe0')
    s.fill(circle(10.8, 24.2, 0.6), '#fffbe0')
    s.fill(capsule(9.6, 16.2, 8.8, 21.5, 0.7), '#eef9ff', within=g)
    s.fill(circle(11.4, 14.6, 0.8), '#ffffff', within=g)


def night_wrap(s):
    a0, a1 = (7.5, 20.5), (24.5, 12.5)
    body = s.paint(capsule(a0[0], a0[1], a1[0], a1[1], 6.6), ramp('#4b3a80'), 5.5)
    ang = math.degrees(math.atan2(a1[1] - a0[1], a1[0] - a0[0]))
    cap = ellipse(a1[0], a1[1], 3.0, 6.7, ang + 0.0)
    cm = s.paint(cap, ramp('#7a63b8'), 3)
    s.fill(ring(a1[0], a1[1], 1.9, 0.4), '#2c2153', within=cm) if False else None
    for r in (1.2, 2.4):
        e = ellipse(a1[0], a1[1], 3.0 * r / 3.0 * 0.9, 6.7 * r / 3.0, ang)
        s.fill(np.abs(e) - 0.28, '#2e2358', within=cm)
    for t in (0.28, 0.62):
        p = lerp(a0, a1, t)
        s.paint(rrect(p[0], p[1], 1.0, 6.7, 0.4, ang), ramp('#c2a248'), 1, within=body)
    s.fill(line((6.5, 17.2), (21, 10.4), 0.35), '#8a76c8', within=body)
    s.fill(line((9, 24), (23.5, 17.4), 0.35), '#2a2050', within=body)
    p = lerp(a0, a1, 0.45)
    s.paint(circle(p[0], p[1], 1.7), ramp('#e7c95a'), 1.7)
    s.fill(circle(p[0], p[1], 0.6), '#fff5c0')


def scroll(s, parchment, seal, ribbon, glyph):
    a0, a1 = (8.5, 24.5), (23.5, 7.5)
    ang = math.degrees(math.atan2(a1[1] - a0[1], a1[0] - a0[0]))
    body = s.paint(capsule(a0[0], a0[1], a1[0], a1[1], 4.7), ramp(parchment), 4)
    for p in (a0, a1):
        cm = s.paint(ellipse(p[0], p[1], 2.4, 4.7, ang), ramp(parchment, spread=1.3), 2.4, bias=-0.15)
        s.fill(np.abs(ellipse(p[0], p[1], 1.2, 2.5, ang)) - 0.3, '#8a6a3a', within=cm)
    for t, off in ((0.22, 0), (0.78, 0)):
        p = lerp(a0, a1, t)
        s.paint(rrect(p[0], p[1], 0.9, 4.7, 0.3, ang), ramp(ribbon), 0.9, within=body)
    s.paint(poly([(11.6, 22.6), (14.4, 20.8), (11.2, 29.6), (9.2, 27.6)]), ramp(ribbon), 1.2)
    m = lerp(a0, a1, 0.5)
    sc = circle(m[0], m[1], 3.9)
    for k in range(7):
        r = math.radians(k * 51 + 10)
        sc = union(sc, circle(m[0] + math.cos(r) * 3.6, m[1] + math.sin(r) * 3.6, 1.0))
    sm = s.paint(sc, ramp(seal), 3.6)
    glyph(s, m, sm)


def glyph_waste(s, m, sm):
    s.fill(circle(m[0], m[1], 1.05), '#ffe7a8', within=sm)
    for k in range(8):
        r = math.radians(k * 45)
        s.fill(circle(m[0] + math.cos(r) * 2.2, m[1] + math.sin(r) * 2.2, 0.35), '#ffe7a8', within=sm)


def glyph_tide(s, m, sm):
    for dy in (-0.9, 0.9):
        pts = [(m[0] - 2.3 + i * 0.8, m[1] + dy + math.sin(i * 1.2) * 0.6) for i in range(7)]
        for a, b in zip(pts, pts[1:]):
            s.fill(line(a, b, 0.32), '#d6fff8', within=sm)


def glyph_barrow(s, m, sm):
    s.fill(line((m[0], m[1] - 2.2), (m[0], m[1] + 2.2), 0.4), '#dff2c8', within=sm)
    s.fill(line((m[0] - 1.7, m[1] - 0.7), (m[0] + 1.7, m[1] - 0.7), 0.4), '#dff2c8', within=sm)
    s.fill(circle(m[0], m[1] - 2.3, 0.6), '#dff2c8', within=sm)


def glyph_frost(s, m, sm):
    s.fill(poly([(m[0], m[1] - 2.5), (m[0] + 1.7, m[1]), (m[0], m[1] + 2.5), (m[0] - 1.7, m[1])]), '#e6fbff', within=sm)
    s.fill(circle(m[0], m[1], 0.5), '#3f78b8', within=sm)


def oath_waste(s):
    scroll(s, '#e6cf98', '#c9772a', '#8f4a1a', glyph_waste)


def oath_tide(s):
    scroll(s, '#d7e0cf', '#2a9d9a', '#1f6f86', glyph_tide)


def oath_barrow(s):
    scroll(s, '#cfcdb4', '#5f7a4a', '#3a4a33', glyph_barrow)


def oath_blackfrost(s):
    scroll(s, '#d4e0ec', '#3e5fa8', '#1e2c58', glyph_frost)


def road_loaf(s):
    loaf = ellipse(16, 17.5, 12.6, 8.0, -10)
    loaf = union(loaf, ellipse(16, 17.5, 11.6, 8.4, -10))
    body = s.paint(loaf, ramp('#c98a3d'), 6)
    for i, x in enumerate((9.5, 15.2, 20.8)):
        y = 17.5 - (x - 16) * 0.17 - 1.4
        s.fill(rrect(x, y, 0.9, 3.4, 0.5, 28), '#f0cf88', within=body)
        s.fill(rrect(x + 0.9, y + 0.5, 0.35, 2.8, 0.2, 28), '#8a5518', within=body)
    for p in ((7, 19), (24, 15), (12, 12.5), (19, 21.5), (22, 20.5)):
        s.fill(circle(p[0], p[1], 0.45), '#f6e6c0', within=body)
    s.fill(ellipse(11, 12.8, 3.4, 0.9, -12), '#eaba6e', within=body)
    s.fill(ellipse(16, 24, 9.6, 0.9, -10), '#7a4716', within=body)


def sea_shell(s):
    fan = poly([(16, 28), (5, 14), (9, 6.6), (16, 4.4), (23, 6.6), (27, 14)])
    for k in range(-3, 4):
        r = math.radians(-90 + k * 20)
        fan = union(fan, circle(16 + math.cos(r) * 15.6, 28 + math.sin(r) * 15.6, 3.2))
    fan = inter(fan, circle(16, 28, 23.2))
    fan = inter(fan, poly([(16, 28.6), (-2, 8), (-2, -4), (34, -4), (34, 8)]))
    body = s.paint(fan, ramp('#f0c9b4'), 4.0, bias=0.1)
    for k in range(-4, 5):
        r = math.radians(-90 + k * 17)
        e = (16 + math.cos(r) * 16, 28 + math.sin(r) * 16)
        s.fill(line((16, 27), e, 0.6), '#a8635a', within=body)
    s.fill(np.abs(circle(16, 28, 14.0)) - 0.5, '#f7dccb', within=body)
    s.fill(np.abs(circle(16, 28, 9.0)) - 0.4, '#f7dccb', within=body)
    s.paint(rrect(16, 27.4, 4.2, 2.2, 1.0), ramp('#d6a58f'), 2)
    s.fill(ellipse(13.5, 8.6, 2.6, 0.8, -20), '#fff2e6', within=body)


def shard_knife(s):
    A, T = (13.0, 19.0), (28.8, 3.2)
    d = (T[0] - A[0], T[1] - A[1])
    l = math.hypot(*d)
    d = (d[0] / l, d[1] / l)
    n = (-d[1], d[0])
    ang = math.degrees(math.atan2(d[1], d[0]))
    blade = poly([(A[0] + n[0] * 3.0, A[1] + n[1] * 3.0), (A[0] + d[0] * 4 + n[0] * 3.3, A[1] + d[1] * 4 + n[1] * 3.3),
                  T, (A[0] + d[0] * 4 - n[0] * 3.3, A[1] + d[1] * 4 - n[1] * 3.3), (A[0] - n[0] * 3.0, A[1] - n[1] * 3.0)])
    body = s.paint(blade, rgb('#0d2a3a', '#1d5670', '#3a95b4', '#82dbea', '#e4fbff'), 3.2)
    s.fill(line(A, (T[0] - d[0] * 1.5, T[1] - d[1] * 1.5), 0.45), '#0f3547', within=body)
    s.fill(line((A[0] + n[0] * 2.4 + d[0] * 5, A[1] + n[1] * 2.4 + d[1] * 5), (T[0] + n[0] * 0.3 - d[0] * 1.8, T[1] + n[1] * 0.3 - d[1] * 1.8), 0.28), '#f1fdff', within=body)
    s.paint(rrect(A[0] - d[0] * 1, A[1] - d[1] * 1, 1.2, 5.6, 0.5, ang), IRON, 1.2)
    G = (5.6, 26.6)
    grip = capsule(A[0] - d[0] * 2, A[1] - d[1] * 2, G[0], G[1], 1.8)
    gb = s.paint(grip, LEATHER, 1.8)
    for t in (0.2, 0.42, 0.64, 0.86):
        p = lerp((A[0] - d[0] * 2, A[1] - d[1] * 2), G, t)
        s.fill(rrect(p[0], p[1], 0.35, 2.0, 0, ang), '#3a2110', within=gb)
    s.paint(circle(G[0] - 0.4, G[1] + 0.4, 1.9), IRON, 1.9)
    s.fill(circle(G[0] - 0.9, G[1] - 0.1, 0.55), '#a9d8e6')


def silver_brooch(s):
    s.paint(capsule(3.5, 19.5, 28, 12.2, 0.85, 0.5), SILVER, 0.8)
    rg = ring(16, 16, 9.6, 1.9)
    rm = s.paint(rg, SILVER, 1.9)
    for a in range(0, 360, 90):
        r = math.radians(a + 45)
        s.paint(circle(16 + math.cos(r) * 9.6, 16 + math.sin(r) * 9.6, 1.9), ramp('#6c4bd0'), 1.9)
        s.fill(circle(16 + math.cos(r) * 9.6 - 0.5, 16 + math.sin(r) * 9.6 - 0.5, 0.4), '#e6dcff')
    gem = poly([(16, 10.5), (21.5, 16), (16, 21.5), (10.5, 16)])
    s.paint(gem, rgb('#1d1650', '#3d2f9a', '#6d55e0', '#b6a3ff'), 3)
    s.fill(line((16, 10.5), (16, 21.5), 0.25), '#2a2072')
    s.fill(line((10.5, 16), (21.5, 16), 0.25), '#2a2072')
    s.fill(poly([(16, 11.4), (18.4, 13.8), (16, 15.4), (13.6, 13.8)]), '#d2c6ff')
    s.paint(capsule(22, 14.2, 28, 12.2, 0.9, 0.5), SILVER, 0.8, keep=rg < 0)


def siren_bead(s):
    for a, b in (((15.2, 14.8), (7, 3.4)), ((16.8, 14.8), (25, 3.4))):
        s.fill(line(a, b, 0.42), '#6a4527')
    for p, r, c in (((11.6, 8.8), 1.7, '#e7dfc8'), ((20.4, 8.8), 1.7, '#e7dfc8'), ((7.6, 4.0), 1.5, '#39b7a6'), ((24.4, 4.0), 1.5, '#39b7a6')):
        s.paint(circle(p[0], p[1], r), ramp(c), r)
    s.paint(rrect(16, 15.4, 1.9, 1.5, 0.6), GOLD, 1.6)
    ball = circle(16, 22.3, 6.6)
    b = s.paint(ball, rgb('#0b4e52', '#178f8c', '#37c8b9', '#8ef0dc', '#f4fffb'), 6.6)
    for dy, rx in ((-2.2, 5.6), (0.6, 6.5), (3.4, 5.4)):
        e = np.abs(ellipse(16, 22.3 + dy, rx, 1.3)) - 0.32
        s.fill(e, '#d9b043', within=b & (Y > 22.3 + dy - 0.4))
    s.fill(ellipse(13.3, 19.4, 1.9, 1.2, -35), '#ffffff', within=b)
    s.fill(circle(19.4, 25.6, 0.7), '#c8fff2', within=b)


def storm_pinion(s):
    a0, a1 = (4.5, 27.5), (26.5, 5.5)
    ang = math.degrees(math.atan2(a1[1] - a0[1], a1[0] - a0[0]))
    vane = ellipse(16.5, 16.5, 15.5, 5.4, ang)
    vane = sub(vane, poly([(20, 6), (24, 8), (21, 10.5)]))
    vane = sub(vane, poly([(10.5, 21), (7, 20), (9.5, 24)]))
    body = s.paint(vane, ramp('#6f96cf'), 4.4)
    for k in range(-5, 6):
        p = lerp((16.5, 16.5), a1, k / 6.0) if k > 0 else lerp((16.5, 16.5), a0, -k / 6.0)
        s.fill(line((p[0], p[1]), (p[0] - 3.1 * math.sin(math.radians(ang)) * -1, p[1] - 3.1 * math.cos(math.radians(ang))), 0.3), '#3f5f9a', within=body)
    s.paint(capsule(a0[0] - 0.5, a0[1] + 0.5, a1[0], a1[1], 1.0, 0.55), ramp('#e9edf5'), 1.0)
    bolt = poly([(15.5, 8.6), (20.8, 8.6), (18, 13.8), (22.2, 13.8), (12.2, 25), (15, 16.6), (11, 16.6)])
    s.paint(bolt, rgb('#c98a00', '#f2c21c', '#ffe55a', '#fff8b8'), 2.4)


def waste_spur(s):
    heel = ring(11.5, 17, 7.8, 1.9)
    heel = sub(heel, rrect(20, 17, 6, 3.3))
    hm = s.paint(heel, ramp('#b5732c'), 1.9)
    s.paint(capsule(17.5, 17, 23.6, 17, 1.6, 1.2), ramp('#b5732c'), 1.4)
    for sgn in (-1, 1):
        s.paint(capsule(5.1, 17 + sgn * 6.5, 2.4, 17 + sgn * 11.4, 1.5), LEATHER, 1.5)
        s.fill(circle(2.7, 17 + sgn * 10.8, 0.6), '#d8c690')
    teeth = circle(26, 17, 3.6)
    for k in range(9):
        r = math.radians(k * 40)
        teeth = union(teeth, capsule(26 + math.cos(r) * 3, 17 + math.sin(r) * 3, 26 + math.cos(r) * 5.4, 17 + math.sin(r) * 5.4, 1.0, 0.25))
    tm = s.paint(teeth, ramp('#cf9a4a'), 3.6)
    s.erase(circle(26, 17, 1.2))
    s.fill(ring(26, 17, 1.2, 0.35), '#5a3612')
    s.fill(circle(9.3, 10.6, 0.5), '#f4d68e', within=hm)


def watchfire(s):
    bowl = poly([(7, 19), (25, 19), (20.5, 27.5), (11.5, 27.5)])
    bm = s.paint(bowl, ramp('#565d6b'), 5)
    s.fill(line((10, 22), (22, 22), 0.3), '#2a2f38', within=bm)
    s.paint(rrect(16, 19, 9.6, 1.5, 0.7), ramp('#7a8290'), 1.5)
    s.paint(rrect(16, 28.8, 5.5, 1.0, 0.4), ramp('#3d434d'), 1)
    for x in (9.5, 22.5):
        s.paint(capsule(x, 26.5, x + (x - 16) * 0.12, 30.6, 0.8), ramp('#3d434d'), 0.8)
    fl = union(ellipse(16, 12.4, 6.2, 7.2), poly([(10.4, 12), (21.6, 12), (17, 1.0), (14.6, 3.6)]))
    fl = sub(fl, ellipse(16, 20.5, 12, 3.0))
    s.paint(fl, rgb('#8a1c10', '#d24a12', '#f58a1e', '#ffc93c', '#fff0a0'), grad=(2, 19))
    inner = union(ellipse(16, 14.6, 3.6, 4.6), poly([(12.6, 14), (19.4, 14), (16.3, 5.2)]))
    s.paint(inner, rgb('#e8631a', '#ff9d26', '#ffd94a', '#fff6b0'), grad=(5, 19))
    s.fill(ellipse(16, 16.5, 1.4, 2.2), '#fffbe0')


def worm_bait(s):
    meat = union(ellipse(15.5, 18.5, 9.4, 7.4, -10), circle(21.5, 16, 4.6), circle(9.5, 20.5, 4.2))
    body = s.paint(meat, ramp('#c0454b'), 6)
    for p, r in (((13, 15.5), 1.7), ((19, 21), 1.5), ((10.5, 20), 1.2), ((22.5, 17), 1.1)):
        s.fill(ellipse(p[0], p[1], r * 1.4, r * 0.8, -30), '#f2cdbf', within=body)
    for x in (10, 15.5, 21):
        s.fill(line((x - 1.2, 10.5), (x + 1.8, 27.5), 0.45), '#b89258', within=body)
    s.fill(line((6.5, 17.8), (26, 15.4), 0.45), '#d6b676', within=body)
    worm = None
    prev = None
    pts = [(21.5, 11.2), (24.3, 8.8), (24.6, 5.6), (21.6, 4.0), (18.8, 5.6)]
    for a, b in zip(pts, pts[1:]):
        c = capsule(a[0], a[1], b[0], b[1], 1.55, 1.45)
        worm = c if worm is None else union(worm, c)
    wm = s.paint(worm, ramp('#e8bfa0'), 1.6)
    for p in pts[1:4]:
        s.fill(circle(p[0] + 0.7, p[1], 0.35), '#a86a4a', within=wm)
    s.fill(circle(19.4, 5.1, 0.45), '#2a1410', within=wm)


def fishing_spear(s):
    shaft = capsule(4.2, 28.2, 22.6, 9.6, 1.35, 1.1)
    sm = s.paint(shaft, WOOD, 1.4)
    for t in (0.12, 0.2, 0.28, 0.36):
        p = lerp((4.2, 28.2), (22.6, 9.6), t)
        s.fill(rrect(p[0], p[1], 0.3, 1.6, 0, 45), '#3a2110', within=sm)
    for t in (0.5, 0.62):
        p = lerp((4.2, 28.2), (22.6, 9.6), t)
        s.paint(rrect(p[0], p[1], 0.5, 1.8, 0.2, 45), ramp('#d8c690'), 0.6, within=sm)
    s.paint(rrect(22.6, 9.6, 1.5, 2.9, 0.5, 45), ramp('#cfa64a'), 1.5)
    steel = rgb('#0e4a52', '#25868c', '#6ad4d6', '#d6fbfa')
    s.paint(poly([(22.2, 9.2), (23.2, 8.2), (28.6, 1.6), (26.6, 8.0), (23.6, 10.4)]), steel, 2.2)
    for sgn in (-1, 1):
        pts = [(22.0, 10.0), (20.6 + 3.2 * (sgn > 0), 5.6 - 0.0), (21.6 + 5.6 * (sgn > 0), 3.0)] if sgn < 0 else [(23.6, 10.8), (27.0, 11.6), (30.0, 9.6)]
        for a, b in zip(pts, pts[1:]):
            s.paint(capsule(a[0], a[1], b[0], b[1], 0.9, 0.5), steel, 0.9)
        e = pts[-1]
        s.paint(poly([(e[0] - 0.6, e[1] + 0.4), (e[0] + 0.6, e[1] - 0.2), (e[0] + (0.9 if sgn > 0 else 1.5), e[1] + (-2.0 if sgn > 0 else -1.6))]), steel, 0.6)
    s.fill(circle(26.6, 4.4, 0.4), '#f4ffff')
    s.paint(circle(3.8, 28.6, 1.6), ramp('#5a3a1e'), 1.6)


ITEMS = {
    'bird_rattle': bird_rattle, 'blackfrost_heart': blackfrost_heart, 'frost_ward': frost_ward,
    'gaze_gauze': gaze_gauze, 'grave_candle': grave_candle, 'hall_pie': hall_pie,
    'hive_seal': hive_seal, 'march_horn': march_horn, 'moon_phial': moon_phial,
    'night_wrap': night_wrap, 'oath_barrow': oath_barrow, 'oath_blackfrost': oath_blackfrost,
    'oath_tide': oath_tide, 'oath_waste': oath_waste, 'road_loaf': road_loaf,
    'sea_shell': sea_shell, 'shard_knife': shard_knife, 'silver_brooch': silver_brooch,
    'siren_bead': siren_bead, 'storm_pinion': storm_pinion, 'waste_spur': waste_spur,
    'watchfire': watchfire, 'worm_bait': worm_bait, 'fishing_spear': fishing_spear,
}

if __name__ == '__main__':
    out = sys.argv[1] if len(sys.argv) > 1 else OUT
    only = sys.argv[2:] or list(ITEMS)
    os.makedirs(out, exist_ok=True)
    for name in only:
        sp = Sprite()
        ITEMS[name](sp)
        sp.render().save(os.path.join(out, name + '.png'))
        print('drew', name)
