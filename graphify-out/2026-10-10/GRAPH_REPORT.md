# Graph Report - AndroidDock  (2026-10-10)

## Corpus Check
- 160 files · ~190,283 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 17 file(s) not represented in the graph (top: .tsv 8, (none) 2, .xml 2)

## Summary
- 1199 nodes · 1600 edges · 159 communities (110 shown, 49 thin omitted)
- Extraction: 93% EXTRACTED · 7% INFERRED · 0% AMBIGUOUS · INFERRED: 118 edges (avg confidence: 0.85)
- Token cost: unknown (host collaboration tool did not expose input/output usage; no monetary cost estimate).

## Community Hubs (Navigation)
- Charging observer service
- Charging presentation policy
- Settings profiles and orientation
- Charging lifecycle regression tests
- Serialized events and editor state
- DreamService lifecycle and system bars
- Compose drawing toolkit
- Automatic consent and persistence tests
- Android device instrumentation
- Activity Compose controls
- Activity UI regression tests
- Permission navigation and checks
- Clock ViewModel state
- Dock Packs draft validation contract
- Automatic settings and theme
- Calendar and display widgets
- Reactive preference streams
- StandBy activity lifecycle
- Product roadmap and draft packs
- Editor Compose controls
- Persistence and model tests
- Clock ticker lifecycle tests
- Dock Packs draft JSON Schema
- Editor draft state
- Photo landscape capture
- Photo portrait capture
- Large text landscape capture
- Large text portrait capture
- Month grid and battery
- Portrait editor options
- Photo landscape Compose capture
- Photo portrait Compose capture
- Digital landscape settings capture
- Digital portrait settings capture
- Analog landscape settings capture
- Analog portrait settings capture
- Portrait editor settings capture
- Flip landscape settings capture
- Flip portrait settings capture
- Current product implementation and backlog
- Clock coroutine runtime
- Charging state phases
- Digital landscape Compose capture
- Digital portrait Compose capture
- Analog landscape Compose capture
- Analog portrait Compose capture
- Landscape editor Compose capture
- Flip landscape Compose capture
- Flip portrait Compose capture
- Landscape editor settings capture
- Android sixteen flip capture
- Coroutine clock test scheduling
- Automatic permission destinations
- Consent based boot restore
- Charging presentation effect commands
- Charging pause reasons
- Immersive digital landscape capture
- Immersive digital portrait capture
- Immersive landscape activity capture
- Immersive portrait activity capture
- Immersive landscape dream capture
- Immersive portrait dream capture
- StandBy analog landscape evidence
- Minimal Black draft fixture
- Analog and flip rendering
- Physical reconnect portrait capture
- Locked prototype portrait capture
- Always On device tests
- Screen broadcast signals
- Background theme choices
- Early landscape activity capture
- Early portrait activity capture
- Initial Compose test failure
- Rotation Compose test evidence
- Rotation Compose test evidence
- Early landscape Dream capture
- Early portrait Dream capture
- Stage two JVM evidence
- Settings loading states
- Clock face choices
- Draft atomic import provenance
- Rotation instrumentation evidence
- Automatic JVM test evidence
- Automatic JVM test evidence
- Rotation instrumentation evidence
- Rotation instrumentation evidence
- Rotation instrumentation evidence
- Initial UI crash evidence
- Gradle wrapper functions
- Pixel shift regression policy
- Automatic keyguard launch evidence
- Initial autostart failure evidence
- Physical power transition evidence
- Repeated lock cycle evidence
- Automatic lifecycle evidence
- Charging reconnection evidence
- Observer restart evidence
- Keyguard call priority evidence
- Automatic lifecycle evidence
- Compose instrumentation results
- Call priority evidence
- FastConnect recovery evidence
- Call priority evidence
- FastConnect recovery evidence
- Call priority evidence
- Always On toggle evidence
- Power button pause evidence
- Charging reconnection evidence
- Repeated relock cycle evidence
- Landscape rotation evidence
- StandBy idle baseline evidence
- Landscape rotation evidence
- Physical controls evidence
- FastConnect timeout evidence
- Independent Always On switches
- Android lint build evidence
- Android lint build evidence
- Android lint build evidence
- Android lint build evidence
- Android lint build evidence
- Android lint build evidence
- Observer process evidence
- Android lint build evidence
- Observer process evidence
- DreamService lifecycle evidence
- POCO DreamService configuration
- Reversible shared screen editor
- Launch screen transition policy
- Power button pause
- DreamService integration architecture
- Native Compose architecture
- Minimum SDK candidate
- Android SDK candidate
- Prototype target SDK
- FastConnect window timeout cause
- POCO DreamService autostart limitation
- StandBy device validation
- Shared Activity Dream screen
- Stage two visual evidence
- Always On release evidence
- Serialized charging policy
- Launch error pause
- Always On setup
- Charging autostart setup
- DreamService usage guide
- Manual editor guide
- Implemented photo and static backgrounds
- Bedroom and desktop profiles
- Duo and Solo compositions
- Quiet Night proposed feature
- Portrait primary product scenario
- Charging observer automatic launch
- Firmware DreamService limitation
- Covered window recovery
- StandBy release overview

