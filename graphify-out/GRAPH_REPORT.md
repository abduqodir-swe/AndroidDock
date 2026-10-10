# Graph Report - AndroidDock  (2026-10-10)

## Corpus Check
- Large corpus: 195 files · ~766,279 words. Semantic extraction will be expensive (many Claude tokens). Consider running on a subfolder.

## Summary
- 1576 nodes · 2149 edges · 192 communities (144 shown, 48 thin omitted)
- Extraction: 93% EXTRACTED · 7% INFERRED · 0% AMBIGUOUS · INFERRED: 147 edges (avg confidence: 0.85)
- Token cost: unknown (host collaboration tool did not expose input/output usage; no monetary cost estimate).

## Community Hubs (Navigation)
- Charging observer service
- Charging presentation policy
- Dock Editor neighborhood
- Charging State Holder Test neighborhood
- dock packs v1 neighborhood
- Clock View Model neighborhood
- PLAN neighborhood
- Dock Screen neighborhood
- DESIGN neighborhood
- Dock Activity Test neighborhood
- Main Activity neighborhood
- Main Activity neighborhood
- Permission navigation and checks
- Dock Settings neighborhood
- dock pack v1.schema neighborhood
- Dock Settings neighborhood
- dock pack v1.schema neighborhood
- Appearance Editor Test neighborhood
- Automatic Settings Activity neighborhood
- Automatic Store neighborhood
- Photo Store neighborhood
- Dock Screen neighborhood
- Serialized charging events
- Dock Dream Service neighborhood
- dock pack v1.schema neighborhood
- StandBy activity lifecycle
- Dock Editor neighborhood
- Dock Photo Preview neighborhood
- Dock Activity Test neighborhood
- Lock Wallpaper neighborhood
- Dock Settings neighborhood
- PRODUCT neighborhood
- Automatic Store neighborhood
- Clock ticker lifecycle tests
- dock pack v1.schema neighborhood
- Clock State Holder neighborhood
- Dock Application neighborhood
- Dream Owners neighborhood
- Lock Wallpaper Test neighborhood
- appearance implementation test report neighborhood
- Photo landscape capture
- Photo portrait capture
- Large text landscape capture
- Large text portrait capture
- BRIEF neighborhood
- Dock Settings neighborhood
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
- dock pack v1.schema neighborhood
- dock pack v1.schema neighborhood
- dock pack v1.schema neighborhood
- Automatic Settings Test neighborhood
- Dock Settings neighborhood
- Charging state phases
- Digital landscape Compose capture
- Digital portrait Compose capture
- Analog landscape Compose capture
- Analog portrait Compose capture
- Landscape editor Compose capture
- Flip landscape Compose capture
- Flip portrait Compose capture
- Landscape editor settings capture
- dock pack v1.schema neighborhood
- Android sixteen flip capture
- Coroutine clock test scheduling
- Automatic permission destinations
- Intent neighborhood
- Battery Provider neighborhood
- README neighborhood
- Charging presentation effect commands
- Charging pause reasons
- Dock Editor State Holder neighborhood
- Immersive digital landscape capture
- Immersive digital portrait capture
- Immersive landscape activity capture
- Immersive portrait activity capture
- Immersive landscape dream capture
- Immersive portrait dream capture
- StandBy analog landscape evidence
- dock pack v1.schema neighborhood
- Automatic Dock Activity Test neighborhood
- Clock Faces neighborhood
- Automatic Store Test neighborhood
- Dock Settings neighborhood
- Physical reconnect portrait capture
- Locked prototype portrait capture
- Always On device tests
- Screen broadcast signals
- Background theme choices
- Dock Settings neighborhood
- Early landscape activity capture
- Early portrait activity capture
- Initial Compose test failure
- Rotation Compose test evidence
- Rotation Compose test evidence
- Early landscape Dream capture
- Early portrait Dream capture
- Stage two JVM evidence
- dock pack v1.schema neighborhood
- dock pack v1.schema neighborhood
- dock pack v1.schema neighborhood
- Settings loading states
- Rotation instrumentation evidence
- Automatic JVM test evidence
- Automatic JVM test evidence
- Rotation instrumentation evidence
- Rotation instrumentation evidence
- Rotation instrumentation evidence
- Initial UI crash evidence
- dock pack v1.schema neighborhood
- dock pack v1.schema neighborhood
- dock pack v1.schema neighborhood
- dock pack v1.schema neighborhood
- Gradle wrapper functions
- Dock Editor neighborhood
- design neighborhood
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
- dock pack v1.schema neighborhood
- dock pack v1.schema neighborhood
- dock pack v1.schema neighborhood
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
- Charging observer automatic launch
- Firmware DreamService limitation
- Covered window recovery
- StandBy release overview

## God Nodes (most connected - your core abstractions)
1. `ChargingStateHolderTest` - 45 edges
2. `AndroidDock current roadmap` - 29 edges
3. `AndroidDock source-derived design record` - 28 edges
4. `DockSettings` - 23 edges
5. `DockProfile` - 22 edges
6. `Dock Packs v1 draft contract` - 21 edges
7. `AppearanceEditorTest` - 18 edges
8. `DockEditor()` - 17 edges
9. `ClockStateHolder` - 17 edges
10. `DockActivity` - 16 edges

## Surprising Connections (you probably didn't know these)
- `DockEditor()` --calls--> `PhotoFraming`  [INFERRED]
  app/src/main/kotlin/dev/androiddock/prototype/DockEditor.kt → core/model/src/main/kotlin/dev/androiddock/core/model/DockSettings.kt
