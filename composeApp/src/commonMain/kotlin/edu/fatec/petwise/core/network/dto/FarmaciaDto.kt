package edu.fatec.petwise.core.network.dto

import edu.fatec.petwise.features.farmacias.domain.models.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO para transferência de dados de Farmácia via rede.
 * 
 * Mapeia os dados entre a camada de rede e o domínio,
 * aplicando as anotações necessárias para serialização JSON.
 */
@Serializable
data class FarmaciaDto(
    @SerialName("id") val id: String = "",
    @SerialName("razao_social") val razaoSocial: String,
    @SerialName("nome_fantasia") val nomeFantasia: String,
    @SerialName("cnpj") val cnpj: String,
    @SerialName("inscricao_estadual") val inscricaoEstadual: String = "",
    @SerialName("inscricao_municipal") val inscricaoMunicipal: String = "",
    @SerialName("tipo") val tipo: String,
    @SerialName("status") val status: String,
    @SerialName("data_abertura") val dataAbertura: String,
    @SerialName("responsavel_tecnico") val responsavelTecnico: String,
    @SerialName("crf") val crf: String,
    @SerialName("registro_anvisa") val registroAnvisa: String,
    @SerialName("autorizacao_funcionamento") val autorizacaoFuncionamento: String,
    @SerialName("endereco") val endereco: String,
    @SerialName("numero") val numero: String,
    @SerialName("complemento") val complemento: String? = null,
    @SerialName("bairro") val bairro: String,
    @SerialName("cidade") val cidade: String,
    @SerialName("estado") val estado: String,
    @SerialName("cep") val cep: String,
    @SerialName("regiao") val regiao: String,
    @SerialName("telefone") val telefone: String,
    @SerialName("celular") val celular: String? = null,
    @SerialName("email") val email: String,
    @SerialName("email_financeiro") val emailFinanceiro: String? = null,
    @SerialName("site") val site: String? = null,
    @SerialName("limite_credito") val limiteCredito: Double = 0.0,
    @SerialName("desconto_maximo") val descontoMaximo: Double = 0.0,
    @SerialName("prazo_entrega_dias") val prazoEntregaDias: Int = 7,
    @SerialName("frete_gratis") val freteGratis: Boolean = false,
    @SerialName("valor_minimo_frete") val valorMinimoFrete: Double = 0.0,
    @SerialName("observacoes") val observacoes: String? = null,
    @SerialName("data_registro") val dataRegistro: String,
    @SerialName("data_ultima_atualizacao") val dataUltimaAtualizacao: String? = null
)

/**
 * DTO para resposta de lista de farmácias.
 */
@Serializable
data class FarmaciaListResponse(
    @SerialName("farmacias") val farmacias: List<FarmaciaDto>,
    @SerialName("total") val total: Int,
    @SerialName("page") val page: Int? = null,
    @SerialName("page_size") val pageSize: Int? = null
)

/**
 * DTO para requisição de criação de farmácia.
 */
@Serializable
data class CreateFarmaciaRequest(
    @SerialName("razao_social") val razaoSocial: String,
    @SerialName("nome_fantasia") val nomeFantasia: String,
    @SerialName("cnpj") val cnpj: String,
    @SerialName("inscricao_estadual") val inscricaoEstadual: String = "",
    @SerialName("inscricao_municipal") val inscricaoMunicipal: String = "",
    @SerialName("tipo") val tipo: String,
    @SerialName("data_abertura") val dataAbertura: String,
    @SerialName("responsavel_tecnico") val responsavelTecnico: String,
    @SerialName("crf") val crf: String,
    @SerialName("registro_anvisa") val registroAnvisa: String,
    @SerialName("autorizacao_funcionamento") val autorizacaoFuncionamento: String,
    @SerialName("endereco") val endereco: String,
    @SerialName("numero") val numero: String,
    @SerialName("complemento") val complemento: String? = null,
    @SerialName("bairro") val bairro: String,
    @SerialName("cidade") val cidade: String,
    @SerialName("estado") val estado: String,
    @SerialName("cep") val cep: String,
    @SerialName("regiao") val regiao: String,
    @SerialName("telefone") val telefone: String,
    @SerialName("celular") val celular: String? = null,
    @SerialName("email") val email: String,
    @SerialName("email_financeiro") val emailFinanceiro: String? = null,
    @SerialName("site") val site: String? = null,
    @SerialName("limite_credito") val limiteCredito: Double = 0.0,
    @SerialName("desconto_maximo") val descontoMaximo: Double = 0.0,
    @SerialName("prazo_entrega_dias") val prazoEntregaDias: Int = 7,
    @SerialName("frete_gratis") val freteGratis: Boolean = false,
    @SerialName("valor_minimo_frete") val valorMinimoFrete: Double = 0.0,
    @SerialName("observacoes") val observacoes: String? = null
)

