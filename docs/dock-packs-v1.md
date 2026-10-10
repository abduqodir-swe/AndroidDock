# Dock Packs v1 — черновой контракт для ревью

Дата: 2026-10-10. Спецификация, схема и пример подготовлены; импорт/экспорт **не реализованы**. Численные лимиты ниже предложены этим черновиком, не являются прежними решениями владельца. Реализация и публикация требуют отдельного поручения.

## Назначение и границы

Декларативный ZIP переносит полный снимок поддерживаемого оформления двух профилей и двух ориентаций. Снимок полон в пределах export allowlist ниже; поведение устройства и личные настройки не переносит. Пакет содержит root `pack.json` и только перечисленные JPEG-ассеты. Код, скрипты, HTML, SVG, шрифты, видео, анимация, плагины, URI, сетевые ссылки и внешние ресурсы не допускаются. Чтение, предпросмотр и применение не выполняют сетевых запросов.

Контракт отдельный от `SettingsCodec.schemaVersion=1`: совпадение числа не означает совместимость. Локальный codec допускает неизвестные ключи; пакет строго отклоняет их на каждом уровне. Поддерживается только `schemaVersion=1`; неизвестная версия отклоняется до применения, без молчаливого downgrade, подстановки дефолтов или частичного импорта. Миграций v1 пока нет.

Источники текущих фактов: [DockSettings.kt](../core/model/src/main/kotlin/dev/androiddock/core/model/DockSettings.kt) (enum, поля, validated, SettingsCodec), [PhotoStore.kt](../app/src/main/kotlin/dev/androiddock/prototype/PhotoStore.kt) (приватное JPEG-хранение и resolve), [DockRepository.kt](../app/src/main/kotlin/dev/androiddock/prototype/DockRepository.kt) (локальные настройки), [README](../README.md) и [платформенные результаты](platform-findings.md). Требования будущего транзакционного импорта ниже не описывают существующие гарантии PhotoStore/DataStore.

## Совместимость и mapping

Все allowlisted поля обязательны, включая скрытую вторую панель Solo: при последующем переключении Duo результат определён. Обе ориентации независимы; настройки профиля общие для его ориентаций.

| Пакет / существующая модель | v1 |
| --- | --- |
| `profiles.bedroom`, `profiles.desktop` → DockSettings profiles | Обязательны оба; импорт заменяет allowlisted оформление обоих |
| `portrait`, `landscape` → DockLayout | Обязательны обе; `kind`: DUO/SOLO |
| `first`, `second` → PanelConfig | widget CLOCK/CALENDAR/BATTERY, accent WHITE/AMBER/RED, textScale 0.7–1.3, surface/rounded boolean |
| DockLayout `firstWeight`, `spacing` | 0.3–0.7, целое 0–32; сохраняются также для Solo |
| DockProfile `clockStyle` | DIGITAL/ANALOG/FLIP |
| `use24Hour`, `showSeconds`, `showDate`, `showBattery` | Boolean; только правила отображения, без фактического времени/батареи |
| `firstDayOfWeek` | ISO 1–7; календарная сетка без событий |
| `background` | BLACK/GRAPHITE/BLUE/PHOTO; фиксированные цвета модели |
| `photoAsset` → новый приватный `photoPath` | PHOTO требует относительную ссылку на объявленный JPEG; остальные требуют null |
| Grid, новые виджеты/цвета, градиенты, crop/blur, панельные фото, day/night variants | Не поддерживаются v1; остаются backlog PLAN, поля отклоняются |

**Export allowlist:** только schemaVersion пакета, metadata, profiles с перечисленными выше полями, assets с описаниями байтов. [Схема](schemas/dock-pack-v1.schema.json) — точный машинный список. Никаких свободных extensions/config objects.

**Local preservation:** DockSettings.schemaVersion и mode; в каждом принимающем профиле brightness, nightBrightness, nightEnabled, animations, pixelShift сохраняются. Они не входят в пакет, даже при полном snapshot оформления. Импорт не меняет выбранный активный режим и системную яркость. На новом устройстве берутся его локальные значения/дефолты. Старый photoPath заменяется новым только для PHOTO; для остальных очищается в новой конфигурации. Старые файлы не удаляются до успешного commit и проверки ссылок всех локальных профилей/черновиков.

Никогда не экспортируются и не применяются permissions, автозапуск/Always On consent, исключения оптимизации батареи, ChargingSession/token/паузы и прочее runtime state, notification contents, календарные события, учётные данные, идентификаторы устройства, пути/URI источников фото. Пакет не запрашивает и не выдаёт разрешения.

## Фото, metadata и авторство

