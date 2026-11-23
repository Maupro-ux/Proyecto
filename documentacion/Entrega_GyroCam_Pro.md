# GyroCam Pro — Entrega técnica completa

**Plataforma:** Android nativo · **Lenguaje:** Kotlin · **Namespace:** `com.proyecto.gyrocampro` · **minSdk:** 24 · **targetSdk:** 35 · **compileSdk:** 36.

Esta entrega organiza los fuentes limpios en el source set de Gradle y conserva la variante explicada en `documentacion/version-comentada/`, fuera del source set para impedir clases duplicadas. Los archivos que siguen se reproducen completos.

# SECCIÓN 1: Estructura completa de paquetes y archivos

```text
GyroCamPro/
    ├── app/
    │   ├── src/
    │   │   └── main/
    │   │       ├── java/
    │   │       │   └── com/
    │   │       │       └── proyecto/
    │   │       │           └── gyrocampro/
    │   │       │               ├── data/
    │   │       │               │   └── local/
    │   │       │               │       ├── AppDatabase.kt
    │   │       │               │       ├── PhotoCaptureDao.kt
    │   │       │               │       └── PhotoCaptureEntity.kt
    │   │       │               ├── ui/
    │   │       │               │   ├── auth/
    │   │       │               │   │   └── LoginActivity.kt
    │   │       │               │   ├── camera/
    │   │       │               │   │   └── CameraHudFragment.kt
    │   │       │               │   ├── customview/
    │   │       │               │   │   └── SensorHudOverlayView.kt
    │   │       │               │   ├── gallery/
    │   │       │               │   │   ├── GalleryCrudFragment.kt
    │   │       │               │   │   └── PhotoCaptureAdapter.kt
    │   │       │               │   └── sensors/
    │   │       │               │       └── SensorLabFragment.kt
    │   │       │               ├── utils/
    │   │       │               │   └── AuthSession.kt
    │   │       │               └── MainActivity.kt
    │   │       ├── res/
    │   │       │   ├── drawable/
    │   │       │   │   ├── ic_camera.xml
    │   │       │   │   ├── ic_fingerprint.xml
    │   │       │   │   ├── ic_gallery.xml
    │   │       │   │   ├── ic_launcher.xml
    │   │       │   │   ├── ic_logout.xml
    │   │       │   │   └── ic_sensors.xml
    │   │       │   ├── layout/
    │   │       │   │   ├── activity_login.xml
    │   │       │   │   ├── activity_main.xml
    │   │       │   │   ├── dialog_edit_capture.xml
    │   │       │   │   ├── fragment_camera_hud.xml
    │   │       │   │   ├── fragment_gallery_crud.xml
    │   │       │   │   ├── fragment_sensor_lab.xml
    │   │       │   │   └── item_photo_capture.xml
    │   │       │   ├── menu/
    │   │       │   │   ├── bottom_nav_menu.xml
    │   │       │   │   └── main_toolbar_menu.xml
    │   │       │   ├── mipmap-hdpi/
    │   │       │   ├── navigation/
    │   │       │   │   └── nav_graph.xml
    │   │       │   └── values/
    │   │       │       ├── colors.xml
    │   │       │       ├── strings.xml
    │   │       │       └── themes.xml
    │   │       └── AndroidManifest.xml
    │   └── build.gradle.kts
    ├── documentacion/
    │   ├── version-comentada/
    │   │   ├── data/
    │   │   │   └── local/
    │   │   │       ├── AppDatabase.kt
    │   │   │       ├── PhotoCaptureDao.kt
    │   │   │       └── PhotoCaptureEntity.kt
    │   │   ├── ui/
    │   │   │   ├── auth/
    │   │   │   │   └── LoginActivity.kt
    │   │   │   ├── camera/
    │   │   │   │   └── CameraHudFragment.kt
    │   │   │   ├── customview/
    │   │   │   │   └── SensorHudOverlayView.kt
    │   │   │   ├── gallery/
    │   │   │   │   ├── GalleryCrudFragment.kt
    │   │   │   │   └── PhotoCaptureAdapter.kt
    │   │   │   └── sensors/
    │   │   │       └── SensorLabFragment.kt
    │   │   ├── utils/
    │   │   │   └── AuthSession.kt
    │   │   └── MainActivity.kt
    │   └── Entrega_GyroCam_Pro.md
    ├── .gitignore
    ├── build.gradle.kts
    ├── gradle.properties
    ├── README.md
    └── settings.gradle.kts
```

### Responsabilidad arquitectónica

| Paquete | Responsabilidad |
|---|---|
| `ui.auth` | `LoginActivity` concentra la autenticación biométrica, los estados de error y la navegación segura al contenido protegido. |
| `ui.camera` | `CameraHudFragment` administra permisos, ciclo de vida de CameraX, listeners de sensores, formulario de captura e inserción de telemetría. |
| `ui.gallery` | `GalleryCrudFragment` presenta el flujo reactivo de Room y los diálogos de detalle, edición y eliminación; `PhotoCaptureAdapter` implementa `ListAdapter` + `DiffUtil`. |
| `ui.sensors` | `SensorLabFragment` expone los valores X/Y/Z del giroscopio y acelerómetro, el selector de tema y el cierre de sesión. |
| `ui.customview` | `SensorHudOverlayView` dibuja el horizonte, retícula y medidor G con `Canvas`; no mantiene listeners propios. |
| `data.local` | `PhotoCaptureEntity`, `PhotoCaptureDao` y `AppDatabase` representan y persisten la telemetría mediante Room/SQLite. |
| `utils` | `AuthSession` conserva el estado de autenticación en memoria para que `MainActivity` rechace entradas directas sin login. |

`MainActivity.kt` es el contenedor de navegación en la raíz del namespace. Las clases de versión comentada son material académico: están fuera de `app/src/main/java` y no se compilan junto con sus pares limpios.

# SECCIÓN 2: Diseño visual y flujo de ejecución paso a paso

## Identidad visual

La interfaz utiliza el tema de vistas Material 3 (`Theme.Material3.DayNight.NoActionBar`) con teal profundo `#126E63` como primario, superficie neutra adaptable, esmeralda `#10B981` para nivelación, ámbar `#F59E0B` para desviaciones moderadas y rojo `#EF4444` para desviación alta. La superficie del visor es oscura para que el HUD blanco y los estados de color contrasten sobre la imagen.

## Las cuatro pantallas

### 1. LoginActivity — acceso biométrico

Fondo de superficie Material 3, icono de huella centrado, nombre GyroCam Pro, texto de propósito, panel de estado y botón primario **Autenticar con huella**. El botón secundario de ajustes aparece únicamente cuando el sistema devuelve `BIOMETRIC_ERROR_NONE_ENROLLED` (o un error equivalente sin biometrías inscritas). Los errores y las validaciones se leen de `strings.xml`. La Activity es la única `LAUNCHER`.

### 2. CameraHudFragment — cámara y HUD

El visor CameraX ocupa el área central. Sobre el `PreviewView` se superpone `SensorHudOverlayView`: una línea de horizonte artificial centrada que gira con el ángulo filtrado, marcas laterales, una retícula que se desplaza de acuerdo con aceleración lineal y una barra inferior de vibración. El horizonte es esmeralda cuando `abs(tiltDegrees) <= 2°`, ámbar cuando la desviación es moderada y roja cuando supera 8°. Debajo se pinta la lectura de fuerza G y el indicador de vibración. El botón principal abre un formulario para título y nota; después toma la fotografía y guarda imagen y telemetría.

La retícula se alimenta de aceleración lineal, no del vector bruto que contiene gravedad estática. El cálculo de fuerza G sí conserva la magnitud total del acelerómetro. El HUD es una vista personalizada y no captura eventos táctiles, por lo que el botón de captura permanece operativo.

### 3. GalleryCrudFragment — galería CRUD

Lista vertical de tarjetas Material 3. Cada tarjeta muestra miniatura, título, fecha, inclinación y fuerza G; incluye **Ver detalle**, **Editar** y **Eliminar**. La lista se alimenta de `Flow<List<PhotoCaptureEntity>>` y se recoge con `repeatOnLifecycle(STARTED)`, de modo que la UI se actualiza cuando cambia SQLite sin refresco manual. El detalle presenta los campos completos de giroscopio y acelerómetro; el formulario de edición usa `TextInputLayout` con contador y validación; eliminar exige confirmación y borra tanto la fila como el archivo privado.

### 4. SensorLabFragment — laboratorio de sensores

Panel desplazable con dos tarjetas: giroscopio (rad/s) y acelerómetro (m/s²), cada una con barras y valores firmados de X, Y y Z. El selector **Modo oscuro** persiste la preferencia y aplica `AppCompatDelegate.MODE_NIGHT_YES` o `MODE_NIGHT_NO`. Un botón de cierre de sesión destacado invoca el mismo mecanismo seguro que la Toolbar. El panel informa si alguno de los sensores no existe en el hardware.

## Fórmulas de telemetría

**Giroscopio.** Android entrega velocidad angular `ωz` en rad/s. Entre dos eventos se calcula `Δt = (timestampActual - timestampAnterior) / 1 000 000 000` segundos y se integra `Δθ_rad = ωz × Δt`. El incremento en grados es `Δθ_deg = Δθ_rad × 180/π`, equivalente a `Math.toDegrees(ωz × Δt)`. El acumulador se normaliza en `[-180°, 180°]`. Un filtro complementario agrega una corrección de baja frecuencia de acelerómetro para reducir la deriva: 98 % del ángulo integrado y 2 % de la diferencia angular estimada con `atan2(ax, -ay)`. La estimación con `atan2` presupone el dispositivo sostenido aproximadamente en vertical; es una referencia de nivelación de pantalla, no una solución inercial 3D completa.

**Acelerómetro.** Los tres ejes están en m/s². La magnitud es `|a| = √(ax² + ay² + az²)` y la fuerza G relativa es `G = |a| / 9.80665`. En reposo el módulo se aproxima a 1 g. La vibración visual se normaliza a partir de `abs(G - 1)`. Para mover la retícula, un pasa-bajas estima gravedad por eje y se resta del dato instantáneo: `alineal = a - agravedad`; esto evita que el vector gravitatorio estático deje la retícula desplazada permanentemente. Los tres valores instantáneos originales se persisten en Room.

**Limitación física importante.** El giroscopio por sí solo mide velocidad angular y la integración acumula error; el filtro reduce deriva en el plano de pantalla, pero no sustituye a un sistema de estabilización óptica/mecánica ni garantiza que una foto salga nítida. El aviso visual es asistencia de encuadre, no una promesa de estabilización física.

## Ejecución de principio a fin

1. Android lanza `LoginActivity`, declarada con `MAIN` + `LAUNCHER`. `BiometricManager.canAuthenticate(BIOMETRIC_STRONG)` comprueba hardware y enrolamiento antes de iniciar el prompt.
2. El usuario pulsa el botón de huella. Si se autentica, el callback establece `AuthSession.isAuthenticated = true`, abre `MainActivity` y ejecuta `finish()` en Login. En cualquier otro resultado no se concede acceso.
3. `MainActivity` comprueba `AuthSession` antes de inflar el layout. Si es falso, inicia Login con `FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK`. La navegación está compuesta por `NavHostFragment`, `NavController` y `BottomNavigationView`.
4. `CameraHudFragment` solicita `CAMERA` con Activity Result API. Si el permiso se concede, CameraX vincula `Preview` e `ImageCapture` al `viewLifecycleOwner`. Los listeners de sensores se registran en `onResume()` y se eliminan en `onPause()`.
5. Al capturar, el usuario escribe título y nota. Se toma una instantánea de los sensores, CameraX escribe el JPEG en `filesDir/captures` y una corrutina inserta la entidad con `Room`.
6. `GalleryCrudFragment` observa la tabla; permite ver detalles, editar título/nota y eliminar con confirmación. La colección se cancela al dejar de estar STARTED.
7. `SensorLabFragment` ofrece diagnóstico X/Y/Z y el tema. El botón de logout de laboratorio o la acción de Toolbar limpia la sesión y toda la pila para regresar a Login.

## Pruebas de los cinco escenarios biométricos

| Escenario | Procedimiento de prueba | Resultado esperado |
|---|---|---|
| Sin hardware o hardware temporalmente no disponible | Ejecutar en un emulador/dispositivo que no exponga sensor biométrico compatible para observar `BIOMETRIC_ERROR_NO_HARDWARE`; para la segunda variante, usar un entorno donde el servicio reporte `BIOMETRIC_ERROR_HW_UNAVAILABLE` (el proveedor puede no permitir forzarlo desde la UI). | `biometric_no_hardware` o `biometric_hardware_unavailable`, respectivamente; no se abre el contenido. |
| Sin huellas enroladas | En un dispositivo con sensor, elimina las huellas registradas o usa una instalación limpia sin enrolamiento; pulsa el botón de acceso. | Mensaje `biometric_none_enrolled` y aparece el botón que abre ajustes de inscripción (`ACTION_BIOMETRIC_ENROLL` en API 30+). |
| Huella no reconocida | En el emulador, registra un dedo virtual y envía una ID no enrolada mediante Extended Controls/`adb emu finger touch <id-no-enrolada>`; en un dispositivo real, usa una huella distinta. | `onAuthenticationFailed()` muestra `biometric_not_recognized`; el prompt continúa aceptando intentos. |
| Cancelación | Abre el prompt y pulsa Cancelar o Atrás. | `ERROR_NEGATIVE_BUTTON`, `ERROR_USER_CANCELED` o `ERROR_CANCELED` muestra `biometric_user_canceled`; no se marca sesión. |
| Bloqueo temporal/permanente | En un dispositivo de pruebas, acumula fallos hasta el umbral del sistema. Para permanente, repite ciclos de bloqueo; el comportamiento exacto depende de la implementación del fabricante. | `ERROR_LOCKOUT` muestra bloqueo temporal y `ERROR_LOCKOUT_PERMANENT` informa que debe desbloquearse el dispositivo con PIN/patrón/contraseña. |

La API de AndroidX define `ERROR_LOCKOUT` tras cinco intentos fallidos consecutivos y un bloqueo temporal habitual de 30 segundos; la permanencia ocurre después de múltiples bloqueos y requiere credencial del dispositivo. Las respuestas del sistema no deben sustituirse por lógica de conteo propia.

## Prueba del permiso de cámara

En la primera entrada a Cámara, Android solicita `CAMERA`. Pulsa **Permitir** para ver el preview y habilitar la captura. Pulsa **No permitir** para verificar el mensaje `camera_permission_denied`; concede luego el permiso desde Ajustes del sistema y vuelve a entrar. Las imágenes se guardan dentro del sandbox privado, por lo que no se solicita `READ_MEDIA_IMAGES` ni permiso de almacenamiento externo.

# SECCIÓN 3: Archivos de configuración y recursos XML

## Configuración Gradle

#### `build.gradle.kts`

```kotlin
plugins {
    id("com.android.application") version "8.11.1" apply false
    id("org.jetbrains.kotlin.android") version "2.2.21" apply false
    id("org.jetbrains.kotlin.kapt") version "2.2.21" apply false
}
```

#### `settings.gradle.kts`

```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "GyroCamPro"
include(":app")
```

#### `gradle.properties`

```properties
org.gradle.jvmargs=-Xmx3g -Dfile.encoding=UTF-8 -XX:+UseParallelGC
org.gradle.parallel=true
org.gradle.caching=true
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
```

