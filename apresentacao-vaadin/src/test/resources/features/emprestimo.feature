Feature: Realização de empréstimo de mídia

  Background:
    Given que existe um sócio ativo e sem multas pendentes
    And que o sócio está autenticado no sistema
    And que existe uma mídia com exemplares disponíveis

  Scenario: Sócio realiza empréstimo de uma mídia com exemplar disponível
    When o sócio seleciona uma mídia com exemplar disponível para empréstimo
    And confirma a realização do empréstimo
    Then o sistema registra o empréstimo vinculado ao sócio e ao exemplar
    And o sistema atualiza a disponibilidade do exemplar para "emprestado"
    And o sistema exibe a data de devolução prevista para o sócio

  Scenario: Sócio tenta realizar empréstimo sem exemplares disponíveis
    Given que todos os exemplares da mídia estão emprestados
    When o sócio tenta realizar o empréstimo dessa mídia
    Then o sistema não permite a realização do empréstimo
    And o sistema exibe a mensagem "Não há exemplares disponíveis para empréstimo."

  Scenario: Sócio com situação irregular tenta realizar empréstimo
    Given que o sócio possui multas pendentes ou está suspenso
    When o sócio tenta realizar um empréstimo
    Then o sistema não permite a realização do empréstimo
    And o sistema exibe a mensagem "Empréstimo não permitido. Verifique sua situação junto à locadora."