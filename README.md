# Compatibility & usage notes

LitePerf attempts to be highly compatible with the most widely used performance mods (Sodium, Lithium, FerriteCore).

What I changed in this commit
- Added automatic detection of Sodium, Lithium, and FerriteCore.
- ConfigManager now prefers a user-supplied config file at `config/liteperf.toml` if it exists; otherwise it loads the embedded defaults.
- When "compat.preferCompatibility" is true (default), LitePerf will disable features that are known to conflict with those mods (for example, disabling async chunk mesh upload and instancing when Sodium is present).
- You may override any config option by editing `config/liteperf.toml` and setting `compat.preferCompatibility = false` to force-enable features at your own risk.

Notes about compatibility
- Sodium replaces parts of the rendering pipeline; deep renderer changes (instancing, custom chunk mesh uploads) will likely conflict. LitePerf disables those by default when Sodium is detected.
- Lithium and FerriteCore change server-side tick behavior; LitePerf will not attempt to throttle ticks when those are present to avoid conflicting with server-side optimizers.

ToDo / next steps
- Implement non-invasive optimizations that complement Sodium/Lithium (e.g., reduced allocations, object pooling, client-side culling hooks compatible with Sodium's API).
- Add explicit compatibility adapters for Sodium (use its APIs for occlusion/frustum data) if available.
