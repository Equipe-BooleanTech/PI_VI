# 🏗️ Arquitetura do Módulo Farmácias - Diagrama Visual

## 📐 Visão Geral da Arquitetura

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           USER INTERFACE                                 │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │                      NAVIGATION LAYER                              │ │
│  │  ┌──────────────────┐  ┌──────────────────┐  ┌─────────────────┐ │ │
│  │  │  NavigationMgr   │  │   MoreMenu       │  │  DashboardScreen│ │ │
│  │  │  • TabScreen     │→ │   • LocalPharmacy│→ │  • onCardClick  │ │ │
│  │  │  • Farmacias     │  │   • navigateToTab│  │  • routes       │ │ │
│  │  └──────────────────┘  └──────────────────┘  └─────────────────┘ │ │
│  └────────────────────────────────────────────────────────────────────┘ │
│                                  ▼                                       │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │                    PRESENTATION LAYER                              │ │
│  │  ┌─────────────────────────────────────────────────────────────┐  │ │
│  │  │               FarmaciasScreen.kt (430 lines)                │  │ │
│  │  │  ┌─────────┐  ┌──────────┐  ┌────────┐  ┌──────────────┐  │  │ │
│  │  │  │TopAppBar│  │SearchBar │  │  FAB   │  │FilterDialog  │  │  │ │
│  │  │  └─────────┘  └──────────┘  └────────┘  └──────────────┘  │  │ │
│  │  │  ┌──────────────────────────────────────────────────────┐  │  │ │
│  │  │  │         LazyColumn + FarmaciaCard Items              │  │  │ │
│  │  │  │  ┌─────┐  ┌─────┐  ┌─────┐  ┌─────┐  ┌─────┐       │  │  │ │
│  │  │  │  │Card1│  │Card2│  │Card3│  │Card4│  │Card5│  ...  │  │  │ │
│  │  │  │  └─────┘  └─────┘  └─────┘  └─────┘  └─────┘       │  │  │ │
│  │  │  └──────────────────────────────────────────────────────┘  │  │ │
│  │  │  ┌──────────┐  ┌───────────┐  ┌──────────────┐          │  │ │
│  │  │  │EmptyState│  │ErrorState │  │LoadingState  │          │  │ │
│  │  │  └──────────┘  └───────────┘  └──────────────┘          │  │ │
│  │  └─────────────────────────────────────────────────────────────┘  │ │
│  │                                ▲                                   │ │
│  │                                │ StateFlow                         │ │
│  │                                │                                   │ │
│  │  ┌─────────────────────────────┴────────────────────────────────┐ │ │
│  │  │                     ViewModels (3 classes)                    │ │ │
│  │  │  ┌──────────────────┐  ┌──────────────────┐  ┌────────────┐ │ │ │
│  │  │  │FarmaciasViewModel│  │AddFarmaciaVM     │  │UpdateVM    │ │ │ │
│  │  │  │ • uiState        │  │ • formState      │  │ • loadData │ │ │ │
│  │  │  │ • farmacias      │  │ • validate       │  │ • update   │ │ │ │
│  │  │  │ • isLoading      │  │ • create         │  │            │ │ │ │
│  │  │  │ • handleEvent    │  │                  │  │            │ │ │ │
│  │  │  └──────────────────┘  └──────────────────┘  └────────────┘ │ │ │
│  │  └───────────────────────────────────────────────────────────────┘ │ │
│  │                                ▲                                   │ │
│  │  ┌─────────────────────────────┴────────────────────────────────┐ │ │
│  │  │           AddFarmaciaFormSchema.kt (260 lines)                │ │ │
│  │  │  • 30 FormFields with validators                              │ │ │
│  │  │  • Business rules enforcement                                 │ │ │
│  │  └───────────────────────────────────────────────────────────────┘ │ │
│  │                                                                    │ │
│  │  ┌─────────────────────────────────────────────────────────────┐  │ │
│  │  │            FarmaciaCard.kt (360 lines)                       │  │ │
│  │  │  ┌─────────────────┐  ┌──────────────────────────────────┐  │  │ │
│  │  │  │FarmaciaCard     │  │FarmaciaCardCompact               │  │  │ │
│  │  │  │ • Full details  │  │ • Minimal view                   │  │  │ │
│  │  │  │ • Action menu   │  │ • Quick info                     │  │  │ │
│  │  │  │ • Status badges │  │                                  │  │  │ │
│  │  │  └─────────────────┘  └──────────────────────────────────┘  │  │ │
│  │  └─────────────────────────────────────────────────────────────┘  │ │
│  └────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────┘
                                  ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                           DOMAIN LAYER                                   │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │                  Farmacia.kt (320 lines)                           │ │
