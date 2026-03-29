# Expense Tracker Android (Kotlin / Jetpack Compose)

Native Android aplikacija za praćenje troškova, sređena kao moderniji v2 projekat za Android Studio.

## Uključeno
- Jetpack Compose + Material 3 UI
- Room lokalna baza
- DataStore za postavke
- dashboard sa budžetom, prosjekom i top kategorijom
- pretraga, filter po kategoriji i sortiranje
- analytics ekran
- dark mode toggle
- export u PDF i CSV + share
- offline-first rad bez plaćenog API-ja

## Otvaranje u Android Studio
1. Raspakuj zip.
2. Otvori folder `kotlin_app` u Android Studio.
3. Pusti Gradle Sync.
4. Pokreni na emulatoru ili telefonu.

## Release / instalacija
Za lokalnu instalaciju na telefon:
- `Run` iz Android Studija ili
- `Build > Build APK(s)` za debug APK.

Za Play Store / release:
- `Build > Generate Signed Bundle / APK`
- izaberi **Android App Bundle (AAB)** za Play Store
- napravi ili učitaj svoj **keystore**
- koristi release build type.

## Napomena
U ovom okruženju nisam mogao napraviti finalni potpisani release build niti testirati sync u Android Studiju, pa je projekat pripremljen kao unaprijeđena build-ready baza za dalji sync/build na tvom računaru.
