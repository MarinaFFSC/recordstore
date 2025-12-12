Feature: Devolução de mídias emprestadas

  Background:
    Given que existe um sócio com empréstimos vigentes
    And que o sócio está autenticado no sistema

  Scenario: Sócio realiza devolução dentro do prazo
    Given que o sócio possui um empréstimo com data de devolução ainda não vencida
    When o sócio seleciona o empréstimo e confirma a devolução
    Then o sistema registra a devolução do exemplar
    And o sistema atualiza a situação do empréstimo para "devolvido"
    And o sistema não gera multa para o sócio

  Scenario: Sócio realiza devolução em atraso
    Given que o sócio possui um empréstimo com data de devolução vencida
    When o sócio seleciona o empréstimo e confirma a devolução
    Then o sistema registra a devolução do exemplar
    And o sistema atualiza a situação do empréstimo para "devolvido"
    And o sistema gera uma multa associada ao sócio e ao empréstimo

  Scenario: Sócio tenta devolver mídia que não está emprestada
    When o sócio tenta registrar a devolução de uma mídia que não consta em seus empréstimos vigentes
    Then o sistema não registra a devolução
    And o sistema exibe a mensagem "Não há empréstimo vigente para esta mídia."