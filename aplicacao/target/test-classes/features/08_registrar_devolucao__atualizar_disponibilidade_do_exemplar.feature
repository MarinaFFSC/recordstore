# 8) REGISTRAR DEVOLUÇÃO / ATUALIZAR DISPONIBILIDADE DO EXEMPLAR
# language: pt
Funcionalidade: REGISTRAR DEVOLUÇÃO / ATUALIZAR DISPONIBILIDADE DO EXEMPLAR
  Como administrador
  Quero registrar a devolução e atualizar a disponibilidade do exemplar
  Para manter o status dos exemplares atualizado e o histórico completo das devoluções

  Cenário: Tentativa de devolução sem condição é recusada
  Dado existe um empréstimo em andamento para este exemplar
  Quando o administrador tenta finalizar a devolução sem informar a condição física
  Então o sistema deve impedir o processamento da devolução
  E a mensagem de validação exibida deve ser "Condição física obrigatória"

  Cenário: Devolução com condição informada é registrada
  Dado existe um empréstimo em andamento para este exemplar
  Quando o administrador finaliza a devolução informando a condição "BOM_ESTADO"
  Então o sistema deve aceitar a devolução
  E o status do exemplar após a devolução deve ser "DISPONIVEL"
  E o evento registrado deve ser "DEVOLUCAO_REGISTRADA"
