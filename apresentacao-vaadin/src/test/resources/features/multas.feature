Feature: Visualização de multas do sócio

  Background:
    Given que o sócio está autenticado no sistema

  Scenario: Sócio visualiza suas multas pendentes
    Given que o sócio possui multas pendentes
    When o sócio acessa a opção "Minhas multas"
    Then o sistema exibe a lista de multas pendentes
    And para cada multa o sistema exibe valor, motivo, data de geração e situação "pendente"

  Scenario: Sócio sem multas pendentes
    Given que o sócio não possui multas pendentes
    When o sócio acessa a opção "Minhas multas"
    Then o sistema exibe a mensagem "Você não possui multas pendentes."