#### `app/build.gradle.kts`

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.proyecto.gyrocampro"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.proyecto.gyrocampro"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    kapt {
        correctErrorTypes = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.8.0")
    implementation("androidx.activity:activity-ktx:1.11.0")
    implementation("androidx.fragment:fragment-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("com.google.android.material:material:1.14.0")

    implementation("androidx.biometric:biometric:1.1.0")

    val cameraXVersion = "1.7.0"
    implementation("androidx.camera:camera-core:$cameraXVersion")
    implementation("androidx.camera:camera-camera2:$cameraXVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraXVersion")
    implementation("androidx.camera:camera-view:$cameraXVersion")

    val navigationVersion = "2.10.2"
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")

    val roomVersion = "2.8.5"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation("io.coil-kt:coil:2.7.0")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
}
```

## Manifest

#### `app/src/main/AndroidManifest.xml`

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.USE_BIOMETRIC" />
    <uses-permission android:name="android.permission.USE_FINGERPRINT" />

    <uses-feature android:name="android.hardware.fingerprint" android:required="false" />
    <uses-feature android:name="android.hardware.camera.any" android:required="false" />
    <uses-feature android:name="android.hardware.sensor.gyroscope" android:required="false" />
    <uses-feature android:name="android.hardware.sensor.accelerometer" android:required="false" />

    <application
        android:allowBackup="false"
        android:icon="@drawable/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/Theme.GyroCamPro">

        <activity
            android:name=".MainActivity"
            android:exported="false" />

        <activity
            android:name=".ui.auth.LoginActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

## Recursos principales

#### `app/src/main/res/values/strings.xml`

```xml
<resources>
    <string name="app_name">GyroCam Pro</string>
    <string name="login_title">GyroCam Pro</string>
    <string name="login_subtitle">Cámara estabilizada con telemetría de sensores</string>
    <string name="login_biometric_button">Autenticar con huella</string>
    <string name="login_security_button">Configurar huellas en el sistema</string>
    <string name="login_initial_status">Autentícate para abrir tu cámara y tus capturas.</string>
    <string name="login_status_checking">Comprobando disponibilidad biométrica…</string>
    <string name="biometric_no_hardware">Este dispositivo no dispone de hardware biométrico compatible.</string>
    <string name="biometric_hardware_unavailable">El hardware biométrico no está disponible en este momento. Inténtalo más tarde.</string>
    <string name="biometric_none_enrolled">No hay huellas registradas. Configura una huella en los ajustes de seguridad del dispositivo.</string>
    <string name="biometric_not_recognized">La huella no fue reconocida. Vuelve a intentarlo.</string>
    <string name="biometric_user_canceled">Autenticación cancelada por el usuario.</string>
    <string name="biometric_lockout">Demasiados intentos fallidos. La autenticación biométrica está bloqueada temporalmente.</string>
    <string name="biometric_lockout_permanent">La autenticación biométrica está bloqueada. Desbloquea el dispositivo con tu método de seguridad principal e inténtalo después.</string>
    <string name="biometric_generic_error">No fue posible completar la autenticación biométrica. Código: %1$d.</string>
    <string name="biometric_success">Autenticación correcta. Abriendo GyroCam Pro…</string>
    <string name="biometric_settings_unavailable">No se pudieron abrir los ajustes de seguridad.</string>
    <string name="biometric_prompt_title">Verificación de identidad</string>
    <string name="biometric_prompt_subtitle">Usa tu huella para continuar</string>
    <string name="biometric_negative_button">Cancelar</string>

    <string name="nav_camera">Cámara</string>
    <string name="nav_gallery">Galería</string>
    <string name="nav_sensors">Sensores</string>
    <string name="toolbar_logout">Cerrar sesión</string>
    <string name="logout_title">Cerrar sesión</string>
    <string name="logout_message">Se cerrará la sesión y volverás a la pantalla de autenticación.</string>
    <string name="logout_confirm">Cerrar sesión</string>
    <string name="cancel">Cancelar</string>
    <string name="save">Guardar</string>
    <string name="edit">Editar</string>
    <string name="delete">Eliminar</string>
    <string name="view_details">Ver detalle</string>
    <string name="confirm_delete_title">Eliminar captura</string>
    <string name="confirm_delete_message">¿Deseas eliminar esta fotografía y sus datos de telemetría? Esta acción no se puede deshacer.</string>
    <string name="capture_deleted">Captura eliminada.</string>
    <string name="capture_updated">Captura actualizada.</string>
    <string name="capture_saved">Fotografía y telemetría guardadas.</string>
    <string name="capture_save_error">No se pudo guardar la fotografía. Revisa el espacio disponible e inténtalo de nuevo.</string>
    <string name="capture_db_error">La fotografía se creó, pero no se pudo guardar su registro local.</string>
    <string name="camera_permission_needed">Se necesita permiso de cámara para mostrar el visor y capturar fotografías.</string>
    <string name="camera_permission_denied">Permiso de cámara denegado. Puedes habilitarlo desde los ajustes de la aplicación.</string>
    <string name="camera_unavailable">No se pudo iniciar CameraX en este dispositivo.</string>
    <string name="camera_sensor_missing">Uno o más sensores no están disponibles. La cámara seguirá funcionando con telemetría parcial.</string>
    <string name="camera_capture_button">Capturar fotografía</string>
    <string name="dialog_capture_title">Nueva captura</string>
    <string name="dialog_edit_title">Editar captura</string>
    <string name="field_title">Título</string>
    <string name="field_note">Nota personal</string>
    <string name="field_title_hint">Escribe un título para la fotografía</string>
    <string name="field_note_hint">Añade observaciones sobre la toma (opcional)</string>
    <string name="field_title_required">El título es obligatorio.</string>
    <string name="field_title_too_long">El título no puede superar 40 caracteres.</string>
    <string name="field_note_too_long">La nota no puede superar 280 caracteres.</string>
    <string name="gallery_title">Mis capturas</string>
    <string name="gallery_empty">Todavía no hay fotografías. Abre la cámara y realiza tu primera captura.</string>
    <string name="gallery_count">%1$d fotografías guardadas</string>
    <string name="item_telemetry_format">Inclinación %1$.1f°  ·  Fuerza G %2$.2f g</string>
    <string name="capture_details_format">Título: %1$s\nNota: %2$s\nFecha: %3$s\nInclinación: %4$.2f°\nFuerza G: %5$.3f g\nGiroscopio (rad/s): X %6$.3f · Y %7$.3f · Z %8$.3f\nAcelerómetro (m/s²): X %9$.3f · Y %10$.3f · Z %11$.3f</string>
    <string name="no_note">Sin nota</string>
    <string name="date_time_format">%1$s · %2$s</string>
    <string name="capture_title_format">Captura %1$s</string>

    <string name="hud_status_level">ESTABILIZADO</string>
    <string name="hud_status_tilt">INCLINACIÓN %1$.1f°</string>
    <string name="hud_force_format">FUERZA G %1$.2f</string>
    <string name="hud_vibration_label">VIBRACIÓN</string>
    <string name="hud_accessibility">Horizonte artificial y medidor de vibración en tiempo real.</string>
    <string name="hud_preview_waiting">Preparando visor de cámara…</string>
    <string name="sensor_lab_title">Laboratorio de sensores</string>
    <string name="sensor_lab_description">Diagnóstico en tiempo real de velocidad angular y aceleración en los tres ejes del dispositivo.</string>
    <string name="gyro_section_title">Giroscopio · rad/s</string>
    <string name="accelerometer_section_title">Acelerómetro · m/s²</string>
    <string name="axis_x">Eje X</string>
    <string name="axis_y">Eje Y</string>
    <string name="axis_z">Eje Z</string>
    <string name="axis_value_format">%1$.3f</string>
    <string name="axis_value_initial">0.000</string>
    <string name="theme_dark_mode">Modo oscuro</string>
    <string name="theme_mode_description">Activa o desactiva el tema oscuro de la aplicación.</string>
    <string name="sensor_unavailable">Este dispositivo no dispone del sensor solicitado.</string>
    <string name="sensor_diagnostics_ready">Sensores conectados. Los valores se actualizan mientras esta pantalla está visible.</string>
    <string name="sensor_logout_button">Cerrar sesión</string>
    <string name="date_pattern">yyyy-MM-dd HH:mm:ss</string>
</resources>
```

#### `app/src/main/res/values/colors.xml`

```xml
<resources>
    <color name="color_primary">#126E63</color>
    <color name="color_on_primary">#FFFFFF</color>
    <color name="color_primary_container">#A8F0E2</color>
    <color name="color_secondary">#59636A</color>
    <color name="color_background">#F5F7F7</color>
    <color name="color_surface">#FFFFFF</color>
    <color name="color_surface_dark">#101819</color>
    <color name="color_on_surface">#172322</color>
    <color name="color_emerald">#10B981</color>
    <color name="color_amber">#F59E0B</color>
    <color name="color_alert">#EF4444</color>
    <color name="color_hud_background">#990B1112</color>
    <color name="color_white">#FFFFFF</color>
    <color name="color_black">#000000</color>
</resources>
```

#### `app/src/main/res/values/themes.xml`

```xml
<resources>
    <style name="Theme.GyroCamPro" parent="Theme.Material3.DayNight.NoActionBar">
        <item name="colorPrimary">@color/color_primary</item>
        <item name="colorOnPrimary">@color/color_on_primary</item>
        <item name="colorSecondary">@color/color_secondary</item>
        <item name="android:colorAccent">@color/color_primary</item>
        <item name="android:windowLightStatusBar">?attr/isLightTheme</item>
        <item name="android:navigationBarColor">?attr/colorSurface</item>
        <item name="android:statusBarColor">?attr/colorSurface</item>
        <item name="android:windowActionModeOverlay">true</item>
        <item name="android:windowLightNavigationBar">?attr/isLightTheme</item>
    </style>
</resources>
```

#### `app/src/main/res/navigation/nav_graph.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/nav_graph"
    app:startDestination="@id/cameraHudFragment">

    <fragment
        android:id="@+id/cameraHudFragment"
        android:name="com.proyecto.gyrocampro.ui.camera.CameraHudFragment"
        android:label="@string/nav_camera" />

    <fragment
        android:id="@+id/galleryCrudFragment"
        android:name="com.proyecto.gyrocampro.ui.gallery.GalleryCrudFragment"
        android:label="@string/nav_gallery" />

    <fragment
        android:id="@+id/sensorLabFragment"
        android:name="com.proyecto.gyrocampro.ui.sensors.SensorLabFragment"
        android:label="@string/nav_sensors" />
</navigation>
```

#### `app/src/main/res/menu/bottom_nav_menu.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:id="@+id/cameraHudFragment"
        android:icon="@drawable/ic_camera"
        android:title="@string/nav_camera" />
    <item
        android:id="@+id/galleryCrudFragment"
        android:icon="@drawable/ic_gallery"
        android:title="@string/nav_gallery" />
    <item
        android:id="@+id/sensorLabFragment"
        android:icon="@drawable/ic_sensors"
        android:title="@string/nav_sensors" />
</menu>
```

#### `app/src/main/res/menu/main_toolbar_menu.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">
    <item
        android:id="@+id/action_logout"
        android:icon="@drawable/ic_logout"
        android:title="@string/toolbar_logout"
        app:showAsAction="ifRoom" />
</menu>
```

#### `app/src/main/res/layout/activity_login.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="?attr/colorSurface"
    android:padding="28dp">

    <ImageView
        android:id="@+id/login_fingerprint_icon"
        android:layout_width="88dp"
        android:layout_height="88dp"
        android:contentDescription="@string/login_biometric_button"
        android:src="@drawable/ic_fingerprint"
        app:layout_constraintBottom_toTopOf="@id/login_title"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_chainStyle="packed" />

    <TextView
        android:id="@+id/login_title"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginTop="20dp"
        android:gravity="center"
        android:text="@string/login_title"
        android:textAppearance="?attr/textAppearanceHeadlineLarge"
        android:textColor="?attr/colorOnSurface"
        android:textStyle="bold"
        app:layout_constraintBottom_toTopOf="@id/login_subtitle"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/login_fingerprint_icon" />

    <TextView
        android:id="@+id/login_subtitle"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp"
        android:gravity="center"
        android:text="@string/login_subtitle"
        android:textAppearance="?attr/textAppearanceBodyLarge"
        android:textColor="?attr/colorOnSurfaceVariant"
        app:layout_constraintBottom_toTopOf="@id/login_status"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/login_title" />

    <TextView
        android:id="@+id/login_status"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginTop="32dp"
        android:background="?attr/colorSurfaceContainer"
        android:gravity="center"
        android:minHeight="72dp"
        android:padding="16dp"
        android:text="@string/login_initial_status"
        android:textAppearance="?attr/textAppearanceBodyMedium"
        android:textColor="?attr/colorOnSurface"
        app:layout_constraintBottom_toTopOf="@id/login_auth_button"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/login_subtitle" />

    <com.google.android.material.button.MaterialButton
        android:id="@+id/login_auth_button"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginTop="24dp"
        android:minHeight="56dp"
        android:text="@string/login_biometric_button"
        app:icon="@drawable/ic_fingerprint"
        app:iconPadding="12dp"
        app:layout_constraintBottom_toTopOf="@id/login_settings_button"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/login_status" />

    <com.google.android.material.button.MaterialButton
        android:id="@+id/login_settings_button"
        style="@style/Widget.Material3.Button.OutlinedButton"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginTop="10dp"
        android:text="@string/login_security_button"
        android:visibility="gone"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/login_auth_button" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

#### `app/src/main/res/layout/activity_main.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="?attr/colorSurface">

    <com.google.android.material.appbar.MaterialToolbar
        android:id="@+id/main_toolbar"
        android:layout_width="0dp"
        android:layout_height="?attr/actionBarSize"
        android:background="?attr/colorSurface"
        android:elevation="2dp"
        app:title="@string/app_name"
        app:titleTextColor="?attr/colorOnSurface"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <androidx.fragment.app.FragmentContainerView
        android:id="@+id/nav_host_fragment"
        android:name="androidx.navigation.fragment.NavHostFragment"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:defaultNavHost="true"
        app:navGraph="@navigation/nav_graph"
        app:layout_constraintBottom_toTopOf="@id/bottom_navigation"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/main_toolbar"
        tools:ignore="FragmentTagUsage" />

    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottom_navigation"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:background="?attr/colorSurface"
        app:itemIconTint="?attr/colorOnSurface"
        app:itemTextColor="?attr/colorOnSurface"
        app:labelVisibilityMode="labeled"
        app:menu="@menu/bottom_nav_menu"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

#### `app/src/main/res/layout/fragment_camera_hud.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#FF101819"
    android:padding="12dp">

    <TextView
        android:id="@+id/camera_intro"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:gravity="center"
        android:text="@string/hud_preview_waiting"
        android:textAppearance="?attr/textAppearanceBodyMedium"
        android:textColor="#FFFFFFFF"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <FrameLayout
        android:id="@+id/camera_preview_container"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_marginTop="10dp"
        android:background="#FF050909"
        app:layout_constraintBottom_toTopOf="@id/camera_status"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/camera_intro">

        <androidx.camera.view.PreviewView
            android:id="@+id/camera_preview"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />

        <com.proyecto.gyrocampro.ui.customview.SensorHudOverlayView
            android:id="@+id/sensor_hud_overlay"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:contentDescription="@string/hud_accessibility"
            android:importantForAccessibility="yes" />
    </FrameLayout>

    <TextView
        android:id="@+id/camera_status"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp"
        android:gravity="center"
        android:minHeight="38dp"
        android:text="@string/camera_sensor_missing"
        android:textAppearance="?attr/textAppearanceBodySmall"
        android:textColor="#FFFFFFFF"
        app:layout_constraintBottom_toTopOf="@id/capture_button"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/camera_preview_container" />

    <com.google.android.material.button.MaterialButton
        android:id="@+id/capture_button"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp"
        android:minHeight="54dp"
        android:text="@string/camera_capture_button"
        app:icon="@drawable/ic_camera"
        app:iconPadding="12dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/camera_status" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

#### `app/src/main/res/layout/fragment_gallery_crud.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="?attr/colorSurface"
    android:padding="16dp">

    <TextView
        android:id="@+id/gallery_heading"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="@string/gallery_title"
        android:textAppearance="?attr/textAppearanceHeadlineSmall"
        android:textColor="?attr/colorOnSurface"
        android:textStyle="bold"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/gallery_count"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginTop="4dp"
        android:textAppearance="?attr/textAppearanceBodyMedium"
        android:textColor="?attr/colorOnSurfaceVariant"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/gallery_heading" />

    <TextView
        android:id="@+id/gallery_empty_state"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:gravity="center"
        android:padding="28dp"
        android:text="@string/gallery_empty"
        android:textAppearance="?attr/textAppearanceBodyLarge"
        android:textColor="?attr/colorOnSurfaceVariant"
        android:visibility="gone"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/gallery_count" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/capture_recycler"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:clipToPadding="false"
        android:paddingTop="12dp"
        android:paddingBottom="12dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/gallery_count" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

#### `app/src/main/res/layout/item_photo_capture.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="12dp"
    app:cardCornerRadius="18dp"
    app:cardElevation="1dp"
    app:strokeColor="?attr/colorOutlineVariant"
    app:strokeWidth="1dp">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:padding="12dp">

        <ImageView
            android:id="@+id/photo_thumbnail"
            android:layout_width="104dp"
            android:layout_height="104dp"
            android:contentDescription="@string/view_details"
            android:scaleType="centerCrop"
            android:background="?attr/colorSurfaceContainer"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent" />

        <TextView
            android:id="@+id/photo_title"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_marginStart="12dp"
            android:textAppearance="?attr/textAppearanceTitleMedium"
            android:textColor="?attr/colorOnSurface"
            android:textStyle="bold"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toEndOf="@id/photo_thumbnail"
            app:layout_constraintTop_toTopOf="@id/photo_thumbnail" />

        <TextView
            android:id="@+id/photo_date"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_marginStart="12dp"
            android:layout_marginTop="4dp"
            android:textAppearance="?attr/textAppearanceBodySmall"
            android:textColor="?attr/colorOnSurfaceVariant"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toEndOf="@id/photo_thumbnail"
            app:layout_constraintTop_toBottomOf="@id/photo_title" />

        <TextView
            android:id="@+id/photo_telemetry"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_marginStart="12dp"
            android:layout_marginTop="8dp"
            android:textAppearance="?attr/textAppearanceBodySmall"
            android:textColor="?attr/colorPrimary"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toEndOf="@id/photo_thumbnail"
            app:layout_constraintTop_toBottomOf="@id/photo_date" />

        <com.google.android.material.button.MaterialButton
            android:id="@+id/button_details"
            style="@style/Widget.Material3.Button.TextButton"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="12dp"
            android:text="@string/view_details"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toBottomOf="@id/photo_thumbnail" />

        <com.google.android.material.button.MaterialButton
            android:id="@+id/button_edit"
            style="@style/Widget.Material3.Button.TextButton"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="12dp"
            android:text="@string/edit"
            app:layout_constraintStart_toEndOf="@id/button_details"
            app:layout_constraintTop_toBottomOf="@id/photo_thumbnail" />

        <com.google.android.material.button.MaterialButton
            android:id="@+id/button_delete"
            style="@style/Widget.Material3.Button.TextButton"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="12dp"
            android:text="@string/delete"
            android:textColor="@color/color_alert"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toBottomOf="@id/photo_thumbnail" />

    </androidx.constraintlayout.widget.ConstraintLayout>
</com.google.android.material.card.MaterialCardView>
```

#### `app/src/main/res/layout/dialog_edit_capture.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:paddingStart="8dp"
    android:paddingTop="8dp"
    android:paddingEnd="8dp">

    <com.google.android.material.textfield.TextInputLayout
        android:id="@+id/title_input_layout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="@string/field_title_hint"
        app:counterEnabled="true"
        app:counterMaxLength="40">

        <com.google.android.material.textfield.TextInputEditText
            android:id="@+id/title_input"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:imeOptions="actionNext"
            android:inputType="textCapSentences"
            android:maxLength="40"
            android:maxLines="1" />
    </com.google.android.material.textfield.TextInputLayout>

    <com.google.android.material.textfield.TextInputLayout
        android:id="@+id/note_input_layout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:hint="@string/field_note_hint"
        app:counterEnabled="true"
        app:counterMaxLength="280">

        <com.google.android.material.textfield.TextInputEditText
            android:id="@+id/note_input"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:gravity="top|start"
            android:inputType="textCapSentences|textMultiLine"
            android:maxLength="280"
            android:minLines="3" />
    </com.google.android.material.textfield.TextInputLayout>

</LinearLayout>
```

#### `app/src/main/res/layout/fragment_sensor_lab.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fillViewport="true">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="18dp">

        <TextView
            android:id="@+id/sensor_lab_heading"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/sensor_lab_title"
            android:textAppearance="?attr/textAppearanceHeadlineSmall"
            android:textColor="?attr/colorOnSurface"
            android:textStyle="bold" />

        <TextView
            android:id="@+id/sensor_lab_description"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="6dp"
            android:text="@string/sensor_lab_description"
            android:textAppearance="?attr/textAppearanceBodyMedium"
            android:textColor="?attr/colorOnSurfaceVariant" />

        <TextView
            android:id="@+id/sensor_status"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="14dp"
            android:background="?attr/colorSurfaceContainer"
            android:padding="12dp"
            android:text="@string/sensor_diagnostics_ready"
            android:textAppearance="?attr/textAppearanceBodySmall"
            android:textColor="?attr/colorOnSurface" />

        <com.google.android.material.card.MaterialCardView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="18dp"
            app:cardCornerRadius="18dp"
            app:cardElevation="1dp">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:padding="16dp">

                <TextView
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:text="@string/gyro_section_title"
                    android:textAppearance="?attr/textAppearanceTitleMedium"
                    android:textColor="?attr/colorPrimary"
                    android:textStyle="bold" />

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="14dp"
                    android:gravity="center_vertical"
                    android:orientation="horizontal">
                    <TextView android:layout_width="42dp" android:layout_height="wrap_content" android:text="@string/axis_x" android:textColor="?attr/colorOnSurface" />
                    <ProgressBar android:id="@+id/gyro_x_progress" style="?android:attr/progressBarStyleHorizontal" android:layout_width="0dp" android:layout_height="10dp" android:layout_weight="1" android:max="100" android:progressTint="@color/color_primary" />
                    <TextView android:id="@+id/gyro_x_value" android:layout_width="64dp" android:layout_height="wrap_content" android:gravity="end" android:text="@string/axis_value_initial" android:textColor="?attr/colorOnSurface" />
                </LinearLayout>

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="10dp"
                    android:gravity="center_vertical"
                    android:orientation="horizontal">
                    <TextView android:layout_width="42dp" android:layout_height="wrap_content" android:text="@string/axis_y" android:textColor="?attr/colorOnSurface" />
                    <ProgressBar android:id="@+id/gyro_y_progress" style="?android:attr/progressBarStyleHorizontal" android:layout_width="0dp" android:layout_height="10dp" android:layout_weight="1" android:max="100" android:progressTint="@color/color_primary" />
                    <TextView android:id="@+id/gyro_y_value" android:layout_width="64dp" android:layout_height="wrap_content" android:gravity="end" android:text="@string/axis_value_initial" android:textColor="?attr/colorOnSurface" />
                </LinearLayout>

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="10dp"
                    android:gravity="center_vertical"
                    android:orientation="horizontal">
                    <TextView android:layout_width="42dp" android:layout_height="wrap_content" android:text="@string/axis_z" android:textColor="?attr/colorOnSurface" />
                    <ProgressBar android:id="@+id/gyro_z_progress" style="?android:attr/progressBarStyleHorizontal" android:layout_width="0dp" android:layout_height="10dp" android:layout_weight="1" android:max="100" android:progressTint="@color/color_primary" />
                    <TextView android:id="@+id/gyro_z_value" android:layout_width="64dp" android:layout_height="wrap_content" android:gravity="end" android:text="@string/axis_value_initial" android:textColor="?attr/colorOnSurface" />
                </LinearLayout>
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <com.google.android.material.card.MaterialCardView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="14dp"
            app:cardCornerRadius="18dp"
            app:cardElevation="1dp">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:padding="16dp">

                <TextView
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:text="@string/accelerometer_section_title"
                    android:textAppearance="?attr/textAppearanceTitleMedium"
                    android:textColor="?attr/colorPrimary"
                    android:textStyle="bold" />

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="14dp"
                    android:gravity="center_vertical"
                    android:orientation="horizontal">
                    <TextView android:layout_width="42dp" android:layout_height="wrap_content" android:text="@string/axis_x" android:textColor="?attr/colorOnSurface" />
                    <ProgressBar android:id="@+id/accel_x_progress" style="?android:attr/progressBarStyleHorizontal" android:layout_width="0dp" android:layout_height="10dp" android:layout_weight="1" android:max="100" android:progressTint="@color/color_primary" />
                    <TextView android:id="@+id/accel_x_value" android:layout_width="64dp" android:layout_height="wrap_content" android:gravity="end" android:text="@string/axis_value_initial" android:textColor="?attr/colorOnSurface" />
                </LinearLayout>

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="10dp"
                    android:gravity="center_vertical"
                    android:orientation="horizontal">
                    <TextView android:layout_width="42dp" android:layout_height="wrap_content" android:text="@string/axis_y" android:textColor="?attr/colorOnSurface" />
                    <ProgressBar android:id="@+id/accel_y_progress" style="?android:attr/progressBarStyleHorizontal" android:layout_width="0dp" android:layout_height="10dp" android:layout_weight="1" android:max="100" android:progressTint="@color/color_primary" />
                    <TextView android:id="@+id/accel_y_value" android:layout_width="64dp" android:layout_height="wrap_content" android:gravity="end" android:text="@string/axis_value_initial" android:textColor="?attr/colorOnSurface" />
                </LinearLayout>

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="10dp"
                    android:gravity="center_vertical"
                    android:orientation="horizontal">
                    <TextView android:layout_width="42dp" android:layout_height="wrap_content" android:text="@string/axis_z" android:textColor="?attr/colorOnSurface" />
                    <ProgressBar android:id="@+id/accel_z_progress" style="?android:attr/progressBarStyleHorizontal" android:layout_width="0dp" android:layout_height="10dp" android:layout_weight="1" android:max="100" android:progressTint="@color/color_primary" />
                    <TextView android:id="@+id/accel_z_value" android:layout_width="64dp" android:layout_height="wrap_content" android:gravity="end" android:text="@string/axis_value_initial" android:textColor="?attr/colorOnSurface" />
                </LinearLayout>
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <com.google.android.material.card.MaterialCardView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="14dp"
            app:cardCornerRadius="18dp"
            app:cardElevation="1dp">
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:gravity="center_vertical"
                android:orientation="horizontal"
                android:padding="16dp">
                <LinearLayout android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1" android:orientation="vertical">
                    <TextView android:layout_width="match_parent" android:layout_height="wrap_content" android:text="@string/theme_dark_mode" android:textAppearance="?attr/textAppearanceTitleMedium" android:textColor="?attr/colorOnSurface" />
                    <TextView android:layout_width="match_parent" android:layout_height="wrap_content" android:layout_marginTop="3dp" android:text="@string/theme_mode_description" android:textAppearance="?attr/textAppearanceBodySmall" android:textColor="?attr/colorOnSurfaceVariant" />
                </LinearLayout>
                <com.google.android.material.switchmaterial.SwitchMaterial
                    android:id="@+id/dark_mode_switch"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:contentDescription="@string/theme_dark_mode" />
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <com.google.android.material.button.MaterialButton
            android:id="@+id/sensor_logout_button"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="20dp"
            android:layout_marginBottom="20dp"
            android:minHeight="54dp"
            android:text="@string/sensor_logout_button"
            app:icon="@drawable/ic_logout"
            app:iconPadding="10dp" />

    </LinearLayout>
</ScrollView>
```

## Drawables vectoriales

#### `app/src/main/res/drawable/ic_camera.xml`

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#FFFFFFFF" android:pathData="M12,12m-3.2,0a3.2,3.2 0,1 0,6.4 0a3.2,3.2 0,1 0,-6.4 0M9,3L7.17,5H4C2.9,5 2,5.9 2,7v12c0,1.1 0.9,2 2,2h16c1.1,0 2,-0.9 2,-2L22,7c0,-1.1 -0.9,-2 -2,-2h-3.17L15,3H9zM12,18a6,6 0,1 1,0 -12a6,6 0,0 1,0 12z"/>
</vector>
```

#### `app/src/main/res/drawable/ic_fingerprint.xml`

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="48dp" android:height="48dp" android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#FF126E63" android:pathData="M12,1C7.03,1 3,5.03 3,10c0,1.7 0.47,3.29 1.29,4.65l1.46,-1.46A6.96,6.96 0,0 1,5 10a7,7 0,1 1,14 0c0,5.25 -1.5,9.1 -4.1,11.78l1.42,1.42C19.3,20.07 21,15.64 21,10c0,-4.97 -4.03,-9 -9,-9zM12,5a5,5 0,0 0,-5 5c0,2.1 -0.4,4.05 -1.14,5.76l1.52,1.52A14.8,14.8 0,0 0,9 10a3,3 0,1 1,6 0c0,4.03 -0.84,7.05 -2.24,9.16l1.43,1.43C15.98,18.23 17,14.64 17,10a5,5 0,0 0,-5 -5zM12,9a1,1 0,0 0,-1 1c0,4.36 -0.8,7.4 -2.3,9.4l1.43,1.43C12.05,18.3 13,14.64 13,10a1,1 0,0 0,-1 -1z"/>
</vector>
```

#### `app/src/main/res/drawable/ic_gallery.xml`

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#FF172322" android:pathData="M21,19V5c0,-1.1 -0.9,-2 -2,-2H5C3.9,3 3,3.9 3,5v14c0,1.1 0.9,2 2,2h14c1.1,0 2,-0.9 2,-2zM8.5,13.5l2.5,3.01L14.5,12l4.5,6H5l3.5,-4.5z"/>
</vector>
```

#### `app/src/main/res/drawable/ic_launcher.xml`

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="48dp" android:height="48dp" android:viewportWidth="48" android:viewportHeight="48">
    <path android:fillColor="#FF126E63" android:pathData="M0,0h48v48h-48z"/>
    <path android:fillColor="#FFFFFFFF" android:pathData="M9,17h6l3,-4h12l3,4h6v20H9z"/>
    <path android:fillColor="#FF126E63" android:pathData="M24,20a7,7 0,1 0,0 14a7,7 0,0 0,0 -14zM24,23a4,4 0,1 1,0 8a4,4 0,0 1,0 -8z"/>
    <path android:fillColor="#FF10B981" android:pathData="M8,9h32v2H8z"/>
</vector>
```

#### `app/src/main/res/drawable/ic_logout.xml`

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#FFFFFFFF" android:pathData="M10.09,15.59L11.5,17l5,-5l-5,-5l-1.41,1.41L12.67,11H3v2h9.67l-2.58,2.59zM19,3H5c-1.1,0 -2,0.9 -2,2v4h2V5h14v14H5v-4H3v4c0,1.1 0.9,2 2,2h14c1.1,0 2,-0.9 2,-2V5c0,-1.1 -0.9,-2 -2,-2z"/>
</vector>
```

#### `app/src/main/res/drawable/ic_sensors.xml`

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#FF172322" android:pathData="M12,2a10,10 0,1 0,0 20a10,10 0,0 0,0 -20zM13,4.07c3.39,0.49 6,3.39 6,6.93h-2c0,-2.42 -1.72,-4.44 -4,-4.9V4.07zM11,19.93c-3.39,-0.49 -6,-3.39 -6,-6.93h2c0,2.42 1.72,4.44 4,4.9v2.03zM7,11H5c0,-3.54 2.61,-6.44 6,-6.93V6.1c-2.28,0.46 -4,2.48 -4,4.9zM13,19.93V17.9c2.28,-0.46 4,-2.48 4,-4.9h2c0,3.54 -2.61,6.44 -6,6.93zM12,15a3,3 0,1 1,0 -6a3,3 0,0 1,0 6z"/>
</vector>
```

# SECCIÓN 4: Código fuente Kotlin — versión 1 (limpia / sin comentarios)

Los archivos siguientes son los que se compilan desde `app/src/main/java`. `AuthSession.kt` se añade como utilidad de seguridad necesaria para centralizar el estado.

#### `app/src/main/java/com/proyecto/gyrocampro/ui/auth/LoginActivity.kt`

```kotlin
package com.proyecto.gyrocampro.ui.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.proyecto.gyrocampro.MainActivity
import com.proyecto.gyrocampro.R
import com.proyecto.gyrocampro.databinding.ActivityLoginBinding
import com.proyecto.gyrocampro.utils.AuthSession

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var biometricPrompt: BiometricPrompt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureBiometricPrompt()
        binding.loginAuthButton.setOnClickListener { authenticate() }
        binding.loginSettingsButton.setOnClickListener { openBiometricSettings() }
        showBiometricAvailability()
    }

    private fun configureBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    binding.loginStatus.text = getString(R.string.biometric_success)
                    AuthSession.markAuthenticated()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    binding.loginStatus.text = getString(R.string.biometric_not_recognized)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    val message = when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED,
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                        BiometricPrompt.ERROR_CANCELED -> getString(R.string.biometric_user_canceled)
                        BiometricPrompt.ERROR_LOCKOUT -> getString(R.string.biometric_lockout)
                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> getString(R.string.biometric_lockout_permanent)
                        BiometricPrompt.ERROR_HW_UNAVAILABLE -> getString(R.string.biometric_hardware_unavailable)
                        BiometricPrompt.ERROR_HW_NOT_PRESENT -> getString(R.string.biometric_no_hardware)
                        BiometricPrompt.ERROR_NO_BIOMETRICS,
                        BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL -> getString(R.string.biometric_none_enrolled)
                        else -> getString(R.string.biometric_generic_error, errorCode)
                    }
                    binding.loginStatus.text = message
                    binding.loginSettingsButton.isVisible = errorCode == BiometricPrompt.ERROR_NO_BIOMETRICS || errorCode == BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL
                }
            }
        )
    }

    private fun showBiometricAvailability() {
        binding.loginSettingsButton.isVisible = false
        when (BiometricManager.from(this).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> binding.loginStatus.text = getString(R.string.login_initial_status)
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> binding.loginStatus.text = getString(R.string.biometric_no_hardware)
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> binding.loginStatus.text = getString(R.string.biometric_hardware_unavailable)
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                binding.loginStatus.text = getString(R.string.biometric_none_enrolled)
                binding.loginSettingsButton.isVisible = true
            }
            else -> binding.loginStatus.text = getString(R.string.biometric_no_hardware)
        }
    }

    private fun authenticate() {
        when (BiometricManager.from(this).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                val promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle(getString(R.string.biometric_prompt_title))
                    .setSubtitle(getString(R.string.biometric_prompt_subtitle))
                    .setNegativeButtonText(getString(R.string.biometric_negative_button))
                    .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                    .build()
                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> binding.loginStatus.text = getString(R.string.biometric_no_hardware)
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> binding.loginStatus.text = getString(R.string.biometric_hardware_unavailable)
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                binding.loginStatus.text = getString(R.string.biometric_none_enrolled)
                binding.loginSettingsButton.isVisible = true
            }
            else -> binding.loginStatus.text = getString(R.string.biometric_no_hardware)
        }
    }

    private fun openBiometricSettings() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent(Settings.ACTION_BIOMETRIC_ENROLL).putExtra(
                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                BiometricManager.Authenticators.BIOMETRIC_STRONG
            )
        } else {
            Intent(Settings.ACTION_SECURITY_SETTINGS)
        }
        runCatching {
            startActivity(intent)
        }.onFailure {
            binding.loginStatus.text = getString(R.string.biometric_settings_unavailable)
        }
    }
}
```

#### `app/src/main/java/com/proyecto/gyrocampro/MainActivity.kt`

```kotlin
package com.proyecto.gyrocampro

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.proyecto.gyrocampro.databinding.ActivityMainBinding
import com.proyecto.gyrocampro.ui.auth.LoginActivity
import com.proyecto.gyrocampro.utils.AuthSession

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        val darkModeEnabled = getSharedPreferences("gyrocam_preferences", MODE_PRIVATE)
            .getBoolean("dark_mode_enabled", false)
        AppCompatDelegate.setDefaultNightMode(
            if (darkModeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        super.onCreate(savedInstanceState)
        if (!AuthSession.isAuthenticated) {
            redirectToLogin()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbar)
        binding.mainToolbar.inflateMenu(R.menu.main_toolbar_menu)
        binding.mainToolbar.menu.findItem(R.id.action_logout)?.icon?.setTint(
            ContextCompat.getColor(this, R.color.color_primary)
        )
        binding.mainToolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_logout) {
                performLogout()
                true
            } else {
                false
            }
        }

        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.mainToolbar.title = destination.label ?: getString(R.string.app_name)
        }
    }

    fun performLogout() {
        AuthSession.clear()
        redirectToLogin()
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
```

#### `app/src/main/java/com/proyecto/gyrocampro/ui/customview/SensorHudOverlayView.kt`

```kotlin
package com.proyecto.gyrocampro.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.proyecto.gyrocampro.R
import java.util.Locale
import kotlin.math.abs

class SensorHudOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { strokeCap = Paint.Cap.ROUND }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textAlign = Paint.Align.CENTER }
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var tiltDegrees = 0f
    private var forceG = 1f
    private var accelerationX = 0f
    private var accelerationY = 0f
    private val emerald = ContextCompat.getColor(context, R.color.color_emerald)
    private val amber = ContextCompat.getColor(context, R.color.color_amber)
    private val alert = ContextCompat.getColor(context, R.color.color_alert)
    private val white = ContextCompat.getColor(context, R.color.color_white)
    private val hudBackground = ContextCompat.getColor(context, R.color.color_hud_background)

    fun setTelemetry(tilt: Float, gForce: Float, accelX: Float, accelY: Float) {
        tiltDegrees = tilt
        forceG = gForce
        accelerationX = accelX
        accelerationY = accelY
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val density = resources.displayMetrics.density
        val centerX = width / 2f
        val centerY = height / 2f
        val halfLine = width * 0.34f
        val isLevel = abs(tiltDegrees) <= 2f
        val horizonColor = when {
            isLevel -> emerald
            abs(tiltDegrees) <= 8f -> amber
            else -> alert
        }

        canvas.save()
        canvas.rotate(-tiltDegrees, centerX, centerY)
        linePaint.color = horizonColor
        linePaint.strokeWidth = 2.5f * density
        canvas.drawLine(centerX - halfLine, centerY, centerX + halfLine, centerY, linePaint)
        linePaint.strokeWidth = 1.5f * density
        canvas.drawLine(centerX - halfLine, centerY - 10f * density, centerX - halfLine, centerY + 10f * density, linePaint)
        canvas.drawLine(centerX + halfLine, centerY - 10f * density, centerX + halfLine, centerY + 10f * density, linePaint)
        canvas.restore()

        val reticleOffsetX = (accelerationX / 9.80665f * 28f * density).coerceIn(-42f * density, 42f * density)
        val reticleOffsetY = (-accelerationY / 9.80665f * 28f * density).coerceIn(-42f * density, 42f * density)
        val reticleX = centerX + reticleOffsetX
        val reticleY = centerY + reticleOffsetY
        linePaint.color = white
        linePaint.strokeWidth = 1.4f * density
        val radius = 12f * density
        canvas.drawCircle(reticleX, reticleY, radius, linePaint)
        canvas.drawLine(reticleX - 20f * density, reticleY, reticleX - 5f * density, reticleY, linePaint)
        canvas.drawLine(reticleX + 5f * density, reticleY, reticleX + 20f * density, reticleY, linePaint)
        canvas.drawLine(reticleX, reticleY - 20f * density, reticleX, reticleY - 5f * density, linePaint)
        canvas.drawLine(reticleX, reticleY + 5f * density, reticleX, reticleY + 20f * density, linePaint)

        val status = if (isLevel) {
            resources.getString(R.string.hud_status_level)
        } else {
            String.format(Locale.getDefault(), resources.getString(R.string.hud_status_tilt), tiltDegrees)
        }
        textPaint.color = horizonColor
        textPaint.textSize = 14f * density
        textPaint.isFakeBoldText = true
        canvas.drawText(status, centerX, centerY + 42f * density, textPaint)

        val panelWidth = width * 0.72f
        val panelLeft = centerX - panelWidth / 2f
        val barTop = height - 76f * density
        val barHeight = 9f * density
        fillPaint.color = hudBackground
        canvas.drawRoundRect(
            RectF(panelLeft - 12f * density, barTop - 31f * density, panelLeft + panelWidth + 12f * density, barTop + 22f * density),
            10f * density,
            10f * density,
            fillPaint
        )
        textPaint.color = white
        textPaint.isFakeBoldText = false
        textPaint.textSize = 11f * density
        val forceLabel = String.format(Locale.getDefault(), resources.getString(R.string.hud_force_format), forceG)
        canvas.drawText(forceLabel, centerX, barTop - 10f * density, textPaint)
        val vibration = (abs(forceG - 1f) / 0.35f).coerceIn(0f, 1f)
        fillPaint.color = ContextCompat.getColor(context, R.color.color_white)
        canvas.drawRoundRect(RectF(panelLeft, barTop, panelLeft + panelWidth, barTop + barHeight), barHeight, barHeight, fillPaint)
        val vibrationColor = when {
            vibration < 0.2f -> emerald
            vibration < 0.55f -> amber
            else -> alert
        }
        fillPaint.color = vibrationColor
        canvas.drawRoundRect(
            RectF(panelLeft, barTop, panelLeft + panelWidth * vibration, barTop + barHeight),
            barHeight,
            barHeight,
            fillPaint
        )
        textPaint.color = white
        canvas.drawText(resources.getString(R.string.hud_vibration_label), centerX, barTop + 18f * density, textPaint)
    }
}
```

#### `app/src/main/java/com/proyecto/gyrocampro/ui/camera/CameraHudFragment.kt`

```kotlin
package com.proyecto.gyrocampro.ui.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.proyecto.gyrocampro.R
import com.proyecto.gyrocampro.data.local.AppDatabase
import com.proyecto.gyrocampro.data.local.PhotoCaptureEntity
import com.proyecto.gyrocampro.databinding.DialogEditCaptureBinding
import com.proyecto.gyrocampro.databinding.FragmentCameraHudBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.sqrt
import kotlinx.coroutines.launch

