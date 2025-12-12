Feature: Gerenciar empréstimos

  Background:
    Given que o administrador está autenticado no sistema

  Scenario: Administrador lista todos os empréstimos do sistema
    Given que existem empréstimos cadastrados
    When o administrador acessa a opção "Empréstimos"
    Then o sistema exibe a lista de empréstimos
    And para cada empréstimo o sistema exibe sócio, mídia, exemplar, data de empréstimo, data de devolução prevista e situação

  Scenario: Administrador edita dados de um empréstimo
    Given que existe um empréstimo cadastrado
    When o administrador seleciona o empréstimo para edição
    And altera dados permitidos do empréstimo (por exemplo, data de devolução prevista ou situação)
    And confirma a edição
    Then o sistema atualiza os dados do empréstimo
    And o sistema exibe a mensagem "Empréstimo atualizado com sucesso."