│  │  ┌────────────────┐  ┌────────────────┐  ┌────────────────────┐  │ │
│  │  │ StatusFarmacia │  │ TipoFarmacia   │  │ ServicoAdicional   │  │ │
│  │  │ • ATIVA        │  │ • INDEPENDENTE │  │ • DELIVERY         │  │ │
│  │  │ • INATIVA      │  │ • REDE         │  │ • MANIPULACAO      │  │ │
│  │  │ • SUSPENSA     │  │ • POPULAR      │  │ • VACINAS          │  │ │
│  │  │ • EM_ANALISE   │  │ • HOSPITALAR   │  │ • TESTES           │  │ │
│  │  └────────────────┘  └────────────────┘  └────────────────────┘  │ │
│  │                                                                    │ │
│  │  ┌────────────────────────────────────────────────────────────┐  │ │
│  │  │               Farmacia Data Class                          │  │ │
│  │  │  • 38 properties (id, nome, cnpj, email, ...)             │  │ │
│  │  │  • 9 utility methods (validators, formatters)             │  │ │
│  │  │  • Nested classes (Endereco, Horario, Contato)            │  │ │
│  │  └────────────────────────────────────────────────────────────┘  │ │
│  └────────────────────────────────────────────────────────────────────┘ │
│                                  ▼                                       │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │            FarmaciaRepository.kt (60 lines - Interface)            │ │
│  │  • getFarmacias(): Flow<Result<List<Farmacia>>>                    │ │
│  │  • getFarmaciaById(id): Flow<Result<Farmacia?>>                    │ │
│  │  • createFarmacia(farmacia): Flow<Result<Farmacia>>                │ │
│  │  • updateFarmacia(farmacia): Flow<Result<Farmacia>>                │ │
│  │  • deleteFarmacia(id): Flow<Result<Unit>>                          │ │
│  │  • searchFarmacias(query): Flow<Result<List<Farmacia>>>            │ │
│  │  • ... +6 more methods                                             │ │
│  └────────────────────────────────────────────────────────────────────┘ │
│                                  ▼                                       │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │            FarmaciaUseCases.kt (240 lines - 13 Use Cases)          │ │
│  │  ┌──────────────────────┐  ┌──────────────────────────────────┐  │ │
│  │  │ GetFarmaciasUseCase  │  │ GetFarmaciaByIdUseCase           │  │ │
│  │  │ • operator invoke()  │  │ • operator invoke(id: String)    │  │ │
│  │  └──────────────────────┘  └──────────────────────────────────┘  │ │
│  │  ┌──────────────────────┐  ┌──────────────────────────────────┐  │ │
│  │  │ AddFarmaciaUseCase   │  │ UpdateFarmaciaUseCase            │  │ │
│  │  │ • 20+ validations    │  │ • validate and update            │  │ │
│  │  │ • invoke(farmacia)   │  │ • invoke(farmacia)               │  │ │
│  │  └──────────────────────┘  └──────────────────────────────────┘  │ │
│  │  ┌──────────────────────┐  ┌──────────────────────────────────┐  │ │
│  │  │ DeleteFarmaciaUseCase│  │ SearchFarmaciasUseCase           │  │ │
│  │  │ • invoke(id)         │  │ • invoke(query)                  │  │ │
│  │  └──────────────────────┘  └──────────────────────────────────┘  │ │
│  │  ... +7 more use cases (Filter by: Cidade, Estado, Tipo, etc.)   │ │
│  └────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────┘
                                  ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                            DATA LAYER                                    │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │       FarmaciaRepositoryImpl.kt (260 lines - Implementation)       │ │
