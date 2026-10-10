# AndroidDock: raster theme concepts

Four concept boards requested: Midnight, Studio, Flip, Gallery. Each contains portrait and landscape Android StandBy screens. Generated with built-in image_gen; no application implementation.

Reference images inspected: `docs/evidence/stage2-captures/compose-root-stage2-photo-portrait.png` and `.impeccable/review/phone-android16-flip-landscape.png`. Existing captures establish clock/calendar structure, not the requested new visual directions.

Synthetic content: 10:08, Saturday 10 October 2026, Monday-first October calendar; only day 10 selected. Generated calendar and lettering must be checked; raster generation cannot guarantee exact numeric or text output. Implementation must use deterministic localized calendar data.

## Exact generation prompts

## Observed raster QA

All four saved PNGs were opened with `view_image`. Each board visibly has one flat portrait Android screen and one flat landscape Android screen; portrait stacks clock and calendar, landscape places them side by side. All show 10:08 (Flip as separate 10 and 08 panels), Saturday 10 October, October 2026, charging 74%, Monday-first weekdays, complete five-row month grid, day 10 selected. No weather, events, integrations, editor controls, browser chrome, perspective or iPhone notch observed.

- Midnight: pure black screen appearance, large white digits, amber day/charging accents; direct calendar on black.
- Studio: restrained analog dial, digital 10:08 backup, orderly calendar and subtle dividers. Analog hands appear near 10:08 but minute-hand angle is approximate; do not derive timing behavior from this raster. Generator added an unrequested decorative board-only tagline above landscape screen.
- Flip: warm ivory split-flap digits, graphite panels and center seams; calendar consistent between orientations. Generator added a board-only English product subtitle outside the screens.
- Gallery: mountain photo visible between content blocks, white serif time, readable calendar over a localized darkened region; same mountain composition adapted with different crops.

Calendar digits were visually inspected and appear correct on these boards, but image generation does not guarantee exact localized text/calendar data or accessible contrast across future background photos. These are concept evidence, not device validation or implementation. No browser, build, tests, commit or push performed.

### midnight

```text
Use case: ui-mockup
Asset type: high fidelity raster concept board for AndroidDock, native Android StandBy clock plus month calendar.
Composition: one polished wide flat board, neutral near-black exterior canvas, two orthographic screen rectangles side by side, left a tall portrait Android screen approximately 9:20, right a wide landscape Android screen approximately 20:9, aligned on their vertical centers. Thin understated device edges only, no perspective, no hand, no physical scene. Make screens large and legible with restrained board margins. Board heading at top, Russian orientation labels "Портрет" and "Альбом" outside the screens. No iPhone notch or Dynamic Island, no iOS chrome, no browser, no editor toolbar. Screen controls hidden.
Identical content in both screens: time 10:08, date "Суббота, 10 октября", month "Октябрь 2026", discreet "74% · Заряжается". Portrait adapts by stacking clock upper half and calendar lower half. Landscape adapts into clock left and calendar right, no stretched duplicate.
Calendar exact Monday-first seven columns "ПН ВТ СР ЧТ ПТ СБ ВС". Five week rows, blank cells are empty: [blank blank blank 1 2 3 4]; [5 6 7 8 9 10 11]; [12 13 14 15 16 17 18]; [19 20 21 22 23 24 25]; [26 27 28 29 30 31 blank]. Highlight only 10 in Saturday column. Never duplicate numbers. No events, weather, integrations, cards for other functions, decorative charts, extra time or seconds.
Typography crisp and deliberate, readable calendar, consistent content and design across orientations.
Theme direction: Board heading "Midnight · Полночь". Pure #000000 AMOLED screens, gigantic clean white digital 10:08 numerals, confident geometric sans serif with tabular numerals. White calendar numerals, subdued gray weekdays/date, very small amber highlight on selected day 10 and tiny charging indicator. All elements directly on black with no card containers, no glow, no gradients. Bold minimalist quiet night clock.
```

### studio

```text
Use case: ui-mockup
Asset type: high fidelity raster concept board for AndroidDock, native Android StandBy clock plus month calendar.
Composition: one polished wide flat board, neutral near-black exterior canvas, two orthographic screen rectangles side by side, left a tall portrait Android screen approximately 9:20, right a wide landscape Android screen approximately 20:9, aligned on their vertical centers. Thin understated device edges only, no perspective, no hand, no physical scene. Make screens large and legible with restrained board margins. Board heading at top, Russian orientation labels "Портрет" and "Альбом" outside the screens. No iPhone notch or Dynamic Island, no iOS chrome, no browser, no editor toolbar. Screen controls hidden.
Identical content in both screens: time 10:08, date "Суббота, 10 октября", month "Октябрь 2026", discreet "74% · Заряжается". Portrait adapts by stacking clock upper half and calendar lower half. Landscape adapts into clock left and calendar right, no stretched duplicate.
Calendar exact Monday-first seven columns "ПН ВТ СР ЧТ ПТ СБ ВС". Five week rows, blank cells are empty: [blank blank blank 1 2 3 4]; [5 6 7 8 9 10 11]; [12 13 14 15 16 17 18]; [19 20 21 22 23 24 25]; [26 27 28 29 30 31 blank]. Highlight only 10 in Saturday column. Never duplicate numbers. No events, weather, integrations, cards for other functions, decorative charts, extra time or seconds.
Typography crisp and deliberate, readable calendar, consistent content and design across orientations.
Theme direction: Board heading "Studio · Студия". Refined disciplined Swiss-inspired dark typography, deep charcoal native screens. Large elegant analog dial with thin precise ivory tick marks, subtle numerals only 12,3,6,9, white hour and minute hands exactly showing 10:08, tiny muted warm accent center. Discreet numeric 10:08 beneath dial so exact time is unambiguous. Structured month calendar with carefully aligned tabular numerals, slender divider line under month name, gently highlighted selected day 10. No faux materials, no luminous glow. Editorial restraint and precise spacing.
```