## God Nodes (most connected - your core abstractions)
1. `ChargingStateHolderTest` - 45 edges
2. `Dock Packs v1 draft contract` - 23 edges
3. `DockSettings` - 18 edges
4. `ClockStateHolder` - 17 edges
5. `DockActivity` - 16 edges
6. `DockScreen()` - 15 edges
7. `DockActivityTest` - 14 edges
8. `AutomaticStore` - 14 edges
9. `Dock Packs v1 draft JSON Schema` - 13 edges
10. `DockEditor()` - 12 edges

## Surprising Connections (you probably didn't know these)
- `DockScreen()` --calls--> `pixelShiftOffset()`  [INFERRED]
  app/src/main/kotlin/dev/androiddock/prototype/DockScreen.kt → core/model/src/main/kotlin/dev/androiddock/core/model/PixelShift.kt
- `Stage 3 Dock Packs draft specification` --references--> `Dock Packs v1 draft contract`  [EXTRACTED]
  PLAN.md → docs/dock-packs-v1.md
- `Dock Packs v1 next-stage draft` --references--> `Dock Packs v1 draft contract`  [EXTRACTED]
  PRODUCT.md → docs/dock-packs-v1.md
- `DockActivityTest` --references--> `DockSettings`  [EXTRACTED]
  app/src/androidTest/kotlin/dev/androiddock/prototype/DockActivityTest.kt → core/model/src/main/kotlin/dev/androiddock/core/model/DockSettings.kt
- `ClockFace()` --references--> `DockProfile`  [EXTRACTED]
  app/src/main/kotlin/dev/androiddock/prototype/ClockFaces.kt → core/model/src/main/kotlin/dev/androiddock/core/model/DockSettings.kt

## Import Cycles
- None detected.

## Communities (159 total, 49 thin omitted)

### Community 0 - "Charging observer service"
Cohesion: 0.03
Nodes (58): ChargingWatchService [class L15], notification() [fun L243], onBind() [fun L241], onCreate() [fun L44], onDestroy() [fun L216], onReceive() [fun L29], onStartCommand() [fun L211], readEnvironment() [fun L228] (+50 more)

### Community 1 - "Charging presentation policy"
Cohesion: 0.04
Nodes (53): cancelLaunch() [fun L211], closeWindow() [fun L196], disable() [fun L183], eligible() [fun L189], exit() [fun L163], launchFailed() [fun L180], schedule() [fun L190], shown() [fun L155] (+45 more)

### Community 2 - "Settings profiles and orientation"
Cohesion: 0.06
Nodes (29): DockRepository, currentDockOrientation(), DockBackdrop(), DockRepositoryTest, DockAccent, AMBER, RED, WHITE (+21 more)

### Community 4 - "Serialized events and editor state"
Cohesion: 0.07
Nodes (27): AutomaticRuntime, ChargingEvent, Disable, Exit, ModesChanged, Refresh, ScreenChanged, Shown (+19 more)

### Community 5 - "DreamService lifecycle and system bars"
Cohesion: 0.07
Nodes (23): DockDreamService, DreamOwners, WindowBrightness(), collectasstatewithlifecycle, composable, ComposeView, disposableeffect, DreamService (+15 more)

### Community 6 - "Compose drawing toolkit"
Cohesion: 0.08
Nodes (32): animatable, asimagebitmap, background, basictext, canvas, circleshape, contentscale, cos (+24 more)

### Community 7 - "Automatic consent and persistence tests"
Cohesion: 0.09
Nodes (8): ActivityScenario, AutomaticDockActivityTest, AutomaticSettingsTest, AutomaticModes, AutomaticStore, AutomaticStoreTest, ChargingSession, SavedChargingSession

### Community 8 - "Android device instrumentation"
Cohesion: 0.14
Nodes (22): activityinfo, after, androidjunit4, asandroidbitmap, assumetrue, before, configuration, copyonwritearraylist (+14 more)

