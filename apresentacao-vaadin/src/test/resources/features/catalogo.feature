Feature: Consulta ao catálogo de mídias

  Background:
    Given que existe um sócio cadastrado
    And que o sócio está autenticado no sistema
    And que existem mídias cadastradas com exemplares disponíveis

  Scenario: Sócio visualiza o catálogo de mídias com sucesso
    When o sócio acessa a opção "Catálogo de mídias"
    Then o sistema exibe a lista de mídias cadastradas
    And cada mídia apresenta título, artista e tipo
    And o sistema indica se há exemplares disponíveis para empréstimo

  Scenario: Catálogo de mídias vazio
    Given que não existem mídias cadastradas
    When o sócio acessa a opção "Catálogo de mídias"
    Then o sistema exibe a mensagem "Não há mídias cadastradas no momento."