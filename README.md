# PetWise KMP - Frontend Multiplataforma

Frontend da plataforma PetWise desenvolvido em Kotlin Multiplatform (KMP) com Compose Multiplatform.

## Tecnologias Utilizadas

- **Kotlin Multiplatform (KMP)** - Compartilhamento de código
- **Compose Multiplatform** - UI declarativa
- **Ktor** - Cliente HTTP para APIs
- **Kotlinx Serialization** - Serialização JSON
- **SQLDelight** - Banco local (opcional)
- **Koin** - Injeção de dependência
- **Coil** - Carregamento de imagens

## Plataformas Suportadas

- **Android** - Aplicativo nativo
- **iOS** - Aplicativo nativo
- **Desktop (JVM)** - Aplicação desktop
- **Web (Wasm/JS)** - Aplicação web

## Arquitetura

O projeto segue o padrão **MVVM** com compartilhamento de código:

```
composeApp/src/
├── commonMain/          # Código compartilhado
│   ├── kotlin/
│   │   ├── data/       # Repositórios e APIs
│   │   ├── domain/     # Modelos e lógica de negócio
│   │   ├── presentation/ # ViewModels e Estados
│   │   └── ui/         # Componentes compartilhados
│   └── resources/      # Recursos compartilhados
├── androidMain/        # Específico do Android
├── iosMain/           # Específico do iOS
├── desktopMain/       # Específico do Desktop
└── webMain/           # Específico da Web
```

## Funcionalidades

### Gestão de Pets
- 📱 Listagem de pets do usuário
- ➕ Cadastro de novos pets
- ✏️ Edição de informações
- ⭐ Marcar/desmarcar favoritos
- 🗑️ Remoção de pets

### Perfil do Usuário
- 👤 Visualização do perfil
- 🔐 Alteração de senha
- 📊 Estatísticas de pets

### Funcionalidades Veterinárias
- 💉 Histórico de vacinas
- 📋 Prescrições médicas
- 🧪 Resultados de exames
- 📅 Agendamento de consultas

## Como Executar

### Pré-requisitos
- JDK 21+
- Android Studio (para Android/iOS)
- Xcode (para iOS)
- Node.js (para web)

### Android
```bash
cd kmp
./gradlew :composeApp:assembleDebug
# Instalar no dispositivo/emulador via Android Studio
```

### Desktop (JVM)
```bash
cd kmp
./gradlew :composeApp:run
```

### Web (Wasm)
```bash
cd kmp
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
# Acesse: http://localhost:8080
```

### Web (JS)
```bash
cd kmp
./gradlew :composeApp:jsBrowserDevelopmentRun
# Acesse: http://localhost:8080
```

### Docker
```bash
cd kmp
# Build e execução
docker compose up --build
# Acesse: http://localhost:3000
```

### Docker Manual
```bash
# Build da imagem
docker build -t petwise-kmp .
# Executar container
docker run -p 3000:80 petwise-kmp
```

## Configuração

### API Base URL
```kotlin
// composeApp/src/commonMain/kotlin/data/api/ApiConfig.kt
object ApiConfig {
    const val BASE_URL = "http://localhost:8080/api"
    // ou para produção: "https://api.petwise.com/api"
}
```

### Dependências
As dependências estão configuradas em `gradle/libs.versions.toml`:

```toml
[versions]
kotlin = "1.9.25"
compose = "1.6.11"
ktor = "2.3.12"
kotlinx-serialization = "1.6.3"
koin = "3.5.6"
coil = "2.6.0"
```

## Estrutura de Dados

### Estados da UI
```kotlin
// Estados comuns para todas as plataformas
sealed class PetListState {
    object Loading : PetListState()
    data class Success(val pets: List<Pet>) : PetListState()
    data class Error(val message: String) : PetListState()
}
```

### Modelos de Dados
```kotlin
@Serializable
data class Pet(
    val id: String,
    val name: String,
    val species: String,
    val breed: String,
    val age: Int,
    val weight: Double,
    val isFavorite: Boolean,
    val ownerId: String
)
```

## Navegação

O app utiliza navegação baseada em estados:

```kotlin
// composeApp/src/commonMain/kotlin/ui/navigation/Navigation.kt
sealed class Screen {
    object PetList : Screen()
    data class PetDetail(val petId: String) : Screen()
    object AddPet : Screen()
    object Profile : Screen()
}
```

