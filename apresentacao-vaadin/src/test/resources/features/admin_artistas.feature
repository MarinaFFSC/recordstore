Feature: Gerenciar artistas

  Background:
    Given que o administrador está autenticado no sistema

  Scenario: Administrador adiciona um novo artista
    When o administrador acessa a opção "Cadastrar artista"
    And informa dados válidos do artista
    And confirma o cadastro
    Then o sistema salva o novo artista
    And o sistema exibe a mensagem "Artista cadastrado com sucesso."

  Scenario: Administrador edita um artista existente
    Given que existe um artista cadastrado
    When o administrador acessa a edição do artista
    And altera os dados do artista com informações válidas
    And confirma a edição
    Then o sistema atualiza os dados do artista
    And o sistema exibe a mensagem "Dados do artista atualizados com sucesso."

  Scenario: Administrador exclui artista sem mídias associadas
    Given que existe um artista sem mídias vinculadas
    When o administrador solicita a exclusão do artista
    And confirma a exclusão
    Then o sistema remove o artista
    And o sistema exibe a mensagem "Artista excluído com sucesso."

  Scenario: Administrador tenta excluir artista com mídias vinculadas
    Given que existe um artista com mídias cadastradas vinculadas a ele
    When o administrador solicita a exclusão do artista
    Then o sistema não permite a exclusão
    And o sistema exibe a mensagem "Não é possível excluir artista com mídias vinculadas."