- `DockScreen()` --calls--> `pixelShiftOffset()`  [INFERRED]
  app/src/main/kotlin/dev/androiddock/prototype/DockScreen.kt → core/model/src/main/kotlin/dev/androiddock/core/model/PixelShift.kt
- `explicitOperationPassesExactPhotoAndCropToLockOnlyAdapter()` --calls--> `PhotoFraming`  [INFERRED]
  app/src/test/kotlin/dev/androiddock/prototype/LockWallpaperTest.kt → core/model/src/main/kotlin/dev/androiddock/core/model/DockSettings.kt
- `supportPolicyAndMissingFileFailBeforeSetting()` --calls--> `PhotoFraming`  [INFERRED]
  app/src/test/kotlin/dev/androiddock/prototype/LockWallpaperTest.kt → core/model/src/main/kotlin/dev/androiddock/core/model/DockSettings.kt
- `zeroIdAndAdapterErrorsAreNeverSuccess()` --calls--> `PhotoFraming`  [INFERRED]
  app/src/test/kotlin/dev/androiddock/prototype/LockWallpaperTest.kt → core/model/src/main/kotlin/dev/androiddock/core/model/DockSettings.kt

## Import Cycles
- None detected.

## Communities (192 total, 48 thin omitted)

### Community 0 - "Charging observer service"
Cohesion: 0.03
Nodes (58): ChargingWatchService [class L15], notification() [fun L243], onBind() [fun L241], onCreate() [fun L44], onDestroy() [fun L216], onReceive() [fun L29], onStartCommand() [fun L211], readEnvironment() [fun L228] (+50 more)

### Community 1 - "Charging presentation policy"
Cohesion: 0.04
Nodes (53): cancelLaunch() [fun L211], closeWindow() [fun L196], disable() [fun L183], eligible() [fun L189], exit() [fun L163], launchFailed() [fun L180], schedule() [fun L190], shown() [fun L155] (+45 more)

### Community 2 - "Dock Editor neighborhood"
Cohesion: 0.04
Nodes (46): ClockStyle.label() [fun L30], DockAccent.label() [fun L32], DockBackground.label() [fun L31], DockMode.label() [fun L28], layoutChange() [fun L63], point() [fun L286], profileChange() [fun L62], widgetChange() [fun L64] (+38 more)

### Community 4 - "dock packs v1 neighborhood"
Cohesion: 0.05
Nodes (42): Draft future import acceptance cases, Draft complete two-profile appearance allowlist, Draft bounded ZIP validation, Draft asset integrity and bounded full decode, Draft durable apply and crash recovery, Draft attribution 512-code-point hard limit, Draft declarative ZIP package, Dock Packs v1 draft contract (+34 more)

### Community 5 - "Clock View Model neighborhood"
Cohesion: 0.05
Nodes (36): beginPhotoPick(), cancelPhotoPick(), PhotoPick, saveEditor(), ClockViewModel [class L19], importPhoto() [fun L104], onCleared() [fun L127], requestLockWallpaper() [fun L53] (+28 more)

### Community 6 - "PLAN neighborhood"
Cohesion: 0.07
Nodes (30): Independent photo crops and explicit lock wallpaper confirmation, Native POCO evidence replaces historical-only validation, Midnight, Studio, Flip and Gallery implementation priority, AndroidDock: идеи и предварительный план, Таймеры, Pomodoro и секундомер, Уведомления, Дополнительные готовые наборы, Фоны (+22 more)

### Community 7 - "Dock Screen neighborhood"
Cohesion: 0.10
Nodes (27): animatable, basictext, border, brush, circleshape, clearandsetsemantics, clip, cos (+19 more)

### Community 8 - "DESIGN neighborhood"
Cohesion: 0.07
Nodes (29): Native editor and display use distinct palettes, Auto size is not verified legibility, Gallery local photo scrim, Source-derived tokens and subsequent native evidence, Secondary, Tertiary, Neutral, Named Rules (+21 more)

### Community 9 - "Dock Activity Test neighborhood"
Cohesion: 0.15
Nodes (23): activityinfo, after, androidjunit4, asandroidbitmap, assumetrue, before, configuration, copyonwritearraylist (+15 more)

### Community 10 - "Main Activity neighborhood"
Cohesion: 0.10
Nodes (24): activityresultcontracts, alertdialog, alignment, Bundle, ComponentActivity, backhandler, build, cancellationexception (+16 more)

### Community 11 - "Main Activity neighborhood"
Cohesion: 0.10
Nodes (12): Bundle, ClockViewModel, ComponentActivity, MainActivity, WindowBrightness(), composable, disposableeffect, sideeffect (+4 more)

### Community 12 - "Permission navigation and checks"
Cohesion: 0.10
Nodes (20): activity, activitynotfoundexception, AutomaticPermissions, Activity.openAutomaticPermission() [fun L31], read() [fun L19], Companion [object L18], batteryUnrestricted [val L17], info [val L32] (+12 more)

### Community 13 - "Dock Settings neighborhood"
Cohesion: 0.15
Nodes (7): DockRepository, DockRepositoryTest, cancelingGalleryPickerLeavesDraftAndAppearanceUntouched(), DockSettings, SettingsCodec, pixelShiftOffset(), DockModelTest

### Community 14 - "dock pack v1.schema neighborhood"
Cohesion: 0.09
Nodes (22): properties, maximum, minimum, type, maximum, minimum, type, const (+14 more)

### Community 15 - "Dock Settings neighborhood"
Cohesion: 0.10
Nodes (18): ClockStyle, ANALOG, DIGITAL, FLIP, DockAccent, AMBER, RED, WHITE (+10 more)

