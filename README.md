<div align="center">

# Ostland Client

**A sleek dark-glass client mod for Minecraft with a Click GUI and a pack of handy HUD modules.**

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-62b47a?style=for-the-badge)
![Loader](https://img.shields.io/badge/Fabric-client--side-dbd0b4?style=for-the-badge)
![License](https://img.shields.io/badge/License-CC0--1.0-7c5cff?style=for-the-badge)

**English** | [Русский](#русский)

</div>

---

## Features

- **Click GUI.** Press **Right Shift** to open a dark-glass menu. Left click switches a module, right click opens its settings (sliders and switches), mouse wheel scrolls.
- **Info Bar.** One rounded, semi-transparent bar with your nickname, FPS, ping (signal bars) and server TPS. Values are color-coded: green, yellow, red.
- **Theme.** Pick your accent color (RGB), turn on the animated rainbow mode, change the glass opacity and the corner radius.
- **Move everything.** Every HUD module has its own position (Pos X / Pos Y) and scale sliders.
- **Client-side only.** Works on any server. Nothing is installed on the server.
- **Your settings are saved** automatically between launches.

## Modules

| Category | Module | What it does |
|---|---|---|
| HUD | **Info Bar** | Nickname, FPS, ping and server TPS |
| HUD | **Coordinates** | XYZ and facing direction |
| HUD | **Speed** | Your speed in blocks per second |
| HUD | **CPS** | Clicks per second (left / right) |
| HUD | **Clock** | Real time clock |
| HUD | **Keystrokes** | WASD, space and mouse buttons, pressed keys light up |
| HUD | **Armor HUD** | Armor and held item with remaining durability |
| HUD | **Module List** | List of enabled modules with accent bars |
| Visual | **Fullbright** | See clearly in the dark (client-side night vision look) |
| Visual | **Notifications** | Small popups when you switch a module |
| Utility | **Toggle Sprint** | Sprints automatically while you move forward |
| Utility | **Auto Respawn** | Respawns you after dying |

By default only **Info Bar** and **Notifications** are on. Enable the rest in the Click GUI.

## Requirements

- Minecraft **1.21.1**
- [Fabric Loader](https://fabricmc.net/use/installer/)
- [Fabric API](https://modrinth.com/mod/fabric-api)

## Installation

1. Install Fabric Loader for Minecraft 1.21.1.
2. Put **Fabric API** and the Ostland Client `.jar` into your `mods` folder.
3. Launch the game with the Fabric profile, join a world and press **Right Shift**.

## Controls

| Key | Action |
|---|---|
| **Right Shift** | Open / close the Click GUI (can be rebound in the controls menu) |
| **Esc** | Close the Click GUI |

Settings are stored in `config/ostlandclient.properties`.

## Good to know

- Minecraft clients cannot read the server TPS directly, so it is **estimated** from the timing of the server's time-update packets (sent every 20 ticks). It shows `-- TPS` for the first few seconds after joining.
- In singleplayer the ping is always 0 ms.
- Ostland Client only contains visual and quality-of-life modules. Still, some servers restrict things like Fullbright, so check the rules of the server you play on.

## Building

```
./gradlew build
```

The mod `.jar` appears in `build/libs/`. You can also let GitHub Actions build it: open the **Actions** tab and download the `mod` artifact.

## License

CC0-1.0

---

<a name="русский"></a>

<div align="center">

# Ostland Client (на русском)

**Стильный клиентский мод для Minecraft в тёмном стеклянном дизайне: Click GUI и набор полезных HUD-модулей.**

</div>

## Возможности

- **Click GUI.** Нажми **Right Shift**, чтобы открыть тёмное стеклянное меню. ЛКМ включает модуль, ПКМ открывает его настройки (ползунки и переключатели), колёсико мыши прокручивает.
- **Info Bar.** Закруглённая полупрозрачная плашка с ником, FPS, пингом (в виде полосок сигнала) и TPS сервера. Значения подсвечиваются: зелёный, жёлтый, красный.
- **Тема.** Выбор цвета акцента (RGB), анимированный радужный режим, настройка прозрачности стекла и скругления углов.
- **Двигай что угодно.** У каждого HUD-модуля есть свои ползунки положения (Pos X / Pos Y) и масштаба.
- **Только клиентская сторона.** Работает на любом сервере, на сервер ничего ставить не нужно.
- **Настройки сохраняются** автоматически между запусками.

## Модули

| Категория | Модуль | Что делает |
|---|---|---|
| HUD | **Info Bar** | Ник, FPS, пинг и TPS сервера |
| HUD | **Coordinates** | Координаты XYZ и направление взгляда |
| HUD | **Speed** | Твоя скорость в блоках в секунду |
| HUD | **CPS** | Клики в секунду (левая / правая кнопка) |
| HUD | **Clock** | Часы с реальным временем |
| HUD | **Keystrokes** | WASD, пробел и кнопки мыши, нажатые клавиши подсвечиваются |
| HUD | **Armor HUD** | Броня и предмет в руке с остатком прочности |
| HUD | **Module List** | Список включённых модулей с цветными полосками |
| Visual | **Fullbright** | Хорошо видно в темноте (эффект ночного зрения только на клиенте) |
| Visual | **Notifications** | Небольшие всплывающие уведомления при включении модулей |
| Utility | **Toggle Sprint** | Автоматический спринт при движении вперёд |
| Utility | **Auto Respawn** | Автоматический респавн после смерти |

По умолчанию включены только **Info Bar** и **Notifications**, остальное включается в Click GUI.

## Требования

- Minecraft **1.21.1**
- [Fabric Loader](https://fabricmc.net/use/installer/)
- [Fabric API](https://modrinth.com/mod/fabric-api)

## Установка

1. Установи Fabric Loader для Minecraft 1.21.1.
2. Положи **Fabric API** и `.jar` мода Ostland Client в папку `mods`.
3. Запусти игру с профилем Fabric, зайди в мир и нажми **Right Shift**.

## Управление

| Клавиша | Действие |
|---|---|
| **Right Shift** | Открыть / закрыть Click GUI (можно переназначить в настройках управления) |
| **Esc** | Закрыть Click GUI |

Настройки хранятся в `config/ostlandclient.properties`.

## Важно знать

- Клиент Minecraft не может напрямую узнать TPS сервера, поэтому он **оценивается** по времени между пакетами обновления времени (они приходят каждые 20 тиков). Первые секунды после входа показывается `-- TPS`.
- В одиночной игре пинг всегда равен 0 мс.
- В Ostland Client только визуальные модули и удобства. Тем не менее некоторые серверы ограничивают такие вещи, как Fullbright, поэтому проверяй правила сервера, на котором играешь.

## Сборка

```
./gradlew build
```

Готовый `.jar` появится в `build/libs/`. Также можно собрать через GitHub Actions: открой вкладку **Actions** и скачай артефакт `mod`.

## Лицензия

CC0-1.0

---

<div align="center">

Made with ❤️ by &nbsp;|&nbsp; Сделано с ❤️

<a href="https://github.com/Vikus13">
  <img src="https://img.shields.io/badge/Author-Vikusya00-ff69b4?style=for-the-badge" alt="Author: Vikusya00">
</a>

<sub>Ostland Client</sub>

</div>
