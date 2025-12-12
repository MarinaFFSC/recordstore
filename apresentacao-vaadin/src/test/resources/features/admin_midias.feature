Feature: Gerenciar mídias

  Background:
    Given que o administrador está autenticado no sistema
    And que existe ao menos um artista cadastrado

  Scenario: Administrador adiciona uma nova mídia vinculada a artista existente
    When o administrador acessa a opção "Cadastrar mídia"
    And informa os dados da mídia com um artista existente selecionado
    And confirma o cadastro
    Then o sistema salva a nova mídia vinculada ao artista informado
    And o sistema exibe a mensagem "Mídia cadastrada com sucesso."

  Scenario: Administrador tenta adicionar mídia sem selecionar artista
    When o administrador acessa a opção "Cadastrar mídia"
    And informa os dados da mídia sem vincular um artista
    And tenta confirmar o cadastro
    Then o sistema não permite o cadastro
    And o sistema exibe a mensagem "Selecione um artista para vincular à mídia."

  Scenario: Administrador edita dados de uma mídia existente
    Given que existe uma mídia cadastrada
    When o administrador acessa a edição da mídia
    And altera os dados da mídia com informações válidas
    And confirma a edição
    Then o sistema atualiza os dados da mídia
    And o sistema exibe a mensagem "Dados da mídia atualizados com sucesso."

  Scenario: Administrador exclui mídia sem exemplares cadastrados
    Given que existe uma mídia cadastrada sem exemplares associados
    When o administrador solicita a exclusão da mídia
    And confirma a exclusão
    Then o sistema remove a mídia
    And o sistema exibe a mensagem "Mídia excluída com sucesso."