class CameraHudFragment : Fragment(), SensorEventListener {
    private var _binding: FragmentCameraHudBinding? = null
    private val binding get() = _binding!!
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageCapture: ImageCapture? = null
    private var sensorManager: SensorManager? = null
    private var gyroSensor: Sensor? = null
    private var accelerometerSensor: Sensor? = null
    private var gyroX = 0f
    private var gyroY = 0f
    private var gyroZ = 0f
    private var accelerationX = 0f
    private var accelerationY = 0f
    private var accelerationZ = 0f
    private var forceG = 1f
    private var tiltDegrees = 0f
    private var lastGyroTimestampNs = 0L
    private var gravityX = 0f
    private var gravityY = 0f
    private var gravityZ = 0f
    private var hasGravityEstimate = false
    private var hasInitialOrientation = false
    private var linearAccelerationX = 0f
    private var linearAccelerationY = 0f

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startCamera()
        } else if (_binding != null) {
            binding.cameraStatus.text = getString(R.string.camera_permission_denied)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraHudBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        accelerometerSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        binding.captureButton.setOnClickListener { showCaptureForm() }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            binding.cameraStatus.text = getString(R.string.camera_permission_needed)
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        if (gyroSensor == null || accelerometerSensor == null) {
            binding.cameraStatus.text = getString(R.string.camera_sensor_missing)
        }
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null && imageCapture == null &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        }
        lastGyroTimestampNs = 0L
        gyroSensor?.let { sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) }
        accelerometerSensor?.let { sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) }
    }

    override fun onPause() {
        sensorManager?.unregisterListener(this)
        super.onPause()
    }

    private fun startCamera() {
        if (_binding == null) return
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            if (_binding == null) return@addListener
            try {
                val provider = cameraProviderFuture.get()
                cameraProvider = provider
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = binding.cameraPreview.surfaceProvider
                }
                val captureUseCase = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()
                imageCapture = captureUseCase
                provider.unbindAll()
                provider.bindToLifecycle(
                    viewLifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    captureUseCase
                )
                binding.cameraStatus.text = getString(R.string.sensor_diagnostics_ready)
            } catch (exception: Exception) {
                binding.cameraStatus.text = getString(R.string.camera_unavailable)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun showCaptureForm() {
        if (imageCapture == null) {
            binding.cameraStatus.text = getString(R.string.camera_unavailable)
            return
        }
        val form = DialogEditCaptureBinding.inflate(layoutInflater)
        val timeLabel = SimpleDateFormat("HHmmss", Locale.getDefault()).format(Date())
        form.titleInput.setText(getString(R.string.capture_title_format, timeLabel))
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_capture_title)
            .setView(form.root)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.save, null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val title = form.titleInput.text?.toString()?.trim().orEmpty()
                val note = form.noteInput.text?.toString()?.trim().orEmpty()
                val valid = validateCaptureForm(form, title, note)
                if (valid) {
                    dialog.dismiss()
                    takePicture(title, note)
                }
            }
        }
        dialog.show()
    }

    private fun validateCaptureForm(form: DialogEditCaptureBinding, title: String, note: String): Boolean {
        form.titleInputLayout.error = null
        form.noteInputLayout.error = null
        if (title.isBlank()) {
            form.titleInputLayout.error = getString(R.string.field_title_required)
            return false
        }
        if (title.length > 40) {
            form.titleInputLayout.error = getString(R.string.field_title_too_long)
            return false
        }
        if (note.length > 280) {
            form.noteInputLayout.error = getString(R.string.field_note_too_long)
            return false
        }
        return true
    }

    private fun takePicture(title: String, note: String) {
        val captureUseCase = imageCapture ?: return
        val outputDirectory = File(requireContext().filesDir, "captures")
        if (!outputDirectory.exists()) outputDirectory.mkdirs()
        val outputFile = File(outputDirectory, "gyro_${System.currentTimeMillis()}.jpg")
        val capturedAt = System.currentTimeMillis()
        val appContext = requireContext().applicationContext
        val snapshot = SensorSnapshot(
            tiltDegrees = tiltDegrees,
            forceG = forceG,
            gyroX = gyroX,
            gyroY = gyroY,
            gyroZ = gyroZ,
            accelX = accelerationX,
            accelY = accelerationY,
            accelZ = accelerationZ
        )
        val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()
        captureUseCase.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    lifecycleScope.launch {
                        val entity = PhotoCaptureEntity(
                            imagePath = outputFile.absolutePath,
                            title = title,
                            note = note,
                            tiltDegrees = snapshot.tiltDegrees,
                            forceG = snapshot.forceG,
                            gyroX = snapshot.gyroX,
                            gyroY = snapshot.gyroY,
                            gyroZ = snapshot.gyroZ,
                            accelX = snapshot.accelX,
                            accelY = snapshot.accelY,
                            accelZ = snapshot.accelZ,
                            capturedAt = capturedAt
                        )
                        runCatching {
                            AppDatabase.getInstance(appContext).photoCaptureDao().insert(entity)
                        }.onSuccess {
                            if (isAdded) Toast.makeText(requireContext(), R.string.capture_saved, Toast.LENGTH_SHORT).show()
                        }.onFailure {
                            outputFile.delete()
                            if (isAdded) Toast.makeText(requireContext(), R.string.capture_db_error, Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    outputFile.delete()
                    if (isAdded) Toast.makeText(requireContext(), R.string.capture_save_error, Toast.LENGTH_LONG).show()
                }
            }
        )
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_GYROSCOPE -> {
                gyroX = event.values[0]
                gyroY = event.values[1]
                gyroZ = event.values[2]
                if (lastGyroTimestampNs != 0L) {
                    val deltaNs = (event.timestamp - lastGyroTimestampNs).coerceIn(0L, 100_000_000L)
                    val deltaSeconds = deltaNs / 1_000_000_000f
                    tiltDegrees += Math.toDegrees((gyroZ * deltaSeconds).toDouble()).toFloat()
                    if (tiltDegrees > 180f) tiltDegrees -= 360f
                    if (tiltDegrees < -180f) tiltDegrees += 360f
                }
                lastGyroTimestampNs = event.timestamp
            }
            Sensor.TYPE_ACCELEROMETER -> {
                accelerationX = event.values[0]
                accelerationY = event.values[1]
                accelerationZ = event.values[2]
                if (!hasGravityEstimate) {
                    gravityX = accelerationX
                    gravityY = accelerationY
                    gravityZ = accelerationZ
                    hasGravityEstimate = true
                } else {
                    val gravityAlpha = 0.8f
                    gravityX = gravityAlpha * gravityX + (1f - gravityAlpha) * accelerationX
                    gravityY = gravityAlpha * gravityY + (1f - gravityAlpha) * accelerationY
                    gravityZ = gravityAlpha * gravityZ + (1f - gravityAlpha) * accelerationZ
                }
                linearAccelerationX = accelerationX - gravityX
                linearAccelerationY = accelerationY - gravityY
                val magnitude = sqrt(
                    accelerationX * accelerationX +
                        accelerationY * accelerationY +
                        accelerationZ * accelerationZ
                )
                forceG = magnitude / SensorManager.GRAVITY_EARTH
                val accelerometerTilt = Math.toDegrees(atan2(accelerationX.toDouble(), -accelerationY.toDouble())).toFloat()
                if (!hasInitialOrientation) {
                    tiltDegrees = accelerometerTilt
                    hasInitialOrientation = true
                } else {
                    var correction = accelerometerTilt - tiltDegrees
                    while (correction > 180f) correction -= 360f
                    while (correction < -180f) correction += 360f
                    tiltDegrees += correction * 0.02f
                }
            }
        }
        _binding?.sensorHudOverlay?.setTelemetry(tiltDegrees, forceG, linearAccelerationX, linearAccelerationY)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    override fun onDestroyView() {
        cameraProvider?.unbindAll()
        cameraProvider = null
        imageCapture = null
        _binding = null
        super.onDestroyView()
    }

    private data class SensorSnapshot(
        val tiltDegrees: Float,
        val forceG: Float,
        val gyroX: Float,
        val gyroY: Float,
        val gyroZ: Float,
        val accelX: Float,
        val accelY: Float,
        val accelZ: Float
    )
}
```

#### `app/src/main/java/com/proyecto/gyrocampro/ui/gallery/GalleryCrudFragment.kt`

```kotlin
package com.proyecto.gyrocampro.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.proyecto.gyrocampro.R
import com.proyecto.gyrocampro.data.local.AppDatabase
import com.proyecto.gyrocampro.data.local.PhotoCaptureEntity
import com.proyecto.gyrocampro.databinding.DialogEditCaptureBinding
import com.proyecto.gyrocampro.databinding.FragmentGalleryCrudBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class GalleryCrudFragment : Fragment() {
    private var _binding: FragmentGalleryCrudBinding? = null
    private val binding get() = _binding!!
    private val database by lazy { AppDatabase.getInstance(requireContext().applicationContext) }
    private lateinit var captureAdapter: PhotoCaptureAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryCrudBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        captureAdapter = PhotoCaptureAdapter(
            onDetails = ::showDetails,
            onEdit = ::showEditDialog,
            onDelete = ::confirmDelete
        )
        binding.captureRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.captureRecycler.adapter = captureAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                database.photoCaptureDao().observeAll().collect { captures ->
                    captureAdapter.submitList(captures)
                    binding.galleryCount.text = getString(R.string.gallery_count, captures.size)
                    binding.galleryEmptyState.visibility = if (captures.isEmpty()) View.VISIBLE else View.GONE
                    binding.captureRecycler.visibility = if (captures.isEmpty()) View.GONE else View.VISIBLE
                }
            }
        }
    }

    private fun showDetails(capture: PhotoCaptureEntity) {
        val date = SimpleDateFormat(getString(R.string.date_pattern), Locale.getDefault())
            .format(Date(capture.capturedAt))
        val details = getString(
            R.string.capture_details_format,
            capture.title,
            capture.note.ifBlank { getString(R.string.no_note) },
            date,
            capture.tiltDegrees,
            capture.forceG,
            capture.gyroX,
            capture.gyroY,
            capture.gyroZ,
            capture.accelX,
            capture.accelY,
            capture.accelZ
        )
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(capture.title)
            .setMessage(details)
            .setPositiveButton(R.string.cancel, null)
            .show()
    }

    private fun showEditDialog(capture: PhotoCaptureEntity) {
        val form = DialogEditCaptureBinding.inflate(layoutInflater)
        form.titleInput.setText(capture.title)
        form.noteInput.setText(capture.note)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_edit_title)
            .setView(form.root)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.save, null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val title = form.titleInput.text?.toString()?.trim().orEmpty()
                val note = form.noteInput.text?.toString()?.trim().orEmpty()
                if (validateForm(form, title, note)) {
                    dialog.dismiss()
                    lifecycleScope.launch {
                        database.photoCaptureDao().update(capture.copy(title = title, note = note))
                        if (isAdded) Toast.makeText(requireContext(), R.string.capture_updated, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        dialog.show()
    }

    private fun validateForm(form: DialogEditCaptureBinding, title: String, note: String): Boolean {
        form.titleInputLayout.error = null
        form.noteInputLayout.error = null
        if (title.isBlank()) {
            form.titleInputLayout.error = getString(R.string.field_title_required)
            return false
        }
        if (title.length > 40) {
            form.titleInputLayout.error = getString(R.string.field_title_too_long)
            return false
        }
        if (note.length > 280) {
            form.noteInputLayout.error = getString(R.string.field_note_too_long)
            return false
        }
        return true
    }

    private fun confirmDelete(capture: PhotoCaptureEntity) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.confirm_delete_title)
            .setMessage(R.string.confirm_delete_message)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ ->
                lifecycleScope.launch {
                    database.photoCaptureDao().delete(capture)
                    File(capture.imagePath).delete()
                    if (isAdded) Toast.makeText(requireContext(), R.string.capture_deleted, Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    override fun onDestroyView() {
        binding.captureRecycler.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
```

#### `app/src/main/java/com/proyecto/gyrocampro/ui/gallery/PhotoCaptureAdapter.kt`

```kotlin
package com.proyecto.gyrocampro.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.proyecto.gyrocampro.R
import com.proyecto.gyrocampro.data.local.PhotoCaptureEntity
import com.proyecto.gyrocampro.databinding.ItemPhotoCaptureBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PhotoCaptureAdapter(
    private val onDetails: (PhotoCaptureEntity) -> Unit,
    private val onEdit: (PhotoCaptureEntity) -> Unit,
    private val onDelete: (PhotoCaptureEntity) -> Unit
) : ListAdapter<PhotoCaptureEntity, PhotoCaptureAdapter.PhotoCaptureViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoCaptureViewHolder {
        val binding = ItemPhotoCaptureBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoCaptureViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoCaptureViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PhotoCaptureViewHolder(
        private val binding: ItemPhotoCaptureBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PhotoCaptureEntity) {
            binding.photoTitle.text = item.title
            binding.photoThumbnail.contentDescription = item.title
            val date = SimpleDateFormat(
                binding.root.context.getString(R.string.date_pattern),
                Locale.getDefault()
            ).format(Date(item.capturedAt))
            binding.photoDate.text = date
            binding.photoTelemetry.text = binding.root.context.getString(
                R.string.item_telemetry_format,
                item.tiltDegrees,
                item.forceG
            )
            val imageFile = File(item.imagePath)
            if (imageFile.exists()) {
                binding.photoThumbnail.load(imageFile) {
                    crossfade(true)
                }
            } else {
                binding.photoThumbnail.setImageResource(com.proyecto.gyrocampro.R.drawable.ic_gallery)
            }
            binding.buttonDetails.setOnClickListener { onDetails(item) }
            binding.buttonEdit.setOnClickListener { onEdit(item) }
            binding.buttonDelete.setOnClickListener { onDelete(item) }
        }
    }

    private companion object DiffCallback : DiffUtil.ItemCallback<PhotoCaptureEntity>() {
        override fun areItemsTheSame(oldItem: PhotoCaptureEntity, newItem: PhotoCaptureEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PhotoCaptureEntity, newItem: PhotoCaptureEntity): Boolean {
            return oldItem == newItem
        }
    }
}
```

#### `app/src/main/java/com/proyecto/gyrocampro/ui/sensors/SensorLabFragment.kt`

```kotlin
package com.proyecto.gyrocampro.ui.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.proyecto.gyrocampro.MainActivity
import com.proyecto.gyrocampro.R
import com.proyecto.gyrocampro.databinding.FragmentSensorLabBinding
import java.util.Locale
import kotlin.math.abs

class SensorLabFragment : Fragment(), SensorEventListener {
    private var _binding: FragmentSensorLabBinding? = null
    private val binding get() = _binding!!
    private var sensorManager: SensorManager? = null
    private var gyroscope: Sensor? = null
    private var accelerometer: Sensor? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSensorLabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroscope = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        binding.sensorStatus.text = if (gyroscope == null || accelerometer == null) {
            getString(R.string.sensor_unavailable)
        } else {
            getString(R.string.sensor_diagnostics_ready)
        }

        val preferences = requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        binding.darkModeSwitch.isChecked = preferences.getBoolean(KEY_DARK_MODE, false)
        binding.darkModeSwitch.setOnCheckedChangeListener { _, enabled ->
            preferences.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (enabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
        binding.sensorLogoutButton.setOnClickListener {
            (activity as? MainActivity)?.performLogout()
        }
    }

    override fun onResume() {
        super.onResume()
        gyroscope?.let { sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        accelerometer?.let { sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }

    override fun onPause() {
        sensorManager?.unregisterListener(this)
        super.onPause()
    }

    override fun onSensorChanged(event: SensorEvent) {
        val currentBinding = _binding ?: return
        val values = event.values
        when (event.sensor.type) {
            Sensor.TYPE_GYROSCOPE -> {
                renderAxis(values[0], currentBinding.gyroXProgress, currentBinding.gyroXValue, 5f)
                renderAxis(values[1], currentBinding.gyroYProgress, currentBinding.gyroYValue, 5f)
                renderAxis(values[2], currentBinding.gyroZProgress, currentBinding.gyroZValue, 5f)
            }
            Sensor.TYPE_ACCELEROMETER -> {
                renderAxis(values[0], currentBinding.accelXProgress, currentBinding.accelXValue, 20f)
                renderAxis(values[1], currentBinding.accelYProgress, currentBinding.accelYValue, 20f)
                renderAxis(values[2], currentBinding.accelZProgress, currentBinding.accelZValue, 20f)
            }
        }
    }

    private fun renderAxis(value: Float, progressBar: ProgressBar, valueView: TextView, scaleMaximum: Float) {
        progressBar.progress = ((abs(value) / scaleMaximum) * 100f).toInt().coerceIn(0, 100)
        valueView.text = String.format(Locale.getDefault(), getString(R.string.axis_value_format), value)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val PREFERENCES_NAME = "gyrocam_preferences"
        const val KEY_DARK_MODE = "dark_mode_enabled"
    }
}
```

#### `app/src/main/java/com/proyecto/gyrocampro/data/local/PhotoCaptureEntity.kt`

```kotlin
package com.proyecto.gyrocampro.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo_captures")
data class PhotoCaptureEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "image_path")
    val imagePath: String,
    val title: String,
    val note: String,
    @ColumnInfo(name = "tilt_degrees")
    val tiltDegrees: Float,
    @ColumnInfo(name = "force_g")
    val forceG: Float,
    @ColumnInfo(name = "gyro_x_rad_s")
    val gyroX: Float,
    @ColumnInfo(name = "gyro_y_rad_s")
    val gyroY: Float,
    @ColumnInfo(name = "gyro_z_rad_s")
    val gyroZ: Float,
    @ColumnInfo(name = "accel_x_m_s2")
    val accelX: Float,
    @ColumnInfo(name = "accel_y_m_s2")
    val accelY: Float,
    @ColumnInfo(name = "accel_z_m_s2")
    val accelZ: Float,
    @ColumnInfo(name = "captured_at")
    val capturedAt: Long
)
```

#### `app/src/main/java/com/proyecto/gyrocampro/data/local/PhotoCaptureDao.kt`

```kotlin
package com.proyecto.gyrocampro.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoCaptureDao {
    @Query("SELECT * FROM photo_captures ORDER BY captured_at DESC")
    fun observeAll(): Flow<List<PhotoCaptureEntity>>

    @Query("SELECT * FROM photo_captures WHERE id = :captureId LIMIT 1")
    suspend fun findById(captureId: Long): PhotoCaptureEntity?

    @Insert
    suspend fun insert(capture: PhotoCaptureEntity): Long

    @Update
    suspend fun update(capture: PhotoCaptureEntity)

    @Delete
    suspend fun delete(capture: PhotoCaptureEntity)

    @Query("DELETE FROM photo_captures WHERE id = :captureId")
    suspend fun deleteById(captureId: Long)
}
```

#### `app/src/main/java/com/proyecto/gyrocampro/data/local/AppDatabase.kt`

```kotlin
package com.proyecto.gyrocampro.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PhotoCaptureEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun photoCaptureDao(): PhotoCaptureDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gyrocam_pro.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
```

#### `app/src/main/java/com/proyecto/gyrocampro/utils/AuthSession.kt`

```kotlin
package com.proyecto.gyrocampro.utils

object AuthSession {
    @Volatile
    var isAuthenticated: Boolean = false
        private set

    fun markAuthenticated() {
        isAuthenticated = true
    }

    fun clear() {
        isAuthenticated = false
    }
}
```

# SECCIÓN 5: Código fuente Kotlin — versión 2 (exhaustivamente comentada)

Son las mismas clases y la misma lógica de la Sección 4, con documentación de responsabilidad, flujo de callbacks, ciclo de vida, operaciones Room y fórmulas. Se guardan bajo `documentacion/version-comentada` para no compilar dos definiciones con el mismo paquete y nombre.

#### `documentacion/version-comentada/ui/auth/LoginActivity.kt`

```kotlin
/**
 * VERSIÓN COMENTADA DE REFERENCIA. La lógica coincide con la versión limpia compilable.
 * Las explicaciones documentan responsabilidad, ciclo de vida, fórmulas y acceso a Room.
 */
package com.proyecto.gyrocampro.ui.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.proyecto.gyrocampro.MainActivity
import com.proyecto.gyrocampro.R
import com.proyecto.gyrocampro.databinding.ActivityLoginBinding
import com.proyecto.gyrocampro.utils.AuthSession

/**
 * Pantalla de entrada y única Activity declarada como LAUNCHER.
 * BiometricManager comprueba primero si hay sensor compatible y enrolamiento;
 * BiometricPrompt ejecuta después la autenticación del sistema operativo.
 * El indicador AuthSession solo se activa desde onAuthenticationSucceeded,
 * de modo que nunca se abre MainActivity tras un error o una cancelación.
 */
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var biometricPrompt: BiometricPrompt

    /** Infla la vista mediante ViewBinding y conecta las acciones de autenticación y ajustes. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureBiometricPrompt()
        binding.loginAuthButton.setOnClickListener { authenticate() }
        binding.loginSettingsButton.setOnClickListener { openBiometricSettings() }
        showBiometricAvailability()
    }

    /**
     * Construye BiometricPrompt con un executor del hilo principal.
     * onAuthenticationFailed representa una coincidencia negativa no terminal:
     * el usuario puede volver a apoyar el dedo en el mismo diálogo.
     * onAuthenticationError es terminal y clasifica hardware ausente/no disponible,
     * ausencia de biometrías, cancelación, bloqueo temporal y bloqueo permanente.
     * ERROR_LOCKOUT suele ser temporal; ERROR_LOCKOUT_PERMANENT exige desbloquear
     * el dispositivo mediante PIN, patrón o contraseña antes de usar biometría.
     */
    private fun configureBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                /** Solo una respuesta autenticada marca la sesión y navega a la cámara. */
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    binding.loginStatus.text = getString(R.string.biometric_success)
                    AuthSession.markAuthenticated()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }

                /** Error de coincidencia no terminal: no se destruye el prompt ni la sesión. */
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    binding.loginStatus.text = getString(R.string.biometric_not_recognized)
                }

                /** Convierte los códigos definidos por AndroidX en mensajes propios centralizados en strings.xml. */
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    val message = when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED,
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                        BiometricPrompt.ERROR_CANCELED -> getString(R.string.biometric_user_canceled)
                        BiometricPrompt.ERROR_LOCKOUT -> getString(R.string.biometric_lockout)
                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> getString(R.string.biometric_lockout_permanent)
                        BiometricPrompt.ERROR_HW_UNAVAILABLE -> getString(R.string.biometric_hardware_unavailable)
                        BiometricPrompt.ERROR_HW_NOT_PRESENT -> getString(R.string.biometric_no_hardware)
                        BiometricPrompt.ERROR_NO_BIOMETRICS,
                        BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL -> getString(R.string.biometric_none_enrolled)
                        else -> getString(R.string.biometric_generic_error, errorCode)
                    }
                    binding.loginStatus.text = message
                    binding.loginSettingsButton.isVisible = errorCode == BiometricPrompt.ERROR_NO_BIOMETRICS || errorCode == BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL
                }
            }
        )
    }

    /**
     * BIOMETRIC_ERROR_NO_HARDWARE significa que no existe hardware compatible;
     * BIOMETRIC_ERROR_HW_UNAVAILABLE indica que existe, pero el servicio no puede usarlo;
     * BIOMETRIC_ERROR_NONE_ENROLLED indica que faltan huellas inscritas y habilita ajustes.
     */
    private fun showBiometricAvailability() {
        binding.loginSettingsButton.isVisible = false
        when (BiometricManager.from(this).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> binding.loginStatus.text = getString(R.string.login_initial_status)
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> binding.loginStatus.text = getString(R.string.biometric_no_hardware)
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> binding.loginStatus.text = getString(R.string.biometric_hardware_unavailable)
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                binding.loginStatus.text = getString(R.string.biometric_none_enrolled)
                binding.loginSettingsButton.isVisible = true
            }
            else -> binding.loginStatus.text = getString(R.string.biometric_no_hardware)
        }
    }

    /** Revalida el estado justo antes de invocar el diálogo para evitar usar hardware/enrolamiento obsoleto. */
    private fun authenticate() {
        when (BiometricManager.from(this).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                val promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle(getString(R.string.biometric_prompt_title))
                    .setSubtitle(getString(R.string.biometric_prompt_subtitle))
                    .setNegativeButtonText(getString(R.string.biometric_negative_button))
                    .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                    .build()
                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> binding.loginStatus.text = getString(R.string.biometric_no_hardware)
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> binding.loginStatus.text = getString(R.string.biometric_hardware_unavailable)
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                binding.loginStatus.text = getString(R.string.biometric_none_enrolled)
                binding.loginSettingsButton.isVisible = true
            }
            else -> binding.loginStatus.text = getString(R.string.biometric_no_hardware)
        }
    }

    /** Android 11+ permite abrir directamente la pantalla de enrolamiento; versiones anteriores abren seguridad. */
    private fun openBiometricSettings() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent(Settings.ACTION_BIOMETRIC_ENROLL).putExtra(
                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                BiometricManager.Authenticators.BIOMETRIC_STRONG
            )
        } else {
            Intent(Settings.ACTION_SECURITY_SETTINGS)
        }
        runCatching {
            startActivity(intent)
        }.onFailure {
            binding.loginStatus.text = getString(R.string.biometric_settings_unavailable)
        }
    }
}
```

#### `documentacion/version-comentada/MainActivity.kt`

```kotlin
/**
 * VERSIÓN COMENTADA DE REFERENCIA. La lógica coincide con la versión limpia compilable.
 * Las explicaciones documentan responsabilidad, ciclo de vida, fórmulas y acceso a Room.
 */