### Community 9 - "Activity Compose controls"
Cohesion: 0.10
Nodes (23): activityresultcontracts, alignment, Bundle, ComponentActivity, backhandler, build, detecttapgestures, dp (+15 more)

### Community 10 - "Activity UI regression tests"
Cohesion: 0.14
Nodes (5): DockActivityTest, Bundle, ComponentActivity, MainActivity, ClockViewModel

### Community 11 - "Permission navigation and checks"
Cohesion: 0.10
Nodes (20): activity, activitynotfoundexception, AutomaticPermissions, Activity.openAutomaticPermission() [fun L31], read() [fun L19], Companion [object L18], batteryUnrestricted [val L17], info [val L32] (+12 more)

### Community 12 - "Clock ViewModel state"
Cohesion: 0.10
Nodes (19): ClockViewModel [class L14], importPhoto() [fun L42], onCleared() [fun L60], start() [fun L57], stop() [fun L58], busy [val L19], draft [val L26], editor [val L17] (+11 more)

### Community 13 - "Dock Packs draft validation contract"
Cohesion: 0.11
Nodes (19): Draft future import acceptance cases, Draft complete two-profile appearance allowlist, Draft bounded ZIP validation, Draft asset integrity and bounded full decode, Draft attribution 512-code-point hard limit, Draft declarative ZIP package, Dock Packs v1 draft contract, Draft runtime and permission exclusion (+11 more)

### Community 14 - "Automatic settings and theme"
Cohesion: 0.12
Nodes (16): Bundle, onCreate(), AutomaticSettingsActivity [class L27], changeMode() [fun L93], alwaysOn [val L46], enabled [val L45], notifications [val L48], status [val L47] (+8 more)

### Community 15 - "Calendar and display widgets"
Cohesion: 0.18
Nodes (16): BatteryLabel(), color(), DockPanel(), DockScreen(), Modifier, YearMonth, MonthButton(), MonthCalendar() (+8 more)

### Community 16 - "Reactive preference streams"
Cohesion: 0.20
Nodes (13): awaitclose, batterymanager, booleanpreferenceskey, callbackflow, catch, coroutinescope, datastore, distinctuntilchanged (+5 more)

### Community 18 - "Product roadmap and draft packs"
Cohesion: 0.14
Nodes (14): AndroidDock 0.3.4 current version, Dock Packs planned portable appearance, Project license not selected, Approved Kotlin Compose stack, Separate portrait landscape layouts, Explicit photo export consent, Remix authorship proposal, AndroidDock roadmap (+6 more)

### Community 19 - "Editor Compose controls"
Cohesion: 0.28
Nodes (12): Choice(), DockEditor(), T, label(), PanelEditor(), SettingSlider(), SettingSwitch(), ClosedFloatingPointRange (+4 more)

### Community 20 - "Persistence and model tests"
Cohesion: 0.23
Nodes (9): DockEditorTest, edit, file, files, first, json, preferencedatastorefactory, stringpreferenceskey (+1 more)

### Community 21 - "Clock ticker lifecycle tests"
Cohesion: 0.24
Nodes (3): AutoCloseable, ClockStateHolder, ClockStateHolderTest

### Community 22 - "Dock Packs draft JSON Schema"
Cohesion: 0.17
Nodes (12): Draft schema asset definition, Draft maximum eight JPEG assets, Draft schema layout definition, Draft required author and license metadata, Draft schema panel definition, Draft PHOTO requires JPEG reference, Draft schema profile definition, Draft bedroom desktop profile requirements (+4 more)

### Community 23 - "Editor draft state"
Cohesion: 0.22
Nodes (4): asstateflow, DockEditorState, DockEditorStateHolder, mutablestateflow

### Community 24 - "Photo landscape capture"
Cohesion: 0.24
Nodes (10): Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Dark blue photo backdrop without identifiable photo subject, Russian date and charging percentage text, Oversized white digital clock with seconds, October 2026 month grid with day 9 highlighted, stage2-photo-landscape screenshot (+2 more)

### Community 25 - "Photo portrait capture"
Cohesion: 0.24
Nodes (10): Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Dark blue photo backdrop without identifiable photo subject, Russian date and charging percentage text, Oversized white digital clock with seconds, October 2026 month grid with day 9 highlighted, stage2-photo-portrait screenshot (+2 more)

### Community 26 - "Large text landscape capture"
Cohesion: 0.24
Nodes (10): Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Russian date and charging percentage text, Oversized white digital clock with seconds, Enlarged labels and calendar text in this capture, October 2026 month grid with day 9 highlighted, stage2-font130-landscape screenshot (+2 more)

