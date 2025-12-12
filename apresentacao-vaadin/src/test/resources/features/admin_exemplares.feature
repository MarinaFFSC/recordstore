Feature: Gerenciar exemplares de mídias

  Background:
    Given que o administrador está autenticado no sistema
    And que existe ao menos uma mídia cadastrada

  Scenario: Administrador cadastra exemplar para uma mídia
    When o administrador acessa a opção "Cadastrar exemplar"
    And seleciona uma mídia existente
    And informa os dados do exemplar
    And confirma o cadastro
    Then o sistema salva o novo exemplar vinculado à mídia
    And o sistema exibe a mensagem "Exemplar cadastrado com sucesso."

  Scenario: Administrador visualiza exemplares existentes de uma mídia
    Given que existem exemplares cadastrados para uma mídia
    When o administrador acessa a tela de exemplares da mídia
    Then o sistema exibe a lista de exemplares com seus respectivos estados (disponível, emprestado, inativo)

  Scenario: Administrador edita exemplar existente
    Given que existe um exemplar cadastrado
    When o administrador acessa a edição do exemplar
    And altera os dados do exemplar com informações válidas
    And confirma a edição
    Then o sistema atualiza os dados do exemplar
    And o sistema exibe a mensagem "Exemplar atualizado com sucesso."

  Scenario: Administrador exclui exemplar não emprestado
    Given que existe um exemplar com estado "disponível" ou "inativo"
    When o administrador solicita a exclusão desse exemplar
    And confirma a exclusão
    Then o sistema remove o exemplar
    And o sistema exibe a mensagem "Exemplar excluído com sucesso."

  Scenario: Administrador tenta excluir exemplar emprestado
    Given que existe um exemplar com estado "emprestado"
    When o administrador solicita a exclusão desse exemplar
    Then o sistema não permite a exclusão
    And o sistema exibe a mensagem "Não é possível excluir exemplar com empréstimo vigente."