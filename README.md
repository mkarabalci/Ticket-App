# TicketApp

Kotlin ve Jetpack Compose ile geliştirilmiş, çok modüllü Clean Architecture ve MVVM prensiplerine dayanan bir etkinlik bileti uygulaması. Kullanıcılar etkinliklere göz atabilir, bilet satın alabilir, biletlerini QR kod olarak görüntüleyebilir; personel (staff) rolündeki kullanıcılar ise QR kod okutarak giriş kontrolü (check-in) yapabilir.




## Özellikler

- **Kimlik Doğrulama** — Email/şifre ile kayıt ve giriş, JWT access/refresh token yönetimi
- **Otomatik Token Yenileme** — Access token süresi dolduğunda (HTTP 401), OkHttp `Authenticator` ile arka planda otomatik refresh; eşzamanlı isteklerde çakışmayı önleyen kilit mekanizması
- **Rol Bazlı Yönlendirme** — Kullanıcı rolüne (USER / STAFF / ADMIN) göre farklı navigasyon akışı
- **Etkinlik Listeleme ve Detay** — Etkinlikleri görüntüleme, bilet tipi seçerek detay ekranına gitme
- **Bilet Satın Alma** — Uçtan uca satın alma akışı, onay diyaloğu ile
- **Biletlerim** — Satın alınan biletlerin listesi, durum bilgisiyle (geçerli / kullanılmış / iptal)
- **QR Kod Gösterimi** — Her bilet için `qrcode-kotlin` ile üretilen QR kod
- **Staff Check-in** — Personel rolündeki kullanıcılar için kamera ile QR kod okutma ve bilet doğrulama ekranı (ZXing)

## Mimari

Proje, bağımlılıkların tek yönde aktığı 3 modüllü bir Clean Architecture yapısı kullanır:

```
        ┌─────────┐
        │  :app   │  Presentation — Compose UI, ViewModel, Navigation, DI
        └────┬────┘
             │
      ┌──────┴──────┐
      ▼             ▼
┌──────────┐   ┌─────────┐
│  :data   │──▶│ :core   │
└──────────┘   └─────────┘
 Repository       Domain
 impl, DTO,       modelleri,
 Retrofit,        repository
 DataStore        interface'leri
```

- **`:core`** — Hiçbir modüle bağımlı değil. Domain modelleri (`Event`, `Ticket`, `User`, `Purchase`), repository interface'leri (soyut sözleşmeler) ve ortak `ApiException` / `NetworkException` tipleri burada.
- **`:data`** — Yalnızca `:core`'a bağımlı. Repository implementasyonları, DTO'lar, Retrofit servis tanımları, OkHttp interceptor/authenticator ve DataStore tabanlı `TokenStore` burada.
- **`:app`** — Hem `:core` hem `:data`'ya bağımlı. Jetpack Compose ekranları, ViewModel'ler, type-safe Navigation Compose route'ları ve Koin modül tanımları burada.

Katmanlar arası iletişim `interface` üzerinden yapılır; `:app` katmanı `:data` katmanının implementasyon detaylarını bilmez, yalnızca `:core`'daki soyutlamaları kullanır.

### Presentation Katmanı — MVVM

`:app` modülü içinde **MVVM (Model-View-ViewModel)** deseni kullanılır: her ekranın durumu ViewModel'de tek bir `UiState` nesnesi olarak tutulur ve `StateFlow` ile Compose'a tek yönlü olarak açılır. View, `Model` (repository) ile hiç doğrudan konuşmaz — her etkileşim ViewModel üzerinden geçer.

## Kullanılan Teknolojiler

| Katman | Teknoloji |
|---|---|
| Mimari | Clean Architecture (çok modül) + MVVM |
| UI | Jetpack Compose, Material 3 |
| Dependency Injection | Koin 4.0 |
| Networking | Retrofit 2.11, OkHttp 4.12 |
| Serialization | Kotlinx Serialization |
| Navigation | Navigation Compose (type-safe `@Serializable` route'lar) |
| Local Storage | DataStore Preferences |
| QR Kod Üretimi | qrcode-kotlin |
| QR Kod Okuma | ZXing (zxing-android-embedded) |
| Dil | Kotlin 2.2.10 |

**Minimum SDK:** 24 · **Target/Compile SDK:** 36

## Proje Yapısı

```
Ticket-App/
├── app/                                # Presentation katmanı
│   └── src/main/java/com/example/ticketapp/
│       ├── screen/                     # Login, Register, Home, Events, EventDetail,
│       │                                # Tickets, TicketDetail, Checkin
│       ├── viewmodel/                  # Her ekran için StateFlow tabanlı ViewModel
│       ├── navigation/                 # AppNavHost — rol bazlı yönlendirme, type-safe route'lar
│       ├── component/                  # QrCodeImage composable
│       └── di/                         # AppModule (ViewModel Koin tanımları)
│
├── core/                               # Domain katmanı
│   └── src/main/java/com/example/core/
│       ├── domain/                     # auth, event, purchase, checkin domain modelleri
│       │                                # ve repository interface'leri
│       ├── network/                    # ApiException, NetworkException
│       └── util/                       # Hata mesajı eşleme (toUserMessage)
│
├── data/                               # Data katmanı
│   └── src/main/java/com/example/data/
│       ├── remote/                     # Retrofit API interface'leri
│       ├── dto/                        # API DTO modelleri
│       ├── repository/                 # Repository implementasyonları
│       ├── network/                    # AuthInterceptor, TokenAuthenticator
│       ├── local/                      # TokenStore (DataStore)
│       └── di/                         # DataModule (network/repository Koin tanımları)
│
└── AGENTS.MD                           # Proje geliştirme kuralları
```

## Kurulum

1. Depoyu klonlayın:
   ```bash
   git clone https://github.com/mkarabalci/Ticket-App.git
   ```
2. Android Studio ile açın, Gradle senkronizasyonunun tamamlanmasını bekleyin.
3. Uygulama, `https://tickets-api.halitkalayci.com/` adresindeki REST API'ye bağlanacak şekilde yapılandırılmıştır — ek bir ortam değişkeni ayarlamaya gerek yoktur.
4. Bir cihaz/emülatörde çalıştırın; ilk açılışta Kayıt Ol ekranından yeni bir hesap oluşturabilirsiniz.

## Rol Bazlı Akış

`AppNavHost`, `AuthRepository`'den gelen `isLoggedIn` ve `currentRole` durumlarını reaktif olarak dinler:

- **Giriş yapılmamış** → Login / Register ekranları
- **USER rolü** → Home → Events / Tickets → Detay ekranları → Satın alma ve QR kod görüntüleme
- **STAFF / ADMIN rolü** → Doğrudan Check-in ekranına yönlendirilir, kamera ile bilet QR kodu okutup doğrulama yapabilir

