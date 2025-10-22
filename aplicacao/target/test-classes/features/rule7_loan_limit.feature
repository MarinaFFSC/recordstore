# language: pt
Funcionalidade: Limite de empréstimos simultâneos por sócio

Regra: Cada sócio possui um limite máximo de 3 empréstimos ativos simultaneamente

Cenário: Criar 3º empréstimo dentro do limite
  Dado que o limite de empréstimos ativos por sócio é "3"
  E o sócio "Erika" possui "2" empréstimos ativos
  E existe um exemplar "CD - Ten Summoner's Tales" com status "disponível"
  Quando o administrador solicita o aluguel do exemplar
  Então o sistema deve permitir a locação
  E registrar o empréstimo como "ativo"

Cenário: Negar 4º empréstimo por limite atingido
  Dado que o limite de empréstimos ativos por sócio é "3"
  E o sócio "Felipe" possui "3" empréstimos ativos
  E existe um exemplar "Vinil - Pet Sounds" com status "disponível"
  Quando o administrador solicita o aluguel do exemplar
  Então o sistema deve recusar a operação
  E exibir a mensagem "Limite de empréstimos ativos atingido"
  E a locação não deve ser criada
