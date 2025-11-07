package edu.fatec.petwise.features.farmacias.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.farmacias.domain.models.*
import edu.fatec.petwise.features.farmacias.domain.usecases.AddFarmaciaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

/**
 * Estado da UI para adição de farmácia.
 */
data class AddFarmaciaUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel para criação de novas farmácias.
 */
class AddFarmaciaViewModel(
    private val addFarmaciaUseCase: AddFarmaciaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddFarmaciaUiState())
    val uiState: StateFlow<AddFarmaciaUiState> = _uiState.asStateFlow()

    fun addFarmacia(
        razaoSocial: String,
        nomeFantasia: String,
        cnpj: String,
        inscricaoEstadual: String,
        inscricaoMunicipal: String,
        tipo: TipoFarmacia,
        dataAbertura: String,
        responsavelTecnico: String,
        crf: String,
        registroAnvisa: String,
        autorizacaoFuncionamento: String,
        endereco: String,
        numero: String,
        complemento: String?,
        bairro: String,
        cidade: String,
        estado: String,
        cep: String,
        regiao: RegiaoAtuacao,
        telefone: String,
        celular: String?,
        email: String,
        emailFinanceiro: String?,
        site: String?,
        limiteCredito: Double,
        descontoMaximo: Double,
        prazoEntregaDias: Int,
        freteGratis: Boolean,
        valorMinimoFrete: Double,
        observacoes: String?
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val farmacia = Farmacia(
                razaoSocial = razaoSocial.trim(),
                nomeFantasia = nomeFantasia.trim(),
                cnpj = cnpj.replace(Regex("[^0-9]"), ""),
                inscricaoEstadual = inscricaoEstadual.trim(),
                inscricaoMunicipal = inscricaoMunicipal.trim(),
                tipo = tipo,
                dataAbertura = dataAbertura,
                responsavelTecnico = responsavelTecnico.trim(),
                crf = crf.trim(),
                registroAnvisa = registroAnvisa.trim(),
                autorizacaoFuncionamento = autorizacaoFuncionamento.trim(),
                endereco = endereco.trim(),
                numero = numero.trim(),
                complemento = complemento?.trim(),
                bairro = bairro.trim(),
                cidade = cidade.trim(),
                estado = estado.trim().uppercase(),
                cep = cep.replace(Regex("[^0-9]"), ""),
                regiao = regiao,
                telefone = telefone.replace(Regex("[^0-9]"), ""),
                celular = celular?.replace(Regex("[^0-9]"), ""),
                email = email.trim().lowercase(),
                emailFinanceiro = emailFinanceiro?.trim()?.lowercase(),
                site = site?.trim(),
                limiteCredito = limiteCredito,
                descontoMaximo = descontoMaximo,
                prazoEntregaDias = prazoEntregaDias,
                freteGratis = freteGratis,
                valorMinimoFrete = valorMinimoFrete,
                observacoes = observacoes?.trim(),
                dataRegistro = Clock.System.now().toString()
            )

            addFarmaciaUseCase(farmacia).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Erro ao adicionar farmácia"
                    )
                }
            )
        }
    }

    fun clearState() {
        _uiState.value = AddFarmaciaUiState()
    }
}