Каждый экспорт требует отдельного явного opt-in для конкретных выбранных личных фото; галочка не сохраняется между экспортами. По умолчанию фото отсутствуют. Для каждого PHOTO-профиля с невыбранным/недоступным фото экспорт **явно преобразует** копию профиля в BLACK + photoAsset=null и показывает это в предпросмотре экспорта; исходные настройки не меняются. Фото нельзя тихо заменить чужим локальным фото или оставить сломанную PHOTO-ссылку. Один выбранный JPEG может использоваться обоими профилями; неиспользуемые ассеты запрещены.

Перед упаковкой экспортёр декодирует в ограниченных ресурсах, применяет EXIF orientation к пикселям, перекодирует статичный baseline JPEG в sRGB без EXIF/GPS/XMP/IPTC/comments/embedded thumbnails и удаляет исходное имя/URI. Импорт отклоняет progressive/multi-image JPEG и JPEG с metadata; разрешён только JFIF без thumbnail либо Adobe marker и необходимые coding tables/scan markers. Исключить trailing data после EOI. Проверить один кадр и фактические размеры. Это требование будущего кодека пакета; текущий PhotoStore сам по себе не доказывает такую проверку. JPEG-only соответствует его локальному `.jpg` контракту; PNG/WebP/HEIC/GIF не входят в v1.

Обязательные metadata: `id` (opaque lower ASCII slug, 1–64), `name` (1–80), `author` (1–120), `license` (1–120; SPDX identifier либо короткое именованное условие), optional `attribution` (1–512). Строковые длины в схеме — Unicode code points. Пробельные-only строки и control characters U+0000–001F/U+007F семантически запрещены. Metadata отображать только как экранированный текст. Не интерпретировать как URL/HTML/команду. ID — не путь, не identity и не доказательство авторства.

`remixOf` optional: один объект с id/author/license исходного пакета, без рекурсивной истории или URL. При remix сохранить известные исходные credit/attribution и проверить условия исходной лицензии перед экспортом; пользователь задаёт id нового пакета. Отсутствие provenance не доказывает оригинальность. Лицензия примера CC0-1.0 относится только к примеру, лицензия AndroidDock ещё не выбрана. Заявления автора/лицензии импортёр не может удостоверить автоматически. SHA-256 ассетов проверяет соответствие manifest байтам, **не подлинность или доверие к автору**; подписей/каталога v1 нет.

### Локальная запись происхождения (требование будущего импорта)

Текущие DockSettings/PhotoStore не хранят metadata. Будущий импорт обязан создавать отдельную приватную **local provenance record**, не добавляя поля в DockSettings или архивную схему. Запись содержит собственную local record version, общий commit/revision идентификатор снимка, связь с обоими профилями и точную валидированную копию всех metadata импортированного пакета (включая attribution и remixOf, если они были). Она не является доказательством авторства. Импорт заменяет provenance обоих профилей вместе с их оформлением; прежняя запись остаётся частью rollback snapshot до durable commit. Настройки, asset ownership и provenance входят в одну транзакцию и crash recovery: допустим только цельный старый либо новый набор всех трёх частей.

Запись сохраняется после перезапуска и любых локальных правок редактора, включая замену фото/фона и изменение любого из двух профилей. Локальная правка отмечает снимок как изменённый, но не удаляет исходные metadata/credit/license; editor commit обновляет связанный revision record атомарно с настройками, сохраняя источник. Изменение локальных comfort/mode не меняет происхождение. Отмена/Undo редактора не должны потерять запись; новый импорт заменяет её только после подтверждённого commit. Повреждение/утрата записи при наличии ссылки на неё — ошибка recovery: нельзя экспортировать такой снимок как будто происхождения не было. Для локально созданного снимка без импортного происхождения запись может отсутствовать; экспорт явно запрашивает собственные id/name/author/license.

**Источник export/remix:** экспорт читает provenance той же подтверждённой revision, что и settings/assets, а не transient preview или память предыдущего процесса. После импорта любой новый экспорт считается remix: пользователь задаёт новое id/name/author/license, `remixOf` берётся из сохранённых id/author/license непосредственно импортированного пакета. Исходное license нельзя заменить в remixOf новой лицензией. Сохранённое attribution переносится без изменения; к нему добавляется credit непосредственно исходного пакета `Source: <id>; author: <author>; license: <license>` и, если исходный remixOf был, такой же credit его источника. Разделитель — `; `. Для простой ограниченной семантики v1 credits добавляются даже при повторении; автоматического deduplication нет. Поля автора отображаются как текст. Новая лицензия не отменяет условия исходных лицензий; их совместимость пользователь проверяет до экспорта.