│  │  • Flow-based reactive data                                        │ │
│  │  • Error handling with Result pattern                              │ │
│  │  • Delegates to RemoteFarmaciaDataSource                           │ │
│  │  • Local filtering and transformation                              │ │
│  └────────────────────────────────────────────────────────────────────┘ │
│                                  ▼                                       │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │   RemoteFarmaciaDataSource.kt (25 lines - Interface)               │ │
│  │   RemoteFarmaciaDataSourceImpl.kt (280 lines - Implementation)     │ │
│  │                                                                      │ │
│  │  ┌──────────────────────────────────────────────────────────────┐  │ │
│  │  │              HTTP Operations + Logging                       │  │ │
│  │  │  • suspend fun getFarmacias(): Result<List<FarmaciaDto>>    │  │ │
│  │  │  • suspend fun getFarmaciaById(id): Result<FarmaciaDto?>    │  │ │
│  │  │  • suspend fun createFarmacia(dto): Result<FarmaciaDto>     │  │ │
│  │  │  • suspend fun updateFarmacia(dto): Result<FarmaciaDto>     │  │ │
│  │  │  • suspend fun deleteFarmacia(id): Result<Unit>             │  │ │
│  │  │  • ... +7 more methods                                      │  │ │
│  │  │  • Detailed logging for debugging                           │  │ │
│  │  │  • Exception handling                                       │  │ │
│  │  └──────────────────────────────────────────────────────────────┘  │ │
│  └────────────────────────────────────────────────────────────────────┘ │
│                                  ▼                                       │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │              FarmaciaDto.kt (380 lines)                            │ │
│  │  ┌────────────────────┐  ┌────────────────────┐                   │ │
│  │  │ FarmaciaDto        │  │ CreateFarmaciaDto  │                   │ │
│  │  │ • Response model   │  │ • Request model    │                   │ │
│  │  └────────────────────┘  └────────────────────┘                   │ │
│  │  ┌────────────────────┐  ┌────────────────────┐                   │ │
│  │  │ UpdateFarmaciaDto  │  │ EnderecoDto        │                   │ │
│  │  │ • Request model    │  │ • Nested DTO       │                   │ │
│  │  └────────────────────┘  └────────────────────┘                   │ │
│  │  ┌────────────────────┐  ┌────────────────────┐                   │ │
│  │  │ HorarioDto         │  │ ContatoDto         │                   │ │
│  │  │ • Nested DTO       │  │ • Nested DTO       │                   │ │
│  │  └────────────────────┘  └────────────────────┘                   │ │
│  │                                                                     │ │
│  │  ┌────────────────────────────────────────────────────────────┐   │ │
│  │  │                  Mapper Extensions                         │   │ │
│  │  │  • FarmaciaDto.toDomain(): Farmacia                        │   │ │
│  │  │  • Farmacia.toDto(): FarmaciaDto                           │   │ │
│  │  │  • Farmacia.toCreateDto(): CreateFarmaciaDto               │   │ │
│  │  │  • Farmacia.toUpdateDto(): UpdateFarmaciaDto               │   │ │
│  │  │  • Safe enum conversions                                   │   │ │
│  │  └────────────────────────────────────────────────────────────┘   │ │
│  └────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────┘
                                  ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                          NETWORK LAYER                                   │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │       FarmaciaApiService.kt (55 lines - Interface)                 │ │
│  │       FarmaciaApiServiceImpl.kt (110 lines - Ktor Implementation)  │ │
│  │                                                                      │ │
│  │  ┌──────────────────────────────────────────────────────────────┐  │ │
│  │  │                    Ktor HTTP Client                          │  │ │
│  │  │  ┌────────────────┐  ┌────────────────┐  ┌───────────────┐ │  │ │
│  │  │  │ GET /farmacias │  │POST /farmacias │  │PUT /{id}      │ │  │ │
│  │  │  └────────────────┘  └────────────────┘  └───────────────┘ │  │ │
│  │  │  ┌────────────────┐  ┌────────────────┐  ┌───────────────┐ │  │ │
│  │  │  │DELETE /{id}    │  │GET /{id}       │  │GET /ativas    │ │  │ │
│  │  │  └────────────────┘  └────────────────┘  └───────────────┘ │  │ │
│  │  │  ┌────────────────┐  ┌────────────────┐  ┌───────────────┐ │  │ │
│  │  │  │GET /cidade/{c} │  │GET /estado/{e} │  │POST /search   │ │  │ │
│  │  │  └────────────────┘  └────────────────┘  └───────────────┘ │  │ │
│  │  └──────────────────────────────────────────────────────────────┘  │ │
│  └────────────────────────────────────────────────────────────────────┘ │
│                                  ▲                                       │
│  ┌────────────────────────────────┴───────────────────────────────────┐ │
│  │                  NetworkConfig.kt (9 endpoints)                    │ │
│  │  • FARMACIAS = "$BASE_URL/farmacias"                               │ │
│  │  • FARMACIA_BY_ID = "$BASE_URL/farmacias/{id}"                     │ │
│  │  • FARMACIAS_ATIVAS = "$BASE_URL/farmacias/ativas"                 │ │
│  │  • ... +6 more endpoints                                           │ │
│  └────────────────────────────────────────────────────────────────────┘ │
│                                  ▲                                       │
│  ┌────────────────────────────────┴───────────────────────────────────┐ │
│  │                NetworkModule.kt (Service Registration)             │ │
│  │  val farmaciaApiService: FarmaciaApiService by lazy {              │ │
│  │      FarmaciaApiServiceImpl(httpClient)                            │ │
│  │  }                                                                  │ │
│  └────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────┘
                                  ▲
