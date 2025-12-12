Feature: Pagamento e solicitação de cancelamento de multas

  Background:
    Given que o sócio está autenticado no sistema
    And que o sócio possui multas pendentes

  Scenario: Sócio realiza pagamento de multa pendente
    When o sócio seleciona uma multa com situação "pendente"
    And escolhe a opção "Pagar multa"
    And confirma a operação de pagamento
    Then o sistema registra o pagamento da multa
    And o sistema atualiza a situação da multa para "paga"

  Scenario: Sócio solicita cancelamento de multa
    When o sócio seleciona uma multa com situação "pendente"
    And escolhe a opção "Solicitar cancelamento"
    And informa uma justificativa
    Then o sistema registra a solicitação de cancelamento da multa
    And o sistema altera o status da multa para "cancelamento solicitado"

  Scenario: Sócio tenta pagar multa não pendente
    Given que a multa já está com situação "paga" ou "cancelada"
    When o sócio tenta realizar o pagamento dessa multa
    Then o sistema não permite a operação
    And o sistema exibe a mensagem "Esta multa não está disponível para pagamento."