### Community 16 - "dock pack v1.schema neighborhood"
Cohesion: 0.10
Nodes (20): layout, $ref, maximum, minimum, type, enum, type, additionalProperties (+12 more)

### Community 18 - "Automatic Settings Activity neighborhood"
Cohesion: 0.12
Nodes (17): Bundle, onCreate(), AutomaticSettingsActivity [class L27], changeMode() [fun L93], alwaysOn [val L46], enabled [val L45], notifications [val L48], status [val L47] (+9 more)

### Community 19 - "Automatic Store neighborhood"
Cohesion: 0.16
Nodes (4): AutomaticStore, AutomaticStoreTest, ChargingSession, SavedChargingSession

### Community 20 - "Photo Store neighborhood"
Cohesion: 0.14
Nodes (13): bitmap, bitmapfactory, currentcoroutinecontext, dispatchers, ensureactive, exifinterface, file, matrix (+5 more)

### Community 21 - "Dock Screen neighborhood"
Cohesion: 0.21
Nodes (16): BatteryLabel(), color(), configAccentIsColored(), DockPanel(), DockScreen(), Modifier, YearMonth, MonthButton() (+8 more)

### Community 22 - "Serialized charging events"
Cohesion: 0.17
Nodes (11): AutomaticRuntime, ChargingEvent, Disable, Exit, ModesChanged, Refresh, ScreenChanged, Shown (+3 more)

### Community 23 - "Dock Dream Service neighborhood"
Cohesion: 0.18
Nodes (8): DockDreamService, collectasstatewithlifecycle, ComposeView, DreamService, getvalue, setviewtreelifecycleowner, setviewtreesavedstateregistryowner, viewcompositionstrategy

### Community 24 - "dock pack v1.schema neighborhood"
Cohesion: 0.13
Nodes (15): $ref, anyOf, $ref, properties, landscape, photoAsset, portrait, showBattery (+7 more)

### Community 26 - "Dock Editor neighborhood"
Cohesion: 0.25
Nodes (13): Choice(), ClockStyleChoices(), DockEditor(), EditorChoiceChip(), Expansion(), T, label(), PanelEditor() (+5 more)

### Community 27 - "Dock Photo Preview neighborhood"
Cohesion: 0.14
Nodes (13): asimagebitmap, background, box, canvas, cliptobounds, dp, fillmaxsize, imagebitmap (+5 more)

### Community 29 - "Lock Wallpaper neighborhood"
Cohesion: 0.21
Nodes (5): AndroidLockWallpaper, confirmLockWallpaper(), requestLockWallpaper(), LockWallpaperAdapter, LockWallpaperRequest

### Community 30 - "Dock Settings neighborhood"
Cohesion: 0.23
Nodes (8): DockPhotoPreview(), Modifier, currentDockOrientation(), DockBackdrop(), DockOrientation, LANDSCAPE, PORTRAIT, DockProfile

### Community 31 - "PRODUCT neighborhood"
Cohesion: 0.15
Nodes (13): Approved four appearance worlds, Current local appearance implementation scope, AndroidDock, Users, Product Purpose, Operating Context, Capabilities and Constraints, Evidence on Hand (+5 more)

### Community 32 - "Automatic Store neighborhood"
Cohesion: 0.29
Nodes (10): booleanpreferenceskey, catch, coroutinescope, datastore, edit, map, preferences, sharingstarted (+2 more)

### Community 33 - "Clock ticker lifecycle tests"
Cohesion: 0.24
Nodes (3): AutoCloseable, ClockStateHolder, ClockStateHolderTest

### Community 34 - "dock pack v1.schema neighborhood"
Cohesion: 0.17
Nodes (12): enum, type, panel, additionalProperties, properties, required, type, accent (+4 more)

### Community 35 - "Clock State Holder neighborhood"
Cohesion: 0.22
Nodes (8): asstateflow, cancel, coroutinedispatcher, isactive, Job, launch, mutablestateflow, StateFlow

### Community 36 - "Dock Application neighborhood"
Cohesion: 0.24
Nodes (7): BatteryProvider, AppContainer, DockApplication, PhotoStore, Application, preferencesdatastore, supervisorjob

### Community 37 - "Dream Owners neighborhood"
Cohesion: 0.25
Nodes (7): DreamOwners, Lifecycle, LifecycleOwner, lifecycleregistry, SavedStateRegistry, savedstateregistrycontroller, SavedStateRegistryOwner

### Community 38 - "Lock Wallpaper Test neighborhood"
Cohesion: 0.40
Nodes (8): LockWallpaperOperation, confirmationKeepsSnapshotAcrossDraftChangesAndCancelDoesNotSet(), explicitOperationPassesExactPhotoAndCropToLockOnlyAdapter(), FakeAdapter, supportPolicyAndMissingFileFailBeforeSetting(), viewModelReportsFailedOperationAndReleasesBusy(), withPhoto(), zeroIdAndAdapterErrorsAreNeverSuccess()

