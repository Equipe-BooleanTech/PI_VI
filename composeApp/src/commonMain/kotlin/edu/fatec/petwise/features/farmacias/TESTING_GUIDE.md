# 🧪 Guia de Testes - Módulo Farmácias

## ✅ Status: Pronto para Testes

Compilação: ✅ 0 erros
Integração: ✅ Completa
Documentação: ✅ Abrangente

---

## 🚀 Como Executar o Projeto

### 1. Compilar o Projeto

```powershell
# Windows PowerShell
.\gradlew.bat build
```

**Esperado**: Build successful sem erros

---

### 2. Executar no Android

```powershell
# Compilar e instalar no dispositivo/emulador
.\gradlew.bat :composeApp:installDebug

# Ou executar diretamente
.\gradlew.bat :composeApp:run
```

**Pré-requisitos**:
- Android Studio instalado
- Emulador Android rodando OU dispositivo físico conectado
- SDK Android configurado

---

### 3. Executar no Desktop (JVM)

```powershell
# Executar aplicação desktop
.\gradlew.bat :composeApp:run
```

**Esperado**: Janela desktop do app abre

---

### 4. Executar no Web (JavaScript)

```powershell
# Modo desenvolvimento com hot reload
.\gradlew.bat :composeApp:jsBrowserDevelopmentRun

# Build de produção
.\gradlew.bat :composeApp:jsBrowserProductionWebpack
```

**Esperado**: 
- Servidor dev inicia em `http://localhost:8080`
- Browser abre automaticamente

---

## 🧪 Testes Manuais da Integração

### Teste 1: Navegação via Menu "Mais"

#### Passos:
1. ✅ Abrir o app
2. ✅ Fazer login (se necessário)
3. ✅ Navegar para tab "Mais" (bottom navigation - último ícone)
4. ✅ Localizar item "Farmácias" com ícone LocalPharmacy
5. ✅ Clicar em "Farmácias"

#### Resultado Esperado:
- ✅ Tela "FarmaciasScreen" deve abrir
- ✅ TopAppBar exibe "Farmácias Parceiras"
- ✅ SearchBar visível
- ✅ FilterChips "Ativas" e "Todas" visíveis
- ✅ FAB "Adicionar" no canto inferior direito

#### Se falhar:
- Verificar `MoreMenu.kt` linha 202
- Verificar `NavigationManager.kt` TabScreen.Farmacias existe
- Verificar `DashboardScreen.kt` roteamento implementado

---

### Teste 2: Estado de Loading

#### Passos:
1. ✅ Navegar para tela de Farmácias
2. ✅ Observar estado inicial

#### Resultado Esperado:
- ✅ CircularProgressIndicator aparece brevemente
- ✅ Ou lista de farmácias carrega (se backend mock implementado)
- ✅ Ou estado vazio aparece "Nenhuma farmácia encontrada"

#### Verificar no código:
```kotlin
// FarmaciasScreen.kt, linha ~200
when {
    isLoading -> CircularProgressIndicator()
    // ...
}
```

---

### Teste 3: SearchBar

#### Passos:
1. ✅ Na tela de Farmácias
2. ✅ Clicar no SearchBar
3. ✅ Digitar qualquer texto (ex: "Central")
4. ✅ Observar comportamento

#### Resultado Esperado:
- ✅ TextField aceita input
- ✅ Ícone "X" de limpar aparece
- ✅ ViewModel.handleEvent(SearchFarmacias) é chamado
- ✅ Lista filtra em tempo real (quando backend integrado)

#### Debug:
```kotlin
// Adicionar log temporário em FarmaciasViewModel
fun handleEvent(event: FarmaciasUiEvent) {
    when (event) {
        is FarmaciasUiEvent.SearchFarmacias -> {
            println("🔍 Search query: ${event.query}")
            // ...
        }
    }
}
```

---

### Teste 4: FilterChips

#### Passos:
1. ✅ Clicar no chip "Ativas"
2. ✅ Observar mudança visual
3. ✅ Clicar no chip "Todas"

