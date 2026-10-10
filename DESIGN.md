---
name: AndroidDock
description: "Four personal clock worlds with a native dark editor; source-derived implementation record."
colors:
  dock-white: "#F5F3ED"
  dock-amber: "#FFCA80"
  dock-red: "#FF8980"
  studio-sand: "#D9C29B"
  flip-cream: "#F3E6CB"
  flip-tan: "#CBA16A"
  black: "#000000"
  graphite: "#15191D"
  blue-night: "#0C1A28"
  panel: "#17191C"
  flip-card: "#242422"
  legacy-flip-card: "#1B1D20"
  flip-border: "#494943"
  editor-primary: "#F2BD68"
  editor-on-primary: "#251A09"
  editor-surface: "#101214"
  editor-text: "#F2F1EF"
  editor-variant: "#292C30"
  editor-muted: "#C8C8C8"
  editor-selection: "#4B381C"
  editor-on-selection: "#FFDDA3"
  editor-outline: "#8E9196"
typography:
  date:
    fontSize: "16sp"
  period:
    fontSize: "18sp"
  battery:
    fontSize: "14sp"
  battery-large:
    fontSize: "28sp"
rounded:
  square: "0dp"
  today-flip: "8dp"
  flip-card: "12dp"
  panel: "16dp"
spacing:
  compact: "4dp"
  small: "8dp"
  control: "12dp"
  panel-gap: "16dp"
  editor-inline: "20dp"
  portrait-edge: "24dp"
components:
  editor-save:
    backgroundColor: "{colors.editor-primary}"
    textColor: "{colors.editor-on-primary}"
  editor-choice-selected:
    backgroundColor: "{colors.editor-selection}"
    textColor: "{colors.editor-on-selection}"
  dock-panel:
    backgroundColor: "{colors.panel}"
    rounded: "{rounded.panel}"
    padding: "{spacing.small}"
  flip-card:
    backgroundColor: "{colors.flip-card}"
    textColor: "{colors.flip-cream}"
    rounded: "{rounded.flip-card}"
  calendar-today-midnight:
    backgroundColor: "{colors.dock-amber}"
    textColor: "{colors.black}"
  calendar-today-studio:
    backgroundColor: "{colors.studio-sand}"
    textColor: "{colors.black}"
  calendar-today-flip:
    backgroundColor: "{colors.flip-tan}"
    textColor: "{colors.black}"
    rounded: "{rounded.today-flip}"
---
# Design System: AndroidDock

## Overview

**Creative North Star: "Midnight / Studio / Flip / Gallery"**

The four user-approved worlds share a quiet dark display and a readable clock/calendar hierarchy. Midnight uses large pale digits and amber calendar emphasis; Studio uses a numbered analog dial and fine dividers; Flip uses warm graphite blocks; Gallery places the clock and calendar over a personally chosen photograph. These descriptions come from the approved mockup brief; implementation values come from the Compose source.

The editor is a separate native Material 3 workspace with amber actions, compact choices and a persistent preview. Personal customization remains part of the system: the presets establish starting values, while independent panel colors, sizes, arrangements and photo crops can change them.

This record describes the current local implementation, not a shipped release. It was extracted from `DockScreen.kt`, `ClockFaces.kt`, `DockPhotoPreview.kt`, `DockEditor.kt`, `MainActivity.kt`, `DockSettings.kt` and `AppearancePresets.kt`, with `PRODUCT.md` and the approved `docs/design/mockups/2026-10-10/BRIEF.md` supplying product context. Token extraction was source-derived. Subsequently, 14 native POCO captures in [appearance-2026-10-10](docs/evidence/appearance-2026-10-10/) received an independent visual review confirming the four theme identities, both orientations, month content, editor previews and visible Save/Cancel actions. The analog style-selector thumbnail was refined after that review; final confirmation of that refinement and execution details belong to the [implementation evidence report](docs/evidence/appearance-implementation-test-report.md). This record does not independently certify a release. Font scaling, contrast over arbitrary photographs and accessibility remain unverified.

**Key Characteristics:**
- Four distinct clock worlds on a shared dark canvas.
- Orientation-specific panels and photo framing.
- Tonal depth, warm calendar emphasis and optional panel surfaces.
- Native Material 3 editor with explicit choices and fixed save/cancel actions.

## Colors

Pale neutral text and warm highlights sit over black, cool graphite or a dimmed photograph; the editor has its own warmer action palette.