### Community 27 - "Large text portrait capture"
Cohesion: 0.24
Nodes (10): Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Russian date and charging percentage text, Oversized white digital clock with seconds, Enlarged labels and calendar text in this capture, October 2026 month grid with day 9 highlighted, stage2-font130-portrait screenshot (+2 more)

### Community 28 - "Month grid and battery"
Cohesion: 0.28
Nodes (6): batteryPercent(), YearMonth, MonthGrid, dayofweek, localdate, yearmonth

### Community 29 - "Portrait editor options"
Cohesion: 0.22
Nodes (9): Black high-contrast canvas, Panel 1 color choices: Light, Amber, Red, DUO and SOLO composition options, My screen editor header with Cancel and Save controls, Clock, date, charging and calendar preview, Bedroom and Desktop mode selector, Portrait and Landscape layout selector, compose-root-stage2-editor-portrait screenshot (+1 more)

### Community 30 - "Photo landscape Compose capture"
Cohesion: 0.28
Nodes (9): Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Dark blue photo backdrop without identifiable photo subject, Russian date and charging percentage text, Oversized white digital clock with seconds, October 2026 month grid with day 9 highlighted, compose-root-stage2-photo-landscape screenshot (+1 more)

### Community 31 - "Photo portrait Compose capture"
Cohesion: 0.28
Nodes (9): Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Dark blue photo backdrop without identifiable photo subject, Russian date and charging percentage text, Oversized white digital clock with seconds, October 2026 month grid with day 9 highlighted, compose-root-stage2-photo-portrait screenshot (+1 more)

### Community 32 - "Digital landscape settings capture"
Cohesion: 0.28
Nodes (9): Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Russian date and charging percentage text, Oversized white digital clock with seconds, October 2026 month grid with day 9 highlighted, stage2-activity-landscape screenshot, Landscape clock and calendar side-by-side layout (+1 more)

### Community 33 - "Digital portrait settings capture"
Cohesion: 0.28
Nodes (9): Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Russian date and charging percentage text, Oversized white digital clock with seconds, October 2026 month grid with day 9 highlighted, stage2-activity-portrait screenshot, Portrait clock above date and calendar (+1 more)

### Community 34 - "Analog landscape settings capture"
Cohesion: 0.28
Nodes (9): White analog hands and tick-mark dial, Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Russian date and charging percentage text, October 2026 month grid with day 9 highlighted, stage2-analog-landscape screenshot, Landscape clock and calendar side-by-side layout (+1 more)

### Community 35 - "Analog portrait settings capture"
Cohesion: 0.28
Nodes (9): White analog hands and tick-mark dial, Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Russian date and charging percentage text, October 2026 month grid with day 9 highlighted, stage2-analog-portrait screenshot, Portrait clock above date and calendar (+1 more)

### Community 36 - "Portrait editor settings capture"
Cohesion: 0.22
Nodes (9): Black high-contrast canvas, Panel 1 color choices: Light, Amber, Red, DUO and SOLO composition options, My screen editor header with Cancel and Save controls, Clock, date, charging and calendar preview, Bedroom and Desktop mode selector, Portrait and Landscape layout selector, stage2-editor-portrait screenshot (+1 more)

### Community 37 - "Flip landscape settings capture"
Cohesion: 0.28
Nodes (9): Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Russian date and charging percentage text, Three dark flip-style digit tiles with horizontal split, October 2026 month grid with day 9 highlighted, stage2-flip-landscape screenshot, Landscape clock and calendar side-by-side layout (+1 more)

### Community 38 - "Flip portrait settings capture"
Cohesion: 0.28
Nodes (9): Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Russian date and charging percentage text, Three dark flip-style digit tiles with horizontal split, October 2026 month grid with day 9 highlighted, stage2-flip-portrait screenshot, Portrait clock above date and calendar (+1 more)

### Community 39 - "Current product implementation and backlog"
Cohesion: 0.22
Nodes (9): Dock Packs v1 next-stage draft, Implemented Stage 2 functionality, Project SDK and license decisions pending, AndroidDock product scope, Bedroom and desk operating scenarios, Historical device and test evidence, Approved native Android stack, Unverified long-run and OEM matrix (+1 more)

### Community 40 - "Clock coroutine runtime"
Cohesion: 0.25
Nodes (7): cancel, coroutinedispatcher, delay, isactive, Job, launch, StateFlow

### Community 41 - "Charging state phases"
Cohesion: 0.25
Nodes (8): ChargingPhase, BLOCKED, DISABLED, LAUNCHING, PENDING, SHOWING, WAIT_FOR_USE, WAITING

