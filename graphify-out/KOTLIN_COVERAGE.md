# Kotlin declaration coverage audit

tree-sitter-kotlin 1.1.0 produced partial AST for these seven Kotlin sources. Supplemental inventory reads current original text, masks comments/strings, and records named class/object/function/property/local/constructor binding tokens with exact original lines. Only file → declaration edges are added. Anonymous objects, destructuring and full lexical scopes are outside this limited extractor. No missing calls are reconstructed. Source SHA256 and declaration inventory are retained in `.graphify_supplemental.json`.

| File | Declaration tokens | Added nodes | Functions |
| --- | ---: | ---: | --- |
| app/src/main/kotlin/dev/androiddock/prototype/AutomaticPermissions.kt | 10 | 8 | read L19, Activity.openAutomaticPermission L31 |
| app/src/main/kotlin/dev/androiddock/prototype/AutomaticRestoreReceiver.kt | 3 | 3 | onReceive L12 |
| app/src/main/kotlin/dev/androiddock/prototype/AutomaticSettingsActivity.kt | 11 | 9 | onResume L32, onCreate L42, changeMode L93 |
| app/src/main/kotlin/dev/androiddock/prototype/ChargingWatchService.kt | 55 | 55 | onReceive L29, onCreate L44, onStartCommand L211, onDestroy L216, readEnvironment L228, releaseWakeLock L238, stopFor L239, trace L240, onBind L241, notification L243, start L258 |
| app/src/main/kotlin/dev/androiddock/prototype/ClockViewModel.kt | 39 | 30 | clearNotice L35, beginPhotoPick L37, cancelPhotoPick L44, requestLockWallpaper L46, requestLockWallpaper L53, cancelLockWallpaper L64, confirmLockWallpaper L70, clearError L84, saveEditor L86, importPhoto L104, start L124, stop L125, onCleared L127 |
| app/src/main/kotlin/dev/androiddock/prototype/DockEditor.kt | 51 | 42 | DockMode.label L28, WidgetKind.label L29, ClockStyle.label L30, DockBackground.label L31, DockAccent.label L32, DockEditor L43, profileChange L62, layoutChange L63, widgetChange L64, EditorChoiceChip L239, ClockStyleChoices L246, AnalogStyleThumbnail L281, point L286, WidgetControls L308, Expansion L320, PanelEditor L329, SettingSwitch L350, SettingSlider L359 |
| core/model/src/main/kotlin/dev/androiddock/core/model/ChargingStateHolder.kt | 62 | 53 | update L54, tick L128, shown L155, exit L163, userLeaveHint L167, windowStopped L171, launchFailed L180, disable L183, eligible L189, schedule L190, closeWindow L196, waitForUse L204, cancelLaunch L211 |

All seven call graphs remain incomplete. AutomaticRestoreReceiver and ChargingWatchService originally contained only their file node; their named declarations are now inventoried, but no AST-equivalent behavior guarantee is claimed.