Accumulated attribution остаётся одним полем максимум **512 Unicode code points**, без рекурсивной истории. Если точный перенос сохранённого текста плюс требуемые credits не помещается, экспорт remix **явно отклоняется до создания пакета** с объяснением ограничения v1. Не обрезать текст, не удалять credits/license, не предлагать отключить provenance для обхода лимита; расширение формата требует отдельного решения. Исходные metadata остаются локально целиком в рамках их schema bounds даже при отказе экспорта. Экспорт не заменяет локальную source record своими новыми metadata. Последующий импорт такого remix на другом устройстве сохранит весь attribution и ближайший remixOf, поэтому цепочка известного авторства/лицензий не теряется; длина цепочки ограничена явным отказом при переполнении.

## Формат, лимиты и уровни проверки

Все пределы включительные; MiB=1 048 576 байт. Проверять до allocation и постоянно при чтении, а не доверять ZIP headers или manifest.

| Предлагаемый предел | Значение |
| --- | --- |
| Входной ZIP | 32 MiB, без многотомности, ZIP64 и encryption |
| pack.json | 64 KiB UTF-8 без BOM |
| Entries | 1 manifest + максимум 8 assets; директории как entries запрещены |
| Реально распакованные байты | 24 MiB всего включая manifest; один asset максимум 6 MiB |
| JPEG размеры | Каждая сторона 1–4096; ≤16 000 000 пикселей на asset, ≤32 000 000 суммарно |
| Decode | Читать bounds сначала; downsample до ≤2048 по длинной стороне и ≤4 194 304 пикселей, ≤16 MiB RGBA buffer; один asset за раз, освобождать buffer перед следующим |
| JSON nesting | ≤16 контейнеров на пути от корня (корень=1), до построения DOM |
| Compression | Только STORE/DEFLATE; uncompressed/compressed ≤100 для каждого entry и суммарно; compressed=0 допустим только при uncompressed=0 |

JSON Schema Draft 2020-12 использует только локальные `$defs`, strict additionalProperties, обязательные поля, enum и числовые пределы модели. Валидировать локально, без загрузки `$schema` из сети. Схема проверяет описания размеров/байтов, не фактические файлы. Числовые значения обязаны переживать conversion Float и последующий DockSettings.validated(); применять без clamping. Decimal boundary `.3`, `.7`, `.7`, `1.3` сопоставляются штатному Float представлению соответствующего предела.

**Парсер:** строгий UTF-8, ровно один JSON object, без duplicate keys на любой глубине, comments/trailing commas, NaN/Infinity, trailing text и непарных Unicode surrogates. Неизвестные поля отклоняются в том числе metadata/assets; schemaVersion integer 1, boolean не integer. Ограничить bytes/depth до parse. Whitespace разрешён только стандартный JSON.

**ZIP до extraction:** проверить центральный каталог и local headers на согласованность имён/метода/flags/размеров; CRC и фактический размер при чтении. Отклонить overlapping entries, truncated streams, вложенные архивы, лишние preamble/trailer и ambiguous encoding. Имена только ASCII: ровно `pack.json` либо `assets/[a-z0-9][a-z0-9_-]{0,63}.jpg`. Запретить абсолютные/drive/UNC пути, backslash, NUL, dot/dotdot segments, percent-encoded aliases, повторные separators. Не «исправлять» небезопасный путь нормализацией. Reject duplicate names и duplicate normalized/case-folded paths. Запретить symlink, hardlink, special/nonregular entries, directory и conflicting external attributes; entries без Unix mode допускаются лишь с обычными DOS file attributes и без link metadata. Не следовать ссылкам и не распаковывать в existing shared directory; staging приватный, с новыми обычными файлами и no-follow, вне пути активных настроек. Лимиты фактической распаковки/числа файлов/compression ratio ограничивают zip bombs; лимит входа действует даже до central directory.

**После схемы:** asset paths уникальны; файлы архива в точности равны manifest+assets, каждая photoAsset объявлена и каждый asset используется. SHA-256, byteLength, JPEG signature/marker policy, mediaType/extension, размеры и pixel budgets совпадают с реальностью. Нет удалённых ссылок и архивов под видом JPEG. Никаких файлов, отсутствующих в allowlist, даже README/thumbnail. Контролировать CPU/time/cancellation декодера; безопасная ошибка при превышении ресурсов, без частичного результата. Не считать одного успешного bounds decode достаточным: нужен bounded full decode всех assets. Разрешённый пакет с фото далее перекодируется в новый приватный JPEG; внутреннее имя генерируется получателем, archive path не становится photoPath.

## Требования будущего применения

