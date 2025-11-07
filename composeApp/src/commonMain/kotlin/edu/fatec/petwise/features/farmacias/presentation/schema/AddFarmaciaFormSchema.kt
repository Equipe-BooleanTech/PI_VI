package edu.fatec.petwise.features.farmacias.presentation.schema

import edu.fatec.petwise.features.auth.shared.FormField
import edu.fatec.petwise.features.auth.shared.FormFieldType
import edu.fatec.petwise.features.auth.shared.FormSchema
import edu.fatec.petwise.features.auth.shared.Validator
import edu.fatec.petwise.features.farmacias.domain.models.*

/**
 * Schema de formulário para adicionar/editar Farmácia.
 * 
 * Define todos os campos, tipos, validações e metadados
 * necessários para o formulário de farmácia.
 */
object AddFarmaciaFormSchema : FormSchema {
    override val fields = listOf(
        // ===== DADOS EMPRESARIAIS =====
        FormField(
            name = "razaoSocial",
            label = "Razão Social",
            type = FormFieldType.TEXT,
            required = true,
            validators = listOf(
                Validator.Required("Razão Social é obrigatória"),
                Validator.MinLength(5, "Razão Social deve ter pelo menos 5 caracteres")
            )
        ),
        FormField(
            name = "nomeFantasia",
            label = "Nome Fantasia",
            type = FormFieldType.TEXT,
            required = true,
            validators = listOf(
                Validator.Required("Nome Fantasia é obrigatório"),
                Validator.MinLength(3, "Nome Fantasia deve ter pelo menos 3 caracteres")
            )
        ),
        FormField(
            name = "cnpj",
            label = "CNPJ",
            type = FormFieldType.TEXT,
            required = true,
            placeholder = "00.000.000/0000-00",
            validators = listOf(
                Validator.Required("CNPJ é obrigatório"),
                Validator.Pattern(
                    Regex("\\d{2}\\.?\\d{3}\\.?\\d{3}/?\\d{4}-?\\d{2}"),
                    "CNPJ inválido"
                )
            )
        ),
        FormField(
            name = "inscricaoEstadual",
            label = "Inscrição Estadual",
            type = FormFieldType.TEXT,
            required = false
        ),
        FormField(
            name = "inscricaoMunicipal",
            label = "Inscrição Municipal",
            type = FormFieldType.TEXT,
            required = false
        ),
        FormField(
            name = "tipo",
            label = "Tipo de Farmácia",
            type = FormFieldType.SELECT,
            required = true,
            options = TipoFarmacia.values().map { it.displayName },
            validators = listOf(
                Validator.Required("Tipo é obrigatório")
            )
        ),
        FormField(
            name = "dataAbertura",
            label = "Data de Abertura",
            type = FormFieldType.DATE,
            required = true,
            validators = listOf(
                Validator.Required("Data de Abertura é obrigatória")
            )
        ),

        // ===== RESPONSÁVEL TÉCNICO =====
        FormField(
            name = "responsavelTecnico",
            label = "Responsável Técnico",
            type = FormFieldType.TEXT,
            required = true,
            validators = listOf(
                Validator.Required("Responsável Técnico é obrigatório"),
                Validator.MinLength(3, "Nome deve ter pelo menos 3 caracteres")
            )
        ),
        FormField(
            name = "crf",
            label = "CRF",
            type = FormFieldType.TEXT,
            required = true,
            placeholder = "00000",
            validators = listOf(
                Validator.Required("CRF é obrigatório")
            )
        ),
        FormField(
            name = "registroAnvisa",
            label = "Registro Anvisa",
            type = FormFieldType.TEXT,
            required = true,
            validators = listOf(
                Validator.Required("Registro Anvisa é obrigatório")
            )
        ),
        FormField(
            name = "autorizacaoFuncionamento",
            label = "Autorização de Funcionamento",
            type = FormFieldType.TEXT,
            required = true,
            validators = listOf(
                Validator.Required("Autorização de Funcionamento é obrigatória")
            )
        ),

        // ===== ENDEREÇO =====
        FormField(
            name = "endereco",
            label = "Endereço",
            type = FormFieldType.TEXT,
            required = true,
            validators = listOf(
                Validator.Required("Endereço é obrigatório")
            )
        ),
        FormField(
            name = "numero",
            label = "Número",
            type = FormFieldType.TEXT,
            required = true,
            validators = listOf(
                Validator.Required("Número é obrigatório")
            )
        ),
        FormField(
            name = "complemento",
            label = "Complemento",
            type = FormFieldType.TEXT,
            required = false
        ),
        FormField(
            name = "bairro",
            label = "Bairro",
            type = FormFieldType.TEXT,
            required = true,
            validators = listOf(
                Validator.Required("Bairro é obrigatório")
            )
        ),
        FormField(
            name = "cidade",
            label = "Cidade",
            type = FormFieldType.TEXT,
            required = true,
            validators = listOf(
                Validator.Required("Cidade é obrigatória")
            )
        ),
        FormField(
            name = "estado",
            label = "Estado (UF)",
            type = FormFieldType.TEXT,
            required = true,
            placeholder = "SP",
            validators = listOf(
                Validator.Required("Estado é obrigatório"),
                Validator.Length(2, "Estado deve ter 2 caracteres")
            )
        ),
        FormField(
            name = "cep",
            label = "CEP",
            type = FormFieldType.TEXT,
            required = true,
            placeholder = "00000-000",
            validators = listOf(
                Validator.Required("CEP é obrigatório"),
                Validator.Pattern(Regex("\\d{5}-?\\d{3}"), "CEP inválido")
            )
        ),
        FormField(
            name = "regiao",
            label = "Região de Atuação",
            type = FormFieldType.SELECT,
            required = true,
            options = RegiaoAtuacao.values().map { it.displayName },
            validators = listOf(
                Validator.Required("Região é obrigatória")
            )
        ),

        // ===== CONTATOS =====
        FormField(
            name = "telefone",
            label = "Telefone",
            type = FormFieldType.TEXT,
            required = true,
            placeholder = "(00) 0000-0000",
            validators = listOf(
                Validator.Required("Telefone é obrigatório"),
                Validator.MinLength(10, "Telefone deve ter pelo menos 10 dígitos")
            )
        ),
        FormField(
            name = "celular",
            label = "Celular",
            type = FormFieldType.TEXT,
            required = false,
            placeholder = "(00) 00000-0000"
        ),
        FormField(
            name = "email",
            label = "E-mail",
            type = FormFieldType.EMAIL,
            required = true,
            validators = listOf(
                Validator.Required("E-mail é obrigatório"),
                Validator.Email("E-mail inválido")
            )
        ),
        FormField(
            name = "emailFinanceiro",
            label = "E-mail Financeiro",
            type = FormFieldType.EMAIL,
            required = false
        ),
        FormField(
            name = "site",
            label = "Website",
            type = FormFieldType.TEXT,
            required = false,
            placeholder = "https://www.exemplo.com.br"
        ),

        // ===== DADOS COMERCIAIS =====
        FormField(
            name = "limiteCredito",
            label = "Limite de Crédito (R$)",
            type = FormFieldType.NUMBER,
            required = true,
            validators = listOf(
                Validator.Required("Limite de Crédito é obrigatório"),
                Validator.Min(0.0, "Limite de Crédito não pode ser negativo")
            )
        ),
        FormField(
            name = "descontoMaximo",
            label = "Desconto Máximo (%)",
            type = FormFieldType.NUMBER,
            required = true,
            validators = listOf(
                Validator.Required("Desconto Máximo é obrigatório"),
                Validator.Min(0.0, "Desconto não pode ser negativo"),
                Validator.Max(100.0, "Desconto não pode ser maior que 100%")
            )
        ),
        FormField(
            name = "prazoEntregaDias",
            label = "Prazo de Entrega (dias)",
            type = FormFieldType.NUMBER,
            required = true,
            validators = listOf(
                Validator.Required("Prazo de Entrega é obrigatório"),
                Validator.Min(0.0, "Prazo não pode ser negativo")
            )
        ),
        FormField(
            name = "freteGratis",
            label = "Oferece Frete Grátis?",
            type = FormFieldType.CHECKBOX,
            required = false
        ),
        FormField(
            name = "valorMinimoFrete",
            label = "Valor Mínimo para Frete Grátis (R$)",
            type = FormFieldType.NUMBER,
            required = false,
            validators = listOf(
                Validator.Min(0.0, "Valor não pode ser negativo")
            )
        ),

        // ===== OBSERVAÇÕES =====
        FormField(
            name = "observacoes",
            label = "Observações",
            type = FormFieldType.TEXTAREA,
            required = false
        )
    )
}
