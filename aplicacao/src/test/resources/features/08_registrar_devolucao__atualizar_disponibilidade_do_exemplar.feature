# language: pt
Funcionalidade: REGISTRAR DEVOLUÇÃO / ATUALIZAR DISPONIBILIDADE DO EXEMPLAR
  Como administrador
  Quero registrar a devolução e atualizar a disponibilidade do exemplar
  Para manter o status dos exemplares atualizado e o histórico completo das devoluções

  Cenário: Tentativa de devolução sem condição é recusada
  Dado existe um empréstimo ativo vinculado a esse exemplar
  Quando o administrador tentar registrar a devolução sem informar a condição física
  Então o sistema deve recusar o registro
  E exibir a mensagem "Condição física obrigatória" 

  Cenário: Devolução com condição informada é registrada
  Dado existe um empréstimo ativo vinculado a esse exemplar
  Quando o administrador registrar a devolução informando a condição "BOM_ESTADO"
  Então o administrador registra a devolução no sistema
  E registrar o evento "DEVOLUCAO_REGISTRADA" 
