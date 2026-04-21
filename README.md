# 📰 News Platform

## 🚀 Tech Stack

### Backend
- Kotlin
- Spring Boot (MVC)
- Spring Security
- Spring Data JPA 
- PostgreSQL
- Flyway

### Frontend
- Server-side rendering
- JavaScript (интерактивные элементы)
- Кастомные UI-компоненты

### DevOps
- Docker / Docker Compose
- Nginx
- Linux VPS
- GitHub Actions (CI/CD)

---

## 🔐 Security

- Регистрация и авторизация  
- Роли: **USER / ADMIN**
- OAuth2 (Google login)
- CAPTCHA при регистрации
- Rate limiting на попытки входа
- Session management + Remember Me 
- 2FA (TOTP) для администратора

### 🛡️ Audit

Логируются все попытки входа:  
- IP-адрес
- Браузер / ОС
- Город / страна
- Провайдер

- Возможность блокировки подозрительной активности  

---

## 🧠 Основной функционал

### 📰 Статьи
- Создание статей через блочную систему
- Drag & Drop редактор
- Кастомный мини WYSIWYG редактор
- Позиционирование обложки

### 🌍 Мультиязычность
- Поддержка нескольких языков
- Возможность добавлять переводы статьи

**Для администратора:**
- отсутствующие переводы явно отображаются
- возможность быстро добавить недостающий язык

---

## 📊 Работа с данными

- Pagination 
- Валидация данных:
  - на клиенте
  - на сервере (Spring Validation)

---

## 👥 Пользовательские функции

- Поиск и фильтрация
- Теги
- Комментарии
- Просмотры
- Избранное

---

## 🎨 UI/UX

- Адаптивный дизайн
- Карусели (Swiper)
- Dynamic scroll
- Hover-эффекты
- SVG карта
- Liquid animation эффект  
  *(с упрощением для слабых устройств)*

---

## 📁 Работа с файлами

- Хранение файлов в файловой системе

---

## 🔍 SEO

- Структура страниц оптимизирована под поисковые системы
- Подготовка под Google News
- SEO-friendly URL

---

## ⚙️ Деплой

Проект развернут в production-окружении:

- Docker контейнеры
- Nginx как reverse proxy
- Linux VPS
- CI/CD через GitHub Actions

---

## 📸 Screenshots

### Public Pages
![The main oage - Liquid effects](screenshots/liquid.png)
![The main page - Swiper slider](screenshots/swiper.png)
![The economics page](screenshots/economy.png)
![The food page](screenshots/food.png)
![The tashkent page](screenshots/tashkent.png)
![The uzbekistan page-svg](screenshots/svg.png)
![The wiki page](screenshots/wiki.png)
![The article view page](screenshots/article_view.png)
![The profile page](screenshots/profile.jpg)

### Admin Pages
![The articles page](screenshots/article_admin.png)
![The create article page - custom WYSIWYG,block system](screenshots/create_article1.png)
![The create article page - drag & drop,positioning of the cover](screenshots/create_article2.png)
![The 2FA page - TOTP QR](screenshots/totp.png)
![The wiki create page](screenshots/wiki_create.png)
![The login events page](screenshots/events.jpg)
![The userinfo page - session management](screenshots/sessions.jpg)
![The users page - blocking users](screenshots/users.jpg)

---

## 💡 О проекте
Vlog Deputata - Это полноценный production-ready  веб-проект,а не учебный или pet-проект.Приложение уже развернуто и доступно в интернете в рабочем состоянии.
На текущем этапе система практически завершена с технической точки зрения: реализована основная функциональность, продумана архитектура, учтены вопросы безопасности и стабильности. Оставшиеся задачи носят премущественно административный характер (наполение котнентом), а  не разработку.
Проект был вынесен  в отдельный демонстративный репозиторий исключительно для портфолио, чтобы наглядно показать уровень реализации и используемые технические решения.
По сути Vlog Deputata - это информационно-аналитическая платформа о Узбекистане, сочетающий формат новостного  портала и журнала. Сайт охватывает для обсужения все актуальные события Узбекистана: проблемы, новости и реформы, а также раскрывает понятие гордостей Узбекистана.
---


## 📬 Контакты

- Email:  akbarxongafurov0211@gmail.com
- Telegram: @AKBARXON_GAFUROV