### Primary
- **Editor amber** (`editor-primary`): editor action and selected clock-style border; paired with `editor-on-primary` for filled actions.
- **Dock ivory** (`dock-white`): default display foreground and main host primary color.

### Secondary
- **Dock amber** (`dock-amber`): configurable panel accent and default today emphasis in Midnight and Gallery.
- **Studio sand** (`studio-sand`): today emphasis and the analog center when Studio uses its default ivory foreground.
- **Flip tan / cream** (`flip-tan`, `flip-cream`): today's rounded calendar cell and warm default Flip text.

### Tertiary
- **Soft red** (`dock-red`): explicit user-selected panel/weekend accent.

### Neutral
- **Black / graphite / blue night**: selectable display backdrops. Gallery starts black until a photograph is available.
- **Panel / flip card / legacy flip card**: optional widget surface and distinct clock blocks.
- **Flip border**: fine warm-card outline.
- **Editor surface / variant / text / muted / outline**: workspace background, unselected style tiles, main copy, supporting copy and outlines.
- **Editor selection / on-selection**: selected Material choice containers and their foreground.

### Named Rules
**The Two Palettes Rule.** Keep display accents and editor action colors separate: their roles and source definitions differ.

**The User Accent Rule.** Preserve explicit panel and weekend colors. Preset-specific today colors apply when the panel foreground is neutral; a selected amber or red foreground supplies its own today highlight.

## Typography

**Body Font:** inherited Material 3 typography for editor controls, headings and helper copy; the app does not override `Typography`.
**Label/Mono Font:** no separate bundled label family is defined.

The display uses auto-sized single-line clock and calendar text. Current source selects platform sans-serif for Midnight and warm Flip, serif for Gallery, and monospace for legacy clocks; these system display faces are recorded as implementation drift, not approved reusable display-font tokens. No new font family is inferred from the raster mockups.

### Hierarchy
- **Display:** digital clock uses width-derived auto sizing, capped at 320sp for new appearances and 144sp for legacy. Midnight and warm Flip request bold (700); other digital clocks request normal (400). These are observed limits, not a recommended future type ramp.
- **Title / Body / Label:** editor uses Material `titleLarge`, `titleMedium`, `bodyMedium`, `bodySmall`, `labelLarge` and `labelMedium`. Their inherited library metrics are not redefined as project primitives.
- **Date / Period / Battery:** explicit source sizes are captured in the frontmatter; supporting display date and battery use foreground at 80% opacity.
- **Calendar:** month uses medium (500), with upper size `20 × textScale` sp; date cells in new appearances share that upper size. Weekdays use upper size `12 × textScale` sp. Current auto-size minima of 6sp for weekdays and 8sp for month/dates are not canonized as legible typography.
- **Studio dial:** quarter-hour numerals derive from 7.5% of available side length, clamped to 12–28sp. Its digital readout has upper size `44 × textScale` sp.

### Named Rules
**The Fit Is Not Legibility Rule.** Auto sizing prevents overflow but does not prove legibility; low source minima must not become recommended text sizes.

## Layout

Duo stacks panels vertically in portrait and places them side by side in landscape. Solo fills the available area. The common preset gap is `panel-gap`; user settings allow 0–32dp and a first-panel weight of 0.3–0.7. Non-preview portrait respects safe drawing insets and uses `portrait-edge`; landscape and previews use `small`. Every panel adds `small` inner padding. Clock supporting rows use `control` spacing.

The editor switches layout by its available bounds (`maxWidth > maxHeight`), rather than a fabricated viewport breakpoint. Wide editor composition allocates 42% to preview and 58% to scrollable controls. Tall composition puts preview above controls, capped at 220dp and 38% of available content height. The preview fits an explicitly chosen 9:16 or 16:9 frame; device rotation does not silently change the draft's edited orientation. Preview density is scaled against 400dp portrait / 711dp landscape reference widths; it is a composition aid rather than a device fidelity guarantee.

The top-bar “Раскладка” action opens a native “Раскладка и оформление” dialog for mode, edited orientation and appearance choices. Moving that context out of the tab form leaves space for the main controls in landscape. Controls use 20dp horizontal and 12dp vertical padding with 12dp group spacing. Choice rows use 8dp horizontal and 4dp vertical gaps. Save/cancel stay outside scrolling controls in a bottom row with 16dp horizontal and 8dp vertical padding, separated by 12dp. Interactive editor controls and calendar month arrows request a minimum 48dp target; this is source evidence, not an instrumented accessibility result.

