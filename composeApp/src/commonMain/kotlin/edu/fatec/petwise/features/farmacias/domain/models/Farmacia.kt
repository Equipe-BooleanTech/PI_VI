package edu.fatec.petwise.features.farmacias.domain.models

import kotlinx.serialization.Serializable

/**
 * Enumeração representando o tipo/categoria da farmácia.
 */
@Serializable
enum class TipoFarmacia(val displayName: String) {
    VETERINARIA("Farmácia Veterinária"),
    MANIPULACAO("Farmácia de Manipulação"),
    COMERCIAL("Farmácia Comercial"),
    HOSPITALAR("Farmácia Hospitalar"),
    DISTRIBUIDORA("Distribuidora");

    companion object {
        fun fromString(value: String): TipoFarmacia {
            return values().find { 
                it.name.equals(value, ignoreCase = true) || 
                it.displayName.equals(value, ignoreCase = true)
            } ?: VETERINARIA
        }
    }
}

/**
 * Enumeração representando o status operacional da farmácia.
 */
@Serializable
enum class StatusFarmacia(val displayName: String, val cor: String) {
    ATIVA("Ativa", "#4CAF50"),
    INATIVA("Inativa", "#9E9E9E"),
    SUSPENSA("Suspensa", "#FF9800"),
    BLOQUEADA("Bloqueada", "#F44336");

    companion object {
        fun fromString(value: String): StatusFarmacia {
            return values().find { 
                it.name.equals(value, ignoreCase = true) || 
                it.displayName.equals(value, ignoreCase = true)
            } ?: ATIVA
        }
    }
}

/**
 * Enumeração representando a região de atuação.
 */
@Serializable
enum class RegiaoAtuacao(val displayName: String) {
    MUNICIPAL("Municipal"),
    ESTADUAL("Estadual"),
    REGIONAL("Regional"),
    NACIONAL("Nacional"),
    INTERNACIONAL("Internacional");

    companion object {
        fun fromString(value: String): RegiaoAtuacao {
            return values().find { 
                it.name.equals(value, ignoreCase = true) || 
                it.displayName.equals(value, ignoreCase = true)
            } ?: MUNICIPAL
        }
    }
}

/**
 * Entidade de domínio representando uma Farmácia.
 * 
 * Esta classe modela todos os dados relevantes de uma farmácia parceira,
 * incluindo informações cadastrais, regulatórias, comerciais e de contato.
 * 
 * ## Responsabilidades:
 * - Armazenar dados cadastrais completos
 * - Manter informações de regulamentação (CNPJ, Anvisa, CRF)
 * - Gerenciar status operacional
 * - Controlar condições comerciais (crédito, desconto)
 * 
 * ## Validações de Negócio:
 * - CNPJ deve ter 14 dígitos
 * - E-mail deve ser válido
 * - Limite de crédito não pode ser negativo
 * - Desconto padrão deve estar entre 0 e 100%
 * 
 * @property id Identificador único da farmácia
 * @property razaoSocial Nome empresarial completo
 * @property nomeFantasia Nome comercial/fantasia
 * @property cnpj Cadastro Nacional de Pessoa Jurídica (14 dígitos)
 * @property inscricaoEstadual Inscrição Estadual
 * @property inscricaoMunicipal Inscrição Municipal
 * @property tipo Categoria/tipo da farmácia
 * @property status Status operacional atual
 * @property dataAbertura Data de abertura do estabelecimento
 * @property responsavelTecnico Nome do farmacêutico responsável
 * @property crf Conselho Regional de Farmácia do responsável
 * @property registroAnvisa Número de registro na Anvisa
 * @property autorizacaoFuncionamento Número da Autorização de Funcionamento
 * @property endereco Endereço completo
 * @property numero Número do estabelecimento
 * @property complemento Complemento do endereço (opcional)
 * @property bairro Bairro/distrito
 * @property cidade Cidade
 * @property estado Estado (UF)
 * @property cep Código de Endereçamento Postal
 * @property regiao Região de atuação comercial
 * @property telefone Telefone principal
 * @property celular Celular de contato (opcional)
 * @property email E-mail principal
 * @property emailFinanceiro E-mail para assuntos financeiros (opcional)
 * @property site Website da farmácia (opcional)
 * @property limiteCredito Limite de crédito disponível
 * @property descontoMaximo Desconto máximo permitido (percentual)
 * @property prazoEntregaDias Prazo médio de entrega em dias
 * @property freteGratis Se oferece frete grátis
 * @property valorMinimoFrete Valor mínimo para frete grátis
 * @property observacoes Observações gerais (opcional)
 * @property dataRegistro Data/hora de cadastro no sistema
 * @property dataUltimaAtualizacao Data/hora da última atualização (opcional)
 */