### Community 42 - "Digital landscape Compose capture"
Cohesion: 0.32
Nodes (8): Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Russian date and charging percentage text, Oversized white digital clock with seconds, October 2026 month grid with day 9 highlighted, compose-root-stage2-activity-landscape screenshot, Landscape clock and calendar side-by-side layout

### Community 43 - "Digital portrait Compose capture"
Cohesion: 0.32
Nodes (8): Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Russian date and charging percentage text, Oversized white digital clock with seconds, October 2026 month grid with day 9 highlighted, compose-root-stage2-activity-portrait screenshot, Portrait clock above date and calendar

### Community 44 - "Analog landscape Compose capture"
Cohesion: 0.32
Nodes (8): White analog hands and tick-mark dial, Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Russian date and charging percentage text, October 2026 month grid with day 9 highlighted, compose-root-stage2-analog-landscape screenshot, Landscape clock and calendar side-by-side layout

### Community 45 - "Analog portrait Compose capture"
Cohesion: 0.32
Nodes (8): White analog hands and tick-mark dial, Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Russian date and charging percentage text, October 2026 month grid with day 9 highlighted, compose-root-stage2-analog-portrait screenshot, Portrait clock above date and calendar

### Community 46 - "Landscape editor Compose capture"
Cohesion: 0.25
Nodes (8): Black high-contrast canvas, DUO and SOLO composition options, My screen editor header with Cancel and Save controls, Clock, date, charging and calendar preview, Bedroom and Desktop mode selector, Portrait and Landscape layout selector, compose-root-stage2-editor-landscape screenshot, Preview on left and controls on right

### Community 47 - "Flip landscape Compose capture"
Cohesion: 0.32
Nodes (8): Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Russian date and charging percentage text, Three dark flip-style digit tiles with horizontal split, October 2026 month grid with day 9 highlighted, compose-root-stage2-flip-landscape screenshot, Landscape clock and calendar side-by-side layout

### Community 48 - "Flip portrait Compose capture"
Cohesion: 0.32
Nodes (8): Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Russian date and charging percentage text, Three dark flip-style digit tiles with horizontal split, October 2026 month grid with day 9 highlighted, compose-root-stage2-flip-portrait screenshot, Portrait clock above date and calendar

### Community 49 - "Landscape editor settings capture"
Cohesion: 0.25
Nodes (8): Black high-contrast canvas, DUO and SOLO composition options, My screen editor header with Cancel and Save controls, Clock, date, charging and calendar preview, Bedroom and Desktop mode selector, Portrait and Landscape layout selector, stage2-editor-landscape screenshot, Preview on left and controls on right

### Community 50 - "Android sixteen flip capture"
Cohesion: 0.32
Nodes (8): Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Russian date and charging percentage text, Three dark flip-style digit tiles with horizontal split, October 2026 month grid with day 9 highlighted, phone-android16-flip-landscape screenshot, Landscape clock and calendar side-by-side layout

### Community 51 - "Coroutine clock test scheduling"
Cohesion: 0.29
Nodes (6): advancetimeby, assertequals, experimentalcoroutinesapi, runcurrent, runtest, standardtestdispatcher

### Community 52 - "Automatic permission destinations"
Cohesion: 0.29
Nodes (7): AutomaticPermissionPage, APP_INFO, BATTERY, NOTIFICATIONS, OEM_AUTOSTART, OEM_WINDOWS, OVERLAY

### Community 53 - "Consent based boot restore"
Cohesion: 0.29
Nodes (6): AutomaticRestoreReceiver [class L11], onReceive() [fun L12], result [val L14], broadcastreceiver, log, settings

### Community 54 - "Charging presentation effect commands"
Cohesion: 0.29
Nodes (7): ChargingEffect, CLOSE, LAUNCH, LAUNCH_TIMEOUT, RESTORE_WINDOW, ChargingEnvironment, update()

### Community 55 - "Charging pause reasons"
Cohesion: 0.29
Nodes (7): ChargingPause, DISPLAY_OFF, LAUNCH_ERROR, MODE_DISABLED, PERMISSION, RECOVERY, USER_EXIT

### Community 56 - "Immersive digital landscape capture"
Cohesion: 0.38
Nodes (7): Black high-contrast canvas, Russian date and charging percentage text, Oversized white digital clock with seconds, October 2026 month grid with day 9 highlighted, compose-root-stage2-activity-landscape screenshot, Landscape clock and calendar side-by-side layout, Full display without visible app action panel

### Community 57 - "Immersive digital portrait capture"
Cohesion: 0.38
Nodes (7): Black high-contrast canvas, Russian date and charging percentage text, Oversized white digital clock with seconds, October 2026 month grid with day 9 highlighted, compose-root-stage2-activity-portrait screenshot, Portrait clock above date and calendar, Full display without visible app action panel