#### Resultado Esperado:
- ✅ Chip selecionado muda cor/estilo
- ✅ ViewModel filtra dados corretamente
- ✅ Lista atualiza (quando backend integrado)

---

### Teste 5: FAB (Floating Action Button)

#### Passos:
1. ✅ Clicar no FAB "Adicionar"

#### Resultado Esperado:
- ✅ `AddFarmaciaDialog` abre
- ✅ Dialog exibe mensagem placeholder
- ✅ Botões "Cancelar" e "Criar" visíveis

#### Próximo passo (futuro):
Substituir dialog por navegação para `AddFarmaciaFormScreen`

---

### Teste 6: FilterDialog

#### Passos:
1. ✅ Clicar no ícone de filtro (se implementado)
2. ✅ Ou adicionar botão temporário para teste

#### Resultado Esperado:
- ✅ Dialog de filtros abre
- ✅ Dropdowns para Status, Tipo, Estado visíveis
- ✅ Botões "Limpar" e "Aplicar" funcionam

---

### Teste 7: Empty State

#### Passos:
1. ✅ Garantir que lista está vazia (sem dados do backend)
2. ✅ Observar UI

#### Resultado Esperado:
- ✅ Ícone LocalPharmacy grande
- ✅ Texto "Nenhuma farmácia encontrada"
- ✅ Subtexto com orientação

#### Verificar:
```kotlin
// FarmaciasScreen.kt
if (farmacias.isEmpty() && !isLoading) {
    EmptyState()
}
```

---

### Teste 8: Error State

#### Passos:
1. ✅ Simular erro (ex: backend offline)
2. ✅ Observar UI

#### Resultado Esperado:
- ✅ Ícone de erro
- ✅ Mensagem de erro exibida
- ✅ Botão "Tentar Novamente" visível
- ✅ Clicar recarrega dados

---

### Teste 9: Navegação de Volta (Back Navigation)

#### Passos:
1. ✅ Abrir tela de Farmácias
2. ✅ Pressionar botão voltar (física ou UI)

#### Resultado Esperado:
- ✅ Retorna para Menu "Mais"
- ✅ OU retorna para Home (dependendo da navegação)

---

### Teste 10: Integração com Dashboard (Usuário PHARMACY)

#### Passos:
1. ✅ Modificar `HomeScreen.kt` temporariamente:
```kotlin
userType = UserType.PHARMACY // Forçar tipo
```
2. ✅ Recompilar
3. ✅ Abrir Dashboard
4. ✅ Procurar cards de "Inventário", "Pedidos", etc.
5. ✅ Clicar em um card

#### Resultado Esperado:
- ✅ Navega para FarmaciasScreen
- ✅ (Atualmente pode não funcionar até rotas serem configuradas)

---

## 🔧 Testes com Backend Mock

### Opção 1: Adicionar Dados Mock no ViewModel

**Arquivo**: `FarmaciasViewModel.kt`

```kotlin
// Adicionar dados mock temporários
init {
    _farmacias.value = listOf(
        Farmacia(
            id = "1",
            nome = "Farmácia Central",
            cnpj = "12.345.678/0001-90",
            email = "contato@central.com",
            telefone = "(11) 98765-4321",
            endereco = Endereco(
                cep = "01234-567",
                rua = "Rua Principal",
                numero = "123",
                bairro = "Centro",
                cidade = "São Paulo",
                estado = "SP",
                pais = "Brasil"
            ),
            tipo = TipoFarmacia.REDE,
            status = StatusFarmacia.ATIVA,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        ),
        Farmacia(
            id = "2",
            nome = "Farmácia Popular",
            cnpj = "98.765.432/0001-10",
            email = "contato@popular.com",
            telefone = "(11) 91234-5678",
            endereco = Endereco(
                cep = "04567-890",
                rua = "Avenida Paulista",
                numero = "1000",
                bairro = "Bela Vista",
                cidade = "São Paulo",
                estado = "SP",
                pais = "Brasil"
            ),
            tipo = TipoFarmacia.POPULAR,
            status = StatusFarmacia.ATIVA,
            createdAt = "2024-01-02T00:00:00Z",
            updatedAt = "2024-01-02T00:00:00Z"
        ),
        Farmacia(
            id = "3",
            nome = "Farmácia Inativa Exemplo",
            cnpj = "11.111.111/0001-11",
            email = "inativa@example.com",
            telefone = "(11) 99999-9999",
            endereco = Endereco(
                cep = "12345-678",
                rua = "Rua Teste",
                numero = "999",
                bairro = "Teste",
                cidade = "São Paulo",
                estado = "SP",
                pais = "Brasil"
            ),
            tipo = TipoFarmacia.INDEPENDENTE,
            status = StatusFarmacia.INATIVA,
            createdAt = "2024-01-03T00:00:00Z",
            updatedAt = "2024-01-03T00:00:00Z"
        )
    )
}
```

