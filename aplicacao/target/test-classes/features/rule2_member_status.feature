# language: pt
Funcionalidade: Atualização automática de status do exemplar

Regra: O status do exemplar deve mudar automaticamente conforme empréstimo e devolução

Cenário: Status muda para "emprestado" após confirmar empréstimo
  Dado que existe um exemplar "CD - Back in Black" com status "disponível"
  Quando o administrador realiza a locação do exemplar
  Então o status do exemplar deve ser atualizado para "emprestado"

Cenário: Status muda para "disponível" após devolução
  Dado que existe um exemplar "Vinil - Rumours" com status "emprestado"
  E existe um empréstimo ativo vinculado a esse exemplar
  Quando o administrador registra a devolução no sistema
  Então o status do exemplar deve ser atualizado para "disponível"