### Community 58 - "Immersive landscape activity capture"
Cohesion: 0.38
Nodes (7): Black high-contrast canvas, Russian date and charging percentage text, Oversized white digital clock with seconds, October 2026 month grid with day 9 highlighted, stage2-activity-landscape screenshot, Landscape clock and calendar side-by-side layout, Full display without visible app action panel

### Community 59 - "Immersive portrait activity capture"
Cohesion: 0.38
Nodes (7): Black high-contrast canvas, Russian date and charging percentage text, Oversized white digital clock with seconds, October 2026 month grid with day 9 highlighted, stage2-activity-portrait screenshot, Portrait clock above date and calendar, Full display without visible app action panel

### Community 60 - "Immersive landscape dream capture"
Cohesion: 0.38
Nodes (7): Black high-contrast canvas, Russian date and charging percentage text, Oversized white digital clock with seconds, October 2026 month grid with day 9 highlighted, stage2-dream-landscape screenshot, Landscape clock and calendar side-by-side layout, Full display without visible app action panel

### Community 61 - "Immersive portrait dream capture"
Cohesion: 0.38
Nodes (7): Black high-contrast canvas, Russian date and charging percentage text, Oversized white digital clock with seconds, October 2026 month grid with day 9 highlighted, stage2-dream-portrait screenshot, Portrait clock above date and calendar, Full display without visible app action panel

### Community 62 - "StandBy analog landscape evidence"
Cohesion: 0.38
Nodes (7): White analog hands and tick-mark dial, Black high-contrast canvas, Russian date and charging percentage text, October 2026 month grid with day 9 highlighted, standby-034-landscape screenshot, Landscape clock and calendar side-by-side layout, Full display without visible app action panel

### Community 63 - "Minimal Black draft fixture"
Cohesion: 0.29
Nodes (7): Example black profiles without photos, Example empty JPEG asset list, CC0-1.0 example contents license, Minimal Black Dock Packs draft fixture, Minimal Black fixture guide, Future positive ZIP excludes README, Example license distinct from project license

### Community 64 - "Analog and flip rendering"
Cohesion: 0.73
Nodes (6): AnalogClock(), ClockFace(), FlipCard(), FlipClock(), Modifier, Color

### Community 65 - "Physical reconnect portrait capture"
Cohesion: 0.47
Nodes (6): Large analog clock with three hands and hour and minute ticks, Black high-contrast canvas, Russian date and charging percentage text, October 2026 month grid with day 9 highlighted, physical-reconnect-portrait screenshot, Portrait clock above date and calendar

### Community 66 - "Locked prototype portrait capture"
Cohesion: 0.47
Nodes (6): Large analog clock with three hands and hour and minute ticks, Black high-contrast canvas, Russian date and charging percentage text, October 2026 month grid with day 9 highlighted, prototype-locked screenshot, Portrait clock above date and calendar

### Community 68 - "Screen broadcast signals"
Cohesion: 0.40
Nodes (5): ChargingScreenSignal, NONE, SCREEN_OFF, SCREEN_ON, USER_PRESENT

### Community 69 - "Background theme choices"
Cohesion: 0.40
Nodes (5): DockBackground, BLACK, BLUE, GRAPHITE, PHOTO

### Community 70 - "Early landscape activity capture"
Cohesion: 0.40
Nodes (5): Black high-contrast canvas, Oversized white digital clock with seconds, Outlined screensaver settings button, activity-landscape screenshot, Visible Android status and navigation bars

### Community 71 - "Early portrait activity capture"
Cohesion: 0.40
Nodes (5): Black high-contrast canvas, Oversized white digital clock with seconds, Outlined screensaver settings button, activity-portrait screenshot, Visible Android status and navigation bars

### Community 72 - "Initial Compose test failure"
Cohesion: 0.40
Nodes (5): AutomaticSettingsTest, Compose UI test results, DockActivity instrumentation, DockActivityTest, Recorded Initial Compose UI test failure

### Community 73 - "Rotation Compose test evidence"
Cohesion: 0.40
Nodes (5): Activity rotation, AutomaticSettingsTest, Recorded Compose UI test results, DockActivity instrumentation, DockActivityTest

### Community 74 - "Rotation Compose test evidence"
Cohesion: 0.40
Nodes (5): Activity rotation, AutomaticSettingsTest, Recorded Compose UI test results, DockActivity instrumentation, DockActivityTest

