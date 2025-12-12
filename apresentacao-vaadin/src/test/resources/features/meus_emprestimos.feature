Feature: Acompanhar empréstimos vigentes

  Background:
    Given que o sócio está autenticado no sistema

  Scenario: Sócio visualiza seus empréstimos vigentes
    Given que o sócio possui empréstimos vigentes
    When o sócio acessa a opção "Meus empréstimos"
    Then o sistema exibe a lista de empréstimos vigentes do sócio
    And para cada empréstimo o sistema exibe mídia, data de empréstimo e data de devolução prevista

  Scenario: Sócio não possui empréstimos vigentes
    Given que o sócio não possui empréstimos vigentes
    When o sócio acessa a opção "Meus empréstimos"
    Then o sistema exibe a mensagem "Você não possui empréstimos vigentes no momento."