### Community 39 - "appearance implementation test report neighborhood"
Cohesion: 0.18
Nodes (10): AndroidDock appearance editor: implementation test evidence, Commands and results, - Final integration build: `env ANDROID_HOME=/opt/homebrew/share/android-commandlinetools JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew :core:model:test :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest, - First native run: `adb -s 192.168.0.100:44049 shell am instrument -w -e class dev.androiddock.prototype.DockActivityTest,dev.androiddock.prototype.AppearanceEditorTest dev.androiddock.prototype.test/androidx.test.runner.AndroidJ, - After synchronizing the test fixture and protecting original photo files, a five-method `AppearanceEditorTest` run passed 2 and failed 3 during `@Before`: the Compose Activity was absent or not foreground. The POCO was awake and, - `git diff --check -- app/src/androidTest/kotlin/dev/androiddock/prototype/DockActivityTest.kt app/src/androidTest/kotlin/dev/androiddock/prototype/AppearanceEditorTest.kt docs/evidence/appearance-implementation-test-report.md` —, Device state and limits, The user explicitly authorized a temporary private backup after automatic approval review initially rejected its export. Before native tests, the app's `files` and `shared_prefs` were copied to `/private/tmp` without printing cont (+2 more)

### Community 40 - "Photo landscape capture"
Cohesion: 0.24
Nodes (10): Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Dark blue photo backdrop without identifiable photo subject, Russian date and charging percentage text, Oversized white digital clock with seconds, October 2026 month grid with day 9 highlighted, stage2-photo-landscape screenshot (+2 more)

### Community 41 - "Photo portrait capture"
Cohesion: 0.24
Nodes (10): Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Dark blue photo backdrop without identifiable photo subject, Russian date and charging percentage text, Oversized white digital clock with seconds, October 2026 month grid with day 9 highlighted, stage2-photo-portrait screenshot (+2 more)

### Community 42 - "Large text landscape capture"
Cohesion: 0.24
Nodes (10): Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Russian date and charging percentage text, Oversized white digital clock with seconds, Enlarged labels and calendar text in this capture, October 2026 month grid with day 9 highlighted, stage2-font130-landscape screenshot (+2 more)

### Community 43 - "Large text portrait capture"
Cohesion: 0.24
Nodes (10): Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Russian date and charging percentage text, Oversized white digital clock with seconds, Enlarged labels and calendar text in this capture, October 2026 month grid with day 9 highlighted, stage2-font130-portrait screenshot (+2 more)

### Community 44 - "BRIEF neighborhood"
Cohesion: 0.22
Nodes (8): Historical pre-implementation mockup brief, Lock screen uses photograph without live clock, AndroidDock: варианты оформления и редактор, Направления, Редактор, Платформенная граница обоев, Решение пользователя, Проверка макетов

### Community 45 - "Dock Settings neighborhood"
Cohesion: 0.33
Nodes (3): PhotoCrop, PhotoFraming, AppearancePresetsTest

### Community 46 - "Month grid and battery"
Cohesion: 0.28
Nodes (6): batteryPercent(), YearMonth, MonthGrid, dayofweek, localdate, yearmonth

### Community 47 - "Portrait editor options"
Cohesion: 0.22
Nodes (9): Black high-contrast canvas, Panel 1 color choices: Light, Amber, Red, DUO and SOLO composition options, My screen editor header with Cancel and Save controls, Clock, date, charging and calendar preview, Bedroom and Desktop mode selector, Portrait and Landscape layout selector, compose-root-stage2-editor-portrait screenshot (+1 more)

### Community 48 - "Photo landscape Compose capture"
Cohesion: 0.28
Nodes (9): Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Dark blue photo backdrop without identifiable photo subject, Russian date and charging percentage text, Oversized white digital clock with seconds, October 2026 month grid with day 9 highlighted, compose-root-stage2-photo-landscape screenshot (+1 more)

### Community 49 - "Photo portrait Compose capture"
Cohesion: 0.28
Nodes (9): Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Dark blue photo backdrop without identifiable photo subject, Russian date and charging percentage text, Oversized white digital clock with seconds, October 2026 month grid with day 9 highlighted, compose-root-stage2-photo-portrait screenshot (+1 more)

### Community 50 - "Digital landscape settings capture"
Cohesion: 0.28
Nodes (9): Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Russian date and charging percentage text, Oversized white digital clock with seconds, October 2026 month grid with day 9 highlighted, stage2-activity-landscape screenshot, Landscape clock and calendar side-by-side layout (+1 more)

### Community 51 - "Digital portrait settings capture"
Cohesion: 0.28
Nodes (9): Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Russian date and charging percentage text, Oversized white digital clock with seconds, October 2026 month grid with day 9 highlighted, stage2-activity-portrait screenshot, Portrait clock above date and calendar (+1 more)

### Community 52 - "Analog landscape settings capture"
Cohesion: 0.28
Nodes (9): White analog hands and tick-mark dial, Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Russian date and charging percentage text, October 2026 month grid with day 9 highlighted, stage2-analog-landscape screenshot, Landscape clock and calendar side-by-side layout (+1 more)

### Community 53 - "Analog portrait settings capture"
Cohesion: 0.28
Nodes (9): White analog hands and tick-mark dial, Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Russian date and charging percentage text, October 2026 month grid with day 9 highlighted, stage2-analog-portrait screenshot, Portrait clock above date and calendar (+1 more)

### Community 54 - "Portrait editor settings capture"
Cohesion: 0.22
Nodes (9): Black high-contrast canvas, Panel 1 color choices: Light, Amber, Red, DUO and SOLO composition options, My screen editor header with Cancel and Save controls, Clock, date, charging and calendar preview, Bedroom and Desktop mode selector, Portrait and Landscape layout selector, stage2-editor-portrait screenshot (+1 more)

### Community 55 - "Flip landscape settings capture"
Cohesion: 0.28
Nodes (9): Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Russian date and charging percentage text, Three dark flip-style digit tiles with horizontal split, October 2026 month grid with day 9 highlighted, stage2-flip-landscape screenshot, Landscape clock and calendar side-by-side layout (+1 more)