### Community 75 - "Early landscape Dream capture"
Cohesion: 0.40
Nodes (5): Black high-contrast canvas, Oversized white digital clock with seconds, Clock-only screen with no visible app controls, Bottom navigation pill, dream-landscape screenshot

### Community 76 - "Early portrait Dream capture"
Cohesion: 0.40
Nodes (5): Black high-contrast canvas, Oversized white digital clock with seconds, Clock-only screen with no visible app controls, Bottom navigation pill, dream-portrait screenshot

### Community 77 - "Stage two JVM evidence"
Cohesion: 0.40
Nodes (5): ClockStateHolderTest, DockModelTest, Gradle build verification, JVM test results, Recorded Stage 2 build summary

### Community 78 - "Settings loading states"
Cohesion: 0.50
Nodes (4): Error, Loading, Ready, SettingsState

### Community 79 - "Clock face choices"
Cohesion: 0.50
Nodes (4): ClockStyle, ANALOG, DIGITAL, FLIP

### Community 80 - "Draft atomic import provenance"
Cohesion: 0.50
Nodes (4): Draft durable apply and crash recovery, Draft private local provenance record, Draft provenance survives restart and editing, Draft settings assets provenance atomic revision

### Community 81 - "Rotation instrumentation evidence"
Cohesion: 0.50
Nodes (4): Activity rotation, Recorded Compose UI test results, DockActivity instrumentation, DockActivityTest

### Community 82 - "Automatic JVM test evidence"
Cohesion: 0.50
Nodes (4): Automatic StandBy lifecycle, AutomaticStoreTest, DockRepositoryTest, Recorded JVM test results

### Community 83 - "Automatic JVM test evidence"
Cohesion: 0.50
Nodes (4): Automatic StandBy lifecycle, AutomaticStoreTest, DockRepositoryTest, Recorded JVM test results

### Community 84 - "Rotation instrumentation evidence"
Cohesion: 0.50
Nodes (4): Activity rotation, Recorded Compose UI test results, DockActivity instrumentation, DockActivityTest

### Community 85 - "Rotation instrumentation evidence"
Cohesion: 0.50
Nodes (4): Activity rotation, Recorded Compose UI test results, DockActivity instrumentation, DockActivityTest

### Community 86 - "Rotation instrumentation evidence"
Cohesion: 0.50
Nodes (4): Activity rotation, Recorded Compose UI test results, DockActivity instrumentation, DockActivityTest

### Community 87 - "Initial UI crash evidence"
Cohesion: 0.50
Nodes (4): Compose UI test results, DockActivity instrumentation, DockActivityTest, Recorded Initial Stage 2 UI process crash

### Community 88 - "Gradle wrapper functions"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 90 - "Automatic keyguard launch evidence"
Cohesion: 0.67
Nodes (3): DockActivity instrumentation, Keyguard behavior, Recorded StandBy 0.2.1 final automatic launch

### Community 91 - "Initial autostart failure evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, DockActivity instrumentation, Recorded Initial automatic StandBy run failure

### Community 92 - "Physical power transition evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, DockActivity instrumentation, Recorded Physical power-button transition

### Community 93 - "Repeated lock cycle evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, DockActivity instrumentation, Recorded Repeated StandBy lock cycle

### Community 94 - "Automatic lifecycle evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, Recorded Automatic StandBy lifecycle observation, DockActivity instrumentation

### Community 95 - "Charging reconnection evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, Recorded Charging reconnection cycle, DockActivity instrumentation

### Community 96 - "Observer restart evidence"
Cohesion: 0.67
Nodes (3): Recorded Automatic observer process restart, Automatic StandBy lifecycle, DockActivity instrumentation

### Community 97 - "Keyguard call priority evidence"
Cohesion: 0.67
Nodes (3): Recorded Automatic StandBy keyguard observation, Keyguard behavior, System call priority

### Community 98 - "Automatic lifecycle evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, Recorded Automatic StandBy lifecycle observation, DockActivity instrumentation

### Community 99 - "Compose instrumentation results"
Cohesion: 0.67
Nodes (3): Recorded Compose UI test results, DockActivity instrumentation, DockActivityTest

### Community 100 - "Call priority evidence"
Cohesion: 0.67
Nodes (3): Recorded Call-screen priority over StandBy, DockActivity instrumentation, System call priority

### Community 101 - "FastConnect recovery evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, Recorded FastConnect overlay and StandBy, FastConnect recovery

### Community 102 - "Call priority evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, Recorded Call-screen priority over StandBy, System call priority

### Community 103 - "FastConnect recovery evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, Recorded FastConnect overlay and StandBy, FastConnect recovery

