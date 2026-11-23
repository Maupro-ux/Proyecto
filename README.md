# GyroCam Pro

Aplicación Android nativa en Kotlin: CameraX, HUD de giroscopio/acelerómetro, autenticación biométrica, navegación inferior, CRUD de fotografías y persistencia local con Room.

## Requisitos de desarrollo

- Android Studio compatible con AGP 8.11.1.
- JDK 17.
- Android SDK Platform 36 y Build Tools instalados.
- Dispositivo/emulador API 24 o superior; para probar huella, perfil con hardware biométrico y al menos una huella registrada.

## Abrir y ejecutar

1. Descomprime el proyecto y abre la carpeta `GyroCamPro` desde Android Studio.
2. Sincroniza Gradle y acepta la instalación del SDK Platform 36 si Android Studio lo solicita.
3. Ejecuta la configuración `app` en un dispositivo o emulador.
4. En el primer acceso registra una huella en los ajustes del sistema; en la pantalla inicial pulsa **Autenticar con huella**.
5. Concede el permiso de cámara cuando Android lo solicite.

El archivo `app/build.gradle.kts` contiene las versiones y dependencias. El proyecto fuente incluye `settings.gradle.kts`, el build raíz y `gradle.properties`. El wrapper estándar (`gradlew`, `gradlew.bat` y `gradle-wrapper.jar`) no se incluye porque este entorno de generación no dispone de Gradle instalado para generarlo; en una máquina con Gradle 8.13 instalado se puede crear con `gradle wrapper --gradle-version 8.13`.

## Estructura

- `ui/auth`: entrada biométrica y control de la sesión.
- `ui/camera`: CameraX, permisos, captura y lectura de sensores.
- `ui/gallery`: lista reactiva y operaciones CRUD.
- `ui/sensors`: panel de los seis ejes y ajuste de tema.
- `ui/customview`: HUD dibujado sobre Canvas.
- `data/local`: entidad, DAO y base de datos Room.
- `utils`: sesión de autenticación en memoria.

## Almacenamiento

Las fotografías se escriben en el directorio privado `filesDir/captures`; no se requiere permiso de almacenamiento compartido. Room guarda la ruta privada, título, nota, inclinación, fuerza G, ejes del giroscopio en rad/s, ejes del acelerómetro en m/s² y timestamp.

## Documentación completa

Consulta `documentacion/Entrega_GyroCam_Pro.md`. Contiene el árbol completo, diseño y flujo, configuración y XML completos, todas las clases Kotlin en versión limpia y comentada, y el plan de ocho commits. La carpeta `documentacion/version-comentada/` contiene los archivos `.kt` comentados; no está dentro del source set de Gradle para evitar definiciones duplicadas.

## Verificación de esta entrega

Se validó el parseo de los XML y la existencia de referencias a recursos, y se comprobó que el código principal no utiliza `findViewById` ni Kotlin synthetics. No se ejecutó `assembleDebug`: el entorno donde se generó el proyecto no contiene Android SDK ni Gradle, por lo que la sincronización y compilación final deben confirmarse en Android Studio.
