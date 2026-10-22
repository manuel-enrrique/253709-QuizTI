# AdivinaTI 🎯

Juego de adivinanzas de tecnología para Android, **100% offline**.

Cada ronda son 5 adivinanzas de la dificultad elegida. Acierta **sin pista** para sumar
**2 puntos**, con pista suma **1 punto** y fallar suma **0**. La puntuación máxima de una
ronda es de **10 puntos**.

---

## Stack tecnológico

| Elemento | Tecnología |
|---|---|
| Lenguaje | Kotlin nativo |
| UI | Jetpack Compose + Material Design 3 |
| Arquitectura | MVVM estricto (Model / View-ViewModel) + UDF |
| Flujo de estado | `StateFlow` (privado mutable en el ViewModel, inmutable en la UI) |
| Persistencia | Preferences DataStore + JSON (`org.json`) |
| Navegación | Navigation Compose |
| Inyección de dependencias | `AppContainer` manual (sin Hilt) |

### Restricciones respetadas

- ❌ Sin SQLite
- ❌ Sin Room
- ❌ Sin APIs ni servicios remotos (la app no declara permiso de red)
- ✅ Persistencia 100% local en DataStore

---

## Estructura del proyecto

```
app/src/main/
├── assets/
│   └── riddles.json                  # 15 adivinanzas semilla (5 fácil / 5 media / 5 difícil)
└── java/com/example/adivinati/
    ├── AdivinaTIApplication.kt       # crea el AppContainer
    ├── MainActivity.kt               # setContent + CompositionLocalProvider + NavHost
    ├── core/
    │   ├── AppContainer.kt           # DI manual (DataStore + data sources + repositorio)
    │   ├── DateFormatter.kt          # formateo de fechas (java.time)
    │   └── GameRules.kt              # reglas de puntuación (constantes puras)
    ├── data/                         # ── CAPA DE DATOS ──
    │   ├── model/
    │   │   ├── Riddle.kt             # data class Riddle + enum Difficulty
    │   │   └── GameResult.kt         # data class GameResult
    │   ├── json/
    │   │   └── JsonMappers.kt        # org.json <-> Riddle / GameResult
    │   ├── local/
    │   │   ├── SeedRiddlesDataSource.kt  # lee assets/riddles.json (con retardo simulado)
    │   │   ├── UserRiddlesDataSource.kt   # DataStore <-> JSON (adivinanzas del usuario)
    │   │   └── GameResultDataSource.kt    # DataStore <-> JSON (historial)
    │   └── repository/
    │       └── RiddleRepository.kt   # única puerta de entrada a los datos
    └── ui/                           # ── CAPA DE PRESENTACIÓN ──
        ├── AppViewModel.kt           # CompositionLocal + factoría manual de ViewModels
        ├── navigation/
        │   ├── Routes.kt             # rutas y argumentos del grafo
        │   └── AdivinaNavHost.kt     # menú → partida → resultado
        ├── components/               # DifficultySelector, OptionButton
        ├── menu/    MenuViewModel · MenuUiState · MenuScreen
        ├── game/    GameViewModel · GameUiState · GameScreen
        ├── result/  ResultViewModel · ResultUiState · ResultScreen
        ├── addriddle/ AddRiddleViewModel · AddRiddleUiState · AddRiddleScreen
        └── history/ HistoryViewModel · HistoryUiState · HistoryScreen
```

---

## Arquitectura

### Separación de capas

```
   UI (Composable)  ──eventos──▶  ViewModel  ──suspend fun──▶  Repository
        ▲                              │                           │
        └──── StateFlow<UiState> ──────┘                     DataStore
                                                                      + assets
```

- **La UI no guarda estado lógico**: todo vive en un `UiState` inmutable que el
  ViewModel expone como `StateFlow`. Las pantallas son "tontas" y sólo envían eventos.
- **La UI nunca toca la persistencia**: sólo conoce el `RiddleRepository`.
- **El repositorio es el único** que habla con DataStore y con los assets.

### Flujo unidireccional (UDF)

1. La UI dispara un evento (`viewModel.onOptionSelected(...)`).
2. El ViewModel lo procesa y actualiza su `MutableStateFlow` privado.
3. La UI recoge el `StateFlow` con `collectAsStateWithLifecycle()` y se redibuja.
4. Los eventos de una sola vez (navegar al resultado, guardar y salir) viajan por un
   `Channel` expuesto como `Flow`, para no re-emitirse en cada recomposición.

### Pantallas