1. Выбрать локальный файл → проверить архив, JSON, schema, семантику и полностью декодировать assets в приватном staging. Ни один отказ не меняет текущие настройки/файлы/provenance/согласия.
2. Построить candidate: взять актуальные локальные mode/comfort поля, заменить оба allowlisted профиля. Не merge отсутствующие поля: полей не может не быть. Mapping проверить DockSettings.validated(); подготовить связанную local provenance record с полными metadata импорта.
3. Показать предпросмотр обоих профилей и обеих ориентаций, автора/лицензию/attribution/remix, список замен и сохранённых личных настроек; не запускать автопоказ. Явная кнопка «Применить» подтверждает замену обоих профилей; отмена удаляет staging и оставляет всё прежним.
4. При изменении локальных настроек со времени preview отклонить устаревшее подтверждение и обновить preview; не затирать параллельный редактор. В commit повторно проверить общую revision settings/assets/provenance и сохранённые локальные поля.
5. Применить как единую транзакцию настроек, asset ownership и local provenance record: durable staging, проверенные файлы сначала, затем atomic settings switch со ссылками только на готовые файлы; журнал/backup обеспечивают crash recovery. Успех объявлять после durable commit. Ошибка/отмена до commit сохраняет old state. После crash выбрать целиком старое или целиком новое состояние, без dangling photoPath/смеси профилей или metadata от другого снимка. Cleanup только orphan files после recovery; текущие/старые необходимые фото не удалять преждевременно. Детальный механизм — задача будущей реализации, DataStore.update сам по себе не обеспечивает multi-file transaction.

Предпросмотр отображает данные ограниченным renderer; ошибка — понятная категория (несовместимая версия, неверная конфигурация, повреждённое фото, небезопасный архив, превышен лимит), без показа локальных секретных путей. Любой отказ сохраняет работу редактора и активный режим.

## Acceptance cases для будущей реализации

| Случай | Ожидание / уровень |
| --- | --- |
| Минимальный пример без фото, BLACK/null, два профиля/две ориентации | Schema + semantics pass; оформление меняется после confirm, local preservation проверяется |
| JPEG обоим PHOTO-профилям, один asset с точными hash/bytes/dimensions | Pass archive/decode; две ссылки на новый локальный JPEG |
| DUO/SOLO, все 3 clock/widget enums, диапазоны на обеих границах | Schema + DockSettings.validated pass |
| Экспорт без opt-in / выбранное фото недоступно | BLACK/null в копии, assets=[], явный preview; локальный PHOTO не меняется |
| Отмена, parser/schema/decode/IO failure, cancel во время staging | Старые settings/assets/provenance/permissions/согласия без изменений |
| Crash до/после switch, параллельный editor | Цельный old/new state либо новый preview, без потерянной чужой правки |
| Import → restart → edit обоих профилей → export remix → import на другом устройстве | Provenance переживает restart/edit; исходные attribution и license сохранены, новый id и правильный ближайший remixOf; фото требуют нового opt-in |
| Отказ записи provenance / crash между подготовкой record и settings switch / rollback импорта | Цельные old/new settings/assets/provenance; export не использует metadata другого снимка |
| Credits ровно 512 / 513 code points; отсутствующая/повреждённая required local record | 512 pass; 513 явный отказ экспорта без обрезки/license loss; повреждённая запись блокирует export до recovery |
| Unknown version/key, missing field, wrong enum, firstWeight=.8, spacing=33, textScale=.6, weekday=0 | Schema reject |
| PHOTO/null, BLACK/non-null, absolute/../remote/backslash ref | Schema reject |
| JSON duplicate keys, NaN, BOM, неверный UTF-8, nesting>16 | Parser reject до DOM |
| Необъявленный/неиспользуемый/duplicate asset, неверный hash/MIME/bytes/dimensions | Semantic/archive reject |
| Traversal, symlink/hardlink, duplicate normalized path, encrypted/ZIP64/nonregular, mismatch headers/CRC | Archive reject до записи вне staging |
| Bomb, excess bytes/count/ratio/pixels, crafted JPEG/metadata/trailing bytes | Bounded archive/decode reject без чрезмерной allocation |
| brightness/AlwaysOn/permissions/token/notification fields в manifest | Schema reject; никогда не применить |

[Минимальный пример](examples/dock-pack-v1/pack.json) — проверяемый manifest без персональных фото. Схема самопроверяется локальным Draft202012Validator. Проверка схемы и negative mutations не означает тестирование ZIP parser, JPEG pipeline, preview или транзакции: они пока существуют только как требования.

## Решения перед реализацией

Подтвердить численные бюджеты, JPEG marker policy и договориться о UI замены двух профилей/omit-photo preview. Выбрать конкретный transaction/recovery protocol с учётом DataStore, PhotoStore и local provenance record, затем тестировать перечисленные отказы. Не расширять v1 градиентами, кадрированием, виджетами этапа 4 или миграциями без нового решения.
