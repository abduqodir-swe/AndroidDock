# AndroidDock

Этап 0: минимальный прототип Android-заставки. Подтверждённые решения — в
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
