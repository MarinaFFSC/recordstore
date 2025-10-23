# 1) LOCAÇÃO DE EXEMPLAR
# language: pt
Funcionalidade: LOCAÇÃO DE EXEMPLAR
  Como administrador
  Quero solicitar e registrar a locação de exemplares disponíveis
  Para controlar a disponibilidade e garantir que apenas itens disponíveis sejam alugados

  Cenário: Exemplar disponível permite locação
  Dado existe um exemplar "DVD Matrix" com status "DISPONIVEL"
  E existe um sócio ativo autenticado no sistema
  Quando o administrador requisita o aluguel do exemplar
  Então o sistema deve autorizar a locação
  E o empréstimo deve ser registrado como "ATIVO"

  Cenário: Exemplar já emprestado recusa nova locação
  Dado há um empréstimo ativo do exemplar "DVD Matrix"
  E existe um sócio ativo autenticado no sistema
  Quando o administrador tenta efetuar a locação desse exemplar
  Então a operação de locação deve ser recusada
  E nenhuma nova locação deve ser criada