┌─────────────────────────────────┴───────────────────────────────────────┐
│                     DEPENDENCY INJECTION LAYER                           │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │         FarmaciaDependencyContainer.kt (120 lines)                 │ │
│  │                                                                      │ │
│  │  ┌──────────────────────────────────────────────────────────────┐  │ │
│  │  │                  Lazy Singletons                             │  │ │
│  │  │  private val farmaciaApiService: FarmaciaApiService by lazy  │  │ │
│  │  │  private val remoteFarmaciaDataSource by lazy { ... }        │  │ │
│  │  │  private val farmaciaRepository by lazy { ... }              │  │ │
│  │  │  private val farmaciaUseCases by lazy { ... }                │  │ │
│  │  └──────────────────────────────────────────────────────────────┘  │ │
│  │                                                                      │ │
│  │  ┌──────────────────────────────────────────────────────────────┐  │ │
│  │  │                  Factory Methods                             │  │ │
│  │  │  fun provideFarmaciasViewModel(): FarmaciasViewModel         │  │ │
│  │  │  fun provideAddFarmaciaViewModel(): AddFarmaciaViewModel     │  │ │
│  │  │  fun provideUpdateFarmaciaViewModel(id): UpdateFarmaciaVM    │  │ │
│  │  └──────────────────────────────────────────────────────────────┘  │ │
│  └────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 🔄 Fluxo de Dados Completo

### 1. User Action → UI Update

```
User clicks "Farmácias" in MoreMenu
        ▼
NavigationManager.navigateToTab(TabScreen.Farmacias)
        ▼
DashboardScreen routes to FarmaciasScreen
        ▼
FarmaciasScreen composable renders
        ▼
ViewModel initialization via DependencyContainer
        ▼
LaunchedEffect triggers LoadFarmacias event
        ▼
ViewModel.handleEvent(LoadFarmacias)
        ▼
FarmaciaUseCases.getFarmaciasUseCase()
        ▼
FarmaciaRepository.getFarmacias()
        ▼
RemoteFarmaciaDataSource.getFarmacias()
        ▼
FarmaciaApiService HTTP GET request
        ▼
Backend responds with List<FarmaciaDto>
        ▼
DTO → Domain mapping (FarmaciaDto.toDomain())
        ▼
Flow emits Result<List<Farmacia>>
        ▼
ViewModel updates StateFlow<List<Farmacia>>
        ▼
FarmaciasScreen recomposes with new data
        ▼
LazyColumn renders FarmaciaCard items
        ▼
User sees pharmacy list 🎉
```

---

## 🎯 Padrões de Design Implementados

### 1. Clean Architecture
```
┌─────────────┐
│ Presentation│ ← ViewModels, Composables
├─────────────┤
│   Domain    │ ← Use Cases, Entities, Repository Interfaces
├─────────────┤
│    Data     │ ← Repository Implementations, Data Sources
├─────────────┤
│   Network   │ ← API Services, DTOs
└─────────────┘
```

### 2. Repository Pattern
```
ViewModel → Use Case → Repository Interface
                              ▼
                    Repository Implementation
                              ▼
                       Data Source(s)
```

### 3. Dependency Injection
```
DependencyContainer (Singleton)
    ├── Lazy Singletons (shared instances)
    └── Factory Methods (new instances per request)
```

### 4. MVVM (Model-View-ViewModel)
```
View (Composable) ← StateFlow ← ViewModel ← Use Cases
        │                                       ▲
        └─ User Events ──────────────────────────┘
```

### 5. Observer Pattern
```
StateFlow<T> (Observable)
        ▼
collectAsState() (Observer)
        ▼
Automatic recomposition
```

---

## 📦 Módulos e Responsabilidades