### Community 56 - "Flip portrait settings capture"
Cohesion: 0.28
Nodes (9): Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Russian date and charging percentage text, Three dark flip-style digit tiles with horizontal split, October 2026 month grid with day 9 highlighted, stage2-flip-portrait screenshot, Portrait clock above date and calendar (+1 more)

### Community 57 - "dock pack v1.schema neighborhood"
Cohesion: 0.22
Nodes (9): additionalProperties, required, type, $defs, asset, provenance, additionalProperties, required (+1 more)

### Community 58 - "dock pack v1.schema neighborhood"
Cohesion: 0.22
Nodes (9): maxLength, minLength, type, maxLength, pattern, type, author, id (+1 more)

### Community 59 - "dock pack v1.schema neighborhood"
Cohesion: 0.22
Nodes (9): $ref, $ref, additionalProperties, properties, required, type, bedroom, desktop (+1 more)

### Community 61 - "Dock Settings neighborhood"
Cohesion: 0.32
Nodes (4): withAppearance(), DockLayout, PanelConfig, DockEditorTest

### Community 62 - "Charging state phases"
Cohesion: 0.25
Nodes (8): ChargingPhase, BLOCKED, DISABLED, LAUNCHING, PENDING, SHOWING, WAIT_FOR_USE, WAITING

### Community 63 - "Digital landscape Compose capture"
Cohesion: 0.32
Nodes (8): Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Russian date and charging percentage text, Oversized white digital clock with seconds, October 2026 month grid with day 9 highlighted, compose-root-stage2-activity-landscape screenshot, Landscape clock and calendar side-by-side layout

### Community 64 - "Digital portrait Compose capture"
Cohesion: 0.32
Nodes (8): Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Russian date and charging percentage text, Oversized white digital clock with seconds, October 2026 month grid with day 9 highlighted, compose-root-stage2-activity-portrait screenshot, Portrait clock above date and calendar

### Community 65 - "Analog landscape Compose capture"
Cohesion: 0.32
Nodes (8): White analog hands and tick-mark dial, Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Russian date and charging percentage text, October 2026 month grid with day 9 highlighted, compose-root-stage2-analog-landscape screenshot, Landscape clock and calendar side-by-side layout

### Community 66 - "Analog portrait Compose capture"
Cohesion: 0.32
Nodes (8): White analog hands and tick-mark dial, Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Russian date and charging percentage text, October 2026 month grid with day 9 highlighted, compose-root-stage2-analog-portrait screenshot, Portrait clock above date and calendar

### Community 67 - "Landscape editor Compose capture"
Cohesion: 0.25
Nodes (8): Black high-contrast canvas, DUO and SOLO composition options, My screen editor header with Cancel and Save controls, Clock, date, charging and calendar preview, Bedroom and Desktop mode selector, Portrait and Landscape layout selector, compose-root-stage2-editor-landscape screenshot, Preview on left and controls on right

### Community 68 - "Flip landscape Compose capture"
Cohesion: 0.32
Nodes (8): Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Russian date and charging percentage text, Three dark flip-style digit tiles with horizontal split, October 2026 month grid with day 9 highlighted, compose-root-stage2-flip-landscape screenshot, Landscape clock and calendar side-by-side layout

### Community 69 - "Flip portrait Compose capture"
Cohesion: 0.32
Nodes (8): Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Russian date and charging percentage text, Three dark flip-style digit tiles with horizontal split, October 2026 month grid with day 9 highlighted, compose-root-stage2-flip-portrait screenshot, Portrait clock above date and calendar

### Community 70 - "Landscape editor settings capture"
Cohesion: 0.25
Nodes (8): Black high-contrast canvas, DUO and SOLO composition options, My screen editor header with Cancel and Save controls, Clock, date, charging and calendar preview, Bedroom and Desktop mode selector, Portrait and Landscape layout selector, stage2-editor-landscape screenshot, Preview on left and controls on right

### Community 71 - "dock pack v1.schema neighborhood"
Cohesion: 0.25
Nodes (8): additionalProperties, required, type, properties, metadata, schemaVersion, const, type

### Community 72 - "Android sixteen flip capture"
Cohesion: 0.32
Nodes (8): Black high-contrast canvas, Rounded bottom panel for mode, edit and screensaver settings, Previous and next month controls around calendar title, Russian date and charging percentage text, Three dark flip-style digit tiles with horizontal split, October 2026 month grid with day 9 highlighted, phone-android16-flip-landscape screenshot, Landscape clock and calendar side-by-side layout

### Community 73 - "Coroutine clock test scheduling"
Cohesion: 0.29
Nodes (6): advancetimeby, assertequals, experimentalcoroutinesapi, runcurrent, runtest, standardtestdispatcher

### Community 74 - "Automatic permission destinations"
Cohesion: 0.29
Nodes (7): AutomaticPermissionPage, APP_INFO, BATTERY, NOTIFICATIONS, OEM_AUTOSTART, OEM_WINDOWS, OVERLAY

### Community 75 - "Intent neighborhood"
Cohesion: 0.29
Nodes (6): AutomaticRestoreReceiver [class L11], onReceive() [fun L12], result [val L14], Intent, log, settings

### Community 76 - "Battery Provider neighborhood"
Cohesion: 0.29
Nodes (6): awaitclose, batterymanager, broadcastreceiver, callbackflow, distinctuntilchanged, intentfilter

### Community 77 - "README neighborhood"
Cohesion: 0.29
Nodes (6): Seven concept boards; raster semantics pending, Mockups are not runtime screenshots, Макеты оформления AndroidDock, Редактор, Границы решения, Оформления

