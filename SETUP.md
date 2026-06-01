# 🛠️ Cómo Ejecutar el Proyecto

## Requisitos

- Android Studio Ladybug+
- Android SDK 35
- Java 8+

## Pasos

### 1. Clonar el repositorio
```bash
git clone https://github.com/edezadev/la-bomba.git
cd la-bomba
```

### 2. Configurar Firebase

- Ve a https://console.firebase.google.com/
- Crea un nuevo proyecto
- Descarga `google-services.json`
- Coloca el archivo en `app/google-services.json`

### 3. Compilar
```bash
./gradlew build
```

### 4. Ejecutar
```bash
./gradlew installDebug
```

## Estructura del Código

```
app/src/main/java/com/edeza/labomba/
├── config/          # Firebase managers
├── controllers/     # Activities
├── models/          # Data classes
└── utils/           # Logger, Constants
```