| Módulo | Responsabilidade | Linhas | Arquivos |
|--------|-----------------|--------|----------|
| **Domain** | Regras de negócio | 620 | 3 |
| **Data** | Acesso a dados | 1.085 | 3 |
| **Network** | Comunicação HTTP | 545 | 4 |
| **Presentation** | UI e interação | 1.465 | 5 |
| **DI** | Injeção de dependências | 120 | 1 |
| **Total** | - | **3.835** | **16** |

---

## 🚦 Estados da Aplicação

```
┌─────────────────────────────────────────────────────────────┐
│                    FarmaciasUiState                          │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────┐   ┌─────────┐   ┌─────────┐   ┌──────────┐  │
│  │  Idle    │→  │ Loading │→  │ Success │   │  Error   │  │
│  │ (Initial)│   │ (Fetch) │   │ (Data)  │   │(Message) │  │
│  └──────────┘   └─────────┘   └─────────┘   └──────────┘  │
│       ▲                            │               │        │
│       └────────────────────────────┴───────────────┘        │
│                     (Retry/Refresh)                         │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔐 Segurança e Validação

### Camadas de Validação

```
┌───────────────────────────────────────────────────────┐
│                  User Input                           │
└───────────────────────┬───────────────────────────────┘
                        ▼
┌───────────────────────────────────────────────────────┐
│           1. UI Validation (FormSchema)               │
│  • Field-level validators                             │
│  • Real-time feedback                                 │
│  • Required field checks                              │
└───────────────────────┬───────────────────────────────┘
                        ▼
┌───────────────────────────────────────────────────────┐
│         2. ViewModel Validation                       │
│  • State validation before submission                 │
│  • Business rule enforcement                          │
└───────────────────────┬───────────────────────────────┘
                        ▼
┌───────────────────────────────────────────────────────┐
│         3. Use Case Validation                        │
│  • 20+ business rules in AddFarmaciaUseCase           │
│  • CNPJ validation with checksum                      │
│  • Email regex validation                             │
│  • CEP format validation                              │
│  • Horario logic validation                           │
└───────────────────────┬───────────────────────────────┘
                        ▼
