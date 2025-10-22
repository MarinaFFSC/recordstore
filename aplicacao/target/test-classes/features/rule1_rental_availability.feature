# language: pt
Funcionalidade: Locação de exemplar

Regra: O exemplar deve estar disponível para ser alugado

Cenário: Exemplar disponível → Empréstimo permitido
  Dado que existe um exemplar "CD - Abbey Road" com status "disponível"
  E existe um sócio ativo logado no sistema
  Quando o administrador solicita o aluguel do exemplar
  Então o sistema deve permitir a locação
  E o status do exemplar deve mudar para "emprestado"

Cenário: Exemplar não disponível → Empréstimo bloqueado
  Dado que existe um exemplar "Vinil - Thriller" com status "emprestado"
  Quando o administrador solicita o aluguel do exemplar
  Então o sistema deve exibir uma mensagem de erro "Exemplar indisponível para locação"
  E a locação não deve ser criada