@Serializable
data class Farmacia(
    val id: String = "",
    val razaoSocial: String,
    val nomeFantasia: String,
    val cnpj: String,
    val inscricaoEstadual: String = "",
    val inscricaoMunicipal: String = "",
    val tipo: TipoFarmacia,
    val status: StatusFarmacia = StatusFarmacia.ATIVA,
    val dataAbertura: String,
    
    // Dados do Responsável Técnico
    val responsavelTecnico: String,
    val crf: String,
    val registroAnvisa: String,
    val autorizacaoFuncionamento: String,
    
    // Endereço
    val endereco: String,
    val numero: String,
    val complemento: String? = null,
    val bairro: String,
    val cidade: String,
    val estado: String,
    val cep: String,
    val regiao: RegiaoAtuacao,
    
    // Contatos
    val telefone: String,
    val celular: String? = null,
    val email: String,
    val emailFinanceiro: String? = null,
    val site: String? = null,
    
    // Dados Comerciais
    val limiteCredito: Double = 0.0,
    val descontoMaximo: Double = 0.0,
    val prazoEntregaDias: Int = 7,
    val freteGratis: Boolean = false,
    val valorMinimoFrete: Double = 0.0,
    
    // Metadados
    val observacoes: String? = null,
    val dataRegistro: String,
    val dataUltimaAtualizacao: String? = null
) {
    /**
     * Valida se o CNPJ tem formato correto (14 dígitos).
     */
    fun isCnpjValido(): Boolean {
        val cnpjNumeros = cnpj.replace(Regex("[^0-9]"), "")
        return cnpjNumeros.length == 14
    }

    /**
     * Valida se o e-mail tem formato básico correto.
     */
    fun isEmailValido(): Boolean {
        return email.contains("@") && email.contains(".")
    }

    /**
     * Retorna o CNPJ formatado (##.###.###/####-##).
     */
    fun getCnpjFormatado(): String {
        val numeros = cnpj.replace(Regex("[^0-9]"), "")
        return if (numeros.length == 14) {
            "${numeros.substring(0, 2)}.${numeros.substring(2, 5)}.${numeros.substring(5, 8)}/${numeros.substring(8, 12)}-${numeros.substring(12, 14)}"
        } else {
            cnpj
        }
    }

    /**
     * Retorna o CEP formatado (#####-###).
     */
    fun getCepFormatado(): String {
        val numeros = cep.replace(Regex("[^0-9]"), "")
        return if (numeros.length == 8) {
            "${numeros.substring(0, 5)}-${numeros.substring(5, 8)}"
        } else {
            cep
        }
    }

    /**
     * Retorna o endereço completo formatado.
     */
    fun getEnderecoCompleto(): String {
        val complementoStr = complemento?.let { ", $it" } ?: ""
        return "$endereco, $numero$complementoStr - $bairro, $cidade/$estado - ${getCepFormatado()}"
    }

    /**
     * Verifica se a farmácia está operacional.
     */
    fun isOperacional(): Boolean {
        return status == StatusFarmacia.ATIVA
    }

    /**
     * Calcula o valor com desconto aplicado.
     */
    fun calcularValorComDesconto(valorOriginal: Double, desconto: Double = descontoMaximo): Double {
        val descontoAplicado = desconto.coerceIn(0.0, descontoMaximo)
        return valorOriginal * (1 - descontoAplicado / 100)
    }

    /**
     * Verifica se tem limite de crédito disponível.
     */
    fun temCreditoDisponivel(valor: Double): Boolean {
        return limiteCredito >= valor
    }

    /**
     * Verifica se o pedido se qualifica para frete grátis.
     */
    fun qualificaFreteGratis(valorPedido: Double): Boolean {
        return freteGratis && valorPedido >= valorMinimoFrete
    }
}

/**
 * Opções de filtro para farmácias.
 * 
 * Permite filtrar farmácias por múltiplos critérios combinados.
 */
@Serializable
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