## Elevation & Depth

Display depth comes from tonal surfaces, outlines and photographic dimming. There is no explicit project drop-shadow vocabulary. Material components inherit their library elevation and state behavior; no resolved shadow measurements were collected.

Gallery applies user-controlled full-photo black dimming, defaulting to 32% when its preset is applied, plus a local vertical black scrim of 25% / 60% / 25% behind panels without an opaque surface. Optional panel surfaces use `panel`. Studio retains its native thin dividers: 1dp at 18% white between Duo panels and at 28% foreground below the calendar header. Flip retains its 1dp card outline and 65% black hinge line.

### Named Rules
**The Local Scrim Rule.** Keep Gallery's local readability scrim when a photo-backed panel has no opaque surface; independent global dimming remains adjustable.

## Shapes

Optional display panels use rounded 16dp corners or square corners according to the user's setting. Warm Flip cards are clipped at 12dp, with a fine outline and central hinge. Today's calendar cell is circular in Midnight, Studio and Gallery and softly squared at 8dp in Flip. Calendar cells retain 2dp inset. Editor preview and style tiles inherit Material medium shape; button and chip shapes stay with their native Material implementations, without invented numeric project radii.

## Components

### Buttons
Native filled Save, outlined Cancel/photo/wallpaper and text reset/undo/expansion actions. Save and Cancel share the fixed bottom row. The busy state disables editing and changes Save text to “Подождите…”. Minimum height is 48dp. Ripple, focus and disabled visuals are inherited Material behavior; browser hover rules in the sidecar are illustrative.

### Chips
Native `FilterChip` choices cover mode, edited orientation, appearance, panel contents, accents and backgrounds. Choice rows flow or scroll according to available space. Selected appearance is exposed separately from whether the profile contains personal adjustments.

### Cards / Containers
Clock style choices have equal widths, a 40dp-high illustrative clock and a label, with 6dp inner padding and 4dp vertical spacing. The analog style-selector thumbnail uses its own proportional Canvas dial geometry so that full-display padding does not crowd the small selector; digital and Flip thumbnails reuse `ClockFace`. Selected tiles use the editor selection container with a 2dp primary border; unselected tiles use variant surface and 1dp outline. Preview uses black with 1dp outline. Generic optional display panels and warm Flip cards follow the Shapes and Elevation sections.

### Inputs / Fields
Settings use native Switch and Slider, with visible labels and state descriptions. Slider rows show the current formatted value. Photo zoom ranges from 1× to 3×, crop positions from 0 to 1 and dimming from 0% to 90%. There is no text-field design in this editor to document.

### Navigation
The three native tabs are “Часы”, “Календарь” and “Фон”. Each has a minimum 48dp height and its own scroll state. The selected tab uses Material selection behavior. Calendar previous/next controls use Canvas-drawn chevrons with spoken descriptions and 48dp targets.

### Clock / Calendar / Photo
Clock styles share the display container. Warm Flip changes cards over 350ms using the source tween, gated by the profile animation switch and Android animator availability. Its center hinge and warm tone are native to this chosen world. Calendar dates are derived from actual month data, not raster-reference labels. Today has an explicit semantic description even when its highlight is disabled. Photo preview and display reuse the same orientation-specific crop calculation; missing/decoding/unavailable photos have explicit messages over black.

## Do's and Don'ts

### Do:
- **Do** preserve independent portrait and landscape panel layouts and photo crops.
- **Do** reuse the display and crop implementation in the editor preview.
- **Do** use native Material selection, disabled and focus behavior for editor controls.
- **Do** retain Studio dividers and Flip hinges as materials of their approved worlds.
- **Do** keep system lock-screen wallpaper as an explicit photo-only action with confirmation.

### Don't:
- **Don't** turn observed auto-size minima into recommended readable text sizes.
- **Don't** promote platform display-font fallbacks into the future house type system.
- **Don't** infer device contrast, font scaling or touch accessibility from source inspection or raster mockups.
- **Don't** replace user-selected accents or orientation-specific framing with preset defaults during ordinary edits.
- **Don't** interpret ordinary Save as permission to change system wallpaper.

Not canonized or repaired: platform system display faces and 6–8sp calendar auto-size minima are carried implementation defects/risks, not reusable rules; this documentation-only pass does not modify app code. Native captures support the reviewed theme/layout behavior; arbitrary-photo contrast, large-font behavior and accessibility remain unverified. Final thumbnail confirmation is tracked in the implementation evidence report.
