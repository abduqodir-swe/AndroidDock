# AndroidDock

Этап 2: первая используемая версия AndroidDock (0.1.0). Подтверждённые решения — в
[PRODUCT.md](PRODUCT.md), предложения — в [PLAN.md](PLAN.md).

Название: AndroidDock. Временный applicationId: `dev.androiddock.prototype`.
Лицензия пока не выбрана.

## Сборка

Нужны JDK 17 и Android SDK Platform 36 / Build Tools 36.0.0.
Задайте `ANDROID_HOME` либо `sdk.dir` в локальном `local.properties`.

```sh
./gradlew assembleDebug test
```

Для прототипа владелец согласовал временные minSdk 26 и targetSdk 36.
Окончательные значения будут выбраны после проверки устройств и записаны
в отдельном ADR. `:core:model` — Kotlin/JVM без Android и Compose.

Инструменты выбраны по документации, проверенной 2026-10-09:
[AGP 9.1](https://developer.android.com/build/releases/agp-9-1-0-release-notes),
[совместимость Kotlin и Gradle](https://kotlinlang.org/docs/gradle-configure-project.html).
AGP использует встроенную поддержку Kotlin; отдельного Kotlin Android plugin нет.

Результаты проверки POCO X7 Pro / Android 16 записываются в
[docs/platform-findings.md](docs/platform-findings.md). Подключение устройства
и успешная сборка сами по себе не подтверждают работоспособность заставки.

На проверенной HyperOS OS3.0.302.0.WOJMIXM работают Activity и штатный предпросмотр
DreamService. Автоматический запуск на зарядке ограничен конфигурацией прошивки
`mDreamsSupportedConfig=false`: в проверенном простое включается системный AOD.
Прототип это ограничение не обходит. Подробности и доказательства — в findings.

## Запуск

```sh
adb -s <serial> install -r app/build/outputs/apk/debug/app-debug.apk
adb -s <serial> shell am start -n dev.androiddock.prototype/.MainActivity
```

В приложении нажмите «Настройки заставки», включите системную заставку и
выберите AndroidDock. Предпросмотр и автоматический запуск при простое на
зарядке — отдельные проверки. При нескольких записях одного беспроводного
телефона выбирайте один конкретный serial.

В Activity касание открывает управление, долгое нажатие — редактор. Кнопка
«Управление» также доступна постоянно. Выберите «Спальня»/«Стол» или нажмите
«Настроить». Управление скрывается после 8 секунд; Back выходит из приложения,
а внутри редактора отменяет несохранённые изменения. Заставка неинтерактивна:
первое касание/Back завершает её, редактирование выполняется в Activity.

В редакторе выберите режим и ориентацию: Duo (две панели) или Solo (одна),
часы/календарь/батарея в каждой панели, пропорции, отступы, цвет, размер и
подложку. Есть цифровые, аналоговые и перекидные часы, 12/24 часа, секунды,
дата, первый день недели, чёрный/графитовый/синий фон и пользовательская
фотография. Фото выбирается системным диалогом, копируется приватно и
уменьшается до 2048 px; доступ ко всей галерее не нужен.

Предпросмотр переключается между портретом и ландшафтом. «Сохранить» применяет
черновик; «Отмена» и Undo позволяют отказаться от изменений. Настройки режима
общие для двух ориентаций; состав/пропорции/оформление панелей независимы.
После поворота и перезапуска сохранённый выбор восстанавливается из DataStore.
Календарь не читает личные события; перелистывание доступно при открытом
управлении, «Сегодня» возвращает текущий месяц.

Ночная яркость включается вручную, затрагивает только окно AndroidDock.
В редакторе действует системная яркость для удобства настройки. Анимацию часов
можно отключить; учитывается системное отключение аниматоров. Pixel-shift
сдвигает содержимое на ±2 физических пикселя раз в минуту в обоих хостах.
Эти меры не гарантируют отсутствие выгорания. Расписаний, датчика освещения,
Dock Packs и интеграций этапов 4–5 в этой версии нет.

Технические решения текущей реализации — [docs/stage2-implementation.md](docs/stage2-implementation.md).
Это не утверждение всех рекомендаций PLAN.md.

## Compose в DreamService

Каждое подключение окна получает DreamOwners с LifecycleRegistry и
SavedStateRegistryController. `performAttach()` и `performRestore(null)` вызываются
до создания композиции. На ComposeView установлены ViewTreeLifecycleOwner и
ViewTreeSavedStateRegistryOwner; ViewModelStoreOwner не нужен, потому что DockScreen
не создаёт ViewModel. Activity использует тонкий ClockViewModel, переживающий поворот.

Lifecycle заставки: CREATED при подключении, RESUMED при старте, CREATED при
остановке, DESTROYED при отсоединении. Стратегия
DisposeOnViewTreeLifecycleDestroyed и явный disposeComposition освобождают
композицию; ClockStateHolder закрывает собственный coroutine scope. Saved-state
registry обеспечивает контракт Compose. Постоянные настройки читает общий
Application-scoped DataStore с JSON; время берётся из системных часов.
Данные батареи наблюдаются только при наличии подписчиков. ViewModel внутри
DreamService не создаётся. Формат Dock Packs и импорт/экспорт не реализованы.

## Проверки

JVM-тесты часов, календаря, редактора, pixel-shift и сохранения: `./gradlew test`.
UI-тест на одном конкретном устройстве:

```sh
./gradlew assembleDebugAndroidTest
adb -s <serial> install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb -s <serial> shell am instrument -w dev.androiddock.prototype.test/androidx.test.runner.AndroidJUnitRunner
```

На HyperOS может потребоваться подтверждение установки тестового APK на телефоне.
UI-тесты проверяют ход часов и геометрию Duo (портрет первым, ландшафт вторым),
пересоздание Activity и сохранение ViewModel, сохранение Solo/первого дня недели,
независимость ориентаций, отмену редактора, три циферблата, яркость и фото-фон
на синтетическом изображении. Сохранение после нового процесса DataStore
проверяется отдельно JVM-тестом; аппаратные результаты записаны в findings. Результаты и нативные снимки —
в [docs/evidence](docs/evidence). Предложение по SDK — [ADR-002](docs/decisions/ADR-002.md).
