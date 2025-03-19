# Axle Load BLE Scanner

Axle Load BLE Scanner — мобильное приложение для сканирования, подключения и настройки датчиков нагрузки на ось машины, реализованное с использованием архитектуры MVVM и DI (Hilt).

## 📌 Основные функции
- 🔍 Сканирование BLE-устройств и отображение их MAC-адресов и имён.
- 📡 Подключение к выбранному BLE-устройству.
- 📊 Считывание и отображение характеристик BLE-устройств в реальном времени.
- 🔄 Автоматическое обновление значений характеристик.
- ⚙️ Разделение на три типа датчиков: **DPS, DSS, DDS**.
- 🚛 Выбор количества осей и добавление датчиков на определённую ось.
- ⚙️ Настройка параметров датчиков нагрузки.
- 📡 Получение информации от датчиков в реальном времени.

## 🏗 Архитектура
Проект использует **MVVM (Model-View-ViewModel)** для разделения логики и представления.
- **View** (Activity/Fragment) — отображает данные и реагирует на пользовательские действия.
- **ViewModel** — управляет состоянием UI и обрабатывает бизнес-логику.
- **Model** — содержит код для работы с BLE.

## 🛠 Технологии и библиотеки
- **Java** — основной язык разработки.
- **Hilt** — Dependency Injection для упрощения работы с зависимостями.
- **LiveData** и **StateFlow** — для реактивного обновления UI.
- **RecyclerView** — для отображения списков устройств и характеристик.
- **Bluetooth Low Energy (BLE)** — работа с беспроводными устройствами.

## 🚀 Запуск проекта
1. **Клонируйте репозиторий**:
   ```sh
   git clone https://github.com/Yahimchik/axle-load.git
   ```
2. **Откройте проект в Android Studio.**
3. **Соберите и запустите на устройстве с поддержкой BLE.**

## 📖 TODO
- [ ] Реализовать кэширование последних подключенных устройств.
- [ ] Добавить поддержку уведомлений об изменениях характеристик.
- [ ] Оптимизировать энергопотребление при сканировании.
- [ ] Добавить возможность создания конфигурации для настройки датчиков нагрузки.
- [ ] Реализовать выбор количества осей и привязку датчиков к определённым осям.

## 🤝 Контрибьюция
Если у вас есть идеи или улучшения, создавайте Pull Request'ы или открывайте Issues. Будем рады вашим предложениям! 🚀

