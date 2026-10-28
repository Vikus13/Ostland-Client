# Example Mod

# Ostland Client

**English** | [Русский](#русский)

A lightweight client-side Fabric mod for Minecraft 1.21.1 that adds a clean, semi-transparent rounded info bar at the top of the screen.

## Features

- Shows your **nickname**, **FPS**, **ping** and **server TPS** in one compact bar
- Rounded, semi-transparent design with an "Ostland Client" label
- Customizable bar color and opacity
- Client-side only: works on any server, nothing needs to be installed on the server
- Settings are saved automatically between launches

## Requirements

- Minecraft **1.21.1**
- [Fabric Loader](https://fabricmc.net/use/installer/)
- [Fabric API](https://modrinth.com/mod/fabric-api)

## Installation

1. Install Fabric Loader for Minecraft 1.21.1.
2. Put `Fabric API` and the Ostland Client `.jar` into your `mods` folder.
3. Launch the game with the Fabric profile.

## Commands

| Command | Description |
|---|---|
| `/ostland color <hex>` | Set the bar color, 6 hex digits without `#`, e.g. `/ostland color ff5555` |
| `/ostland opacity <0-100>` | Set the bar opacity in percent, e.g. `/ostland opacity 40` |

Settings are stored in `config/ostlandclient.properties`.

## Notes

- Minecraft clients cannot read the server TPS directly, so it is estimated from the timing of the server's time-update packets (sent every 20 ticks). It shows `-- TPS` for the first few seconds after joining.
- In singleplayer the ping is always 0 ms.

## Building

```
./gradlew build
```

The mod `.jar` will appear in `build/libs/`. You can also let GitHub Actions build it: open the **Actions** tab and download the `mod` artifact.

## License

CC0-1.0

---

<a name="русский"></a>

# Ostland Client (на русском)

Лёгкий клиентский Fabric-мод для Minecraft 1.21.1, который добавляет аккуратную полупрозрачную закруглённую плашку с информацией в верхней части экрана.

## Возможности

- Показывает **ник**, **FPS**, **пинг** и **TPS сервера** в одной компактной плашке
- Закруглённый полупрозрачный дизайн с надписью «Ostland Client»
- Настраиваемые цвет и прозрачность плашки
- Только клиентская сторона: работает на любом сервере, на сервер ничего ставить не нужно
- Настройки сохраняются между запусками

## Требования

- Minecraft **1.21.1**
- [Fabric Loader](https://fabricmc.net/use/installer/)
- [Fabric API](https://modrinth.com/mod/fabric-api)

## Установка

1. Установите Fabric Loader для Minecraft 1.21.1.
2. Положите `Fabric API` и `.jar` мода Ostland Client в папку `mods`.
3. Запустите игру с профилем Fabric.

## Команды

| Команда | Описание |
|---|---|
| `/ostland color <hex>` | Цвет плашки, 6 hex-цифр без `#`, например `/ostland color ff5555` |
| `/ostland opacity <0-100>` | Прозрачность плашки в процентах, например `/ostland opacity 40` |

Настройки хранятся в `config/ostlandclient.properties`.

## Примечания

- Клиент Minecraft не может напрямую узнать TPS сервера, поэтому он оценивается по времени между пакетами обновления времени (они приходят каждые 20 тиков). Первые секунды после входа показывается `-- TPS`.
- В одиночной игре пинг всегда равен 0 мс.

## Сборка

```
./gradlew build
```

Готовый `.jar` появится в `build/libs/`. Также можно собрать через GitHub Actions: откройте вкладку **Actions** и скачайте артефакт `mod`.

## Лицензия

CC0-1.0
