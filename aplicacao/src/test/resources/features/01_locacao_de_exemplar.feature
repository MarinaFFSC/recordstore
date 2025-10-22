# language: pt
Funcionalidade: LOCAÇÃO DE EXEMPLAR
  Como administrador
  Quero solicitar e registrar a locação de exemplares disponíveis
  Para controlar a disponibilidade e garantir que apenas itens disponíveis sejam alugados

  Cenário: Exemplar disponível permite locação
  Dado existe um exemplar "DVD Matrix" com status "DISPONIVEL"
  E existe um sócio ativo logado no sistema
  Quando o administrador solicita o aluguel do exemplar
  Então o sistema deve permitir a locação
  E registrar o empréstimo como "ATIVO" 

  Cenário: Exemplar já emprestado recusa nova locação
  Dado há um empréstimo ativo do exemplar "DVD Matrix"
  E existe um sócio ativo logado no sistema
  Quando o administrador solicita a locação
  Então o sistema deve recusar a locação
  E a locação não deve ser criada