package com.proyecto.gyrocampro

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.proyecto.gyrocampro.databinding.ActivityMainBinding
import com.proyecto.gyrocampro.ui.auth.LoginActivity
import com.proyecto.gyrocampro.utils.AuthSession

/**
 * Contenedor de las tres pantallas autenticadas. El chequeo ocurre antes de inflar
 * el NavHost: una apertura directa sin AuthSession activa se redirige a LoginActivity.
 * La navegación inferior se enlaza al NavController y la Toolbar proporciona el logout.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    /** Aplica la preferencia visual persistida y exige una sesión antes de mostrar contenido protegido. */
    override fun onCreate(savedInstanceState: Bundle?) {
        val darkModeEnabled = getSharedPreferences("gyrocam_preferences", MODE_PRIVATE)
            .getBoolean("dark_mode_enabled", false)
        AppCompatDelegate.setDefaultNightMode(
            if (darkModeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        super.onCreate(savedInstanceState)
        // Esta barrera evita que un Intent interno abra la interfaz si la autenticación no ocurrió en este proceso.
        if (!AuthSession.isAuthenticated) {
            redirectToLogin()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbar)
        binding.mainToolbar.inflateMenu(R.menu.main_toolbar_menu)
        binding.mainToolbar.menu.findItem(R.id.action_logout)?.icon?.setTint(
            ContextCompat.getColor(this, R.color.color_primary)
        )
        binding.mainToolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_logout) {
                performLogout()
                true
            } else {
                false
            }
        }

        // NavHostFragment posee el NavController declarado en nav_graph.xml; BottomNavigationView sigue sus destinos.
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.mainToolbar.title = destination.label ?: getString(R.string.app_name)
        }
    }

    /** Revoca la sesión en memoria y elimina la pila para impedir volver atrás a una pantalla protegida. */
    fun performLogout() {
        AuthSession.clear()
        redirectToLogin()
    }

    /** NEW_TASK + CLEAR_TASK descarta todas las Activities anteriores y establece Login como raíz nueva. */
    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
