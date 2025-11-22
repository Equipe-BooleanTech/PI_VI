package edu.fatec.petwise.features.hygiene.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.hygiene.di.HygieneDependencyContainer
import edu.fatec.petwise.features.hygiene.domain.models.HygieneProduct
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class HygieneUiState(
    val products: List<HygieneProduct> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedCategory: String? = null
)

sealed class HygieneUiEvent {
    object LoadProducts : HygieneUiEvent()
    data class SearchProducts(val query: String) : HygieneUiEvent()
    data class FilterByCategory(val category: String?) : HygieneUiEvent()
    data class AddProduct(val product: HygieneProduct) : HygieneUiEvent()
    data class UpdateProduct(val product: HygieneProduct) : HygieneUiEvent()
    data class DeleteProduct(val productId: String) : HygieneUiEvent()
}

class HygieneViewModel : ViewModel() {

    private val getHygieneProductsUseCase = HygieneDependencyContainer.getHygieneProductsUseCase
    private val addHygieneProductUseCase = HygieneDependencyContainer.addHygieneProductUseCase
    private val updateHygieneProductUseCase = HygieneDependencyContainer.updateHygieneProductUseCase
    private val deleteHygieneProductUseCase = HygieneDependencyContainer.deleteHygieneProductUseCase

    private val _uiState = MutableStateFlow(HygieneUiState())
    val uiState: StateFlow<HygieneUiState> = _uiState.asStateFlow()

    private var currentDataJob: kotlinx.coroutines.Job? = null

    init {
        loadProducts()
    }

    override fun onCleared() {
        super.onCleared()
        currentDataJob?.cancel()
    }

    fun onEvent(event: HygieneUiEvent) {
        when (event) {
            is HygieneUiEvent.LoadProducts -> loadProducts()
            is HygieneUiEvent.SearchProducts -> searchProducts(event.query)
            is HygieneUiEvent.FilterByCategory -> filterByCategory(event.category)
            is HygieneUiEvent.AddProduct -> addProduct(event.product)
            is HygieneUiEvent.UpdateProduct -> updateProduct(event.product)
            is HygieneUiEvent.DeleteProduct -> deleteProduct(event.productId)
        }
    }

    private fun loadProducts() {
        // Cancel any existing data loading job
        currentDataJob?.cancel()

        currentDataJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                getHygieneProductsUseCase().collectLatest { products ->
                    _uiState.value = _uiState.value.copy(
                        products = products,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                if (e !is kotlinx.coroutines.CancellationException) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Erro ao carregar produtos de higiene"
                    )
                }
            }
        }
    }

    private fun searchProducts(query: String) {
        // Cancel any existing data loading job
        currentDataJob?.cancel()

        currentDataJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(searchQuery = query, isLoading = true)

            try {
                if (query.isEmpty()) {
                    getHygieneProductsUseCase().collectLatest { products ->
                        _uiState.value = _uiState.value.copy(
                            products = products,
                            isLoading = false
                        )
                    }
                } else {
                    getHygieneProductsUseCase.searchProducts(query).collectLatest { products ->
                        _uiState.value = _uiState.value.copy(
                            products = products,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                if (e !is kotlinx.coroutines.CancellationException) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Erro ao buscar produtos de higiene"
                    )
                }
            }
        }
    }

    private fun filterByCategory(category: String?) {
        // Cancel any existing data loading job
        currentDataJob?.cancel()

        currentDataJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(selectedCategory = category, isLoading = true)

            try {
                if (category == null) {
                    getHygieneProductsUseCase().collectLatest { products ->
                        _uiState.value = _uiState.value.copy(
                            products = products,
                            isLoading = false
                        )
                    }
                } else {
                    getHygieneProductsUseCase.getProductsByCategory(category).collectLatest { products ->
                        _uiState.value = _uiState.value.copy(
                            products = products,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                if (e !is kotlinx.coroutines.CancellationException) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Erro ao filtrar produtos de higiene"
                    )
                }
            }
        }
    }

    private fun addProduct(product: HygieneProduct) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                addHygieneProductUseCase(product).fold(
                    onSuccess = { newProduct ->
                        // Reload products to get updated list
                        loadProducts()
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao adicionar produto de higiene"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro ao adicionar produto de higiene"
                )
            }
        }
    }

    private fun updateProduct(product: HygieneProduct) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                updateHygieneProductUseCase(product).fold(
                    onSuccess = { updatedProduct ->
                        // Reload products to get updated list
                        loadProducts()
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao atualizar produto de higiene"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro ao atualizar produto de higiene"
                )
            }
        }
    }

    private fun deleteProduct(productId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                deleteHygieneProductUseCase(productId).fold(
                    onSuccess = {
                        // Reload products to get updated list
                        loadProducts()
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao excluir produto de higiene"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro ao excluir produto de higiene"
                )
            }
        }
    }
}