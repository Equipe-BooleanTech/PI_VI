# Módulo de Farmácias - PetWise

## 📋 Visão Geral

Módulo completo KMP (Kotlin Multiplatform) para gerenciamento de farmácias parceiras no sistema PetWise. Implementa arquitetura limpa (Clean Architecture) com separação clara de responsabilidades entre camadas.

## 🏗️ Arquitetura

```
features/farmacias/
├── domain/
│   ├── models/
│   │   └── Farmacia.kt              # Entidade de domínio + Enums
│   ├── repository/
│   │   └── FarmaciaRepository.kt    # Interface do repositório
│   └── usecases/
│       └── FarmaciaUseCases.kt      # 12 casos de uso
├── data/
│   ├── datasource/
│   │   ├── RemoteFarmaciaDataSource.kt     # Interface
│   │   └── RemoteFarmaciaDataSourceImpl.kt # Implementação
│   └── repository/
│       └── FarmaciaRepositoryImpl.kt        # Implementação do repositório
├── presentation/
│   ├── viewmodel/
│   │   ├── FarmaciasViewModel.kt           # Listagem e operações
│   │   ├── AddFarmaciaViewModel.kt         # Criação
│   │   └── UpdateFarmaciaViewModel.kt      # Atualização
│   ├── schema/
│   │   └── AddFarmaciaFormSchema.kt        # Schema de formulário (30 campos)
│   └── components/
│       └── FarmaciaCard.kt                 # Componentes UI
└── di/
    └── FarmaciaDependencyContainer.kt      # Injeção de dependências

core/network/
├── dto/
│   └── FarmaciaDto.kt                      # DTOs e Mappers
└── api/
    ├── FarmaciaApiService.kt               # Interface da API
    └── FarmaciaApiServiceImpl.kt           # Implementação da API
```

## 🎯 Funcionalidades

### Entidade Farmacia

**Dados Empresariais:**
- Razão Social e Nome Fantasia
- CNPJ, Inscrição Estadual e Municipal
- Tipo de Farmácia (5 tipos)
- Status Operacional (4 estados)
- Data de Abertura

**Responsável Técnico:**
- Nome do farmacêutico
- CRF (Conselho Regional de Farmácia)
- Registro Anvisa
- Autorização de Funcionamento

**Endereço Completo:**
- Rua, Número, Complemento
- Bairro, Cidade, Estado, CEP
- Região de Atuação (5 níveis)

**Contatos:**
- Telefone e Celular
- E-mail principal e financeiro
- Website

**Dados Comerciais:**
- Limite de Crédito
- Desconto Máximo
- Prazo de Entrega
- Frete Grátis (com valor mínimo)

### Enumerações

**TipoFarmacia:**
- VETERINARIA
- MANIPULACAO
- COMERCIAL
- HOSPITALAR
- DISTRIBUIDORA

**StatusFarmacia:**
- ATIVA (verde)
- INATIVA (cinza)
- SUSPENSA (laranja)
- BLOQUEADA (vermelho)

**RegiaoAtuacao:**
- MUNICIPAL
- ESTADUAL
- REGIONAL
- NACIONAL
- INTERNACIONAL

## 🔧 Casos de Uso

### Query Use Cases

1. **GetFarmaciasUseCase** - Busca todas as farmácias
2. **GetFarmaciaByIdUseCase** - Busca farmácia específica
3. **FilterFarmaciasUseCase** - Aplica filtros combinados
4. **GetFarmaciasByCidadeUseCase** - Filtra por cidade
5. **GetFarmaciasByEstadoUseCase** - Filtra por estado (valida UF)
6. **GetFarmaciasAtivasUseCase** - Apenas farmácias ativas
7. **GetFarmaciasComFreteGratisUseCase** - Com frete grátis

### Command Use Cases

8. **AddFarmaciaUseCase** - Cria nova farmácia (com 20+ validações)
9. **UpdateFarmaciaUseCase** - Atualiza farmácia existente
10. **DeleteFarmaciaUseCase** - Remove farmácia
11. **UpdateLimiteCreditoUseCase** - Atualiza limite de crédito
12. **UpdateStatusFarmaciaUseCase** - Altera status (requer motivo para suspensão/bloqueio)

### Business Logic Use Case

13. **FindBestFarmaciasForOrderUseCase** - Encontra melhores farmácias para pedido

## 📊 ViewModels

### FarmaciasViewModel

**Responsabilidades:**
- Listagem de farmácias
- Busca e filtros
- Operações CRUD
- Gerenciamento de estado reativo