**Como adicionar**:
1. Abrir `FarmaciasViewModel.kt`
2. Adicionar código acima no bloco `init { }`
3. Recompilar
4. Executar app
5. ✅ Deve exibir 3 farmácias mock

---

### Opção 2: Mock no Repository

**Arquivo**: `FarmaciaRepositoryImpl.kt`

```kotlin
override fun getFarmacias(): Flow<Result<List<Farmacia>>> = flow {
    // Mock data para testes
    val mockFarmacias = listOf(/* ... dados mock ... */)
    emit(Result.success(mockFarmacias))
    
    // Comentar chamada real ao backend
    // val result = remoteFarmaciaDataSource.getFarmacias()
}
```

---

## 📊 Checklist de Testes

### Navegação
- [ ] Menu Mais → Farmácias
- [ ] Dashboard → Cards → Farmácias (quando PHARMACY user)
- [ ] Voltar (back) funciona corretamente

### UI/UX
- [ ] TopAppBar exibe corretamente
- [ ] SearchBar aceita input
- [ ] FilterChips selecionam/desselecionam
- [ ] FAB abre dialog
- [ ] Empty state exibe quando vazio
- [ ] Error state exibe quando erro
- [ ] Loading state aparece durante carregamento

### Funcionalidades
- [ ] Busca filtra lista (com mock)
- [ ] Filtro "Ativas" funciona (com mock)
- [ ] Filtro "Todas" funciona (com mock)
- [ ] Cards renderizam dados corretamente
- [ ] Status badges mostram cores corretas
- [ ] Action menu (Edit/Delete) aparece

### Integração
- [ ] ViewModel inicializa corretamente
- [ ] StateFlows atualizam UI
- [ ] DependencyContainer fornece instâncias
- [ ] Sem crashes ao navegar

---

## 🐛 Problemas Comuns e Soluções

### Problema: Tela branca ao abrir Farmácias

**Possíveis causas**:
1. ViewModel não inicializado
2. Erro no DependencyContainer
3. Falta import

**Solução**:
```kotlin
// Verificar em FarmaciasScreen.kt
val viewModel = remember { 
    FarmaciaDependencyContainer.provideFarmaciasViewModel() 
}

// Adicionar log:
println("✅ FarmaciasViewModel initialized")
```

---

### Problema: "Unresolved reference: FarmaciasScreen"

**Solução**:
```kotlin
// Em DashboardScreen.kt, verificar import:
import edu.fatec.petwise.features.farmacias.presentation.screens.FarmaciasScreen
```

---

### Problema: FAB não responde

**Solução**:
```kotlin
// Verificar onClick em FarmaciasScreen.kt
floatingActionButton = {
    ExtendedFloatingActionButton(
        onClick = { 
            println("🔘 FAB clicked")
            viewModel.handleEvent(FarmaciasUiEvent.ShowAddDialog) 
        }
        // ...
    )
}
```

---

### Problema: Lista sempre vazia

**Solução**:
1. Adicionar mock data (ver Opção 1 acima)
2. Verificar `_farmacias.value` no ViewModel
3. Verificar `collectAsState()` na Screen

```kotlin
// Debug
LaunchedEffect(Unit) {
    println("📊 Farmacias count: ${farmacias.size}")
}
```

---

## 📱 Testes em Diferentes Plataformas

### Android
```powershell
.\gradlew.bat :composeApp:installDebug
# Ou Android Studio → Run
```

