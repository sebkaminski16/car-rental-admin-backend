# Car Rental Admin - Backend (Spring Boot)

Backend REST API do obsługi wypożyczalni samochodów **dla administratorów** (bez autentykacji - chociaż w przyszłości przewiduje się jej dodanie).

#### REPOZYTORIUM Z FRONTENDEM: https://github.com/sebkaminski16/car-rental-admin-frontend

## Technologie
- Java 21
- Spring Boot (Web, Validation, Data JPA)
- MySQL 
- H2 (testy repozytoriów)
- JUnit 5 + Mockito + MockMvc
- Jacoco (dla wykazania % pokrycia kodu)

## Wzorce projektowe 
- **Builder** - ręcznie zaimplementowany w encjach (`Car.builder()...build()` itp.)
- **Strategy** - różne taryfy liczenia ceny wynajmu: `HOURLY / DAILY / WEEKLY` 
- **Factory** - tworzenie konkretnego sposobu liczenia ceny wynajmu: `PricingStrategyFactory`

## Wymagania
- Java 21 
- Maven
- MySQL

## Uruchomienie

### 1) Konfiguracja

Zanim uruchomi się aplikację należy ją odpowiednio skonfigurować. Konfiguracji dokonuje się w pliku `application.yml`.

Konfiguracji bazy danych MySQL dokonuje się w polu `datasource`, gdzie:
- `url`: oznacza adres URL do bazy danych i domyślnie odnosi się do bazy "car_rental_admin" na serwerze lokalnym MySQL (utworzy się automatycznie jeżeli jej nie ma)
- `username` oznacza użytkownika i domyślnie jego wartość to "root"
- `password` oznacza hasło i domyślnie jego wartość to również "root"

UWAGA!!! Zakładając konto MAILTRAP należy wybrać opcję `Email Sandbox`!!!
Konfiguracji serwisu emailowego (MailTrap) dokonuje się w polu `email`, gdzie:
- `fromEmail`: oznacza adres email, z ktrego wysyłane będą maile do klientów i domyślnie jego wartość to "admin@carrental.com"
- `apiToken` oznacza Token API umożliwiający korzystanie z API MailTrap (po założeniu konta na MailTrap należy przejść do linku: https://mailtrap.io/api-tokens, a następnie dodać nowy Token API dla Sandboxa i skopiować ten Token)
- `inboxId` oznacza ID skrzynki pocztowej na MailTrap (widoczne w adresie URL po wejściu w zakładkę "Sandboxes" a następnie swoją skrzynkę, np. "mailtrap.io/inboxes/4358982/messages", gdzie 4358982 to właśnie numer ID skrzynki pocztowej, należy ten ID stamtąd właśnie skopiować)

Konfiguracji serwisu do przesyłania zdjęć (ImgBB) dokonuje się w polu `imgbb`, gdzie:
- `apiKey`: oznacza klucz API umożliwiający korzystanie z API ImgBB (po założeniu konta na ImgBB należy skorzystać z linku: https://api.imgbb.com/, kliknąć w "GET API KEY" i skopiować kod)

### 2) Start

Po zakończonej konfiguracji, w celu uruchomienia projektu (aplikacji Spring Boot) należy wywołać w terminalu (będąc w folderze głównym) komendę
`mvn spring-boot:run`

W celu uruchomienia testów jednostkowych i wygenerowania raportu Jacoco nalęzy wywołać w terminalu (będąc w folderze głównym) komendę
`mvn clean test` lub `mvn verify`

Raport Jacoco znajduje się w:
`target/site/jacoco/index.html`

## Zewnętrzne API 
### 1) ImgBB – upload zdjęć aut
Endpoint:
- `POST /api/images/upload` (multipart/form-data, pole: `file`)

### 2) Mailtrap – wysyłka maili (sandbox)
Testowy endpoint:
- `POST /api/emails/test`

## Scheduler (@Scheduled)
W projekcie jest jedna prosta metoda oznaczona `@Scheduled` (codziennie o 08:00),
która wysyła mail do klientów nieaktywnych przez 30 dni.