### Community 104 - "Call priority evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, Recorded Call-screen priority over StandBy, System call priority

### Community 105 - "Always On toggle evidence"
Cohesion: 0.67
Nodes (3): Always On mode, Recorded Automatic charging mode toggle, Automatic StandBy lifecycle

### Community 106 - "Power button pause evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, Recorded Physical power-button transition, Power-button display pause

### Community 107 - "Charging reconnection evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, Charging reconnection, Recorded Charging reconnection cycle

### Community 108 - "Repeated relock cycle evidence"
Cohesion: 0.67
Nodes (3): Keyguard behavior, Power-button display pause, Recorded Repeated StandBy lock cycle

### Community 109 - "Landscape rotation evidence"
Cohesion: 0.67
Nodes (3): Activity rotation, Automatic StandBy lifecycle, Recorded Landscape and rotation transition

### Community 110 - "StandBy idle baseline evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, Keyguard behavior, Recorded StandBy idle baseline observation

### Community 111 - "Landscape rotation evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, Keyguard behavior, Recorded Landscape and rotation transition

### Community 112 - "Physical controls evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, Recorded Physical power-button transition, Power-button display pause

### Community 113 - "FastConnect timeout evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, Recorded FastConnect overlay and StandBy, FastConnect recovery

## Knowledge Gaps
- **465 isolated node(s):** `OVERLAY`, `NOTIFICATIONS`, `OEM_WINDOWS`, `OEM_AUTOSTART`, `BATTERY` (+460 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 680 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **49 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `ChargingStateHolderTest` connect `Charging lifecycle regression tests` to `Persistence and model tests`, `Charging presentation effect commands`, `Automatic consent and persistence tests`?**
  _High betweenness centrality (0.031) - this node is a cross-community bridge._
- **Why does `ChargingSession` connect `Automatic consent and persistence tests` to `Charging presentation policy`, `Charging lifecycle regression tests`, `Android device instrumentation`, `Reactive preference streams`, `Persistence and model tests`?**
  _High betweenness centrality (0.029) - this node is a cross-community bridge._
- **Why does `ChargingStateHolder` connect `Charging lifecycle regression tests` to `Charging observer service`, `Charging presentation policy`, `Persistence and model tests`, `Automatic consent and persistence tests`?**
  _High betweenness centrality (0.028) - this node is a cross-community bridge._
- **Are the 6 inferred relationships involving `DockSettings` (e.g. with `.committedSettingsSurviveNewStoreAndConcurrentEdits()` and `.invalidSavedSettingsShowErrorAndAreNotOverwritten()`) actually correct?**
  _`DockSettings` has 6 INFERRED edges - model-reasoned connections that need verification._
- **Are the 6 inferred relationships involving `ClockStateHolder` (e.g. with `.closingCancelsScopeAndPreventsRestart()` and `.initialStateUsesCurrentWallTime()`) actually correct?**
  _`ClockStateHolder` has 6 INFERRED edges - model-reasoned connections that need verification._
- **What connects `OVERLAY`, `NOTIFICATIONS`, `OEM_WINDOWS` to the rest of the system?**
  _465 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Charging observer service` be split into smaller, more focused modules?**
  _Cohesion score 0.03389830508474576 - nodes in this community are weakly interconnected._
## Coverage and integrity limitations

Six Kotlin files have partial AST from tree-sitter-kotlin 1.1.0. Original-text supplemental declarations add file/declaration edges only, never missing calls. See KOTLIN_COVERAGE.md and .graphify_supplemental.json. 20 evidence JSON files were processed but yielded no structural nodes. Full call-graph coverage is not claimed.

Graph health warning: 325 dangling_endpoint_edges; 39 undirected_same_endpoint_collapsed_edges. Full original edge records remain in audit/extraction.json.

Raw health: 325 import edges reference 161 external endpoint IDs. 24 self-call resolver artifacts were quarantined in audit/quarantined-edges.json. Filtered health: 0 self loops. Synthesized import nodes have external=true/type=external; they are references, not extracted declarations. Same-endpoint edge collapse can lose relation/location detail; retain audit/extraction-raw.json and audit/extraction.json for full records.

## Documentation refresh scope

Dock Packs v1 schema, fixture, import/export, provenance and security rules are DRAFT future-contract documentation, not implemented runtime guarantees. Only the six reviewed documentation files were semantically refreshed; no AST refresh, product changes, Android tests, or raw graphify update occurred. The two new JSON artifacts have semantic stamps only. See audit/dock-packs-refresh.json for source hashes and preservation checks.