```

#### `documentacion/version-comentada/ui/customview/SensorHudOverlayView.kt`

```kotlin
/**
 * VERSIÓN COMENTADA DE REFERENCIA. La lógica coincide con la versión limpia compilable.
 * Las explicaciones documentan responsabilidad, ciclo de vida, fórmulas y acceso a Room.
 */
package com.proyecto.gyrocampro.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.proyecto.gyrocampro.R
import java.util.Locale
import kotlin.math.abs

class SensorHudOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { strokeCap = Paint.Cap.ROUND }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textAlign = Paint.Align.CENTER }
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var tiltDegrees = 0f
    private var forceG = 1f
    private var accelerationX = 0f
    private var accelerationY = 0f
    private val emerald = ContextCompat.getColor(context, R.color.color_emerald)
    private val amber = ContextCompat.getColor(context, R.color.color_amber)
    private val alert = ContextCompat.getColor(context, R.color.color_alert)
    private val white = ContextCompat.getColor(context, R.color.color_white)
    private val hudBackground = ContextCompat.getColor(context, R.color.color_hud_background)

    /**
     * Recibe una instantánea ya calculada por CameraHudFragment. La vista no registra
     * sensores: solo representa valores, evitando que el ciclo de vida del Canvas
     * duplique listeners o mantenga referencias a una pantalla destruida.
     */
    fun setTelemetry(tilt: Float, gForce: Float, accelX: Float, accelY: Float) {
        tiltDegrees = tilt
        forceG = gForce
        accelerationX = accelX
        accelerationY = accelY
        invalidate()
    }

    /**
     * Dibuja el HUD en coordenadas de la vista. El horizonte rota por el ángulo filtrado;
     * ±2 grados se considera nivelado. El retículo recibe aceleración lineal (gravedad
     * sustraída) para que no quede desplazado permanentemente por la gravedad terrestre.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val density = resources.displayMetrics.density
        val centerX = width / 2f
        val centerY = height / 2f
        val halfLine = width * 0.34f
        // La condición pedida por la rúbrica es estricta: dentro de ±2° el horizonte es esmeralda.
        val isLevel = abs(tiltDegrees) <= 2f
        val horizonColor = when {
            isLevel -> emerald
            abs(tiltDegrees) <= 8f -> amber
            else -> alert
        }

        canvas.save()
        canvas.rotate(-tiltDegrees, centerX, centerY)
        linePaint.color = horizonColor
        linePaint.strokeWidth = 2.5f * density
        canvas.drawLine(centerX - halfLine, centerY, centerX + halfLine, centerY, linePaint)
        linePaint.strokeWidth = 1.5f * density
        canvas.drawLine(centerX - halfLine, centerY - 10f * density, centerX - halfLine, centerY + 10f * density, linePaint)
        canvas.drawLine(centerX + halfLine, centerY - 10f * density, centerX + halfLine, centerY + 10f * density, linePaint)
        canvas.restore()

        // Se escala la aceleración lineal en unidades de g y se limita el desplazamiento para conservar el retículo visible.
        val reticleOffsetX = (accelerationX / 9.80665f * 28f * density).coerceIn(-42f * density, 42f * density)
        val reticleOffsetY = (-accelerationY / 9.80665f * 28f * density).coerceIn(-42f * density, 42f * density)
        val reticleX = centerX + reticleOffsetX
        val reticleY = centerY + reticleOffsetY
        linePaint.color = white
        linePaint.strokeWidth = 1.4f * density
        val radius = 12f * density
        canvas.drawCircle(reticleX, reticleY, radius, linePaint)
        canvas.drawLine(reticleX - 20f * density, reticleY, reticleX - 5f * density, reticleY, linePaint)
        canvas.drawLine(reticleX + 5f * density, reticleY, reticleX + 20f * density, reticleY, linePaint)
        canvas.drawLine(reticleX, reticleY - 20f * density, reticleX, reticleY - 5f * density, linePaint)
        canvas.drawLine(reticleX, reticleY + 5f * density, reticleX, reticleY + 20f * density, linePaint)

        val status = if (isLevel) {
            resources.getString(R.string.hud_status_level)
        } else {
            String.format(Locale.getDefault(), resources.getString(R.string.hud_status_tilt), tiltDegrees)
        }
        textPaint.color = horizonColor
        textPaint.textSize = 14f * density
        textPaint.isFakeBoldText = true
        canvas.drawText(status, centerX, centerY + 42f * density, textPaint)

        val panelWidth = width * 0.72f
        val panelLeft = centerX - panelWidth / 2f
        val barTop = height - 76f * density
        val barHeight = 9f * density
        fillPaint.color = hudBackground
        canvas.drawRoundRect(
            RectF(panelLeft - 12f * density, barTop - 31f * density, panelLeft + panelWidth + 12f * density, barTop + 22f * density),
            10f * density,
            10f * density,
            fillPaint
        )
        textPaint.color = white
        textPaint.isFakeBoldText = false
        textPaint.textSize = 11f * density
        val forceLabel = String.format(Locale.getDefault(), resources.getString(R.string.hud_force_format), forceG)
        canvas.drawText(forceLabel, centerX, barTop - 10f * density, textPaint)
        // En reposo el módulo de aceleración ronda 1 g; la desviación respecto a 1 g se normaliza para la barra.
        val vibration = (abs(forceG - 1f) / 0.35f).coerceIn(0f, 1f)
        fillPaint.color = ContextCompat.getColor(context, R.color.color_white)
        canvas.drawRoundRect(RectF(panelLeft, barTop, panelLeft + panelWidth, barTop + barHeight), barHeight, barHeight, fillPaint)
        val vibrationColor = when {
            vibration < 0.2f -> emerald
            vibration < 0.55f -> amber
            else -> alert
        }
        fillPaint.color = vibrationColor
        canvas.drawRoundRect(
            RectF(panelLeft, barTop, panelLeft + panelWidth * vibration, barTop + barHeight),
            barHeight,
            barHeight,
            fillPaint
        )
        textPaint.color = white
        canvas.drawText(resources.getString(R.string.hud_vibration_label), centerX, barTop + 18f * density, textPaint)
    }
}
```

#### `documentacion/version-comentada/ui/camera/CameraHudFragment.kt`

```kotlin
/**
 * VERSIÓN COMENTADA DE REFERENCIA. La lógica coincide con la versión limpia compilable.
 * Las explicaciones documentan responsabilidad, ciclo de vida, fórmulas y acceso a Room.
 */