## Componentes Compartilhados

### Cards e Listas
```kotlin
@Composable
fun PetCard(
    pet: Pet,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        // Implementação compartilhada
    }
}
```

### Temas e Cores
```kotlin
// composeApp/src/commonMain/kotlin/ui/theme/Theme.kt
object PetWiseTheme {
    val colors = PetWiseColors(
        primary = Color(0xFF4CAF50),
        secondary = Color(0xFF2196F3),
        // ...
    )
}
```

## Testes

### Testes Compartilhados
```bash
./gradlew :composeApp:commonTest
```

### Testes por Plataforma
```bash
# Android
./gradlew :composeApp:androidTest

# iOS
./gradlew :composeApp:iosTest

# Desktop
./gradlew :composeApp:desktopTest
```

## Build de Produção

### Android APK
```bash
./gradlew :composeApp:assembleRelease
# Arquivo: composeApp/build/outputs/apk/release/composeApp-release.apk
```

### iOS App Store
```bash
./gradlew :composeApp:assembleIosAppStoreRelease
# Arquivo: composeApp/build/outputs/iosAppStore/Release-iphoneos/PetWise.ipa
```

### Web
```bash
./gradlew :composeApp:wasmJsBrowserProductionWebpack
# Arquivos: composeApp/build/dist/wasmJs/productionExecutable/
```

## Integração com Backend

### Cliente HTTP (Ktor)
```kotlin
// composeApp/src/commonMain/kotlin/data/api/PetApi.kt
class PetApi(private val httpClient: HttpClient) {

    suspend fun getPets(): List<Pet> {
        return httpClient.get("$BASE_URL/pets") {
            header("Authorization", "Bearer $token")
        }.body()
    }

    suspend fun createPet(pet: CreatePetRequest): Pet {
        return httpClient.post("$BASE_URL/pets") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(pet)
        }.body()
    }
}
```

### Autenticação
```kotlin
// Token armazenado localmente (por plataforma)
expect class TokenStorage {
    fun saveToken(token: String)
    fun getToken(): String?
    fun clearToken()
}
```

## Desenvolvimento

### Adicionando Nova Funcionalidade

1. **Modelo de Dados** (commonMain)
2. **API Client** (commonMain)
3. **Repository** (commonMain)
4. **ViewModel** (commonMain)
5. **UI Component** (commonMain + platform specific se necessário)
6. **Navegação** (commonMain)

### Convenções de Código

- **Nomes**: PascalCase para classes, camelCase para funções/variáveis
- **Null Safety**: Sempre preferir tipos não-null
- **Coroutines**: Usar suspend functions para operações assíncronas
- **State**: Usar StateFlow para estados reativos

## Plataformas Específicas

### Android
- **Manifest**: `androidMain/AndroidManifest.xml`
- **Permissões**: Camera, localização, etc.
- **Notificações**: Firebase Cloud Messaging

### iOS
- **Info.plist**: `iosMain/Info.plist`
- **Capabilities**: Push notifications, etc.
- **SwiftUI Integration**: Para componentes nativos

### Web
- **Webpack Config**: `webpack.config.d/`
- **Service Worker**: Para PWA
- **SEO**: Meta tags e Open Graph

### Desktop
- **JVM Args**: Configurações de memória
- **System Tray**: Ícone na barra de tarefas
- **Menu Bar**: Menus nativos

## Troubleshooting

### Problemas Comuns

**Erro de build no iOS:**
```bash
# Limpar cache
./gradlew clean
cd iosApp && pod install --repo-update
```

**Problemas com Wasm:**
```bash
# Verificar Node.js version (18+)
node --version
```

**Performance no Android:**
- Usar `remember` para estados
- Lazy loading para listas grandes
- Otimizar imagens com Coil

## Contribuição

1. Criar branch: `git checkout -b feature/nova-feature`
2. Testar em todas as plataformas
3. Commit: `git commit -m "feat: descrição da feature"`
4. Push: `git push origin feature/nova-feature`
5. PR com descrição detalhada

## Roadmap

- [ ] PWA completo
- [ ] Notificações push
- [ ] Modo offline
- [ ] Biometria/Face ID
- [ ] Wear OS
- [ ] macOS app
- [ ] Linux app

## Licença

MIT