┌───────────────────────────────────────────────────────┐
│         4. Backend Validation (Expected)              │
│  • Database constraints                               │
│  • Duplicate checks                                   │
│  • Authorization                                      │
└───────────────────────────────────────────────────────┘
```

---

## 📱 UI Component Hierarchy

```
FarmaciasScreen
├── Scaffold
│   ├── TopAppBar
│   │   ├── NavigationIcon (LocalPharmacy)
│   │   └── Title ("Farmácias Parceiras")
│   │
│   ├── Content (Column)
│   │   ├── SearchBar
│   │   │   ├── TextField
│   │   │   └── ClearButton
│   │   │
│   │   ├── FilterChips Row
│   │   │   ├── FilterChip("Ativas")
│   │   │   └── FilterChip("Todas")
│   │   │
│   │   └── LazyColumn
│   │       ├── items(farmacias) { farmacia ->
│   │       │   FarmaciaCard(farmacia)
│   │       │   ├── Card
│   │       │   │   ├── Header Row
│   │       │   │   │   ├── Icon + Nome
│   │       │   │   │   └── ActionMenu
│   │       │   │   │       ├── Edit MenuItem
│   │       │   │   │       └── Delete MenuItem
│   │       │   │   │
│   │       │   │   ├── Status Badge
│   │       │   │   │
│   │       │   │   ├── Info Column
│   │       │   │   │   ├── CNPJ Text
│   │       │   │   │   ├── Endereco Text
│   │       │   │   │   ├── Telefone Text
│   │       │   │   │   └── Email Text
│   │       │   │   │
│   │       │   │   └── Footer Row
│   │       │   │       ├── Tipo Chip
│   │       │   │       └── Servicos Chips
│   │       │   └───────
│   │       └───────
│   │       │
│   │       └── EmptyState / ErrorState / Loading
│   │
│   └── FloatingActionButton
│       └── Add Icon + "Adicionar"
│
└── Dialogs (Conditional)
    ├── FilterDialog
    │   ├── Status Dropdown
    │   ├── Tipo Dropdown
    │   └── Estado Dropdown
    │
    └── AddFarmaciaDialog
        └── Placeholder for full form
```

---

## 🎨 Material 3 Design Tokens

### Colors
```kotlin
Primary: MaterialTheme.colorScheme.primary
Secondary: MaterialTheme.colorScheme.secondary
Error: MaterialTheme.colorScheme.error
Surface: MaterialTheme.colorScheme.surface
Background: MaterialTheme.colorScheme.background
```

### Typography
```kotlin
headlineMedium: 24sp, Bold
titleLarge: 20sp, SemiBold
bodyLarge: 16sp, Normal
bodyMedium: 14sp, Normal
labelSmall: 12sp, Medium
```

### Spacing
```kotlin
Padding: 16.dp (standard)
Card spacing: 8.dp between items
Icon size: 24.dp
Chip height: 32.dp
FAB size: 56.dp
```

---

## 🔄 Reactive Data Flow

```
┌──────────────────────────────────────────────────────┐
│              ViewModel StateFlows                     │
│  ┌────────────┐  ┌───────────┐  ┌────────────────┐  │
│  │ farmacias  │  │ isLoading │  │ errorMessage   │  │
│  │Flow<List<>>│  │Flow<Bool> │  │Flow<String?>   │  │
│  └─────┬──────┘  └─────┬─────┘  └───────┬────────┘  │
└────────┼───────────────┼─────────────────┼───────────┘
         │               │                 │
         ▼               ▼                 ▼
┌──────────────────────────────────────────────────────┐
│           Composable @Composable                      │
│  val farmacias by viewModel.farmacias.collectAsState()│
│  val isLoading by viewModel.isLoading.collectAsState()│
│  val error by viewModel.errorMessage.collectAsState() │
└────────┬──────────────┬─────────────────┬────────────┘
         │              │                 │
         ▼              ▼                 ▼
┌─────────────┐  ┌─────────────┐  ┌──────────────┐
│LazyColumn   │  │Loading      │  │Error         │
│+ Cards      │  │Indicator    │  │Message       │
└─────────────┘  └─────────────┘  └──────────────┘
```

---

## 🧪 Testing Strategy (Future)

```
┌─────────────────────────────────────────────────────┐
│                Unit Tests                            │
│  • UseCases validation logic                        │
│  • ViewModel state management                       │
│  • Mapper functions (DTO ↔ Domain)                  │
│  • Utility functions (CNPJ, Email validators)       │
└─────────────────────────────────────────────────────┘
         ▼
┌─────────────────────────────────────────────────────┐
│             Integration Tests                        │
│  • Repository + DataSource interaction              │
│  • API Service + HTTP Client                        │
│  • ViewModel + Use Cases flow                       │
└─────────────────────────────────────────────────────┘
         ▼
┌─────────────────────────────────────────────────────┐
│                UI Tests                              │
│  • Screen navigation                                │
│  • User interactions (click, type, scroll)          │
│  • State-driven UI updates                          │
│  • Dialog interactions                              │
└─────────────────────────────────────────────────────┘
```

---

## 📈 Performance Considerations

### Optimizations Implemented

1. **Lazy Initialization**
   ```kotlin
   private val farmaciaRepository by lazy { 
       FarmaciaRepositoryImpl(remoteFarmaciaDataSource) 
   }
   ```

2. **Flow-based Pagination** (Ready for)
   ```kotlin
   fun getFarmacias(page: Int, limit: Int): Flow<Result<Page<Farmacia>>>
   ```

3. **Debounced Search** (In SearchBar)
   ```kotlin
   LaunchedEffect(searchQuery) {
       delay(300) // Debounce
       viewModel.handleEvent(SearchFarmacias(searchQuery))
   }
   ```

4. **Efficient Recomposition**
   ```kotlin
   val farmacias by viewModel.farmacias.collectAsState()
   // Only recompose when farmacias change
   ```

---

## 🌐 Multi-Platform Support

```
┌──────────────────────────────────────────────────────┐
│                   commonMain                          │
│  All business logic, UI, and data layers             │
└───┬──────────────┬──────────────┬──────────────┬────┘
    │              │              │              │
    ▼              ▼              ▼              ▼
┌────────┐   ┌─────────┐   ┌─────────┐   ┌──────────┐
│Android │   │   iOS   │   │ Desktop │   │   Web    │
│ Main   │   │  Main   │   │  (JVM)  │   │   (JS)   │
└────────┘   └─────────┘   └─────────┘   └──────────┘
```

Farmácias module works seamlessly across all platforms! 🚀

---

**Diagrama criado para documentação do Módulo Farmácias - PetWise KMP**
*Versão: 1.0 | Data: 2024*