/**
 * DTO para requisição de atualização de farmácia.
 */
@Serializable
data class UpdateFarmaciaRequest(
    @SerialName("razao_social") val razaoSocial: String? = null,
    @SerialName("nome_fantasia") val nomeFantasia: String? = null,
    @SerialName("inscricao_estadual") val inscricaoEstadual: String? = null,
    @SerialName("inscricao_municipal") val inscricaoMunicipal: String? = null,
    @SerialName("tipo") val tipo: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("responsavel_tecnico") val responsavelTecnico: String? = null,
    @SerialName("crf") val crf: String? = null,
    @SerialName("registro_anvisa") val registroAnvisa: String? = null,
    @SerialName("autorizacao_funcionamento") val autorizacaoFuncionamento: String? = null,
    @SerialName("endereco") val endereco: String? = null,
    @SerialName("numero") val numero: String? = null,
    @SerialName("complemento") val complemento: String? = null,
    @SerialName("bairro") val bairro: String? = null,
    @SerialName("cidade") val cidade: String? = null,
    @SerialName("estado") val estado: String? = null,
    @SerialName("cep") val cep: String? = null,
    @SerialName("regiao") val regiao: String? = null,
    @SerialName("telefone") val telefone: String? = null,
    @SerialName("celular") val celular: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("email_financeiro") val emailFinanceiro: String? = null,
    @SerialName("site") val site: String? = null,
    @SerialName("limite_credito") val limiteCredito: Double? = null,
    @SerialName("desconto_maximo") val descontoMaximo: Double? = null,
    @SerialName("prazo_entrega_dias") val prazoEntregaDias: Int? = null,
    @SerialName("frete_gratis") val freteGratis: Boolean? = null,
    @SerialName("valor_minimo_frete") val valorMinimoFrete: Double? = null,
    @SerialName("observacoes") val observacoes: String? = null
)

/**
 * DTO para atualização de limite de crédito.
 */
@Serializable
data class UpdateLimiteCreditoRequest(
    @SerialName("limite_credito") val limiteCredito: Double
)

/**
 * DTO para atualização de status.
 */
@Serializable
data class UpdateStatusRequest(
    @SerialName("status") val status: String,
    @SerialName("motivo") val motivo: String? = null
)

// ===========================
// EXTENSION FUNCTIONS - MAPPERS
// ===========================

/**
 * Converte entidade de domínio para DTO de rede.
 */
fun Farmacia.toDto(): FarmaciaDto {
    return FarmaciaDto(
        id = id,
        razaoSocial = razaoSocial,
        nomeFantasia = nomeFantasia,
        cnpj = cnpj,
        inscricaoEstadual = inscricaoEstadual,
        inscricaoMunicipal = inscricaoMunicipal,
        tipo = tipo.name,
        status = status.name,
        dataAbertura = dataAbertura,
        responsavelTecnico = responsavelTecnico,
        crf = crf,
        registroAnvisa = registroAnvisa,
        autorizacaoFuncionamento = autorizacaoFuncionamento,
        endereco = endereco,
        numero = numero,
        complemento = complemento,
        bairro = bairro,
        cidade = cidade,
        estado = estado,
        cep = cep,
        regiao = regiao.name,
        telefone = telefone,
        celular = celular,
        email = email,
        emailFinanceiro = emailFinanceiro,
        site = site,
        limiteCredito = limiteCredito,
        descontoMaximo = descontoMaximo,
        prazoEntregaDias = prazoEntregaDias,
        freteGratis = freteGratis,
        valorMinimoFrete = valorMinimoFrete,
        observacoes = observacoes,
        dataRegistro = dataRegistro,
        dataUltimaAtualizacao = dataUltimaAtualizacao
    )
}

/**
 * Converte DTO de rede para entidade de domínio.
 */