| Pantalla | Qué hace |
|---|---|
| **MenuScreen** | Elige dificultad (RadioButtons) y ofrece *Jugar*, *Agregar Adivinanza* y *Ver Historial*. Muestra cuántas adivinanzas hay disponibles. |
| **GameScreen** | Muestra una adivinanza por vez: enunciado, botón de pista, 4 opciones, feedback de acierto/error y avance a la siguiente. |
| **ResultScreen** | Muestra el puntaje (`8/10`), los aciertos, la dificultad y la fecha. El `GameResult` ya se ha guardado en DataStore. |
| **AddRiddleScreen** | Formulario con `OutlinedTextField` para enunciado, pista y 4 opciones, `RadioButton` para la opción correcta y para la dificultad. Valida que no haya campos vacíos. |
| **HistoryScreen** | `LazyColumn` con las partidas guardadas y el promedio de puntos. |

---

## Persistencia

Una única instancia de Preferences DataStore (`adivinati_prefs`) con dos claves:

| Clave | Contenido |
|---|---|
| `user_riddles` | Array JSON con las adivinanzas creadas por el usuario |
| `game_results` | Array JSON con el historial de partidas |

Las adivinanzas semilla se leen de `assets/riddles.json` al arrancar (con un retardo
simulado de 700 ms) y se cachean en memoria.

---

## Tests

### Unitarios (JVM, sin emulador)

```bash
./gradlew :app:testDebugUnitTest
```

- `GameRulesTest` — reglas de puntuación (2 / 1 / 0 y máximo de 10).
- `JsonMappersTest` — ciclos de codificación y decodificación JSON.
- `SeedRiddlesTest` — valida el JSON semilla (15 adivinanzas, 4 opciones, correctIndex válido).

### De UI (instrumentación, necesitan emulador o móvil)

```bash
./gradlew :app:connectedDebugAndroidTest
```

- `ScreensContentTest` — renderizado de las 5 pantallas `*Content` (sin estado propio)
  usando `createComposeRule()`: menús, contador de adivinanzas, punctuación
  (+2 / +1 / 0), estados vacío / con partidas, mensajes de validación del formulario.
- `AdivinaTIFlowTest` — flujo end-to-end sobre `MainActivity` real:
  Menú → 5 preguntas → Resultado → Menú → Historial, comprobando que la partida
  **se persiste en DataStore** (el historial crece en 1 y sobrevive a un
  `recreate()` de la Activity, equivalente a cerrar y reabrir la app).

> Los tests de UI son deterministas: no dependen del azar de la mezcla de
> adivinanzas, sólo del flujo y de la persistencia.

---

## Cómo compilar

```bash
./gradlew :app:assembleDebug
```

- Gradle 9.6 · AGP 9.4.1 · Kotlin 2.2.10 · Compose BOM 2026.02.01
- `minSdk 26` · `targetSdk 37`

---

## Cómo ejecutar (dispositivo virtual)

El juego funciona **100% offline**, así que no necesita ni servidor ni cuenta de Google:
basta cualquier emulador (AVD) o un móvil con depuración USB.

### Opción A · Emulador de Android Studio (GUI)

1. **Tools → Device Manager** → botón **+** → *New Virtual Device*.
2. Hardware: **Pixel 7** → *Next*.
3. System Image: **API 36** con variante **Google APIs** (x86_64) → *Next* → *Finish*.
   - Android Studio descarga la imagen sola si no la tienes (~1,3 GB).
4. Clic derecho sobre el AVD → **Edit** → **Memory**: `2048 MB` → *Show Advanced Settings* →
   **Multi-core CPU**: `2`.
5. Pulsa **▶**. El primer arranque tarda 1-3 minutos.
   - Si se queda colgado: menú **⋮** → **Cold Boot Now**; si persiste, **Wipe Data**.

### Opción B · Por consola (reproducible)

```powershell
# 1. Imagen de sistema (solo la primera vez)
android sdk install "system-images;android-36;google_apis;x86_64"

# 2. Crear el dispositivo virtual
avdmanager create avd -n AdivinaTI_API36 `
  -k "system-images;android-36;google_apis;x86_64" -d pixel_7

# 3. Arrancar (2048 MB y 2 núcleos para portátiles con 8 GB)
emulator -avd AdivinaTI_API36 -memory 2048 -cores 2
```

### Instalar y abrir la app

```powershell
adb wait-for-device
adb install -r app\build\outputs\apk\debug\app-debug.apk
adb shell am start -n com.example.adivinati/.MainActivity
```

> El AVD creado por consola **también aparece en el Device Manager** de Android Studio,
> así que puedes lanzarlo desde la interfaz gráfica cuando quieras.

### Verificar que la app es 100% offline

```powershell
# El APK no debe declarar ningún permiso de red
aapt2 dump permissions app\build\outputs\apk\debug\app-debug.apk
```

El único permiso del manifest fusionado es el autogenerado
`DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`; **no aparece `INTERNET`**.
