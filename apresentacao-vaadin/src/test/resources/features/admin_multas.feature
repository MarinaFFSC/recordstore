Feature: Gerenciar multas

  Background:
    Given que o administrador está autenticado no sistema

  Scenario: Administrador visualiza multas pendentes de todos os sócios
    Given que existem multas pendentes cadastradas
    When o administrador acessa a opção "Multas"
    Then o sistema exibe a lista de multas pendentes de todos os sócios
    And para cada multa o sistema exibe sócio, valor, motivo, data de geração e situação