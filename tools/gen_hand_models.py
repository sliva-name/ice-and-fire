"""Builds the 3D in-hand models (<name>_hand.json), the shared colour palette
texture, and the 26.x item definitions that switch between the flat sprite
(gui / frames / shelves) and the 3D model (hands, ground, head).

Usage: python tools/gen_hand_models.py
"""
import json
import math
import os

from PIL import Image

HERE = os.path.dirname(os.path.abspath(__file__))
ASSETS = os.path.join(HERE, '..', 'src', 'main', 'resources', 'assets', 'iceandfire')
ITEMS26 = os.path.join(HERE, '..', 'port-26.1', 'src', 'main', 'resources', 'assets', 'iceandfire', 'items')

DISPLAY_HELD = {
    'thirdperson_righthand': {'rotation': [0, 0, 0], 'translation': [0, 3, 1], 'scale': [0.55, 0.55, 0.55]},
    'thirdperson_lefthand': {'rotation': [0, 0, 0], 'translation': [0, 3, 1], 'scale': [0.55, 0.55, 0.55]},
    'firstperson_righthand': {'rotation': [0, -90, 25], 'translation': [1.13, 3.2, 1.13], 'scale': [0.68, 0.68, 0.68]},
    'firstperson_lefthand': {'rotation': [0, 90, -25], 'translation': [1.13, 3.2, 1.13], 'scale': [0.68, 0.68, 0.68]},
    'ground': {'rotation': [0, 0, 0], 'translation': [0, 2, 0], 'scale': [0.5, 0.5, 0.5]},
    'head': {'rotation': [0, 180, 0], 'translation': [0, 13, 7], 'scale': [1, 1, 1]},
}
DISPLAY_TOOL = {
    'thirdperson_righthand': {'rotation': [0, -90, 55], 'translation': [0, 4, 0.5], 'scale': [0.85, 0.85, 0.85]},
    'thirdperson_lefthand': {'rotation': [0, 90, -55], 'translation': [0, 4, 0.5], 'scale': [0.85, 0.85, 0.85]},
    'firstperson_righthand': {'rotation': [0, -90, 25], 'translation': [1.13, 3.2, 1.13], 'scale': [0.68, 0.68, 0.68]},
    'firstperson_lefthand': {'rotation': [0, 90, -25], 'translation': [1.13, 3.2, 1.13], 'scale': [0.68, 0.68, 0.68]},
    'ground': {'rotation': [0, 0, 0], 'translation': [0, 2, 0], 'scale': [0.5, 0.5, 0.5]},
    'head': {'rotation': [0, 180, 0], 'translation': [0, 13, 7], 'scale': [1, 1, 1]},
}

PALETTE = []


def col(h):
    h = h.lstrip('#').lower()
    if h not in PALETTE:
        PALETTE.append(h)
    return PALETTE.index(h)