**Eventos:**
```kotlin
sealed class FarmaciasUiEvent {
    object LoadFarmacias
    data class SearchFarmacias(val query: String)
    data class FilterFarmacias(val options: FarmaciaFilterOptions)
    object LoadApenasAtivas
    data class DeleteFarmacia(val id: String)
    data class UpdateStatus(val id: String, val novoStatus: StatusFarmacia, val motivo: String?)
    data class UpdateLimiteCredito(val id: String, val novoLimite: Double)
    data class GetByCidade(val cidade: String)
    data class GetByEstado(val estado: String)
    object ClearError
}
```

**Estado:**
```kotlin
data class FarmaciasUiState(
    val farmacias: List<Farmacia>,
    val filteredFarmacias: List<Farmacia>,
    val isLoading: Boolean,
    val errorMessage: String?,
    val filterOptions: FarmaciaFilterOptions,
    val searchQuery: String,
    val showApenasAtivas: Boolean
)
```

### AddFarmaciaViewModel

- Validações automáticas de campos
- Formatação de CNPJ, CEP, telefones
- Normalização de dados (uppercase, lowercase, trim)

### UpdateFarmaciaViewModel

- Carregamento de dados existentes
- Preservação de metadados (dataRegistro)
- Validações de atualização

## 🎨 Componentes UI

### FarmaciaCard

**Features:**
- Exibição de informações principais
- Badge de status com cores
- Menu dropdown com ações
- Chips informativos (Crédito, Desconto, Frete)
- Dialog de confirmação de exclusão
- Suporte a farmácias inativas (visual diferenciado)

**Ações:**
- Ver detalhes
- Editar
- Excluir
- Atualizar status

### FarmaciaCompactCard

Versão compacta para listas:
- Nome fantasia
- Cidade/Estado
- Status badge

## 📝 Form Schema

**AddFarmaciaFormSchema** - 30 campos configurados:

**Validações Implementadas:**
- Required (campos obrigatórios)
- MinLength / MaxLength
- Pattern (regex para CNPJ, CEP, e-mail)
- Min / Max (valores numéricos)
- Length (tamanho exato - UF)
- Email (formato de e-mail)

**Tipos de Campos:**
- TEXT (15 campos)
- NUMBER (5 campos)
- EMAIL (2 campos)
- SELECT (3 campos)
- DATE (1 campo)
- CHECKBOX (1 campo)
- TEXTAREA (1 campo)

## 🌐 API Endpoints

```kotlin
const val FARMACIAS = "/api/farmacias"
fun getFarmacia(id: String) = "$FARMACIAS/$id"
const val FARMACIAS_SEARCH = "$FARMACIAS/search"
const val FARMACIAS_ATIVAS = "$FARMACIAS/ativas"
const val FARMACIAS_FRETE_GRATIS = "$FARMACIAS/frete-gratis"
fun getFarmaciasByCidade(cidade: String) = "$FARMACIAS/cidade/$cidade"
fun getFarmaciasByEstado(estado: String) = "$FARMACIAS/estado/$estado"
fun updateFarmaciaLimiteCredito(id: String) = "$FARMACIAS/$id/limite-credito"
fun updateFarmaciaStatus(id: String) = "$FARMACIAS/$id/status"
```

## 💉 Dependency Injection

**FarmaciaDependencyContainer** gerencia:
- RemoteDataSource (singleton lazy)
- Repository (singleton lazy)
- 13 Use Cases (singleton lazy)
- 3 ViewModels (factory methods - nova instância por chamada)

**Uso:**
```kotlin
val viewModel = FarmaciaDependencyContainer.provideFarmaciasViewModel()
val addViewModel = FarmaciaDependencyContainer.provideAddFarmaciaViewModel()
val updateViewModel = FarmaciaDependencyContainer.provideUpdateFarmaciaViewModel("farmacia_id")
```

## 🧪 Validações de Negócio

### Na Entidade (Farmacia.kt)

```kotlin
fun isCnpjValido(): Boolean          // Valida 14 dígitos
fun isEmailValido(): Boolean         // Valida formato básico
fun getCnpjFormatado(): String       // ##.###.###/####-##
fun getCepFormatado(): String        // #####-###
fun getEnderecoCompleto(): String    // Endereço formatado completo
fun isOperacional(): Boolean         // Verifica se está ativa
fun calcularValorComDesconto()       // Aplica desconto
fun temCreditoDisponivel()           // Verifica crédito
fun qualificaFreteGratis()           // Verifica elegibilidade para frete grátis
```

### No AddFarmaciaUseCase

20+ validações antes de criar:
- Campos obrigatórios não podem estar vazios
- CNPJ deve ter 14 dígitos
- Estado deve ter 2 caracteres
- E-mail deve ter formato válido
- Limite de crédito não pode ser negativo
- Desconto deve estar entre 0 e 100%
- Prazo de entrega não pode ser negativo
- Valor mínimo de frete não pode ser negativo

