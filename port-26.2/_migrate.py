"""Copy 26.1 Java files that fail on 26.2 and apply mechanical API replacements."""
from __future__ import annotations

import re
import shutil
from pathlib import Path

ROOT = Path(r"C:\Users\antyu\Desktop\Ice_and_Fire")
LOG = Path(r"C:\Users\antyu\.cursor\projects\c-Users-antyu-Desktop-Ice-and-Fire\agent-tools\219d608a-b666-4d5c-aa00-33e4b7ff5dd1.txt")
SRC = ROOT / "src" / "main" / "java"
DST = ROOT / "port-26.2" / "src" / "main" / "java"


def add_import(text: str, import_line: str) -> str:
    if import_line in text:
        return text
    lines = text.splitlines(True)
    last = 0
    for i, line in enumerate(lines):
        if line.startswith("import "):
            last = i
    lines.insert(last + 1, import_line + "\n")
    return "".join(lines)


def expand_knockback(text: str) -> str:
    out = []
    i = 0
    while True:
        m = re.search(r"([A-Za-z_][\w\.]*)\.knockback\(", text[i:])
        if not m:
            out.append(text[i:])
            break
        start = i + m.start()
        call = i + m.end() - 1  # at '('
        out.append(text[i:start])
        receiver = m.group(1)
        depth = 0
        j = call
        while j < len(text):
            ch = text[j]
            if ch == "(":
                depth += 1
            elif ch == ")":
                depth -= 1
                if depth == 0:
                    break
            j += 1
        args = text[call + 1 : j]
        # count top-level commas
        d = 0
        commas = 0
        for ch in args:
            if ch in "([{":
                d += 1
            elif ch in ")]}":
                d -= 1
            elif ch == "," and d == 0:
                commas += 1
        if commas == 2 and "damageSources" not in args:
            new = f"{receiver}.knockback({args}, {receiver}.damageSources().generic(), 0.0F)"
        else:
            new = f"{receiver}.knockback({args})"
        out.append(new)
        i = j + 1
    return "".join(out)


def patch(text: str, rel: str) -> str:
    text = text.replace("EntityType.LIGHTNING_BOLT", "EntityTypes.LIGHTNING_BOLT")
    text = text.replace("EntityType.ENDERMAN", "EntityTypes.ENDERMAN")
    text = text.replace("Minecraft.getInstance().setScreen(", "Minecraft.getInstance().gui.setScreen(")
    text = text.replace("Blocks.WHITE_WOOL", "Blocks.WOOL.white()")
    text = text.replace("BlockTags.CONCRETE_POWDER", "BlockTags.CONCRETE_POWDERS")
    text = text.replace("VertexFormat.Mode.QUADS", "PrimitiveTopology.QUADS")
    text = text.replace(", null, false, false,", ", null,")
    text = text.replace("EntityType.byString(id)", "BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.parse(id))")
    text = text.replace("net.minecraft.util.Tuple", "com.mojang.datafixers.util.Pair")
    text = text.replace("Tuple<", "Pair<")
    text = re.sub(r"new ParticleRenderType\(\"([^\"]+)\"\)", r'new ParticleRenderType("\1", "IA")', text)
    text = re.sub(r"\n\s*\.bufferSize\(256\)", "", text)
    text = re.sub(r"\n\s*\.withSampler\(\"Sampler1\"\)", "", text)
    text = text.replace("extends StructureProcessor", "implements StructureProcessor")
    text = re.sub(
        r"@Override\s+protected\s+@NotNull\s+StructureProcessorType(?:<[^>]+>)?\s+getType\(\)\s*\{\s*return IafProcessors\.\w+\.get\(\);\s*\}",
        "@Override\n    public MapCodec<? extends StructureProcessor> codec() {\n        return CODEC;\n    }",
        text,
    )
    text = expand_knockback(text)

    if "EntityTypes.LIGHTNING_BOLT" in text or "EntityTypes.ENDERMAN" in text:
        text = add_import(text, "import net.minecraft.world.entity.EntityTypes;")
    if "BuiltInRegistries.ENTITY_TYPE" in text:
        text = add_import(text, "import net.minecraft.core.registries.BuiltInRegistries;")
    if "PrimitiveTopology.QUADS" in text:
        text = add_import(text, "import com.mojang.blaze3d.PrimitiveTopology;")
    if "implements StructureProcessor" in text and "import com.mojang.serialization.MapCodec;" not in text:
        text = add_import(text, "import com.mojang.serialization.MapCodec;")

    # Dragon / myrmex baby flag moved off the now-final isBaby().
    if rel.endswith("EntityDragonBase.java") or rel.endswith("EntityMyrmexBase.java"):
        text = text.replace("public boolean isBaby()", "protected boolean canBeABaby()")

    # Custom geometry instead of removed MultiBufferSource.
    if "MultiBufferSource" in text:
        text = text.replace("import net.minecraft.client.renderer.MultiBufferSource;", "import net.minecraft.client.renderer.SubmitNodeCollector;")
        text = text.replace("MultiBufferSource.BufferSource", "SubmitNodeCollector")
        text = text.replace("MultiBufferSource", "SubmitNodeCollector")

    if rel.endswith("ClientEvents.java"):
        text = text.replace(
            "Minecraft.getInstance().renderBuffers().bufferSource()",
            "event.getNodeCollector()",
        )
    return text


def main() -> None:
    log = LOG.read_text(encoding="utf-8", errors="replace")
    files = sorted(set(re.findall(r"Ice_and_Fire\\src\\main\\java\\(.+?\.java):\d+: error:", log)))
    print("files", len(files))
    for rel in files:
        src = SRC / rel
        dst = DST / rel
        dst.parent.mkdir(parents=True, exist_ok=True)
        raw = src.read_text(encoding="utf-8")
        dst.write_text(patch(raw, rel.replace("\\", "/")), encoding="utf-8")
        print("patched", rel)


if __name__ == "__main__":
    main()