### Community 78 - "Charging presentation effect commands"
Cohesion: 0.29
Nodes (7): ChargingEffect, CLOSE, LAUNCH, LAUNCH_TIMEOUT, RESTORE_WINDOW, ChargingEnvironment, update()

### Community 79 - "Charging pause reasons"
Cohesion: 0.29
Nodes (7): ChargingPause, DISPLAY_OFF, LAUNCH_ERROR, MODE_DISABLED, PERMISSION, RECOVERY, USER_EXIT

### Community 81 - "Immersive digital landscape capture"
Cohesion: 0.38
Nodes (7): Black high-contrast canvas, Russian date and charging percentage text, Oversized white digital clock with seconds, October 2026 month grid with day 9 highlighted, compose-root-stage2-activity-landscape screenshot, Landscape clock and calendar side-by-side layout, Full display without visible app action panel

### Community 82 - "Immersive digital portrait capture"
Cohesion: 0.38
Nodes (7): Black high-contrast canvas, Russian date and charging percentage text, Oversized white digital clock with seconds, October 2026 month grid with day 9 highlighted, compose-root-stage2-activity-portrait screenshot, Portrait clock above date and calendar, Full display without visible app action panel

### Community 83 - "Immersive landscape activity capture"
Cohesion: 0.38
Nodes (7): Black high-contrast canvas, Russian date and charging percentage text, Oversized white digital clock with seconds, October 2026 month grid with day 9 highlighted, stage2-activity-landscape screenshot, Landscape clock and calendar side-by-side layout, Full display without visible app action panel

### Community 84 - "Immersive portrait activity capture"
Cohesion: 0.38
Nodes (7): Black high-contrast canvas, Russian date and charging percentage text, Oversized white digital clock with seconds, October 2026 month grid with day 9 highlighted, stage2-activity-portrait screenshot, Portrait clock above date and calendar, Full display without visible app action panel

### Community 85 - "Immersive landscape dream capture"
Cohesion: 0.38
Nodes (7): Black high-contrast canvas, Russian date and charging percentage text, Oversized white digital clock with seconds, October 2026 month grid with day 9 highlighted, stage2-dream-landscape screenshot, Landscape clock and calendar side-by-side layout, Full display without visible app action panel

### Community 86 - "Immersive portrait dream capture"
Cohesion: 0.38
Nodes (7): Black high-contrast canvas, Russian date and charging percentage text, Oversized white digital clock with seconds, October 2026 month grid with day 9 highlighted, stage2-dream-portrait screenshot, Portrait clock above date and calendar, Full display without visible app action panel

### Community 87 - "StandBy analog landscape evidence"
Cohesion: 0.38
Nodes (7): White analog hands and tick-mark dial, Black high-contrast canvas, Russian date and charging percentage text, October 2026 month grid with day 9 highlighted, standby-034-landscape screenshot, Landscape clock and calendar side-by-side layout, Full display without visible app action panel

### Community 88 - "dock pack v1.schema neighborhood"
Cohesion: 0.29
Nodes (7): maxLength, minLength, type, properties, attribution, remixOf, $ref

### Community 90 - "Clock Faces neighborhood"
Cohesion: 0.73
Nodes (6): AnalogClock(), ClockFace(), FlipCard(), FlipClock(), Color, Modifier

### Community 91 - "Automatic Store Test neighborhood"
Cohesion: 0.53
Nodes (4): files, first, preferencedatastorefactory, test

### Community 92 - "Dock Settings neighborhood"
Cohesion: 0.33
Nodes (6): DockAppearance, FLIP, GALLERY, LEGACY, MIDNIGHT, STUDIO

### Community 93 - "Physical reconnect portrait capture"
Cohesion: 0.47
Nodes (6): Large analog clock with three hands and hour and minute ticks, Black high-contrast canvas, Russian date and charging percentage text, October 2026 month grid with day 9 highlighted, physical-reconnect-portrait screenshot, Portrait clock above date and calendar

### Community 94 - "Locked prototype portrait capture"
Cohesion: 0.47
Nodes (6): Large analog clock with three hands and hour and minute ticks, Black high-contrast canvas, Russian date and charging percentage text, October 2026 month grid with day 9 highlighted, prototype-locked screenshot, Portrait clock above date and calendar

### Community 96 - "Screen broadcast signals"
Cohesion: 0.40
Nodes (5): ChargingScreenSignal, NONE, SCREEN_OFF, SCREEN_ON, USER_PRESENT

### Community 97 - "Background theme choices"
Cohesion: 0.40
Nodes (5): DockBackground, BLACK, BLUE, GRAPHITE, PHOTO

### Community 98 - "Dock Settings neighborhood"
Cohesion: 0.40
Nodes (3): DockMode, BEDROOM, DESKTOP

### Community 99 - "Early landscape activity capture"
Cohesion: 0.40
Nodes (5): Black high-contrast canvas, Oversized white digital clock with seconds, Outlined screensaver settings button, activity-landscape screenshot, Visible Android status and navigation bars

### Community 100 - "Early portrait activity capture"
Cohesion: 0.40
Nodes (5): Black high-contrast canvas, Oversized white digital clock with seconds, Outlined screensaver settings button, activity-portrait screenshot, Visible Android status and navigation bars

### Community 101 - "Initial Compose test failure"
Cohesion: 0.40
Nodes (5): AutomaticSettingsTest, Compose UI test results, DockActivity instrumentation, DockActivityTest, Recorded Initial Compose UI test failure

### Community 102 - "Rotation Compose test evidence"
Cohesion: 0.40
Nodes (5): Activity rotation, AutomaticSettingsTest, Recorded Compose UI test results, DockActivity instrumentation, DockActivityTest