fun FarmaciaDto.toDomain(): Farmacia {
    return Farmacia(
        id = id,
        razaoSocial = razaoSocial,
        nomeFantasia = nomeFantasia,
        cnpj = cnpj,
        inscricaoEstadual = inscricaoEstadual,
        inscricaoMunicipal = inscricaoMunicipal,
        tipo = mapStringToTipoFarmacia(tipo),
        status = mapStringToStatusFarmacia(status),
        dataAbertura = dataAbertura,
        responsavelTecnico = responsavelTecnico,
        crf = crf,
        registroAnvisa = registroAnvisa,
        autorizacaoFuncionamento = autorizacaoFuncionamento,
        endereco = endereco,
        numero = numero,
        complemento = complemento,
        bairro = bairro,
        cidade = cidade,
        estado = estado,
        cep = cep,
        regiao = mapStringToRegiaoAtuacao(regiao),
        telefone = telefone,
        celular = celular,
        email = email,
        emailFinanceiro = emailFinanceiro,
        site = site,
        limiteCredito = limiteCredito,
        descontoMaximo = descontoMaximo,
        prazoEntregaDias = prazoEntregaDias,
        freteGratis = freteGratis,
        valorMinimoFrete = valorMinimoFrete,
        observacoes = observacoes,
        dataRegistro = dataRegistro,
        dataUltimaAtualizacao = dataUltimaAtualizacao
    )
}

/**
 * Converte Farmacia para CreateFarmaciaRequest.
 */
fun Farmacia.toCreateRequest(): CreateFarmaciaRequest {
    return CreateFarmaciaRequest(
        razaoSocial = razaoSocial,
        nomeFantasia = nomeFantasia,
        cnpj = cnpj,
        inscricaoEstadual = inscricaoEstadual,
        inscricaoMunicipal = inscricaoMunicipal,
        tipo = tipo.name,
        dataAbertura = dataAbertura,
        responsavelTecnico = responsavelTecnico,
        crf = crf,
        registroAnvisa = registroAnvisa,
        autorizacaoFuncionamento = autorizacaoFuncionamento,
        endereco = endereco,
        numero = numero,
        complemento = complemento,
        bairro = bairro,
        cidade = cidade,
        estado = estado,
        cep = cep,
        regiao = regiao.name,
        telefone = telefone,
        celular = celular,
        email = email,
        emailFinanceiro = emailFinanceiro,
        site = site,
        limiteCredito = limiteCredito,
        descontoMaximo = descontoMaximo,
        prazoEntregaDias = prazoEntregaDias,
        freteGratis = freteGratis,
        valorMinimoFrete = valorMinimoFrete,
        observacoes = observacoes
    )
}

/**
 * Converte Farmacia para UpdateFarmaciaRequest.
 */
fun Farmacia.toUpdateRequest(): UpdateFarmaciaRequest {
    return UpdateFarmaciaRequest(
        razaoSocial = razaoSocial,
        nomeFantasia = nomeFantasia,
        inscricaoEstadual = inscricaoEstadual,
        inscricaoMunicipal = inscricaoMunicipal,
        tipo = tipo.name,
        status = status.name,
        responsavelTecnico = responsavelTecnico,
        crf = crf,
        registroAnvisa = registroAnvisa,
        autorizacaoFuncionamento = autorizacaoFuncionamento,
        endereco = endereco,
        numero = numero,
        complemento = complemento,
        bairro = bairro,
        cidade = cidade,
        estado = estado,
        cep = cep,
        regiao = regiao.name,
        telefone = telefone,
        celular = celular,
        email = email,
        emailFinanceiro = emailFinanceiro,
        site = site,
        limiteCredito = limiteCredito,
        descontoMaximo = descontoMaximo,
        prazoEntregaDias = prazoEntregaDias,
        freteGratis = freteGratis,
        valorMinimoFrete = valorMinimoFrete,
        observacoes = observacoes
    )
}

// ===========================
// HELPER FUNCTIONS
// ===========================

private fun mapStringToTipoFarmacia(tipo: String): TipoFarmacia {
    return try {
        TipoFarmacia.valueOf(tipo.uppercase())
    } catch (e: Exception) {
        TipoFarmacia.VETERINARIA
    }
}

private fun mapStringToStatusFarmacia(status: String): StatusFarmacia {
    return try {
        StatusFarmacia.valueOf(status.uppercase())
    } catch (e: Exception) {
        StatusFarmacia.ATIVA
    }
}

private fun mapStringToRegiaoAtuacao(regiao: String): RegiaoAtuacao {
    return try {
        RegiaoAtuacao.valueOf(regiao.uppercase())
    } catch (e: Exception) {
        RegiaoAtuacao.MUNICIPAL
    }
}