class Model:
    def __init__(self, display=None):
        self.elements = []
        self.display = display or DISPLAY_HELD

    def box(self, x0, y0, x1, y1, z0, z1, color, rot=None, axis='y', origin=None, tag=None):
        i = col(color)
        u = (i % 16) + 0.25
        v = (i // 16) + 0.25
        uv = [u, v, u + 0.5, v + 0.5]
        el = {
            'from': [round(min(x0, x1), 3), round(min(y0, y1), 3), round(min(z0, z1), 3)],
            'to': [round(max(x0, x1), 3), round(max(y0, y1), 3), round(max(z0, z1), 3)],
            'faces': {f: {'uv': uv, 'texture': '#0'} for f in ('north', 'east', 'south', 'west', 'up', 'down')},
        }
        if rot:
            if origin is None:
                origin = [(x0 + x1) / 2, (y0 + y1) / 2, (z0 + z1) / 2]
            el['rotation'] = {'angle': rot, 'axis': axis, 'origin': [round(o, 3) for o in origin]}
        self.elements.append(el)

    def cyl(self, cx, cz, r, y0, y1, color):
        """Octagonal prism: two squares, one turned by 45 degrees."""
        a = r * 0.924
        self.box(cx - a, y0, cx + a, y1, cz - a, cz + a, color)
        self.box(cx - a, y0 + 0.01, cx + a, y1 - 0.01, cz - a, cz + a, color, rot=45, axis='y', origin=[cx, y0, cz])

    def bar(self, cx, cy, length, thick, depth, angle, color, z=8):
        """Box centred on (cx,cy) whose long side is turned about Z."""
        self.box(cx - length / 2, cy - thick / 2, cx + length / 2, cy + thick / 2, z - depth / 2, z + depth / 2,
                 color, rot=angle, axis='z', origin=[cx, cy, z])

    def save(self, name):
        data = {
            'textures': {'0': 'iceandfire:item/hand_palette', 'particle': 'iceandfire:item/hand_palette'},
            'elements': self.elements,
            'display': self.display,
        }
        path = os.path.join(ASSETS, 'models', 'item', name + '_hand.json')
        with open(path, 'w', encoding='utf8') as f:
            json.dump(data, f, indent=1)


def moon_phial():
    m = Model()
    m.cyl(8, 8, 4.4, 0.4, 7.6, 'dceb8a')
    m.cyl(8, 8, 4.5, 0.4, 1.0, '9cc4d2')
    m.cyl(8, 8, 4.5, 7.0, 7.6, '9cc4d2')
    m.cyl(8, 8, 3.2, 7.6, 8.6, 'a9cfdc')
    m.cyl(8, 8, 1.8, 8.6, 12.0, 'a9cfdc')
    m.cyl(8, 8, 2.5, 12.0, 12.9, '8fb7c6')
    m.cyl(8, 8, 1.9, 12.9, 15.2, '8a5a2b')
    for (x, y, w, h) in ((6.6, 4.2, 0.9, 2.8), (7.3, 3.6, 1.0, 0.9), (7.3, 6.5, 1.0, 0.9)):
        m.box(x, y, x + w, y + h, 12.55, 12.75, 'fffbe0')
    m.box(9.4, 5.4, 10.2, 6.2, 12.55, 12.75, 'fffbe0')
    m.save('moon_phial')


def grave_candle():
    m = Model()
    m.cyl(8, 8, 5.4, 0.0, 1.0, '4c525d')
    m.cyl(8, 8, 4.0, 1.0, 1.7, '666d79')
    m.cyl(8, 8, 2.3, 1.7, 10.0, 'e2d3ae')
    m.box(5.55, 6.5, 6.1, 9.6, 7.3, 8.7, 'f0e6c8')
    m.box(9.9, 4.6, 10.45, 9.6, 7.3, 8.7, 'f0e6c8')
    m.box(7.85, 10.0, 8.15, 11.0, 7.85, 8.15, '2a1c14')
    m.cyl(8, 8, 1.1, 10.8, 13.4, '22b083')
    m.cyl(8, 8, 0.6, 13.4, 14.6, '6ff0b5')
    m.cyl(8, 8, 0.45, 11.2, 13.0, 'd5ffe6')
    m.save('grave_candle')


def watchfire():
    m = Model()
    for x in (4.6, 11.4):
        for z in (4.6, 11.4):
            m.box(x - 0.6, 0, x + 0.6, 3.0, z - 0.6, z + 0.6, '3d434d')
    m.cyl(8, 8, 5.4, 3.0, 5.6, '565d6b')
    m.cyl(8, 8, 5.8, 5.6, 6.6, '7a8290')
    m.cyl(8, 8, 4.4, 6.6, 6.9, '2a2f38')
    m.cyl(8, 8, 3.6, 6.9, 9.0, 'd24a12')
    m.cyl(8, 8, 2.8, 9.0, 11.2, 'f58a1e')
    m.cyl(8, 8, 1.9, 11.2, 13.4, 'ffc93c')
    m.cyl(8, 8, 1.0, 13.4, 15.2, 'fff0a0')
    m.save('watchfire')


def hall_pie():
    m = Model()
    m.cyl(8, 8, 7.4, 0.0, 0.7, '9aa4b4')
    m.cyl(8, 8, 6.2, 0.7, 3.4, 'd99a3d')
    m.cyl(8, 8, 5.6, 3.4, 3.9, 'e6ad4a')
    m.cyl(8, 8, 4.6, 3.9, 4.1, '93202a')
    for off in (-2.6, 0.0, 2.6):
        m.box(8 + off - 0.5, 4.1, 8 + off + 0.5, 4.5, 8 - 4.4, 8 + 4.4, 'e6ad4a')
        m.box(8 - 4.4, 4.1, 8 + 4.4, 4.5, 8 + off - 0.5, 8 + off + 0.5, 'd99a3d')
    m.save('hall_pie')


def road_loaf():
    m = Model()
    m.box(1.8, 3.0, 14.2, 5.0, 4.8, 11.2, 'a8712e')
    m.box(1.0, 5.0, 15.0, 7.4, 4.2, 11.8, 'c98a3d')
    m.box(2.4, 7.4, 13.6, 9.0, 5.0, 11.0, 'd99a3d')
    m.box(4.0, 9.0, 12.0, 9.8, 5.8, 10.2, 'e2ac52')
    for x in (4.6, 7.6, 10.6):
        m.box(x, 9.75, x + 0.8, 10.0, 5.6, 8.9, 'f0cf88', rot=22.5, axis='y')
    m.box(2.0, 5.3, 3.0, 5.8, 4.1, 4.3, 'f6e6c0')
    m.save('road_loaf')


def march_horn():
    m = Model()
    m.bar(3.6, 3.4, 3.8, 1.8, 1.8, 45, 'e2d3ae')
    m.bar(3.6, 3.4, 0.6, 2.2, 2.2, 45, 'c9973a')
    m.bar(6.2, 6.0, 4.4, 2.6, 2.6, 45, 'e2d3ae')
    m.bar(9.4, 8.9, 4.6, 3.4, 3.4, 22.5, 'e2d3ae')
    m.bar(9.4, 8.9, 0.7, 3.9, 3.9, 22.5, 'c9973a')
    m.bar(12.6, 10.3, 3.6, 4.4, 4.4, 22.5, 'e2d3ae')
    m.bar(14.2, 11.0, 0.8, 5.0, 5.0, 22.5, 'c9973a')
    m.bar(14.7, 11.2, 0.4, 3.4, 3.4, 22.5, '2e2016')
    m.save('march_horn')


def shard_knife():
    m = Model(DISPLAY_TOOL)
    m.bar(11.2, 11.2, 9.6, 2.6, 0.9, 45, '3a95b4')
    m.bar(12.0, 12.0, 9.0, 0.7, 1.2, 45, '82dbea')
    m.bar(14.4, 14.4, 3.0, 1.4, 0.8, 45, 'e4fbff')
    m.bar(6.3, 6.3, 1.4, 5.4, 1.6, 45, '5b6270')
    m.bar(4.0, 4.0, 4.6, 1.6, 1.6, 45, '7a4a2a')
    m.bar(4.0, 4.0, 0.5, 1.9, 1.9, 45, '3a2110')
    m.bar(3.3, 3.3, 0.5, 1.9, 1.9, 45, '3a2110')
    m.bar(1.7, 1.7, 1.4, 2.1, 2.1, 45, '5b6270')
    m.save('shard_knife')


def fishing_spear():
    m = Model(DISPLAY_TOOL)
    m.bar(6.6, 6.6, 15.6, 1.2, 1.2, 45, '8a5a2b')
    for t in (3.0, 4.2, 5.4):
        m.bar(t, t, 0.5, 1.4, 1.4, 45, '3a2110')
    m.bar(11.4, 11.4, 1.6, 1.8, 1.8, 45, 'cfa64a')
    m.bar(14.0, 14.0, 4.0, 1.0, 0.8, 45, '6ad4d6')
    m.bar(15.6, 15.6, 1.2, 0.5, 0.6, 45, 'd6fbfa')
    m.bar(11.0, 13.6, 3.4, 0.9, 0.7, 45, '25868c', z=8)
    m.bar(13.6, 11.0, 3.4, 0.9, 0.7, 45, '25868c', z=8)
    m.bar(11.6, 15.2, 0.9, 0.7, 0.6, -45, '6ad4d6')
    m.bar(15.2, 11.6, 0.9, 0.7, 0.6, -45, '6ad4d6')
    m.save('fishing_spear')


def bird_rattle():
    m = Model()
    m.cyl(8, 8, 0.95, 0.0, 6.4, '8a5a2b')
    m.cyl(8, 8, 1.4, 4.0, 4.5, '3d2412')
    m.cyl(8, 8, 3.0, 6.4, 7.6, 'b98f4a')
    m.cyl(8, 8, 3.7, 7.6, 11.6, 'd9b46a')
    m.cyl(8, 8, 3.0, 11.6, 12.6, 'b98f4a')
    for a in (0, 90, 180, 270):
        r = math.radians(a)
        cx, cz = 8 + math.cos(r) * 3.5, 8 + math.sin(r) * 3.5
        m.box(cx - 0.35, 7.6, cx + 0.35, 11.6, cz - 0.35, cz + 0.35, 'a83a2c')
    m.cyl(8, 8, 1.1, 6.2, 6.8, '5a3a1e')
    m.bar(9.6, 13.9, 3.4, 0.9, 0.5, 45, '3f9a63')
    m.bar(6.4, 13.9, 3.4, 0.9, 0.5, -45, 'c9463a')
    for x, c in ((3.6, 'c0392b'), (12.4, 'e8e0c8')):
        m.box(x - 0.15, 8.0, x + 0.15, 11.0, 7.85, 8.15, '3d2a18')
        m.box(x - 0.55, 6.6, x + 0.55, 7.8, 7.45, 8.55, c)
    m.save('bird_rattle')


def siren_bead():
    m = Model()
    for y0, y1, r in ((1.6, 2.6, 1.9), (2.6, 3.8, 3.0), (3.8, 6.6, 3.7), (6.6, 7.8, 3.0), (7.8, 8.8, 1.9)):
        m.cyl(8, 8, r, y0, y1, '2fb6a8' if y0 in (3.8,) else '37c8b9')
    m.cyl(8, 8, 3.9, 4.6, 4.9, 'd9b043')
    m.cyl(8, 8, 3.9, 5.7, 6.0, 'd9b043')
    m.cyl(8, 8, 1.0, 8.8, 9.8, 'e2b13c')
    m.bar(5.4, 12.0, 3.6, 0.4, 0.4, 45, '6a4527')
    m.bar(10.6, 12.0, 3.6, 0.4, 0.4, -45, '6a4527')
    m.box(3.6, 13.6, 4.8, 14.8, 7.4, 8.6, '39b7a6')
    m.box(11.2, 13.6, 12.4, 14.8, 7.4, 8.6, '39b7a6')
    m.box(6.0, 6.4, 7.0, 7.4, 11.4, 11.6, 'ffffff')
    m.save('siren_bead')


def sea_shell():
    m = Model()
    for i, a in enumerate((-45, -22.5, 0, 22.5, 45)):
        c = 'f0c9b4' if i % 2 == 0 else 'e0aa98'
        r = math.radians(a)
        L = 8.4 - abs(a) * 0.03
        cx, cy = 8 + math.sin(r) * L / 2, 3.2 + math.cos(r) * L / 2
        m.box(cx - 1.35, cy - L / 2, cx + 1.35, cy + L / 2, 7.3, 8.7, c, rot=-a, axis='z', origin=[cx, cy, 8])
    m.box(6.3, 1.4, 9.7, 3.6, 7.2, 8.8, 'd6a58f')
    m.box(6.8, 2.0, 9.2, 2.6, 8.7, 8.9, 'fff2e6')
    m.save('sea_shell')


def scroll(name, parchment, seal, ribbon, glyph):
    m = Model()
    m.cyl(8, 8, 2.5, 0.6, 15.4, parchment)
    m.cyl(8, 8, 2.15, 0.5, 0.75, '8a6a3a')
    m.cyl(8, 8, 2.15, 15.25, 15.5, '8a6a3a')
    m.cyl(8, 8, 2.8, 3.0, 4.2, ribbon)
    m.cyl(8, 8, 2.8, 11.8, 13.0, ribbon)
    m.cyl(8, 8, 2.9, 7.4, 8.9, ribbon)
    m.box(6.4, 6.6, 9.6, 9.8, 10.2, 11.0, seal)
    m.box(6.9, 7.1, 9.1, 9.3, 10.9, 11.15, glyph)
    m.box(7.5, 7.7, 8.5, 8.7, 11.1, 11.3, seal)
    m.save(name)


def oaths():
    scroll('oath_waste', 'e6cf98', 'c9772a', '8f4a1a', 'f0b96a')
    scroll('oath_tide', 'd7e0cf', '2a9d9a', '1f6f86', '7ee0d8')
    scroll('oath_barrow', 'cfcdb4', '5f7a4a', '3a4a33', 'a7c78a')
    scroll('oath_blackfrost', 'd4e0ec', '3e5fa8', '1e2c58', '9dbcf0')


HAND = ['moon_phial', 'grave_candle', 'watchfire', 'hall_pie', 'road_loaf', 'march_horn', 'shard_knife',
        'fishing_spear', 'bird_rattle', 'siren_bead', 'sea_shell',
        'oath_waste', 'oath_tide', 'oath_barrow', 'oath_blackfrost']


def item_defs():
    os.makedirs(ITEMS26, exist_ok=True)
    for n in HAND:
        d = {
            'model': {
                'type': 'minecraft:select',
                'property': 'minecraft:display_context',
                'cases': [{
                    'when': ['gui', 'fixed', 'on_shelf'],
                    'model': {'type': 'minecraft:model', 'model': 'iceandfire:item/' + n},
                }],
                'fallback': {'type': 'minecraft:model', 'model': 'iceandfire:item/' + n + '_hand'},
            }
        }
        with open(os.path.join(ITEMS26, n + '.json'), 'w', encoding='utf8') as f:
            json.dump(d, f, indent=2)


if __name__ == '__main__':
    for fn in (moon_phial, grave_candle, watchfire, hall_pie, road_loaf, march_horn, shard_knife,
               fishing_spear, bird_rattle, siren_bead, sea_shell, oaths):
        fn()
    img = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    for i, h in enumerate(PALETTE):
        img.putpixel((i % 16, i // 16), tuple(int(h[k:k + 2], 16) for k in (0, 2, 4)) + (255,))
    img.save(os.path.join(ASSETS, 'textures', 'item', 'hand_palette.png'))
    item_defs()
    print(len(PALETTE), 'colours;', len(HAND), 'hand models')