### Community 103 - "Rotation Compose test evidence"
Cohesion: 0.40
Nodes (5): Activity rotation, AutomaticSettingsTest, Recorded Compose UI test results, DockActivity instrumentation, DockActivityTest

### Community 104 - "Early landscape Dream capture"
Cohesion: 0.40
Nodes (5): Black high-contrast canvas, Oversized white digital clock with seconds, Clock-only screen with no visible app controls, Bottom navigation pill, dream-landscape screenshot

### Community 105 - "Early portrait Dream capture"
Cohesion: 0.40
Nodes (5): Black high-contrast canvas, Oversized white digital clock with seconds, Clock-only screen with no visible app controls, Bottom navigation pill, dream-portrait screenshot

### Community 106 - "Stage two JVM evidence"
Cohesion: 0.40
Nodes (5): ClockStateHolderTest, DockModelTest, Gradle build verification, JVM test results, Recorded Stage 2 build summary

### Community 107 - "dock pack v1.schema neighborhood"
Cohesion: 0.40
Nodes (4): additionalProperties, required, title, type

### Community 108 - "dock pack v1.schema neighborhood"
Cohesion: 0.40
Nodes (5): items, maxItems, type, $ref, assets

### Community 109 - "dock pack v1.schema neighborhood"
Cohesion: 0.40
Nodes (5): profile, additionalProperties, allOf, required, type

### Community 110 - "Settings loading states"
Cohesion: 0.50
Nodes (4): Error, Loading, Ready, SettingsState

### Community 111 - "Rotation instrumentation evidence"
Cohesion: 0.50
Nodes (4): Activity rotation, Recorded Compose UI test results, DockActivity instrumentation, DockActivityTest

### Community 112 - "Automatic JVM test evidence"
Cohesion: 0.50
Nodes (4): Automatic StandBy lifecycle, AutomaticStoreTest, DockRepositoryTest, Recorded JVM test results

### Community 113 - "Automatic JVM test evidence"
Cohesion: 0.50
Nodes (4): Automatic StandBy lifecycle, AutomaticStoreTest, DockRepositoryTest, Recorded JVM test results

### Community 114 - "Rotation instrumentation evidence"
Cohesion: 0.50
Nodes (4): Activity rotation, Recorded Compose UI test results, DockActivity instrumentation, DockActivityTest

### Community 115 - "Rotation instrumentation evidence"
Cohesion: 0.50
Nodes (4): Activity rotation, Recorded Compose UI test results, DockActivity instrumentation, DockActivityTest

### Community 116 - "Rotation instrumentation evidence"
Cohesion: 0.50
Nodes (4): Activity rotation, Recorded Compose UI test results, DockActivity instrumentation, DockActivityTest

### Community 117 - "Initial UI crash evidence"
Cohesion: 0.50
Nodes (4): Compose UI test results, DockActivity instrumentation, DockActivityTest, Recorded Initial Stage 2 UI process crash

### Community 118 - "dock pack v1.schema neighborhood"
Cohesion: 0.50
Nodes (4): maximum, minimum, type, firstDayOfWeek

### Community 119 - "dock pack v1.schema neighborhood"
Cohesion: 0.50
Nodes (4): maxLength, minLength, type, license

### Community 120 - "dock pack v1.schema neighborhood"
Cohesion: 0.50
Nodes (4): maxLength, minLength, type, name

### Community 121 - "dock pack v1.schema neighborhood"
Cohesion: 0.50
Nodes (4): textScale, maximum, minimum, type

### Community 122 - "Gradle wrapper functions"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 123 - "Dock Editor neighborhood"
Cohesion: 0.67
Nodes (3): AnalogStyleThumbnail(), Color, Modifier

### Community 124 - "design neighborhood"
Cohesion: 0.67
Nodes (3): Design sidecar components, Design sidecar narrative, AndroidDock structured design sidecar

### Community 125 - "Automatic keyguard launch evidence"
Cohesion: 0.67
Nodes (3): DockActivity instrumentation, Keyguard behavior, Recorded StandBy 0.2.1 final automatic launch

### Community 126 - "Initial autostart failure evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, DockActivity instrumentation, Recorded Initial automatic StandBy run failure

### Community 127 - "Physical power transition evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, DockActivity instrumentation, Recorded Physical power-button transition

### Community 128 - "Repeated lock cycle evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, DockActivity instrumentation, Recorded Repeated StandBy lock cycle

### Community 129 - "Automatic lifecycle evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, Recorded Automatic StandBy lifecycle observation, DockActivity instrumentation

### Community 130 - "Charging reconnection evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, Recorded Charging reconnection cycle, DockActivity instrumentation

### Community 131 - "Observer restart evidence"
Cohesion: 0.67
Nodes (3): Recorded Automatic observer process restart, Automatic StandBy lifecycle, DockActivity instrumentation

### Community 132 - "Keyguard call priority evidence"
Cohesion: 0.67
Nodes (3): Recorded Automatic StandBy keyguard observation, Keyguard behavior, System call priority

### Community 133 - "Automatic lifecycle evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, Recorded Automatic StandBy lifecycle observation, DockActivity instrumentation

### Community 134 - "Compose instrumentation results"
Cohesion: 0.67
Nodes (3): Recorded Compose UI test results, DockActivity instrumentation, DockActivityTest

### Community 135 - "Call priority evidence"
Cohesion: 0.67
Nodes (3): Recorded Call-screen priority over StandBy, DockActivity instrumentation, System call priority

