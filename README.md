# android-compose-juicy-ui

Senior-craft Android UI POC: Jetpack Compose slot-machine demo built around **the feel** - particle bursts on win, custom Canvas glow per symbol, weighted spring physics on win-shake, three-stage reel spin with tick haptics, climactic waveform haptic on win.

This is the kind of micro-interaction work that separates a checkbox Compose app from a category-leading consumer product. Tap, hold, win - every tap should feel like it pushed a button on a high-end console.

## What it shows

- Custom Canvas drawing for per-symbol radial glow.
- `Animatable` + `tween` with `LinearOutSlowInEasing` for reel deceleration; three reels spin with staggered durations (900ms / 1100ms / 1300ms) so the eye stops on each one in sequence.
- `Spring(dampingRatio = HighBouncy)` shake on win, applied through `graphicsLayer { translationX = sin(...) * 12f }`.
- Particle system: ~40 particles spawned on win, integrated each frame with gravity, drawn via Canvas + HSV color, life-decaying alpha.
- Haptics: short tick (`createOneShot`) per reel stop, layered waveform (`createWaveform`) on win - the small dopamine hit.
- `Modifier.graphicsLayer { rotationZ, shadowElevation }` for cheap GPU-side transforms (no recomposition per frame).

## Compose perf notes (the LazyColumn-of-200-items question)

If a `LazyColumn` of 200 items with per-item animated state jankily renders on a Pixel 6a, first three checks in order:

1. **Stability of item lambdas + keys.** Make sure each item has a stable `key` and the lambda passed to `items(...)` does not capture unstable types - otherwise every scroll invalidates every visible item.
2. **Animated state location.** Move `Animatable` / `animateFloatAsState` *inside* the item composable that owns it. State hoisted up at the list level causes the whole list to recompose on every animation tick.
3. **Read state through `graphicsLayer { ... }` lambda, not direct Modifier args.** That defers state reads to the draw phase, skipping recomposition entirely. Same for `Modifier.offset { ... }` vs `Modifier.offset(dp)`.

After those three: `Modifier.drawWithCache`, `derivedStateOf` for filtered scroll positions, and `Recomposer` traces in Layout Inspector.

## Stack

- Kotlin 1.9 + Jetpack Compose BOM 2024.06
- Material 3
- Compose Animation, Canvas, Modifier.graphicsLayer
- VibratorManager / Vibrator + VibrationEffect.createWaveform

## Run

```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

Or open in Android Studio Hedgehog+ and run on a Pixel 6a / API 33+ emulator (haptics work on physical devices).

## Author

Built by Hau (`tranthienhau`).
