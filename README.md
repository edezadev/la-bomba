# LaBomba 🎮💣

Una aplicación Android emocionante para jugar con amigos. ¡Responde preguntas antes de que explote la bomba!

## 🎯 Características Principales

- 🎪 **Temporizador Dinámico**: Configura el tiempo de la bomba
- 📚 **Temas Personalizados**: Crea temas propios para las preguntas
- 🎭 **Castigos Entretenidos**: Castigos personalizados para el perdedor
- 👥 **Multijugador**: Juega con 2 o más jugadores
- ☁️ **Almacenamiento en la Nube**: Tus temas y castigos se guardan en Firebase
- 📶 **Modo Offline**: Juega sin internet (se requiere conexión para guardar datos)

## 📋 Requisitos Técnicos

- **Android**: 7.0 (API 25) o superior
- **RAM Mínima**: 2GB
- **Almacenamiento**: 50MB libres

## 🚀 Instalación

### Desde Código Fuente

```bash
# Clonar repositorio
git clone https://github.com/edezadev/la-bomba.git
cd la-bomba

# Compilar
gradlew build

# Instalar en dispositivo/emulador
gradlew installDebug
```

## 🛠️ Stack Técnico

- **Lenguaje**: Kotlin 100%
- **UI**: Material Design 3
- **Base de Datos**: Firebase Firestore
- **Autenticación**: Firebase Auth (Anónimo)
- **Analytics**: Firebase Analytics
- **Ads**: Google Mobile Ads (AdMob)
- **Media**: ExoPlayer 3

## 📐 Estructura del Proyecto

```
app/src/main/java/com/edeza/labomba/
├── config/          # Firebase, Auth config
├── controllers/     # Activities, Fragments, Adapters
├── models/          # Data classes
└── utils/           # Utilities, Constants, Listeners
```

## 🎮 Cómo Jugar

1. **Configura la Partida**:
    - Establece un castigo (opcional)
    - Agrega jugadores (mínimo 2)
    - Selecciona temas (mínimo 5)
    - Elige tiempo de bomba

2. **Juega**:
    - El juego mostrará un tema, ej: "Nombre de animales que empiecen con A"
    - Responde correctamente y pasa la bomba rápido
    - ¡No dejes que explote!

3. **Resultados**:
    - Quien tiene más rondas perdidas acumuladas pierde
    - Debe cumplir el castigo si es necesario

## 🔐 Seguridad y Privacidad

- Autenticación anónima (sin email requerido)
- Datos protegidos por Firebase Firestore
- Código ofuscado con ProGuard/R8 en release
- Ver [Política de Privacidad](PRIVACY.md)

## 📄 Licencia

Este proyecto está bajo la Licencia Apache 2.0. Ver el archivo [LICENSE](LICENSE) para más detalles.

## 👨‍💻 Autor

Desarrollado por [Estefano Deza](https://github.com/edezadev)

## 📞 Soporte

- 🐛 [Reportar un Bug](https://github.com/edezadev/la-bomba/issues)
- 💡 [Sugerir Feature](https://github.com/edezadev/la-bomba/discussions)

---

**Última actualización**: Mayo 2026