### flip

```text
Use case: ui-mockup
Asset type: high fidelity raster concept board for AndroidDock, native Android StandBy clock plus month calendar.
Composition: one polished wide flat board, neutral near-black exterior canvas, two orthographic screen rectangles side by side, left a tall portrait Android screen approximately 9:20, right a wide landscape Android screen approximately 20:9, aligned on their vertical centers. Thin understated device edges only, no perspective, no hand, no physical scene. Make screens large and legible with restrained board margins. Board heading at top, Russian orientation labels "Портрет" and "Альбом" outside the screens. No iPhone notch or Dynamic Island, no iOS chrome, no browser, no editor toolbar. Screen controls hidden.
Identical content in both screens: time 10:08, date "Суббота, 10 октября", month "Октябрь 2026", discreet "74% · Заряжается". Portrait adapts by stacking clock upper half and calendar lower half. Landscape adapts into clock left and calendar right, no stretched duplicate.
Calendar exact Monday-first seven columns "ПН ВТ СР ЧТ ПТ СБ ВС". Five week rows, blank cells are empty: [blank blank blank 1 2 3 4]; [5 6 7 8 9 10 11]; [12 13 14 15 16 17 18]; [19 20 21 22 23 24 25]; [26 27 28 29 30 31 blank]. Highlight only 10 in Saturday column. Never duplicate numbers. No events, weather, integrations, cards for other functions, decorative charts, extra time or seconds.
Typography crisp and deliberate, readable calendar, consistent content and design across orientations.
Theme direction: Board heading "Flip · Перекидные". Restrained retro split-flap clock, two graphite mechanical panels displaying warm ivory "10" and "08", fine horizontal seams midway across each digit group, tiny realistic hinge cues, flat surfaces, restrained shadow, no excessive skeuomorphism. Warm ivory calendar on charcoal, muted weekdays, compact warm highlight around 10. Portrait two large clock panels horizontally above calendar; landscape clock panels at left and month at right. No seconds, no novelty vintage props.
```

### gallery

```text
Use case: ui-mockup
Asset type: high fidelity raster concept board for AndroidDock, native Android StandBy clock plus month calendar.
Composition: one polished wide flat board, neutral near-black exterior canvas, two orthographic screen rectangles side by side, left a tall portrait Android screen approximately 9:20, right a wide landscape Android screen approximately 20:9, aligned on their vertical centers. Thin understated device edges only, no perspective, no hand, no physical scene. Make screens large and legible with restrained board margins. Board heading at top, Russian orientation labels "Портрет" and "Альбом" outside the screens. No iPhone notch or Dynamic Island, no iOS chrome, no browser, no editor toolbar. Screen controls hidden.
Identical content in both screens: time 10:08, date "Суббота, 10 октября", month "Октябрь 2026", discreet "74% · Заряжается". Portrait adapts by stacking clock upper half and calendar lower half. Landscape adapts into clock left and calendar right, no stretched duplicate.
Calendar exact Monday-first seven columns "ПН ВТ СР ЧТ ПТ СБ ВС". Five week rows, blank cells are empty: [blank blank blank 1 2 3 4]; [5 6 7 8 9 10 11]; [12 13 14 15 16 17 18]; [19 20 21 22 23 24 25]; [26 27 28 29 30 31 blank]. Highlight only 10 in Saturday column. Never duplicate numbers. No events, weather, integrations, cards for other functions, decorative charts, extra time or seconds.
Typography crisp and deliberate, readable calendar, consistent content and design across orientations.
Theme direction: Board heading "Gallery · Галерея". Beautiful atmospheric mountain photograph fills each screen edge to edge: distant layered rugged peaks at blue hour, slate navy sky, low cloud, subtle warm light on a ridge, cinematic natural photographic realism. Crop the same scene appropriately for each orientation. Huge refined white digital 10:08 and readable white month calendar, localized subtle dark scrims behind clock and calendar that smoothly blend into the photo and preserve scenic visibility. Selected day 10 a restrained warm ivory disk. Landscape ridge remains visible between and beneath clock/calendar; portrait mountain vista visible between upper clock and lower calendar. No boxed opaque cards, no neon, no bright busy backdrop.
```