### Community 136 - "FastConnect recovery evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, Recorded FastConnect overlay and StandBy, FastConnect recovery

### Community 137 - "Call priority evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, Recorded Call-screen priority over StandBy, System call priority

### Community 138 - "FastConnect recovery evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, Recorded FastConnect overlay and StandBy, FastConnect recovery

### Community 139 - "Call priority evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, Recorded Call-screen priority over StandBy, System call priority

### Community 140 - "Always On toggle evidence"
Cohesion: 0.67
Nodes (3): Always On mode, Recorded Automatic charging mode toggle, Automatic StandBy lifecycle

### Community 141 - "Power button pause evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, Recorded Physical power-button transition, Power-button display pause

### Community 142 - "Charging reconnection evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, Charging reconnection, Recorded Charging reconnection cycle

### Community 143 - "Repeated relock cycle evidence"
Cohesion: 0.67
Nodes (3): Keyguard behavior, Power-button display pause, Recorded Repeated StandBy lock cycle

### Community 144 - "Landscape rotation evidence"
Cohesion: 0.67
Nodes (3): Activity rotation, Automatic StandBy lifecycle, Recorded Landscape and rotation transition

### Community 145 - "StandBy idle baseline evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, Keyguard behavior, Recorded StandBy idle baseline observation

### Community 146 - "Landscape rotation evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, Keyguard behavior, Recorded Landscape and rotation transition

### Community 147 - "Physical controls evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, Recorded Physical power-button transition, Power-button display pause

### Community 148 - "FastConnect timeout evidence"
Cohesion: 0.67
Nodes (3): Automatic StandBy lifecycle, Recorded FastConnect overlay and StandBy, FastConnect recovery

### Community 149 - "dock pack v1.schema neighborhood"
Cohesion: 0.67
Nodes (3): enum, type, background

### Community 150 - "dock pack v1.schema neighborhood"
Cohesion: 0.67
Nodes (3): enum, type, clockStyle

### Community 151 - "dock pack v1.schema neighborhood"
Cohesion: 0.67
Nodes (3): widget, enum, type

## Knowledge Gaps
- **695 isolated node(s):** `OVERLAY`, `NOTIFICATIONS`, `OEM_WINDOWS`, `OEM_AUTOSTART`, `BATTERY` (+690 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 914 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **48 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `DockProfile` connect `Dock Settings neighborhood` to `Dock Settings neighborhood`, `Clock Faces neighborhood`, `Clock View Model neighborhood`, `Dock Screen neighborhood`, `Lock Wallpaper Test neighborhood`, `Dock Settings neighborhood`, `Dock Settings neighborhood`, `Dock Settings neighborhood`, `Dock Settings neighborhood`, `Dock Screen neighborhood`, `Dock Editor neighborhood`, `Dock Photo Preview neighborhood`, `Lock Wallpaper neighborhood`?**
  _High betweenness centrality (0.030) - this node is a cross-community bridge._
- **Why does `DockSettings` connect `Dock Settings neighborhood` to `Automatic Store neighborhood`, `Dock Settings neighborhood`, `Lock Wallpaper Test neighborhood`, `Dock Settings neighborhood`, `Dock Settings neighborhood`, `Dock Editor State Holder neighborhood`, `Appearance Editor Test neighborhood`, `Dock Screen neighborhood`, `Dock Activity Test neighborhood`, `Dock Settings neighborhood`?**
  _High betweenness centrality (0.023) - this node is a cross-community bridge._
- **Why does `ChargingStateHolderTest` connect `Charging State Holder Test neighborhood` to `Automatic Store neighborhood`, `Charging presentation effect commands`, `Dock Settings neighborhood`?**
  _High betweenness centrality (0.019) - this node is a cross-community bridge._
- **Are the 10 inferred relationships involving `DockSettings` (e.g. with `.committedSettingsSurviveNewStoreAndConcurrentEdits()` and `.invalidSavedSettingsShowErrorAndAreNotOverwritten()`) actually correct?**
  _`DockSettings` has 10 INFERRED edges - model-reasoned connections that need verification._
- **Are the 3 inferred relationships involving `DockProfile` (e.g. with `confirmationKeepsSnapshotAcrossDraftChangesAndCancelDoesNotSet()` and `viewModelReportsFailedOperationAndReleasesBusy()`) actually correct?**
  _`DockProfile` has 3 INFERRED edges - model-reasoned connections that need verification._
- **What connects `OVERLAY`, `NOTIFICATIONS`, `OEM_WINDOWS` to the rest of the system?**
  _695 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Charging observer service` be split into smaller, more focused modules?**
  _Cohesion score 0.03389830508474576 - nodes in this community are weakly interconnected._
## Coverage and integrity limitations

Seven Kotlin files have partial AST from tree-sitter-kotlin 1.1.0. Original-text supplemental declarations add file/declaration edges only, never missing calls. See KOTLIN_COVERAGE.md and .graphify_supplemental.json. 20 evidence JSON files were processed but yielded no structural nodes. Full call-graph coverage is not claimed.

Graph health warning: 383 dangling_endpoint_edges; 7 self_loop_edges; 68 undirected_same_endpoint_collapsed_edges. Full original edge records remain in audit/extraction.json.

Raw health: 383 import edges reference 180 external endpoint IDs. 19 self-call resolver artifacts were quarantined in audit/quarantined-edges.json. Filtered health: 7 self loops. Synthesized import nodes have external=true/type=external; they are references, not extracted declarations. Same-endpoint edge collapse can lose relation/location detail; retain audit/extraction-raw.json and audit/extraction.json for full records.