package com.proyecto.gyrocampro.ui.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.proyecto.gyrocampro.R
import com.proyecto.gyrocampro.data.local.AppDatabase
import com.proyecto.gyrocampro.data.local.PhotoCaptureEntity
import com.proyecto.gyrocampro.databinding.DialogEditCaptureBinding
import com.proyecto.gyrocampro.databinding.FragmentCameraHudBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.sqrt
import kotlinx.coroutines.launch

/**
 * Vista de cámara y coordinador de los dos sensores. Registra listeners solo entre
 * onResume/onPause y desliga CameraX al destruir la vista. La referencia _binding
 * se invalida en onDestroyView para no conservar vistas fuera de su ciclo de vida.
 */
class CameraHudFragment : Fragment(), SensorEventListener {
    // El binding pertenece a la vista del Fragment, no a la instancia completa del Fragment.
    private var _binding: FragmentCameraHudBinding? = null
    private val binding get() = _binding!!
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageCapture: ImageCapture? = null
    private var sensorManager: SensorManager? = null
    private var gyroSensor: Sensor? = null
    private var accelerometerSensor: Sensor? = null
    private var gyroX = 0f
    private var gyroY = 0f
    private var gyroZ = 0f
    private var accelerationX = 0f
    private var accelerationY = 0f
    private var accelerationZ = 0f
    private var forceG = 1f
    private var tiltDegrees = 0f
    private var lastGyroTimestampNs = 0L
    private var gravityX = 0f
    private var gravityY = 0f
    private var gravityZ = 0f
    private var hasGravityEstimate = false
    private var hasInitialOrientation = false
    private var linearAccelerationX = 0f
    private var linearAccelerationY = 0f

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startCamera()
        } else if (_binding != null) {
            binding.cameraStatus.text = getString(R.string.camera_permission_denied)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraHudBinding.inflate(inflater, container, false)
        return binding.root
    }

    /** Obtiene sensores disponibles, enlaza el botón y solicita CAMERA en tiempo de ejecución. */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        accelerometerSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        binding.captureButton.setOnClickListener { showCaptureForm() }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            binding.cameraStatus.text = getString(R.string.camera_permission_needed)
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        if (gyroSensor == null || accelerometerSensor == null) {
            binding.cameraStatus.text = getString(R.string.camera_sensor_missing)
        }
    }

    /** Registra el giroscopio y acelerómetro únicamente cuando la pantalla está activa. */
    override fun onResume() {
        super.onResume()
        if (_binding != null && imageCapture == null &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        }
        lastGyroTimestampNs = 0L
        gyroSensor?.let { sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) }
        accelerometerSensor?.let { sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) }
    }

    /** Libera los listeners para reducir consumo y evitar actualizaciones en segundo plano. */
    override fun onPause() {
        sensorManager?.unregisterListener(this)
        super.onPause()
    }

    /**
     * Configura CameraX con Preview e ImageCapture vinculados a viewLifecycleOwner.
     * El Future puede terminar después de una navegación; por eso se verifica _binding
     * antes de tocar la vista y el lifecycle del propietario controla el caso de uso.
     */
    private fun startCamera() {
        if (_binding == null) return
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            if (_binding == null) return@addListener
            try {
                val provider = cameraProviderFuture.get()
                cameraProvider = provider
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = binding.cameraPreview.surfaceProvider
                }
                val captureUseCase = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()
                imageCapture = captureUseCase
                provider.unbindAll()
                provider.bindToLifecycle(
                    viewLifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    captureUseCase
                )
                binding.cameraStatus.text = getString(R.string.sensor_diagnostics_ready)
            } catch (exception: Exception) {
                binding.cameraStatus.text = getString(R.string.camera_unavailable)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    /** Solicita título/nota antes de capturar y reutiliza el layout compartido con el CRUD de galería. */
    private fun showCaptureForm() {
        if (imageCapture == null) {
            binding.cameraStatus.text = getString(R.string.camera_unavailable)
            return
        }
        val form = DialogEditCaptureBinding.inflate(layoutInflater)
        val timeLabel = SimpleDateFormat("HHmmss", Locale.getDefault()).format(Date())
        form.titleInput.setText(getString(R.string.capture_title_format, timeLabel))
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_capture_title)
            .setView(form.root)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.save, null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val title = form.titleInput.text?.toString()?.trim().orEmpty()
                val note = form.noteInput.text?.toString()?.trim().orEmpty()
                val valid = validateCaptureForm(form, title, note)
                if (valid) {
                    dialog.dismiss()
                    takePicture(title, note)
                }
            }
        }
        dialog.show()
    }

    /** TextInputLayout muestra errores accesibles; el límite también se valida en código además de android:maxLength. */
    private fun validateCaptureForm(form: DialogEditCaptureBinding, title: String, note: String): Boolean {
        form.titleInputLayout.error = null
        form.noteInputLayout.error = null
        if (title.isBlank()) {
            form.titleInputLayout.error = getString(R.string.field_title_required)
            return false
        }
        if (title.length > 40) {
            form.titleInputLayout.error = getString(R.string.field_title_too_long)
            return false
        }
        if (note.length > 280) {
            form.noteInputLayout.error = getString(R.string.field_note_too_long)
            return false
        }
        return true
    }

    /**
     * Congela una instantánea de sensores al disparar, escribe el JPEG en filesDir/captures
     * y después inserta sus metadatos en Room desde lifecycleScope. Room ejecuta las
     * operaciones suspend fuera del hilo principal. Si falla la inserción se borra el JPEG
     * para no dejar un archivo huérfano; el contexto de aplicación evita retener una Activity.
     */
    private fun takePicture(title: String, note: String) {
        val captureUseCase = imageCapture ?: return
        val outputDirectory = File(requireContext().filesDir, "captures")
        if (!outputDirectory.exists()) outputDirectory.mkdirs()
        val outputFile = File(outputDirectory, "gyro_${System.currentTimeMillis()}.jpg")
        val capturedAt = System.currentTimeMillis()
        val appContext = requireContext().applicationContext
        // Los valores se copian antes de la operación asíncrona: la telemetría guardada corresponde al instante del disparo.
        val snapshot = SensorSnapshot(
            tiltDegrees = tiltDegrees,
            forceG = forceG,
            gyroX = gyroX,
            gyroY = gyroY,
            gyroZ = gyroZ,
            accelX = accelerationX,
            accelY = accelerationY,
            accelZ = accelerationZ
        )
        val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()
        captureUseCase.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    lifecycleScope.launch {
                        val entity = PhotoCaptureEntity(
                            imagePath = outputFile.absolutePath,
                            title = title,
                            note = note,
                            tiltDegrees = snapshot.tiltDegrees,
                            forceG = snapshot.forceG,
                            gyroX = snapshot.gyroX,
                            gyroY = snapshot.gyroY,
                            gyroZ = snapshot.gyroZ,
                            accelX = snapshot.accelX,
                            accelY = snapshot.accelY,
                            accelZ = snapshot.accelZ,
                            capturedAt = capturedAt
                        )
                        runCatching {
                            AppDatabase.getInstance(appContext).photoCaptureDao().insert(entity)
                        }.onSuccess {
                            if (isAdded) Toast.makeText(requireContext(), R.string.capture_saved, Toast.LENGTH_SHORT).show()
                        }.onFailure {
                            outputFile.delete()
                            if (isAdded) Toast.makeText(requireContext(), R.string.capture_db_error, Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    outputFile.delete()
                    if (isAdded) Toast.makeText(requireContext(), R.string.capture_save_error, Toast.LENGTH_LONG).show()
                }
            }
        )
    }

    /**
     * El giroscopio entrega velocidad angular en rad/s, no un ángulo absoluto. Se integra
     * alrededor del eje Z: Δθ(rad) = ωz(rad/s) × Δt(s), y Math.toDegrees convierte el
     * incremento a grados. Δt se acota para que una pausa larga no genere un salto.
     * El acelerómetro entrega m/s². Su módulo |a| = √(ax²+ay²+az²), dividido entre
     * g0 = 9.80665 m/s², produce la fuerza G. Una estimación pasa-bajas de gravedad
     * permite sustraerla y desplazar el retículo con aceleración lineal.
     * Un filtro complementario corrige lentamente el ángulo integrado: 98 % giroscopio
     * (respuesta rápida) + 2 % estimación del acelerómetro (referencia de baja frecuencia).
     */
    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_GYROSCOPE -> {
                gyroX = event.values[0]
                gyroY = event.values[1]
                gyroZ = event.values[2]
                if (lastGyroTimestampNs != 0L) {
                    val deltaNs = (event.timestamp - lastGyroTimestampNs).coerceIn(0L, 100_000_000L)
                    // Convierte nanosegundos del timestamp del sensor a segundos para multiplicarlos por rad/s.
                    val deltaSeconds = deltaNs / 1_000_000_000f
                    // Fórmula dimensional: (rad/s × s) = rad; Math.toDegrees(rad) = grados acumulados.
                    tiltDegrees += Math.toDegrees((gyroZ * deltaSeconds).toDouble()).toFloat()
                    if (tiltDegrees > 180f) tiltDegrees -= 360f
                    if (tiltDegrees < -180f) tiltDegrees += 360f
                }
                lastGyroTimestampNs = event.timestamp
            }
            Sensor.TYPE_ACCELEROMETER -> {
                accelerationX = event.values[0]
                accelerationY = event.values[1]
                accelerationZ = event.values[2]
                if (!hasGravityEstimate) {
                    gravityX = accelerationX
                    gravityY = accelerationY
                    gravityZ = accelerationZ
                    hasGravityEstimate = true
                } else {
                    val gravityAlpha = 0.8f
                    gravityX = gravityAlpha * gravityX + (1f - gravityAlpha) * accelerationX
                    gravityY = gravityAlpha * gravityY + (1f - gravityAlpha) * accelerationY
                    gravityZ = gravityAlpha * gravityZ + (1f - gravityAlpha) * accelerationZ
                }
                linearAccelerationX = accelerationX - gravityX
                linearAccelerationY = accelerationY - gravityY
                val magnitude = sqrt(
                    accelerationX * accelerationX +
                        accelerationY * accelerationY +
                        accelerationZ * accelerationZ
                )
                // |a| / 9.80665 convierte m/s² a múltiplos de g terrestre.
                forceG = magnitude / SensorManager.GRAVITY_EARTH
                // La estimación de inclinación usa atan2(ax, -ay) asumiendo el dispositivo en orientación vertical.
                val accelerometerTilt = Math.toDegrees(atan2(accelerationX.toDouble(), -accelerationY.toDouble())).toFloat()
                if (!hasInitialOrientation) {
                    tiltDegrees = accelerometerTilt
                    hasInitialOrientation = true
                } else {
                    var correction = accelerometerTilt - tiltDegrees
                    while (correction > 180f) correction -= 360f
                    while (correction < -180f) correction += 360f
                    tiltDegrees += correction * 0.02f
                }
            }
        }
        _binding?.sensorHudOverlay?.setTelemetry(tiltDegrees, forceG, linearAccelerationX, linearAccelerationY)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    /** Desvincula los casos CameraX y nulifica _binding antes de que el Fragment pierda su vista. */
    override fun onDestroyView() {
        cameraProvider?.unbindAll()
        cameraProvider = null
        imageCapture = null
        _binding = null
        super.onDestroyView()
    }

    private data class SensorSnapshot(
        val tiltDegrees: Float,
        val forceG: Float,
        val gyroX: Float,
        val gyroY: Float,
        val gyroZ: Float,
        val accelX: Float,
        val accelY: Float,
        val accelZ: Float
    )
}
```

#### `documentacion/version-comentada/ui/gallery/GalleryCrudFragment.kt`

```kotlin
/**
 * VERSIÓN COMENTADA DE REFERENCIA. La lógica coincide con la versión limpia compilable.
 * Las explicaciones documentan responsabilidad, ciclo de vida, fórmulas y acceso a Room.
 */
