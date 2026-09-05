# LitePerf

LitePerf — a lightweight client-side Fabric mod focused on performance improvements for Minecraft 1.26.2.

This repository contains the initial project scaffold and Phase 1 optimizations (placeholders and basic wiring).

Build instructions (development):
- Install JDK 17+
- ./gradlew build
- ./gradlew runClient

Runtime instructions:
- Build the mod jar with `./gradlew build` and copy the produced jar plus Fabric API (+26.1.2) into your Minecraft `mods/` folder.

Notes:
- Experimental features (instancing, frameGeneration) are disabled by default in `src/main/resources/liteperf.toml`.
- Many rendering-level optimizations require mixins and deeper Fabric Loom mappings; this scaffold provides the structure and safe default config for further development.
