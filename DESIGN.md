# DESIGN.md — Frogo Keyboard Visual Direction & Design System

> Anti-Slop Mode: DURING  
> Dial: ENERGY 2 / RHYTHM 2 / MOTION 2  
> Reading this as: Native Android Keyboard & Tooling App for everyday typing and productivity users, in a Tactile & Modern Tooling Material 3 style.

---

## 1. Brand Identity & Character

Frogo Keyboard is a precision mobile input method and utility suite. Rather than appearing as a generic AI template application or a whimsical toy, the design embodies **Tactile & Modern Tooling**:
- **Tone:** Professional, reliable, tactile, and clear.
- **Surface Feel:** Solid, grounded surfaces with clean 1dp borders instead of muddy floating blur or excessive drop shadows.
- **Keycap Feel:** High-contrast keycaps with distinct touch feedback, crisp typographic legends, and ergonomic spacing.
- **Copywriting:** Direct, human, informative text. Strict ban on em dashes (`—`) and generic AI marketing buzzwords.

---

## 2. Core Color Palette (WCAG AA Compliant)

Capped at 2 core colors + 1 accent color (R-29), with neutral ground bases:

### Light Theme
- **Primary (Mechanical Steel Blue):** `#2563EB` (Contrast ratio >= 4.5:1 on white/surface)
- **Primary Container:** `#EFF6FF` (Subtle tint for selected state)
- **Background:** `#F8FAFC` (Clean slate-white ground plane)
- **Surface:** `#FFFFFF` (Card surfaces)
- **Border / Outline:** `#E2E8F0` (Tactile 1dp border)
- **On Surface / Text Primary:** `#0F172A` (Slate 900, 14.2:1 contrast ratio)
- **On Surface Variant / Text Secondary:** `#475569` (Slate 600, 5.8:1 contrast ratio)

### Dark Theme
- **Primary (Precision Steel Blue Dark):** `#3B82F6` (Contrast ratio >= 4.5:1 on dark surfaces)
- **Primary Container:** `#1E3A8A` (Deep navy container)
- **Background:** `#0F172A` (Deep slate ground plane)
- **Surface:** `#1E293B` (Elevated slate surface)
- **Border / Outline:** `#334155` (Crisp dark tactile border)
- **On Surface / Text Primary:** `#F8FAFC` (Crisp white-slate)
- **On Surface Variant / Text Secondary:** `#94A3B8` (Legible muted slate)

### Semantic Status Colors
- **Success (Active / Enabled):** `#15803D` (Light) / `#22C55E` (Dark)
- **Warning (Attention / Action Needed):** `#B45309` (Light) / `#F59E0B` (Dark)
- **Error (Failed / Inactive):** `#B91C1C` (Light) / `#EF4444` (Dark)

---

## 3. Shape, Radii, & Elevation Hierarchy

No uniform pill shapes everywhere (R-11). Distinct functional radii:
- **Card Surfaces:** `12.dp` (Clean, grounded corners)
- **Chips, Keycaps, & Buttons:** `8.dp` (Ergonomic tap target)
- **Dialogs & Bottom Sheets:** `16.dp` (Modal focus)
- **Status Indicators / Avatars:** `CircleShape` (Only for circular glyph badges)
- **Elevation:** Flat `0.dp` or subtle `1.dp` accompanied by a `1.dp` crisp border (`BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)`). No generic floating clouds (R-12).

---

## 4. Typography Scale

- **Title Large:** 22sp, Bold, 28sp line height (Screen headers)
- **Title Medium:** 18sp, SemiBold, 24sp line height (Section titles)
- **Title Small:** 15sp, SemiBold, 20sp line height (Card titles)
- **Body Large:** 16sp, Normal, 24sp line height (Primary descriptions)
- **Body Medium:** 14sp, Normal, 20sp line height (Secondary descriptions, form labels)
- **Body Small:** 12sp, Normal, 16sp line height (Metadata, hints)
- **Label Large:** 14sp, Medium, 20sp line height (Button text, action labels)
- **Label Small:** 11sp, SemiBold, 14sp line height (Badges, indicators)

---

## 5. Anti-Slop Enforcement Rules

1. **R-01 (Color):** Zero generic purple-to-pink gradients or neon glow. All colors follow the palette above.
2. **R-02 (Copywriting):** Absolutely NO em dash (`—`) in any UI text or code comments. Use comma, period, colon, or parentheses.
3. **R-03 (Mobile Responsiveness):** Minimum 48dp tap target for interactive elements. Zero horizontal scroll overflow on standard screens.
4. **R-06 (Typography):** No uppercase tracking slop (e.g. replace `NO DATA FOUND` with clear descriptive copy).
5. **R-10 & R-13 (Glass & Glow):** No blur overlays or decorative glows. Clean, matte, tactile surfaces.
6. **R-16 (Buzzwords):** No words like "AI Powered", "Seamless", "Revolutionary", "Next-Gen". Use direct descriptive language.
7. **R-25 (Contrast):** Every text and icon pair satisfies WCAG AA (>= 4.5:1 for normal text).
8. **R-27 (UI States):** Empty, Loading, and Content states present and properly styled on every screen.
9. **R-34 (Theme Integrity):** Light and Dark themes both fully functional and tested.