## 📦 DTOs e Mappers

**5 DTOs criados:**
1. `FarmaciaDto` - Transferência completa
2. `FarmaciaListResponse` - Resposta de lista com paginação
3. `CreateFarmaciaRequest` - Criação
4. `UpdateFarmaciaRequest` - Atualização (campos opcionais)
5. `UpdateLimiteCreditoRequest` - Atualização de crédito
6. `UpdateStatusRequest` - Atualização de status com motivo

**Mappers bidirecionais:**
```kotlin
fun Farmacia.toDto(): FarmaciaDto
fun FarmaciaDto.toDomain(): Farmacia
fun Farmacia.toCreateRequest(): CreateFarmaciaRequest
fun Farmacia.toUpdateRequest(): UpdateFarmaciaRequest
```

**Conversão segura de enums:**
```kotlin
private fun mapStringToTipoFarmacia(tipo: String): TipoFarmacia
private fun mapStringToStatusFarmacia(status: String): StatusFarmacia
private fun mapStringToRegiaoAtuacao(regiao: String): RegiaoAtuacao
```

## 🔍 Filtros Avançados

**FarmaciaFilterOptions:**
```kotlin
data class FarmaciaFilterOptions(
    val tipo: TipoFarmacia? = null,
    val status: StatusFarmacia? = null,
    val regiao: RegiaoAtuacao? = null,
    val estado: String? = null,
    val cidade: String? = null,
    val apenasComCredito: Boolean = false,
    val apenasFreteGratis: Boolean = false,
    val descontoMinimo: Double? = null
)
```

Filtros são aplicados de forma combinada (AND lógico).

## 🚀 Fluxo de Dados

```
UI (Compose) 
  ↓ eventos
ViewModel (StateFlow)
  ↓ invoca
Use Case (validações)
  ↓ chama
Repository (Flow/Result)
  ↓ delega
RemoteDataSource
  ↓ usa
ApiService (Ktor)
  ↓ HTTP
Backend API
```

**Resposta:**
```
Backend API
  ↓ JSON
ApiService (deserializa)
  ↓ NetworkResult<DTO>
RemoteDataSource (processa)
  ↓ NetworkResult<DTO>
Repository (mapeia DTO → Domain)
  ↓ Flow<List<Farmacia>> ou Result<Farmacia>
Use Case (aplica lógica)
  ↓ retorna
ViewModel (atualiza StateFlow)
  ↓ emite
UI (recompõe)
```

## 📈 Estatísticas do Módulo

- **Arquivos criados:** 16
- **Linhas de código:** ~3.500
- **Camadas:** 4 (Domain, Data, Presentation, Network)
- **Use Cases:** 13
- **ViewModels:** 3
- **DTOs:** 6
- **Validações:** 20+
- **Campos de formulário:** 30
- **Enumerações:** 3
- **Endpoints API:** 9

## 🛠️ Integração no Projeto

1. **NetworkConfig.kt** - Endpoints adicionados ✅
2. **NetworkModule.kt** - farmaciaApiService registrado ✅
3. **Dependency Container** - Pronto para uso ✅

## 💡 Uso Recomendado

```kotlin
// Em uma tela Compose
@Composable
fun FarmaciasScreen() {
    val viewModel = remember { 
        FarmaciaDependencyContainer.provideFarmaciasViewModel() 
    }
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.onEvent(FarmaciasUiEvent.LoadFarmacias)
    }
    
    Column {
        // SearchBar
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { 
                viewModel.onEvent(FarmaciasUiEvent.SearchFarmacias(it)) 
            }
        )
        
        // Lista
        LazyColumn {
            items(uiState.filteredFarmacias) { farmacia ->
                FarmaciaCard(
                    farmacia = farmacia,
                    onViewDetails = { /* navegar */ },
                    onEdit = { /* navegar para edição */ },
                    onDelete = { 
                        viewModel.onEvent(FarmaciasUiEvent.DeleteFarmacia(farmacia.id)) 
                    },
                    onUpdateStatus = { status ->
                        viewModel.onEvent(
                            FarmaciasUiEvent.UpdateStatus(farmacia.id, status, null)
                        )
                    }
                )
            }
        }
    }
}
```

## 🎯 Próximos Passos Sugeridos

1. Implementar cache local (Room/SQLDelight)
2. Adicionar paginação na listagem
3. Implementar busca com KMP algorithm (como no módulo Vacinas)
4. Criar tela de detalhes completos
5. Adicionar gráficos de métricas comerciais
6. Implementar histórico de alterações
7. Adicionar upload de documentos (Alvará, Licenças)
8. Criar relatórios de performance por farmácia

## 📄 Licença

Parte do projeto PetWise - Sistema de Gestão Veterinária