package com.proyecto.gyrocampro.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.proyecto.gyrocampro.R
import com.proyecto.gyrocampro.data.local.AppDatabase
import com.proyecto.gyrocampro.data.local.PhotoCaptureEntity
import com.proyecto.gyrocampro.databinding.DialogEditCaptureBinding
import com.proyecto.gyrocampro.databinding.FragmentGalleryCrudBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Pantalla CRUD de capturas. Observa Room como Flow y colecta solo en STARTED;
 * ListAdapter aplica DiffUtil para actualizar únicamente las filas modificadas.
 * Todos los accesos a widgets pasan por ViewBinding y la referencia se limpia al destruir la vista.
 */
class GalleryCrudFragment : Fragment() {
    // Binding válido únicamente entre onCreateView y onDestroyView.
    private var _binding: FragmentGalleryCrudBinding? = null
    private val binding get() = _binding!!
    private val database by lazy { AppDatabase.getInstance(requireContext().applicationContext) }
    private lateinit var captureAdapter: PhotoCaptureAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryCrudBinding.inflate(inflater, container, false)
        return binding.root
    }

    /** Configura el adapter y recoge la consulta observable mientras el Fragment está visible. */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        captureAdapter = PhotoCaptureAdapter(
            onDetails = ::showDetails,
            onEdit = ::showEditDialog,
            onDelete = ::confirmDelete
        )
        binding.captureRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.captureRecycler.adapter = captureAdapter

        // repeatOnLifecycle cancela la colección al salir de STARTED y la reanuda al volver a la pantalla.
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                database.photoCaptureDao().observeAll().collect { captures ->
                    captureAdapter.submitList(captures)
                    binding.galleryCount.text = getString(R.string.gallery_count, captures.size)
                    binding.galleryEmptyState.visibility = if (captures.isEmpty()) View.VISIBLE else View.GONE
                    binding.captureRecycler.visibility = if (captures.isEmpty()) View.GONE else View.VISIBLE
                }
            }
        }
    }

    /** La consulta de detalle presenta metadatos, fecha, grados, fuerza G y los seis valores instantáneos de los sensores. */
    private fun showDetails(capture: PhotoCaptureEntity) {
        val date = SimpleDateFormat(getString(R.string.date_pattern), Locale.getDefault())
            .format(Date(capture.capturedAt))
        val details = getString(
            R.string.capture_details_format,
            capture.title,
            capture.note.ifBlank { getString(R.string.no_note) },
            date,
            capture.tiltDegrees,
            capture.forceG,
            capture.gyroX,
            capture.gyroY,
            capture.gyroZ,
            capture.accelX,
            capture.accelY,
            capture.accelZ
        )
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(capture.title)
            .setMessage(details)
            .setPositiveButton(R.string.cancel, null)
            .show()
    }

    /** Actualización: el formulario limita longitud, valida título y persiste el nuevo título/nota mediante Room. */
    private fun showEditDialog(capture: PhotoCaptureEntity) {
        val form = DialogEditCaptureBinding.inflate(layoutInflater)
        form.titleInput.setText(capture.title)
        form.noteInput.setText(capture.note)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_edit_title)
            .setView(form.root)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.save, null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val title = form.titleInput.text?.toString()?.trim().orEmpty()
                val note = form.noteInput.text?.toString()?.trim().orEmpty()
                if (validateForm(form, title, note)) {
                    dialog.dismiss()
                    lifecycleScope.launch {
                        database.photoCaptureDao().update(capture.copy(title = title, note = note))
                        if (isAdded) Toast.makeText(requireContext(), R.string.capture_updated, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        dialog.show()
    }

    /** Mantiene los mensajes de validación en TextInputLayout para asociarlos visualmente al campo correspondiente. */
    private fun validateForm(form: DialogEditCaptureBinding, title: String, note: String): Boolean {
        form.titleInputLayout.error = null
        form.noteInputLayout.error = null
        if (title.isBlank()) {
            form.titleInputLayout.error = getString(R.string.field_title_required)
            return false
        }
        if (title.length > 40) {
            form.titleInputLayout.error = getString(R.string.field_title_too_long)
            return false
        }
        if (note.length > 280) {
            form.noteInputLayout.error = getString(R.string.field_note_too_long)
            return false
        }
        return true
    }

    /** Eliminación confirmada: borra la fila Room y el JPEG privado asociado. */
    private fun confirmDelete(capture: PhotoCaptureEntity) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.confirm_delete_title)
            .setMessage(R.string.confirm_delete_message)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ ->
                lifecycleScope.launch {
                    database.photoCaptureDao().delete(capture)
                    File(capture.imagePath).delete()
                    if (isAdded) Toast.makeText(requireContext(), R.string.capture_deleted, Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    /** Desconecta el adapter del RecyclerView y nulifica _binding para no filtrar el árbol de vistas. */
    override fun onDestroyView() {
        binding.captureRecycler.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
```

#### `documentacion/version-comentada/ui/gallery/PhotoCaptureAdapter.kt`

```kotlin
/**
 * VERSIÓN COMENTADA DE REFERENCIA. La lógica coincide con la versión limpia compilable.
 * Las explicaciones documentan responsabilidad, ciclo de vida, fórmulas y acceso a Room.
 */
package com.proyecto.gyrocampro.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.proyecto.gyrocampro.R
import com.proyecto.gyrocampro.data.local.PhotoCaptureEntity
import com.proyecto.gyrocampro.databinding.ItemPhotoCaptureBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PhotoCaptureAdapter(
    private val onDetails: (PhotoCaptureEntity) -> Unit,
    private val onEdit: (PhotoCaptureEntity) -> Unit,
    private val onDelete: (PhotoCaptureEntity) -> Unit
) : ListAdapter<PhotoCaptureEntity, PhotoCaptureAdapter.PhotoCaptureViewHolder>(DiffCallback) {

    /** Infla item_photo_capture mediante ItemPhotoCaptureBinding, sin findViewById. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoCaptureViewHolder {
        val binding = ItemPhotoCaptureBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoCaptureViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoCaptureViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PhotoCaptureViewHolder(
        private val binding: ItemPhotoCaptureBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        /** Renderiza imagen y telemetría, y delega las acciones al Fragment propietario. */
        fun bind(item: PhotoCaptureEntity) {
            binding.photoTitle.text = item.title
            binding.photoThumbnail.contentDescription = item.title
            val date = SimpleDateFormat(
                binding.root.context.getString(R.string.date_pattern),
                Locale.getDefault()
            ).format(Date(item.capturedAt))
            binding.photoDate.text = date
            binding.photoTelemetry.text = binding.root.context.getString(
                R.string.item_telemetry_format,
                item.tiltDegrees,
                item.forceG
            )
            val imageFile = File(item.imagePath)
            if (imageFile.exists()) {
                binding.photoThumbnail.load(imageFile) {
                    crossfade(true)
                }
            } else {
                binding.photoThumbnail.setImageResource(com.proyecto.gyrocampro.R.drawable.ic_gallery)
            }
            binding.buttonDetails.setOnClickListener { onDetails(item) }
            binding.buttonEdit.setOnClickListener { onEdit(item) }
            binding.buttonDelete.setOnClickListener { onDelete(item) }
        }
    }

    /** DiffUtil compara identidad por id y contenido por igualdad de los campos de la entidad. */
    private companion object DiffCallback : DiffUtil.ItemCallback<PhotoCaptureEntity>() {
        override fun areItemsTheSame(oldItem: PhotoCaptureEntity, newItem: PhotoCaptureEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PhotoCaptureEntity, newItem: PhotoCaptureEntity): Boolean {
            return oldItem == newItem
        }
    }
}
```

#### `documentacion/version-comentada/ui/sensors/SensorLabFragment.kt`

```kotlin
/**
 * VERSIÓN COMENTADA DE REFERENCIA. La lógica coincide con la versión limpia compilable.
 * Las explicaciones documentan responsabilidad, ciclo de vida, fórmulas y acceso a Room.
 */
package com.proyecto.gyrocampro.ui.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.proyecto.gyrocampro.MainActivity
import com.proyecto.gyrocampro.R
import com.proyecto.gyrocampro.databinding.FragmentSensorLabBinding
import java.util.Locale
import kotlin.math.abs

/**
 * Diagnóstico de ejes y ajuste visual. Los listeners viven solo entre onResume/onPause;
 * la pantalla muestra los tres componentes del giroscopio (rad/s) y acelerómetro (m/s²).
 */
class SensorLabFragment : Fragment(), SensorEventListener {
    // Esta referencia se invalida en onDestroyView, no en onDestroy, porque Fragment puede recrear su vista.
    private var _binding: FragmentSensorLabBinding? = null
    private val binding get() = _binding!!
    private var sensorManager: SensorManager? = null
    private var gyroscope: Sensor? = null
    private var accelerometer: Sensor? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSensorLabBinding.inflate(inflater, container, false)
        return binding.root
    }

    /** Detecta hardware, restaura preferencia del tema y conecta el botón de cierre de sesión. */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroscope = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        binding.sensorStatus.text = if (gyroscope == null || accelerometer == null) {
            getString(R.string.sensor_unavailable)
        } else {
            getString(R.string.sensor_diagnostics_ready)
        }

        val preferences = requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        binding.darkModeSwitch.isChecked = preferences.getBoolean(KEY_DARK_MODE, false)
        binding.darkModeSwitch.setOnCheckedChangeListener { _, enabled ->
            preferences.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (enabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
        binding.sensorLogoutButton.setOnClickListener {
            (activity as? MainActivity)?.performLogout()
        }
    }

    /** Activa la recepción de eventos cuando el panel está en primer plano. */
    override fun onResume() {
        super.onResume()
        gyroscope?.let { sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        accelerometer?.let { sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }

    /** Desregistra ambos sensores antes de abandonar el panel. */
    override fun onPause() {
        sensorManager?.unregisterListener(this)
        super.onPause()
    }

    /** Actualiza únicamente el grupo de barras perteneciente al tipo de sensor que reportó el evento. */
    override fun onSensorChanged(event: SensorEvent) {
        val currentBinding = _binding ?: return
        val values = event.values
        when (event.sensor.type) {
            Sensor.TYPE_GYROSCOPE -> {
                renderAxis(values[0], currentBinding.gyroXProgress, currentBinding.gyroXValue, 5f)
                renderAxis(values[1], currentBinding.gyroYProgress, currentBinding.gyroYValue, 5f)
                renderAxis(values[2], currentBinding.gyroZProgress, currentBinding.gyroZValue, 5f)
            }
            Sensor.TYPE_ACCELEROMETER -> {
                renderAxis(values[0], currentBinding.accelXProgress, currentBinding.accelXValue, 20f)
                renderAxis(values[1], currentBinding.accelYProgress, currentBinding.accelYValue, 20f)
                renderAxis(values[2], currentBinding.accelZProgress, currentBinding.accelZValue, 20f)
            }
        }
    }

    /** Normaliza la magnitud absoluta a 0–100; el TextView conserva el signo y el valor numérico real. */
    private fun renderAxis(value: Float, progressBar: ProgressBar, valueView: TextView, scaleMaximum: Float) {
        progressBar.progress = ((abs(value) / scaleMaximum) * 100f).toInt().coerceIn(0, 100)
        valueView.text = String.format(Locale.getDefault(), getString(R.string.axis_value_format), value)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    /** Rompe la referencia al binding cuando el árbol de vistas deja de existir. */
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val PREFERENCES_NAME = "gyrocam_preferences"
        const val KEY_DARK_MODE = "dark_mode_enabled"
    }
}
```

#### `documentacion/version-comentada/data/local/PhotoCaptureEntity.kt`

```kotlin
/**
 * VERSIÓN COMENTADA DE REFERENCIA. La lógica coincide con la versión limpia compilable.
 * Las explicaciones documentan responsabilidad, ciclo de vida, fórmulas y acceso a Room.
 */
package com.proyecto.gyrocampro.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room. imagePath apunta al JPEG en almacenamiento privado; tiltDegrees y forceG
 * son los valores derivados del instante de captura. gyroX/Y/Z se expresan en rad/s y
 * accelX/Y/Z en m/s² para conservar también los valores físicos originales.
 */
@Entity(tableName = "photo_captures")
data class PhotoCaptureEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "image_path")
    val imagePath: String,
    val title: String,
    val note: String,
    @ColumnInfo(name = "tilt_degrees")
    val tiltDegrees: Float,
    @ColumnInfo(name = "force_g")
    val forceG: Float,
    @ColumnInfo(name = "gyro_x_rad_s")
    val gyroX: Float,
    @ColumnInfo(name = "gyro_y_rad_s")
    val gyroY: Float,
    @ColumnInfo(name = "gyro_z_rad_s")
    val gyroZ: Float,
    @ColumnInfo(name = "accel_x_m_s2")
    val accelX: Float,
    @ColumnInfo(name = "accel_y_m_s2")
    val accelY: Float,
    @ColumnInfo(name = "accel_z_m_s2")
    val accelZ: Float,
    @ColumnInfo(name = "captured_at")
    val capturedAt: Long
)
```

#### `documentacion/version-comentada/data/local/PhotoCaptureDao.kt`

```kotlin
/**
 * VERSIÓN COMENTADA DE REFERENCIA. La lógica coincide con la versión limpia compilable.
 * Las explicaciones documentan responsabilidad, ciclo de vida, fórmulas y acceso a Room.
 */
package com.proyecto.gyrocampro.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/** Contrato de acceso a SQLite: Flow para observación reactiva y suspend para operaciones CRUD. */
@Dao
interface PhotoCaptureDao {
    // Room invalida y vuelve a emitir la consulta cuando cambia la tabla; la galería no necesita refresco manual.
    @Query("SELECT * FROM photo_captures ORDER BY captured_at DESC")
    fun observeAll(): Flow<List<PhotoCaptureEntity>>

    @Query("SELECT * FROM photo_captures WHERE id = :captureId LIMIT 1")
    suspend fun findById(captureId: Long): PhotoCaptureEntity?

    // INSERT devuelve el identificador autogenerado, útil para futuras operaciones sobre la captura.
    @Insert
    suspend fun insert(capture: PhotoCaptureEntity): Long

    // UPDATE modifica la fila por su clave primaria; se usa para editar título y nota sin alterar telemetría.
    @Update
    suspend fun update(capture: PhotoCaptureEntity)

    // DELETE elimina la entidad seleccionada tras la confirmación del usuario.
    @Delete
    suspend fun delete(capture: PhotoCaptureEntity)

    @Query("DELETE FROM photo_captures WHERE id = :captureId")
    suspend fun deleteById(captureId: Long)
}
```

#### `documentacion/version-comentada/data/local/AppDatabase.kt`

```kotlin
/**
 * VERSIÓN COMENTADA DE REFERENCIA. La lógica coincide con la versión limpia compilable.
 * Las explicaciones documentan responsabilidad, ciclo de vida, fórmulas y acceso a Room.
 */
package com.proyecto.gyrocampro.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PhotoCaptureEntity::class],
    version = 1,
    exportSchema = false
)
/** Base Room singleton: evita crear conexiones duplicadas durante la vida del proceso. */
abstract class AppDatabase : RoomDatabase() {
    abstract fun photoCaptureDao(): PhotoCaptureDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /** Double-checked locking y applicationContext evitan fugas de Activities/Fragments. */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gyrocam_pro.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
```

#### `documentacion/version-comentada/utils/AuthSession.kt`

```kotlin
/**
 * VERSIÓN COMENTADA DE REFERENCIA. La lógica coincide con la versión limpia compilable.
 * Las explicaciones documentan responsabilidad, ciclo de vida, fórmulas y acceso a Room.
 */
package com.proyecto.gyrocampro.utils

/**
 * Estado de autenticación en memoria del proceso. Solo LoginActivity puede activar
 * markAuthenticated desde el callback de éxito; logout revoca la bandera.
 * Si el proceso muere, el valor vuelve a false: la sesión no se restaura sin nueva huella.
 */
object AuthSession {
    // Volatile asegura visibilidad del valor si el estado se consulta desde otro hilo.
    @Volatile
    var isAuthenticated: Boolean = false
        private set

    fun markAuthenticated() {
        isAuthenticated = true
    }

    fun clear() {
        isAuthenticated = false
    }
}
```

# SECCIÓN 6: Plan de ocho commits significativos para Git

1. **`chore: bootstrap proyecto Android Kotlin y configuración Gradle`**  
   Inicializar namespace, SDK 24/35/36, AGP, Kotlin, ViewBinding, Material 3 y dependencias AndroidX.
2. **`feat(auth): añadir LoginActivity y autenticación BiometricPrompt`**  
   Implementar comprobación de hardware/enrolamiento, estados de error, ajustes de huellas y barrera de acceso.
3. **`feat(navigation): integrar MainActivity con Navigation Component`**  
   Añadir NavHostFragment, grafo, BottomNavigationView, Toolbar y cierre de sesión con limpieza de pila.
4. **`feat(camera): implementar visor CameraX y solicitud de permiso`**  
   Vincular Preview/ImageCapture al lifecycle del Fragment y manejar concesión/denegación de CAMERA.
5. **`feat(sensors): dibujar HUD y diagnóstico de telemetría`**  
   Registrar gyro/accelerometer por ciclo de vida, integrar velocidad angular, calcular fuerza G y renderizar Canvas.
6. **`feat(data): persistir capturas y telemetría con Room`**  
   Crear entidad, DAO, base singleton, escritura de archivos privados y colección Flow en galería.
7. **`feat(gallery): completar CRUD y formularios validados`**  
   Añadir ListAdapter/DiffUtil, detalle, edición con TextInputLayout y eliminación confirmada de archivo/fila.
8. **`docs: documentar arquitectura, pruebas y código comentado`**  
   Añadir manual técnico, escenarios biométricos, instrucciones de permisos y código comentado.

## Criterios de verificación antes de entregar

- Abrir el proyecto con JDK 17, Android SDK Platform 36 y sincronizar las dependencias.
- Ejecutar `assembleDebug` y probar en al menos un emulador API 24+ y un dispositivo con huella/cámara/sensores.
- Verificar el escenario de no hardware, sin huellas, fallo de coincidencia, cancelación y lockout.
- Revisar que `findViewById`/synthetics no aparezcan y que todos los Fragments limpien `_binding` en `onDestroyView()`.
- Comprobar permisos denegados, navegación de Atrás, recreación por cambio de tema, CRUD y persistencia tras reinicio.

---

**Nota de validación de esta entrega:** los 20 recursos XML fueron parseados estáticamente, se verificaron referencias a `strings`, drawables, layouts, menús, navegación y colores, y no se encontraron llamadas `findViewById` ni Kotlin synthetics en los fuentes principales. No se declara una compilación ejecutada: el entorno de generación no tenía Gradle ni Android SDK.
