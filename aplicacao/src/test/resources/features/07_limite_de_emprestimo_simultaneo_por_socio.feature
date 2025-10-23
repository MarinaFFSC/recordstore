# 7) LIMITE DE EMPRÉSTIMO SIMULTÂNEO POR SÓCIO
# language: pt
Funcionalidade: LIMITE DE EMPRÉSTIMO SIMULTÂNEO POR SÓCIO
  Como administrador
  Quero respeitar o limite máximo de empréstimos simultâneos por sócio
  Para manter o controle e equilíbrio na disponibilidade do acervo

  Cenário: Sócio abaixo do limite pode alugar
  Dado que o limite máximo de empréstimos por sócio é "3"
  E o sócio "João" possui 2 empréstimos em andamento
  E existe no catálogo um exemplar "LP Queen" com status "DISPONIVEL"
  Quando o administrador submete o pedido de locação respeitando o limite
  Então a locação deve ser autorizada por estar abaixo do limite

  Cenário: Sócio no limite tem nova locação recusada
  Dado que o limite máximo de empréstimos por sócio é "3"
  E o sócio "João" possui 3 empréstimos em andamento
  E existe no catálogo um exemplar "LP Queen" com status "DISPONIVEL"
  Quando o administrador submete o pedido de locação respeitando o limite
  Então a locação deve ser recusada por limite atingido
  E a mensagem de limite retornada deve ser "Limite de empréstimos atingido"
