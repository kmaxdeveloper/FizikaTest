# Fizika Test Loyihasini Modernizatsiya Qilish Rejasi

Ushbu reja **Fizika Test** loyihasini **Kimyo Test** loyihasi arxitekturasi va funksionalligiga (Clean Architecture, Hilt, Immersive Mode) o'tkazishni ko'zda tutadi.

## User Review Required

> [!IMPORTANT]
> Loyiha arxitekturasini o'zgartirish (paketlar strukturasini o'zgartirish) katta o'zgarish hisoblanadi. Bu jarayonda ba'zi importlar o'zgarishi mumkin.
> Hilt integratsiyasi uchun `Application` klassi qo'shiladi va `AndroidManifest.xml` yangilanadi.

## Proposed Changes

### [Project Configuration]

#### [MODIFY] [libs.versions.toml](file:///C:/Users/User/AndroidStudioProjects/FizikaTest/gradle/libs.versions.toml)
*   Hilt, Shimmer, Yandex Ads va Crashlytics versiyalarini qo'shish.
*   Kotlin va AGP versiyalarini yangilash.

#### [MODIFY] [build.gradle.kts (root)](file:///C:/Users/User/AndroidStudioProjects/FizikaTest/build.gradle.kts)
*   Hilt va Crashlytics pluginlarini qo'shish.

#### [MODIFY] [build.gradle.kts (app)](file:///C:/Users/User/AndroidStudioProjects/FizikaTest/app/build.gradle.kts)
*   Hilt pluginini qo'llash.
*   `kapt` pluginini qo'shish.
*   Yangi kutubxonalarni (`implementation`) qo'shish.

---

### [Architecture Setup]

#### [NEW] [App.kt](file:///C:/Users/User/AndroidStudioProjects/FizikaTest/app/src/main/java/uz/kmax/fizikatest/App.kt)
*   `@HiltAndroidApp` annotatsiyasi bilan yangi Application klassi.

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/User/AndroidStudioProjects/FizikaTest/app/src/main/xml/AndroidManifest.xml)
*   `<application>` tegiga `android:name=".App"` qo'shish.

#### [RESTRUCTURE] Paketlar strukturasi
*   `uz.kmax.fizikatest.domain`
*   `uz.kmax.fizikatest.data`
*   `uz.kmax.fizikatest.presentation` (ichida `ui` va `viewModel`)

---

### [UI Modernization]

#### [MODIFY] [MainActivity.kt](file:///C:/Users/User/AndroidStudioProjects/FizikaTest/app/src/main/java/uz/kmax/fizikatest/MainActivity.kt)
*   `@AndroidEntryPoint` qo'shish.
*   `enableEdgeToEdge()` va `hideSystemUI()` funksiyalarini implement qilish.
*   `SharedPref`ni `@Inject` orqali olish.

#### [MODIFY] [SharedPref.kt](file:///C:/Users/User/AndroidStudioProjects/FizikaTest/app/src/main/java/uz/kmax/fizikatest/tools/tools/SharedPref.kt)
*   Hilt orqali inject qilish uchun `@Inject constructor` qo'shish.

## Verification Plan

### Automated Tests
*   `./gradlew assembleDebug` orqali buildni tekshirish.

### Manual Verification
*   Dasturni ishga tushirib, Splash ekrani va asosiy menyu to'g'ri chiqayotganini ko'rish.
*   Immersive mode (full screen) ishlayotganini tekshirish.
