Feature: Gerenciar sócios

  Background:
    Given que o administrador está autenticado no sistema

  Scenario: Administrador adiciona um novo sócio
    When o administrador acessa a opção "Cadastrar sócio"
    And informa dados válidos do sócio obrigatórios
    And confirma o cadastro
    Then o sistema salva o novo sócio
    And o sistema exibe a mensagem "Sócio cadastrado com sucesso."

  Scenario: Administrador edita dados de um sócio existente
    Given que existe um sócio cadastrado
    When o administrador acessa a edição do sócio
    And altera os dados do sócio com informações válidas
    And confirma a edição
    Then o sistema atualiza os dados do sócio
    And o sistema exibe a mensagem "Dados do sócio atualizados com sucesso."

  Scenario: Administrador exclui sócio sem pendências
    Given que existe um sócio cadastrado sem empréstimos vigentes e sem multas pendentes
    When o administrador solicita a exclusão do sócio
    And confirma a exclusão
    Then o sistema remove o sócio do cadastro
    And o sistema exibe a mensagem "Sócio excluído com sucesso."

  Scenario: Administrador tenta excluir sócio com pendências
    Given que existe um sócio com empréstimos vigentes ou multas pendentes
    When o administrador solicita a exclusão do sócio
    Then o sistema não permite a exclusão
    And o sistema exibe a mensagem "Não é possível excluir sócio com empréstimos ou multas pendentes."