**Verificar**:
- Bottom navigation funciona
- Gestures (swipe, scroll)
- Teclado aparece no SearchBar

---

### Desktop (JVM)
```powershell
.\gradlew.bat :composeApp:run
```

**Verificar**:
- Janela redimensionável
- Mouse hover effects
- Keyboard shortcuts

---

### Web (JS)
```powershell
.\gradlew.bat :composeApp:jsBrowserDevelopmentRun
```

**Verificar**:
- Responsividade no browser
- Cliques funcionam
- Sem erros no console

---

## 🧪 Testes Unitários (Para Implementar)

### Exemplo: Testar CNPJ Validator

**Criar arquivo**: `FarmaciaTest.kt` em `commonTest/`

```kotlin
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class FarmaciaTest {
    
    @Test
    fun `should validate correct CNPJ`() {
        val farmacia = Farmacia(/* ... */, cnpj = "12.345.678/0001-90")
        assertTrue(farmacia.isCnpjValido())
    }
    
    @Test
    fun `should reject invalid CNPJ`() {
        val farmacia = Farmacia(/* ... */, cnpj = "00.000.000/0000-00")
        assertFalse(farmacia.isCnpjValido())
    }
}
```

**Executar testes**:
```powershell
.\gradlew.bat :composeApp:test
```

---

## 📝 Logs Úteis

### Adicionar logs temporários para debug

**FarmaciasScreen.kt**:
```kotlin
LaunchedEffect(Unit) {
    println("🎯 FarmaciasScreen composed")
}

LaunchedEffect(farmacias) {
    println("📋 Farmacias updated: ${farmacias.size} items")
}
```

**FarmaciasViewModel.kt**:
```kotlin
fun handleEvent(event: FarmaciasUiEvent) {
    println("🎬 Event received: $event")
    when (event) {
        // ...
    }
}
```

**NavigationManager.kt**:
```kotlin
fun navigateToTab(screen: TabScreen) {
    println("🧭 Navigating to: ${screen::class.simpleName}")
    _currentTabScreen.value = screen
}
```

---

## ✅ Testes Finais Antes de Deploy

### 1. Compilação Limpa
```powershell
.\gradlew.bat clean
.\gradlew.bat build
```

### 2. Verificar Erros
```powershell
.\gradlew.bat :composeApp:check
```

### 3. Code Quality (Opcional)
```powershell
.\gradlew.bat detekt  # Se configurado
.\gradlew.bat ktlintCheck  # Se configurado
```

### 4. Build de Produção
```powershell
.\gradlew.bat :composeApp:assembleRelease  # Android
.\gradlew.bat :composeApp:jsBrowserProductionWebpack  # Web
```

---

## 🎉 Sucesso!

Se todos os testes passarem:

✅ Navegação funciona
✅ UI renderiza corretamente
✅ Estados (loading, error, empty) funcionam
✅ ViewModels gerenciam estado
✅ DependencyContainer fornece instâncias
✅ Sem crashes ou erros

**Próximo passo**: Integrar com backend real! 🚀

---

## 📞 Troubleshooting Avançado

### Verificar versões das dependências

```powershell
.\gradlew.bat :composeApp:dependencies
```

### Limpar cache Gradle

```powershell
.\gradlew.bat clean
Remove-Item -Recurse -Force .gradle
.\gradlew.bat build
```

### Verificar configuração do SDK

```powershell
# Verificar SDK Android
$env:ANDROID_HOME

# Verificar Java
java -version
```

---

## 📚 Recursos Adicionais

- **Documentação Compose**: https://developer.android.com/jetpack/compose
- **Kotlin Flows**: https://kotlinlang.org/docs/flow.html
- **KMP Guide**: https://kotlinlang.org/docs/multiplatform.html
- **Material 3**: https://m3.material.io/

---

**Guia de testes criado para o Módulo Farmácias - PetWise**
*Versão: 1.0 | Atualizado: 2024*

**Dúvidas?** Consulte `README.md`, `INTEGRATION_GUIDE.md`, ou `ARCHITECTURE_DIAGRAM.md`
